package model;
/*
    Representa la carta individual, de la baraja de 52 cartas
    Se mantiene el valor de 2 a 14, 11=jota, 12=reina, 13=Rey, 14=As
 */
/**
 * Representa una carta de la baraja mediante su palo y valor.
 */
public class Carta {
    private final Palo palo;
    private final int valor;

    //Crea una nueva carta con el palo y valor, recibe por parámetro
/**
 * Crea una nueva instancia de Carta con los datos recibidos.
 * @param palo palo de la carta.
 * @param valor valor de la carta, entre 2 y 14 (11 = J, 12 = Q, 13 = K, 14 = As).
 */
    public Carta (Palo palo, int valor) {
        if (valor < 2 || valor > 14) {
            throw new IllegalArgumentException("El valor debe estar entre 2 y 14 (recibido: " + valor + ")");
        }
        this.palo = palo;
        this.valor = valor;
    }
    //getters del palo y del valor de la carta
/**
 * Obtiene el palo de la carta.
 * @return palo de la carta.
 */
    public Palo getPalo() {return palo; }

/**
 * Obtiene el valor numérico de la carta.
 *
 * @return valor de la carta.
 */
    public int getValor() {return valor; }

    //convertidor del valor numérico a su representación en carta
/**
 * Obtiene la representación textual del valor de la carta.
 * @return texto del valor: "J", "Q", "K", "A" o el número de la carta.
 */
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
/**
 * Devuelve una representación textual legible del objeto.
 * @return texto con el valor y el símbolo del palo, por ejemplo "A♠".
 */
    @Override
    public String toString() {
        return getNombreValor() + palo.getSimbolo();
    }

    //Determina si dos cartas son iguales (mismo palo o mismo valor). Retorna true si son iguales y false en caso contrario
/**
 * Compara el objeto actual con otro para determinar si representan la misma entidad.
 * @param obj objeto con el que se compara la carta.
 * @return true si ambas cartas tienen el mismo palo y el mismo valor.
 */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Carta otraCarta)) return false;
        return this.valor == otraCarta.valor && this.palo == otraCarta.palo;
    }

    //en continuación al equals(), se requiere para manejar las cartas y utilizar las colecciones de HashMap o HashSet
/**
 * Calcula el código hash del objeto a partir de sus atributos relevantes.
 * @return código hash calculado con el palo y el valor.
 */
    @Override
    public int hashCode(){
        return palo.hashCode() * 31 + valor;
    }
}
