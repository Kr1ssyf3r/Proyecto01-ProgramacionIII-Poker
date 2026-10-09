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

/**
 * Obtiene el valor numérico de la carta o combinación.
 * @return valor numérico de la combinación (de 1 a 10).
 */
    public int getValor() {
        return valor;
    }

/**
 * Indica si esta combinación tiene mayor jerarquía que otra.
 * @param otra combinación con la que se compara.
 * @return true si esta combinación vale más que la otra.
 */
    public boolean esMayorQue(CombinacionPoker otra) {
        return this.valor > otra.valor;
    }
}
