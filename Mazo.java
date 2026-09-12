package com.una.pokerdefichas.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Implementación de una clase genérica que representa un mazo de elementos
// Para mezclar, repartir y reiniciar el mazo

public class Mazo<T> {
    private List<T> cartas;
    private final List<T> cartasOriginales;

    // Se crea un mazo a partir de una lista inicial de elementos. Se guarda una copia de las cartas iniciales
    //  con las que arranca el mazo
    public Mazo(List<T> cartasIniciales) {
        this.cartasOriginales = new ArrayList<>(cartasIniciales);
        this.cartas = new ArrayList<>(cartasIniciales);
    }

    // se mezcla el orden de las cartas en el mazo
    public void mezclar() {
        Collections.shuffle(cartas);
    }

    //extrae una carta de la parte superior del mazo y lanza una excepcion si el mazo esta vacio
    public T repartirUna() {
        if(cartas.isEmpty()){
            throw new IllegalStateException("No hay cartas en el mazo para repartir");
        }
        return cartas.removeFirst();
    }

    //reparte varias cartas de una sola vez
    public List<T> repartir(int cantidad) {
        if (cantidad > cartas.size()) {
            throw new IllegalArgumentException(
                    "No hay suficientes cartas en el mazo (solicitadas: "
                            + cantidad + ", disponibles : " + cartas.size() + ")");
        }
        List<T> repartidas = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            repartidas.add(repartirUna());
        }
        return repartidas;
    }

    //reinicia todo el mazo y lo mezcla automáticamente
    public void reiniciar(){
        this.cartas = new ArrayList<>(cartasOriginales);
        mezclar();
    }

    // se indican cuantas cartas quedan en el mazo, muestra cantidad restante
    public int cartasRestantes() { return cartas.size();}

    //indica si el mazo contiene cartas
    public boolean estaVacio() { return cartas.isEmpty();}

    //Se crea un mazo ya mezclado y listo para usar. Evita hcerse de forma manual, retorna Mazo&lt;Carta&gt
    // con las 52 cartas ya mezcladas estandar.
    public static Mazo<Carta> crearMazoPoker() {
        List<Carta> cartas = new ArrayList<>();
        for(Palo palo : Palo.values()) {
            for (int valor =2; valor <=14; valor++) {
                cartas.add(new Carta(palo, valor));
            }
        }
        Mazo<Carta> mazo = new Mazo<>(cartas);
        mazo.mezclar();
        return mazo;
    }

}
