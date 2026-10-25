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
 * Decide la acción del bot conservador: pasa si no hay apuesta, iguala apuestas de 20 o menos y se retira ante apuestas mayores.
 * @param apuestaActual apuesta más alta vigente en la ronda actual.
 * @param boteActual monto acumulado en el bote.
 * @return CHECK sin apuesta, CALL si la apuesta es de 20 o menos, FOLD si es mayor.
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

