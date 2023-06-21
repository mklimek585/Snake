package pwr.project.snake;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class SnakeMenu extends Application {
    // Zmienne globalne
    private static final int WIDTH = 800;
    private static final int HEIGHT = WIDTH;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SnakeMenu.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle("Snake Game Menu");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @FXML
    private Label welcomeText;

    @FXML
    private Button btnGraj, btnWyniki, btnUstawienia, btnWyjdz;

    @FXML
    private VBox rootVBox;
    public void initialize() {
        rootVBox.setStyle("-fx-background-color: #429642;");
        // #808080
        btnGraj.setStyle("-fx-border-color: black;");
        btnGraj.setOnMousePressed(event -> btnGraj.setStyle("-fx-border-color: #666666;"));
        btnGraj.setOnMouseReleased(event -> btnGraj.setStyle("-fx-border-color: black;"));
        //-fx-background-color: #a9a9a9;
        btnWyniki.setStyle("-fx-border-color: black;");
        btnWyniki.setOnMousePressed(event -> btnWyniki.setStyle("-fx-border-color: #666666;"));
        btnWyniki.setOnMouseReleased(event -> btnWyniki.setStyle("-fx-border-color: black;"));

        btnUstawienia.setStyle("-fx-border-color: black;");
        btnUstawienia.setOnMousePressed(event -> btnUstawienia.setStyle("-fx-border-color: none;"));
        btnUstawienia.setOnMouseReleased(event -> btnUstawienia.setStyle("-fx-border-color: black;"));

        btnWyjdz.setStyle("-fx-border-color: black;");
        btnWyjdz.setOnMousePressed(event -> btnWyjdz.setStyle("fx-border-color: none;"));
        btnWyjdz.setOnMouseReleased(event -> btnWyjdz.setStyle("-fx-border-color: black;"));
    }
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Póki co zmiana ustawień nie została zaimplementowana!");
    }
    @FXML
    protected void onWynikButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SnakeMenu.class.getResource("ScoreboardHandler.fxml"));
        Node scoreView = fxmlLoader.load();
        ScoreHandler scoreController = fxmlLoader.getController();
        scoreController.setRootContainer(rootVBox);  // Set rootVBox
        rootVBox.getChildren().setAll(scoreView);
    }
    @FXML
    protected void onExitButtonClick() {
        Platform.exit();
    }
    @FXML
    public void onGrajButtonClick() {
        BorderPane gameRoot = new BorderPane();
        Game game = new Game(gameRoot, WIDTH, HEIGHT);

        // Dodaje paski po bokach
        Pane leftPane = new Pane();
        Pane rightPane = new Pane();

        leftPane.prefWidthProperty().bind(gameRoot.widthProperty().subtract(game.getCanvasWidth()).divide(2));
        rightPane.prefWidthProperty().bind(gameRoot.widthProperty().subtract(game.getCanvasWidth()).divide(2));

        gameRoot.setLeft(leftPane);
        gameRoot.setRight(rightPane);

        Stage stage = (Stage) rootVBox.getScene().getWindow();
        game.startGame(stage);
    }
}