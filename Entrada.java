import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;
public final class Entrada {
    private final Scanner scanner;
    public Entrada(Scanner scanner) { this.scanner = scanner; }
    public String texto(String mensaje) {
        System.out.print(mensaje);
        return Validacion.texto(scanner.nextLine(), "Entrada");
    }
    public int entero(String mensaje) {
        try { return Integer.parseInt(texto(mensaje)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Ingrese un entero valido."); }
    }
    public BigDecimal decimal(String mensaje) {
        try { return new BigDecimal(texto(mensaje)); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingrese un numero valido, con punto para los decimales.");
        }
    }
    public LocalDate fecha(String mensaje) {
        try { return LocalDate.parse(texto(mensaje)); }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha invalida. Use AAAA-MM-DD.");
        }
    }
    public Calidad calidad() {
        try { return Calidad.valueOf(texto("Calidad (A/B/C): ").toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("Calidad invalida: use A, B o C."); }
    }
}
