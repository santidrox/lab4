import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public final class Reportes {
    private Reportes() { }
    public static void lotes(List<Lote> lotes) {
        if (lotes.isEmpty()) System.out.println("Sin lotes.");
        for (Lote l : lotes) System.out.println(l);
    }
    public static void existencias(Acopio a) {
        Map<String, BigDecimal> totales = new LinkedHashMap<>();
        for (Lote l : a.getExistencias())
            totales.merge(l.getCategoria(), l.getDisponible(), BigDecimal::add);
        for (Map.Entry<String, BigDecimal> entry : totales.entrySet()) {
            System.out.println("Categoria " + entry.getKey() + ": " + entry.getValue().toPlainString() + " kg");
            lotes(a.getExistencias().stream().filter(l -> l.getCategoria().equals(entry.getKey())).toList());
        }
        if (totales.isEmpty()) System.out.println("Sin existencias.");
    }
    public static void bodegas(Acopio a) {
        for (Bodega b : a.getBodegas()) {
            System.out.println(b);
            for (Lote l : b) System.out.println("  " + l);
        }
    }
    public static void finanzas(Acopio a) {
        a.getFinanzas().forEach((c,r) -> System.out.println(c + " | " + r));
    }
    public static void despacho(Despacho d) {
        System.out.println("Despacho a " + d.getComprador() + " | " + d.getFecha());
        for (LineaDespacho l : d.getLineas())
            System.out.println(l.getLote().getCodigo() + " | " + l.getKg().toPlainString() + " kg");
        System.out.println("Total cobrado: " + Validacion.monto(d.getTotal()));
    }
}
