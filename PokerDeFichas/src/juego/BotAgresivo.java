package juego;


import model.AccionPoker;

/** El bot intenta aumentar la apuesta cuando puede.*/
public class BotAgresivo implements EstrategiaApuesta {

/**
 * Determina la acción que debe realizar el jugador o bot según la apuesta y el bote actuales.
 * @param apuestaActual valor utilizado por el método para realizar su operación.
 * @param boteActual valor utilizado por el método para realizar su operación.
 * @return valor calculado o recuperado por el método.
 */
    @Override
    public AccionPoker decidirAccion(int apuestaActual, int boteActual) {

        if (apuestaActual == 0) {
            return AccionPoker.RAISE;
        }

        return AccionPoker.CALL;
    }
}
