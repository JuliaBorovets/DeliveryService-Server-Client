package ua.training.domain.user;


import lombok.*;
import ua.training.domain.order.Receipt;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "card",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class BankCard {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private Long expMonth;

    private Long expYear;

    private Long ccv;

    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToMany(mappedBy = "cards", cascade = CascadeType.REFRESH)
    private List<User> users = new ArrayList<>();


    @OneToMany(mappedBy = "bankCard")
    private List<Receipt> receipts = new ArrayList<>();

    @PreRemove
    public void deleteBankCard(){
        users.forEach(b -> b.getCards().remove(this));
        receipts.forEach(b -> b.setBankCard(null));
    }

    public void deleteUser(User user){
        users.remove(user);
        user.getCards().remove(this);
    }

}
