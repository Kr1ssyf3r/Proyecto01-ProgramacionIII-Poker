package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Implementación de una clase genérica que representa un mazo de elementos
// Para mezclar, repartir y reiniciar el mazo

/**
 * Representa un mazo genérico de cartas y permite mezclar, repartir y consultar las cartas restantes.
 */
public class Mazo<T> {
    private List<T> cartas;
    private final List<T> cartasOriginales;

    // Se crea un mazo a partir de una lista inicial de elementos. Se guarda una copia de las cartas iniciales
    //  con las que arranca el mazo
/**
 * Crea una nueva instancia de Mazo con los datos recibidos.
 * @param cartasIniciales valor utilizado por el método para realizar su operación.
 */
    public Mazo(List<T> cartasIniciales) {
        this.cartasOriginales = new ArrayList<>(cartasIniciales);
        this.cartas = new ArrayList<>(cartasIniciales);
    }

    // se mezcla el orden de las cartas en el mazo
/**
 * Baraja aleatoriamente las cartas restantes del mazo.
 */
    public void mezclar() {
        Collections.shuffle(cartas);
    }

    //extrae una carta de la parte superior del mazo y lanza una excepcion si el mazo esta vacio
/**
 * Extrae y devuelve una carta del mazo.
 * @return valor calculado o recuperado por el método.
 */
    public T repartirUna() {
        if(cartas.isEmpty()){
            throw new IllegalStateException("No hay cartas en el mazo para repartir");
        }
        return cartas.removeFirst();
    }

    //reparte varias cartas de una sola vez
/**
 * Extrae la cantidad indicada de elementos del mazo y los devuelve.
 * @param cantidad valor utilizado por el método para realizar su operación.
 * @return valor calculado o recuperado por el método.
 */
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
/**
 * Restaura el mazo a su contenido inicial y lo vuelve a mezclar.
 */
    public void reiniciar(){
        this.cartas = new ArrayList<>(cartasOriginales);
        mezclar();
    }

    // se indican cuantas cartas quedan en el mazo, muestra cantidad restante
/**
 * Realiza la operación asociada al método cartasRestantes.
 * @param cartas.size( parámetro de entrada del método.
 * @return resultado de tipo int.
 */
    public int cartasRestantes() { return cartas.size();}

    //indica si el mazo contiene cartas
/**
 * Realiza la operación asociada al método estaVacio.
 * @param cartas.isEmpty( parámetro de entrada del método.
 * @return resultado de tipo boolean.
 */
    public boolean estaVacio() { return cartas.isEmpty();}

    //Se crea un mazo ya mezclado y listo para usar. Evita hcerse de forma manual, retorna Mazo&lt;Carta&gt
    // con las 52 cartas ya mezcladas estandar.
/**
 * Crea un mazo estándar de 52 cartas y lo devuelve barajado.
 * @return valor calculado o recuperado por el método.
 */
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
