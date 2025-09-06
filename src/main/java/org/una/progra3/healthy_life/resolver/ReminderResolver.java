package org.una.progra3.healthy_life.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.entity.enums.ReminderFrequency;
import org.una.progra3.healthy_life.service.ReminderService;
import org.una.progra3.healthy_life.service.UserService;
import org.una.progra3.healthy_life.service.HabitService;

import java.time.LocalTime;
import java.util.List;

@Controller
public class ReminderResolver {

    @Autowired private ReminderService reminderService;
    @Autowired private UserService userService;
    @Autowired private HabitService habitService;

    @QueryMapping
    public List<Reminder> remindersByUser(@Argument Long userId) {
        User user = userService.findById(userId);
        return user == null ? List.of() : reminderService.findByUser(user);
    }

    @MutationMapping
    public Reminder createReminder(@Argument Long userId, @Argument Long habitId, @Argument String time,
                                   @Argument ReminderFrequency frequency) {
        User user = userService.findById(userId);
        Habit habit = habitService.findById(habitId);
        return reminderService.create(user, habit, LocalTime.parse(time), frequency);
    }

    @MutationMapping
    public Reminder updateReminder(@Argument Long id, @Argument String time,
                                   @Argument ReminderFrequency frequency) {
        return reminderService.update(id, time != null ? LocalTime.parse(time) : null, frequency);
    }

    @MutationMapping
    public Boolean deleteReminder(@Argument Long id) { return reminderService.deleteById(id); }
}
