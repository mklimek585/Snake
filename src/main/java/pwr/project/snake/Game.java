package pwr.project.snake;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import pwr.project.snake.SnakeMenu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {
    // Window variables
    private int width;
    private int height;
    private final BorderPane root;
    private StackPane wrapper;
    private Scene gameScene;

    // Board variables
    private static final int COLUMNS = 40;
    private static final int ROWS = COLUMNS;
    private int SQUARE_SIZE;

    // Game variables
    private GraphicsContext gc;
    private Canvas canvas;
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private boolean gameOver;
    private Timeline timeline;
    private Point food;
    private final int food_counter = 80;
    private int counter;
    private final List<Point> foods = new ArrayList(food_counter);
    private Image foodImage;
    private int points;
    private ScoreHandler scoreHandler; // tworzenie instancji klasy ScoreHandler jako pola w klasie Game
    private boolean obstacles = true;

    public class Point {
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
    public class Object {
        private final List<Point> snakeBody = new ArrayList();
        private Point Head;
        private int Direction;
        private int score = 0;

        Object() {
            this.score = 0;
            int x = 10;
            int y = 10;
            this.Head = new Point(x, y);
            this.snakeBody.add(Head);
//            for(int i = 1; i < 2; i++) {
//                this.snakeBody.add(new Point(x - i, y));
//            }
        }

        public Point getHead() {
            return Head;
        }
        public List<Point> getSnakeBody() {
            return snakeBody;
        }
        public void printPosition() {
//            System.out.print("Snake head:");
//            System.out.print("X = " + getHead().getX());
//            System.out.print(" Y = " + getHead().getY());
//            System.out.print("\n");
//            if(this.snakeBody.size() == 3) {
//                System.out.print("Snake body(1):");
//                System.out.print("X = " + getSnakeBody().get(1).getX());
//                System.out.print(" Y = " + getSnakeBody().get(1).getY());
//                System.out.print("\n");
//                System.out.print("Snake body(2):");
//                System.out.print("X = " + getSnakeBody().get(2).getX());
//                System.out.print(" Y = " + getSnakeBody().get(2).getY());
//                System.out.print("\n");
//            }
        }
    }

    public Game(BorderPane gameRoot, int width, int height) {  // Changed VBox to BorderPane BorderPane
        this.root = gameRoot;
        this.width = width;
        this.height = height;
        this.SQUARE_SIZE = height / ROWS;
        this.scoreHandler = new ScoreHandler();

        root.setStyle("-fx-background-color: #429642;"); // Change to your desired color #86b42e

        this.canvas = new Canvas(width, height);
        root.setCenter(canvas);  // This will center the canvas in the BorderPane
        gc = canvas.getGraphicsContext2D();
    }
    public List<Point> getFoods() {
        return foods;
    }
    public int generateFood(int counter, int food_counter) {
        if(counter < food_counter) {
            int randomX;
            int randomY;
            Random rand = new Random();
            for(int i = counter; i < food_counter; i++) {  // Zmieniłem z counter+1 na counter
                randomX = rand.nextInt(40);
                randomY = rand.nextInt(40);
                foods.add(new Point(randomX,randomY));

                System.out.print("Food " + i + ":");
                System.out.print("X = " + getFoods().get(i).getX());
                System.out.print(" Y = " + getFoods().get(i).getY());
                System.out.print("\n");
            }
            counter = food_counter;
        }
        return counter;
    }
    public void clearFood(int counter) {
            for(int i = counter; i > 0; i--) {  // Zmieniłem z counter+1 na counter
                foods.remove(i);
            }
    }

    public boolean eatFood(List<Point> foods, Object snake) {
        for (int i = 0; i < foods.size(); i++) {
            if(snake.Head.getX() == getFoods().get(i).getX() &&
                    snake.Head.getY() == getFoods().get(i).getY()) {
                foods.remove(i);
                counter--;
                snake.score += 10;
                return true;
            }
        }
    return false;
    }

    public void startGame(Stage stage) {
        gameScene = new Scene(root);
        gameScene.widthProperty().addListener((observable, oldValue, newValue) -> {
            updateSize(newValue.doubleValue(), canvas.getHeight());
        });
        gameScene.heightProperty().addListener((observable, oldValue, newValue) -> {
            updateSize(canvas.getWidth(), newValue.doubleValue());
        });
        stage.setTitle("Snake Game");
        stage.setScene(gameScene);
        //stage.setMaximized(true);
        stage.setFullScreen(true);

        gameOver = false;
        Object snake = new Object();
        Object obstacles = new Object();

        snake.score = 0;
        counter = 0;                                    // Tu musi być zerowane
        counter = generateFood(counter, food_counter);  // food_counter w menu bedzie można ustawić
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    if (snake.Direction != LEFT) {
                        snake.Direction = RIGHT;
                    }
                } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                    if (snake.Direction != RIGHT) {
                        snake.Direction = LEFT;
                    }
                } else if (code == KeyCode.UP || code == KeyCode.W) {
                    if (snake.Direction != DOWN) {
                        snake.Direction = UP;
                    }
                } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                    if (snake.Direction != UP) {
                        snake.Direction = DOWN;
                    }
                }
            }
        });

        this.timeline = new Timeline(new KeyFrame(Duration.millis(130), e -> {
            try {
                realTimeGame(gc, snake, foods, stage);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }));
        this.timeline.setCycleCount(Animation.INDEFINITE);
        this.timeline.play();
    }
    private void realTimeGame(GraphicsContext gc, Object snake, List<Point> foods, Stage stage) throws FileNotFoundException {
        gameOver = isGameOver(snake);
        if(!gameOver) {
            drawBackground(gc);
            snake.printPosition();
            counter = generateFood(counter, food_counter);
            drawFood(foods);
            drawSnake(snake);
            switch (snake.Direction) {
                case RIGHT:
                    moveRight(snake);
                    break;
                case LEFT:
                    moveLeft(snake);
                    break;
                case UP:
                    moveUp(snake);
                    break;
                case DOWN:
                    moveDown(snake);
                    break;
            }
            System.out.print("gameOver = " + gameOver + "\n");
        }
        else {
            System.out.print("else konczenia gry \n");
            snake.printPosition();
            System.out.print("SCORE: " + snake.score);
            this.timeline.stop(); // Zatrzymanie gry

            scoreHandler.writeScore(snake.score);
            showGameOverScreen(stage, snake);
        }
    }

    private void showGameOverScreen(Stage stage, Object snake) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: #429642;");

        VBox layout = new VBox(50);
        layout.setAlignment(Pos.CENTER);


        VBox textLayout = new VBox(20);
        textLayout.setAlignment(Pos.CENTER);

        Label gameOverLabel = new Label("Game Over");
        gameOverLabel.setTextFill(Color.web("#b81414" ));
        gameOverLabel.setFont(new Font("Arial", 100));


        String gameoverText = new String("Your score is " + snake.score);
        Label scoreLabel = new Label(gameoverText);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", 46));


        HBox buttonLayout = new HBox(10);
        buttonLayout.setAlignment(Pos.CENTER);

        Button restartButton = new Button("Play again");
        restartButton.setFont(new Font("Arial", 24));
        restartButton.setOnAction(event -> {
            restartGame(stage);
        });

        Button exitButton = new Button("Quit");
        exitButton.setFont(new Font("Arial", 24));
        exitButton.setOnAction(event -> {
            // Pobierz referencję do bieżącego okna
            Stage currentStage = (Stage) exitButton.getScene().getWindow();

            // Stwórz nowe okno
            Stage newStage = new Stage();

            // Uruchom nowe okno menu
            SnakeMenu snakeMenu = new SnakeMenu();
            try {
                snakeMenu.start(newStage);
                newStage.setFullScreen(false);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Zamknij bieżące okno, ale dopiero po otwarciu nowego
            currentStage.close();
        });

        buttonLayout.getChildren().addAll(restartButton, exitButton);  // Dodaje przyciski do layoutu
        textLayout.getChildren().addAll(gameOverLabel, scoreLabel);    // Dodaje napisy do layotu

        layout.getChildren().addAll(textLayout, buttonLayout);         // Dodaje layouty do layoutu głównego
        overlay.getChildren().add(layout);

        // Create new StackPane which will contain the overlay
        this.wrapper = new StackPane();

        // Add overlay to wrapper
        wrapper.getChildren().add(overlay);

        // Set wrapper alignment to center
        StackPane.setAlignment(overlay, Pos.CENTER);

        // Add overlay to root
        this.root.setCenter(wrapper);
    }

    private boolean isGameOver(Object snake) {
        // TODO wywalka na przeszkodach
        for(int i = 1; i < snake.snakeBody.size(); i++) {
           if(snake.Head.getX() == snake.snakeBody.get(i).getX() &&
                   snake.Head.getY() == snake.snakeBody.get(i).getY()) {
               return true;
           }
        }
        if(snake.Head.getX() < 0 || snake.Head.getY() < 0
        || snake.Head.getX() > (COLUMNS - 1)  || snake.Head.getY() > (COLUMNS - 1)) {
            return true;
        }
        return false;
    }
    private void moveRight(Object snake) {
        snake.Head.x++;
        Point newPoint = new Point(snake.Head.x, snake.Head.y);
        if (eatFood(foods,snake)) {
            // Jeśli wąż zjadł jedzenie, dodaj nowy punkt do ciała węża bez usuwania ostatniego (wąż rośnie)
            snake.snakeBody.add(0,newPoint);
        } else {
            // Jeśli wąż nie zjadł jedzenia, przesuń całe ciało węża (dodaj na początek, usuń z końca)
            snake.snakeBody.add(0,newPoint);
            snake.snakeBody.remove(snake.snakeBody.size()-1); // Usuń ostatni element
        }
    }

    private void moveLeft(Object snake) {
        snake.Head.x--;
        Point newPoint = new Point(snake.Head.x, snake.Head.y);
        if (eatFood(foods,snake)) {
            // Jeśli wąż zjadł jedzenie, dodaj nowy punkt do ciała węża bez usuwania ostatniego (wąż rośnie)
            snake.snakeBody.add(0,newPoint);
        } else {
            // Jeśli wąż nie zjadł jedzenia, przesuń całe ciało węża (dodaj na początek, usuń z końca)
            snake.snakeBody.add(0,newPoint);
            snake.snakeBody.remove(snake.snakeBody.size()-1); // Usuń ostatni element
        }
    }

    private void moveUp(Object snake) {
        snake.Head.y--;
        Point newPoint = new Point(snake.Head.x, snake.Head.y);
        if (eatFood(foods,snake)) {
            // Jeśli wąż zjadł jedzenie, dodaj nowy punkt do ciała węża bez usuwania ostatniego (wąż rośnie)
            snake.snakeBody.add(0,newPoint);
        } else {
            // Jeśli wąż nie zjadł jedzenia, przesuń całe ciało węża (dodaj na początek, usuń z końca)
            snake.snakeBody.add(0,newPoint);
            snake.snakeBody.remove(snake.snakeBody.size()-1); // Usuń ostatni element
        }
    }

    private void moveDown(Object snake) {
        snake.Head.y++;
        Point newPoint = new Point(snake.Head.x, snake.Head.y);
        if (eatFood(foods,snake)) {
            // Jeśli wąż zjadł jedzenie, dodaj nowy punkt do ciała węża bez usuwania ostatniego (wąż rośnie)
            snake.snakeBody.add(0,newPoint);
        } else {
            // Jeśli wąż nie zjadł jedzenia, przesuń całe ciało węża (dodaj na początek, usuń z końca)
            snake.snakeBody.add(0,newPoint);
            snake.snakeBody.remove(snake.snakeBody.size()-1); // Usuń ostatni element
        }
//        eatFood(foods,snake);
//        // Utwórz nowy punkt z tymi samymi współrzędnymi co Head
//        Point newPoint = new Point(snake.Head.x, snake.Head.y);
//        snake.snakeBody.add(0,newPoint);
//        snake.snakeBody.remove(snake.snakeBody.size()-1); // Usuń ostatni element
    }

    private void drawSnake(Object snake) {
        // Rysuje głowe
        Point Head = snake.getHead();
        gc.setFill(Color.web("494ed5"));
        gc.fillRect(Head.getX() * SQUARE_SIZE, Head.getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

        // Ryusuje ciało
        List<Point> snakeBody = snake.getSnakeBody();
        gc.setFill(Color.web("6265bc"));
        for (int i = 1; i < snakeBody.size(); i++) {
            gc.fillRect(snakeBody.get(i).getX() * SQUARE_SIZE, snakeBody.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }
    }

    private void drawFood(List<Point> foods) {
        List<Point> Foods = foods;
        gc.setFill(Color.web("fce12e"));
        for (int i = 0; i < Foods.size(); i++) {
            gc.fillRect(Foods.get(i).getX() * SQUARE_SIZE, Foods.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }
    }
    private void drawBackground(GraphicsContext gc) {
        // Malowanie pól
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if ((i + j) % 2 == 0) {
                    gc.setFill(Color.web("AAD751"));
                } else {
                    gc.setFill(Color.web("A2D149"));
                }
                gc.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

                // Kolor tekstu
                gc.setFill(Color.BLACK);
                gc.setFont(new Font("Verdana", 6));

                // Koordynaty pól
                String text = "(" + i + ", " + j + ")";
                double textWidth = new Text(text).getLayoutBounds().getWidth();
                double textHeight = new Text(text).getLayoutBounds().getHeight();

                gc.fillText(text, (i * SQUARE_SIZE) + (SQUARE_SIZE - textWidth) / 2, (j * SQUARE_SIZE) + SQUARE_SIZE / 2 + textHeight / 4);
            }
        }
    }

    private void restartGame(Stage stage) {
        this.root.getChildren().remove(wrapper);

        this.canvas = new Canvas(width, height);
        root.setCenter(canvas);  // This will center the canvas in the BorderPane
        gc = canvas.getGraphicsContext2D();

        gameOver = false;
        Object snake = new Object();

        foods.clear();
        snake.score = 0;
        counter = 0;
        System.out.print("Food counter = " + food_counter + "\n");
        counter = generateFood(counter, food_counter);  // food_counter w menu bedzie można ustawić
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    if (snake.Direction != LEFT) {
                        snake.Direction = RIGHT;
                    }
                } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                    if (snake.Direction != RIGHT) {
                        snake.Direction = LEFT;
                    }
                } else if (code == KeyCode.UP || code == KeyCode.W) {
                    if (snake.Direction != DOWN) {
                        snake.Direction = UP;
                    }
                } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                    if (snake.Direction != UP) {
                        snake.Direction = DOWN;
                    }
                }
            }
        });
        this.timeline = new Timeline(new KeyFrame(Duration.millis(130), e -> {
            try {
                realTimeGame(gc, snake, foods, stage);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }));
        this.timeline.setCycleCount(Animation.INDEFINITE);
        System.out.println("Przed rozpoczęciem Timeline");
        this.timeline.play();
        System.out.println("Po rozpoczęciu Timeline");
        root.requestLayout();
    }

    // Od okienka
    private void updateSize(double width, double height) {
        this.width = (int) width;
        this.height = (int) height;
        this.SQUARE_SIZE = (int) (height / ROWS);
        canvas.setWidth(width);
        canvas.setHeight(height);
        drawBackground(gc);
    }
    public double getCanvasWidth() {
        return canvas.getWidth();
    }

}
