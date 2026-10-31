package model;

/**
 * Una acción realizada durante una mano (CHECK, CALL, FOLD o RAISE).
 * @param orden posición de la acción dentro de la mano (1, 2, 3...).
 * @param jugador nombre del jugador que actuó.
 * @param tipo tipo de acción realizada.
 * @param monto fichas apostadas en la acción (0 si no aplica).
 */
public record AccionDetalle(int orden, String jugador, String tipo, int monto) { }
