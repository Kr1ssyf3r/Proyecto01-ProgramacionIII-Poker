package model;

/**Representa a un jugador controlado por una persona
 * mediante la interfaz gráfica*/

public class JugadorHumano extends Jugador {

    /**Crea un jugador humano.*/

    public JugadorHumano(String nombre, int saldoFichasIniciales) {
        super(nombre, saldoFichasIniciales);
    }

    /**La decisión del jugador humano no se realiza automáticamente.
     * La interfaz gráfica será la encargada de obtener la acción
     * seleccionada por el usuario y enviarla al juego.
     */
    @Override
    public AccionPoker decidirAccion(int apuestaActual, int boteActual) {
        throw new UnsupportedOperationException(
                "La acción del jugador humano debe seleccionarse desde la interfaz"
        );
    }
}
