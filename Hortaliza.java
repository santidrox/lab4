import java.math.BigDecimal;
import java.time.LocalDate;
public final class Hortaliza extends Lote implements Perecedero, Refrigerado {
    private final int vidaUtil;
    public Hortaliza(String codigo, String productor, LocalDate ingreso, BigDecimal peso,
                     Calidad calidad, int vidaUtil) {
        super(codigo, productor, ingreso, peso, calidad);
        if (vidaUtil < 3 || vidaUtil > 14)
            throw new IllegalArgumentException("Vida util debe estar entre 3 y 14 dias.");
        this.vidaUtil = vidaUtil;
    }
    @Override public int getVidaUtil() { return vidaUtil; }
    @Override public String getCategoria() { return "HORTALIZA"; }
    @Override public BigDecimal calcularPago() {
        return Validacion.dinero(getPesoRecibido().multiply(tarifa("4", "3", "2"))
            .multiply(new BigDecimal("0.95")));
    }
}
