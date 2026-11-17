import java.math.BigDecimal;
import java.time.LocalDate;
public final class QuesoFresco extends Lote implements Perecedero, Refrigerado {
    private final BigDecimal grasa;
    public QuesoFresco(String codigo, String productor, LocalDate ingreso, BigDecimal peso,
                       Calidad calidad, BigDecimal grasa) {
        super(codigo, productor, ingreso, peso, calidad);
        if (calidad == Calidad.C) throw new IllegalArgumentException("No se acepta queso de calidad C.");
        this.grasa = Validacion.rango(grasa, "10", "40", "Grasa");
    }
    public BigDecimal getGrasa() { return grasa; }
    @Override public int getVidaUtil() { return 21; }
    @Override public String getCategoria() { return "QUESO_FRESCO"; }
    @Override public BigDecimal calcularPago() {
        return Validacion.dinero(getPesoRecibido().multiply(tarifa("38", "30", "0")
            .subtract(new BigDecimal("1.50"))));
    }
}
