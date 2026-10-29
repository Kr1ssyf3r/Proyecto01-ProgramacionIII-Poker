package dao;

import model.AccionDetalle;
import model.FilaRanking;
import model.ManoResumen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Consultas de solo lectura para el historial y las estadísticas.
 * Usa JOIN (para mostrar nombres en lugar de ids) y GROUP BY (para contar victorias).
 * Todos los métodos abren y cierran su propia conexión con ConexionBD.
 */
public class EstadisticasDAO {

    private static final String SELECT_MANO = """
            SELECT m.id, m.fecha, m.bote, m.combinacion_ganadora, j.nombre AS ganador
            FROM mano m JOIN jugador j ON j.id = m.ganador_id
            """;

    /**
     * Lista todas las manos guardadas, de la más reciente a la más antigua.
     * @return lista de manos (vacía si no hay ninguna).
     * @throws SQLException si falla la consulta.
     */
    public List<ManoResumen> listarManos() throws SQLException {
        String sql = SELECT_MANO + "ORDER BY m.fecha DESC, m.id DESC";
        try (Connection con = ConexionBD.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<ManoResumen> manos = new ArrayList<>();
            while (rs.next()) {
                manos.add(leerMano(rs));
            }
            return manos;
        }
    }

    /**
     * Lista las acciones de una mano en el orden en que ocurrieron.
     * @param manoId identificador de la mano.
     * @return acciones de esa mano (vacía si no tiene).
     * @throws SQLException si falla la consulta.
     */
    public List<AccionDetalle> listarAcciones(int manoId) throws SQLException {
        String sql = """
                SELECT a.orden, j.nombre, a.tipo, a.monto
                FROM accion a JOIN jugador j ON j.id = a.jugador_id
                WHERE a.mano_id = ?
                ORDER BY a.orden
                """;
        try (Connection con = ConexionBD.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, manoId);
            try (ResultSet rs = ps.executeQuery()) {
                List<AccionDetalle> acciones = new ArrayList<>();
                while (rs.next()) {
                    acciones.add(new AccionDetalle(
                            rs.getInt("orden"), rs.getString("nombre"),
                            rs.getString("tipo"), rs.getInt("monto")));
                }
                return acciones;
            }
        }
    }

    /**
     * Cuenta las manos ganadas por cada jugador (GROUP BY), de más a menos victorias.
     * @return ranking de victorias (vacío si no hay manos).
     * @throws SQLException si falla la consulta.
     */
    public List<FilaRanking> rankingVictorias() throws SQLException {
        String sql = """
                SELECT j.nombre, COUNT(*) AS victorias
                FROM mano m JOIN jugador j ON j.id = m.ganador_id
                GROUP BY j.id, j.nombre
                ORDER BY victorias DESC, j.nombre
                """;
        return consultarRanking(sql);
    }

    /**
     * Busca la mano con el bote más grande.
     * @return la mano de mayor bote, o vacío si no hay manos.
     * @throws SQLException si falla la consulta.
     */
    public Optional<ManoResumen> mayorBote() throws SQLException {
        String sql = SELECT_MANO + "ORDER BY m.bote DESC, m.fecha DESC LIMIT 1";
        try (Connection con = ConexionBD.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? Optional.of(leerMano(rs)) : Optional.empty();
        }
    }

    /**
     * Busca el bot con más manos ganadas (GROUP BY solo sobre jugadores de tipo BOT).
     * @return el bot con más victorias, o vacío si ningún bot ha ganado.
     * @throws SQLException si falla la consulta.
     */
    public Optional<FilaRanking> botConMasVictorias() throws SQLException {
        String sql = """
                SELECT j.nombre, COUNT(*) AS victorias
                FROM mano m JOIN jugador j ON j.id = m.ganador_id
                WHERE j.tipo = 'BOT'
                GROUP BY j.id, j.nombre
                ORDER BY victorias DESC, j.nombre
                LIMIT 1
                """;
        List<FilaRanking> filas = consultarRanking(sql);
        return filas.isEmpty() ? Optional.empty() : Optional.of(filas.get(0));
    }

    private List<FilaRanking> consultarRanking(String sql) throws SQLException {
        try (Connection con = ConexionBD.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<FilaRanking> filas = new ArrayList<>();
            while (rs.next()) {
                filas.add(new FilaRanking(rs.getString("nombre"), rs.getInt("victorias")));
            }
            return filas;
        }
    }

    private ManoResumen leerMano(ResultSet rs) throws SQLException {
        return new ManoResumen(
                rs.getInt("id"),
                rs.getTimestamp("fecha").toLocalDateTime(),
                rs.getInt("bote"),
                rs.getString("combinacion_ganadora"),
                rs.getString("ganador"));
    }
}
