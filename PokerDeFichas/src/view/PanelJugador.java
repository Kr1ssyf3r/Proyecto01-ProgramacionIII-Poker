package view;

import model.Jugador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Panel que muestra la información de UN jugador en la mesa:
 * su nombre, sus fichas (saldo) y sus cartas.
 *
 * Ya usa la clase Jugador real de Persona 1. Las cartas siguen como
 * placeholder ("?? ??") porque ManoPoker (Persona 2) todavía no está
 * lista; en cuanto exista, actualizarDesde(Jugador) también podrá leer
 * jugador.getManoActual() para mostrarlas.
 */
public class PanelJugador extends VBox {

    private final Label lblNombre;
    private final Label lblSaldo;
    private final Label lblCartas;

    public PanelJugador(Jugador jugador) {
        setSpacing(4);
        setPadding(new Insets(8));
        setAlignment(Pos.CENTER);
        setStyle("-fx-border-color: gray; -fx-border-radius: 6; -fx-background-radius: 6;"
                + " -fx-background-color: #1e3d2f;");

        lblNombre = new Label(jugador.getNombre());
        lblNombre.setStyle("-fx-font-weight: bold;");
        lblNombre.setTextFill(Color.WHITE);

        lblSaldo = new Label("Fichas: " + jugador.getSaldoFichas());
        lblSaldo.setTextFill(Color.GOLD);

        lblCartas = new Label("[ ?? ] [ ?? ]");
        lblCartas.setTextFill(Color.WHITE);

        HBox filaCartas = new HBox(lblCartas);
        filaCartas.setAlignment(Pos.CENTER);

        getChildren().addAll(lblNombre, lblSaldo, filaCartas);
    }

    /** Sincroniza el panel con el estado actual del Jugador (llamar después de cada acción). */
    public void actualizarDesde(Jugador jugador) {
        lblSaldo.setText("Fichas: " + jugador.getSaldoFichas());
        if (!jugador.isActivo()) {
            lblNombre.setText(jugador.getNombre() + " (retirado)");
        } else {
            lblNombre.setText(jugador.getNombre());
        }
    }

    /** Actualiza las cartas visibles del jugador (por ahora como texto simple, ej. "A♠ K♦"). */
    public void actualizarCartas(String cartasTexto) {
        lblCartas.setText(cartasTexto);
    }

    public String getNombre() {
        return lblNombre.getText();
    }
}