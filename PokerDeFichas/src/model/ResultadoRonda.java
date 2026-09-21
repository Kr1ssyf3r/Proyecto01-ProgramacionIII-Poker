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
 * @param ganador jugador que ganó la ronda.
 * @param fichasGanadas fichas que se lleva el ganador.
 * @param combinacionGanadora combinación con la que ganó la ronda.
 */
    public ResultadoRonda(Jugador ganador, int fichasGanadas, CombinacionPoker combinacionGanadora) {
        this.ganador = ganador;
        this.fichasGanadas = fichasGanadas;
        this.combinacionGanadora = combinacionGanadora;
    }

    //obtiene el jugador ganador de la ronda
/**
 * Obtiene el jugador ganador de la ronda.
 * @return jugador que ganó la ronda.
 */
    public Jugador getGanador(){ return ganador; }

    //obtiene las fichas ganadas en la ronda
/**
 * Obtiene la cantidad de fichas ganadas.
 * @return fichas que ganó el jugador.
 */
    public int getFichasGanadas() {return fichasGanadas; }

    //obtiene la combinacion de poker con la que se gano la ronda
/**
 * Obtiene la combinación de póquer ganadora.
 * @return combinación con la que se ganó la ronda.
 */
    public CombinacionPoker getCombinacionGanadora() {return combinacionGanadora; }

    //para imprimir en pantalla el resultado
/**
 * Devuelve una representación textual legible del objeto.
 * @return texto como "Ana gana 60 fichas con PAR".
 */
    @Override
    public String toString() {
        return ganador.getNombre() + " gana " + fichasGanadas
                + " fichas con " + combinacionGanadora;
    }

}
