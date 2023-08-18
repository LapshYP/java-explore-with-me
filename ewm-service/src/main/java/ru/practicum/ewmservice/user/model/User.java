package ru.practicum.ewmservice.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "must not be empty")
    @NotBlank(message = "must not be blank")
    @Column
    private String name;
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotEmpty(message = "must not be empty")
    @Column
    private String email;


}
