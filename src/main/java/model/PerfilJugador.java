package main.java.model;

/**
 * Perfil persistente de un jugador humano: su identificador en la base de datos,
 * su nombre y las fichas acumuladas. Es la entidad que viaja entre la interfaz
 * gráfica y la capa DAO.
 */
public class PerfilJugador {

    /** Largo máximo del nombre; coincide con VARCHAR(40) de la tabla jugador. */
    public static final int LARGO_MAXIMO_NOMBRE = 40;

    private int id;
    private String nombre;
    private int fichas;

    /**
     * Crea un perfil que ya existe en la base de datos.
     * @param id llave primaria del perfil.
     * @param nombre nombre del perfil (no puede estar vacío ni pasar de 40 caracteres).
     * @param fichas fichas acumuladas (no puede ser negativo).
     * @throws IllegalArgumentException si el nombre es inválido o las fichas son negativas.
     */
    public PerfilJugador(int id, String nombre, int fichas) {
        validarNombre(nombre);
        if (fichas < 0) {
            throw new IllegalArgumentException("Las fichas no pueden ser negativas.");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.fichas = fichas;
    }

    /**
     * Crea un perfil nuevo que todavía no se ha guardado (id = 0); el id real lo
     * asigna MySQL al insertarlo.
     * @param nombre nombre del perfil.
     * @param fichas fichas iniciales.
     * @throws IllegalArgumentException si el nombre es inválido o las fichas son negativas.
     */
    public PerfilJugador(String nombre, int fichas) {
        this(0, nombre, fichas);
    }

    /**
     * Verifica que un nombre sea válido para un perfil.
     * @param nombre texto a validar.
     * @throws IllegalArgumentException si es nulo, vacío o excede el largo máximo.
     */
    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del perfil no puede estar vacío.");
        }
        if (nombre.trim().length() > LARGO_MAXIMO_NOMBRE) {
            throw new IllegalArgumentException(
                    "El nombre no puede pasar de " + LARGO_MAXIMO_NOMBRE + " caracteres.");
        }
    }

    /**
     * Obtiene el identificador del perfil en la base de datos.
     * @return el id; 0 si el perfil aún no se ha guardado.
     */
    public int getId() {
        return id;
    }

    /**
     * Asigna el identificador generado por la base de datos.
     * @param id llave primaria generada.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del perfil.
     * @return nombre del perfil.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el nombre del perfil.
     * @param nombre nuevo nombre (no vacío, máximo 40 caracteres).
     * @throws IllegalArgumentException si el nombre es inválido.
     */
    public void setNombre(String nombre) {
        validarNombre(nombre);
        this.nombre = nombre.trim();
    }

    /**
     * Obtiene las fichas acumuladas del perfil.
     * @return cantidad de fichas.
     */
    public int getFichas() {
        return fichas;
    }

    /**
     * Cambia las fichas del perfil.
     * @param fichas nueva cantidad (no puede ser negativa).
     * @throws IllegalArgumentException si es negativa.
     */
    public void setFichas(int fichas) {
        if (fichas < 0) {
            throw new IllegalArgumentException("Las fichas no pueden ser negativas.");
        }
        this.fichas = fichas;
    }

    /**
     * Devuelve una representación textual del perfil.
     * @return texto con el nombre y las fichas, por ejemplo "Ana (500 fichas)".
     */
    @Override
    public String toString() {
        return nombre + " (" + fichas + " fichas)";
    }
}
