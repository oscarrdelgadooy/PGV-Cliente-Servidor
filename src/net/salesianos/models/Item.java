package net.salesianos.models;

public class Item {
    private String nombre;
    private double precioSalida;
    private String descripcion;

    public Item(String nombre, double precioSalida, String descripcion) {
        this.nombre = nombre;
        this.precioSalida = precioSalida;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioSalida() {
        return precioSalida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        String bordeSup  = "╔════════════════════════════════════════════════╗\n";
        String separador = "╠════════════════════════════════════════════════╣\n";
        String bordeInf  = "╚════════════════════════════════════════════════╝";

        String lineaNombre = String.format("║ LOTE: %-40s ║\n", nombre);
        String lineaPrecio = String.format("║ PRECIO: %-38s ║\n", precioSalida + " totis");
        String lineaDesc   = String.format("║ INFO: %-40s ║\n", descripcion);

        return "\n" + bordeSup + lineaNombre + separador + lineaPrecio + lineaDesc + bordeInf;
    }
}