package view;

import model.AccionPoker;
import util.ApuestaInvalidaException;
import util.SaldoInsuficienteException;
import util.Validaciones;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Botones de acción del jugador humano: Check, Call, Raise, Fold.
 * Usa el enum AccionPoker real (entregado por Persona 1: CHECK, CALL, FOLD, RAISE).
 *
 * Nota: el enum no tiene un valor separado para "apuesta inicial" (BET); cuando
 * apuestaActual es 0, un RAISE se comporta como la apuesta de apertura.
 */
public class PanelControles extends HBox {

    /** Se notifica cada vez que el jugador confirma una acción válida. */
    public interface OnAccion {
        void ejecutar(AccionPoker accion, int monto);
    }

    private final TextField campoMonto;
    private final Button btnCheck;
    private final Button btnCall;
    private OnAccion listener;

    private int saldoDisponible = 0;
    private int apuestaActual = 0;

    public PanelControles() {
        setSpacing(10);
        setPadding(new Insets(12));
        setAlignment(Pos.CENTER);

        campoMonto = new TextField();
        campoMonto.setPromptText("Monto");
        campoMonto.setPrefWidth(80);

        btnCheck = new Button("Check");
        btnCall = new Button("Call");
        Button btnRaise = new Button("Raise");
        Button btnFold = new Button("Fold");

        btnCheck.setOnAction(e -> confirmarCheck());
        btnCall.setOnAction(e -> confirmarCall());
        btnRaise.setOnAction(e -> confirmarRaise());
        btnFold.setOnAction(e -> confirmarFold());

        getChildren().addAll(campoMonto, btnCheck, btnCall, btnRaise, btnFold);
    }

    /** Registra quién debe enterarse cuando el jugador confirme una acción válida. */
    public void setOnAccion(OnAccion listener) {
        this.listener = listener;
    }

    /** Debe llamarse cada vez que cambie el saldo del jugador humano en turno. */
    public void setSaldoDisponible(int saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    /**
     * Debe llamarse cada vez que cambie la apuesta más alta vigente en la ronda.
     * También habilita/deshabilita Check y Call según corresponda:
     * Check solo es válido si apuestaActual es 0; Call no aplica si apuestaActual es 0
     * (no hay nada que igualar todavía).
     */
    public void setApuestaActual(int apuestaActual) {
        this.apuestaActual = apuestaActual;
        btnCheck.setDisable(apuestaActual != 0);
        btnCall.setDisable(apuestaActual == 0);
    }

    private void confirmarCheck() {
        if (apuestaActual != 0) {
            mostrarError("No podés hacer check: ya hay una apuesta de " + apuestaActual + " en la mesa.");
            return;
        }
        notificar(AccionPoker.CHECK, 0);
    }

    private void confirmarCall() {
        if (apuestaActual == 0) {
            mostrarError("No hay ninguna apuesta que igualar todavía.");
            return;
        }
        try {
            Validaciones.validarSaldoSuficiente(apuestaActual, saldoDisponible);
        } catch (SaldoInsuficienteException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        notificar(AccionPoker.CALL, apuestaActual);
    }

    private void confirmarRaise() {
        int monto;
        try {
            monto = Integer.parseInt(campoMonto.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarError("Ingresá un monto numérico válido.");
            return;
        }

        try {
            if (apuestaActual == 0) {
                // Sin apuesta previa, un "raise" funciona como la apuesta de apertura.
                Validaciones.validarApuesta(monto, saldoDisponible);
            } else {
                Validaciones.validarSubida(monto, apuestaActual, saldoDisponible);
            }
        } catch (ApuestaInvalidaException | SaldoInsuficienteException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        notificar(AccionPoker.RAISE, monto);
    }

    private void confirmarFold() {
        notificar(AccionPoker.FOLD, 0);
    }

    private void notificar(AccionPoker accion, int monto) {
        if (listener != null) {
            listener.ejecutar(accion, monto);
        } else {
            // Mientras JuegoPoker no esté conectado, esto sirve para probar el panel solo.
            System.out.println("Acción: " + accion + " | Monto: " + monto);
        }
        campoMonto.clear();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setHeaderText("Acción no válida");
        alerta.showAndWait();
    }
}