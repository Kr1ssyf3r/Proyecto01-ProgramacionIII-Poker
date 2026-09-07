package com.una.pokerdefichas.juego;

import com.una.pokerdefichas.modelo.AccionPoker;
import com.una.pokerdefichas.modelo.Jugador;

/*
 * Estrategia conservadora para un jugador bot.
 *
 * Este bot intenta reducir los riesgos durante
 * las rondas de apuestas.
 */
public class BotConservador implements EstrategiaApuesta {

    /*
     * Determina qué acción realizará el bot
     * dependiendo de la apuesta actual.
     */
    @Override
    public AccionPoker decidirAccion(
            Jugador jugador,
            int apuestaActual,
            int boteActual
    ) {

        /*
         * Si nadie ha realizado una apuesta,
         * el bot utiliza CHECK para no arriesgar fichas.
         */
        if (apuestaActual == 0) {
            return AccionPoker.CHECK;
        }

        /*
         * Si la apuesta es relativamente pequeña,
         * el bot decide igualarla.
         */
        if (apuestaActual <= 100) {
            return AccionPoker.CALL;
        }

        /*
         * Si la apuesta es demasiado alta,
         * el bot conservador se retira.
         */
        return AccionPoker.FOLD;
    }
}