package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.entity.enums.ReminderFrequency;
import org.una.progra3.healthy_life.entity.enums.RoleType;
import org.una.progra3.healthy_life.repository.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Servicio para generar datos masivos de prueba
 * IMPORTANTE: Solo usar en ambiente de desarrollo/testing
 */
@Service
public class DataGenerationService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HabitRepository habitRepository;
    
    @Autowired
    private RoutineRepository routineRepository;
    
    @Autowired
    private GuideRepository guideRepository;
    
    @Autowired
    private ReminderRepository reminderRepository;
    
    @Autowired
    private CompletedActivityRepository completedActivityRepository;
    
    @Autowired
    private ProgressLogRepository progressLogRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Random random = new Random();
    private final String[] FIRST_NAMES = {"Ana", "Luis", "María", "Carlos", "Laura", "Diego", "Sofia", "Miguel", "Carmen", "Pedro", "Isabella", "Juan", "Valentina", "Roberto", "Camila", "Fernando", "Natalia", "Jorge", "Gabriela", "Andrés"};
    private final String[] LAST_NAMES = {"García", "Rodríguez", "González", "Fernández", "López", "Martínez", "Sánchez", "Pérez", "Gómez", "Martín", "Jiménez", "Ruiz", "Hernández", "Díaz", "Moreno", "Muñoz", "Álvarez", "Romero", "Alonso", "Gutiérrez"};
    private final String[] HABIT_NAMES = {"Ejercicio matutino", "Meditación", "Lectura", "Hidratación", "Caminar", "Yoga", "Estudiar", "Cocinar saludable", "Dormir temprano", "Ejercicios de estiramiento", "Escribir diario", "Hacer ejercicio", "Tomar vitaminas", "Planificar el día", "Ejercicios de respiración"};
    private final String[] ROUTINE_TITLES = {"Rutina matutina", "Rutina nocturna", "Rutina de ejercicios", "Rutina de estudio", "Rutina de relajación", "Rutina productiva", "Rutina saludable", "Rutina energizante", "Rutina de bienestar", "Rutina diaria"};
    private final String[] GUIDE_TITLES = {"Guía de ejercicios", "Guía de alimentación", "Guía de sueño", "Guía de productividad", "Guía de mindfulness", "Guía de hábitos", "Guía de bienestar", "Guía de motivación", "Guía práctica", "Guía completa"};

    @Transactional
    public void generateMassiveDataset() {
        System.out.println("Iniciando generación de dataset masivo...");
        
        // Crear roles básicos si no existen
        createBasicRoles();
        
        // Generar usuarios (100,000)
        generateUsers(100000);
        
        // Generar hábitos (10,000)
        generateHabits(10000);
        
        // Generar rutinas (150,000)
        generateRoutines(150000);
        
        // Generar guías (5,000)
        generateGuides(5000);
        
        // Generar recordatorios (100,000)
        generateReminders(100000);
        
        // Generar actividades completadas (200,000)
        generateCompletedActivities(200000);
        
        System.out.println("Dataset masivo generado exitosamente!");
    }

    @Transactional
    public void createBasicRoles() {
        if (!roleRepository.existsByName(RoleType.USER)) {
            Role userRole = new Role();
            userRole.setName(RoleType.USER);
            userRole.setDescription("Usuario estándar");
            userRole.setCanRead(true);
            userRole.setCanWrite(true);
            userRole.setCanDelete(false);
            roleRepository.save(userRole);
        }
        
        if (!roleRepository.existsByName(RoleType.ADMIN)) {
            Role adminRole = new Role();
            adminRole.setName(RoleType.ADMIN);
            adminRole.setDescription("Administrador");
            adminRole.setCanRead(true);
            adminRole.setCanWrite(true);
            adminRole.setCanDelete(true);
            roleRepository.save(adminRole);
        }
    }

    @Transactional
    public void generateUsers(int count) {
        System.out.println("Generando " + count + " usuarios...");
        List<User> users = new ArrayList<>();
        List<Role> roles = roleRepository.findAll();
        
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setName(FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + " " + 
                        LAST_NAMES[random.nextInt(LAST_NAMES.length)]);
            user.setEmail("user" + i + "@healthylife.com");
            user.setPassword(passwordEncoder.encode("password123"));
            
            if (!roles.isEmpty()) {
                user.setRole(roles.get(random.nextInt(roles.size())));
            }
            
            users.add(user);
            
            // Guardar en lotes para mejor rendimiento
            if (users.size() == 1000) {
                userRepository.saveAll(users);
                users.clear();
                System.out.println("Guardados " + (i + 1) + " usuarios...");
            }
        }
        
        if (!users.isEmpty()) {
            userRepository.saveAll(users);
        }
        
        System.out.println("Usuarios generados completamente.");
    }

    @Transactional
    public void generateHabits(int count) {
        System.out.println("Generando " + count + " hábitos...");
        List<Habit> habits = new ArrayList<>();
        HabitCategory[] categories = HabitCategory.values();
        
        for (int i = 0; i < count; i++) {
            Habit habit = new Habit();
            habit.setName(HABIT_NAMES[random.nextInt(HABIT_NAMES.length)] + " " + (i + 1));
            habit.setCategory(categories[random.nextInt(categories.length)]);
            habit.setDescription("Descripción del hábito " + (i + 1) + " para mejorar tu bienestar.");
            
            habits.add(habit);
            
            if (habits.size() == 1000) {
                habitRepository.saveAll(habits);
                habits.clear();
                System.out.println("Guardados " + (i + 1) + " hábitos...");
            }
        }
        
        if (!habits.isEmpty()) {
            habitRepository.saveAll(habits);
        }
        
        System.out.println("Hábitos generados completamente.");
    }

    @Transactional
    public void generateRoutines(int count) {
        System.out.println("Generando " + count + " rutinas...");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return;
        
        List<Routine> routines = new ArrayList<>();
        String[] daysOfWeek = {"LUNES,MIÉRCOLES,VIERNES", "MARTES,JUEVES", "LUNES,MARTES,MIÉRCOLES,JUEVES,VIERNES", "SÁBADO,DOMINGO", "TODOS LOS DÍAS"};
        
        for (int i = 0; i < count; i++) {
            Routine routine = new Routine();
            routine.setTitle(ROUTINE_TITLES[random.nextInt(ROUTINE_TITLES.length)] + " " + (i + 1));
            routine.setUser(users.get(random.nextInt(users.size())));
            routine.setDaysOfWeek(daysOfWeek[random.nextInt(daysOfWeek.length)]);
            
            routines.add(routine);
            
            if (routines.size() == 1000) {
                routineRepository.saveAll(routines);
                routines.clear();
                System.out.println("Guardadas " + (i + 1) + " rutinas...");
            }
        }
        
        if (!routines.isEmpty()) {
            routineRepository.saveAll(routines);
        }
        
        System.out.println("Rutinas generadas completamente.");
    }

    @Transactional
    public void generateGuides(int count) {
        System.out.println("Generando " + count + " guías...");
        List<Guide> guides = new ArrayList<>();
        HabitCategory[] categories = HabitCategory.values();
        
        for (int i = 0; i < count; i++) {
            Guide guide = new Guide();
            guide.setTitle(GUIDE_TITLES[random.nextInt(GUIDE_TITLES.length)] + " " + (i + 1));
            guide.setContent("Esta es una guía completa número " + (i + 1) + " que te ayudará a mejorar tus hábitos y alcanzar tus objetivos de bienestar. Incluye consejos prácticos, estrategias efectivas y recomendaciones personalizadas.");
            guide.setCategory(categories[random.nextInt(categories.length)]);
            
            guides.add(guide);
            
            if (guides.size() == 1000) {
                guideRepository.saveAll(guides);
                guides.clear();
                System.out.println("Guardadas " + (i + 1) + " guías...");
            }
        }
        
        if (!guides.isEmpty()) {
            guideRepository.saveAll(guides);
        }
        
        System.out.println("Guías generadas completamente.");
    }

    @Transactional
    public void generateReminders(int count) {
        System.out.println("Generando " + count + " recordatorios...");
        List<User> users = userRepository.findAll();
        List<Habit> habits = habitRepository.findAll();
        
        if (users.isEmpty() || habits.isEmpty()) return;
        
        List<Reminder> reminders = new ArrayList<>();
        ReminderFrequency[] frequencies = ReminderFrequency.values();
        LocalTime[] times = {LocalTime.of(7, 0), LocalTime.of(8, 0), LocalTime.of(12, 0), 
                           LocalTime.of(18, 0), LocalTime.of(20, 0), LocalTime.of(21, 0)};
        
        for (int i = 0; i < count; i++) {
            Reminder reminder = new Reminder();
            reminder.setUser(users.get(random.nextInt(users.size())));
            reminder.setHabit(habits.get(random.nextInt(habits.size())));
            reminder.setTime(times[random.nextInt(times.length)]);
            reminder.setFrequency(frequencies[random.nextInt(frequencies.length)]);
            
            reminders.add(reminder);
            
            if (reminders.size() == 1000) {
                reminderRepository.saveAll(reminders);
                reminders.clear();
                System.out.println("Guardados " + (i + 1) + " recordatorios...");
            }
        }
        
        if (!reminders.isEmpty()) {
            reminderRepository.saveAll(reminders);
        }
        
        System.out.println("Recordatorios generados completamente.");
    }

    @Transactional
    public void generateCompletedActivities(int count) {
        System.out.println("Generando " + count + " actividades completadas...");
        List<Habit> habits = habitRepository.findAll();
        
        if (habits.isEmpty()) return;
        
        List<CompletedActivity> activities = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            CompletedActivity activity = new CompletedActivity();
            activity.setHabit(habits.get(random.nextInt(habits.size())));
            
            // Generar fecha aleatoria en los últimos 30 días
            LocalDateTime randomDate = LocalDateTime.now().minusDays(random.nextInt(30));
            activity.setCompletedAt(randomDate);
            activity.setNotes("Actividad completada " + (i + 1) + " - Gran progreso!");
            
            activities.add(activity);
            
            if (activities.size() == 1000) {
                completedActivityRepository.saveAll(activities);
                activities.clear();
                System.out.println("Guardadas " + (i + 1) + " actividades completadas...");
            }
        }
        
        if (!activities.isEmpty()) {
            completedActivityRepository.saveAll(activities);
        }
        
        System.out.println("Actividades completadas generadas completamente.");
    }

    /**
     * Método para limpiar todos los datos generados (usar con precaución)
     */
    @Transactional
    public void cleanGeneratedData() {
        System.out.println("Limpiando datos generados...");
        
        completedActivityRepository.deleteAll();
        reminderRepository.deleteAll();
        guideRepository.deleteAll();
        routineRepository.deleteAll();
        habitRepository.deleteAll();
        
        // Mantener algunos usuarios admin, eliminar usuarios generados
        userRepository.deleteByEmailContaining("@healthylife.com");
        
        System.out.println("Datos limpiados completamente.");
    }

    /**
     * Método para obtener estadísticas del dataset
     */
    public void printDatasetStatistics() {
        System.out.println("=== ESTADÍSTICAS DEL DATASET ===");
        System.out.println("Usuarios: " + userRepository.count());
        System.out.println("Hábitos: " + habitRepository.count());
        System.out.println("Rutinas: " + routineRepository.count());
        System.out.println("Guías: " + guideRepository.count());
        System.out.println("Recordatorios: " + reminderRepository.count());
        System.out.println("Actividades completadas: " + completedActivityRepository.count());
        System.out.println("Logs de progreso: " + progressLogRepository.count());
        System.out.println("Roles: " + roleRepository.count());
        System.out.println("================================");
    }
}