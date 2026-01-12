# SISTEMA DE GESTIÓN DE LICENCIAS – PROYECTO POO

Aplicación de escritorio desarrollada en Java para la simulación y gestión integral del proceso de emisión de licencias de conducir. El sistema permite administrar usuarios, solicitantes, trámites, exámenes y licencias aplicando principios de Programación Orientada a Objetos y la arquitectura MVC.

---

## Características principales

### Seguridad y control de acceso
- Autenticación de usuarios contra una base de datos MySQL desplegada en la nube.
- Cifrado de contraseñas mediante SHA2.
- Control de acceso por roles:
  - Administrador: gestión completa del sistema.
  - Analista: gestión operativa de trámites y licencias.
- Verificación del estado de usuario (activo/inactivo).

### Auditoría y trazabilidad
- Registro automático del responsable de cada operación mediante campos de auditoría:
  - `created_by`
  - `created_at`
- Seguimiento de las acciones realizadas por el analista durante el flujo del trámite.
- Control de sesión del usuario autenticado durante la ejecución de la aplicación.

### Reportes y consultas
- Visualización de trámites por estado: pendiente, en exámenes, aprobado, rechazado, licencia emitida.
- Filtros y búsquedas para facilitar el control y la revisión de la información.
- Preparación del sistema para la exportación de reportes administrativos.

---

## Arquitectura y tecnologías
- Lenguaje: Java (JDK 17)
- Interfaz gráfica: JavaFX
- Arquitectura: MVC (Modelo – Vista – Controlador)
- Base de datos: MySQL (instancia en la nube)
- Persistencia: JDBC
- Seguridad: Cifrado SHA2 para contraseñas
- Organización del código (paquetes principales):
  - model — (Usuario, Solicitante, Tramite, Examen, Licencia)
  - dao — Acceso a datos (interfaces e implementaciones)
  - service — Reglas de negocio y validaciones
  - ui — Controladores JavaFX y vistas (FXML)
  - resources — FXML y CSS

> La separación entre interfaz, lógica de negocio y acceso a datos facilita la mantenibilidad, escalabilidad y defensa académica del proyecto.

---

## Instrucciones de instalación y uso

1. Clonar el repositorio y abrir el proyecto en su IDE preferido (IntelliJ IDEA o NetBeans).
2. Configurar el JDK del proyecto a la versión 17 o superior.
3. Asegurarse de tener JavaFX disponible en el classpath o en el module-path (según la configuración del proyecto).
4. Ejecutar el script SQL proporcionado para crear la base de datos y las tablas necesarias.
5. Configurar las credenciales de conexión a la base de datos en el archivo de configuración o mediante variables de entorno (ver sección siguiente).
6. Ejecutar la clase principal (main) para iniciar la interfaz 
7. Iniciar sesión con las credenciales y verificar funcionalidades: registro de solicitantes, gestión de trámites, generación de licencias y reportes.

---

## Configuración de la base de datos

- Ejecutar el script SQL incluido en el repositorio que crea las tablas principales y los campos de auditoría (`created_by`, `created_at`).
- Verificar que la tabla `licencia` incluya una restricción UNIQUE en el campo `numero` para garantizar unicidad.
- Si almacena hashes o valores cifrados, asegúrese de que el código y la base de datos usen el mismo esquema de cifrado/derivación.

Variables de conexión recomendadas:
- DB_URL: `jdbc:mysql://<host>:<puerto>/<nombre_bd>?useSSL=false&serverTimezone=UTC`
- DB_USER: `<usuario_bd>`
- DB_PASS: `<contraseña_bd>`
- DB_DRIVER: `com.mysql.cj.jdbc.Driver`

Cómo aplicarlas:
- En el IDE: configure las variables de entorno en la configuración de ejecución.
- En consola (Linux/macOS):
  - export DB_URL="..."
  - export DB_USER="..."
  - export DB_PASS="..."
- En Windows CMD:
  - set DB_URL=...
  - set DB_USER=...
  - set DB_PASS=...

---

## Requisitos del entorno
- Java JDK 17 o superior
- JavaFX SDK compatible 
- Conexión a Internet para acceso a la base de datos remota
- IDE recomendado: IntelliJ IDEA o NetBeans
- (Opcional) Launch4j para generar ejecutables `.exe`

---

## Credenciales de prueba
Rol Administrador
- Usuario: admin
- Contraseña: admin123

Rol Analista
- Usuario: `analista`
- Contraseña: `analista123`


