package ua.training.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ua.training.domain.user.BankCard;
import ua.training.domain.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "receipt")
    private Order order;

    private BigDecimal priceInCents;

    @ManyToOne
    private User user;

    @JsonIgnore
    @ManyToOne
    private BankCard bankCard;

    private LocalDate creationDate ;

}
