#  Online Quiz Application (JavaFX + MySQL)

A modern, user-friendly **Online Quiz Application** built using **Java**, **JavaFX** for GUI, and **MySQL** for database operations. Designed to support multiple users, quizzes, scoring, progress tracking, and an admin dashboard — all in one smooth Java desktop app.

---

##  Features

###  User Authentication
- Secure login and signup for users
- Role-based access: **Admin** vs **User**
- Password validation and protection

###  Admin Dashboard
- Create quizzes with multiple-choice questions
- Add/Edit/Delete questions
- View all quizzes and manage them easily

###  User Dashboard
- View available quiz topics
- Take quizzes one question at a time
- See immediate feedback (correct/incorrect)
- Track your scores and view past attempts

###  Leaderboard
- See top scorers
- Rankings based on total and average scores

###  Result Viewer
- Review past quiz attempts with scores
- Easy to navigate history

###  User Interface
- Built with **pure JavaFX**
- Styled with CSS for consistent layout
- Smooth navigation with back buttons

###  Security
- Passwords are validated before signup
- Ready for password hashing (future enhancement)
- SQL-safe using `PreparedStatement`

---

##  Tech Stack

| Layer            | Tech                       |
|------------------|----------------------------|
| Programming Lang | Java 21                    |
| GUI Framework    | JavaFX (v17+)              |
| Database         | MySQL                      |
| IDE              | Eclipse                    |
| DB Connection    | JDBC                       |
| Build Tool       | Manual / Eclipse Build     |

---

##  Database Structure (MySQL)

**`users`**
| id | username | password | role   |
|----|----------|----------|--------|

**`quizzes`**
| id | title        |
|----|--------------|

**`questions`**
| id | quiz_id | question_text | optionA | optionB | optionC | optionD | correct_answer |

**`results`**
| id | username | quiz_id | score | total | timestamp |

---

##  How to Run

1.  Install Java 17+ and JavaFX SDK
2.  Import project into **Eclipse**
3.  Set VM arguments:
--module-path /path/to/javafx-sdk-17/lib --add-modules javafx.controls,javafx.fxml

4.  Set **LoginScreen.java** as the main class
5.  Run the project — it will open the **Login Screen**

---

##  Testing the App

| Action                     | What to Check                        |
|----------------------------|--------------------------------------|
| Sign up without input      | Shows validation warning             |
| Sign in as admin/user      | Routes to correct dashboard          |
| Create a quiz              | Appears in user dashboard            |
| Take a quiz                | Feedback + score after completion    |
| Leaderboard                | Rankings displayed correctly         |
| View attempts              | Past quiz scores shown properly      |
| Back buttons               | Smooth scene navigation              |

---


## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you'd like to change.

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

## 🙌 Acknowledgments

Thanks to:
- JavaFX team for beautiful GUI
- MySQL for seamless DB experience
- Eclipse IDE for development

---

> Created with 💻 by **Jagadish Gonnakuti**

