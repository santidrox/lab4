import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
public interface Producto {
    String getCategoria();
    BigDecimal calcularPago();
    default String getAlmacenamiento() { return "SECO"; }
    default Optional<LocalDate> getVencimiento() { return Optional.empty(); }
}
