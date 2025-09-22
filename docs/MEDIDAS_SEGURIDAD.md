# 🔐 MEDIDAS DE SEGURIDAD IMPLEMENTADAS

1) Inyección SQL (SQL Injection)
- Qué es: intento de insertar SQL malicioso a través de entradas de usuario para leer o alterar datos.
- Cómo se mitiga: consultas parametrizadas con JPA/Hibernate, validación estricta de tipos en GraphQL y políticas de logging que evitan exponer SQL.
- Resultado: prevención efectiva frente a inyección SQL.

2) Robo de sesiones (Session Hijacking)
- Qué es: uso indebido de tokens de autenticación robados para suplantar al usuario.
- Cómo se mitiga: tokens JWT con expiración corta, contraseñas con hash BCrypt y verificación del token en cada petición.
- Resultado: alta protección ante robo y reutilización de credenciales.

3) Escalación de privilegios (Privilege Escalation)
- Qué es: intento de realizar acciones o acceder a datos sin permisos adecuados.
- Cómo se mitiga: control de acceso basado en roles (RBAC) con permisos granulares y validación de permisos antes de ejecutar operaciones.
- Resultado: control de acceso robusto y consistente.

Resumen rápido
- SQL Injection: prevenido con JPA/GraphQL y buenas prácticas de logging.
- Session Hijacking: mitigado con JWT expirable y BCrypt.
- Privilege Escalation: controlado con RBAC y validación de permisos.