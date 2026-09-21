package juego;


import model.AccionPoker;

/** El bot intenta aumentar la apuesta cuando puede.*/
public class BotAgresivo implements EstrategiaApuesta {

/**
 * Decide la acción del bot agresivo: sube si no hay apuesta y, si la hay, la iguala.
 * @param apuestaActual apuesta más alta vigente en la ronda actual.
 * @param boteActual monto acumulado en el bote.
 * @return RAISE si no hay apuesta; CALL en caso contrario.
 */
    @Override
    public AccionPoker decidirAccion(int apuestaActual, int boteActual) {

        if (apuestaActual == 0) {
            return AccionPoker.RAISE;
        }

        return AccionPoker.CALL;
    }
}
