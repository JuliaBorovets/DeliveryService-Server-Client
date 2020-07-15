package ua.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.domain.order.Destination;

import java.util.Optional;

public interface DestinationRepository extends JpaRepository<Destination, Long> {

    Optional<Destination> findByCityFromAndCityTo(String cityFrom, String cityTo);

}
