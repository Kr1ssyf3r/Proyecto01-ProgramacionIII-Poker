package view;

import model.Carta;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Carga y cachea las imágenes de las cartas desde src/main/resources/cartas/.
 * Convierte cada Carta a su nombre de archivo (ej. valor=14, palo=PICAS -> "ace_of_spades.png")
 * y las mantiene en memoria para no releer el archivo cada vez que se dibuja la misma carta.
 */
public class CartaImagenes {

/**
 * Realiza la operación asociada al método HashMap<>.
 * @return resultado de tipo new.
 */
    private static final Map<Carta, Image> cache = new HashMap<>();
    private static Image reverso;

/**
 * Crea una nueva instancia de CartaImagenes con los datos recibidos.
 */
    private CartaImagenes() {
    }

/**
 * Obtiene la imagen correspondiente a la carta indicada utilizando la caché de imágenes.
 * @param carta valor utilizado por el método para realizar su operación.
 * @return valor calculado o recuperado por el método.
 */
    public static Image obtener(Carta carta) {
        return cache.computeIfAbsent(carta, c -> cargar(nombreArchivo(c)));
    }

/**
 * Obtiene la imagen del reverso de una carta.
 * @return valor calculado o recuperado por el método.
 */
    public static Image obtenerReverso() {
        if (reverso == null) {
            reverso = cargar("back.png");
        }
        return reverso;
    }

/**
 * Construye el nombre del archivo de imagen correspondiente a una carta.
 * @param carta valor utilizado por el método para realizar su operación.
 * @return valor calculado o recuperado por el método.
 */
    private static String nombreArchivo(Carta carta) {
        String valor = switch (carta.getValor()) {
            case 11 -> "jack";
            case 12 -> "queen";
            case 13 -> "king";
            case 14 -> "ace";
            default -> String.valueOf(carta.getValor());
        };
        String palo = switch (carta.getPalo()) {
            case CORAZONES -> "hearts";
            case DIAMANTES -> "diamonds";
            case TREBOLES -> "clubs";
            case PICAS -> "spades";
        };
        return valor + "_of_" + palo + ".png";
    }

/**
 * Carga desde los recursos la imagen indicada.
 * @param nombreArchivo valor utilizado por el método para realizar su operación.
 * @return valor calculado o recuperado por el método.
 */
    private static Image cargar(String nombreArchivo) {
        String ruta = "/cartas/" + nombreArchivo;
        InputStream in = CartaImagenes.class.getResourceAsStream(ruta);
        if (in == null) {
            throw new IllegalStateException(
                    "No se encontró la imagen " + ruta + " (¿está en src/main/resources/cartas/?)");
        }
        return new Image(in);
    }
}
