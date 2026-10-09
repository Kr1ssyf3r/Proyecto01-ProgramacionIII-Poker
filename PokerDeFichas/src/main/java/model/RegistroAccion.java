package model;

/**
 * Registro inmutable de una jugada: quién la hizo, qué acción fue y con qué monto.
 * @param jugador jugador que realizó la acción.
 * @param accion acción que realizó el jugador.
 * @param monto monto asociado a la acción (0 si no aplica).
 */
public record RegistroAccion(Jugador jugador, AccionPoker accion, int monto) {
}
