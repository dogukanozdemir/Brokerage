package com.inghubs.brokerage.entity;

import com.inghubs.brokerage.enums.OrderSide;
import com.inghubs.brokerage.enums.OrderStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long customerId;
  private String assetName;

  @Enumerated(EnumType.STRING)
  private OrderSide orderSide;

  private Double size;
  private Double price;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  private LocalDateTime createDate;
}
