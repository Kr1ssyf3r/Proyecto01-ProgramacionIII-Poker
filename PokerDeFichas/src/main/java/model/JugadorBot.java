package model;

import juego.EstrategiaApuesta;

/**Representa a un jugador controlado automáticamente por el programa.
  Utiliza una estrategia de apuesta para decidir sus acciones.*/

public class JugadorBot extends Jugador {

    private EstrategiaApuesta estrategia;

    /**
     * Construye un jugador bot con un nombre, un saldo inicial y una estrategia de apuestas.
     * @param nombre nombre que identificará al jugador bot en la partida.
     * @param saldoFichasIniciales cantidad inicial de fichas del jugador.
     * @param estrategia estrategia que utilizará el bot para decidir sus acciones.
     * @throws IllegalArgumentException si la estrategia recibida es null.
     */
    public JugadorBot(String nombre, int saldoFichasIniciales,
                      EstrategiaApuesta estrategia) {

        super(nombre, saldoFichasIniciales);

        if (estrategia == null) {
            throw new IllegalArgumentException(
                    "La estrategia del bot no puede ser null"
            );
        }

        this.estrategia = estrategia;
    }

/**
 * Cambia la estrategia de apuestas utilizada por el bot.
 * @param estrategia estrategia de apuestas del bot (no puede ser null).
 */

    public void setEstrategia(EstrategiaApuesta estrategia) {

        if (estrategia == null) {
            throw new IllegalArgumentException(
                    "La estrategia del bot no puede ser null"
            );
        }

        this.estrategia = estrategia;
    }

/**
 * Obtiene la estrategia de apuestas configurada para el bot.
 * @return estrategia de apuestas actual del bot.
 */

    public EstrategiaApuesta getEstrategia() {
        return estrategia;
    }

    /**Decide qué acción realizar durante su turno.*/

/**
 * Delega en la estrategia del bot la decisión de la acción a realizar.
 * @param apuestaActual apuesta más alta vigente en la ronda actual.
 * @param boteActual monto acumulado en el bote.
 * @return acción que indica la estrategia del bot: CHECK, CALL, FOLD o RAISE.
 */
    @Override
    public AccionPoker decidirAccion(int apuestaActual, int boteActual) {
        return estrategia.decidirAccion(apuestaActual, boteActual);
    }
}
