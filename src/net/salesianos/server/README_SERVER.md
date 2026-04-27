# Documentación del Servidor (Server-Side)

El servidor implementa un modelo de **gestión concurrente** capaz de atender a múltiples clientes simultáneamente sin bloquear el hilo principal de ejecución.

## Implementación Técnica

```java
try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {

            System.out.println("Subasta iniciada en el puerto " + serverSocket.getLocalPort());

            ArrayList<DataOutputStream> clientsOutputs = new ArrayList<>();

            AuctionState auctionState = new AuctionState(clientsOutputs);
            auctionState.iniciarTemporizador();
```
El servidor inicia su ejecución estableciendo el punto de escucha y preparando el estado global que compartirán todos los clientes.

### Gestión de Solicitudes mediante Hilos

```java
System.out.println("Esperando nuevos postores...");
while (true) {
    Socket clientSocket = serverSocket.accept();

    DataOutputStream clientOutputStream = new DataOutputStream(
            new BufferedOutputStream(clientSocket.getOutputStream()));
    auctionState.getClients().add(clientOutputStream);

    DataInputStream clientInputStream = new DataInputStream(
            new BufferedInputStream(clientSocket.getInputStream()));
    String name = clientInputStream.readUTF();

    System.out.println(name + " ha entrado a la subasta.");

    ClientHandler clientHandler = new ClientHandler(clientInputStream, name, auctionState);
    clientHandler.start();
}
```

El servidor utiliza un bucle de aceptación (`accept()`) que, al recibir una conexión, delega la comunicación a un objeto `ClientHandler` que corre en su propio `Thread`. Esto garantiza que el servidor siempre esté disponible para nuevos postores.

### Gestión de Estado

- **Clase AuctionState**

```java
public class AuctionState {
    private AunctionLot aunctionLot = new AunctionLot();
    private Item itemActual;
    private double precioActual;
    private String ganadorActual;
    private List<DataOutputStream> clientsOutputs;

    private long tiempoUltimaPuja;
    private boolean subastaFinalizada = false;
    .
    .
    .
```

Es el objeto central donde reside la lógica de la subasta. Al ser compartido por múltiples hilos de clientes, recibe una lista para enviar información a todos los usuarios (clientsOutputs), inicializa un AunctionLot que maneja todos los objetos a subastar.

- **Exclusión Mutua con synchronized**

```java
public synchronized boolean procesarPuja(double nuevaPuja, String nombrePostor) {
    if (nuevaPuja > precioActual && !subastaFinalizada) {
        precioActual = nuevaPuja;
        ganadorActual = nombrePostor;
        tiempoUltimaPuja = System.currentTimeMillis();
        return true;
    }
    return false;
}
```

Para evitar que dos hilos actualicen el precio simultáneamente (lo que causaría que una puja válida se perdiera).

- **Temporizador**
```java
Thread hiloReloj = new Thread(() -> {
    while (!subastaFinalizada) {
        long segundosPasados = (System.currentTimeMillis() - tiempoUltimaPuja) / 1000;
        
        if (segundosPasados >= 30) {
            siguienteLote();
        }
        Thread.sleep(1000);
    }
});
hiloReloj.start();
```
El servidor no espera una acción externa para cerrar una puja, sino que gestiona su propio tiempo mediante un hilo en segundo plano que despierta cada segundo.

- **Broadcast**
```java
public synchronized void broadcast(String msg) {
        for (DataOutputStream out : clientsOutputs) {
            try {
                out.writeUTF(msg);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error al enviar mensaje a un cliente: \n" + e.getMessage());
            }
        }
    }
```
El método `broadcast` permite la comunicación unidireccional masiva. Es el mecanismo por el cual el servidor notifica a todos los postores sobre eventos relevantes (nuevas pujas, alertas de tiempo o cambios de lote).

- **Apostar todos los objetos**
```java
public synchronized void siguienteLote() {
        if (ganadorActual.equals("Nadie")) {
            broadcast("\n[FIN DEL TIEMPO] Nadie ha hecho puja.");
        } else {
            broadcast("\n[FIN DEL TIEMPO] ¡VENDIDO! " + ganadorActual + " se lleva el lote por " + precioActual
                    + " totis.");
        }

        if (aunctionLot.nextItemBet()) {
            this.itemActual = aunctionLot.getOneItem();
            this.precioActual = itemActual.getPrecioSalida();
            this.ganadorActual = "Nadie";
            this.tiempoUltimaPuja = System.currentTimeMillis();
            broadcast(itemActual.toString());
        } else {
            this.subastaFinalizada = true;
            broadcast("\n[ANUNCIO] --- LA SUBASTA HA TERMINADO. ¡GRACIAS POR PARTICIPAR! ---");
            System.out.println("No quedan más lotes. Fin de la subasta.");
        }
    }
```
Este método representa el fin de una puja y el inicio de la siguiente. Aquí entra en juego AunctionLot para suministrar el nuevo ítem o dar por finalizada la sesión si se agota el inventario.

### Control de Lotes

- **AunctionLot**

```java
public class AunctionLot {
    private Item[] inventory;
    private int actualIndex;

    public AunctionLot() {
        this.inventory = Constants.MOCK_LOTS;
        this.actualIndex = 0;
    }

    public Item getOneItem() {
        if (actualIndex < inventory.length) {
            return inventory[actualIndex];
        }
        return null;
    }

    public boolean nextItemBet() {
        actualIndex++;
        return actualIndex < inventory.length;
    }
}
```

Clase encargada de gestionar el inventario de la subasta. Se inicializa siempre que se inicialice un AuctionState, controla toda la lista de items para subastar separadamente. Si se quisiesen añadir más items para subastar, en **/utils/Constants.java** podréis modificar una lista añadiendo más datos, siguiendo la estructura del constructor del objeto de esta App en **/models/Item.java**:

```java
public Item(String nombre, double precioSalida, String descripcion, boolean isLegendary) {
        this.nombre = nombre;
        this.precioSalida = precioSalida;
        this.descripcion = descripcion;
        this.isLegendary = isLegendary;
    }
```

[Volver](/README.md)