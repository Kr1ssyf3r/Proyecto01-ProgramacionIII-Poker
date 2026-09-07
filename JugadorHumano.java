package com.una.pokerdefichas.modelo;

/*
 * Representa al jugador humano.
 *
 * Hereda de la clase abstracta Jugador.
 *
 * La acción del jugador humano será seleccionada
 * posteriormente mediante los botones de la interfaz gráfica.
 */
public class JugadorHumano extends Jugador {

    /*
     * Guarda la acción seleccionada por el jugador.
     */
    private AccionPoker accionSeleccionada;

    /*
     * Constructor del jugador humano.
     *
     * @param nombre nombre del jugador
     * @param saldoFichasIniciales fichas iniciales
     */
    public JugadorHumano(
            String nombre,
            int saldoFichasIniciales
    ) {

        /*
         * Inicializa los atributos heredados
         * de la clase Jugador.
         */
        super(nombre, saldoFichasIniciales);

        /*
         * Al comenzar todavía no existe una acción.
         */
        this.accionSeleccionada = null;
    }

    /*
     * Guarda la acción que seleccionó el jugador.
     *
     * Posteriormente la interfaz gráfica utilizará
     * este método cuando el usuario presione un botón.
     */
    public void seleccionarAccion(AccionPoker accion) {
        this.accionSeleccionada = accion;
    }

    /*
     * Devuelve la acción seleccionada por el jugador.
     */
    @Override
    public AccionPoker decidirAccion(
            int apuestaActual,
            int boteActual
    ) {

        return accionSeleccionada;
    }
}