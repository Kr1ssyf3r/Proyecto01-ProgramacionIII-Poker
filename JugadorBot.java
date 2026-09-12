package com.una.pokerdefichas.modelo;

/**Representa a un jugador controlado automáticamente por el programa.
  Utiliza una estrategia de apuesta para decidir sus acciones.*/

public class JugadorBot extends Jugador {

    private EstrategiaApuesta estrategia;

    /**Crea un nuevo jugador bot y el seteo de las acciones*/

    public JugadorBot(String nombre, int saldoFichasIniciales,
                      EstrategiaApuesta estrategia) {

        super(nombre, saldoFichasIniciales);

        if (estrategia == null) {
            throw new IllegalArgumentException(
                    "La estrategia del bot no puede ser null"
            );
        }

        this.estrategia = estrategia;
    }

    /**Cambia la estrategia utilizada por el bot.
     estrategia nueva estrategia */

    public void setEstrategia(EstrategiaApuesta estrategia) {

        if (estrategia == null) {
            throw new IllegalArgumentException(
                    "La estrategia del bot no puede ser null"
            );
        }

        this.estrategia = estrategia;
    }

    /**Obtiene la estrategia actual del bot.*/

    public EstrategiaApuesta getEstrategia() {
        return estrategia;
    }

    /**Decide qué acción realizar durante su turno.*/

    @Override
    public AccionPoker decidirAccion(int apuestaActual, int boteActual) {
        return estrategia.decidirAccion(apuestaActual, boteActual);
    }
}
