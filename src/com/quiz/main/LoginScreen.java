package com.quiz.main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class LoginScreen extends Application{

	private Connection connectToDB() {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quiz_app", "root", "Harika@27111999");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean login(String username, String password) {
        try (Connection conn = connectToDB()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

 // ──────────────────────────────────────────────
//  ROLE LOOK‑UP
// ──────────────────────────────────────────────
private String getUserRole(String username) {
    try (Connection conn = connectToDB()) {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT role FROM users WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("role");   // "admin" or "user"
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null; // not found
}


    private boolean signup(String username, String password) {
        try (Connection conn = connectToDB()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (username, password) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException dup) {
            // Username already exists
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void start(Stage primaryStage) {
    	Label label = new Label("Welcome to the Quiz App!");
        Label title = new Label("Quiz App Login");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        Button signupBtn = new Button("Sign Up");

        Label message = new Label();

        HBox buttonBox = new HBox(10, loginBtn, signupBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, label,title, usernameField, passwordField, buttonBox, message);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Button Logic
        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();

            boolean ok = login(user, pass);
            if (!ok) {
                message.setText("❌ Invalid credentials.");
                return;
            }

            // ✔ credentials are valid → find role
            String role = getUserRole(user);
            if ("admin".equals(role)) {
                // open admin dashboard
                AdminDashboard.launchAdmin(primaryStage, user);
            } else {
                message.setText("✅ Login successful (user). "
                              + "User dashboard coming in next requirement!");
                // TODO: later: open UserDashboard
                UserDashboard.launchUser(primaryStage, user);

            }
        });

        signupBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            // ✅ Step 1: Basic field validation
            if (username.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing Fields");
                alert.setHeaderText(null);
                alert.setContentText("⚠️ Please enter both username and password.");
                alert.showAndWait();
                return; // ⛔ Don't proceed
            }

            // ✅ Step 2: Check if username already exists
            try (Connection conn = connectToDB()) {
                PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT * FROM users WHERE username = ?");
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Username Taken");
                    alert.setHeaderText(null);
                    alert.setContentText("⚠️ Username already exists. Please choose another one.");
                    alert.showAndWait();
                    return;
                }

                // ✅ Step 3: Insert new user (default role: user)
                PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
                insertStmt.setString(1, username);
                insertStmt.setString(2, password); // ❗ later hash it
                insertStmt.setString(3, "user");
                insertStmt.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Signup Success");
                alert.setHeaderText(null);
                alert.setContentText("✅ Account created. You can now log in.");
                alert.showAndWait();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });


//        signupBtn.setOnAction(e -> {
//            boolean success = signup(usernameField.getText(), passwordField.getText());
//            if (success) {
//                message.setText("✅ Account created! You can now log in.");
//            } else {
//                message.setText("⚠️ Username already exists.");
//            }
//        });

        Scene scene = new Scene(layout, 400, 300);
       // scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        scene.getStylesheets().add(LoginScreen.class.getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Login - Quiz App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
