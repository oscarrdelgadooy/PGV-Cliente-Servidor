# **Crea una clase con métodos de encriptación y desencriptación de información.**

**Clase de Criptografía (RSA)**

Para asegurar la aplicación y proteger la información transmitida, se ha creado una clase con métodos de encriptación y desencriptación. Se ha optado por un sistema **asimétrico** utilizando el algoritmo **RSA**, proporcionando un esquema robusto basado en un par de claves.

**Análisis de la implementación (AsymmetricCipher.java)**

La clase centraliza la lógica criptográfica y se divide en cuatro métodos principales:
```java
public class AsymmetricCipher {

    private static final String ALGORITHM = "RSA";

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);  Crea el par de claves (Pública y Privada). 
                                                                            El servidor lo ejecuta al arrancar, asegurando que la clave privada nunca 
                                                                            abandone su memoria.
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public static PublicKey getPublicKeyFromString(String keyBase64) throws Exception {     Toma la clave pública que viaja en formato Base64 por la red y la 
                                                                                            reconstruye en un objeto PublicKey válido en el 
                                                                                            cliente usando X509EncodedKeySpec.  
        byte[] publicBytes = Base64.getDecoder().decode(keyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    public static String encrypt(String data, PublicKey publicKey) throws Exception {   Aplica el cifrado matemático. Recibe texto plano y la clave pública,
                                                                                        transforma el mensaje y lo codifica en \`Base64\` para evitar errores
                                                                                        de codificación al enviarlo por el socket.  
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {    Realiza el proceso inverso en el servidor. Decodifica el
                                                                                                    paquete \`Base64\` recibido y utiliza la clave privada para 
                                                                                                    revelar el texto original de forma segura.
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        return new String(cipher.doFinal(decodedBytes), "UTF-8");
    }
}
```
