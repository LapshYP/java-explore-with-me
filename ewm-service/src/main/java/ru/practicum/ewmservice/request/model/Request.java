package ru.practicum.ewmservice.request.model;

import jdk.jshell.Snippet;
import lombok.*;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    Long id;
    @NotNull
    @Column
    String description;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @Column
    LocalDateTime created;
    @Column
    @Enumerated(EnumType.STRING)
    Status status;
//    @OneToMany
//            (mappedBy = "request", orphanRemoval = true,
//                    cascade = CascadeType.ALL)
//    @Column(nullable = true)
//    //@JoinColumn(name = "request_id")
//    @JsonIgnore
//    // @JsonManagedReference
//    List<Event> events;


}
