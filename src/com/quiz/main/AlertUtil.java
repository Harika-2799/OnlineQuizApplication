package com.quiz.main;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertUtil {

	 public static void info(String msg){ show(AlertType.INFORMATION, msg); }
	    public static void warn(String msg){ show(AlertType.WARNING, msg); }
	    public static void error(String msg){ show(AlertType.ERROR, msg); }

	    private static void show(AlertType type, String msg){
	        Alert a = new Alert(type, msg);
	        a.setHeaderText(null);
	        a.showAndWait();
	    }
}
