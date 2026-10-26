package servicio;

import dao.ConexionBD;
import dao.ManoDAO;
import dao.ManoDAOJdbc;
import model.DatosMano;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio que guarda una mano completa (mano + participaciones + acciones) en UNA transacción:
 * o se guarda todo, o no se guarda nada. Así la base nunca queda con una mano a medias.
 */
public class GuardadorMano {

    /**
     * Guarda la mano. Puede tardar (red, disco), por eso debe llamarse fuera del hilo de JavaFX.
     * @param datos foto de la mano terminada.
     * @return id de la mano guardada.
     * @throws SQLException si no se pudo conectar o algún insert falló (ya con rollback hecho).
     */
    public int guardar(DatosMano datos) throws SQLException {
        try (Connection con = ConexionBD.get()) {          // se cierra sola al salir del try
            con.setAutoCommit(false);                       // empieza la transacción (A)
            try {
                ManoDAO dao = new ManoDAOJdbc(con);

                // 1) Ids de los jugadores (se crean si es la primera vez que aparecen)
                Map<String, Integer> ids = new HashMap<>();
                for (DatosMano.Participacion p : datos.participaciones()) {
                    ids.put(p.nombre(), dao.obtenerOCrearJugador(p.nombre(), p.tipo()));
                }
                Integer ganadorId = ids.get(datos.nombreGanador());
                if (ganadorId == null) {
                    throw new IllegalStateException("El ganador no está entre los participantes");
                }

                // 2) La mano (necesitamos su id para las tablas hijas)
                int manoId = dao.insertarMano(datos.bote(), datos.combinacionGanadora(), ganadorId);

                // 3) Participaciones
                for (DatosMano.Participacion p : datos.participaciones()) {
                    dao.insertarParticipacion(manoId, ids.get(p.nombre()), p.fichasAntes(), p.fichasDespues());
                }

                // 4) Acciones, en el orden en que ocurrieron
                for (DatosMano.Accion a : datos.acciones()) {
                    dao.insertarAccion(manoId, ids.get(a.nombreJugador()), a.orden(), a.tipo(), a.monto());
                }

                con.commit();                               // todo salió bien: se confirma de golpe (B)
                return manoId;
            } catch (SQLException | RuntimeException e) {
                try {
                    con.rollback();                         // algo falló: se deshace TODO lo anterior (C)
                } catch (SQLException ex) {
                    e.addSuppressed(ex);                    // no perder el error original
                }
                throw e;
            }
        }
    }
}
/*
(A) setAutoCommit(false). Por defecto, JDBC confirma cada INSERT en el momento. Al apagar el autocommit, los inserts quedan “pendientes” hasta que tú decidas.

(B) commit(). Confirma todos los inserts pendientes de golpe.

(C) rollback(). Descarta todos los inserts pendientes. Es lo que evita que quede una mano sin acciones o sin participantes.
 */