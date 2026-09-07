package com.una.pokerdefichas.juego;

import com.una.pokerdefichas.modelo.AccionPoker;
import com.una.pokerdefichas.modelo.Jugador;

/*
 * Estrategia agresiva para un jugador bot.
 *
 * Este bot intenta presionar a los demás jugadores
 * mediante aumentos de las apuestas.
 */
public class BotAgresivo implements EstrategiaApuesta {

    /*
     * Determina qué acción realizará el bot.
     */
    @Override
    public AccionPoker decidirAccion(
            Jugador jugador,
            int apuestaActual,
            int boteActual
    ) {

        /*
         * Si todavía no existe una apuesta,
         * el bot realiza un RAISE para comenzar agresivamente.
         */
        if (apuestaActual == 0) {
            return AccionPoker.RAISE;
        }

        /*
         * Si ya existe una apuesta,
         * el bot intenta aumentarla nuevamente.
         */
        return AccionPoker.RAISE;
    }
}