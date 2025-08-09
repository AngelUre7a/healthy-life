package org.una.progra3.healthy_life.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;

import java.util.Set;

@Entity
@Table(name = "habits")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
