package com.una.pokerdefichas.modelo;
/*
    Representa la carta individual, de la baraja de 52 cartas
    Se mantiene el valor de 2 a 14, 11=jota, 12=reina, 13=Rey, 14=As
 */
public class Carta {
    private final Palo palo;
    private final int valor;

    //Crea una nueva carta con el palo y valor, recibe por parámetro
    public Carta (Palo palo, int valor) {
        if (valor < 2 || valor > 14) {
            throw new IllegalArgumentException("El valor debe estar entre 2 y 14 (recibido: " + valor + ")");
        }
        this.palo = palo;
        this.valor = valor;
    }
    //getters del palo y del valor de la carta
    public Palo getPalo() {return palo; }
    public int getValor() {return valor; }

    //convertidor del valor numérico a su representación en carta
    public String getNombreValor(){
        return switch (valor) {
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            case 14 -> "A";
            default -> String.valueOf(valor);
        };
    }

    //toString() muestra en pantalla la carta con letra y palo correspondiente
    @Override
    public String toString() {
        return getNombreValor() + palo.getSimbolo();
    }

    //Determina si dos cartas son iguales (mismo palo o mismo valor). Retorna true si son iguales y false en caso contrario
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Carta otraCarta)) return false;
        return this.valor == otraCarta.valor && this.palo == otraCarta.palo;
    }

    //en continuación al equals(), se requiere para manejar las cartas y utilizar las colecciones de HashMap o HashSet
    @Override
    public int hashCode(){
        return palo.hashCode() * 31 + valor;
    }
}
