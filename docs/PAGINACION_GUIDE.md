# Guía de Implementación de Paginación - Healthy Life API

## 📋 Resumen
Esta guía explica cómo se implementó la paginación en la API GraphQL de Healthy Life y cómo generar el dataset de 500k registros requerido para la defensa.

## 🚀 Características Implementadas

### 1. Paginación en GraphQL
- **PageInput**: Input type para recibir parámetros de paginación
- **PageInfo**: Información sobre la paginación actual
- **Respuestas Paginadas**: Tipos específicos para cada entidad (UserPagedResponse, HabitPagedResponse, etc.)

### 2. Consultas con Paginación
Todas las consultas principales ahora soportan paginación:

```graphql
# Usuarios paginados
query GetUsers($pageInput: PageInput) {
  allUsers(pageInput: $pageInput) {
    content {
      id
      name
      email
    }
    pageInfo {
      hasNextPage
      hasPreviousPage
      totalElements
      totalPages
      currentPage
      pageSize
    }
  }
}

# Hábitos paginados por categoría
query GetHabitsByCategory($category: HabitCategory!, $pageInput: PageInput) {
  habitsByCategory(category: $category, pageInput: $pageInput) {
    content {
      id
      name
      category
      description
    }
    pageInfo {
      hasNextPage
      hasPreviousPage
      totalElements
      totalPages
      currentPage
      pageSize
    }
  }
}
```

### 3. Variables de Entrada
```json
{
  "pageInput": {
    "page": 0,
    "size": 20,
    "sortBy": "id",
    "sortDirection": "ASC"
  },
  "category": "PHYSICAL"
}
```

## 🗄️ Generación de Dataset Masivo

### Endpoints REST para Generación de Datos

#### 1. Generar Dataset Completo (500k+ registros)
```bash
POST /api/data-generation/massive-dataset
```
Genera:
- 100,000 usuarios
- 10,000 hábitos  
- 150,000 rutinas
- 5,000 guías
- 100,000 recordatorios
- 200,000 actividades completadas

#### 2. Obtener Estadísticas
```bash
GET /api/data-generation/statistics
```

#### 3. Limpiar Datos Generados
```bash
DELETE /api/data-generation/clean
```

#### 4. Generar Usuarios Específicos
```bash
POST /api/data-generation/users/{count}
# Ejemplo: POST /api/data-generation/users/50000
```

#### 5. Generar Hábitos Específicos
```bash
POST /api/data-generation/habits/{count}
# Ejemplo: POST /api/data-generation/habits/10000
```

## 🔧 Uso en Desarrollo

### 1. Levantar el Servidor
```bash
# Windows
./mvnw.cmd spring-boot:run

# O usar la tarea configurada en VS Code
# Ctrl+Shift+P -> "Run Task" -> "Start Spring Boot Server"
```

### 2. Generar Dataset para Defensa
```bash
# Usar Postman, curl o cualquier cliente REST
curl -X POST http://localhost:8080/api/data-generation/massive-dataset
```

### 3. Verificar Datos Generados
```bash
curl -X GET http://localhost:8080/api/data-generation/statistics
```

### 4. Probar Paginación en GraphiQL
Acceder a: `http://localhost:8080/graphiql`

```graphql
query TestPagination {
  allUsers(pageInput: { page: 0, size: 10, sortBy: "name", sortDirection: "ASC" }) {
    content {
      id
      name
      email
    }
    pageInfo {
      hasNextPage
      hasPreviousPage
      totalElements
      totalPages
      currentPage
      pageSize
    }
  }
}
```

## 📊 Ejemplos de Consultas Paginadas

### 1. Usuarios con Paginación
```graphql
query GetUsersPage1 {
  allUsers(pageInput: { page: 0, size: 50 }) {
    content {
      id
      name
      email
    }
    pageInfo {
      totalElements
      totalPages
      hasNextPage
    }
  }
}
```

### 2. Hábitos Físicos Paginados
```graphql
query GetPhysicalHabits {
  habitsByCategory(
    category: PHYSICAL, 
    pageInput: { page: 0, size: 25, sortBy: "name", sortDirection: "DESC" }
  ) {
    content {
      id
      name
      description
    }
    pageInfo {
      totalElements
      currentPage
      hasNextPage
    }
  }
}
```

### 3. Todas las Rutinas Paginadas
```graphql
query GetAllRoutines {
  allRoutines(pageInput: { page: 2, size: 30 }) {
    content {
      id
      title
      daysOfWeek
    }
    pageInfo {
      totalElements
      currentPage
      totalPages
      hasNextPage
      hasPreviousPage
    }
  }
}
```

## ⚡ Optimizaciones de Rendimiento

### 1. Índices de Base de Datos
Se recomienda agregar índices para mejorar el rendimiento:

```sql
-- Índices para paginación eficiente
CREATE INDEX idx_users_name ON users(name);
CREATE INDEX idx_habits_category ON habits(category);
CREATE INDEX idx_routines_user_id ON routines(user_id);
CREATE INDEX idx_reminders_user_id ON reminders(user_id);
CREATE INDEX idx_completed_activities_completed_at ON completed_activities(completed_at);
```

### 2. Configuración de JPA
En `application.properties`:
```properties
# Configuraciones para grandes volúmenes de datos
spring.jpa.properties.hibernate.jdbc.batch_size=1000
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
```

## 🧪 Testing de Paginación

### 1. Casos de Prueba Importantes
- Página primera (page: 0)
- Página última
- Páginas intermedias
- Tamaños de página diferentes (1, 10, 50, 100)
- Ordenamiento ascendente y descendente
- Diferentes campos de ordenamiento

### 2. Validación de Rendimiento
```graphql
# Prueba con gran volumen
query LargePageTest {
  allUsers(pageInput: { page: 1000, size: 100 }) {
    pageInfo {
      totalElements
      currentPage
      hasNextPage
    }
  }
}
```

## 🔒 Consideraciones de Seguridad

### 1. Limitaciones Implementadas
- Tamaño máximo de página: 100 elementos
- Tamaño mínimo de página: 1 elemento
- Validación de permisos en todos los resolvers

## 📝 Notas Importantes

1. **Tiempo de Generación**: El dataset completo puede tomar 10-15 minutos en generarse
2. **Espacio en Disco**: Considerar al menos 1GB libre para la base de datos
3. **Memoria RAM**: Configurar heap size adecuado para JVM si es necesario