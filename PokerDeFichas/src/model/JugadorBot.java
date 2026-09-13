zpackage model;

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
 * @param estrategia valor utilizado por el método para realizar su operación.
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
 * @return valor calculado o recuperado por el método.
 */

    public EstrategiaApuesta getEstrategia() {
        return estrategia;
    }

    /**Decide qué acción realizar durante su turno.*/

/**
 * Determina la acción que debe realizar el jugador o bot según la apuesta y el bote actuales.
 * @param apuestaActual valor utilizado por el método para realizar su operación.
 * @param boteActual valor utilizado por el método para realizar su operación.
 * @return valor calculado o recuperado por el método.
 */
    @Override
    public AccionPoker decidirAccion(int apuestaActual, int boteActual) {
        return estrategia.decidirAccion(apuestaActual, boteActual);
    }
}
