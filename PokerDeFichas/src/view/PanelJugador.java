package view;

import model.Carta;
import model.Jugador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Panel que muestra la información de UN jugador en la mesa:
 * su nombre, sus fichas (saldo) y sus cartas (con imágenes reales).
 *
 * Las cartas arrancan mostrando el reverso (oculto). El jugador humano las
 * revela siempre (mostrarCartas), y los bots solo al final de la ronda,
 * cuando VentanaPrincipal llama a mostrarCartas() con sus cartas privadas;
 * ocultarCartas() las vuelve a tapar al iniciar la siguiente ronda.
 */
public class PanelJugador extends VBox {

    private static final double ANCHO_CARTA = 60;
    private static final double ALTO_CARTA = 85;

    private final Label lblNombre;
    private final Label lblSaldo;
    private final ImageView imgCarta1;
    private final ImageView imgCarta2;

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

        imgCarta1 = crearImageView();
        imgCarta2 = crearImageView();
        ocultarCartas();

        HBox filaCartas = new HBox(4, imgCarta1, imgCarta2);
        filaCartas.setAlignment(Pos.CENTER);

        getChildren().addAll(lblNombre, lblSaldo, filaCartas);
    }

    private ImageView crearImageView() {
        ImageView iv = new ImageView();
        iv.setFitWidth(ANCHO_CARTA);
        iv.setFitHeight(ALTO_CARTA);
        return iv;
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

    /**
     * Muestra las cartas reales boca arriba. Se usa siempre para el jugador humano,
     * y para los bots solo al final de la ronda (showdown), para "revelar" su mano.
     */
    public void mostrarCartas(List<Carta> cartas) {
        if (cartas.size() > 0) imgCarta1.setImage(CartaImagenes.obtener(cartas.get(0)));
        if (cartas.size() > 1) imgCarta2.setImage(CartaImagenes.obtener(cartas.get(1)));
    }

    /** Vuelve a tapar ambas cartas con el reverso (para ocultar la mano de un bot). */
    public void ocultarCartas() {
        imgCarta1.setImage(CartaImagenes.obtenerReverso());
        imgCarta2.setImage(CartaImagenes.obtenerReverso());
    }

    public String getNombre() {
        return lblNombre.getText();
    }
}
