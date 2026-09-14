package util;

/**
 * Métodos de validación reutilizables para las apuestas del juego.
 * Todos los métodos son estáticos porque no dependen de un estado propio:
 * solo revisan los datos que reciben y lanzan una excepción si algo
 * no cumple las reglas.
 *
 * Uso típico desde PanelControles o desde la lógica del juego:
 *
 *   try {
 *       Validaciones.validarApuesta(monto, saldoDisponible);
 *       // si no se lanzó excepción, la apuesta es válida
 *   } catch (ApuestaInvalidaException | SaldoInsuficienteException e) {
 *       mostrarError(e.getMessage());
 *   }
 */
public class Validaciones {

    // Evita que alguien intente instanciar esta clase (solo tiene métodos estáticos)
/**
 * Crea una nueva instancia de Validaciones con los datos recibidos.
 */
    private Validaciones() {
    }

/**
 * Valida que el monto recibido sea mayor que cero.
 * @param monto valor utilizado por el método para realizar su operación.
 * @throws ApuestaInvalidaException si los datos recibidos no cumplen las reglas requeridas.
 */
    public static void validarMontoPositivo(int monto) throws ApuestaInvalidaException {
        if (monto <= 0) {
            throw new ApuestaInvalidaException(
                    "El monto de la apuesta debe ser mayor a cero (recibido: " + monto + ")"
            );
        }
    }

/**
 * Valida que el saldo disponible sea suficiente para cubrir el monto indicado.
 * @param monto valor utilizado por el método para realizar su operación.
 * @param saldoDisponible valor utilizado por el método para realizar su operación.
 * @throws SaldoInsuficienteException si los datos recibidos no cumplen las reglas requeridas.
 */
    public static void validarSaldoSuficiente(int monto, int saldoDisponible) throws SaldoInsuficienteException {
        if (monto > saldoDisponible) {
            throw new SaldoInsuficienteException(
                    "Saldo insuficiente: se intentó apostar " + monto
                            + " pero el jugador solo tiene " + saldoDisponible
            );
        }
    }

/**
 * Valida conjuntamente que la apuesta sea positiva y que el jugador tenga saldo suficiente.
 * @param monto valor utilizado por el método para realizar su operación.
 * @param saldoDisponible valor utilizado por el método para realizar su operación.
 */
    public static void validarApuesta(int monto, int saldoDisponible)
            throws ApuestaInvalidaException, SaldoInsuficienteException {
        validarMontoPositivo(monto);
        validarSaldoSuficiente(monto, saldoDisponible);
    }

/**
 * Valida que la nueva apuesta sea válida y superior a la apuesta actual.
 * @param nuevoMonto valor utilizado por el método para realizar su operación.
 * @param apuestaActual valor utilizado por el método para realizar su operación.
 * @param saldoDisponible valor utilizado por el método para realizar su operación.
 */
    public static void validarSubida(int nuevoMonto, int apuestaActual, int saldoDisponible)
            throws ApuestaInvalidaException, SaldoInsuficienteException {
        validarApuesta(nuevoMonto, saldoDisponible);
        if (nuevoMonto <= apuestaActual) {
            throw new ApuestaInvalidaException(
                    "Para subir, el monto (" + nuevoMonto
                            + ") debe ser mayor a la apuesta actual (" + apuestaActual + ")"
            );
        }
    }
}
