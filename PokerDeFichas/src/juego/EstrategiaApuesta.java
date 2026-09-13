package juego;


import model.AccionPoker;

public interface EstrategiaApuesta {

    
    AccionPoker decidirAccion(int apuestaActual, int boteActual);
}
