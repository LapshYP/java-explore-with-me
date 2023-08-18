package ru.practicum.ewmservice.event.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewmservice.category.model.Category;
import ru.practicum.ewmservice.compilation.model.Compilation;
import ru.practicum.ewmservice.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "events")
@ToString
public class Event {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation", nullable = false)
    @NotBlank
    @NotEmpty
    @NotNull
    @Length(min = 20, max = 2000)
    private String annotation;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "confirmed_requests")
    private long confirmedRequests;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "description", nullable = false)
    @NotBlank
    @NotEmpty
    @NotNull
    @Length(min = 20, max = 7000)
    private String description;
    @Column(name = "event_date")
    private LocalDateTime date;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @AttributeOverrides(value = {
            @AttributeOverride(name = "lat", column = @Column(name = "lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "lon"))
    })
    private Location location;
    @Column(name = "paid", nullable = false)
    private Boolean paid;
    @Column(name = "participant_limit")
    private Long participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;
    @Column(name = "title", nullable = false)
    @Length(min = 3, max = 120)
    private String title;
    @Column(name = "views")
    private Long views;
    @Column
    Long request;

    @ManyToMany(mappedBy = "events")
    private Set<Compilation> compilations = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(getDate(), event.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate());
    }
}