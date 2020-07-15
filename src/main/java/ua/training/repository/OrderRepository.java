package ua.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.domain.order.Order;
import ua.training.domain.order.Status;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOrderByOwnerId(Long ownerId);

    List<Order> findOrderByOwner_Login(String login);

    List<Order> findByStatusAndOwner_Login(Status status, String login);

    List<Order> findOrderByStatus(Status status);

    Optional<Order> findByIdAndOwner_Login(Long orderId, String login);

}
