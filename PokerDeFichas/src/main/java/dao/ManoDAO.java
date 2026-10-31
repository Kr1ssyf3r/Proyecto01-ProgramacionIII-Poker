package dao;

import model.DatosMano;

/**
 * Contrato de acceso a datos para guardar las manos jugadas. El resto de la aplicación
 * solo conoce esta interfaz, nunca SQL ni JDBC.
 */
public interface ManoDAO {

    /**
     * Guarda una mano completa (mano, participaciones y acciones) en una sola transacción.
     * @param mano datos de la mano terminada.
     * @return el id que MySQL asignó a la mano.
     * @throws DAOException si algún jugador no existe o falla la base de datos.
     */
    int guardar(DatosMano mano) throws DAOException;
}
