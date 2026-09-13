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

    private static final Map<Carta, Image> cache = new HashMap<>();
    private static Image reverso;

    private CartaImagenes() {
    }

    /** @return la imagen boca-arriba de la carta indicada (se carga una sola vez y se reutiliza). */
    public static Image obtener(Carta carta) {
        return cache.computeIfAbsent(carta, c -> cargar(nombreArchivo(c)));
    }

    /** @return la imagen del reverso (dorso), la misma para todas las cartas ocultas. */
    public static Image obtenerReverso() {
        if (reverso == null) {
            reverso = cargar("back.png");
        }
        return reverso;
    }

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
