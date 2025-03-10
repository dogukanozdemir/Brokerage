package com.inghubs.brokerage.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.brokerage.dto.OrderDto;
import com.inghubs.brokerage.dto.request.CreateOrderRequest;
import com.inghubs.brokerage.enums.OrderSide;
import com.inghubs.brokerage.enums.OrderStatus;
import com.inghubs.brokerage.service.OrderService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private OrderService orderService;

  @Test
  @WithMockUser(username = "test")
  void testCreateOrder() throws Exception {
    CreateOrderRequest request = new CreateOrderRequest(1L, "USD", OrderSide.BUY, 10.0, 100.0);
    OrderDto response =
        new OrderDto(
            1L, 1L, "USD", OrderSide.BUY, 10.0, 100.0, OrderStatus.PENDING, LocalDateTime.now());

    Mockito.when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.customerId").value(1L))
        .andExpect(jsonPath("$.assetName").value("USD"))
        .andExpect(jsonPath("$.orderSide").value("BUY"));
  }

  @Test
  @WithMockUser(username = "test")
  void testGetOrdersWithoutDate() throws Exception {
    List<OrderDto> orders =
        List.of(
            new OrderDto(
                1L,
                1L,
                "USD",
                OrderSide.BUY,
                10.0,
                100.0,
                OrderStatus.PENDING,
                LocalDateTime.now()));

    Mockito.when(orderService.getAllOrders(eq(1L), any(), any())).thenReturn(orders);

    mockMvc
        .perform(get("/v1/orders").param("customerId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].customerId").value(1L));
  }

  @Test
  @WithMockUser(username = "test")
  void testGetOrdersWithDate() throws Exception {
    List<OrderDto> orders =
        List.of(
            new OrderDto(
                1L,
                1L,
                "USD",
                OrderSide.BUY,
                10.0,
                100.0,
                OrderStatus.PENDING,
                LocalDateTime.now()));

    Mockito.when(orderService.getAllOrders(eq(1L), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(orders);

    mockMvc
        .perform(
            get("/v1/orders")
                .param("customerId", "1")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].customerId").value(1L));
  }

  @Test
  @WithMockUser(username = "test")
  void testCancelOrder() throws Exception {
    OrderDto response =
        new OrderDto(
            1L, 1L, "USD", OrderSide.BUY, 10.0, 100.0, OrderStatus.CANCELED, LocalDateTime.now());

    Mockito.when(orderService.cancelOrder(1L)).thenReturn(response);

    mockMvc
        .perform(delete("/v1/orders/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.status").value("CANCELED"));
  }
}
