package model;
/*
    Representa los palos posibles de la baraja
    Cada valor incluye su símbolo asociado para facilitar la representación visual en la GUI
 */
public enum Palo {
    //se utiliza enum cuando se conocen todos los valores posibles de una variable en tiempo de compilación
    CORAZONES("♥"),
    DIAMANTES("♦"),
    TREBOLES("♣"),
    PICAS("♠");

    private final String simbolo;

    //constructor que recibe el simbolo
    Palo(String simbolo) {
        this.simbolo = simbolo;
    }
    //getSimbolo() retorna el simbolo correspondiente
    public String getSimbolo() {return simbolo; }
}
