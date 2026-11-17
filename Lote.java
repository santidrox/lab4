import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.Objects;
public abstract class Lote implements Producto, Comparable<Lote> {
    private final String codigo;
    private final String productor;
    private final LocalDate ingreso;
    private final BigDecimal pesoRecibido;
    private final Calidad calidad;
    private BigDecimal disponible;
    private Estado estado = Estado.DISPONIBLE;
    private String bodega;
    protected Lote(String codigo, String productor, LocalDate ingreso,
                   BigDecimal peso, Calidad calidad) {
        this.codigo = Validacion.codigo(codigo);
        this.productor = Validacion.texto(productor, "Productor");
        this.ingreso = Objects.requireNonNull(ingreso, "Fecha requerida.");
        this.pesoRecibido = Validacion.positivo(peso, "Peso");
        this.calidad = Objects.requireNonNull(calidad, "Calidad requerida.");
        this.disponible = peso;
    }
    public String getCodigo() { return codigo; }
    public String getProductor() { return productor; }
    public LocalDate getIngreso() { return ingreso; }
    public BigDecimal getPesoRecibido() { return pesoRecibido; }
    public Calidad getCalidad() { return calidad; }
    public BigDecimal getDisponible() { return disponible; }
    public Estado getEstado() { return estado; }
    public String getBodega() { return bodega; }
    public BigDecimal getCostoKg() {
        return calcularPago().divide(pesoRecibido, MathContext.DECIMAL128);
    }
    protected final BigDecimal tarifa(String a, String b, String c) {
        return new BigDecimal(new String[] {a, b, c}[calidad.ordinal()]);
    }
    final void asignarBodega(String codigoBodega) {
        if (bodega != null) throw new IllegalArgumentException("El lote ya fue almacenado.");
        bodega = codigoBodega;
    }
    final void consumir(BigDecimal kg) {
        Validacion.positivo(kg, "Cantidad");
        if (estado != Estado.DISPONIBLE || disponible.compareTo(kg) < 0)
            throw new IllegalArgumentException("Peso no disponible.");
        disponible = disponible.subtract(kg);
        if (disponible.signum() == 0) estado = Estado.AGOTADO;
    }
    final BigDecimal descartar() {
        BigDecimal perdida = Validacion.dinero(getCostoKg().multiply(disponible));
        disponible = BigDecimal.ZERO;
        estado = Estado.DESCARTADO;
        return perdida;
    }
    public final boolean estaVencido(LocalDate fecha) {
        return getVencimiento().map(fecha::isAfter).orElse(false);
    }
    @Override
    public final int compareTo(Lote otro) {
        int comparacion;
        if (getVencimiento().isPresent() != otro.getVencimiento().isPresent())
            return getVencimiento().isPresent() ? -1 : 1;
        LocalDate prioridad = getVencimiento().orElse(ingreso);
        LocalDate prioridadOtro = otro.getVencimiento().orElse(otro.ingreso);
        comparacion = prioridad.compareTo(prioridadOtro);
        if (comparacion == 0) comparacion = calidad.compareTo(otro.calidad);
        if (comparacion == 0) comparacion = codigo.compareTo(otro.codigo);
        return comparacion;
    }
    @Override
    public String toString() {
        return codigo + " | " + getCategoria() + " | productor=" + productor
            + " | ingreso=" + ingreso + " | " + calidad + " | "
            + disponible.stripTrailingZeros().toPlainString() + " kg | " + estado
            + " | bodega=" + bodega + " | vence="
            + getVencimiento().map(Object::toString).orElse("No vence");
    }
}
