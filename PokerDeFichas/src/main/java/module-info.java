module cr.ac.una.est.pokerdefichas {
    requires javafx.controls;
    requires javafx.fxml;

    // "view" se abre a javafx.fxml por si más adelante cargan pantallas
    // desde archivos .fxml (reflexión). Si nunca usan FXML, "opens" no
    // hace daño dejarlo, pero "exports" sí es necesario para que
    // javafx pueda lanzar la clase Application (VentanaPrincipal).
    exports view;
    opens view to javafx.fxml;
}