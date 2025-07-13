package com.quiz.main;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.sql.*;
import java.util.*;

public class QuizTaker {

	  private static List<Question> questions = new ArrayList<>();
	    private static int currentIndex = 0;
	    private static int score = 0;

	    public static void startQuiz(Stage stage, int quizId, String quizTitle, String username) {
	        loadQuestions(quizId);
	        if (questions.isEmpty()) {
	            Alert alert = new Alert(Alert.AlertType.INFORMATION);
	            alert.setTitle("Empty Quiz");
	            alert.setHeaderText(null);
	            alert.setContentText("⚠️ This quiz has no questions yet. Please try another quiz.");
	            alert.showAndWait();
	            UserDashboard.launchUser(stage, username);
	            return;
	        }
	        currentIndex = 0;
	        score = 0;

	        showQuestion(stage, quizId, quizTitle, username);
	    }

	    private static void loadQuestions(int quizId) {
	        questions.clear();
	        try (Connection c = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/quiz_app", "root", "Harika@27111999");
	             PreparedStatement ps = c.prepareStatement("SELECT * FROM questions WHERE quiz_id=?")) {
	            ps.setInt(1, quizId);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	                questions.add(new Question(
	                        rs.getString("question_text"),
	                        rs.getString("option_a"),
	                        rs.getString("option_b"),
	                        rs.getString("option_c"),
	                        rs.getString("option_d"),
	                        rs.getString("correct_option")
	                ));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    private static void showQuestion(Stage stage, int quizId, String quizTitle, String username) {
	        final Timeline[] timer = new Timeline[1]; // ✅ Use array to mutate inside lambdas

	        if (currentIndex >= questions.size()) {
	            int total = questions.size();
	            saveScore(username, quizId, score, total);

	            Alert a = new Alert(Alert.AlertType.INFORMATION);
	            a.setHeaderText("Quiz Completed!");
	            a.setContentText("Score: " + score + "/" + total);
	            a.showAndWait();

	            UserDashboard.launchUser(stage, username);
	            return;
	        }

	        Question q = questions.get(currentIndex);

	        Label qLabel = new Label("Q" + (currentIndex + 1) + ": " + q.text);
	        ToggleGroup group = new ToggleGroup();

	        RadioButton a = new RadioButton(q.optionA); a.setToggleGroup(group);
	        RadioButton b = new RadioButton(q.optionB); b.setToggleGroup(group);
	        RadioButton c = new RadioButton(q.optionC); c.setToggleGroup(group);
	        RadioButton d = new RadioButton(q.optionD); d.setToggleGroup(group);

	        Button submit = new Button("Submit");
	        Label feedback = new Label();
	        Label timerLabel = new Label("⏱️ Time left: 30 sec");

	        // ✅ Start Timer
	        timer[0] = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
	            int t = Integer.parseInt(timerLabel.getText().replaceAll("\\D", "")) - 1;
	            timerLabel.setText("⏱️ Time left: " + t + " sec");

	            if (t <= 0) {
	                timer[0].stop();
	                feedback.setText("⏱️ Time's up! Correct answer: " + q.getCorrectAnswer());
	                submit.setDisable(true);

	                new Timer().schedule(new TimerTask() {
	                    public void run() {
	                        currentIndex++;
	                        Platform.runLater(() -> showQuestion(stage, quizId, quizTitle, username));
	                    }
	                }, 1500);
	            }
	        }));
	        timer[0].setCycleCount(30);
	        timer[0].play();

	        // ✅ Back/Quit Button
	        Button back = new Button("Quit Quiz");
	        back.setOnAction(ev -> {
	            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
	            confirm.setTitle("Exit Quiz");
	            confirm.setHeaderText("Are you sure?");
	            confirm.setContentText("Current score will not be saved.");
	            confirm.showAndWait().ifPresent(result -> {
	                if (result.getText().equals("OK")) {
	                    timer[0].stop(); // stop the timer
	                    UserDashboard.launchUser(stage, username);
	                }
	            });
	        });

	        VBox box = new VBox(10, timerLabel, qLabel, a, b, c, d, submit, feedback, back);
	        box.setPadding(new Insets(20));
	        box.setAlignment(Pos.CENTER_LEFT);

	        // ✅ Submit Answer
	        submit.setOnAction(e -> {
	            RadioButton selected = (RadioButton) group.getSelectedToggle();
	            if (selected == null) {
	                feedback.setText("⚠️ Please select an option.");
	                return;
	            }

	            String selectedText = selected.getText();
	            boolean correct = selectedText.equals(q.getCorrectAnswer());
	            if (correct) {
	                feedback.setText("✅ Correct!");
	                score++;
	            } else {
	                feedback.setText("❌ Incorrect! Correct answer is: " + q.getCorrectAnswer());
	            }

	            submit.setDisable(true);
	            timer[0].stop();

	            new Timer().schedule(new TimerTask() {
	                @Override
	                public void run() {
	                    currentIndex++;
	                    Platform.runLater(() -> showQuestion(stage, quizId, quizTitle, username));
	                }
	            }, 1500);
	        });

	        Scene scene = new Scene(box, 500, 400);
	        scene.getStylesheets().add(QuizTaker.class.getResource("/style.css").toExternalForm());
	        stage.setScene(scene);
	        stage.setTitle("Quiz: " + quizTitle);
	        stage.show();
	    }

//	    private static void showQuestion(Stage stage, int quizId, String quizTitle, String username) {
//	    	Timeline timer = null;
//
//	    	if (currentIndex >= questions.size()) {
//	        	
//	            int total = questions.size();
//	            saveScore(username, quizId, score, total);
//
//	            Alert a = new Alert(Alert.AlertType.INFORMATION);
//	            a.setHeaderText("Quiz Completed!");
//	            a.setContentText("Score: " + score + "/" + total);
//	            a.showAndWait();
//
//	            UserDashboard.launchUser(stage, username);
//	            return;
//	        }
//
//	        Question q = questions.get(currentIndex);
//
//	        Label qLabel = new Label("Q" + (currentIndex + 1) + ": " + q.text);
//	        ToggleGroup group = new ToggleGroup();
//
//	        RadioButton a = new RadioButton(q.optionA); a.setToggleGroup(group);
//	        RadioButton b = new RadioButton(q.optionB); b.setToggleGroup(group);
//	        RadioButton c = new RadioButton(q.optionC); c.setToggleGroup(group);
//	        RadioButton d = new RadioButton(q.optionD); d.setToggleGroup(group);
//
//	        Button submit = new Button("Submit");
//	        Label feedback = new Label();
//	        Label timerLabel = new Label("⏱️ Time left: 30 sec");
//	         timer = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
//	            int t = Integer.parseInt(timerLabel.getText().replaceAll("\\D", "")) - 1;
//	            timerLabel.setText("⏱️ Time left: " + t + " sec");
//	            if (t <= 0) {
//	                timer.stop();
//	                feedback.setText("⏱️ Time's up! Correct answer: " + q.getCorrectAnswer());
//	                submit.setDisable(true);
//	                new Timer().schedule(new TimerTask() {
//	                    public void run() {
//	                        currentIndex++;
//	                        Platform.runLater(() -> showQuestion(stage, quizId, quizTitle, username));
//	                    }
//	                }, 1500);
//	            }
//	        }));
//	        timer.setCycleCount(30);
//	        timer.play();
//
//	        Button back = new Button("Quit Quiz");
//	        back.setOnAction(ev -> {
//	            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
//	            confirm.setTitle("Exit Quiz");
//	            confirm.setHeaderText("Are you sure?");
//	            confirm.setContentText("Current score will not be saved.");
//	            confirm.showAndWait().ifPresent(result -> {
//	                if (result.getText().equals("OK")) {
//	                    timer.stop(); // stop the timer
//	                    UserDashboard.launchUser(stage, username);
//	                }
//	            });
//	        });
//
//	        VBox box = new VBox(10, timerLabel, qLabel, a, b, c, d, submit, feedback, back);
//
//	       // VBox box = new VBox(10, qLabel, a, b, c, d, submit, feedback);
//	        box.setPadding(new Insets(20));
//	        box.setAlignment(Pos.CENTER_LEFT);
//
//	        submit.setOnAction(e -> {
//	            RadioButton selected = (RadioButton) group.getSelectedToggle();
//	            if (selected == null) {
//	                feedback.setText("⚠️ Please select an option.");
//	                return;
//	            }
//
//	            String selectedText = selected.getText();
//	            boolean correct = selectedText.equals(q.getCorrectAnswer());
//	            if (correct) {
//	                feedback.setText("✅ Correct!");
//	                score++;
//	            } else {
//	                feedback.setText("❌ Incorrect! Correct answer is: " + q.getCorrectAnswer());
//	            }
//
//	            submit.setDisable(true);
//
//	            new Timer().schedule(new TimerTask() {
//	                @Override
//	                public void run() {
//	                    currentIndex++;
//	                    javafx.application.Platform.runLater(() ->
//	                        showQuestion(stage, quizId, quizTitle, username)
//	                    );
//	                }
//	            }, 1500);
//	        });
//
//	        Scene scene = new Scene(box, 500, 400);
//	        scene.getStylesheets().add(QuizTaker.class.getResource("/style.css").toExternalForm());
//	        stage.setScene(scene);
//	        stage.setTitle("Quiz: " + quizTitle);
//	        stage.show();
//	    }

	    private static void saveScore(String username, int quizId, int score, int total) {
	        try (Connection c = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/quiz_app", "root", "Harika@27111999");
	             PreparedStatement ps = c.prepareStatement(
	                     "INSERT INTO quiz_attempts (username, quiz_id, score, total_questions) VALUES (?, ?, ?, ?)")) {
	            ps.setString(1, username);
	            ps.setInt(2, quizId);
	            ps.setInt(3, score);
	            ps.setInt(4, total);
	            ps.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    private static class Question {
	        String text, optionA, optionB, optionC, optionD, correct;

	        Question(String text, String a, String b, String c, String d, String correct) {
	            this.text = text; this.optionA = a; this.optionB = b;
	            this.optionC = c; this.optionD = d; this.correct = correct;
	        }

	        String getCorrectAnswer() {
	            switch (correct) {
	                case "A": return optionA;
	                case "B": return optionB;
	                case "C": return optionC;
	                case "D": return optionD;
	                default: return "";
	            }
	        }
	    }
}
