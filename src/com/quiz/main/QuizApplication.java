package com.quiz.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class QuizApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Welcome to the Quiz App!");
        Scene scene = new Scene(label, 400, 200);
        scene.getStylesheets().add(QuizApplication.class.getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Quiz App");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
