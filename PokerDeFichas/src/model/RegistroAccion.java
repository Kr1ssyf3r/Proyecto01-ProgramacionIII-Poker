package model;

/**
 * Registra una acción ya procesada por JuegoPoker: quién la hizo, cuál fue,
 * y el monto asociado (0 para CHECK/FOLD, el monto pagado para CALL,
 * la nueva apuesta actual para RAISE).
 *
 * Se usa para que la GUI pueda "narrar" con calma lo que hicieron los bots,
 * en vez de solo ver el resultado final de golpe.
 */
public record RegistroAccion(Jugador jugador, AccionPoker accion, int monto) {
}