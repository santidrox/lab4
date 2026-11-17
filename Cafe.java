import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
public class Cafe extends Lote {
    private final BigDecimal humedad;
    public Cafe(String codigo, String productor, LocalDate ingreso, BigDecimal peso,
                Calidad calidad, BigDecimal humedad) {
        super(codigo, productor, ingreso, peso, calidad);
        this.humedad = Validacion.rango(humedad, "8", "14", "Humedad");
    }
    public BigDecimal getHumedad() { return humedad; }
    @Override public String getCategoria() { return "CAFE"; }
    @Override public BigDecimal calcularPago() {
        return Validacion.dinero(calcularBase());
    }
    protected final BigDecimal calcularBase() {
        BigDecimal puntos = humedad.subtract(new BigDecimal("12")).max(BigDecimal.ZERO)
            .setScale(0, RoundingMode.CEILING);
        BigDecimal factor = BigDecimal.ONE.subtract(puntos.multiply(new BigDecimal("0.03")));
        return getPesoRecibido().multiply(tarifa("12", "9", "6")).multiply(factor);
    }
}
