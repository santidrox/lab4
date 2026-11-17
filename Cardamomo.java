import java.math.BigDecimal;
import java.time.LocalDate;
public final class Cardamomo extends Lote implements Perecedero {
    private final BigDecimal granoVerde;
    public Cardamomo(String codigo, String productor, LocalDate ingreso, BigDecimal peso,
                     Calidad calidad, BigDecimal granoVerde) {
        super(codigo, productor, ingreso, peso, calidad);
        this.granoVerde = Validacion.rango(granoVerde, "0", "100", "Grano verde");
    }
    public BigDecimal getGranoVerde() { return granoVerde; }
    @Override public int getVidaUtil() { return 180; }
    @Override public String getCategoria() { return "CARDAMOMO"; }
    @Override public BigDecimal calcularPago() {
        BigDecimal factor = granoVerde.compareTo(new BigDecimal("85")) >= 0
            ? new BigDecimal("1.10") : BigDecimal.ONE;
        return Validacion.dinero(getPesoRecibido().multiply(tarifa("60", "48", "35")).multiply(factor));
    }
}
