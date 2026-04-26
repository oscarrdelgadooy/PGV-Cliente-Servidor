package net.salesianos.utils;

import net.salesianos.models.Item;

public class Constants {
    public static final int SERVER_PORT = 5000;
    public static final double PRECIO_INICIAL = 100.0;

    public static final String[] CMD_COMMANDS = {"precio", "ganador", "ayuda"};

    public static final Item[] MOCK_LOTS = {
        new Item("Peine de Adrián", 100.0, "Un peine con el que Adrián se peina cada mañana antes de venir a clase.", false),
        new Item("Cromo de la Boda de Adrián (Edición Limitada)", 1200.0, "Un cromo que conmemora la boda de Adrián, con detalles brillantes y una foto exclusiva del evento.", true),
        new Item("Antena extensible de Alexis", 50.0, "Tipica antena extensible de las antiguas radios que Alexis usa para explicar en clase, sin miedo al éxito.", false),
        new Item("Usb con linux portable de Santi", 100.0, "Una memoria USB con una versión portable de Linux, ideal para los amantes de la informática y la seguridad informática.", false),
        new Item("La bata de Cedrés", 1000.0, "La bata que Cedrés usa en clase, se dice que a veces sale caminando sola...", true),
        new Item("La correa de edu", 35.0, "La correa que tiene edu en el cuello.", false)
    };
}
