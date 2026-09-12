package model;

/**
 * Enum que representa todas las combinaciones posibles en el póker,
 * ordenadas de menor a mayor valor para facilitar la comparación.
 */
public enum CombinacionPoker {
    CARTA_ALTA(1),
    PAR(2),
    DOS_PARES(3),
    TRIO(4),
    ESCALERA(5),
    COLOR(6),
    FULL(7),
    POKER(8),
    ESCALERA_COLOR(9),
    ESCALERA_REAL(10);

    private final int valor;

    CombinacionPoker(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    /**
     * Compara si esta combinación es de mayor rango que otra.
     * @param otra la combinación a comparar
     * @return true si esta es mayor
     */
    public boolean esMayorQue(CombinacionPoker otra) {
        return this.valor > otra.valor;
    }
}
