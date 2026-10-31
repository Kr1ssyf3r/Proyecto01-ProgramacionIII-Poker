package dao;

import model.DatosMano;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementación JDBC de ManoDAO. Guarda una mano completa (mano, participaciones y
 * acciones) en una sola transacción: o se guarda todo, o no se guarda nada. Todas las
 * consultas usan PreparedStatement. Los jugadores se buscan por nombre, que es único.
 */
public class ManoDAOJdbc implements ManoDAO {

    private static final String SQL_MANO =
            "INSERT INTO mano (bote, combinacion_ganadora, ganador_id) "
            + "VALUES (?, ?, (SELECT id FROM jugador WHERE nombre = ?))";
    private static final String SQL_PARTICIPACION =
            "INSERT INTO participacion (mano_id, jugador_id, fichas_antes, fichas_despues) "
            + "VALUES (?, (SELECT id FROM jugador WHERE nombre = ?), ?, ?)";
    private static final String SQL_ACCION =
            "INSERT INTO accion (mano_id, jugador_id, orden, tipo, monto) "
            + "VALUES (?, (SELECT id FROM jugador WHERE nombre = ?), ?, ?, ?)";

    /**
     * Guarda la mano, sus participaciones y sus acciones en una transacción.
     * Si algo falla, se deshace todo (rollback) y no queda una mano a medias.
     * @param mano datos de la mano terminada.
     * @return el id que MySQL asignó a la mano.
     * @throws DAOException si algún jugador no existe o falla la base de datos.
     */
    @Override
    public int guardar(DatosMano mano) throws DAOException {
        try (Connection con = ConexionBD.get()) {
            con.setAutoCommit(false);
            try {
                int manoId = insertarMano(con, mano);
                insertarParticipaciones(con, manoId, mano);
                insertarAcciones(con, manoId, mano);
                con.commit();
                return manoId;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo guardar la mano en el historial.", e);
        }
    }

    /**
     * Inserta la fila de la tabla mano y devuelve su id.
     * @param con conexión con la transacción abierta.
     * @param mano datos de la mano.
     * @return id generado por MySQL.
     * @throws SQLException si falla la inserción (por ejemplo, si el ganador no existe).
     */
    private int insertarMano(Connection con, DatosMano mano) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_MANO, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, mano.bote());
            ps.setString(2, mano.combinacion());
            ps.setString(3, mano.ganador());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
                throw new SQLException("MySQL no devolvió el id de la mano.");
            }
        }
    }

    /**
     * Inserta una fila de participacion por cada jugador de la mano.
     * @param con conexión con la transacción abierta.
     * @param manoId id de la mano ya insertada.
     * @param mano datos de la mano.
     * @throws SQLException si falla alguna inserción.
     */
    private void insertarParticipaciones(Connection con, int manoId, DatosMano mano)
            throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_PARTICIPACION)) {
            for (DatosMano.Participacion p : mano.participaciones()) {
                ps.setInt(1, manoId);
                ps.setString(2, p.jugador());
                ps.setInt(3, p.fichasAntes());
                ps.setInt(4, p.fichasDespues());
                ps.executeUpdate();
            }
        }
    }

    /**
     * Inserta una fila de accion por cada jugada, numerándolas en orden desde 1.
     * @param con conexión con la transacción abierta.
     * @param manoId id de la mano ya insertada.
     * @param mano datos de la mano.
     * @throws SQLException si falla alguna inserción.
     */
    private void insertarAcciones(Connection con, int manoId, DatosMano mano)
            throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_ACCION)) {
            int orden = 1;
            for (DatosMano.Jugada j : mano.acciones()) {
                ps.setInt(1, manoId);
                ps.setString(2, j.jugador());
                ps.setInt(3, orden++);
                ps.setString(4, j.tipo());
                ps.setInt(5, j.monto());
                ps.executeUpdate();
            }
        }
    }
}
