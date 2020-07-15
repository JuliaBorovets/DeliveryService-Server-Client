package ua.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.domain.user.BankCard;
import ua.training.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface BankCardRepository extends JpaRepository<BankCard, Long> {

    List<BankCard> findBankCardByUsers(User user);

    Optional<BankCard> findBankCardByIdAndExpMonthAndExpYearAndCcv(Long id, Long expMonth, Long expYear, Long cvv);

}
