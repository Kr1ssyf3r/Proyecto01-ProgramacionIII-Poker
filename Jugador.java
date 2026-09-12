package com.una.pokerdefichas.modelo;
/*
    Clase abstracta, representa a un participante de la mesa, ya sea humano o un bot
    Contiene el comportamiento y los datos comunes entre ambos (fichas, mano, estado)
    Esta deja la decision de que acciones tomar a cada subclase concreta
 */
public abstract class Jugador {
    protected String nombre;
    protected int saldoFichas;
    protected ManoPoker manoActual;
    protected boolean activo;

    //Se crea un nuevo jugador con nombre y saldo inicial. Lanza una excepcion si el saldo es negativo
    protected Jugador(String nombre, int saldoFichasIniciales) {
        if(saldoFichasIniciales < 0) {
            throw new IllegalArgumentException("El saldo inicial de fichas no puede ser negativo");
        }
        this.nombre = nombre;
        this.saldoFichas = saldoFichasIniciales;
        this.activo = true;
        this.manoActual = null;
    }

    //obtiene nombre del jugador
    public String getNombre() {return nombre; }

    //obtiene cantidad de fichas
    public int getSaldoFichas() {return saldoFichas;}

    //obtiene la mano actual de poker
    public ManoPoker getManoActual() {return manoActual; }

    //asigna una nueva mano de cartas al jugador, para repartir al inicio de cada ronda
    public void setManoActual(ManoPoker manoActual) { this.manoActual = manoActual; }

    //bool si el jugador esta activo dentro de la ronda, que este partipando
    public boolean isActivo() {return activo; }

    //Mostrar información del jugador
    @Override
    public String toString() {
        return nombre + " (" + saldoFichas + " fichas)";
    }

    //bool si se puede apostar si la cantidad de fichas lo permite
    public boolean apostar(int fichas) {
        if (fichas <= 0 || fichas > saldoFichas) return false;
        saldoFichas -= fichas;
        return true;
    }

    //Se suman las fichas del jugador, cuando se gana una ronda
    public void agregarFichas(int fichas) {
        if(fichas >= 0) saldoFichas+= fichas;
    }

    //Marca al jugador en el estado de retirarse (fold), deja de participar
    public void fold() { this.activo = false; }

    //reinicia el estado del jugador para una nueva ronda
    public void reiniciarParaNuevaRonda() {
        this.activo = true;
        this.manoActual = null;
    }

    //booleano que indica que tiene suficientes fichas
    public boolean tieneFichas() {
        return saldoFichas > 0;
    }

    //ACCIONES DENTRO DEL JUEGO POR CADA TURNO
    public abstract AccionPoker decidirAccion(int apuestaActual, int boteActual);
}


