package view;

import juego.BotAgresivo;
import juego.BotConservador;
import juego.JuegoPoker;
import servicio.GuardadorMano;
import model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
    private final GuardadorMano guardadorMano = new GuardadorMano();
    /** Fichas de cada jugador al EMPEZAR la ronda (para la tabla participacion). */
    private Map<Jugador, Integer> saldosInicioRonda = new HashMap<>();
    /** Evita guardar dos veces la misma mano. */
    private boolean manoGuardada = false;
    /** Pausa utilizada para narrar las jugadas de los bots de forma visible. */
    private static final Duration PAUSA_ENTRE_JUGADAS = Duration.seconds(1.5);

/**
 * Configura los componentes de la interfaz y muestra la ventana principal de la aplicación.
 * @param stage ventana principal que entrega JavaFX.
 */
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

/**
 * Crea una nueva ronda y actualiza la interfaz con el estado inicial.
 */
    private void iniciarNuevaRonda() {
        ultimoIndiceHistorial = 0;
        saldosInicioRonda = capturarSaldos(); // ANTES de iniciarRonda(): son las fichas "antes" de la mano
        manoGuardada = false;
        panelBot1.ocultarCartas();
        panelBot2.ocultarCartas();
        juego.iniciarRonda();
        narrarNuevasJugadas(this::refrescarUI);
    }

/**
 * Recibe la acción seleccionada por el usuario y la envía a la lógica de la partida.
 * @param accion acción que el usuario eligió en los controles.
 * @param monto monto que ingresó el usuario (0 si la acción no lo requiere).
 */
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
 * Recorre las acciones recientes y las muestra progresivamente en la interfaz.
 * @param alTerminar acción que se ejecuta al terminar la narración (puede ser null).
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
            // Platform.runLater espera a que termine este ciclo de animación antes
            // de ejecutar alTerminar; si no, showAndWait() (dentro de mostrarFinDeRonda)
            // lanza IllegalStateException por llamarse "durante" la animación.
            if (alTerminar != null) {
                Platform.runLater(alTerminar);
            }
        });
        secuencia.play();
    }

/**
 * Convierte un registro de acción en un texto legible para la interfaz.
 * @param registro jugada que se quiere describir.
 * @return frase que describe la jugada.
 */
    private String describirJugada(RegistroAccion registro) {
        String nombre = registro.jugador().getNombre();
        return switch (registro.accion()) {
            case CHECK -> nombre + " pasa (check): no hay apuesta que igualar.";
            case CALL -> nombre + " iguala (call) con " + registro.monto() + " fichas.";
            case RAISE -> nombre + " sube (raise) la apuesta a " + registro.monto() + " fichas.";
            case FOLD -> nombre + " se retira (fold): la apuesta le pareció demasiado alta.";
        };
    }

/**
 * Captura los saldos de los jugadores antes de finalizar la ronda.
 * @return mapa con el saldo actual de cada jugador.
 */
    private Map<Jugador, Integer> capturarSaldos() {
        Map<Jugador, Integer> saldos = new HashMap<>();
        for (Jugador j : juego.getJugadores()) {
            saldos.put(j, j.getSaldoFichas());
        }
        return saldos;
    }

/**
 * Actualiza los distintos paneles de la interfaz con el estado actual de la partida.
 */
    private void refrescarUI() {
        panelHumano.actualizarDesde(jugadorHumano);
        panelBot1.actualizarDesde(bot1);
        panelBot2.actualizarDesde(bot2);
        panelMesa.actualizarPozo(juego.getBote());

        // Solo se muestran las cartas del jugador humano; las de los bots quedan ocultas
        // hasta el final de la ronda (ver revelarCartasBots()).
        List<Carta> misCartas = juego.getCartasPrivadas(jugadorHumano);
        if (!misCartas.isEmpty()) {
            panelHumano.mostrarCartas(misCartas);
        }

        panelMesa.actualizarCartasComunitarias(juego.getCartasComunitarias());

        controles.setApuestaActual(juego.getApuestaActual());
        controles.setSaldoDisponible(jugadorHumano.getSaldoFichas());

        Jugador actual = juego.getJugadorActual();
        lblTurno.setText(actual != null ? "Turno de: " + actual.getNombre() : "Ronda terminada");

        boolean esTurnoHumano = actual == jugadorHumano;
        controles.setDisable(!esTurnoHumano);
    }

/**
 * Comprueba si la ronda terminó y presenta el resultado al usuario cuando corresponde.
 * @param saldosAntes saldo de fichas de cada jugador antes de la acción.
 */
    private void revisarFinDeRonda(Map<Jugador, Integer> saldosAntes) {
        if (!juego.isRondaTerminada()) {
            return;
        }

        revelarCartasBots();

        if ("SHOWDOWN".equals(juego.getFaseActual())) {
            ResultadoRonda resultado = juego.resolverRonda();
            if (resultado != null) {
                guardarManoEnSegundoPlano(resultado.getCombinacionGanadora());
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
            guardarManoEnSegundoPlano(null); // null = nadie llegó al showdown
            String nombreGanador = ganador != null ? ganador.getNombre() : "Nadie";
            mostrarFinDeRonda(nombreGanador, fichasGanadas, null);
        }
        refrescarUI();
    }

/**
 * Guarda la mano terminada en la base de datos sin congelar la ventana.
 * La fase de "foto" ocurre aquí, en el hilo de JavaFX; solo el acceso a MySQL va en otro hilo (Task).
 * Si algo falla se avisa en pantalla y la partida sigue.
 * @param combinacion combinación ganadora, o null si los demás se retiraron.
 */
    private void guardarManoEnSegundoPlano(CombinacionPoker combinacion) {
        if (manoGuardada) return;
        manoGuardada = true;

        Jugador ganador = juego.getGanadorUltimaRonda();
        if (ganador == null) return; // no hubo reparto de bote: no hay nada que guardar

        // 1) Foto de los datos (hilo de JavaFX): después de esto el Task ya no toca objetos del juego.
        DatosMano datos = construirDatosMano(ganador, combinacion);

        // 2) Tarea en segundo plano
        Task<Integer> tarea = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return guardadorMano.guardar(datos);
            }
        };
        // 3) Si falla, JavaFX ejecuta esto de vuelta en el hilo de la interfaz (seguro para mostrar alertas)
        tarea.setOnFailed(e -> mostrarErrorGuardado(tarea.getException()));

        new Thread(tarea, "guardar-mano").start();
    }

/**
 * Convierte el estado actual del juego en un DatosMano inmutable.
 * @param ganador jugador que ganó la mano.
 * @param combinacion combinación ganadora, o null si nadie llegó al showdown.
 * @return datos listos para guardar.
 */
    private DatosMano construirDatosMano(Jugador ganador, CombinacionPoker combinacion) {
        List<DatosMano.Participacion> participaciones = new ArrayList<>();
        for (Jugador j : juego.getJugadores()) {
            int antes = saldosInicioRonda.getOrDefault(j, j.getSaldoFichas());
            String tipo = (j instanceof JugadorBot) ? "BOT" : "HUMANO";
            participaciones.add(new DatosMano.Participacion(j.getNombre(), tipo, antes, j.getSaldoFichas()));
        }

        List<DatosMano.Accion> acciones = new ArrayList<>();
        int orden = 1;
        for (RegistroAccion r : juego.getHistorial()) {
            acciones.add(new DatosMano.Accion(r.jugador().getNombre(), orden++, r.accion().name(), r.monto()));
        }

        String nombreCombinacion = (combinacion != null) ? combinacion.name() : "SIN_SHOWDOWN";
        return new DatosMano(juego.getBoteUltimaRonda(), nombreCombinacion, ganador.getNombre(),
                participaciones, acciones);
    }

/**
 * Avisa que no se pudo guardar la mano, sin cerrar la aplicación.
 * Usa show() y no showAndWait() para no bloquear nada mientras el usuario juega.
 * @param causa excepción que hizo fallar el guardado.
 */
    private void mostrarErrorGuardado(Throwable causa) {
        String detalle = (causa != null && causa.getMessage() != null) ? causa.getMessage() : "error desconocido";
        Alert alerta = new Alert(Alert.AlertType.WARNING,
                "No se pudo guardar la mano en la base de datos.\nLa partida puede continuar.\n\nDetalle: " + detalle);
        alerta.setHeaderText("Guardado de mano fallido");
        alerta.show();
    }

/**
 * Muestra las cartas de los jugadores bot cuando finaliza la ronda.
 */
    private void revelarCartasBots() {
        panelBot1.mostrarCartas(juego.getCartasPrivadas(bot1));
        panelBot2.mostrarCartas(juego.getCartasPrivadas(bot2));
    }

/**
 * Muestra un resumen visual del ganador, las fichas ganadas y la combinación obtenida.
 * @param nombreGanador nombre del jugador que ganó la ronda.
 * @param fichasGanadas fichas que se lleva el ganador.
 * @param combinacionGanadora combinación ganadora, o null si los demás jugadores se retiraron.
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

/**
 * Convierte el nombre interno de una combinación de póker en un texto amigable para el usuario.
 * @param combinacion combinación de póker que se quiere convertir en texto.
 * @return nombre en español de la combinación de póker.
 */
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

/**
 * Muestra un mensaje de error al usuario mediante un diálogo de JavaFX.
 * @param mensaje texto del error que se muestra al usuario.
 */
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setHeaderText("Acción no válida");
        alerta.showAndWait();
    }
}
