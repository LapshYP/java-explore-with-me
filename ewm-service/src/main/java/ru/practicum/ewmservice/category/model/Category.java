package ru.practicum.ewmservice.category.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "categories", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "must not be empty")
    @NotBlank(message = "must not be blank")
    @Column
    private String name;


//    @OneToMany(mappedBy = "owner")
//    private List<Item> items;
//    @OneToMany(mappedBy = "booker")
//    private List<Booking> bookings;

}
