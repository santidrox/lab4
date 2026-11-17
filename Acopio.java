import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public final class Acopio {
    private LocalDate fecha;
    private final Map<String, Bodega> bodegas = new LinkedHashMap<>();
    private final Map<String, Lote> historial = new LinkedHashMap<>();
    private final Map<String, ResumenFinanciero> finanzas = new LinkedHashMap<>();
    private final List<Despacho> despachos = new ArrayList<>();
    public Acopio(LocalDate fecha) { this.fecha = java.util.Objects.requireNonNull(fecha); }
    public LocalDate getFecha() { return fecha; }
    public List<Bodega> getBodegas() { return List.copyOf(bodegas.values()); }
    public List<Despacho> getDespachos() { return List.copyOf(despachos); }
    public Map<String, ResumenFinanciero> getFinanzas() { return Collections.unmodifiableMap(finanzas); }
    public void registrarBodega(Bodega bodega) {
        if (bodegas.containsKey(bodega.getCodigo()))
            throw new IllegalArgumentException("Codigo de bodega repetido.");
        bodegas.put(bodega.getCodigo(), bodega);
    }
    public Bodega buscarBodega(String codigo) {
        Bodega b = bodegas.get(Validacion.codigo(codigo));
        if (b == null) throw new IllegalArgumentException("No existe esa bodega.");
        return b;
    }
    public void validarNuevo(Lote lote) {
        if (historial.containsKey(lote.getCodigo()))
            throw new IllegalArgumentException("Codigo de lote repetido.");
        if (!lote.getIngreso().equals(fecha))
            throw new IllegalArgumentException("El ingreso debe coincidir con la fecha de operacion.");
        if (lote.getBodega() != null || lote.getEstado() != Estado.DISPONIBLE)
            throw new IllegalArgumentException("El lote no es nuevo.");
    }
    public BigDecimal preliquidar(Lote lote) { validarNuevo(lote); return lote.calcularPago(); }
    public void recibir(Lote lote, String codigoBodega) {
        validarNuevo(lote);
        Bodega b = buscarBodega(codigoBodega);
        b.validar(lote);
        BigDecimal pago = lote.calcularPago();
        b.almacenar(lote);
        historial.put(lote.getCodigo(), lote);
        resumen(lote.getCategoria()).pagar(pago);
    }
    void cargarInicial(Lote lote, String codigoBodega) {
        if (historial.containsKey(lote.getCodigo()) || lote.getIngreso().isAfter(fecha)
                || lote.estaVencido(fecha) || lote.getEstado() != Estado.DISPONIBLE)
            throw new IllegalArgumentException("Lote inicial invalido.");
        buscarBodega(codigoBodega).almacenar(lote);
        historial.put(lote.getCodigo(), lote);
        resumen(lote.getCategoria());
    }
    private ResumenFinanciero resumen(String categoria) {
        return finanzas.computeIfAbsent(categoria, c -> new ResumenFinanciero());
    }
    public List<Lote> getExistencias() {
        List<Lote> todos = new ArrayList<>();
        for (Bodega b : bodegas.values()) for (Lote l : b) todos.add(l);
        Collections.sort(todos);
        return List.copyOf(todos);
    }
    public List<Lote> getHistorial() {
        List<Lote> todos = new ArrayList<>(historial.values());
        Collections.sort(todos); return List.copyOf(todos);
    }
    public List<Lote> proximosAVencer() {
        List<Lote> proximos = new ArrayList<>();
        for (Lote l : getExistencias()) l.getVencimiento().ifPresent(v -> {
            if (!v.isBefore(fecha) && !v.isAfter(fecha.plusDays(3))) proximos.add(l);
        });
        return List.copyOf(proximos);
    }
    public Despacho despachar(String comprador, String categoria, BigDecimal kg) {
        comprador = Validacion.texto(comprador, "Comprador");
        categoria = Validacion.codigo(categoria);
        Validacion.positivo(kg, "Pedido");
        List<LineaDespacho> plan = new ArrayList<>();
        BigDecimal pendiente = kg;
        for (Lote l : getExistencias()) {
            if (l.getCategoria().equals(categoria) && !l.estaVencido(fecha)) {
                BigDecimal tomar = pendiente.min(l.getDisponible());
                plan.add(new LineaDespacho(l, tomar));
                pendiente = pendiente.subtract(tomar);
                if (pendiente.signum() == 0) break;
            }
        }
        if (pendiente.signum() > 0)
            throw new IllegalArgumentException("Existencias insuficientes; pedido rechazado sin cambios.");
        Despacho despacho = new Despacho(comprador, categoria, fecha, plan);
        for (LineaDespacho linea : plan) linea.getLote().consumir(linea.getKg());
        for (Bodega b : bodegas.values()) {
            Iterator<Lote> it = b.iterator();
            while (it.hasNext()) if (it.next().getEstado() == Estado.AGOTADO) it.remove();
        }
        resumen(categoria).cobrar(despacho.getTotal());
        despachos.add(despacho);
        return despacho;
    }
    public void avanzarFecha(LocalDate nueva) {
        java.util.Objects.requireNonNull(nueva, "Fecha requerida.");
        if (!nueva.isAfter(fecha))
            throw new IllegalArgumentException("La nueva fecha debe avanzar al menos un dia.");
        // Construye primero la lista: valida los vencimientos antes de modificar existencias.
        List<Lote> vencidos = new ArrayList<>();
        for (Lote l : getExistencias()) if (l.estaVencido(nueva)) vencidos.add(l);
        for (Bodega b : bodegas.values()) {
            Iterator<Lote> it = b.iterator();
            while (it.hasNext()) {
                Lote l = it.next();
                if (vencidos.contains(l)) {
                    resumen(l.getCategoria()).perder(l.descartar());
                    it.remove();
                }
            }
        }
        fecha = nueva;
    }
}
