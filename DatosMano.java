package model;

import java.util.List;

/**
 * "Foto" inmutable de una mano ya terminada, lista para guardarse en la base de datos.
 * Se usa un record con datos simples (textos y números) en lugar de los objetos Jugador/JuegoPoker
 * porque el guardado corre en OTRO hilo: así el hilo de la base de datos nunca toca objetos
 * que la interfaz sigue modificando.
 * bote fichas totales que había en el bote.
 * combinacionGanadora nombre de la combinación ganadora (o SIN_SHOWDOWN si todos se retiraron).
 * nombreGanador nombre del jugador que ganó la mano.
 * participaciones fichas de cada jugador antes y después de la mano.
 * acciones jugadas de la mano, en orden.
 */
public record DatosMano(int bote, String combinacionGanadora,
                        String nombreGanador, List<Participacion> participaciones,
                        List<Accion> acciones) {

    /** Constructor compacto: copia las listas para que nadie pueda modificarlas después. */
    public DatosMano {
        participaciones = List.copyOf(participaciones);
        acciones = List.copyOf(acciones);
    }

    /** Una fila de la tabla participacion. */
    public record Participacion(String nombre, String tipo, int fichasAntes, int fichasDespues) { }

    /** Una fila de la tabla accion. */
    public record Accion(String nombreJugador, int orden, String tipo, int monto) { }
}
