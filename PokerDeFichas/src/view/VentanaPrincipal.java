package view;

import model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;
import javafx.application.Platform;
import java.util.*;

/**
 * Ventana principal de la aplicación, conectada a la lógica real de JuegoPoker.
 *  - Arriba: los dos bots + etiqueta de turno actual
 *  - Centro: la mesa (cartas comunitarias + pozo)
 *  - Abajo: el jugador humano y los controles de apuesta
 */
public class VentanaPrincipal extends Application {

    private JuegoPoker juego;
    private JugadorHumano jugadorHumano;
    private JugadorBot bot1;
    private JugadorBot bot2;

    private PanelJugador panelHumano;
    private PanelJugador panelBot1;
    private PanelJugador panelBot2;
    private PanelMesa panelMesa;
    private PanelControles controles;
    private Label lblTurno;
    private Label lblNarracion;
    private int ultimoIndiceHistorial = 0;
    private static final Duration PAUSA_ENTRE_JUGADAS = Duration.seconds(2.0);

    @Override
    public void start(Stage stage) {
        jugadorHumano = new JugadorHumano("Jugador", 500);
        bot1 = new JugadorBot("Bot Conservador", 500, new BotConservador());
        bot2 = new JugadorBot("Bot Agresivo", 500, new BotAgresivo());

        juego = new JuegoPoker(List.of(jugadorHumano, bot1, bot2));

        panelMesa = new PanelMesa();

        panelBot1 = new PanelJugador(bot1);
        panelBot2 = new PanelJugador(bot2);
        HBox filaBots = new HBox(20, panelBot1, panelBot2);
        filaBots.setAlignment(Pos.CENTER);

        lblTurno = new Label("Turno de: -");
        lblTurno.setTextFill(Color.WHITE);
        lblTurno.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        lblNarracion = new Label(" ");
        lblNarracion.setTextFill(Color.LIGHTYELLOW);
        lblNarracion.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        VBox arriba = new VBox(6, filaBots, lblTurno, lblNarracion);
        arriba.setAlignment(Pos.CENTER);
        arriba.setPadding(new Insets(10));

        panelHumano = new PanelJugador(jugadorHumano);
        controles = new PanelControles();
        controles.setOnAccion(this::onAccionHumana);

        Button btnNuevaRonda = new Button("Nueva ronda");
        btnNuevaRonda.setOnAction(e -> iniciarNuevaRonda());

        HBox filaHumano = new HBox(20, panelHumano, controles, btnNuevaRonda);
        filaHumano.setAlignment(Pos.CENTER);
        filaHumano.setPadding(new Insets(10));

        BorderPane raiz = new BorderPane();
        raiz.setTop(arriba);
        raiz.setCenter(panelMesa);
        raiz.setBottom(filaHumano);
        raiz.setStyle("-fx-background-color: #0a3d24;");

        Scene escena = new Scene(raiz, 850, 680);
        stage.setTitle("Póker de Fichas");
        stage.setScene(escena);
        stage.show();

        iniciarNuevaRonda();
    }

    private void iniciarNuevaRonda() {
        ultimoIndiceHistorial = 0;
        juego.iniciarRonda();
        narrarNuevasJugadas(this::refrescarUI);
    }

    private void onAccionHumana(AccionPoker accion, int monto) {
        Map<Jugador, Integer> saldosAntes = capturarSaldos();
        try {
            juego.procesarAccion(jugadorHumano, accion, monto);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        narrarNuevasJugadas(() -> {
            refrescarUI();
            revisarFinDeRonda(saldosAntes);
        });
    }

    /**
     * Muestra, una por una y con pausa entre cada una, las jugadas de los BOTS que
     * ocurrieron desde la última vez que se revisó el historial (la del jugador humano
     * no se narra porque ya se ve reflejada en sus propios botones). Al terminar,
     * ejecuta alTerminar (normalmente refrescarUI + revisar fin de ronda).
     */
    private void narrarNuevasJugadas(Runnable alTerminar) {
        List<RegistroAccion> historialCompleto = juego.getHistorial();
        List<RegistroAccion> nuevas = historialCompleto.subList(ultimoIndiceHistorial, historialCompleto.size());
        ultimoIndiceHistorial = historialCompleto.size();

        List<RegistroAccion> jugadasDeBots = nuevas.stream()
                .filter(r -> r.jugador() instanceof JugadorBot)
                .toList();

        if (jugadasDeBots.isEmpty()) {
            if (alTerminar != null) alTerminar.run();
            return;
        }

        controles.setDisable(true); // que no se pueda jugar mientras se narra
        SequentialTransition secuencia = new SequentialTransition();
        for (RegistroAccion registro : jugadasDeBots) {
            PauseTransition pausa = new PauseTransition(PAUSA_ENTRE_JUGADAS);
            pausa.setOnFinished(e -> lblNarracion.setText(describirJugada(registro)));
            secuencia.getChildren().add(pausa);
        }

        // Pausa extra SOLO para que el último mensaje también se alcance a leer;
        // si no, se muestra y se limpia en el mismo instante (bug: nunca se ve
        // la jugada del último bot en actuar).
        secuencia.getChildren().add(new PauseTransition(PAUSA_ENTRE_JUGADAS));
        secuencia.setOnFinished(e -> {

            lblNarracion.setText(" ");
            if (alTerminar != null) {
                Platform.runLater(alTerminar);
            }
        });
        secuencia.play();
    }

        /** Arma el texto explicativo de una jugada de bot, para que se entienda qué y por qué. */
        private String describirJugada(RegistroAccion registro) {
            String nombre = registro.jugador().getNombre();
            return switch (registro.accion()) {
                case CHECK -> nombre + " pasa (check): no hay apuesta que igualar.";
                case CALL -> nombre + " iguala (call) con " + registro.monto() + " fichas.";
                case RAISE -> nombre + " sube (raise) la apuesta a " + registro.monto() + " fichas.";
                case FOLD -> nombre + " se retira (fold): la apuesta le pareció demasiado alta.";
            };
        }

    private Map<Jugador, Integer> capturarSaldos() {
        Map<Jugador, Integer> saldos = new HashMap<>();
        for (Jugador j : juego.getJugadores()) {
            saldos.put(j, j.getSaldoFichas());
        }
        return saldos;
    }

    /** Sincroniza todos los paneles (incluida la etiqueta de turno) con el estado actual de JuegoPoker. */
    private void refrescarUI() {
        panelHumano.actualizarDesde(jugadorHumano);
        panelBot1.actualizarDesde(bot1);
        panelBot2.actualizarDesde(bot2);
        panelMesa.actualizarPozo(juego.getBote());

        // Solo se muestran las cartas del jugador humano; las de los bots quedan ocultas.
        List<Carta> misCartas = juego.getCartasPrivadas(jugadorHumano);
        if (!misCartas.isEmpty()) {
            String textoCartas = misCartas.stream().map(Carta::toString)
                    .reduce((a, b) -> a + "  " + b).orElse("");
            panelHumano.actualizarCartas(textoCartas);
        }

        List<Carta> comunitarias = juego.getCartasComunitarias();
        String[] textos = comunitarias.stream().map(Carta::toString).toArray(String[]::new);
        panelMesa.actualizarCartasComunitarias(textos);

        controles.setApuestaActual(juego.getApuestaActual());
        controles.setSaldoDisponible(jugadorHumano.getSaldoFichas());

        Jugador actual = juego.getJugadorActual();
        lblTurno.setText(actual != null ? "Turno de: " + actual.getNombre() : "Ronda terminada");

        boolean esTurnoHumano = actual == jugadorHumano;
        controles.setDisable(!esTurnoHumano);
    }

    /**
     * Revisa si la ronda terminó y muestra el resultado.
     * Hay dos caminos porque JuegoPoker maneja ambos casos de forma distinta:
     *  - SHOWDOWN: hay que llamar resolverRonda() para evaluar manos (sí sabemos la combinación ganadora).
     *  - Retiro de los demás: el bote ya se asignó dentro de JuegoPoker; se detecta comparando
     *    el saldo de cada jugador antes/después de la acción (no hay combinación que mostrar).
     */
    private void revisarFinDeRonda(Map<Jugador, Integer> saldosAntes) {
        if (!juego.isRondaTerminada()) {
            return;
        }

        if ("SHOWDOWN".equals(juego.getFaseActual())) {
            ResultadoRonda resultado = juego.resolverRonda();
            if (resultado != null) {
                mostrarFinDeRonda(resultado.getGanador().getNombre(),
                        resultado.getFichasGanadas(), resultado.getCombinacionGanadora());
            }
        } else {
            Jugador ganador = null;
            int fichasGanadas = 0;
            for (Jugador j : juego.getJugadores()) {
                int antes = saldosAntes.getOrDefault(j, j.getSaldoFichas());
                int despues = j.getSaldoFichas();
                if (despues > antes) {
                    ganador = j;
                    fichasGanadas = despues - antes;
                }
            }
            String nombreGanador = ganador != null ? ganador.getNombre() : "Nadie";
            mostrarFinDeRonda(nombreGanador, fichasGanadas, null);
        }
        refrescarUI();
    }

    /**
     * Arma y muestra el mensaje de fin de ronda: quién ganó, cuántas fichas, y el ranking
     * completo de combinaciones de póker de mayor a menor poder, señalando cuál fue la ganadora.
     * Si combinacionGanadora es null (se ganó por retiro de los demás), se muestra el ranking
     * completo igual mas sin marcar ninguna, porque no hubo comparación de manos.
     */
    private void mostrarFinDeRonda(String nombreGanador, int fichasGanadas, CombinacionPoker combinacionGanadora) {
        StringBuilder mensaje = new StringBuilder();
        if (combinacionGanadora != null) {
            mensaje.append(nombreGanador).append(" gana la ronda con ")
                    .append(nombreLegible(combinacionGanadora))
                    .append(" y se lleva ").append(fichasGanadas).append(" fichas.\n\n");
        } else {
            mensaje.append(nombreGanador).append(" gana la ronda (los demás se retiraron) y se lleva ")
                    .append(fichasGanadas).append(" fichas.\n\n");
        }

        mensaje.append("Ranking de manos (de más a menos poderosa):\n");
        List<CombinacionPoker> combinaciones = Arrays.asList(CombinacionPoker.values());
        Collections.reverse(combinaciones); // de ESCALERA_REAL a CARTA_ALTA
        for (CombinacionPoker combinacion : combinaciones) {
            boolean esGanadora = combinacion == combinacionGanadora;
            mensaje.append(esGanadora ? "⭐ " : "    ")
                    .append(nombreLegible(combinacion))
                    .append(esGanadora ? "   ← GANADORA" : "")
                    .append("\n");
        }

        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensaje.toString());
        alerta.setHeaderText("Fin de la ronda");
        alerta.getDialogPane().setPrefWidth(380);
        alerta.showAndWait();
    }

    /** Nombre en español y con acentos para mostrar cada combinación en la GUI. */
    private String nombreLegible(CombinacionPoker combinacion) {
        return switch (combinacion) {
            case CARTA_ALTA -> "Carta Alta";
            case PAR -> "Par";
            case DOS_PARES -> "Dos Pares";
            case TRIO -> "Trío";
            case ESCALERA -> "Escalera";
            case COLOR -> "Color";
            case FULL -> "Full";
            case POKER -> "Póker";
            case ESCALERA_COLOR -> "Escalera de Color";
            case ESCALERA_REAL -> "Escalera Real";
        };
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setHeaderText("Acción no válida");
        alerta.showAndWait();
    }
}