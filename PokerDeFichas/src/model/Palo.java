
package model;
/*
    Representa los palos posibles de la baraja
    Cada valor incluye su símbolo asociado para facilitar la representación visual en la GUI
 */
/**
 * Enumera los cuatro palos de una baraja y su símbolo visual.
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
/**
 * Obtiene el símbolo del palo.
 * @return resultado de tipo String.
 */
    public String getSimbolo() {return simbolo; }
}
