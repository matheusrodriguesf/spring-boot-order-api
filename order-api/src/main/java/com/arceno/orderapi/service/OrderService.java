package com.arceno.orderapi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arceno.orderapi.dto.OrderCreateItemDTO;
import com.arceno.orderapi.dto.OrderCreateRequestDTO;
import com.arceno.orderapi.dto.OrderItemResponseDTO;
import com.arceno.orderapi.dto.OrderResponseDTO;
import com.arceno.orderapi.dto.OrderUpdateRequestDTO;
import com.arceno.orderapi.entity.Order;
import com.arceno.orderapi.entity.OrderItem;
import com.arceno.orderapi.entity.Product;
import com.arceno.orderapi.entity.User;
import com.arceno.orderapi.enumeration.OrderStatus;
import com.arceno.orderapi.exception.InsufficientStockException;
import com.arceno.orderapi.exception.InvalidOrderRequestException;
import com.arceno.orderapi.exception.InvalidOrderStatusTransitionException;
import com.arceno.orderapi.exception.OrderNotFoundException;
import com.arceno.orderapi.exception.OrderUpdateNotAllowedException;
import com.arceno.orderapi.exception.ProductNotFoundException;
import com.arceno.orderapi.exception.UserNotFoundException;
import com.arceno.orderapi.repository.OrderRepository;
import com.arceno.orderapi.repository.ProductRepository;
import com.arceno.orderapi.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {

    OrderRepository orderRepository;
    UserRepository userRepository;
    ProductRepository productRepository;

    public OrderResponseDTO getOrderById(Long id) {
        log.info("Buscando pedido com id: {}", id);
        Order order = findOrderByIdOrThrow(id);
        return toOrderResponseDTO(order);
    }

    public Page<OrderResponseDTO> allOrders(Pageable pageable, OrderStatus status) {
        log.info("Buscando pedidos com paginação: {} e status: {}", pageable, status);
        Page<Order> orders = status == null
                ? orderRepository.findAll(pageable)
                : orderRepository.findByStatus(status, pageable);

        return orders.map(this::toOrderResponseDTO);
    }

    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderUpdateRequestDTO orderUpdateRequestDTO) {
        log.info("Atualizando itens do pedido {}", id);
        Order order = findOrderByIdOrThrow(id);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new OrderUpdateNotAllowedException(order.getId(), order.getStatus());
        }

        validateDuplicatedProducts(orderUpdateRequestDTO.items());
        restoreStock(order.getItems());

        Set<Long> productIds = orderUpdateRequestDTO.items().stream()
                .map(OrderCreateItemDTO::productId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<Long, Product> productsById = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<OrderItem> newItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderCreateItemDTO item : orderUpdateRequestDTO.items()) {
            Product product = productsById.get(item.productId());
            if (product == null) {
                throw new ProductNotFoundException(item.productId());
            }

            if (product.getStock() < item.quantity()) {
                throw new InsufficientStockException(product.getId(), product.getStock(), item.quantity());
            }

            BigDecimal unitPrice = normalizeMoney(product.getPrice());
            BigDecimal itemTotal = normalizeMoney(unitPrice.multiply(BigDecimal.valueOf(item.quantity())));

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(item.quantity())
                    .unitPrice(unitPrice)
                    .total(itemTotal)
                    .build();

            newItems.add(orderItem);
            totalAmount = totalAmount.add(itemTotal);
            product.setStock(product.getStock() - item.quantity());
        }

        order.setItems(newItems);
        order.setTotal(normalizeMoney(totalAmount));

        Order updatedOrder = orderRepository.save(order);
        return toOrderResponseDTO(updatedOrder);
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus newStatus) {
        log.info("Atualizando status do pedido {} para {}", id, newStatus);
        Order order = findOrderByIdOrThrow(id);
        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == newStatus) {
            return toOrderResponseDTO(order);
        }

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new InvalidOrderStatusTransitionException(order.getId(), currentStatus, newStatus);
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return toOrderResponseDTO(updatedOrder);
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderCreateRequestDTO orderCreateRequestDTO) {
        log.info("Criando pedido para usuário: {}", orderCreateRequestDTO.userId());

        validateDuplicatedProducts(orderCreateRequestDTO.items());

        User user = findUserByIdOrThrow(orderCreateRequestDTO.userId());

        Set<Long> productIds = orderCreateRequestDTO.items().stream()
                .map(OrderCreateItemDTO::productId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<Long, Product> productsById = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderCreateItemDTO item : orderCreateRequestDTO.items()) {
            Product product = productsById.get(item.productId());
            if (product == null) {
                throw new ProductNotFoundException(item.productId());
            }

            if (product.getStock() < item.quantity()) {
                throw new InsufficientStockException(product.getId(), product.getStock(), item.quantity());
            }

            BigDecimal unitPrice = normalizeMoney(product.getPrice());
            BigDecimal itemTotal = normalizeMoney(unitPrice.multiply(BigDecimal.valueOf(item.quantity())));

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(item.quantity())
                    .unitPrice(unitPrice)
                    .total(itemTotal)
                    .build();

            order.addItem(orderItem);
            totalAmount = totalAmount.add(itemTotal);

            product.setStock(product.getStock() - item.quantity());
        }

        order.setTotal(normalizeMoney(totalAmount));
        Order savedOrder = orderRepository.save(order);
        return toOrderResponseDTO(savedOrder);
    }

    private void validateDuplicatedProducts(List<OrderCreateItemDTO> items) {
        Set<Long> uniqueProducts = new LinkedHashSet<>();
        for (OrderCreateItemDTO item : items) {
            if (!uniqueProducts.add(item.productId())) {
                throw new InvalidOrderRequestException("Pedido possui produto duplicado: " + item.productId());
            }
        }
    }

    private void restoreStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        }
    }

    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private Order findOrderByIdOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case CREATED -> to == OrderStatus.PROCESSING;
            case PROCESSING -> to == OrderStatus.DONE;
            case DONE -> false;
        };
    }

    private OrderResponseDTO toOrderResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotal()))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getUser().getId(),
                order.getStatus().name(),
                order.getTotal(),
                order.getCreatedAt(),
                itemResponses);
    }

}