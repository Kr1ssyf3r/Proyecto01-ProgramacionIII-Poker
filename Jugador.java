package cr.ac.una.est.proyecto.modelo;

public abstract class Jugador {

    protected String nombre;
    protected double fichas;
    protected boolean retirado;

    public Jugador(String nombre, double fichas) {
        this.nombre = nombre;
        this.fichas = fichas;
        this.retirado = false;
    }

    public String getNombre() {
        return nombre;
    }

    public double getFichas() {
        return fichas;
    }

    public boolean isRetirado() {
        return retirado;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFichas(double fichas) {
        this.fichas = fichas;
    }

    public void retirar() {
        this.retirado = true;
    }

    public void reiniciarEstado() {
        this.retirado = false;
    }

    public abstract AccionPoker decidirAccion(
            double apuestaActual,
            double pozo
    );
}