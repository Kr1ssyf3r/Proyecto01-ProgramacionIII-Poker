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
     * Constructor para una mano de exactamente 5 cartas.
     * @param cartas lista de 5 cartas
     * @throws IllegalArgumentException si no son exactamente 5
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
     * Constructor para Texas Hold'em: recibe entre 5 y 7 cartas (2 privadas + comunitarias)
     * y elige la mejor combinación de 5 cartas.
     * @param cartas lista de cartas (5 a 7)
     * @param esTexasHoldem debe ser true para usar este constructor
     * @throws IllegalArgumentException si el tamaño no está entre 5 y 7
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

    private void ordenarPorValor() {
        this.cartas.sort(Comparator.comparingInt(Carta::getValor));
    }

    /**
     * Genera todas las combinaciones de 5 cartas de una lista de 7 y devuelve la mejor.
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
     * Evalúa la mano y devuelve la combinación más alta.
     * @return CombinacionPoker correspondiente
     */
    public CombinacionPoker evaluar() {
        return tipo;
    }

    /**
     * Calcula el tipo de mano y los valores para desempate.
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
     * Devuelve el valor de la carta más alta (para desempates simples).
     */
    public int getValorAlto() {
        return cartas.get(cartas.size() - 1).getValor();
    }

    // --- Métodos de verificación de combinaciones ---

    private boolean esColor() {
        Palo primero = cartas.get(0).getPalo();
        return cartas.stream().allMatch(c -> c.getPalo() == primero);
    }

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

    private boolean esEscaleraReal() {
        return esColor() && esEscalera() &&
                cartas.get(0).getValor() == 10 && cartas.get(4).getValor() == 14;
    }

    private boolean esEscaleraColor() {
        return esColor() && esEscalera() && !esEscaleraReal();
    }

    private Map<Integer, Integer> obtenerFrecuencias() {
        Map<Integer, Integer> frec = new HashMap<>();
        for (Carta c : cartas) {
            frec.put(c.getValor(), frec.getOrDefault(c.getValor(), 0) + 1);
        }
        return frec;
    }

    private boolean esPoker() {
        return obtenerFrecuencias().containsValue(4);
    }

    private boolean esFull() {
        Map<Integer, Integer> frec = obtenerFrecuencias();
        return frec.containsValue(3) && frec.containsValue(2);
    }

    private boolean esTrio() {
        Map<Integer, Integer> frec = obtenerFrecuencias();
        return frec.containsValue(3) && !frec.containsValue(2);
    }

    private boolean esDosPares() {
        long pares = obtenerFrecuencias().values().stream().filter(v -> v == 2).count();
        return pares == 2;
    }

    private boolean esPar() {
        return obtenerFrecuencias().containsValue(2);
    }

    // --- Métodos para obtener valores de desempate (ordenados de mayor a menor importancia) ---

    private List<Integer> obtenerValoresAltos() {
        List<Integer> valores = new ArrayList<>();
        for (Carta c : cartas) valores.add(c.getValor());
        Collections.reverse(valores); // de mayor a menor
        return valores;
    }

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

    private List<Integer> obtenerValoresFull() {
        Map<Integer, Integer> frec = obtenerFrecuencias();
        int trio = -1, par = -1;
        for (Map.Entry<Integer, Integer> e : frec.entrySet()) {
            if (e.getValue() == 3) trio = e.getKey();
            else if (e.getValue() == 2) par = e.getKey();
        }
        return Arrays.asList(trio, par);
    }

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
     * Compara esta mano con otra para desempate.
     * @param otra la mano contraria
     * @return 1 si esta es mayor, -1 si es menor, 0 si son iguales (en teoría)
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

    public List<Carta> getCartas() {
        return new ArrayList<>(cartas);
    }

    @Override
    public String toString() {
        return cartas.toString() + " → " + tipo;
    }
}
