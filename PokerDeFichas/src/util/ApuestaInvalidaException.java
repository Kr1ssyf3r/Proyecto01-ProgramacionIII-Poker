package util;

/**
 * Se lanza cuando el monto de una apuesta no cumple las reglas básicas
 * del juego: por ejemplo, es negativo, es cero cuando se requiere un
 * monto positivo, o no respeta el mínimo/múltiplo exigido por la mesa.
 *
 * Es una excepción verificada (checked) para forzar su manejo explícito
 * antes de que la apuesta se procese en la lógica del juego.
 */
public class ApuestaInvalidaException extends Exception {

/**
 * Crea una nueva instancia de ApuestaInvalidaException con los datos recibidos.
 * @param mensaje valor utilizado por el método para realizar su operación.
 */
    public ApuestaInvalidaException(String mensaje) {
        super(mensaje);
    }

/**
 * Crea una nueva instancia de ApuestaInvalidaException con los datos recibidos.
 * @param mensaje valor utilizado por el método para realizar su operación.
 * @param causa valor utilizado por el método para realizar su operación.
 */
    public ApuestaInvalidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
