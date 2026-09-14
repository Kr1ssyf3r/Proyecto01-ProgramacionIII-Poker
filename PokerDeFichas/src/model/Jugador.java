package model;
/*
    Clase abstracta, representa a un participante de la mesa, ya sea humano o un bot
    Contiene el comportamiento y los datos comunes entre ambos (fichas, mano, estado)
    Esta deja la decision de que acciones tomar a cada subclase concreta
 */
/**
 * Clase abstracta base que contiene los datos y comportamientos comunes de los jugadores de la partida.
 */
public abstract class Jugador {
    protected String nombre;
    protected int saldoFichas;
    protected ManoPoker manoActual;
    protected boolean activo;

    //Se crea un nuevo jugador con nombre y saldo inicial. Lanza una excepcion si el saldo es negativo
/**
 * Crea una nueva instancia de Jugador con los datos recibidos.
 * @param nombre valor utilizado por el método para realizar su operación.
 * @param saldoFichasIniciales valor utilizado por el método para realizar su operación.
 */
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
/**
 * Obtiene el nombre del jugador.
 * @return resultado de tipo String.
 */
    public String getNombre() {return nombre; }

    //obtiene cantidad de fichas
/**
 * Obtiene el saldo de fichas del jugador.
 * @return resultado de tipo int.
 */
    public int getSaldoFichas() {return saldoFichas;}

    //obtiene la mano actual de poker
/**
 * Obtiene la mano actual del jugador.
 * @return resultado de tipo ManoPoker.
 */
    public ManoPoker getManoActual() {return manoActual; }

    //asigna una nueva mano de cartas al jugador, para repartir al inicio de cada ronda
/**
 * Asigna la mano actual del jugador.
 * @param manoActual parámetro de entrada del método.
 * @return no retorna un valor.
 */
    public void setManoActual(ManoPoker manoActual) { this.manoActual = manoActual; }

    //bool si el jugador esta activo dentro de la ronda, que este partipando
/**
 * Indica si el jugador está activo en la ronda.
 * @return resultado de tipo boolean.
 */
    public boolean isActivo() {return activo; }

    //Mostrar información del jugador
/**
 * Devuelve una representación textual legible del objeto.
 * @return valor calculado o recuperado por el método.
 */
    @Override
    public String toString() {
        return nombre + " (" + saldoFichas + " fichas)";
    }

    //bool si se puede apostar si la cantidad de fichas lo permite
/**
 * Descuenta fichas del saldo del jugador si el monto es válido.
 * @param fichas valor utilizado por el método para realizar su operación.
 * @return true si se cumple la condición evaluada; false en caso contrario.
 */
    public boolean apostar(int fichas) {
        if (fichas <= 0 || fichas > saldoFichas) return false;
        saldoFichas -= fichas;
        return true;
    }

    //Se suman las fichas del jugador, cuando se gana una ronda
/**
 * Aumenta el saldo del jugador en la cantidad indicada.
 * @param fichas valor utilizado por el método para realizar su operación.
 */
    public void agregarFichas(int fichas) {
        if(fichas >= 0) saldoFichas+= fichas;
    }

    //Marca al jugador en el estado de retirarse (fold), deja de participar
/**
 * Realiza la operación asociada al método fold.
 * @return no retorna un valor.
 */
    public void fold() { this.activo = false; }

    //reinicia el estado del jugador para una nueva ronda
/**
 * Restablece el estado del jugador para comenzar una nueva ronda.
 */
    public void reiniciarParaNuevaRonda() {
        this.activo = true;
        this.manoActual = null;
    }

    //booleano que indica que tiene suficientes fichas
/**
 * Indica si el jugador aún dispone de fichas para continuar en la partida.
 * @return true si se cumple la condición evaluada; false en caso contrario.
 */
    public boolean tieneFichas() {
        return saldoFichas > 0;
    }

    //ACCIONES DENTRO DEL JUEGO POR CADA TURNO
/**
 * Realiza la operación asociada al método decidirAccion.
 * @param apuestaActual parámetro de entrada del método.
 * @param boteActual parámetro de entrada del método.
 * @return resultado de tipo AccionPoker.
 */
    public abstract AccionPoker decidirAccion(int apuestaActual, int boteActual);
}


