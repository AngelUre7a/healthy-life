# 🏥 Healthy Life API - Sistema de Gestión de Hábitos Saludables

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen)
![GraphQL](https://img.shields.io/badge/GraphQL-Schema--First-e10098)
![MariaDB](https://img.shields.io/badge/MariaDB-11.0-blue)
![JWT](https://img.shields.io/badge/Security-JWT-green)

## 📋 Descripción del Proyecto

**Healthy Life API** es una aplicación backend robusta desarrollada en **Spring Boot** que permite a los usuarios gestionar sus hábitos saludables, crear rutinas personalizadas, y hacer seguimiento de su progreso. El sistema utiliza **GraphQL** como API principal, **JWT** para autenticación, y está optimizado para manejar grandes volúmenes de datos (500,000+ registros).

---

## 🏗️ Arquitectura del Sistema

### **📊 Patrón Arquitectónico: Layered Architecture + Clean Architecture**

```
┌─────────────────────────────────────────────────────────┐
│                    GraphQL Layer                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐   │
│  │   Resolvers │ │    Auth     │ │   Data Gen      │   │
│  │             │ │   Resolver  │ │   Resolver      │   │
│  └─────────────┘ └─────────────┘ └─────────────────┘   │
└─────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────┐
│                   Business Layer                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐   │
│  │   Services  │ │  Security   │ │   Validators    │   │
│  │             │ │             │ │                 │   │
│  └─────────────┘ └─────────────┘ └─────────────────┘   │
└─────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────┐
│                 Persistence Layer                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐   │
│  │ Repositories│ │   Entities  │ │      DTOs       │   │
│  │    (JPA)    │ │             │ │                 │   │
│  └─────────────┘ └─────────────┘ └─────────────────┘   │
└─────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────┐
│                    Database Layer                       │
│                   MariaDB 11.0                          │
└─────────────────────────────────────────────────────────┘
```

---

## 🎯 Patrones de Diseño Implementados

### **1. 🔧 Repository Pattern**
**Ubicación**: `src/main/java/org/una/progra3/healthy_life/repository/`

**Propósito**: Abstrae el acceso a datos y centraliza la lógica de consultas.

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.role.canDelete = true")
    List<User> findAdminUsers();
}
```

**Beneficios**:
- ✅ Separación clara entre lógica de negocio y acceso a datos
- ✅ Facilita testing con mocks
- ✅ Reutilización de consultas complejas

### **2. 🏛️ Service Layer Pattern**
**Ubicación**: `src/main/java/org/una/progra3/healthy_life/service/`

**Propósito**: Encapsula la lógica de negocio y coordina operaciones entre repositorios.

```java
@Service
@Transactional(readOnly = true)
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public User save(User user) {
        // Business logic here
        return userRepository.save(user);
    }
    
    public Page<User> findAllPaginated(PageInputDTO pageInput) {
        // Pagination logic
    }
}
```

**Características**:
- ✅ **Transaccionalidad** gestionada con `@Transactional`
- ✅ **Lógica de negocio** centralizada
- ✅ **Paginación** implementada en todos los servicios principales

### **3. 🎭 Facade Pattern (GraphQL Resolvers)**
**Ubicación**: `src/main/java/org/una/progra3/healthy_life/resolver/`

**Propósito**: Proporciona una interfaz unificada para operaciones complejas del sistema.

```java
@Controller
public class UserResolver {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @QueryMapping
    public UserPagedResponseDTO allUsers(@Argument PageInputDTO pageInput) {
        // Coordinates multiple services
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        
        Page<User> userPage = userService.findAllPaginated(pageInput);
        return new UserPagedResponseDTO(userDTOs, pageInfo);
    }
}
```

**Resolvers Implementados**:
- 👤 `UserResolver` - Gestión de usuarios
- 🎯 `HabitResolver` - Gestión de hábitos
- 📅 `RoutineResolver` - Gestión de rutinas
- 📚 `GuideResolver` - Gestión de guías
- ⏰ `ReminderResolver` - Gestión de recordatorios
- 📈 `ProgressResolver` - Seguimiento de progreso
- 🔐 `RoleResolver` - Gestión de roles
- 🔑 `AuthResolver` - Autenticación
- 📊 `DataGenerationResolver` - Generación de datos masivos

### **4. 📦 Data Transfer Object (DTO) Pattern**
**Ubicación**: `src/main/java/org/una/progra3/healthy_life/dtos/`

**Propósito**: Transporta datos entre capas evitando exposición directa de entidades.

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Long roleId;
    private Set<Long> favoriteHabitIds;
    // Security: Password nunca se expone
}
```

**DTOs Principales**:
- 📄 `PageInputDTO` - Parámetros de paginación
- 📊 `UserPagedResponseDTO` - Respuestas paginadas
- 🔐 `LoginResponse` - Respuesta de autenticación
- 📈 `DatasetStatisticsDTO` - Estadísticas del sistema

### **5. 🔒 Strategy Pattern (Security & Permissions)**
**Ubicación**: `src/main/java/org/una/progra3/healthy_life/security/`

**Propósito**: Implementa diferentes estrategias de validación de permisos.

```java
public class PermissionValidator {
    
    public static void checkRead(User user) {
        if (user == null || user.getRole() == null || !user.getRole().isCanRead()) {
            throw new SecurityException("No read permission");
        }
    }
    
    public static void checkWrite(User user) {
        if (user == null || user.getRole() == null || !user.getRole().isCanWrite()) {
            throw new SecurityException("No write permission");
        }
    }
    
    public static void checkDelete(User user) {
        if (user == null || user.getRole() == null || !user.getRole().isCanDelete()) {
            throw new SecurityException("No delete permission");
        }
    }
}
```

### **6. 🏭 Factory Pattern (Data Generation)**
**Ubicación**: `src/main/java/org/una/progra3/healthy_life/service/DataGenerationService.java`

**Propósito**: Crea objetos complejos de manera controlada y optimizada.

```java
@Service
public class DataGenerationService {
    
    @Transactional
    public void generateMassiveDataset() {
        createBasicRoles();           // 2 roles
        generateUsers(100000);        // 100K usuarios
        generateHabits(10000);        // 10K hábitos
        generateRoutines(150000);     // 150K rutinas
        generateGuides(5000);         // 5K guías
        generateReminders(100000);    // 100K recordatorios
        generateCompletedActivities(200000); // 200K actividades
        // Total: ~565,000 registros
    }
}
```

### **7. 🎨 Template Method Pattern (Pagination)**
**Propósito**: Define el esqueleto de algoritmos de paginación reutilizables.

```java
// Template base para todas las respuestas paginadas
public abstract class PagedResponseTemplate<T> {
    protected List<T> content;
    protected PageInfo pageInfo;
    
    // Template method
    public PagedResponseTemplate(List<T> content, PageInfo pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }
}

// Implementaciones específicas
public class UserPagedResponseDTO extends PagedResponseTemplate<UserDTO> { }
public class HabitPagedResponseDTO extends PagedResponseTemplate<HabitDTO> { }
```

---

## 🔐 Sistema de Seguridad

### **Patrón: Chain of Responsibility + Strategy**

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response,
                                   FilterChain filterChain) {
        String token = extractToken(request);
        
        if (token != null && jwtUtil.validateToken(token)) {
            // Strategy: Different authentication strategies
            Authentication auth = createAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        filterChain.doFilter(request, response); // Chain continues
    }
}
```

**Niveles de Seguridad**:
1. 🔑 **JWT Token Validation** - Filtro a nivel de request
2. 🛡️ **Role-Based Access Control (RBAC)** - Validación en resolvers
3. 🎯 **Permission Granular Control** - Read/Write/Delete específicos

---

## 📊 Optimizaciones de Rendimiento

### **1. Lazy Loading Pattern**
```java
@Entity
public class User {
    @ManyToOne(fetch = FetchType.LAZY)  // Solo carga cuando se necesita
    @JoinColumn(name = "role_id")
    private Role role;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Routine> routines;
}
```

### **2. Batch Processing Pattern**
```properties
# application.properties
spring.jpa.properties.hibernate.jdbc.batch_size=1000
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.jdbc.fetch_size=50
```

### **3. Pagination Pattern**
```java
@QueryMapping
public HabitPagedResponseDTO allHabits(@Argument PageInputDTO pageInput) {
    if (pageInput == null) {
        pageInput = new PageInputDTO(0, 20); // Default pagination
    }
    
    Page<Habit> habitPage = habitService.findAllPaginated(pageInput);
    // Returns paginated results with metadata
}
```

---

## 🗃️ Modelo de Datos

### **Entidades Principales**

| Entidad | Responsabilidad | Relaciones |
|---------|----------------|------------|
| 👤 **User** | Gestión de usuarios | `@ManyToOne` con Role, `@OneToMany` con Routines |
| 🎯 **Habit** | Catálogo de hábitos | `@ManyToMany` con Users (favoritos) |
| 📅 **Routine** | Rutinas personalizadas | `@ManyToOne` con User, `@OneToMany` con Activities |
| 📈 **ProgressLog** | Seguimiento diario | `@ManyToOne` con User y Routine |
| 🔐 **Role** | Sistema de permisos | `@OneToMany` con Users |

### **Relaciones Complejas**
- **Many-to-Many**: User ↔ Hábitos favoritos
- **Many-to-Many**: Guide ↔ Hábitos recomendados
- **Cascada completa**: Routine → RoutineActivity → ProgressLog

---

## 🚀 Tecnologías y Herramientas

### **Core Framework**
- ☕ **Java 21** - Último LTS con mejoras de rendimiento
- 🍃 **Spring Boot 3.5.4** - Framework principal
- 🎯 **Spring GraphQL** - API moderna y eficiente
- 🔒 **Spring Security** - Autenticación y autorización
- 💾 **Spring Data JPA** - Abstracción de persistencia

### **Base de Datos**
- 🗄️ **MariaDB 11.0** - Base de datos principal
- 🔄 **Hibernate** - ORM con optimizaciones avanzadas
- 📊 **Connection Pooling** - HikariCP para alta concurrencia

### **Testing**
- ✅ **JUnit 5** - Framework de testing
- 🎭 **Mockito** - Mocking para unit tests
- 📊 **JaCoCo** - Coverage reports

### **Herramientas de Desarrollo**
- 🏗️ **Maven** - Gestión de dependencias
- 🐳 **Docker** - Containerización de MariaDB
- 📝 **Lombok** - Reducción de boilerplate
- 🔍 **GraphiQL** - Interfaz para testing de API

---

## 📈 Características del Sistema

### **✨ Funcionalidades Principales**
- 👥 **Gestión de Usuarios** con roles granulares
- 🎯 **Catálogo de Hábitos** categorizados y personalizables
- 📅 **Rutinas Personalizadas** con horarios y actividades
- 📈 **Seguimiento de Progreso** con estadísticas detalladas
- 📚 **Sistema de Guías** educativas con recomendaciones
- ⏰ **Recordatorios Programables** con múltiples frecuencias
- 🔐 **Autenticación JWT** segura y escalable

### **🔧 Características Técnicas**
- 📄 **Paginación Completa** en todas las consultas principales
- 🚀 **Generación de Datos Masivos** (500,000+ registros)
- ⚡ **Optimizaciones de Rendimiento** con batch processing
- 🔍 **API GraphQL** con schema-first approach
- 🛡️ **Seguridad Robusta** con RBAC y JWT
- 📊 **Logging y Monitoreo** integrado

---

## 🛠️ Configuración y Despliegue

### **Requisitos Previos**
- Java 21+
- Maven 3.9+
- Docker (para MariaDB)

### **Configuración de Base de Datos**
```bash
# Levantar MariaDB con Docker
docker pull mariadb:11.0
docker run -d --name mariadb \
  -e MYSQL_ROOT_PASSWORD=rootpass \
  -e MYSQL_DATABASE=healthy_life_db \
  -e MYSQL_USER=admin \
  -e MYSQL_PASSWORD=admin \
  -p 3307:3306 mariadb:11.0
```

### **Ejecución del Proyecto**
```bash
# Compilar y ejecutar
./mvnw spring-boot:run

# Ejecutar tests
./mvnw test

# Generar reportes de coverage
./mvnw jacoco:report
```

### **Endpoints Principales**
- 🌐 **GraphQL Playground**: http://localhost:8080/graphiql
- 📊 **API GraphQL**: http://localhost:8080/graphql
- 🔧 **Health Check**: http://localhost:8080/actuator/health

---

## 📚 Documentación Adicional

- 📊 [Diagrama de Base de Datos](./database-schema.md)
- 📖 [Guía de Paginación](./PAGINACION_GUIDE.md)

---

## 👥 Estructura del Equipo de Desarrollo

**Patrones Organizacionales Aplicados**:
- 🏗️ **Separation of Concerns** - Cada capa tiene responsabilidades específicas
- 🔄 **Dependency Injection** - Acoplamiento débil entre componentes
- 🧪 **Test-Driven Development** - Tests unitarios para cada resolver y service
- 📋 **Domain-Driven Design** - Entidades reflejan el dominio del negocio

---

## 🎯 Conclusión

El proyecto **Healthy Life API** implementa una arquitectura robusta y escalable utilizando patrones de diseño reconocidos en la industria. La combinación de **Spring Boot**, **GraphQL**, y **patrones arquitectónicos modernos** resulta en un sistema mantenible, testeable y preparado para crecer con las necesidades del negocio.

**Patrones clave aplicados**:
- ✅ Repository Pattern para acceso a datos
- ✅ Service Layer para lógica de negocio
- ✅ Facade Pattern en GraphQL Resolvers
- ✅ DTO Pattern para transferencia de datos
- ✅ Strategy Pattern para seguridad
- ✅ Factory Pattern para generación de datos
- ✅ Template Method para paginación

El sistema está optimizado para manejar **grandes volúmenes de datos** (500,000+ registros) manteniendo **alta performance** y **seguridad robusta**.

---

## 📞 Contactos

angel.urena.naranjo@est.una.ac.cr
maikel.segura.quiros@est.una.ac.cr
saul.chinchilla.badilla@est.una.ac.cr

---

*Desarrollado con cariño y dedicación usando Spring Boot y GraphQL*
