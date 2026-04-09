package com.arceno.orderapi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arceno.orderapi.dto.OrderCreateItemDTO;
import com.arceno.orderapi.dto.OrderCreateRequestDTO;
import com.arceno.orderapi.dto.OrderItemResponseDTO;
import com.arceno.orderapi.dto.OrderResponseDTO;
import com.arceno.orderapi.entity.Order;
import com.arceno.orderapi.entity.OrderItem;
import com.arceno.orderapi.entity.Product;
import com.arceno.orderapi.entity.User;
import com.arceno.orderapi.enumeration.OrderStatus;
import com.arceno.orderapi.exception.InsufficientStockException;
import com.arceno.orderapi.exception.InvalidOrderRequestException;
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

    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
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