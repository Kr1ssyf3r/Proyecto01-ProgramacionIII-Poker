package util;


/**
 * Se lanza cuando un jugador intenta apostar, igualar o subir
 * una cantidad de fichas mayor a la que tiene disponible en su saldo.
 *
 * Es una excepción verificada (checked) a propósito: obliga a manejar
 * el caso en la GUI (por ejemplo, mostrando un mensaje de error al
 * usuario) en lugar de dejar que el programa se caiga.
 */
public class SaldoInsuficienteException extends Exception {

/**
 * Crea una nueva instancia de SaldoInsuficienteException con los datos recibidos.
 * @param mensaje mensaje que describe el error.
 */
    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }

/**
 * Crea una nueva instancia de SaldoInsuficienteException con los datos recibidos.
 * @param mensaje mensaje que describe el error.
 * @param causa excepción original que provocó este error.
 */
    public SaldoInsuficienteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
