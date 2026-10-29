package main.java.dao;

import model.PerfilJugador;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos para los perfiles de jugadores humanos. El resto de la
 * aplicación solo conoce esta interfaz, nunca SQL ni JDBC.
 */
public interface JugadorDAO {

    /**
     * Inserta un perfil nuevo en la base de datos.
     * @param perfil perfil sin guardar (id = 0).
     * @return el mismo perfil con su id ya asignado.
     * @throws DAOException si el nombre ya existe o falla la base de datos.
     */
    PerfilJugador crear(PerfilJugador perfil) throws DAOException;

    /**
     * Lista todos los perfiles de jugadores humanos, ordenados por nombre.
     * @return lista de perfiles (vacía si no hay ninguno).
     * @throws DAOException si falla la base de datos.
     */
    List<PerfilJugador> listar() throws DAOException;

    /**
     * Busca un perfil humano por su id.
     * @param id identificador del perfil.
     * @return el perfil, o un Optional vacío si no existe.
     * @throws DAOException si falla la base de datos.
     */
    Optional<PerfilJugador> buscarPorId(int id) throws DAOException;

    /**
     * Actualiza el nombre y las fichas de un perfil existente.
     * @param perfil perfil con un id válido.
     * @return true si se actualizó, false si el perfil no existe.
     * @throws DAOException si el nombre ya lo usa otro perfil o falla la base de datos.
     */
    boolean actualizar(PerfilJugador perfil) throws DAOException;

    /**
     * Actualiza únicamente las fichas de un perfil (se usa al terminar una ronda).
     * @param id identificador del perfil.
     * @param fichas nueva cantidad de fichas (no negativa).
     * @return true si se actualizó, false si el perfil no existe.
     * @throws DAOException si las fichas son negativas o falla la base de datos.
     */
    boolean actualizarFichas(int id, int fichas) throws DAOException;

    /**
     * Elimina un perfil de la base de datos.
     * @param id identificador del perfil.
     * @return true si se eliminó, false si no existía.
     * @throws DAOException si el perfil tiene manos registradas o falla la base de datos.
     */
    boolean eliminar(int id) throws DAOException;
}
