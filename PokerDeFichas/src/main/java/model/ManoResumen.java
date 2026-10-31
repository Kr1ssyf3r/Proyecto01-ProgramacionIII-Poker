package model;

import java.time.LocalDateTime;

/**
 * Resumen de una mano guardada en la base de datos (una fila del historial).
 * @param id identificador de la mano.
 * @param fecha momento en que se jugó la mano.
 * @param bote fichas que había en el bote al terminar.
 * @param combinacion combinación con la que ganó la mano.
 * @param ganador nombre del jugador que ganó.
 */
public record ManoResumen(int id, LocalDateTime fecha, int bote, String combinacion, String ganador) { }
