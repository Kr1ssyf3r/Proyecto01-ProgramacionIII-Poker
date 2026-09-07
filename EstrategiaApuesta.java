package cr.ac.una.est.proyecto.juego;

import cr.ac.una.est.proyecto.modelo.Jugador;

public interface EstrategiaApuesta {

    AccionPoker decidirAccion(
            Jugador jugador,
            double apuestaActual,
            double pozo
    );

}