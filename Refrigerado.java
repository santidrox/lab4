public interface Refrigerado extends Producto {
    @Override
    default String getAlmacenamiento() { return "FRIO"; }
}
