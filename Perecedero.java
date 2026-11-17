import java.time.LocalDate;
import java.util.Optional;
public interface Perecedero extends Producto {
    LocalDate getIngreso();
    int getVidaUtil();
    @Override
    default Optional<LocalDate> getVencimiento() {
        return Optional.of(getIngreso().plusDays(getVidaUtil()));
    }
}
