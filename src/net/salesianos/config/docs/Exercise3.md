# **Modifica la aplicación cliente y la aplicación servidor para que el tránsito de información entre ambas esté encriptado.**

**Integración del Cifrado en Cliente y Servidor**

Se ha modificado la arquitectura de intercambio de información de ambas aplicaciones para garantizar que el tránsito sensible entre ellas viaje de forma encriptada. 

## **El proceso de Handshake (Intercambio de Claves)**

Para habilitar el cifrado asimétrico sin necesidad de pre-configurar claves estáticas en el código fuente, se ha implementado un protocolo de presentación dinámica al inicio de la conexión:

* **Servidor:** Genera un par de claves (**KeyPair**) en memoria al arrancar. Al aceptar un nuevo socket, el hilo `ClientHandler` envía al cliente su Clave Pública (**PublicKey**) codificada en formato Base64.
* **Cliente:** Al conectarse, recibe esta cadena, la decodifica y reconstruye la `PublicKey` del servidor en su memoria local para cifrar sus mensajes.

El servidor gestiona la seguridad desde el hilo `ClientHandler`, asegurando que la clave privada nunca abandone el entorno del servidor.

```java
// ClientHandler.java - Método run()
try {
    // 1. Convertimos la llave pública a String Base64 para enviarla por el socket
    String publicKeyBase64 = java.util.Base64.getEncoder().encodeToString(serverKeys.getPublic().getEncoded());
    out.writeUTF(publicKeyBase64);
    out.flush();

    // 2. Recibimos el nombre del cliente (Ya viene cifrado desde el origen)
    String nombreCifrado = in.readUTF();
    
    // 3. DESENCRIPTACIÓN: Usamos nuestra llave PRIVADA (única capaz de abrir el mensaje)
    this.nombre = AsymmetricCipher.decrypt(nombreCifrado, serverKeys.getPrivate());
    
    // A partir de aquí, cada mensaje recibido se procesa con AsymmetricCipher.decrypt()
} catch (Exception e) {
    System.out.println("Error en el protocolo de seguridad: " + e.getMessage());
}
```

## **Implementación en la Aplicación Cliente (ClientApp)**
El cliente debe esperar la llave del servidor antes de enviar cualquier información identificativa.

```java
// ClientApp.java
// 1. RECUPERACIÓN: Leemos la llave pública que nos envía el servidor
String serverPubKeyString = inputStream.readUTF();
PublicKey serverPublicKey = AsymmetricCipher.getPublicKeyFromString(serverPubKeyString);

// 2. CIFRADO: Antes de enviar el nombre, lo encriptamos con la pública del servidor
String nombreCifrado = AsymmetricCipher.encrypt(name, serverPublicKey);
outputStream.writeUTF(nombreCifrado);
outputStream.flush();

// 3. FLUJO CONTINUO: Las pujas también se cifran antes de salir
String pujaCifrada = AsymmetricCipher.encrypt(inputUsuario, serverPublicKey);
outputStream.writeUTF(pujaCifrada);
```

**Flujo de datos asegurado**
Emisión desde el Cliente: Cualquier dato introducido por el usuario (nombre, comandos o cantidades a pujar) pasa por el método AsymmetricCipher.encrypt() utilizando la clave pública del servidor antes de ser enviado por el flujo de red.

**Recepción en el Servidor:** El **ClientHandler** lee los paquetes encriptados y ejecuta AsymmetricCipher.decrypt() utilizando su Clave Privada (*PrivateKey*). Sólo tras este paso, la información real es procesada por el motor de la subasta.

**Decisión de Diseño: Cifrado Selectivo**  
Se ha optado por un modelo de seguridad eficiente. Las vías de subida (acciones del postor) están estrictamente cifradas para evitar suplantaciones de identidad y espionaje. Sin embargo, los **broadcasts** del servidor (anuncios de lotes, precios actuales o avisos del temporizador) se transmiten en texto plano. Esto optimiza los recursos de la red al tratar dichos mensajes como información pública inherente al estado de la subasta, cumpliendo con la protección de los datos sin sobrecargar el procesamiento del cliente.