package com.una.pokerdefichas.modelo;

import com.una.pokerdefichas.juego.EstrategiaApuesta;

/*
 * Representa a un jugador controlado por la computadora.
 *
 * Hereda de la clase abstracta Jugador.
 * Además, posee una estrategia que determina
 * cómo debe comportarse durante las apuestas.
 */
public class JugadorBot extends Jugador {

    /*
     * Estrategia utilizada por el bot.
     *
     * Puede ser BotConservador, BotAgresivo
     * u otra estrategia que se cree posteriormente.
     */
    private EstrategiaApuesta estrategia;

    /*
     * Constructor del jugador bot.
     *
     * @param nombre nombre del bot
     * @param saldoFichasIniciales fichas iniciales
     * @param estrategia estrategia que utilizará
     */
    public JugadorBot(
            String nombre,
            int saldoFichasIniciales,
            EstrategiaApuesta estrategia
    ) {

        /*
         * Llama al constructor de Jugador.
         */
        super(nombre, saldoFichasIniciales);

        /*
         * Guarda la estrategia seleccionada.
         */
        this.estrategia = estrategia;
    }

    /*
     * Obtiene la estrategia actual del bot.
     */
    public EstrategiaApuesta getEstrategia() {
        return estrategia;
    }

    /*
     * Permite cambiar la estrategia del bot.
     */
    public void setEstrategia(EstrategiaApuesta estrategia) {
        this.estrategia = estrategia;
    }

    /*
     * Decide qué acción realizará el bot.
     *
     * La decisión se delega a la estrategia.
     */
    @Override
    public AccionPoker decidirAccion(
            int apuestaActual,
            int boteActual
    ) {

        return estrategia.decidirAccion(
                this,
                apuestaActual,
                boteActual
        );
    }
}