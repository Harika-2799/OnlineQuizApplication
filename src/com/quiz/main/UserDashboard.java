package com.quiz.main;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.quiz.main.Leaderboard;


import java.sql.*;

public class UserDashboard {

	 public static void launchUser(Stage primaryStage, String username) {
	        ObservableList<QuizItem> quizzes = FXCollections.observableArrayList();

	        try (Connection c = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/quiz_app", "root", "Harika@27111999");
	             Statement s = c.createStatement();
	             ResultSet rs = s.executeQuery("SELECT quiz_id, title FROM quizzes")) {
	            while (rs.next()) {
	                quizzes.add(new QuizItem(rs.getInt(1), rs.getString(2)));
	            }
	        } catch (SQLException e) { e.printStackTrace(); }

	        ListView<QuizItem> list = new ListView<>(quizzes);
	        list.setPrefHeight(200);

	        Button takeQuizBtn = new Button("Take Quiz");
	        Button viewResultsBtn = new Button("My Attempts");
	        Button leaderboardBtn = new Button("Leaderboard");

	        takeQuizBtn.setOnAction(e -> {
	            QuizItem selected = list.getSelectionModel().getSelectedItem();
	            
	            // ✅ 1. Nothing selected → show warning and return
	            if (selected == null) {
	                Alert alert = new Alert(Alert.AlertType.WARNING);
	                alert.setTitle("No Quiz Selected");
	                alert.setHeaderText(null);
	                alert.setContentText("⚠️ Please select a quiz before clicking 'Take Quiz'.");
	                alert.showAndWait();
	                return;  // stop here
	            }

	            // ✅ 2. A quiz is selected → proceed
	            QuizTaker.startQuiz(primaryStage,
	                                selected.getId(),
	                                selected.getTitle(),
	                                username);
	        });


	        viewResultsBtn.setOnAction(e -> {
	            ResultViewer.showResults(primaryStage, username);
	        });
	        
	        leaderboardBtn.setOnAction(e -> Leaderboard.show(primaryStage));


	        
	        VBox root = new VBox(10, new Label("Available Quizzes:"), list, takeQuizBtn, viewResultsBtn,leaderboardBtn);
	        root.setPadding(new Insets(20));
	        root.setAlignment(Pos.CENTER);

	        Scene scene = new Scene(root, 400, 350);
	        scene.getStylesheets().add(UserDashboard.class.getResource("/style.css").toExternalForm());
	       // scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
	        primaryStage.setTitle("Quiz Dashboard - " + username);
	        primaryStage.setScene(scene);
	        primaryStage.show();

	    }
	 
//	 Button leaderboardBtn = new Button("Leaderboard");
//	 leaderboardBtn.setOnAction(e -> Leaderboard.show(primaryStage, username));
//	 VBox root = new VBox(10,
//	         new Label("Available Quizzes:"), list,
//	         takeQuizBtn, viewResultsBtn, leaderboardBtn);


	    public static class QuizItem {
	        private final int id;
	        private final String title;

	        public QuizItem(int id, String title) {
	            this.id = id;
	            this.title = title;
	        }

	        public int getId() { return id; }
	        public String getTitle() { return title; }

	        @Override
	        public String toString() {
	            return title;
	        }
	    }
}
