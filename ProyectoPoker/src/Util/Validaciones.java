package Util;

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
    private Validaciones() {
    }

    /**
     * Valida que el monto de una apuesta sea mayor a cero.
     *
     * @param monto cantidad de fichas que el jugador quiere apostar
     * @throws ApuestaInvalidaException si el monto es cero o negativo
     */
    public static void validarMontoPositivo(int monto) throws ApuestaInvalidaException {
        if (monto <= 0) {
            throw new ApuestaInvalidaException(
                    "El monto de la apuesta debe ser mayor a cero (recibido: " + monto + ")"
            );
        }
    }

    /**
     * Valida que el jugador tenga fichas suficientes para cubrir el monto.
     *
     * @param monto           cantidad de fichas que el jugador quiere apostar
     * @param saldoDisponible fichas actuales del jugador
     * @throws SaldoInsuficienteException si el saldo no alcanza para cubrir el monto
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
     * Valida una apuesta completa: monto positivo y saldo suficiente.
     * Es el método que normalmente se llama desde la GUI antes de
     * enviar la acción a la lógica del juego.
     *
     * @param monto           cantidad de fichas que el jugador quiere apostar
     * @param saldoDisponible fichas actuales del jugador
     * @throws ApuestaInvalidaException   si el monto no es válido
     * @throws SaldoInsuficienteException si el saldo no alcanza
     */
    public static void validarApuesta(int monto, int saldoDisponible)
            throws ApuestaInvalidaException, SaldoInsuficienteException {
        validarMontoPositivo(monto);
        validarSaldoSuficiente(monto, saldoDisponible);
    }

    /**
     * Valida que una subida ("subir") sea estrictamente mayor a la apuesta actual de la mesa,
     * además de cumplir las validaciones normales de monto y saldo.
     *
     * @param nuevoMonto      monto que el jugador propone para subir
     * @param apuestaActual   apuesta más alta vigente en la ronda
     * @param saldoDisponible fichas actuales del jugador
     * @throws ApuestaInvalidaException   si el nuevo monto no supera la apuesta actual, o no es válido
     * @throws SaldoInsuficienteException si el saldo no alcanza
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