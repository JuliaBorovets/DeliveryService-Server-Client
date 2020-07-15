package ua.training.api.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ua.training.configuration.RegexConstants;
import ua.training.configuration.ValidPassword;
import ua.training.domain.user.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserDto {

    private Long id;

    @NotBlank
    private String login;

    @ValidPassword
    private String password;

    @Pattern(regexp = RegexConstants.firstNameRegexp)
    private String firstName;

    @Length(min = 3, max = 30)
    private String lastName;

    @Email
    private String email;

    private Role role = Role.ROLE_USER;
}

