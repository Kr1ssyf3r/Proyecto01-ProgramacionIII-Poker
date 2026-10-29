package view;

import dao.EstadisticasDAO;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.AccionDetalle;
import model.FilaRanking;
import model.ManoResumen;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Ventana del historial: tabla de manos jugadas, detalle de acciones de la mano
 * seleccionada y estadísticas (ranking de victorias, mayor bote y mejor bot).
 * Los datos se leen de MySQL en un hilo aparte (Task) y se muestran con Platform.runLater.
 */
public class HistorialVista {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final EstadisticasDAO dao = new EstadisticasDAO();

    private final ObservableList<ManoResumen> manos = FXCollections.observableArrayList();
    private final ObservableList<AccionDetalle> acciones = FXCollections.observableArrayList();
    private final ObservableList<FilaRanking> ranking = FXCollections.observableArrayList();

    private final TableView<ManoResumen> tablaManos = new TableView<>(manos);
    private final Label lblMayorBote = new Label("-");
    private final Label lblMejorBot = new Label("-");
    private final Label lblEstado = new Label();

    /** Datos que se cargan juntos desde la base de datos. */
    private record Datos(List<ManoResumen> manos, List<FilaRanking> ranking,
                         Optional<ManoResumen> mayorBote, Optional<FilaRanking> mejorBot) { }

    /** Abre la ventana del historial y carga los datos. */
    public void mostrar() {
        Stage stage = new Stage();
        stage.setTitle("Historial y estadísticas");
        stage.setScene(new Scene(construirRaiz(), 980, 620));
        stage.show();
        cargarTodo();
    }

    private VBox construirRaiz() {
        configurarTablaManos();
        TableView<AccionDetalle> tablaAcciones = crearTablaAcciones();
        TableView<FilaRanking> tablaRanking = crearTablaRanking();

        tablaManos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, mano) -> {
            if (mano == null) {
                acciones.clear();
            } else {
                cargarAcciones(mano.id());
            }
        });

        Label tituloManos = titulo("Manos jugadas");
        Label tituloAcciones = titulo("Acciones de la mano seleccionada");
        VBox.setVgrow(tablaManos, Priority.ALWAYS);
        VBox.setVgrow(tablaAcciones, Priority.ALWAYS);
        VBox izquierda = new VBox(6, tituloManos, tablaManos, tituloAcciones, tablaAcciones);
        HBox.setHgrow(izquierda, Priority.ALWAYS);

        Label tituloRanking = titulo("Ranking de victorias");
        VBox.setVgrow(tablaRanking, Priority.ALWAYS);
        VBox derecha = new VBox(6,
                tituloRanking, tablaRanking,
                titulo("Mayor bote"), lblMayorBote,
                titulo("Bot con más victorias"), lblMejorBot);
        derecha.setPrefWidth(300);
        derecha.setMinWidth(260);

        Button btnActualizar = new Button("Actualizar");
        btnActualizar.setOnAction(e -> cargarTodo());
        lblEstado.setStyle("-fx-text-fill: #b00020;");
        HBox barra = new HBox(12, btnActualizar, lblEstado);

        HBox centro = new HBox(14, izquierda, derecha);
        VBox.setVgrow(centro, Priority.ALWAYS);

        VBox raiz = new VBox(10, barra, centro);
        raiz.setPadding(new Insets(12));
        return raiz;
    }

    private void configurarTablaManos() {
        tablaManos.setPlaceholder(new Label("Todavía no hay manos guardadas."));
        tablaManos.getColumns().add(columna("ID", ManoResumen::id, 55));
        tablaManos.getColumns().add(columna("Fecha", (ManoResumen m) -> m.fecha().format(FORMATO_FECHA), 140));
        tablaManos.getColumns().add(columna("Bote", ManoResumen::bote, 70));
        tablaManos.getColumns().add(columna("Combinación", ManoResumen::combinacion, 150));
        tablaManos.getColumns().add(columna("Ganador", ManoResumen::ganador, 130));
    }

    private TableView<AccionDetalle> crearTablaAcciones() {
        TableView<AccionDetalle> tabla = new TableView<>(acciones);
        tabla.setPlaceholder(new Label("Selecciona una mano para ver sus acciones."));
        tabla.getColumns().add(columna("Orden", AccionDetalle::orden, 70));
        tabla.getColumns().add(columna("Jugador", AccionDetalle::jugador, 160));
        tabla.getColumns().add(columna("Acción", AccionDetalle::tipo, 100));
        tabla.getColumns().add(columna("Monto", AccionDetalle::monto, 80));
        return tabla;
    }

    private TableView<FilaRanking> crearTablaRanking() {
        TableView<FilaRanking> tabla = new TableView<>(ranking);
        tabla.setPlaceholder(new Label("Sin victorias registradas."));
        tabla.getColumns().add(columna("Jugador", FilaRanking::nombre, 170));
        tabla.getColumns().add(columna("Victorias", FilaRanking::victorias, 90));
        return tabla;
    }

    /** Crea una columna que muestra el valor devuelto por la función (sirve con records). */
    private static <S, T> TableColumn<S, T> columna(String titulo, Function<S, T> valor, double ancho) {
        TableColumn<S, T> col = new TableColumn<>(titulo);
        col.setCellValueFactory(celda -> new ReadOnlyObjectWrapper<>(valor.apply(celda.getValue())));
        col.setPrefWidth(ancho);
        return col;
    }

    private static Label titulo(String texto) {
        Label l = new Label(texto);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        return l;
    }

    /** Carga manos y estadísticas en un hilo aparte para no congelar la ventana. */
    private void cargarTodo() {
        lblEstado.setText("Cargando...");
        Task<Datos> tarea = new Task<>() {
            @Override
            protected Datos call() throws Exception {
                Datos datos = new Datos(dao.listarManos(), dao.rankingVictorias(),
                        dao.mayorBote(), dao.botConMasVictorias());
                Platform.runLater(() -> aplicar(datos));
                return datos;
            }
        };
        tarea.setOnFailed(e -> mostrarError(tarea.getException()));
        iniciar(tarea);
    }

    /** Carga las acciones de una mano; si el usuario ya cambió de mano, descarta el resultado. */
    private void cargarAcciones(int manoId) {
        Task<List<AccionDetalle>> tarea = new Task<>() {
            @Override
            protected List<AccionDetalle> call() throws Exception {
                List<AccionDetalle> lista = dao.listarAcciones(manoId);
                Platform.runLater(() -> {
                    ManoResumen elegida = tablaManos.getSelectionModel().getSelectedItem();
                    if (elegida != null && elegida.id() == manoId) {
                        acciones.setAll(lista);
                    }
                });
                return lista;
            }
        };
        tarea.setOnFailed(e -> mostrarError(tarea.getException()));
        iniciar(tarea);
    }

    private void aplicar(Datos datos) {
        manos.setAll(datos.manos());
        acciones.clear();
        ranking.setAll(datos.ranking());
        lblMayorBote.setText(datos.mayorBote()
                .map(m -> m.bote() + " fichas (mano #" + m.id() + ", ganó " + m.ganador() + ")")
                .orElse("-"));
        lblMejorBot.setText(datos.mejorBot()
                .map(b -> b.nombre() + " (" + b.victorias() + " victorias)")
                .orElse("-"));
        lblEstado.setText("");
    }

    private void mostrarError(Throwable error) {
        lblEstado.setText("No se pudo leer la base de datos: " + error.getMessage());
    }

    private static void iniciar(Task<?> tarea) {
        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }
}
