package ua.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.training.domain.order.Receipt;

import java.math.BigDecimal;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findAllByUser_Login(String login);

    @Query("select count (e) from Receipt e where year(e.creationDate) = :year ")
    Long ordersByCreationYear( @Param("year") int year);

    @Query("select count (e) from Receipt e where month(e.creationDate) = :month and year(e.creationDate) = :year ")
    Long ordersByCreationMonthsAndYear(@Param("month") int month, @Param("year") int year);

    @Query("select sum (e.priceInCents) from Receipt e where year(e.creationDate) = :year ")
    BigDecimal earningsByCreationYear(@Param("year") int year);

    @Query("select sum (e.priceInCents) from Receipt e where month(e.creationDate) = :month and year(e.creationDate) = :year ")
    BigDecimal earningsByCreationMonthsAndYear(@Param("month") int month, @Param("year") int year);

}
