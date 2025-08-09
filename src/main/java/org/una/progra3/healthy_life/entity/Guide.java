package org.una.progra3.healthy_life.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;

import java.util.Set;

@Entity
@Table(name = "guides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private HabitCategory category;

    @ManyToMany
    @JoinTable(
            name = "guide_recommended_habits",
            joinColumns = @JoinColumn(name = "guide_id"),
            inverseJoinColumns = @JoinColumn(name = "habit_id")
    )
    private Set<Habit> recommendedFor;
}
