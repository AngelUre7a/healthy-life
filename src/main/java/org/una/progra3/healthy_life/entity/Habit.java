package org.una.progra3.healthy_life.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;

import java.util.Set;

@Entity
@Table(name = "habits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"favoredBy"})
@ToString(exclude = {"favoredBy"})
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private HabitCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "favoriteHabits")
    private Set<User> favoredBy;
}
