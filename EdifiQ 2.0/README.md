# EdifiQ 2.0 — React + Spring Boot + MySQL

Proyecto organizado en frontend React y backend Spring Boot. React no se conecta directamente a MySQL: todas las operaciones de datos pasan por la API REST.

## Estructura

- `edifiq-frontend/`: interfaz React, rutas, páginas, componentes, contexto de autenticación y cliente Axios.
- `edifiq-backend/`: API Spring Boot organizada por `controller`, `service`, `repository`, `entity`, `dto`, `security`, `config` y `exception`.
- `edifiq-2.0.sql`: esquema y datos iniciales de la base de datos `edifiq`.

## Puesta en marcha

1. Importar `edifiq-2.0.sql` en MySQL/MariaDB y verificar que exista la BD `edifiq`.
2. Configurar las credenciales de BD en `edifiq-backend/src/main/resources/application.properties`.
3. Desde `edifiq-backend` ejecutar en Windows:

```bat
mvnw.cmd clean spring-boot:run
```

La API queda en `http://localhost:8080/api`.

4. Desde `edifiq-frontend` ejecutar:

```bat
npm install
npm run dev
```

El frontend queda normalmente en `http://localhost:5173`.

## Roles y alcance

### Administrador
Gestión completa de personas, usuarios, vigilantes, torres, apartamentos, zonas comunes, visitas, paquetes, recibos y reservas.

### Vigilante
Consulta de personas, gestión operativa de visitas y paquetes, y consulta de reservas. No puede crear, editar ni eliminar personas ni modificar reservas.

### Residente
Accede únicamente a la información asociada a su apartamento activo y puede crear/modificar/cancelar sus propias reservas, además de consultar paquetes, visitas y recibos y marcar sus recibos como pagados.

### Todos los roles
`/perfil` permite actualizar datos personales, nombre de usuario y contraseña.

## API principal

- `/api/auth/login`, `/api/auth/me`, `/api/auth/logout`
- `/api/catalogos`
- `/api/dashboard/*`
- `/api/personas`
- `/api/usuarios`
- `/api/vigilantes`
- `/api/torres`
- `/api/apartamentos`
- `/api/zonas`
- `/api/visitas`
- `/api/paquetes`
- `/api/recibos`
- `/api/reservas`
- `/api/perfil`

## Nota sobre seguridad
La autorización está implementada en backend con Spring Security y `@PreAuthorize`; las restricciones del frontend son únicamente de experiencia de usuario y no sustituyen la seguridad del servidor.
