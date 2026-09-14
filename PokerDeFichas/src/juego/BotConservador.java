package juego;

import model.AccionPoker;

/*
 * El bot intenta evitar riesgos:
 * - Si no hay apuesta, hace CHECK.
 * - Si hay una apuesta pequeña, hace CALL.
 * - Si la apuesta es demasiado alta, hace FOLD.
 */
/**
 * Implementa una estrategia de apuestas conservadora para los jugadores bot.
 */
public class BotConservador implements EstrategiaApuesta {

/**
 * Determina la acción que debe realizar el jugador o bot según la apuesta y el bote actuales.
 * @param apuestaActual valor utilizado por el método para realizar su operación.
 * @param boteActual valor utilizado por el método para realizar su operación.
 * @return valor calculado o recuperado por el método.
 */
    @Override
    public AccionPoker decidirAccion(int apuestaActual, int boteActual) {

        if (apuestaActual == 0) {
            return AccionPoker.CHECK;
        }

        if (apuestaActual <= 20) {
            return AccionPoker.CALL;
        }

        return AccionPoker.FOLD;
    }
}

