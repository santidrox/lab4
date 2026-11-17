import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
public class Bodega implements Iterable<Lote> {
    private final String codigo;
    private final BigDecimal capacidad;
    private final String ambiente;
    private final List<Lote> lotes = new ArrayList<>();
    public Bodega(String codigo, BigDecimal capacidad, String ambiente) {
        this.codigo = Validacion.codigo(codigo);
        this.capacidad = Validacion.positivo(capacidad, "Capacidad");
        this.ambiente = Validacion.codigo(ambiente);
    }
    public String getCodigo() { return codigo; }
    public BigDecimal getCapacidad() { return capacidad; }
    public String getAmbiente() { return ambiente; }
    public boolean esCamaraFria() { return ambiente.equals("FRIO"); }
    public BigDecimal getOcupacion() {
        return lotes.stream().map(Lote::getDisponible).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public void validar(Lote lote) {
        if (lote.getBodega() != null) throw new IllegalArgumentException("El lote ya tiene bodega.");
        if (!ambiente.equals(lote.getAlmacenamiento()))
            throw new IllegalArgumentException("Bodega incompatible: se requiere " + lote.getAlmacenamiento());
        if (getOcupacion().add(lote.getPesoRecibido()).compareTo(capacidad) > 0)
            throw new IllegalArgumentException("Capacidad insuficiente en " + codigo);
    }
    void almacenar(Lote lote) {
        validar(lote);
        lote.asignarBodega(codigo);
        lotes.add(lote);
    }
    @Override public Iterator<Lote> iterator() {
        List<Lote> ordenados = new ArrayList<>(lotes);
        Collections.sort(ordenados);
        Iterator<Lote> lectura = ordenados.iterator();
        return new Iterator<Lote>() {
            private Lote actual;
            private boolean puedeEliminar;
            @Override public boolean hasNext() { return lectura.hasNext(); }
            @Override public Lote next() {
                if (!hasNext()) throw new NoSuchElementException();
                actual = lectura.next(); puedeEliminar = true; return actual;
            }
            @Override public void remove() {
                if (!puedeEliminar) throw new IllegalStateException("Primero invoque next.");
                if (actual.getEstado() == Estado.DISPONIBLE)
                    throw new IllegalStateException("Solo se retiran lotes agotados o descartados.");
                lotes.remove(actual); lectura.remove(); puedeEliminar = false;
            }
        };
    }
    @Override public String toString() {
        return codigo + " | " + ambiente + " | " + getOcupacion().toPlainString()
            + "/" + capacidad.toPlainString() + " kg";
    }
}
