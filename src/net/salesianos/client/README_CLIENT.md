# 💻 Documentación del Cliente (Client-Side)

La aplicación cliente ha sido diseñada bajo el principio de **UI No-Bloqueante**, permitiendo que el usuario interactúe con el teclado mientras recibe actualizaciones del servidor en tiempo real sin interrupciones visuales ni bloqueos de flujo.

## Implementación Técnica

### Inicialización y Conexión

```java
System.out.print("Introduce tu nombre de postor: ");
String name = scanner.nextLine();

try (Socket socket = new Socket("localhost", Constants.SERVER_PORT)) {
    DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    outputStream.writeUTF(name);
    outputStream.flush();

    DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    ServerListener listener = new ServerListener(inputStream);
    listener.start();

    System.out.println("Conectado! Disfruta de tu estancia:");
```

El cliente inicia la comunicación estableciendo un **Socket TCP** y configurando los flujos de datos. Inmediatamente después, realiza un "handshake" enviando el nombre del postor.

### Gestión de Hilos (Multithreading)

Para resolver el problema de esperar una entrada de teclado (Scanner.nextLine()) mientras llegan mensajes asíncronos del servidor, la aplicación divide sus responsabilidades en dos hilos:

- 1. **Hilo Principal (Gestión de Entrada)**
     El hilo main se encarga exclusivamente de capturar lo que el usuario escribe y delegar la **validación** al sistema de filtros antes de intentar cualquier envío por red.

- 2. **Hilo de Escucha Asíncrona (ServerListener)**
     Este hilo secundario permanece en un bucle infinito **escuchando** el InputStream del socket. Su función es procesar anuncios de pujas, alertas de tiempo y resultados de subasta.

```java
public class ServerListener extends Thread {
    @Override
    public void run() {
        try {
            while (true) {
                String serverMessage = this.inputStream.readUTF();
                System.out.println("[ANUNCIO] " + serverMessage);
                System.out.print("Tu puja -> ");
            }
        } catch (IOException e) {
            System.out.println("[IOE] Conexión cerrada por el servidor.");
        }
    }
}
```

### Filtro de Entrada (FilterChain)

Una vez establecida la conexión, el flujo de datos salientes es supervisado por un sistema de validación local. El cliente actúa como un middleware de seguridad, filtrando la información antes de que llegue a la red para garantizar una buena comunicación.

_Lógica de Control_
El proceso comienza en el bucle principal, que invoca a la lógica de filtrado para decidir si el mensaje debe ser enviado, procesado localmente o descartado por error de formato.

```java
while (true) {
    String input = scanner.nextLine();
    try {
        if (!FilterChain.filter_chain(input, outputStream)) {
            break;
}
```

```java
public class FilterChain {
    public static boolean filter_chain(String input, DataOutputStream out) throws IOException {
        input = input.trim().toLowerCase();

        if (input.isEmpty()) {
            System.out.print("Tu puja -> ");
            return true;
        }

        if (input.equals("salir")) {
            return false;
        }

        for (String command : Constants.CMD_COMMANDS) {
            if (input.equals(command)) {
                out.writeUTF("COMANDO_" + command.toUpperCase());
                out.flush();
                return true;
            }
        }
        try {
            Double.parseDouble(input);
            out.writeUTF(input);
            out.flush();
        } catch (NumberFormatException nfe) {
            System.out.println(
                    "[ERROR] Entrada inválida. Usa números para pujar, 'precio', 'ganador', 'ayuda' o 'salir'.");
            System.out.print("Tu puja -> ");
        }
        return true;
    }
}
```

| Resultado del Filtro | Acción Realizada                                                       |
| -------------------- | ---------------------------------------------------------------------- |
| Válido               | Se envía el valor numérico al servidor para procesar la puja.          |
| Comando              | Se procesa la acción (ayuda, salir, precio) localmente o vía servidor. |
| Error / Vacío        | Se notifica al usuario en local sin consumir recursos de red.          |

### Comandos y Experiencia de Usuario

El cliente reconoce comandos específicos definidos en la clase Constants.java. Esto facilita la expansión del sistema (por ejemplo, añadir un comando historial).

| Comando  | Función Técnica                                                              |
| -------- | ---------------------------------------------------------------------------- |
| [Número] | Envía una propuesta económica para intentar superar la puja actual.          |
| precio   | Solicita información actualizada sobre el valor del lote actual.             |
| ganador  | Consulta quién es el líder de la subasta en el momento actual.               |
| ayuda    | Despliega localmente el manual de comandos disponibles.                      |
| salir    | Cierra los flujos de datos y termina el proceso del cliente de forma limpia. |

[Volver](/README.md)