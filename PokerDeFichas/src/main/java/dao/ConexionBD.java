package dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Entrega conexiones a MySQL leyendo los datos de db.properties. */
public class ConexionBD {
    private static final String ARCHIVO = "db.properties";

    private ConexionBD() { }

    /**
     * Abre una conexión nueva a la base de datos.
     * Busca db.properties en la carpeta actual y, si no está, en PokerDeFichas.
     * @return conexión lista para usar (quien la pide debe cerrarla).
     * @throws SQLException si no se puede leer la configuración o conectar.
     */
    public static Connection get() throws SQLException {
        Path archivo = Files.exists(Path.of(ARCHIVO))
                ? Path.of(ARCHIVO)
                : Path.of("PokerDeFichas", ARCHIVO);
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(archivo)) {
            p.load(in);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer " + archivo.toAbsolutePath(), e);
        }
        return DriverManager.getConnection(
                p.getProperty("url"), p.getProperty("usuario"), p.getProperty("clave"));
    }
}