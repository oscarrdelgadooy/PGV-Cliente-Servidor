# 5. Esquema de Seguridad basado en Roles (RBAC)

Para garantizar la escalabilidad y el control de acceso en un entorno de producción, se propone la implementación de un modelo de **Control de Acceso Basado en Roles**, este sistema asegura que cada usuario interactúe con la subasta únicamente a través de los permisos asignados.

## Matriz de Roles y Permisos Proyectada

A continuación, se define la estructura de permisos para los diferentes perfiles de usuario que interactuarían con el sistema:

| Rol | Descripción | Acciones Permitidas |
| :--- | :--- | :--- |
| **Postor** | Usuario base que participa en las subastas. | - Conexión mediante cifrado RSA.<br>- Realizar pujas por encima del precio actual.<br>- Consultar comandos disponibles.|
| **Subastador** | Perfil moderador que gestiona el ritmo de la sesión. | - Iniciar o pausar el temporizador de la subasta.<br>- Adjudicar lotes de forma manual en caso de conflicto.<br>- Expulsar (kick) a postores por comportamiento indebido. |
| **Administrador** | Responsable técnico con control total. | - Gestión del inventario de lotes (CRUD).<br>- Rotación y gestión de los pares de claves RSA.<br>- Acceso a logs de auditoría y direcciones IP de los nodos. |

## Propuesta de Implementación Técnica

Para integrar este esquema en la arquitectura actual, se requerirían dos fases adicionales:

### 1. Autenticación (¿Quién eres?)
Implementar un sistema de credenciales (usuario y contraseña) previo al intercambio de llaves. Las contraseñas se almacenarían en el servidor utilizando un algoritmo de hashing robusto como **BCrypt**, nunca en texto plano.

### 2. Autorización (¿Qué puedes hacer?)
Modificar la clase `FilterChain` para que actúe como un **Interceptor de Comandos**. Antes de procesar una cadena como `COMANDO_GANADOR` o una puja, el filtro verificaría el rol asociado al hilo `ClientHandler` que emite la petición. Si el rol no tiene el permiso necesario, el servidor denegaría la acción y registraría un intento de acceso no autorizado.

---

Este diseño asegura que, aunque un usuario logre conectar un cliente al socket, sus acciones estarán limitadas por su perfil, protegiendo la integridad de la subasta y la lógica de negocio.