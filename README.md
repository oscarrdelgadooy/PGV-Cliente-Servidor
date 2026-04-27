# Sistema de Subastas Multihilo (Cliente-Servidor)

Aplicación de consola en Java que simula una casa de subastas en tiempo real. Construida con una arquitectura pura basada en **Sockets TCP** y **Multithreading**, permite a múltiples clientes conectarse simultáneamente, realizar pujas concurrentes y recibir actualizaciones en vivo.

## Escenario Práctico Identificado

Se ha implementado un Sistema de Subastas en Tiempo Real. En este escenario, múltiples usuarios (clientes) compiten por lotes de productos únicos. Es necesaria una comunicación bidireccional constante: los clientes envían pujas y el servidor debe retransmitir (broadcast) el estado de la subasta a todos los participantes de forma asíncrona para garantizar la transparencia y competitividad.

## Roles del Sistema

| Rol          | Descripción de Funciones                                                                                                                                                                                                            |
| :----------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Servidor** | • Gestiona el ciclo de vida de los lotes.<br>• Escucha conexiones entrantes en el puerto 5000.<br>• Sincroniza el estado global (precio/ganador) entre todos los hilos.<br>• Implementa un temporizador automático de adjudicación. |
| **Cliente**  | • Proporciona la interfaz de usuario por consola.<br>• Filtra y valida las entradas antes del envío.<br>• Escucha de forma asíncrona las actualizaciones del servidor.<br>• Gestiona el cierre limpio de la sesión.                 |

## Clases y Librerías Java Empleadas

- **`java.net.ServerSocket`**: Para la escucha de peticiones de conexión en el lado del servidor.
- **`java.net.Socket`**: Para establecer el túnel de comunicación bidireccional.
- **`java.io.DataInputStream/DataOutputStream`**: Para la lectura y escritura de datos tipados (UTF, double) de forma eficiente.
- **`Thread` / `Runnable`**: Para la ejecución de procesos en paralelo (Handlers, Listeners y Timers).

## Guía de Ejecución

Desde la ruta de la aplicación:

1. Iniciar el servidor: `java -cp bin net.salesianos.server.ServerApp`
2. Conectar uno o varios clientes: `java -cp bin net.salesianos.client.ClientApp`

[Server](/src/net/salesianos/server/README_SERVER.md)