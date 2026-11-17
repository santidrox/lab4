import java.math.BigDecimal;
import java.time.LocalDate;
public record DatosLote(String codigo, String productor, LocalDate ingreso,
                        BigDecimal peso, Calidad calidad) { }
