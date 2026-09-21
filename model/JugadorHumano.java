package model;

/**
 * Jugador controlado por una persona a través de la interfaz gráfica.
 * A diferencia del bot, no decide sus acciones por sí mismo: las elige el
 * usuario con los botones de la ventana y el motor las procesa directamente.
 */
public class JugadorHumano extends Jugador {

    /**
     * Crea un jugador humano con su nombre y su saldo inicial de fichas.
     * @param nombre nombre que se muestra en la mesa.
     * @param saldoFichasIniciales cantidad de fichas con la que empieza (no puede ser negativa).
     * @throws IllegalArgumentException si el saldo inicial es negativo.
     */
    public JugadorHumano(String nombre, int saldoFichasIniciales) {
        super(nombre, saldoFichasIniciales);
    }

    /**
     * No se usa para el jugador humano: su acción llega desde la interfaz, no desde una estrategia.
     * @param apuestaActual apuesta más alta vigente en la ronda.
     * @param boteActual monto acumulado en el bote.
     * @return nunca retorna un valor.
     * @throws UnsupportedOperationException siempre, porque la decisión la toma el usuario.
     */
    @Override
    public AccionPoker decidirAccion(int apuestaActual, int boteActual) {
        throw new UnsupportedOperationException(
                "Las acciones del jugador humano las elige el usuario desde la interfaz");
    }
}
