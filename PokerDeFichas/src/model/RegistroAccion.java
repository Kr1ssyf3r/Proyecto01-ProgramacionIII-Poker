package model;

/**
 * Realiza la operación correspondiente al método RegistroAccion sobre los datos recibidos.
 * @param jugador valor utilizado por el método para realizar su operación.
 * @param accion valor utilizado por el método para realizar su operación.
 * @param monto valor utilizado por el método para realizar su operación.
 * @return valor calculado o recuperado por el método.
 */
public record RegistroAccion(Jugador jugador, AccionPoker accion, int monto) {
}
