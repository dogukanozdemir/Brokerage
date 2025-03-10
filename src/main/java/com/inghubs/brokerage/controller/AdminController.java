package com.inghubs.brokerage.controller;

import com.inghubs.brokerage.dto.OrderDto;
import com.inghubs.brokerage.dto.request.MatchOrdersRequest;
import com.inghubs.brokerage.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController {

  private final OrderService orderService;

  @PostMapping("/match-orders")
  public ResponseEntity<List<OrderDto>> matchOrders(
      @RequestBody @Valid MatchOrdersRequest requestDto) {
    return ResponseEntity.ok(orderService.matchOrders(requestDto));
  }
}
