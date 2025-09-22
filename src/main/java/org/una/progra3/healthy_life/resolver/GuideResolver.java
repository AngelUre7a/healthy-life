package org.una.progra3.healthy_life.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.una.progra3.healthy_life.entity.Guide;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.service.GuideService;
import org.una.progra3.healthy_life.service.HabitService;

import org.una.progra3.healthy_life.service.AuthenticationService;
import org.una.progra3.healthy_life.security.PermissionValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

@Controller
public class GuideResolver {

    @Autowired private GuideService guideService;
    @Autowired private HabitService habitService;
    @Autowired private AuthenticationService authenticationService;

    @QueryMapping
    public List<Guide> guidesByCategory(@Argument HabitCategory category) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        return category == null ? guideService.findAll() : guideService.findByCategory(category);
    }

    @QueryMapping
    public List<Guide> guidesByCategorySimple(@Argument HabitCategory category) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        return category == null ? guideService.findAll() : guideService.findByCategory(category);
    }

    @QueryMapping
    // ...existing code...
    public Guide guideById(@Argument Long id) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        return guideService.findById(id);
    }

    @MutationMapping
    public Guide createGuide(@Argument String title, @Argument String content, @Argument HabitCategory category,
                             @Argument List<String> recommendedHabitIds) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkWrite(currentUser);
        Guide g = new Guide();
        g.setTitle(title);
        g.setContent(content);
        g.setCategory(category);
        g.setRecommendedFor(resolveHabits(recommendedHabitIds));
        return guideService.create(g);
    }

    @MutationMapping
    public Guide updateGuide(@Argument Long id, @Argument String title, @Argument String content,
                             @Argument HabitCategory category, @Argument List<String> recommendedHabitIds) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkWrite(currentUser);
        Guide existing = guideService.findById(id);
        if (existing == null) throw new RuntimeException("Guide not found");
        if (title != null) existing.setTitle(title);
        if (content != null) existing.setContent(content);
        if (category != null) existing.setCategory(category);
        if (recommendedHabitIds != null) existing.setRecommendedFor(resolveHabits(recommendedHabitIds));
        // Reuse create (save) to persist changes (ID present -> merge)
        return guideService.create(existing);
    }

    @MutationMapping
    // ...existing code...
    public Boolean deleteGuide(@Argument Long id) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkDelete(currentUser);
        return guideService.deleteById(id);
    }
    private Set<Habit> resolveHabits(List<String> rawList) {
        Set<Habit> habits = new HashSet<>();
        if (rawList == null) return habits;
        List<String> flat = new ArrayList<>();
        for (String entry : rawList) {
            if (entry == null) continue;
            String cleaned = entry.trim();
            if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
                cleaned = cleaned.substring(1, cleaned.length() - 1);
            }
            if (cleaned.contains(",")) {
                for (String token : cleaned.split(",")) {
                    if (!token.isBlank()) flat.add(token.trim());
                }
            } else if (!cleaned.isBlank()) {
                flat.add(cleaned);
            }
        }
        for (String token : flat) {
            try {
                Long id = Long.parseLong(token);
                Habit h = habitService.findById(id);
                if (h != null) habits.add(h);
            } catch (NumberFormatException ignored) {
            }
        }
        return habits;
    }
}
