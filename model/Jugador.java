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
 * @param nombre nombre del jugador.
 * @param saldoFichasIniciales fichas con las que empieza el jugador (no puede ser negativo).
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
 * @return nombre del jugador.
 */
    public String getNombre() {return nombre; }

    //obtiene cantidad de fichas
/**
 * Obtiene el saldo de fichas del jugador.
 * @return fichas disponibles del jugador.
 */
    public int getSaldoFichas() {return saldoFichas;}

    //obtiene la mano actual de poker
/**
 * Obtiene la mano actual del jugador.
 * @return mano actual del jugador, o null si todavía no tiene.
 */
    public ManoPoker getManoActual() {return manoActual; }

    //asigna una nueva mano de cartas al jugador, para repartir al inicio de cada ronda
/**
 * Asigna la mano actual del jugador.
 * @param manoActual mano que se asigna al jugador para la ronda.
 * @return no retorna un valor.
 */
    public void setManoActual(ManoPoker manoActual) { this.manoActual = manoActual; }

    //bool si el jugador esta activo dentro de la ronda, que este partipando
/**
 * Indica si el jugador está activo en la ronda.
 * @return true si el jugador sigue participando en la ronda.
 */
    public boolean isActivo() {return activo; }

    //Mostrar información del jugador
/**
 * Devuelve una representación textual legible del objeto.
 * @return texto con el nombre y el saldo, por ejemplo "Ana (500 fichas)".
 */
    @Override
    public String toString() {
        return nombre + " (" + saldoFichas + " fichas)";
    }

    //bool si se puede apostar si la cantidad de fichas lo permite
/**
 * Descuenta fichas del saldo del jugador si el monto es válido.
 * @param fichas cantidad de fichas que se descuentan del saldo.
 * @return true si se descontaron las fichas; false si el monto es inválido o supera el saldo.
 */
    public boolean apostar(int fichas) {
        if (fichas <= 0 || fichas > saldoFichas) return false;
        saldoFichas -= fichas;
        return true;
    }

    //Se suman las fichas del jugador, cuando se gana una ronda
/**
 * Aumenta el saldo del jugador en la cantidad indicada.
 * @param fichas cantidad de fichas que se suman al saldo (se ignora si es negativa).
 */
    public void agregarFichas(int fichas) {
        if(fichas >= 0) saldoFichas+= fichas;
    }

    //Marca al jugador en el estado de retirarse (fold), deja de participar
/**
 * Retira al jugador de la ronda actual: deja de estar activo.
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
 * @return true si el saldo del jugador es mayor que cero.
 */
    public boolean tieneFichas() {
        return saldoFichas > 0;
    }

    //ACCIONES DENTRO DEL JUEGO POR CADA TURNO
/**
 * Decide la acción del jugador en su turno; cada subclase define cómo se decide.
 * @param apuestaActual apuesta más alta vigente en la ronda actual.
 * @param boteActual monto acumulado en el bote.
 * @return acción que decide realizar el jugador: CHECK, CALL, FOLD o RAISE.
 */
    public abstract AccionPoker decidirAccion(int apuestaActual, int boteActual);
}


