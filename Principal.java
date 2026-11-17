import java.util.NoSuchElementException;
import java.util.Scanner;
public final class Principal {
    private Principal() { }
    public static void main(String[] args) {
        Acopio acopio = DatosIniciales.crear();
        FabricaLotes fabrica = new FabricaLotes();
        Entrada entrada = new Entrada(new Scanner(System.in));
        boolean continuar = true;
        while (continuar) {
            System.out.println("\nAcopioGT | Fecha: " + acopio.getFecha());
            System.out.println("1. Pre-liquidar lote\n2. Recibir lote\n3. Registrar bodega"
                + "\n4. Registrar pedido y despachar\n5. Avanzar fecha\n6. Existencias por categoria"
                + "\n7. Contenido de bodegas\n8. Vencen en los proximos 3 dias"
                + "\n9. Reporte financiero\n10. Historial de lotes\n0. Salir");
            try {
                int opcion = entrada.entero("Opcion: ");
                switch (opcion) {
                    case 0 -> continuar = false;
                    case 1, 2 -> {
                        Lote lote = fabrica.crear(entrada, acopio.getFecha());
                        System.out.println("Pago: " + Validacion.monto(acopio.preliquidar(lote)));
                        System.out.println("Vencimiento: " + lote.getVencimiento()
                            .map(Object::toString).orElse("No vence"));
                        if (opcion == 2 && entrada.texto("Aceptar recepcion (S/N): ").equalsIgnoreCase("S")) {
                            acopio.recibir(lote, entrada.texto("Codigo de bodega: "));
                            System.out.println("Lote recibido y pago registrado.");
                        }
                    }
                    case 3 -> {
                        String codigo = entrada.texto("Codigo de bodega: ");
                        java.math.BigDecimal capacidad = entrada.decimal("Capacidad en kg: ");
                        String ambiente = Validacion.codigo(entrada.texto("Ambiente (SECO/FRIO): "));
                        if (!ambiente.equals("SECO") && !ambiente.equals("FRIO"))
                            throw new IllegalArgumentException("Use SECO o FRIO.");
                        acopio.registrarBodega(new Bodega(codigo, capacidad, ambiente));
                        System.out.println("Bodega registrada.");
                    }
                    case 4 -> {
                        String comprador = entrada.texto("Comprador: ");
                        System.out.println("Categorias: " + fabrica.getCategorias());
                        String categoria = entrada.texto("Categoria: ");
                        Reportes.despacho(acopio.despachar(comprador, categoria, entrada.decimal("Kilogramos: ")));
                    }
                    case 5 -> {
                        acopio.avanzarFecha(entrada.fecha("Nueva fecha (AAAA-MM-DD): "));
                        System.out.println("Fecha actualizada y vencidos descartados.");
                    }
                    case 6 -> Reportes.existencias(acopio);
                    case 7 -> Reportes.bodegas(acopio);
                    case 8 -> Reportes.lotes(acopio.proximosAVencer());
                    case 9 -> Reportes.finanzas(acopio);
                    case 10 -> Reportes.lotes(acopio.getHistorial());
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Operacion rechazada: " + e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("Fin de entrada. Programa terminado.");
                continuar = false;
            }
        }
    }
}
