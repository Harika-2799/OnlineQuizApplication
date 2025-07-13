package com.quiz.main;

import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class QuestionManager {

	 public static void launchForQuiz(Stage primaryStage, AdminDashboard.Quiz quiz) {
	        TableView<Question> table = new TableView<>();
	        ObservableList<Question> data = FXCollections.observableArrayList();
	        fetchQuestions(quiz.getId(), data);

	        // ── Table Columns
	        TableColumn<Question, Integer> idCol = new TableColumn<>("ID");
	        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
	        idCol.setPrefWidth(50);

	        TableColumn<Question, String> qCol = new TableColumn<>("Question");
	        qCol.setCellValueFactory(new PropertyValueFactory<>("questionText"));
	        qCol.setPrefWidth(300);

	        TableColumn<Question, String> ansCol = new TableColumn<>("Correct");
	        ansCol.setCellValueFactory(new PropertyValueFactory<>("correctOption"));
	        ansCol.setPrefWidth(60);

	        table.getColumns().addAll(idCol, qCol, ansCol);
	        table.setItems(data);

	        // ── Buttons
	        Button addBtn = new Button("Add");
	        Button editBtn = new Button("Edit");
	        Button deleteBtn = new Button("Delete");
	        Button backBtn = new Button("Back");

	        HBox controls = new HBox(10, addBtn, editBtn, deleteBtn, backBtn);
	        controls.setAlignment(Pos.CENTER);

	        VBox root = new VBox(10, new Label("Quiz: " + quiz.getTitle()), table, controls);
	        root.setPadding(new Insets(15));

	        Scene scene = new Scene(root, 500, 400);
	        scene.getStylesheets().add(QuestionManager.class.getResource("/style.css").toExternalForm());
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Manage Questions");

	        // ── Button Actions
	        addBtn.setOnAction(e -> {
	            showQuestionForm(quiz.getId(), null);
	            fetchQuestions(quiz.getId(), data);
	        });

	        editBtn.setOnAction(e -> {
	            Question q = table.getSelectionModel().getSelectedItem();
	            if (q != null) {
	                showQuestionForm(quiz.getId(), q);
	                fetchQuestions(quiz.getId(), data);
	            }
	        });

	        deleteBtn.setOnAction(e -> {
	            Question q = table.getSelectionModel().getSelectedItem();
	            if (q != null && confirm("Delete question?")) {
	                deleteQuestion(q.getId());
	                fetchQuestions(quiz.getId(), data);
	            }
	        });

	        backBtn.setOnAction(e -> AdminDashboard.launchAdmin(primaryStage, quiz.getTitle()));
	    }

	    private static void fetchQuestions(int quizId, ObservableList<Question> list) {
	        list.clear();
	        try (Connection c = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/quiz_app", "root", "Harika@27111999");
	             PreparedStatement ps = c.prepareStatement(
	                     "SELECT * FROM questions WHERE quiz_id=?")) {
	            ps.setInt(1, quizId);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	                list.add(new Question(
	                        rs.getInt("question_id"),
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

	    private static void showQuestionForm(int quizId, Question existing) {
	        Dialog<Question> dialog = new Dialog<>();
	        dialog.setTitle(existing == null ? "Add Question" : "Edit Question");

	        GridPane grid = new GridPane();
	        grid.setHgap(10); grid.setVgap(10);
	        grid.setPadding(new Insets(20));

	        TextField questionField = new TextField();
	        questionField.setPromptText("Question");

	        TextField a = new TextField(); a.setPromptText("Option A");
	        TextField b = new TextField(); b.setPromptText("Option B");
	        TextField c = new TextField(); c.setPromptText("Option C");
	        TextField d = new TextField(); d.setPromptText("Option D");

	        ComboBox<String> correct = new ComboBox<>();
	        correct.getItems().addAll("A", "B", "C", "D");
	        correct.setPromptText("Correct Option");

	        if (existing != null) {
	            questionField.setText(existing.getQuestionText());
	            a.setText(existing.getOptionA());
	            b.setText(existing.getOptionB());
	            c.setText(existing.getOptionC());
	            d.setText(existing.getOptionD());
	            correct.setValue(existing.getCorrectOption());
	        }

	        grid.addRow(0, new Label("Question:"), questionField);
	        grid.addRow(1, new Label("Option A:"), a);
	        grid.addRow(2, new Label("Option B:"), b);
	        grid.addRow(3, new Label("Option C:"), c);
	        grid.addRow(4, new Label("Option D:"), d);
	        grid.addRow(5, new Label("Correct:"), correct);

	        dialog.getDialogPane().setContent(grid);
	        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

	        dialog.setResultConverter(btn -> {
	            if (btn == ButtonType.OK) {
	                return new Question(
	                        existing != null ? existing.getId() : 0,
	                        questionField.getText(),
	                        a.getText(), b.getText(), c.getText(), d.getText(),
	                        correct.getValue()
	                );
	            }
	            return null;
	        });

	        dialog.showAndWait().ifPresent(q -> {
	            if (existing == null)
	                insertQuestion(quizId, q);
	            else
	                updateQuestion(q);
	        });
	    }

	    private static void insertQuestion(int quizId, Question q) {
	        String sql = "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) " +
	                "VALUES (?, ?, ?, ?, ?, ?, ?)";
	        try (Connection c = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/quiz_app", "root", "Harika@27111999");
	             PreparedStatement ps = c.prepareStatement(sql)) {
	            ps.setInt(1, quizId);
	            ps.setString(2, q.getQuestionText());
	            ps.setString(3, q.getOptionA());
	            ps.setString(4, q.getOptionB());
	            ps.setString(5, q.getOptionC());
	            ps.setString(6, q.getOptionD());
	            ps.setString(7, q.getCorrectOption());
	            ps.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    private static void updateQuestion(Question q) {
	        String sql = "UPDATE questions SET question_text=?, option_a=?, option_b=?, option_c=?, option_d=?, correct_option=? " +
	                "WHERE question_id=?";
	        try (Connection c = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/quiz_app", "root", "Harika@27111999");
	             PreparedStatement ps = c.prepareStatement(sql)) {
	            ps.setString(1, q.getQuestionText());
	            ps.setString(2, q.getOptionA());
	            ps.setString(3, q.getOptionB());
	            ps.setString(4, q.getOptionC());
	            ps.setString(5, q.getOptionD());
	            ps.setString(6, q.getCorrectOption());
	            ps.setInt(7, q.getId());
	            ps.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    private static void deleteQuestion(int id) {
	        try (Connection c = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/quiz_app", "root", "Harika@27111999");
	             PreparedStatement ps = c.prepareStatement("DELETE FROM questions WHERE question_id=?")) {
	            ps.setInt(1, id);
	            ps.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    private static boolean confirm(String msg) {
	        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
	        alert.setHeaderText(null);
	        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
	    }

	    // ── Question model class
	    public static class Question {
	        private final int id;
	        private final String questionText, optionA, optionB, optionC, optionD, correctOption;

	        public Question(int id, String qt, String a, String b, String c, String d, String correct) {
	            this.id = id;
	            this.questionText = qt;
	            this.optionA = a;
	            this.optionB = b;
	            this.optionC = c;
	            this.optionD = d;
	            this.correctOption = correct;
	        }

	        public int getId() { return id; }
	        public String getQuestionText() { return questionText; }
	        public String getOptionA() { return optionA; }
	        public String getOptionB() { return optionB; }
	        public String getOptionC() { return optionC; }
	        public String getOptionD() { return optionD; }
	        public String getCorrectOption() { return correctOption; }
	    }
}
