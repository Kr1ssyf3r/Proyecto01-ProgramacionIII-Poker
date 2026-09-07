package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Panel central de la mesa: muestra las cartas comunitarias y el pozo (bote) actual.
 *
 * NOTA TEMPORAL: los valores empiezan en 0 / sin cartas. Cuando JuegoPoker
 * (Persona 2) esté listo, este panel se actualizará vía actualizarPozo()
 * y actualizarCartasComunitarias() en cada cambio de ronda.
 */
public class PanelMesa extends VBox {

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
        cartasComunitarias.getChildren().addAll(
                new Label("[ ]"), new Label("[ ]"), new Label("[ ]"),
                new Label("[ ]"), new Label("[ ]")
        );

        lblPozo = new Label("Pozo: 0");
        lblPozo.setTextFill(Color.GOLD);
        lblPozo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        getChildren().addAll(titulo, cartasComunitarias, lblPozo);
    }

    /** Actualiza el monto del pozo mostrado en la mesa. */
    public void actualizarPozo(int nuevoPozo) {
        lblPozo.setText("Pozo: " + nuevoPozo);
    }

    /**
     * Actualiza las cartas comunitarias visibles.
     * @param cartasTexto lista de textos de carta (ej. "A♠", "10♦"); se muestran en orden.
     */
    public void actualizarCartasComunitarias(String... cartasTexto) {
        cartasComunitarias.getChildren().clear();
        for (String carta : cartasTexto) {
            cartasComunitarias.getChildren().add(new Label("[ " + carta + " ]"));
        }
    }
}