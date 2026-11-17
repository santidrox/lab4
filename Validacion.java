import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
public final class Validacion {
    private Validacion() { }
    public static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty())
            throw new IllegalArgumentException(campo + " no puede estar vacio.");
        return valor.trim();
    }
    public static String codigo(String valor) {
        return texto(valor, "Codigo").toUpperCase(Locale.ROOT);
    }
    public static BigDecimal positivo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0)
            throw new IllegalArgumentException(campo + " debe ser mayor que cero.");
        return valor;
    }
    public static BigDecimal rango(BigDecimal valor, String min, String max, String campo) {
        if (valor == null || valor.compareTo(new BigDecimal(min)) < 0
                || valor.compareTo(new BigDecimal(max)) > 0)
            throw new IllegalArgumentException(campo + " debe estar entre " + min + " y " + max + ".");
        return valor;
    }
    public static BigDecimal dinero(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
    public static String monto(BigDecimal valor) { return "Q" + dinero(valor).toPlainString(); }
}
