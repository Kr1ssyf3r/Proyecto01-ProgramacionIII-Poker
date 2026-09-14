package juego;


import model.AccionPoker;

/**
 * Contrato para las estrategias que determinan la acción de un bot.
 */
public interface EstrategiaApuesta {

    /**
     * Decide la acción que debe realizar el bot según la apuesta y el bote actuales.
     *
     * @param apuestaActual apuesta más alta vigente en la ronda.
     * @param boteActual monto acumulado en el bote.
     * @return acción que debe ejecutar el bot.
     */
    AccionPoker decidirAccion(int apuestaActual, int boteActual);
}
