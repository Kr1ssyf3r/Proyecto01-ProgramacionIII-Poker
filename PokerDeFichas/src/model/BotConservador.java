package model;

/*
 * El bot intenta evitar riesgos:
 * - Si no hay apuesta, hace CHECK.
 * - Si hay una apuesta pequeña, hace CALL.
 * - Si la apuesta es demasiado alta, hace FOLD.
 */
public class BotConservador implements EstrategiaApuesta {

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
