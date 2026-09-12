package com.una.pokerdefichas.modelo;

//representa el resultado final de una ronda: ganador, fichas ganadas y con que mano gano
public class ResultadoRonda {
    private final Jugador ganador;
    private final int fichasGanadas;
    private final CombinacionPoker combinacionGanadora;

    //se genera el resultado de una ronda
    public ResultadoRonda(Jugador ganador, int fichasGanadas, CombinacionPoker combinacionGanadora) {
        this.ganador = ganador;
        this.fichasGanadas = fichasGanadas;
        this.combinacionGanadora = combinacionGanadora;
    }

    //obtiene el jugador ganador de la ronda
    public Jugador getGanador(){ return ganador; }

    //obtiene las fichas ganadas en la ronda
    public int getFichasGanadas() {return fichasGanadas; }

    //obtiene la combinacion de poker con la que se gano la ronda
    public CombinacionPoker getCombinacionGanadora() {return combinacionGanadora; }

    //para imprimir en pantalla el resultado
    @Override
    public String toString() {
        return ganador.getNombre() + " gana " + fichasGanadas
                + " fichas con " + combinacionGanadora;
    }

}
