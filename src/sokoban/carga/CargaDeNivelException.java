package sokoban.carga;

/** Error al obtener un nivel: no existe, no se puede leer o su contenido es inválido. */
public class CargaDeNivelException extends Exception {

    public CargaDeNivelException(String mensaje) {
        super(mensaje);
    }

    public CargaDeNivelException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
