package com.gudusoft.format.util;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * CemB
 */
public class BouncedUtil {

    public static void successBounced(String messages) {
        Alert alert = new Alert(Alert.AlertType.NONE, messages + " successful!", new ButtonType("ok", ButtonBar.ButtonData.YES));
        alert.show();
    }

    public static void failBounced(String messages) {
        Alert alert = new Alert(Alert.AlertType.NONE, messages, new ButtonType("ok", ButtonBar.ButtonData.YES));
        alert.show();
    }


}
