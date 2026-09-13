package view;

import model.Carta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Panel central de la mesa: muestra las cartas comunitarias (con imágenes reales,
 * 0 a 5 según la fase) y el pozo (bote) actual.
 */
public class PanelMesa extends VBox {

    private static final double ANCHO_CARTA = 60;
    private static final double ALTO_CARTA = 85;

    private final Label lblPozo;
    private final FlowPane cartasComunitarias;

    public PanelMesa() {
        setSpacing(10);
        setPadding(new Insets(16));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #0b5d33; -fx-border-color: #063b20; -fx-border-width: 4;"
                + " -fx-border-radius: 40; -fx-background-radius: 40;");

        Label titulo = new Label("Cartas comunitarias");
        titulo.setTextFill(Color.WHITE);

        cartasComunitarias = new FlowPane();
        cartasComunitarias.setHgap(8);
        cartasComunitarias.setAlignment(Pos.CENTER);

        lblPozo = new Label("Pozo: 0");
        lblPozo.setTextFill(Color.GOLD);
        lblPozo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        getChildren().addAll(titulo, cartasComunitarias, lblPozo);
    }

    /** Actualiza el monto del pozo mostrado en la mesa. */
    public void actualizarPozo(int nuevoPozo) {
        lblPozo.setText("Pozo: " + nuevoPozo);
    }

    /** Muestra las cartas comunitarias reveladas hasta ahora (lista vacía en PREFLOP). */
    public void actualizarCartasComunitarias(List<Carta> cartas) {
        cartasComunitarias.getChildren().clear();
        for (Carta carta : cartas) {
            ImageView iv = new ImageView(CartaImagenes.obtener(carta));
            iv.setFitWidth(ANCHO_CARTA);
            iv.setFitHeight(ALTO_CARTA);
            cartasComunitarias.getChildren().add(iv);
        }
    }
}
