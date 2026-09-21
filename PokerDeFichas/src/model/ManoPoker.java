package model;

import java.util.*;

/**
 * Representa una mano de póker y evalúa su combinación.
 * Soporta evaluación de 5 cartas exactas o de 7 cartas (Texas Hold'em)
 * para encontrar la mejor jugada de 5 cartas posible.
 */
public class ManoPoker {
    private final List<Carta> cartas;
    private CombinacionPoker tipo;
    private List<Integer> valoresOrdenados; // para desempate

    // --- Constructores ---

/**
 * Crea una nueva instancia de ManoPoker con los datos recibidos.
 * @param cartas las 5 cartas que forman la mano.
 */
    public ManoPoker(List<Carta> cartas) {
        if (cartas.size() != 5) {
            throw new IllegalArgumentException("La mano debe tener exactamente 5 cartas");
        }
        this.cartas = new ArrayList<>(cartas);
        ordenarPorValor();
        evaluarYCalcularDesempate();
    }

/**
 * Crea una nueva instancia de ManoPoker con los datos recibidos.
 * @param cartas cartas disponibles del jugador (de 5 a 7: privadas y comunitarias).
 * @param esTexasHoldem debe ser true para usar el modo de 5 a 7 cartas; si es false se lanza una excepción.
 */
    public ManoPoker(List<Carta> cartas, boolean esTexasHoldem) {
        if (!esTexasHoldem) {
            throw new IllegalArgumentException("Usa el otro constructor para 5 cartas");
        }
        if (cartas.size() < 5 || cartas.size() > 7) {
            throw new IllegalArgumentException("Se necesitan entre 5 y 7 cartas");
        }
        // Encuentra la mejor combinación de 5 cartas
        List<Carta> mejorMano = encontrarMejorManoDe5(cartas);
        this.cartas = new ArrayList<>(mejorMano);
        ordenarPorValor();
        evaluarYCalcularDesempate();
    }

    // --- Métodos privados auxiliares ---

/**
 * Ordena las cartas de la mano de acuerdo con su valor para facilitar la evaluación.
 */
    private void ordenarPorValor() {
        this.cartas.sort(Comparator.comparingInt(Carta::getValor));
    }

/**
 * Genera las combinaciones posibles de cinco cartas y selecciona la mejor mano disponible.
 * @param todas cartas disponibles (de 5 a 7) entre las que se busca la mejor mano de 5.
 * @return las 5 cartas que forman la mejor mano posible.
 */
    private List<Carta> encontrarMejorManoDe5(List<Carta> todas) {
        List<List<Carta>> combinaciones = new ArrayList<>();
        int n = todas.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    for (int l = k + 1; l < n; l++) {
                        for (int m = l + 1; m < n; m++) {
                            combinaciones.add(Arrays.asList(
                                    todas.get(i), todas.get(j), todas.get(k),
                                    todas.get(l), todas.get(m)
                            ));
                        }
                    }
                }
            }
        }

        // Evaluar cada combinación y quedarse con la mejor
        List<Carta> mejorMano = null;
        ManoPoker mejorManoObj = null;

        for (List<Carta> combo : combinaciones) {
            ManoPoker temp = new ManoPoker(combo); // usa constructor de 5 cartas
            if (mejorManoObj == null || temp.compararCon(mejorManoObj) > 0) {
                mejorManoObj = temp;
                mejorMano = combo;
            }
        }
        return mejorMano;
    }

    // --- Evaluación principal ---

/**
 * Evalúa la mano y determina la combinación de póker correspondiente.
 * @return combinación de póker de la mano.
 */
    public CombinacionPoker evaluar() {
        return tipo;
    }

/**
 * Evalúa la combinación y prepara los valores necesarios para resolver empates entre manos.
 */
    private void evaluarYCalcularDesempate() {
        if (esEscaleraReal()) {
            tipo = CombinacionPoker.ESCALERA_REAL;
            valoresOrdenados = Arrays.asList(14); // solo importa la carta más alta
        } else if (esEscaleraColor()) {
            tipo = CombinacionPoker.ESCALERA_COLOR;
            valoresOrdenados = Arrays.asList(getValorAlto());
        } else if (esPoker()) {
            tipo = CombinacionPoker.POKER;
            valoresOrdenados = obtenerValoresPoker();
        } else if (esFull()) {
            tipo = CombinacionPoker.FULL;
            valoresOrdenados = obtenerValoresFull();
        } else if (esColor()) {
            tipo = CombinacionPoker.COLOR;
            valoresOrdenados = obtenerValoresAltos();
        } else if (esEscalera()) {
            tipo = CombinacionPoker.ESCALERA;
            valoresOrdenados = Arrays.asList(getValorAlto());
        } else if (esTrio()) {
            tipo = CombinacionPoker.TRIO;
            valoresOrdenados = obtenerValoresTrio();
        } else if (esDosPares()) {
            tipo = CombinacionPoker.DOS_PARES;
            valoresOrdenados = obtenerValoresDosPares();
        } else if (esPar()) {
            tipo = CombinacionPoker.PAR;
            valoresOrdenados = obtenerValoresPar();
        } else {
            tipo = CombinacionPoker.CARTA_ALTA;
            valoresOrdenados = obtenerValoresAltos();
        }
    }

/**
 * Obtiene el valor más alto de la mano para desempatar; en la escalera A-2-3-4-5 es 5, porque el As cuenta como 1.
 * @return valor más alto de la mano para desempatar (5 en la escalera A-2-3-4-5).
 */
    public int getValorAlto() {
        // En la escalera A-2-3-4-5 el As cuenta como 1, así que su carta más alta es el 5
        if (esEscalera() && cartas.get(3).getValor() == 5 && cartas.get(4).getValor() == 14) {
            return 5;
        }
        return cartas.get(cartas.size() - 1).getValor();
    }

    // --- Métodos de verificación de combinaciones ---

/**
 * Comprueba si las cinco cartas pertenecen al mismo palo.
 * @return true si las 5 cartas son del mismo palo.
 */
    private boolean esColor() {
        Palo primero = cartas.get(0).getPalo();
        return cartas.stream().allMatch(c -> c.getPalo() == primero);
    }

/**
 * Comprueba si los valores de las cartas forman una secuencia consecutiva.
 * @return true si las 5 cartas son consecutivas (incluye A-2-3-4-5).
 */
    private boolean esEscalera() {
        // Escalera normal: valores consecutivos
        for (int i = 0; i < cartas.size() - 1; i++) {
            if (cartas.get(i + 1).getValor() != cartas.get(i).getValor() + 1) {
                // Caso especial: A-2-3-4-5 (As como 1)
                if (i == 3 && cartas.get(0).getValor() == 2 &&
                        cartas.get(1).getValor() == 3 &&
                        cartas.get(2).getValor() == 4 &&
                        cartas.get(3).getValor() == 5 &&
                        cartas.get(4).getValor() == 14) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }

/**
 * Comprueba si la mano corresponde a una escalera real.
 * @return true si es una escalera de color del 10 al As.
 */
    private boolean esEscaleraReal() {
        return esColor() && esEscalera() &&
                cartas.get(0).getValor() == 10 && cartas.get(4).getValor() == 14;
    }

/**
 * Comprueba si la mano corresponde a una escalera de color.
 * @return true si es una escalera de un mismo palo que no es la real.
 */
    private boolean esEscaleraColor() {
        return esColor() && esEscalera() && !esEscaleraReal();
    }

/**
 * Calcula la frecuencia de cada valor presente en la mano.
 * @return mapa con cada valor de carta y cuántas veces aparece en la mano.
 */
    private Map<Integer, Integer> obtenerFrecuencias() {
        Map<Integer, Integer> frec = new HashMap<>();
        for (Carta c : cartas) {
            frec.put(c.getValor(), frec.getOrDefault(c.getValor(), 0) + 1);
        }
        return frec;
    }

/**
 * Comprueba si existe un grupo de cuatro cartas del mismo valor.
 * @return true si hay cuatro cartas del mismo valor.
 */
    private boolean esPoker() {
        return obtenerFrecuencias().containsValue(4);
    }

/**
 * Comprueba si la mano contiene un trío y una pareja.
 * @return true si hay un trío y una pareja.
 */
    private boolean esFull() {
        Map<Integer, Integer> frec = obtenerFrecuencias();
        return frec.containsValue(3) && frec.containsValue(2);
    }

/**
 * Comprueba si la mano contiene un trío.
 * @return true si hay un trío sin pareja.
 */
    private boolean esTrio() {
        Map<Integer, Integer> frec = obtenerFrecuencias();
        return frec.containsValue(3) && !frec.containsValue(2);
    }

/**
 * Comprueba si la mano contiene dos parejas diferentes.
 * @return true si hay exactamente dos parejas.
 */
    private boolean esDosPares() {
        long pares = obtenerFrecuencias().values().stream().filter(v -> v == 2).count();
        return pares == 2;
    }

/**
 * Comprueba si la mano contiene una pareja.
 * @return true si hay al menos una pareja.
 */
    private boolean esPar() {
        return obtenerFrecuencias().containsValue(2);
    }

    // --- Métodos para obtener valores de desempate (ordenados de mayor a menor importancia) ---

/**
 * Obtiene los valores altos de la mano en orden descendente para desempates.
 * @return valores de las cartas de mayor a menor.
 */
    private List<Integer> obtenerValoresAltos() {
        List<Integer> valores = new ArrayList<>();
        for (Carta c : cartas) valores.add(c.getValor());
        Collections.reverse(valores); // de mayor a menor
        return valores;
    }

/**
 * Obtiene los valores relevantes cuando la mano contiene una pareja.
 * @return valor de la pareja seguido de los kickers, de mayor a menor.
 */
    private List<Integer> obtenerValoresPar() {
        Map<Integer, Integer> frec = obtenerFrecuencias();
        int parValor = -1;
        List<Integer> kickers = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : frec.entrySet()) {
            if (e.getValue() == 2) parValor = e.getKey();
            else kickers.add(e.getKey());
        }
        kickers.sort(Collections.reverseOrder());
        List<Integer> resultado = new ArrayList<>();
        resultado.add(parValor);
        resultado.addAll(kickers);
        return resultado;
    }

/**
 * Obtiene los valores de las dos parejas y del kicker para resolver desempates.
 * @return valores de las dos parejas (de mayor a menor) seguidos del kicker.
 */
    private List<Integer> obtenerValoresDosPares() {
        Map<Integer, Integer> frec = obtenerFrecuencias();
        List<Integer> pares = new ArrayList<>();
        int kicker = -1;
        for (Map.Entry<Integer, Integer> e : frec.entrySet()) {
            if (e.getValue() == 2) pares.add(e.getKey());
            else kicker = e.getKey();
        }
        pares.sort(Collections.reverseOrder());
        List<Integer> resultado = new ArrayList<>(pares);
        resultado.add(kicker);
        return resultado;
    }

/**
 * Obtiene los valores necesarios para desempatar una mano con trío.
 * @return valor del trío seguido de los kickers, de mayor a menor.
 */
    private List<Integer> obtenerValoresTrio() {
        Map<Integer, Integer> frec = obtenerFrecuencias();
        int trio = -1;
        List<Integer> kickers = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : frec.entrySet()) {
            if (e.getValue() == 3) trio = e.getKey();
            else kickers.add(e.getKey());
        }
        kickers.sort(Collections.reverseOrder());
        List<Integer> resultado = new ArrayList<>();
        resultado.add(trio);
        resultado.addAll(kickers);
        return resultado;
    }

/**
 * Obtiene los valores del trío y la pareja de un full house.
 * @return valor del trío seguido del valor de la pareja.
 */
    private List<Integer> obtenerValoresFull() {
        Map<Integer, Integer> frec = obtenerFrecuencias();
        int trio = -1, par = -1;
        for (Map.Entry<Integer, Integer> e : frec.entrySet()) {
            if (e.getValue() == 3) trio = e.getKey();
            else if (e.getValue() == 2) par = e.getKey();
        }
        return Arrays.asList(trio, par);
    }

/**
 * Obtiene los valores del poker y del kicker para desempatar.
 * @return valor del póker seguido del kicker.
 */
    private List<Integer> obtenerValoresPoker() {
        Map<Integer, Integer> frec = obtenerFrecuencias();
        int poker = -1, kicker = -1;
        for (Map.Entry<Integer, Integer> e : frec.entrySet()) {
            if (e.getValue() == 4) poker = e.getKey();
            else kicker = e.getKey();
        }
        return Arrays.asList(poker, kicker);
    }

    // --- Comparación completa ---

/**
 * Compara esta mano con otra mano de póker utilizando la combinación y los criterios de desempate.
 * @param otra mano con la que se compara.
 * @return número positivo si esta mano gana, negativo si pierde y 0 si empatan.
 */
    public int compararCon(ManoPoker otra) {
        if (this.tipo.getValor() != otra.tipo.getValor()) {
            return Integer.compare(this.tipo.getValor(), otra.tipo.getValor());
        }

        // Mismo tipo, comparar listas de valores
        List<Integer> misValores = this.valoresOrdenados;
        List<Integer> susValores = otra.valoresOrdenados;
        for (int i = 0; i < Math.min(misValores.size(), susValores.size()); i++) {
            if (!misValores.get(i).equals(susValores.get(i))) {
                return Integer.compare(misValores.get(i), susValores.get(i));
            }
        }
        return 0; // empate (raro, pero posible con manos idénticas)
    }

    // --- Getters ---

/**
 * Obtiene una copia de las cartas que conforman la mano.
 * @return copia de las 5 cartas de la mano, ordenadas de menor a mayor valor.
 */
    public List<Carta> getCartas() {
        return new ArrayList<>(cartas);
    }

/**
 * Devuelve una representación textual legible del objeto.
 * @return texto con las cartas de la mano y su combinación.
 */
    @Override
    public String toString() {
        return cartas.toString() + " → " + tipo;
    }
}
