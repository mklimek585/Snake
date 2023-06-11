//package pwr.project.snake;
//
//import javafx.application.Platform;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//import pwr.project.snake.SnakeMenu;
//import static pwr.project.snake.SnakeMenu.getHeight;
//import static pwr.project.snake.SnakeMenu.getWidth;
//
//public class HelloController {
//
//    private static final int WIDTH = 600;
//    private static final int HEIGHT = WIDTH;
//    private static final int COLUMNS = 30;
//    private static final int ROWS = COLUMNS;
//    private static final int SQUARE_SIZE = HEIGHT / ROWS;
//
//    @FXML
//    private Label welcomeText;
//
//    @FXML
//    private Button btnGraj, btnWyniki, btnUstawienia, btnWyjdz;
//
//    @FXML
//    private VBox rootVBox;
//
//    public void initialize() {
//
//        rootVBox.setStyle("-fx-background-color: #429642;");
//        // #808080
//        btnGraj.setStyle("-fx-border-color: black;");
//        btnGraj.setOnMousePressed(event -> btnGraj.setStyle("-fx-border-color: #666666;"));
//        btnGraj.setOnMouseReleased(event -> btnGraj.setStyle("-fx-border-color: black;"));
//        //-fx-background-color: #a9a9a9;
//        btnWyniki.setStyle("-fx-border-color: black;");
//        btnWyniki.setOnMousePressed(event -> btnWyniki.setStyle("-fx-border-color: #666666;"));
//        btnWyniki.setOnMouseReleased(event -> btnWyniki.setStyle("-fx-border-color: black;"));
//
//        btnUstawienia.setStyle("-fx-border-color: black;");
//        btnUstawienia.setOnMousePressed(event -> btnUstawienia.setStyle("-fx-border-color: none;"));
//        btnUstawienia.setOnMouseReleased(event -> btnUstawienia.setStyle("-fx-border-color: black;"));
//
//        btnWyjdz.setStyle("-fx-border-color: black;");
//        btnWyjdz.setOnMousePressed(event -> btnWyjdz.setStyle("fx-border-color: none;"));
//        btnWyjdz.setOnMouseReleased(event -> btnWyjdz.setStyle("-fx-border-color: black;"));
//    }
//
//    // TODO onclicki buttonow w menu
//    @FXML
//    protected void onHelloButtonClick() {
//        welcomeText.setText("Welcome to JavaFX Application!");
//    }
//
//    @FXML
//    protected void onExitButtonClick() {
//        Platform.exit();
//    }
//
//    @FXML
//    protected void onGrajButtonClick() {
//        Game game = new Game();
//        game.startGame();
//    }
//
//
//}