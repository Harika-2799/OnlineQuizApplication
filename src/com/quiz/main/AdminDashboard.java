package com.quiz.main;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

/**
 * Simple admin panel for CRUD on quizzes (title only for now).
 * After selecting a quiz you can Delete or Edit its title.
 */

public class AdminDashboard {

	
	 // ---------- DB helpers ----------
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quiz_app", "root", "Harika@27111999");
    }

    private static ObservableList<Quiz> fetchQuizzes() {
        ObservableList<Quiz> list = FXCollections.observableArrayList();
        String sql = "SELECT quiz_id, title FROM quizzes";
        try (Connection c = connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Quiz(rs.getInt("quiz_id"),
                                  rs.getString("title")));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    private static void insertQuiz(String title, String adminName) {
        String sql = "INSERT INTO quizzes (title, created_by) VALUES (?, ?)";
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, adminName);
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private static void updateQuiz(int id, String newTitle) {
        try (Connection c = connect();
             PreparedStatement ps =
                     c.prepareStatement("UPDATE quizzes SET title=? WHERE quiz_id=?")) {
            ps.setString(1, newTitle);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private static void deleteQuiz(int id) {
        try (Connection c = connect();
             PreparedStatement ps =
                     c.prepareStatement("DELETE FROM quizzes WHERE quiz_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    // ---------- JavaFX UI ----------
    public static void launchAdmin(Stage primaryStage, String adminName) {

        TableView<Quiz> table = new TableView<>();
        TableColumn<Quiz,Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Quiz,String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(240);

        table.getColumns().addAll(idCol, titleCol);
        table.setItems(fetchQuizzes());

        // ── Buttons
        Button addBtn    = new Button("Add");
        Button editBtn   = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        Button manageQBtn = new Button("Manage Questions");

        HBox btnBox = new HBox(10, addBtn, editBtn, deleteBtn, manageQBtn);

       // HBox btnBox = new HBox(10, addBtn, editBtn, deleteBtn);
        btnBox.setAlignment(Pos.CENTER);

        // ── Layout
        VBox root = new VBox(10, table, btnBox);
        root.setPadding(new Insets(15));

        // ───────── Button Actions ─────────
        addBtn.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog();
            d.setHeaderText("New Quiz Title:");
            d.showAndWait().ifPresent(title -> {
                if (!title.trim().isEmpty()) {
                    insertQuiz(title.trim(), adminName);
                    table.setItems(fetchQuizzes());
                }
            });
        });

        editBtn.setOnAction(e -> {
            Quiz q = table.getSelectionModel().getSelectedItem();
            if (q == null) return;
            TextInputDialog d = new TextInputDialog(q.getTitle());
            d.setHeaderText("Edit Title:");
            d.showAndWait().ifPresent(newT -> {
                if (!newT.trim().isEmpty()) {
                    updateQuiz(q.getId(), newT.trim());
                    table.setItems(fetchQuizzes());
                }
            });
        });

        deleteBtn.setOnAction(e -> {
            Quiz q = table.getSelectionModel().getSelectedItem();
            if (q == null) return;
            if (confirm("Delete quiz '" + q.getTitle() + "'?")) {
                deleteQuiz(q.getId());
                table.setItems(fetchQuizzes());
            }
        });
        
        manageQBtn.setOnAction(e -> {
            Quiz selectedQuiz = table.getSelectionModel().getSelectedItem();
            if (selectedQuiz == null) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Please select a quiz.");
                a.show();
                return;
            }
            QuestionManager.launchForQuiz(primaryStage, selectedQuiz);
        });


        Scene scene = new Scene(root, 320, 400);
        scene.getStylesheets().add(AdminDashboard.class.getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard - " + adminName);
        primaryStage.show();

    }

    // utility confirm dialog
    private static boolean confirm(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg,
                                ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    
    // ---------- Inner model class ----------
    public static class Quiz {
        private final int id;
        private final String title;
        public Quiz(int id, String title) { this.id=id; this.title=title; }
        public int getId()    { return id; }
        public String getTitle() { return title; }
    }
    
}
