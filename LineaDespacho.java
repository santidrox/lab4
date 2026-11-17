import java.math.BigDecimal;
public final class LineaDespacho {
    private final Lote lote;
    private final BigDecimal kg;
    private final BigDecimal importe;
    public LineaDespacho(Lote lote, BigDecimal kg) {
        this.lote = java.util.Objects.requireNonNull(lote);
        this.kg = Validacion.positivo(kg, "Cantidad");
        this.importe = lote.getCostoKg().multiply(kg).multiply(new BigDecimal("1.35"));
    }
    public Lote getLote() { return lote; }
    public BigDecimal getKg() { return kg; }
    public BigDecimal getImporte() { return importe; }
}
