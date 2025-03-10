package com.inghubs.brokerage.controller;

import com.inghubs.brokerage.dto.OrderDto;
import com.inghubs.brokerage.dto.request.CreateOrderRequest;
import com.inghubs.brokerage.service.OrderService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid CreateOrderRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
  }

  @GetMapping
  public ResponseEntity<List<OrderDto>> getOrders(
      @RequestParam Long customerId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {
    return ResponseEntity.ok(orderService.getAllOrders(customerId, startDate, endDate));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.cancelOrder(id));
  }
}
