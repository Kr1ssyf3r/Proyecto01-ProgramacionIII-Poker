package main.java.view;

import dao.JugadorDAO;
import model.PerfilJugador;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Pantalla de administración y selección de perfiles de jugador. Muestra una tabla con
 * los perfiles guardados en MySQL y permite crear, editar, eliminar y elegir con cuál
 * perfil jugar. Toda operación de base de datos corre en un hilo aparte (Task) para que
 * la interfaz nunca se congele. Esta clase no contiene SQL: solo usa la interfaz JugadorDAO.
 */
public final class PerfilesVista extends BorderPane {

    /** Fichas con las que arranca un perfil nuevo o uno que se quedó sin fichas. */
    private static final int FICHAS_INICIALES = 500;

    private final JugadorDAO dao;
    private final Consumer<PerfilJugador> alSeleccionar;

    private final TableView<PerfilJugador> tabla = new TableView<>();
    private final ObservableList<PerfilJugador> datos = FXCollections.observableArrayList();
    private final Label lblEstado = new Label();
    private final ProgressIndicator indicador = new ProgressIndicator();
    private final BooleanProperty ocupado = new SimpleBooleanProperty(false);

    private final Button btnNuevo = new Button("Nuevo");
    private final Button btnEditar = new Button("Editar");
    private final Button btnEliminar = new Button("Eliminar");
    private final Button btnJugar = new Button("Jugar con este perfil");

    /**
     * Construye la pantalla y lanza de inmediato la carga de perfiles en un hilo aparte.
     * @param dao acceso a los datos de los perfiles.
     * @param alSeleccionar acción que se ejecuta con el perfil elegido al pulsar "Jugar"
     *                      (ahí se arranca la partida con las fichas del perfil).
     */
    public PerfilesVista(JugadorDAO dao, Consumer<PerfilJugador> alSeleccionar) {
        this.dao = dao;
        this.alSeleccionar = alSeleccionar;
        construirInterfaz();
        cargarPerfiles();
    }

    /**
     * Arma la tabla, los botones y la barra de estado, y conecta los eventos.
     */
    private void construirInterfaz() {
        TableColumn<PerfilJugador, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNombre()));
        colNombre.setPrefWidth(220);

        TableColumn<PerfilJugador, Number> colFichas = new TableColumn<>("Fichas");
        colFichas.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getFichas()));
        colFichas.setPrefWidth(120);

        tabla.getColumns().add(colNombre);
        tabla.getColumns().add(colFichas);
        tabla.setItems(datos);
        tabla.setPlaceholder(new Label("No hay perfiles. Cree uno con \"Nuevo\"."));

        Label titulo = new Label("Perfiles de jugador");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        indicador.setPrefSize(22, 22);
        indicador.visibleProperty().bind(ocupado);

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        HBox barra = new HBox(10, btnNuevo, btnEditar, btnEliminar, espacio,
                indicador, lblEstado, btnJugar);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(10, 0, 0, 0));

        btnNuevo.disableProperty().bind(ocupado);
        var sinSeleccion = tabla.getSelectionModel().selectedItemProperty().isNull();
        btnEditar.disableProperty().bind(sinSeleccion.or(ocupado));
        btnEliminar.disableProperty().bind(sinSeleccion.or(ocupado));
        btnJugar.disableProperty().bind(sinSeleccion.or(ocupado));

        btnNuevo.setOnAction(e -> alNuevo());
        btnEditar.setOnAction(e -> alEditar());
        btnEliminar.setOnAction(e -> alEliminar());
        btnJugar.setOnAction(e -> alJugar());

        setPadding(new Insets(15));
        setTop(titulo);
        BorderPane.setMargin(titulo, new Insets(0, 0, 10, 0));
        setCenter(tabla);
        setBottom(barra);
    }

    /**
     * Ejecuta un trabajo en un hilo aparte con un Task de JavaFX y devuelve el resultado
     * al hilo de la interfaz. Mientras corre, bloquea los botones y muestra el indicador;
     * si falla, muestra el mensaje en una alerta sin cerrar la aplicación.
     * @param trabajo operación de base de datos a ejecutar en segundo plano.
     * @param alExito acción que recibe el resultado, ya en el hilo de JavaFX.
     * @param mensajeEstado texto que se muestra mientras se espera.
     * @param <T> tipo del resultado que produce el trabajo.
     */
    private <T> void ejecutar(Callable<T> trabajo, Consumer<T> alExito, String mensajeEstado) {
        ocupado.set(true);
        lblEstado.setText(mensajeEstado);

        Task<T> tarea = new Task<>() {
            /**
             * Corre en el hilo de fondo e invoca la operación de base de datos.
             * @return el resultado del trabajo.
             * @throws Exception si la operación falla.
             */
            @Override
            protected T call() throws Exception {
                return trabajo.call();
            }
        };
        // setOnSucceeded y setOnFailed ya se ejecutan en el hilo de JavaFX
        tarea.setOnSucceeded(e -> {
            ocupado.set(false);
            lblEstado.setText("");
            alExito.accept(tarea.getValue());
        });
        tarea.setOnFailed(e -> {
            ocupado.set(false);
            lblEstado.setText("");
            Throwable causa = tarea.getException();
            mostrarError(causa.getMessage() != null ? causa.getMessage() : causa.toString());
        });

        Thread hilo = new Thread(tarea, "hilo-bd-perfiles");
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Carga todos los perfiles desde la base de datos en segundo plano y refresca la
     * tabla al terminar. Se llama automáticamente al crear la pantalla.
     */
    public void cargarPerfiles() {
        ejecutar(dao::listar, lista -> datos.setAll(lista), "Cargando perfiles...");
    }

    /**
     * Maneja el botón "Nuevo": pide los datos y crea el perfil.
     */
    private void alNuevo() {
        pedirDatos(null).ifPresent(nuevo ->
                ejecutar(() -> dao.crear(nuevo), creado -> {
                    datos.add(creado);
                    tabla.getSelectionModel().select(creado);
                }, "Guardando..."));
    }

    /**
     * Maneja el botón "Editar": pide los datos nuevos del perfil seleccionado y los
     * actualiza en la base de datos.
     */
    private void alEditar() {
        PerfilJugador original = tabla.getSelectionModel().getSelectedItem();
        if (original == null) {
            return;
        }
        pedirDatos(original).ifPresent(editado ->
                ejecutar(() -> dao.actualizar(editado), actualizado -> {
                    if (actualizado) {
                        datos.set(datos.indexOf(original), editado);
                        tabla.getSelectionModel().select(editado);
                    } else {
                        mostrarError("El perfil ya no existe. Se recargará la lista.");
                        cargarPerfiles();
                    }
                }, "Guardando..."));
    }

    /**
     * Maneja el botón "Eliminar": confirma con el usuario y borra el perfil.
     */
    private void alEliminar() {
        PerfilJugador seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }
        if (!confirmar("¿Eliminar el perfil \"" + seleccionado.getNombre()
                + "\"? Esta acción no se puede deshacer.")) {
            return;
        }
        ejecutar(() -> dao.eliminar(seleccionado.getId()), eliminado -> {
            if (!eliminado) {
                mostrarError("El perfil ya no existía.");
            }
            datos.remove(seleccionado);
        }, "Eliminando...");
    }

    /**
     * Maneja el botón "Jugar": entrega el perfil elegido a quien arranca la partida.
     * Si el perfil se quedó sin fichas, ofrece reiniciarlo con 500 (si no, la partida
     * lo dejaría fuera de todas las rondas y no podría jugar nunca más).
     */
    private void alJugar() {
        PerfilJugador seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }
        if (seleccionado.getFichas() > 0) {
            alSeleccionar.accept(seleccionado);
            return;
        }
        if (!confirmar("El perfil \"" + seleccionado.getNombre() + "\" se quedó sin fichas. "
                + "¿Reiniciarlo con " + FICHAS_INICIALES + " fichas para poder jugar?")) {
            return;
        }
        ejecutar(() -> dao.actualizarFichas(seleccionado.getId(), FICHAS_INICIALES), reiniciado -> {
            if (reiniciado) {
                seleccionado.setFichas(FICHAS_INICIALES);
                tabla.refresh();
                alSeleccionar.accept(seleccionado);
            } else {
                mostrarError("El perfil ya no existe. Se recargará la lista.");
                cargarPerfiles();
            }
        }, "Reiniciando fichas...");
    }

    /**
     * Muestra un diálogo con nombre y fichas, validando los datos antes de cerrarlo
     * (el diálogo no se cierra mientras haya errores).
     * @param existente perfil a editar, o null para crear uno nuevo.
     * @return el perfil armado con los datos escritos, o vacío si el usuario canceló.
     */
    private Optional<PerfilJugador> pedirDatos(PerfilJugador existente) {
        Dialog<PerfilJugador> dialogo = new Dialog<>();
        dialogo.setTitle(existente == null ? "Nuevo perfil" : "Editar perfil");
        dialogo.setHeaderText(null);

        ButtonType tipoGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(tipoGuardar, ButtonType.CANCEL);

        TextField txtNombre = new TextField(existente == null ? "" : existente.getNombre());
        TextField txtFichas = new TextField(String.valueOf(
                existente == null ? FICHAS_INICIALES : existente.getFichas()));
        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #c0392b;");

        GridPane rejilla = new GridPane();
        rejilla.setHgap(10);
        rejilla.setVgap(10);
        rejilla.setPadding(new Insets(15));
        rejilla.addRow(0, new Label("Nombre:"), txtNombre);
        rejilla.addRow(1, new Label("Fichas:"), txtFichas);
        rejilla.add(lblError, 0, 2, 2, 1);
        dialogo.getDialogPane().setContent(rejilla);

        Button botonGuardar = (Button) dialogo.getDialogPane().lookupButton(tipoGuardar);
        botonGuardar.addEventFilter(ActionEvent.ACTION, ev -> {
            String error = validar(txtNombre.getText(), txtFichas.getText());
            if (error != null) {
                lblError.setText(error);
                ev.consume();
            }
        });

        dialogo.setResultConverter(boton -> {
            if (boton != tipoGuardar) {
                return null;
            }
            int id = existente == null ? 0 : existente.getId();
            return new PerfilJugador(id, txtNombre.getText().trim(),
                    Integer.parseInt(txtFichas.getText().trim()));
        });
        return dialogo.showAndWait();
    }

    /**
     * Valida los textos escritos en el formulario (nombre no vacío de máximo 40
     * caracteres y fichas como entero mayor o igual a 0).
     * @param nombre texto escrito en el campo de nombre.
     * @param fichas texto escrito en el campo de fichas.
     * @return un mensaje de error, o null si todo es válido.
     */
    private String validar(String nombre, String fichas) {
        if (nombre == null || nombre.isBlank()) {
            return "El nombre no puede estar vacío.";
        }
        if (nombre.trim().length() > PerfilJugador.LARGO_MAXIMO_NOMBRE) {
            return "El nombre no puede pasar de " + PerfilJugador.LARGO_MAXIMO_NOMBRE + " caracteres.";
        }
        try {
            if (Integer.parseInt(fichas.trim()) < 0) {
                return "Las fichas no pueden ser negativas.";
            }
        } catch (NumberFormatException e) {
            return "Las fichas deben ser un número entero.";
        }
        return null;
    }

    /**
     * Pide una confirmación al usuario con un diálogo Aceptar/Cancelar.
     * @param mensaje pregunta que se muestra.
     * @return true si el usuario aceptó, false si canceló o cerró el diálogo.
     */
    private boolean confirmar(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.OK, ButtonType.CANCEL);
        alerta.setHeaderText(null);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    /**
     * Muestra una alerta de error al usuario.
     * @param mensaje texto a mostrar.
     */
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
