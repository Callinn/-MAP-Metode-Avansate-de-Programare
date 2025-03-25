module com.example.map_lab6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.media;
    requires eu.hansolo.tilesfx;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    opens com.example.map_lab6.src.controller to javafx.fxml;

    opens com.example.map_lab6.src to javafx.fxml;
    exports com.example.map_lab6.src;
    exports com.example.map_lab6.src.controller;
    exports com.example.map_lab6.src.domain;
}