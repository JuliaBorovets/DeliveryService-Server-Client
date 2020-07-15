package ua.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.domain.order.OrderType;

public interface OrderTypeRepository extends JpaRepository<OrderType, Long> {

}
