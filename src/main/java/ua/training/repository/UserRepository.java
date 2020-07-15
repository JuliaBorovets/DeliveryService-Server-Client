package ua.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    Optional<User> findUserById(long id);

    List<User> findAllByLoginLike(String login);

}
