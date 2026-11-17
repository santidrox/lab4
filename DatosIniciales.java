import java.math.BigDecimal;
import java.time.LocalDate;
public final class DatosIniciales {
    private DatosIniciales() { }
    private static BigDecimal n(String x) { return new BigDecimal(x); }
    public static Acopio crear() {
        Acopio a = new Acopio(LocalDate.of(2026, 11, 16));
        a.registrarBodega(new Bodega("SECA-1", n("2000"), "SECO"));
        a.registrarBodega(new Bodega("SECA-2", n("2000"), "SECO"));
        a.registrarBodega(new Bodega("FRIA-1", n("1200"), "FRIO"));
        LocalDate f = a.getFecha();
        a.cargarInicial(new Cafe("CF-02", "Ana Lopez", f.minusDays(4), n("500"), Calidad.A, n("13.4")), "SECA-1");
        a.cargarInicial(new Cafe("CF-01", "Luis Perez", f.minusDays(4), n("100"), Calidad.B, n("12")), "SECA-2");
        a.cargarInicial(new CafeOrganico("CO-01", "Rosa Ruiz", f.minusDays(2), n("500"), Calidad.A,
            n("13.4"), "Certificadora Guatemala", f.plusYears(1)), "SECA-2");
        a.cargarInicial(new CafeOrganico("CO-02", "Mario Paz", f.minusDays(1), n("70"), Calidad.C,
            n("10"), "Certificadora Guatemala", f.plusMonths(1)), "SECA-1");
        a.cargarInicial(new Cardamomo("CA-01", "Elena Gil", f.minusDays(179), n("80"), Calidad.B, n("90")), "SECA-1");
        a.cargarInicial(new Cardamomo("CA-02", "Pedro Leon", f.minusDays(10), n("60"), Calidad.C, n("84")), "SECA-2");
        a.cargarInicial(new Hortaliza("HO-01", "Sara Diaz", f.minusDays(6), n("200"), Calidad.A, 7), "FRIA-1");
        a.cargarInicial(new Hortaliza("HO-02", "Juan Sol", f.minusDays(6), n("120"), Calidad.B, 7), "FRIA-1");
        a.cargarInicial(new QuesoFresco("QU-01", "Lucia Mar", f.minusDays(20), n("40"), Calidad.A, n("25")), "FRIA-1");
        a.cargarInicial(new QuesoFresco("QU-02", "David Rey", f.minusDays(3), n("30"), Calidad.B, n("15")), "FRIA-1");
        return a;
    }
}
