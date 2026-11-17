import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
public final class CafeOrganico extends Cafe {
    private final String certificadora;
    private final LocalDate validezSello;
    public CafeOrganico(String codigo, String productor, LocalDate ingreso, BigDecimal peso,
            Calidad calidad, BigDecimal humedad, String certificadora, LocalDate validezSello) {
        super(codigo, productor, ingreso, peso, calidad, humedad);
        this.certificadora = Validacion.texto(certificadora, "Certificadora");
        this.validezSello = Objects.requireNonNull(validezSello, "Validez requerida.");
        if (validezSello.isBefore(ingreso))
            throw new IllegalArgumentException("El sello esta vencido en la fecha de ingreso.");
    }
    public String getCertificadora() { return certificadora; }
    public LocalDate getValidezSello() { return validezSello; }
    @Override public String getCategoria() { return "CAFE_ORGANICO"; }
    @Override public BigDecimal calcularPago() {
        return Validacion.dinero(super.calcularBase().add(getPesoRecibido().multiply(new BigDecimal("0.75"))));
    }
}
