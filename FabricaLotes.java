import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
public final class FabricaLotes {
    private final Map<String, BiFunction<DatosLote, Entrada, Lote>> creadores = new LinkedHashMap<>();
    public FabricaLotes() {
        registrar("CAFE", (d,e) -> new Cafe(d.codigo(),d.productor(),d.ingreso(),d.peso(),d.calidad(),
            e.decimal("Humedad (%): ")));
        registrar("CAFE_ORGANICO", (d,e) -> new CafeOrganico(d.codigo(),d.productor(),d.ingreso(),d.peso(),d.calidad(),
            e.decimal("Humedad (%): "), e.texto("Certificadora: "), e.fecha("Validez del sello (AAAA-MM-DD): ")));
        registrar("CARDAMOMO", (d,e) -> new Cardamomo(d.codigo(),d.productor(),d.ingreso(),d.peso(),d.calidad(),
            e.decimal("Grano verde (%): ")));
        registrar("HORTALIZA", (d,e) -> new Hortaliza(d.codigo(),d.productor(),d.ingreso(),d.peso(),d.calidad(),
            e.entero("Vida util (3-14 dias): ")));
        registrar("QUESO_FRESCO", (d,e) -> new QuesoFresco(d.codigo(),d.productor(),d.ingreso(),d.peso(),d.calidad(),
            e.decimal("Grasa (%): ")));
    }
    public void registrar(String categoria, BiFunction<DatosLote, Entrada, Lote> creador) {
        String clave = Validacion.codigo(categoria);
        if (creadores.containsKey(clave)) throw new IllegalArgumentException("Categoria repetida.");
        creadores.put(clave, java.util.Objects.requireNonNull(creador));
    }
    public Set<String> getCategorias() { return java.util.Collections.unmodifiableSet(creadores.keySet()); }
    public Lote crear(Entrada e, LocalDate fecha) {
        System.out.println("Categorias: " + String.join(", ", creadores.keySet()));
        String categoria = Validacion.codigo(e.texto("Categoria: "));
        BiFunction<DatosLote, Entrada, Lote> creador = creadores.get(categoria);
        if (creador == null) throw new IllegalArgumentException("Categoria no registrada.");
        DatosLote datos = new DatosLote(e.texto("Codigo: "), e.texto("Productor: "), fecha,
            e.decimal("Peso recibido (kg): "), e.calidad());
        return creador.apply(datos, e);
    }
}
