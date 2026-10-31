package model;

/**
 * Una fila del ranking de victorias.
 * @param nombre nombre del jugador.
 * @param victorias cantidad de manos ganadas.
 */
public record FilaRanking(String nombre, int victorias) { }
