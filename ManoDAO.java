package dao;

import java.sql.SQLException;

/**
 * Contrato para guardar manos de póker. Quien lo usa (el servicio) no sabe si por debajo
 * hay JDBC, otra base de datos o un archivo: solo conoce estas operaciones.
 */
public interface ManoDAO {

    /**
     * Busca un jugador por nombre y, si no existe, lo crea.
     * @param nombre nombre único del jugador.
     * @param tipo "HUMANO" o "BOT".
     * @return id del jugador en la base de datos.
     * @throws SQLException si falla la consulta.
     */
    int obtenerOCrearJugador(String nombre, String tipo) throws SQLException;

    /**
     * Inserta la fila principal de la mano.
     * @param bote fichas del bote.
     * @param combinacionGanadora combinación con la que se ganó.
     * @param ganadorId id del jugador ganador.
     * @return id que MySQL generó para la mano.
     * @throws SQLException si falla el insert.
     */
    int insertarMano(int bote, String combinacionGanadora, int ganadorId) throws SQLException;

    /**
     * Inserta cuántas fichas tenía un jugador antes y después de la mano.
     * @param manoId id de la mano.
     * @param jugadorId id del jugador.
     * @param fichasAntes fichas al empezar la mano.
     * @param fichasDespues fichas al terminar la mano.
     * @throws SQLException si falla el insert.
     */
    void insertarParticipacion(int manoId, int jugadorId, int fichasAntes, int fichasDespues) throws SQLException;

    /**
     * Inserta una acción (check, call, fold o raise) de la mano.
     * @param manoId id de la mano.
     * @param jugadorId id del jugador que actuó.
     * @param orden posición de la jugada dentro de la mano (1, 2, 3...).
     * @param tipo CHECK, CALL, FOLD o RAISE.
     * @param monto monto asociado (0 si no aplica).
     * @throws SQLException si falla el insert.
     */
    void insertarAccion(int manoId, int jugadorId, int orden, String tipo, int monto) throws SQLException;
}
