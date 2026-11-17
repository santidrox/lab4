import java.math.BigDecimal;
public final class ResumenFinanciero {
    private BigDecimal pagos = BigDecimal.ZERO;
    private BigDecimal cobros = BigDecimal.ZERO;
    private BigDecimal perdidas = BigDecimal.ZERO;
    public BigDecimal getPagos() { return pagos; }
    public BigDecimal getCobros() { return cobros; }
    public BigDecimal getPerdidas() { return perdidas; }
    void pagar(BigDecimal monto) { pagos = pagos.add(monto); }
    void cobrar(BigDecimal monto) { cobros = cobros.add(monto); }
    void perder(BigDecimal monto) { perdidas = perdidas.add(monto); }
    @Override public String toString() {
        return "pagado=" + Validacion.monto(pagos) + " | cobrado=" + Validacion.monto(cobros)
            + " | perdidas=" + Validacion.monto(perdidas);
    }
}
