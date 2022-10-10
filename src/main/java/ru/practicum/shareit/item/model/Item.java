package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", nullable = false, length = 512)
    private String description;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

/*    @OneToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
*/
}
