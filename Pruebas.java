import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Pruebas {
    private static int total;
    private static final LocalDate F = LocalDate.of(2026, 3, 5);
    private static BigDecimal n(String x) { return new BigDecimal(x); }
    private static Cafe cafe(String codigo, Calidad q, String humedad) {
        return new Cafe(codigo, "Productor", F, n("500"), q, n(humedad));
    }
    private static void caso(String entrada, String esperado, String obtenido) {
        total++;
        if (!esperado.equals(obtenido)) throw new AssertionError("P" + total + ": " + entrada
            + " esperado=" + esperado + " obtenido=" + obtenido);
        System.out.println("P" + String.format("%02d", total) + "\t" + entrada + "\t" + esperado + "\t" + obtenido);
    }
    private static void pago(String entrada, Lote l, String esperado) {
        caso(entrada, esperado, l.calcularPago().toPlainString());
    }
    private static String foto(Acopio a) {
        return a.getFecha() + "|" + a.getHistorial() + "|" + a.getBodegas()
            + "|" + a.getFinanzas() + "|despachos=" + a.getDespachos().size();
    }
    private static void rechazo(Acopio a, String entrada, Runnable operacion) {
        String antes = foto(a);
        boolean rechazado = false;
        java.io.PrintStream salida = System.out;
        try {
            System.setOut(new java.io.PrintStream(new java.io.ByteArrayOutputStream()));
            operacion.run();
        } catch (IllegalArgumentException e) { rechazado = true; }
        finally { System.setOut(salida); }
        caso(entrada, "Rechazo; estado identico", rechazado && antes.equals(foto(a))
            ? "Rechazo; estado identico" : "ERROR");
    }
    private static Acopio vacio() {
        Acopio a = new Acopio(F);
        a.registrarBodega(new Bodega("S1", n("1000"), "SECO"));
        a.registrarBodega(new Bodega("S2", n("1000"), "SECO"));
        a.registrarBodega(new Bodega("F1", n("1000"), "FRIO"));
        return a;
    }
    private static String codigos(Iterable<Lote> lotes) {
        List<String> codigos = new ArrayList<>();
        for (Lote l : lotes) codigos.add(l.getCodigo());
        return String.join(",", codigos);
    }
    public static void main(String[] args) {
        System.out.println("ID\tEntrada\tEsperado\tObtenido");
        pago("Cafe 500 kg A humedad 13.4", cafe("A", Calidad.A, "13.4"), "5640.00");
        pago("Cafe 500 kg A humedad 12.01", cafe("A", Calidad.A, "12.01"), "5820.00");
        pago("Cafe 500 kg A humedad 13 exacto", cafe("A", Calidad.A, "13"), "5820.00");
        pago("Cafe 500 kg A humedad 14", cafe("A", Calidad.A, "14"), "5640.00");
        pago("Cafe 500 kg A humedad 8", cafe("A", Calidad.A, "8"), "6000.00");
        pago("Cafe 500 kg B humedad 12", cafe("A", Calidad.B, "12"), "4500.00");
        pago("Cafe 500 kg C humedad 12", cafe("A", Calidad.C, "12"), "3000.00");
        pago("Organico 500 kg A 13.4; sello vigente", new CafeOrganico("O", "P", F, n("500"),
            Calidad.A, n("13.4"), "Cert", F), "6015.00");
        pago("Cardamomo 80 kg B 90% verde", new Cardamomo("C", "P", F, n("80"), Calidad.B, n("90")), "4224.00");
        pago("Cardamomo 80 kg A 85% verde", new Cardamomo("C", "P", F, n("80"), Calidad.A, n("85")), "5280.00");
        pago("Cardamomo 80 kg C 84.99% verde", new Cardamomo("C", "P", F, n("80"), Calidad.C, n("84.99")), "2800.00");
        for (Calidad q : Calidad.values()) {
            pago("Hortaliza 200 kg " + q + " merma 5%", new Hortaliza("H", "P", F, n("200"), q, 7),
                new String[]{"760.00","570.00","380.00"}[q.ordinal()]);
        }
        pago("Queso 40 kg A grasa 10; empaque", new QuesoFresco("Q", "P", F, n("40"), Calidad.A, n("10")), "1460.00");
        pago("Queso 40 kg B grasa 40; empaque", new QuesoFresco("Q", "P", F, n("40"), Calidad.B, n("40")), "1140.00");
        Acopio a = vacio();
        Lote h = new Hortaliza("H", "P", F, n("200"), Calidad.A, 7);
        String antes = foto(a);
        BigDecimal p = a.preliquidar(h);
        caso("Preliquidar H sin recibir", "760.00; estado identico", p.toPlainString() + "; "
            + (antes.equals(foto(a)) ? "estado identico" : "ERROR"));
        a.recibir(h,"F1");
        caso("Recibir H 200 kg A", "760.00;200", a.getFinanzas().get("HORTALIZA").getPagos().toPlainString()
            + ";" + a.buscarBodega("F1").getOcupacion().toPlainString());
        caso("Vencimiento H ingreso 2026-03-05 + 7", "2026-03-12", h.getVencimiento().orElseThrow().toString());
        caso("Vencimiento cardamomo +180; queso +21; cafe", "2026-09-01;2026-03-26;false",
            new Cardamomo("C","P",F,n("1"),Calidad.A,n("0")).getVencimiento().orElseThrow()
            + ";" + new QuesoFresco("Q","P",F,n("1"),Calidad.A,n("20")).getVencimiento().orElseThrow()
            + ";" + cafe("C",Calidad.A,"10").getVencimiento().isPresent());
        a.despachar("Cliente", "HORTALIZA", n("150"));
        caso("Despachar 150 kg H; cobrar costo x1.35", "769.50;50", a.getFinanzas().get("HORTALIZA")
            .getCobros().toPlainString() + ";" + h.getDisponible().toPlainString());
        a.avanzarFecha(F.plusDays(7));
        caso("Mismo dia de vencimiento", "DISPONIBLE;50", h.getEstado() + ";" + h.getDisponible());
        caso("Reporte incluye lote que vence hoy", "H", codigos(a.proximosAVencer()));
        a.avanzarFecha(F.plusDays(8));
        caso("Dia siguiente; descartar 50 kg a Q3.80", "DESCARTADO;0;190.00;0", h.getEstado() + ";"
            + h.getDisponible() + ";" + a.getFinanzas().get("HORTALIZA").getPerdidas().toPlainString()
            + ";" + a.buscarBodega("F1").getOcupacion());
        a.avanzarFecha(F.plusDays(9));
        caso("No repetir perdida por lote ya descartado", "190.00", a.getFinanzas().get("HORTALIZA").getPerdidas().toPlainString());
        Acopio b = vacio();
        rechazo(b, "Cafe humedad 7.99", () -> cafe("X", Calidad.A, "7.99"));
        rechazo(b, "Cafe humedad 14.01", () -> cafe("X", Calidad.A, "14.01"));
        rechazo(b, "Organico sello un dia antes del ingreso", () -> new CafeOrganico("O","P",F,n("1"),Calidad.A,n("12"),"C",F.minusDays(1)));
        rechazo(b, "Queso calidad C", () -> new QuesoFresco("Q","P",F,n("1"),Calidad.C,n("20")));
        rechazo(b, "Peso cero", () -> new Cafe("X","P",F,n("0"),Calidad.A,n("12")));
        rechazo(b, "Peso negativo", () -> new Cafe("X","P",F,n("-1"),Calidad.A,n("12")));
        rechazo(b, "Codigo vacio", () -> cafe(" ",Calidad.A,"12"));
        rechazo(b, "Productor vacio", () -> new Cafe("X"," ",F,n("1"),Calidad.A,n("12")));
        rechazo(b, "Certificadora vacia", () -> new CafeOrganico("O","P",F,n("1"),Calidad.A,n("12")," ",F));
        rechazo(b, "Grano verde -0.1", () -> new Cardamomo("C","P",F,n("1"),Calidad.A,n("-0.1")));
        rechazo(b, "Grano verde 100.1", () -> new Cardamomo("C","P",F,n("1"),Calidad.A,n("100.1")));
        rechazo(b, "Vida util 2 dias", () -> new Hortaliza("H","P",F,n("1"),Calidad.A,2));
        rechazo(b, "Vida util 15 dias", () -> new Hortaliza("H","P",F,n("1"),Calidad.A,15));
        rechazo(b, "Grasa 9.9", () -> new QuesoFresco("Q","P",F,n("1"),Calidad.A,n("9.9")));
        rechazo(b, "Grasa 40.1", () -> new QuesoFresco("Q","P",F,n("1"),Calidad.A,n("40.1")));
        rechazo(b, "Capacidad cero", () -> b.registrarBodega(new Bodega("S3",n("0"),"SECO")));
        rechazo(b, "Bodega S1 repetida", () -> b.registrarBodega(new Bodega("s1",n("50"),"SECO")));
        rechazo(b, "Cafe en camara fria", () -> b.recibir(cafe("CF",Calidad.A,"12"),"F1"));
        rechazo(b, "Hortaliza en bodega seca", () -> b.recibir(new Hortaliza("H","P",F,n("1"),Calidad.A,3),"S1"));
        rechazo(b, "Queso en bodega seca", () -> b.recibir(new QuesoFresco("Q","P",F,n("1"),Calidad.A,n("20")),"S1"));
        rechazo(b, "Lote 1001 kg en bodega 1000", () -> b.recibir(new Cafe("CF","P",F,n("1001"),Calidad.A,n("12")),"S1"));
        rechazo(b, "Bodega inexistente", () -> b.recibir(cafe("CF",Calidad.A,"12"),"NO"));
        b.recibir(cafe("CF",Calidad.A,"12"),"S1");
        rechazo(b, "Codigo de lote repetido", () -> b.recibir(cafe("cf",Calidad.A,"12"),"S2"));
        rechazo(b, "Preliquidacion de codigo repetido", () -> b.preliquidar(cafe("cf",Calidad.A,"12")));
        rechazo(b, "Intentar mover lote a otra bodega", () -> b.recibir(b.getExistencias().get(0),"S2"));
        rechazo(b, "Ingreso distinto de fecha operacion", () -> b.recibir(new Cafe("X","P",F.minusDays(1),n("1"),Calidad.A,n("12")),"S1"));
        rechazo(b, "Pedido 501 kg con 500 disponibles", () -> b.despachar("C","CAFE",n("501")));
        rechazo(b, "Pedido categoria sin existencias", () -> b.despachar("C","MIEL",n("1")));
        rechazo(b, "Pedido cero", () -> b.despachar("C","CAFE",n("0")));
        rechazo(b, "Comprador vacio", () -> b.despachar(" ","CAFE",n("1")));
        rechazo(b, "Retroceder fecha", () -> b.avanzarFecha(F.minusDays(1)));
        rechazo(b, "Avanzar cero dias", () -> b.avanzarFecha(F));
        Acopio c = vacio();
        c.cargarInicial(new Cafe("0B","P",F.minusDays(1),n("10"),Calidad.B,n("12")),"S1");
        c.cargarInicial(new Cafe("A","P",F.minusDays(1),n("10"),Calidad.A,n("12")),"S2");
        c.cargarInicial(new Cafe("AA","P",F.minusDays(1),n("10"),Calidad.A,n("12")),"S1");
        c.cargarInicial(new Cafe("ANT","P",F.minusDays(2),n("10"),Calidad.C,n("12")),"S2");
        c.cargarInicial(new Cardamomo("CAR","P",F.minusDays(179),n("2"),Calidad.C,n("100")),"S1");
        c.cargarInicial(new Hortaliza("HOR","P",F,n("2"),Calidad.A,3),"F1");
        caso("Orden global: perecedero, fecha, calidad, codigo", "CAR,HOR,ANT,A,AA,0B", codigos(c.getExistencias()));
        caso("Iteracion de S1 ordenada", "CAR,AA,0B", codigos(c.buscarBodega("S1")));
        Despacho d = c.despachar("Cliente","CAFE",n("25"));
        caso("Despacho multibodega 25 kg toma ANT10,A10,AA5", "ANT:10,A:10,AA:5;324.00",
            String.join(",", d.getLineas().stream().map(l -> l.getLote().getCodigo()+":"+l.getKg()).toList())
            + ";" + d.getTotal());
        caso("Agotados salen y liberan capacidad", "0;CAR,AA,0B;5",
            c.buscarBodega("S2").getOcupacion()+";"+codigos(c.buscarBodega("S1"))+";"
            + c.getExistencias().stream().filter(l -> l.getCodigo().equals("AA")).findFirst().orElseThrow().getDisponible());
        rechazo(c,"Codigo agotado no puede reutilizarse", () -> c.recibir(cafe("ANT",Calidad.A,"12"),"S1"));
        caso("Proximos 3 dias incluye limites 1 y 3", "CAR,HOR",codigos(c.proximosAVencer()));
        c.avanzarFecha(F.plusDays(4));
        caso("Descarte durante iteracion retira multiples lotes", "AA,0B;77.00;7.60", codigos(c.getExistencias())+";"
            +c.getFinanzas().get("CARDAMOMO").getPerdidas()+";"+c.getFinanzas().get("HORTALIZA").getPerdidas());
        Acopio inicial = DatosIniciales.crear();
        boolean dos = List.of("CAFE","CAFE_ORGANICO","CARDAMOMO","HORTALIZA","QUESO_FRESCO").stream()
            .allMatch(cat -> inicial.getExistencias().stream().filter(l->l.getCategoria().equals(cat)).count() >= 2);
        caso("Carga inicial 10 lotes, 3 bodegas y dos por categoria", "10;3;true", inicial.getExistencias().size()+";"+inicial.getBodegas().size()+";"+dos);
        caso("Carga inicial sin pagos, cobros ni perdidas", "0;0;0",
            inicial.getFinanzas().values().stream().map(ResumenFinanciero::getPagos).reduce(BigDecimal.ZERO,BigDecimal::add)
            +";"+inicial.getFinanzas().values().stream().map(ResumenFinanciero::getCobros).reduce(BigDecimal.ZERO,BigDecimal::add)
            +";"+inicial.getFinanzas().values().stream().map(ResumenFinanciero::getPerdidas).reduce(BigDecimal.ZERO,BigDecimal::add));
        Acopio decimal = vacio();
        decimal.registrarBodega(new Bodega("EXACTA",n("0.3"),"SECO"));
        decimal.recibir(new Cafe("D1","P",F,n("0.1"),Calidad.A,n("12")),"EXACTA");
        decimal.recibir(new Cafe("D2","P",F,n("0.2"),Calidad.A,n("12")),"EXACTA");
        caso("Capacidad decimal exacta 0.1+0.2=0.3", "0.3",decimal.buscarBodega("EXACTA").getOcupacion().toPlainString());
        rechazo(decimal,"Exceso decimal de capacidad", ()->decimal.recibir(new Cafe("D3","P",F,n("0.001"),Calidad.A,n("12")),"EXACTA"));
        caso("Dos cafes empatados por codigo", "D1,D2",codigos(decimal.getExistencias()));
        Iterator<Lote> it = decimal.buscarBodega("EXACTA").iterator();
        it.next();
        boolean bloqueado = false;
        try { it.remove(); } catch (IllegalStateException e) { bloqueado = true; }
        caso("Iterador impide retirar un lote disponible", "true;0.3",bloqueado+";"+decimal.buscarBodega("EXACTA").getOcupacion());

        Acopio e = vacio();
        e.recibir(cafe("E",Calidad.A,"13.4"),"S1");
        caso("Ejemplo cafe: despacho 100 kg costo 11.28", "1522.80",
            e.despachar("Comprador","CAFE",n("100")).getTotal().toPlainString());
        rechazo(e,"Cardamomo en camara fria",()->e.recibir(new Cardamomo("C","P",F,n("1"),Calidad.A,n("0")),"F1"));
        rechazo(e,"Numero mal escrito",()->new Entrada(new java.util.Scanner("abc\n")).decimal(""));
        rechazo(e,"Numero no finito",()->new Entrada(new java.util.Scanner("NaN\n")).decimal(""));
        rechazo(e,"Entero mal escrito",()->new Entrada(new java.util.Scanner("3.5\n")).entero(""));
        rechazo(e,"Fecha imposible",()->new Entrada(new java.util.Scanner("2026-02-30\n")).fecha(""));
        rechazo(e,"Calidad D",()->new Entrada(new java.util.Scanner("D\n")).calidad());
        Acopio empates = vacio();
        empates.cargarInicial(new Hortaliza("H0","P",F.minusDays(1),n("1"),Calidad.B,4),"F1");
        empates.cargarInicial(new Hortaliza("HB","P",F,n("1"),Calidad.A,3),"F1");
        empates.cargarInicial(new Hortaliza("HA","P",F.minusDays(2),n("1"),Calidad.A,5),"F1");
        caso("Perecederos misma fecha: calidad y codigo", "HA,HB,H0",codigos(empates.getExistencias()));
        empates.avanzarFecha(F.plusDays(4));
        caso("Iterador descarta tres lotes consecutivos", "0;10.45",empates.getExistencias().size()+";"
            +empates.getFinanzas().get("HORTALIZA").getPerdidas());
        pago("Organico 0.02 kg A 13.4: redondeo unico", new CafeOrganico("DEC","P",F,n("0.02"),
            Calidad.A,n("13.4"),"Cert",F),"0.24");
        System.out.println("TOTAL\t"+total+"\tAPROBADAS\t"+total);
    }
}
