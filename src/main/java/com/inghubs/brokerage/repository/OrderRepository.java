package com.inghubs.brokerage.repository;

import com.inghubs.brokerage.entity.Order;
import com.inghubs.brokerage.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByIdInAndStatus(List<Long> orderIds, OrderStatus status);

  List<Order> findByCustomerIdAndCreateDateBetween(
      Long customerId, LocalDateTime createDateStart, LocalDateTime createDateEnd);

  List<Order> findByCustomerId(Long customerId);

  List<Order> findAllByStatus(OrderStatus status);
}
