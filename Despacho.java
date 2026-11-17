import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public final class Despacho {
    private final String comprador;
    private final String categoria;
    private final LocalDate fecha;
    private final List<LineaDespacho> lineas;
    private final BigDecimal total;
    public Despacho(String comprador, String categoria, LocalDate fecha, List<LineaDespacho> lineas) {
        this.comprador = Validacion.texto(comprador, "Comprador");
        this.categoria = categoria;
        this.fecha = fecha;
        this.lineas = List.copyOf(lineas);
        this.total = Validacion.dinero(lineas.stream().map(LineaDespacho::getImporte)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
    public String getComprador() { return comprador; }
    public String getCategoria() { return categoria; }
    public LocalDate getFecha() { return fecha; }
    public List<LineaDespacho> getLineas() { return lineas; }
    public BigDecimal getTotal() { return total; }
}
