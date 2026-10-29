package dao;

import model.PerfilJugador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC de JugadorDAO sobre la tabla jugador de MySQL. Todas las
 * consultas usan PreparedStatement (sin concatenar texto) para evitar inyección SQL.
 * Solo trabaja con jugadores de tipo HUMANO: los bots sembrados en schema.sql quedan
 * protegidos de edición y borrado desde la pantalla de perfiles.
 */
public class JugadorDAOJdbc implements JugadorDAO {

    private static final int MYSQL_DUPLICADO = 1062;
    private static final int MYSQL_LLAVE_FORANEA = 1451;

    private static final String SQL_INSERTAR =
            "INSERT INTO jugador (nombre, fichas, tipo) VALUES (?, ?, 'HUMANO')";
    private static final String SQL_LISTAR =
            "SELECT id, nombre, fichas FROM jugador WHERE tipo = 'HUMANO' ORDER BY nombre";
    private static final String SQL_BUSCAR =
            "SELECT id, nombre, fichas FROM jugador WHERE id = ? AND tipo = 'HUMANO'";
    private static final String SQL_ACTUALIZAR =
            "UPDATE jugador SET nombre = ?, fichas = ? WHERE id = ? AND tipo = 'HUMANO'";
    private static final String SQL_ACTUALIZAR_FICHAS =
            "UPDATE jugador SET fichas = ? WHERE id = ? AND tipo = 'HUMANO'";
    private static final String SQL_ELIMINAR =
            "DELETE FROM jugador WHERE id = ? AND tipo = 'HUMANO'";

    /**
     * Inserta un perfil nuevo y recupera el id que generó MySQL.
     * @param perfil perfil sin guardar.
     * @return el mismo perfil con su id asignado.
     * @throws DAOException si el nombre ya existe o falla la base de datos.
     */
    @Override
    public PerfilJugador crear(PerfilJugador perfil) throws DAOException {
        try (Connection con = ConexionBD.get();
             PreparedStatement ps = con.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, perfil.getNombre());
            ps.setInt(2, perfil.getFichas());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    perfil.setId(claves.getInt(1));
                }
            }
            return perfil;
        } catch (SQLException e) {
            throw traducir(e, "No se pudo crear el perfil.", perfil.getNombre());
        }
    }

    /**
     * Lista todos los perfiles humanos ordenados por nombre.
     * @return lista de perfiles (vacía si no hay ninguno).
     * @throws DAOException si falla la base de datos.
     */
    @Override
    public List<PerfilJugador> listar() throws DAOException {
        List<PerfilJugador> perfiles = new ArrayList<>();
        try (Connection con = ConexionBD.get();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                perfiles.add(mapear(rs));
            }
            return perfiles;
        } catch (SQLException e) {
            throw traducir(e, "No se pudieron cargar los perfiles.", null);
        }
    }

    /**
     * Busca un perfil humano por su id.
     * @param id identificador del perfil.
     * @return el perfil, o un Optional vacío si no existe.
     * @throws DAOException si falla la base de datos.
     */
    @Override
    public Optional<PerfilJugador> buscarPorId(int id) throws DAOException {
        try (Connection con = ConexionBD.get();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw traducir(e, "No se pudo buscar el perfil.", null);
        }
    }

    /**
     * Actualiza nombre y fichas de un perfil existente.
     * @param perfil perfil con id válido.
     * @return true si se modificó una fila, false si el perfil no existe.
     * @throws DAOException si el nombre ya lo usa otro perfil o falla la base de datos.
     */
    @Override
    public boolean actualizar(PerfilJugador perfil) throws DAOException {
        try (Connection con = ConexionBD.get();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, perfil.getNombre());
            ps.setInt(2, perfil.getFichas());
            ps.setInt(3, perfil.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traducir(e, "No se pudo actualizar el perfil.", perfil.getNombre());
        }
    }

    /**
     * Actualiza solo las fichas de un perfil (guardado al terminar la ronda).
     * @param id identificador del perfil.
     * @param fichas nueva cantidad de fichas (no negativa).
     * @return true si se modificó una fila, false si el perfil no existe.
     * @throws DAOException si las fichas son negativas o falla la base de datos.
     */
    @Override
    public boolean actualizarFichas(int id, int fichas) throws DAOException {
        if (fichas < 0) {
            throw new DAOException("Las fichas no pueden ser negativas.");
        }
        try (Connection con = ConexionBD.get();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR_FICHAS)) {
            ps.setInt(1, fichas);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traducir(e, "No se pudieron guardar las fichas.", null);
        }
    }

    /**
     * Elimina un perfil por su id.
     * @param id identificador del perfil.
     * @return true si se eliminó, false si no existía.
     * @throws DAOException si el perfil tiene manos registradas o falla la base de datos.
     */
    @Override
    public boolean eliminar(int id) throws DAOException {
        try (Connection con = ConexionBD.get();
             PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traducir(e, "No se pudo eliminar el perfil.", null);
        }
    }

    /**
     * Convierte la fila actual de un ResultSet en un PerfilJugador.
     * @param rs resultado posicionado en una fila con id, nombre y fichas.
     * @return el perfil correspondiente.
     * @throws SQLException si falla la lectura de alguna columna.
     */
    private PerfilJugador mapear(ResultSet rs) throws SQLException {
        return new PerfilJugador(rs.getInt("id"), rs.getString("nombre"), rs.getInt("fichas"));
    }

    /**
     * Traduce un SQLException técnico a una DAOException con mensaje para el usuario,
     * reconociendo nombre duplicado y perfiles con manos asociadas.
     * @param e error SQL original.
     * @param mensajeBase mensaje genérico si el error no es uno conocido.
     * @param nombre nombre del perfil involucrado, o null si no aplica.
     * @return la DAOException lista para lanzar.
     */
    private DAOException traducir(SQLException e, String mensajeBase, String nombre) {
        if (e.getErrorCode() == MYSQL_DUPLICADO) {
            return new DAOException("Ya existe un perfil llamado \"" + nombre + "\".", e);
        }
        if (e.getErrorCode() == MYSQL_LLAVE_FORANEA) {
            return new DAOException("No se puede eliminar: el perfil tiene manos registradas.", e);
        }
        return new DAOException(mensajeBase + " Revise la conexión con la base de datos.", e);
    }
}
