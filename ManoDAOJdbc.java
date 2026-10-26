package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementación de ManoDAO con JDBC puro y MySQL.
 * NO abre ni cierra la conexión ni hace commit: eso lo decide el servicio, porque
 * todos los inserts de una mano deben formar UNA sola transacción.
 */
public class ManoDAOJdbc implements ManoDAO {
    private final Connection conexion;

    /**
     * Crea el DAO sobre una conexión ya abierta.
     * @param conexion conexión que controla el servicio (con autocommit apagado).
     */
    public ManoDAOJdbc(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public int obtenerOCrearJugador(String nombre, String tipo) throws SQLException {
        String buscar = "SELECT id FROM jugador WHERE nombre = ?";
        try (PreparedStatement ps = conexion.prepareStatement(buscar)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        String crear = "INSERT INTO jugador (nombre, tipo) VALUES (?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(crear, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, tipo);
            ps.executeUpdate();
            return leerIdGenerado(ps);
        }
    }

    @Override
    public int insertarMano(int bote, String combinacionGanadora, int ganadorId) throws SQLException {
        String sql = "INSERT INTO mano (bote, combinacion_ganadora, ganador_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bote);
            ps.setString(2, combinacionGanadora);
            ps.setInt(3, ganadorId);
            ps.executeUpdate();
            return leerIdGenerado(ps);
        }
    }

    @Override
    public void insertarParticipacion(int manoId, int jugadorId, int fichasAntes, int fichasDespues)
            throws SQLException {
        String sql = "INSERT INTO participacion (mano_id, jugador_id, fichas_antes, fichas_despues) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, manoId);
            ps.setInt(2, jugadorId);
            ps.setInt(3, fichasAntes);
            ps.setInt(4, fichasDespues);
            ps.executeUpdate();
        }
    }

    @Override
    public void insertarAccion(int manoId, int jugadorId, int orden, String tipo, int monto)
            throws SQLException {
        String sql = "INSERT INTO accion (mano_id, jugador_id, orden, tipo, monto) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, manoId);
            ps.setInt(2, jugadorId);
            ps.setInt(3, orden);
            ps.setString(4, tipo);
            ps.setInt(5, monto);
            ps.executeUpdate();
        }
    }

    /** Lee el id AUTO_INCREMENT que MySQL asignó al último insert de este statement. */
    private int leerIdGenerado(PreparedStatement ps) throws SQLException {
        try (ResultSet claves = ps.getGeneratedKeys()) {
            if (claves.next()) {
                return claves.getInt(1);
            }
            throw new SQLException("MySQL no devolvió el id generado");
        }
    }
}
