# 🏢 EdifiQ 2.0

### Sistema de gestión para propiedad horizontal

EdifiQ 2.0 es una aplicación web desarrollada para la **administración y gestión de una propiedad horizontal**. El sistema permite centralizar procesos relacionados con residentes, usuarios, apartamentos, torres, visitas, paquetes, recibos y reservas de zonas comunes.

El proyecto está construido bajo una arquitectura separada de **Frontend + API REST + Base de Datos**, permitiendo mantener una comunicación organizada entre la interfaz de usuario y la lógica del sistema.

> 🎓 Proyecto desarrollado con fines académicos como parte del proceso de formación del programa **Tecnólogo en Análisis y Desarrollo de Software (ADSO) — SENA**.

---

## 🛠️ Tecnologías

### Frontend

* React 19
* Vite
* JavaScript
* Axios
* React Router
* Bootstrap 5

### Backend

* Java 25
* Spring Boot 4.1.1
* Spring Web MVC
* Spring Data JPA
* Spring Security
* Spring Validation
* Spring Actuator
* Maven

### Base de datos

* MySQL
* Hibernate / JPA

### Herramientas

* Git
* GitHub
* Visual Studio Code
* IntelliJ IDEA
* MySQL / MariaDB

---

## 🏗️ Arquitectura

EdifiQ utiliza una arquitectura de tres capas:

```text
┌──────────────────────────┐
│       FRONTEND           │
│                          │
│ React + Vite + Axios     │
└────────────┬─────────────┘
             │
             │ HTTP / REST
             ▼
┌──────────────────────────┐
│        BACKEND           │
│                          │
│ Spring Boot              │
│ Spring Security          │
│ JPA / Hibernate          │
└────────────┬─────────────┘
             │
             │ SQL
             ▼
┌──────────────────────────┐
│       BASE DE DATOS      │
│                          │
│          MySQL           │
└──────────────────────────┘
```

El frontend **no se conecta directamente a MySQL**. Todas las operaciones relacionadas con los datos pasan por la API REST desarrollada en Spring Boot.

---

## 📂 Estructura del proyecto

```text
EdifiQ/
│
├── edifiq-frontend/
│   ├── public/
│   ├── src/
│   │   ├── assets/
│   │   ├── components/
│   │   ├── context/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   └── main.jsx
│   │
│   ├── .env.example
│   ├── package.json
│   └── vite.config.js
│
├── edifiq-backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── EdifiQ/
│   │       │       ├── config/
│   │       │       ├── controller/
│   │       │       ├── dto/
│   │       │       ├── entity/
│   │       │       ├── exception/
│   │       │       ├── repository/
│   │       │       └── ...
│   │       │
│   │       └── resources/
│   │
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── edifiq-2.0.sql
└── README.md
```

---

# 👥 Roles del sistema

EdifiQ implementa un sistema de permisos basado en roles.

### 👨‍💼 Administrador

Cuenta con acceso completo a la administración del sistema.

Puede gestionar:

* Personas
* Usuarios
* Vigilantes
* Torres
* Apartamentos
* Zonas comunes
* Visitas
* Paquetes
* Recibos
* Reservas

---

### 🛡️ Vigilante

El vigilante tiene acceso a las funciones relacionadas con la operación y control de acceso.

Puede:

* Consultar personas.
* Gestionar visitas.
* Gestionar paquetes.
* Consultar reservas.
* Consultar información necesaria para sus funciones.

No tiene permisos para administrar completamente la información de residentes ni modificar las reservas.

---

### 🏠 Residente

El residente puede acceder a la información relacionada con su apartamento activo.

Puede:

* Consultar información de su apartamento.
* Consultar paquetes.
* Consultar visitas.
* Consultar recibos.
* Consultar reservas.
* Crear reservas.
* Modificar sus propias reservas.
* Cancelar sus propias reservas.
* Marcar sus recibos como pagados.

---

### 👤 Perfil

Todos los roles disponen de un módulo de perfil donde pueden:

* Actualizar información personal.
* Cambiar nombre de usuario.
* Cambiar contraseña.

---

# 📋 Módulos

## 🔐 Autenticación

El sistema cuenta con:

* Inicio de sesión.
* Cierre de sesión.
* Consulta de sesión actual.
* Control de acceso.
* Autorización según rol.

Endpoints principales:

```text
/api/auth/login
/api/auth/me
/api/auth/logout
```

---

## 📊 Dashboard

El dashboard proporciona información general del sistema mediante diferentes endpoints:

```text
/api/dashboard/*
```

---

## 👤 Personas

Permite administrar la información de las personas registradas en el sistema.

```text
/api/personas
```

---

## 👨‍💼 Usuarios

Permite administrar las cuentas de usuario y sus respectivos roles.

```text
/api/usuarios
```

---

## 🛡️ Vigilantes

Permite gestionar la información de los vigilantes.

```text
/api/vigilantes
```

---

## 🏢 Torres

Permite administrar las torres de la propiedad horizontal.

```text
/api/torres
```

---

## 🏠 Apartamentos

Permite gestionar los apartamentos asociados a cada torre.

```text
/api/apartamentos
```

---

## 🏊 Zonas comunes

Permite administrar las zonas comunes disponibles para los residentes.

```text
/api/zonas
```

---

## 👥 Visitas

Permite controlar el ingreso y salida de visitantes.

```text
/api/visitas
```

Los estados principales de una visita son:

```text
Pendiente
Ingresó
Finalizada
Cancelada
```

---

## 📦 Paquetes

Permite llevar el control de los paquetes recibidos por los residentes.

```text
/api/paquetes
```

Estados disponibles:

```text
Recibido
Entregado
```

---

## 💰 Recibos

Permite consultar y administrar los recibos de los apartamentos.

```text
/api/recibos
```

Estados:

```text
Pendiente
Pagado
```

---

## 📅 Reservas

Permite administrar las reservas de las zonas comunes.

```text
/api/reservas
```

Estados:

```text
Pendiente
Aprobada
Cancelada
```

---

## 📚 Catálogos

El backend proporciona información de los diferentes catálogos utilizados por el sistema.

```text
/api/catalogos
```

Entre ellos se encuentran:

* Tipos de documento.
* Tipos de residente.
* Tipos de visita.
* Tipos de servicio.
* Estados.
* Roles.
* Permisos.

---

# 🔒 Seguridad

La seguridad de EdifiQ se encuentra implementada principalmente en el backend mediante:

* Spring Security.
* Control de sesiones.
* Autorización basada en roles.
* `@PreAuthorize`.
* Validación de solicitudes.
* Restricción de endpoints.

Las restricciones implementadas en React son principalmente de experiencia de usuario.

**La seguridad real se encuentra en el servidor**, por lo que ocultar una opción en el frontend no constituye por sí mismo una medida de seguridad.

---

# 🗄️ Base de datos

El proyecto incluye el archivo:

```text
edifiq-2.0.sql
```

Este script crea la base de datos:

```text
edifiq
```

e incluye las tablas, relaciones y datos iniciales necesarios para ejecutar el sistema.

Entre las entidades principales se encuentran:

```text
persona
usuario
rol
permiso
torre
apartamento
apartamento_persona
visita
paquete
recibo
reserva_zona
zona_comun
notificacion
historial
```

---

# ⚙️ Requisitos

Para ejecutar el proyecto localmente necesitas:

* **Java 25**
* **Node.js**
* **npm**
* **MySQL o MariaDB**
* **Git**

Puedes verificar las instalaciones con:

```bash
java -version
node -v
npm -v
git --version
```

Para Maven no es necesario instalarlo globalmente, ya que el backend incluye **Maven Wrapper**.

---

# 🚀 Instalación

## 1. Clonar el repositorio

```bash
git clone https://github.com/TU-USUARIO/edifiq.git
```

Entrar al proyecto:

```bash
cd edifiq
```

---

## 2. Crear la base de datos

Abrir MySQL y ejecutar el archivo:

```text
edifiq-2.0.sql
```

El script se encarga de crear la base de datos `edifiq`.

También puede ejecutarse desde MySQL:

```sql
SOURCE edifiq-2.0.sql;
```

---

# 🔧 Configurar el Backend

El backend utiliza variables de entorno para permitir la configuración de la conexión a la base de datos.

Archivo:

```text
edifiq-backend/src/main/resources/application.properties
```

Configuración utilizada por defecto:

```properties
DB_URL=jdbc:mysql://localhost:3306/edifiq
DB_USERNAME=root
DB_PASSWORD=
```

También se puede utilizar una configuración personalizada mediante variables de entorno:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
SERVER_PORT
```

### ⚠️ Importante

No subir contraseñas reales, credenciales ni información sensible al repositorio.

---

# ▶️ Ejecutar el Backend

Entrar a la carpeta:

```bash
cd edifiq-backend
```

### Windows

```bat
mvnw.cmd clean spring-boot:run
```

### Linux / macOS

```bash
./mvnw clean spring-boot:run
```

La API estará disponible en:

```text
http://localhost:8080/api
```

---

# ▶️ Ejecutar el Frontend

Abrir otra terminal:

```bash
cd edifiq-frontend
```

Instalar las dependencias:

```bash
npm install
```

Crear el archivo `.env` a partir de `.env.example`.

### Windows

```bat
copy .env.example .env
```

### Linux / macOS

```bash
cp .env.example .env
```

La configuración por defecto es:

```env
VITE_API_URL=http://localhost:8080/api
```

Finalmente ejecutar:

```bash
npm run dev
```

Vite mostrará la dirección local del frontend, normalmente:

```text
http://localhost:5173
```

---

# 🔄 Flujo de funcionamiento

```text
Usuario
   │
   ▼
React
   │
   │ Axios / HTTP
   ▼
Spring Boot
   │
   ├── Controllers
   ├── Services
   ├── Repositories
   ├── Security
   └── Entities
   │
   ▼
MySQL
```

---

# 🧪 Scripts del Frontend

Dentro de `edifiq-frontend` están disponibles:

### Desarrollo

```bash
npm run dev
```

### Compilar para producción

```bash
npm run build
```

### Ejecutar ESLint

```bash
npm run lint
```

### Previsualizar la compilación

```bash
npm run preview
```

---

# 📡 API REST

Los principales recursos de la API son:

```text
/api/auth
/api/catalogos
/api/dashboard
/api/personas
/api/usuarios
/api/vigilantes
/api/torres
/api/apartamentos
/api/zonas
/api/visitas
/api/paquetes
/api/recibos
/api/reservas
/api/perfil
```

---

# 🎯 Objetivos del proyecto

EdifiQ busca:

* Digitalizar procesos de administración residencial.
* Centralizar la información de la propiedad horizontal.
* Facilitar el control de visitantes.
* Llevar un registro de paquetes.
* Gestionar recibos.
* Administrar reservas de zonas comunes.
* Controlar el acceso mediante roles y permisos.
* Aplicar una arquitectura frontend/backend.
* Implementar una API REST.
* Aplicar conceptos de bases de datos relacionales.
* Fortalecer las competencias adquiridas durante la formación ADSO.

---

# 🎓 Contexto académico

**Proyecto:** EdifiQ 2.0
**Programa:** Tecnólogo en Análisis y Desarrollo de Software (ADSO)
**Institución:** Servicio Nacional de Aprendizaje — SENA

El proyecto tiene fines académicos y formativos y representa la aplicación práctica de conocimientos relacionados con:

* Análisis y diseño de sistemas.
* Desarrollo web.
* Programación orientada a objetos.
* Bases de datos.
* APIs REST.
* Seguridad.
* Arquitectura de software.
* Control de versiones con Git.
* Desarrollo frontend y backend.

---

# 📌 Estado del proyecto

🟡 **En desarrollo**

El proyecto continúa en proceso de construcción y puede recibir nuevas funcionalidades, mejoras de seguridad, optimizaciones y cambios en la interfaz.

---

# 👨‍💻 Equipo

Proyecto desarrollado por aprendices del programa:

**Tecnólogo en Análisis y Desarrollo de Software — SENA**

### Integrantes

* 👤 Juan Esteban Puentes Correa
* 👤 Antony Mercado Betin
* 👤 Nestor David Cuadrado Olivero
* 👤 Jenny Zamira Cruz Chica

> Reemplazar los nombres anteriores por los integrantes reales del proyecto.

---

# 📄 Licencia

Proyecto desarrollado con fines académicos.

---

<p align="center">

## 🏢 EdifiQ 2.0

**Gestión inteligente para propiedad horizontal**

Desarrollado con ❤️ utilizando React, Spring Boot y MySQL.

</p>
