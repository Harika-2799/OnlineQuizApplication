package com.quiz.main;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class Leaderboard {

	 public static void show(Stage stage) {
	        Stage lbStage = new Stage();
	        lbStage.initOwner(stage);

	        ComboBox<QuizItem> quizChoice = new ComboBox<>();
	        ObservableList<QuizItem> quizzes = FXCollections.observableArrayList();
	        loadQuizzes(quizzes);
	        quizChoice.setItems(quizzes);
	        quizChoice.setPromptText("Choose Quiz for Top 10");

	        TableView<Row> table = new TableView<>();
	        TableColumn<Row,String> userCol = new TableColumn<>("User");
	        userCol.setCellValueFactory(c->c.getValue().user);

	        TableColumn<Row,String> valCol = new TableColumn<>("Score / %");
	        valCol.setCellValueFactory(c->c.getValue().value);

	        table.getColumns().addAll(userCol,valCol);

	        Button overallBtn = new Button("Overall Average Top 10");

	        quizChoice.setOnAction(e -> {
	            QuizItem q = quizChoice.getValue();
	            if (q != null) fillPerQuiz(table, q.getId());
	        });

	        overallBtn.setOnAction(e -> fillOverall(table));

	        VBox root = new VBox(10, quizChoice, overallBtn, table);
	        root.setPadding(new Insets(15));

	        Scene scene = new Scene(root, 350, 400);
	        scene.getStylesheets().add(Leaderboard.class.getResource("/style.css").toExternalForm());	
	        lbStage.setScene(scene);
	        lbStage.setTitle("Leaderboard");
	        lbStage.show();
	    }

	    /* ---------- DB helpers ---------- */
	    private static Connection con() throws SQLException {
	        return DriverManager.getConnection(
	            "jdbc:mysql://localhost:3306/quiz_app","root","Harika@27111999");
	    }

	    private static void loadQuizzes(ObservableList<QuizItem> list){
	        String sql="SELECT quiz_id,title FROM quizzes";
	        try(Connection c=con(); Statement s=c.createStatement();
	            ResultSet rs=s.executeQuery(sql)){
	            while(rs.next()) list.add(new QuizItem(rs.getInt(1),rs.getString(2)));
	        }catch(SQLException ex){ex.printStackTrace();}
	    }

	    private static void fillPerQuiz(TableView<Row> t,int quizId){
	        t.getItems().clear();
	        String sql="SELECT username, score FROM quiz_attempts " +
	                   "WHERE quiz_id=? ORDER BY score DESC LIMIT 10";
	        try(Connection c=con(); PreparedStatement ps=c.prepareStatement(sql)){
	            ps.setInt(1,quizId);
	            ResultSet rs=ps.executeQuery();
	            while(rs.next()){
	                t.getItems().add(new Row(rs.getString(1),
	                                         String.valueOf(rs.getInt(2))));
	            }
	        }catch(SQLException ex){ex.printStackTrace();}
	    }

	    private static void fillOverall(TableView<Row> t){
	        t.getItems().clear();
	        String sql="SELECT username, ROUND(AVG(score*1.0/total_questions)*100,1) AS avg_pct " +
	                   "FROM quiz_attempts GROUP BY username ORDER BY avg_pct DESC LIMIT 10";
	        try(Connection c=con(); Statement s=c.createStatement();
	            ResultSet rs=s.executeQuery(sql)){
	            while(rs.next()){
	                t.getItems().add(new Row(rs.getString(1),
	                                          rs.getString(2)+"%"));
	            }
	        }catch(SQLException ex){ex.printStackTrace();}
	    }

	    /* ---------- helper model classes ---------- */
	    public static class QuizItem{
	        private final int id; private final String title;
	        QuizItem(int i,String t){id=i;title=t;}
	        public int getId(){return id;}
	        @Override public String toString(){return title;}
	    }
	    public static class Row{
	        SimpleStringProperty user = new SimpleStringProperty();
	        SimpleStringProperty value = new SimpleStringProperty();
	        Row(String u,String v){user.set(u); value.set(v);}
	    }
}
