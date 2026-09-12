package model;


 /** El bot intenta aumentar la apuesta cuando puede.*/
public class BotAgresivo implements EstrategiaApuesta {

    @Override
    public AccionPoker decidirAccion(int apuestaActual, int boteActual) {

        if (apuestaActual == 0) {
            return AccionPoker.RAISE;
        }

        return AccionPoker.CALL;
    }
}
