package net.salesianos.models;

public class Item {
    private String nombre;
    private double precioSalida;
    private String descripcion;
    private boolean isLegendary;

    public Item(String nombre, double precioSalida, String descripcion, boolean isLegendary) {
        this.nombre = nombre;
        this.precioSalida = precioSalida;
        this.descripcion = descripcion;
        this.isLegendary = isLegendary;
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

    public boolean isLegendary() {
        return isLegendary;
    }

    @Override
    public String toString() {
        String bordeSup = "╔════════════════════════════════════════════════════════════╗\n";
        String separador = "╠════════════════════════════════════════════════════════════╣\n";
        String bordeInf = "╚════════════════════════════════════════════════════════════╝";

        String prefijo = isLegendary ? "★ LEGENDARIO ★ " : "";

        String lineaNombre = String.format("║ LOTE: %-52s ║\n", prefijo + nombre);
        String lineaPrecio = String.format("║ PRECIO: %-50s ║\n", precioSalida + " totis");
        String lineaDesc = String.format("║ INFO: %-52s ║\n", descripcion);

        return "\n" + bordeSup + lineaNombre + separador + lineaPrecio + lineaDesc + bordeInf;
    }
}