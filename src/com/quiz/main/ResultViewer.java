package com.quiz.main;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class ResultViewer {

	 public static void showResults(Stage stage, String username) {
	        TableView<Result> table = new TableView<>();
	        ObservableList<Result> data = FXCollections.observableArrayList();

	        TableColumn<Result, String> quizCol = new TableColumn<>("Quiz");
	        quizCol.setCellValueFactory(c -> c.getValue().quizTitleProperty());

	        TableColumn<Result, Integer> scoreCol = new TableColumn<>("Score");
	        scoreCol.setCellValueFactory(c -> c.getValue().scoreProperty().asObject());

	        TableColumn<Result, Integer> totalCol = new TableColumn<>("Total");
	        totalCol.setCellValueFactory(c -> c.getValue().totalProperty().asObject());

	        table.getColumns().addAll(quizCol, scoreCol, totalCol);
	        table.setItems(data);

	        try (Connection c = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/quiz_app", "root", "Harika@27111999");
	             PreparedStatement ps = c.prepareStatement(
	                     "SELECT qa.*, q.title FROM quiz_attempts qa JOIN quizzes q ON qa.quiz_id = q.quiz_id WHERE qa.username = ?")) {
	            ps.setString(1, username);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	                data.add(new Result(
	                        rs.getString("title"),
	                        rs.getInt("score"),
	                        rs.getInt("total_questions")
	                ));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        Button backBtn = new Button("Back to Dashboard");
	        backBtn.setOnAction(e -> {
	            UserDashboard.launchUser(stage, username); // 👈 Go back to user dashboard
	        });
	        VBox root = new VBox(10, new Label("Your Quiz Attempts:"), table,backBtn);
	        root.setAlignment(Pos.CENTER);
	        root.setPadding(new Insets(20));
	        Scene scene = new Scene(root, 400, 300);
	        //scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
	        stage.setScene(scene);
	        scene.getStylesheets().add(ResultViewer.class.getResource("/style.css").toExternalForm());
	        stage.setTitle("My Attempts");
	        stage.show();
	    }

	    // ── Inner Class
	    public static class Result {
	        private final SimpleStringProperty quizTitle;
	        private final SimpleIntegerProperty score;
	        private final SimpleIntegerProperty total;

	        public Result(String quizTitle, int score, int total) {
	            this.quizTitle = new SimpleStringProperty(quizTitle);
	            this.score = new SimpleIntegerProperty(score);
	            this.total = new SimpleIntegerProperty(total);
	        }

	        public SimpleStringProperty quizTitleProperty() { return quizTitle; }
	        public SimpleIntegerProperty scoreProperty() { return score; }
	        public SimpleIntegerProperty totalProperty() { return total; }
	    }
}
