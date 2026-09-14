package model;

//representa el resultado final de una ronda: ganador, fichas ganadas y con que mano gano
/**
 * Encapsula el resultado final de una ronda, incluyendo ganador, fichas obtenidas y combinación ganadora.
 */
public class ResultadoRonda {
    private final Jugador ganador;
    private final int fichasGanadas;
    private final CombinacionPoker combinacionGanadora;

    //se genera el resultado de una ronda
/**
 * Crea una nueva instancia de ResultadoRonda con los datos recibidos.
 * @param ganador valor utilizado por el método para realizar su operación.
 * @param fichasGanadas valor utilizado por el método para realizar su operación.
 * @param combinacionGanadora valor utilizado por el método para realizar su operación.
 */
    public ResultadoRonda(Jugador ganador, int fichasGanadas, CombinacionPoker combinacionGanadora) {
        this.ganador = ganador;
        this.fichasGanadas = fichasGanadas;
        this.combinacionGanadora = combinacionGanadora;
    }

    //obtiene el jugador ganador de la ronda
/**
 * Obtiene el jugador ganador de la ronda.
 * @return resultado de tipo Jugador.
 */
    public Jugador getGanador(){ return ganador; }

    //obtiene las fichas ganadas en la ronda
/**
 * Obtiene la cantidad de fichas ganadas.
 * @return resultado de tipo int.
 */
    public int getFichasGanadas() {return fichasGanadas; }

    //obtiene la combinacion de poker con la que se gano la ronda
/**
 * Obtiene la combinación de póquer ganadora.
 * @return resultado de tipo CombinacionPoker.
 */
    public CombinacionPoker getCombinacionGanadora() {return combinacionGanadora; }

    //para imprimir en pantalla el resultado
/**
 * Devuelve una representación textual legible del objeto.
 * @return valor calculado o recuperado por el método.
 */
    @Override
    public String toString() {
        return ganador.getNombre() + " gana " + fichasGanadas
                + " fichas con " + combinacionGanadora;
    }

}
