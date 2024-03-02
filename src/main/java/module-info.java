module ir.map.socialnetworkapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens ir.map.socialnetworkapp to javafx.fxml;
    opens ir.map.socialnetworkapp.Domain to javafx.base;
    exports ir.map.socialnetworkapp;
}