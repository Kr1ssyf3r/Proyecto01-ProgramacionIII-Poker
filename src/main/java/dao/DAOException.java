package main.java.dao;

/**
 * Excepción de la capa de persistencia. Envuelve los errores de SQL con un mensaje
 * entendible para el usuario, de modo que la interfaz gráfica nunca dependa de
 * java.sql ni muestre mensajes técnicos de MySQL.
 */
public class DAOException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción con un mensaje para el usuario.
     * @param mensaje explicación del problema.
     */
    public DAOException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea la excepción con un mensaje y la causa original.
     * @param mensaje explicación del problema para el usuario.
     * @param causa error original, normalmente un SQLException.
     */
    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
