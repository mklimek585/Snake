package pwr.project.snake;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Game extends Thread{
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
    private int food_amount = 20;
    private int maxAttempts = 1000;  // Limit prob wygenerowania owoca
    private int counter;
    private final List<Point> foods = new ArrayList(food_amount);
    private Image foodImage;
    private ScoreHandler scoreHandler; // tworzenie instancji klasy ScoreHandler jako pola w klasie Game
    private final int obstaces_amount = 6;
    private List<Point> obstaces = new ArrayList(obstaces_amount);
    private List<Point> avoid = new ArrayList<>();
    private boolean snakeai = true;
    private Point Gromp; // TODO Gromp w formie punktu który ucieka po mapie
    private List<Object> object_list = new ArrayList<>(); // Lista na Snake, SnakeAI

    public class Point {
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean isEqualTo(Point other) {
            return this.x == other.x && this.y == other.y;
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
            // Snake konstruktor
            this.score = 0;
            ArrayList<Integer> pom = spawnSnake();
            int x = pom.get(0); int y = pom.get(1); // Ustawiam x i y na losowe wartosci
            this.Head = new Point(x, y);
            this.snakeBody.add(Head);
        }
        Object(Object snake) {
            // SnakeAI konstruktor

            this.score = 0;
            ArrayList<Integer> pom = spawnSnakeAI(snake);
            int x = pom.get(0); int y = pom.get(1); // Ustawiam x i y na losowe wartosci
            this.Head = new Point(x, y);
            this.snakeBody.add(Head);
            this.Direction = RIGHT;
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

        private ArrayList<Integer> spawnSnake() { // TODO funkcja spawnujaca Snake'a
            ArrayList<Integer> pom = new ArrayList<>();
            int randomX; int randomY;
            Random rand = new Random();
            randomX = rand.nextInt(30);
            randomY = rand.nextInt(30);
            randomX += 3; randomY += 3; // Żeby nie zacząć np na 0,0
            pom.add(randomX); pom.add(randomY); // pom(0) = x, pom(1) = y
            return pom;
        }

        private ArrayList<Integer> spawnSnakeAI(Object snake) { // TODO funkcja spawnujaca SnakeAI
            ArrayList<Integer> pom = new ArrayList<>();
            int randomX; int randomY;
            Random rand = new Random();
            boolean cond = false;
            do {
                cond = false;
                randomX = rand.nextInt(40);
                randomY = rand.nextInt(40);
                if (Math.abs(snake.getHead().getX() - randomX) <= 3 && Math.abs(snake.getHead().getY() - randomY) <= 3) {
                    cond = true;
                    break;
                }
            } while(cond);

            randomX += 3; randomY += 3; // Żeby nie zacząć np na 0,0
            pom.add(randomX); pom.add(randomY); // pom(0) = x, pom(1) = y
            return pom;
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

    public void generateObstaces(){
        int randomX;
        int randomY;
        Random rand = new Random();
        for(int i = 0; i < obstaces_amount; i++) {
            boolean valid;
            do {
                valid = true;
                randomX = rand.nextInt(40);
                randomY = rand.nextInt(40);
                for (Point p : object_list.get(0).snakeBody) { // Check for first snake
                    if (Math.abs(p.getX() - randomX) <= 3 && Math.abs(p.getY() - randomY) <= 3) {
                        valid = false;
                        break;
                    }
                }
                if(valid){ // If the point is valid for the first snake, check for the second snake
                    for (Point p : object_list.get(1).snakeBody) {
                        if (Math.abs(p.getX() - randomX) <= 3 && Math.abs(p.getY() - randomY) <= 3) {
                            valid = false;
                            break;
                        }
                    }
                }
            } while (!valid);

            obstaces.add(new Point(randomX,randomY));

            System.out.print("Obstaces " + i + ":");
            System.out.print("X = " + obstaces.get(i).getX());
            System.out.print(" Y = " + obstaces.get(i).getY());
            System.out.print("\n");
        }
    }

    public int generateFood(int counter) {
        if(counter < food_amount) {
            int randomX = 22;
            int randomY = 22;
            Random rand = new Random();

            for(int i = counter; i < food_amount; i++) {
                boolean cond;
                int attempts = 0;  // Counter of attempts for the current fruit
                do {
                    cond = false;
                    randomX = rand.nextInt(40);
                    randomY = rand.nextInt(40);
                    for(int j = 0; j < object_list.get(0).snakeBody.size(); j++) { // Snake
                        if(randomX == object_list.get(0).snakeBody.get(j).getX() &&
                                randomY == object_list.get(0).snakeBody.get(j).getY()) {
                            cond = true;
                            break;
                        }
                    }

                    for(int j = 0; j < object_list.get(1).snakeBody.size(); j++) { // SnakeAI
                        if(randomX == object_list.get(1).snakeBody.get(j).getX() &&
                                randomY == object_list.get(1).snakeBody.get(j).getY()) {
                            cond = true;
                            break;
                        }
                    }

                    for(int j = 0; j < obstaces.size() && !cond; j++) { // Obstacles
                        if(randomX == obstaces.get(j).getX() &&
                                randomY == obstaces.get(j).getY()) {
                            System.out.print("Koordynaty ktore skonczyly gre \n");
                            System.out.print("Head:" + object_list.get(0).Head.getX() +","
                                    + object_list.get(0).Head.getY() + "\n");
                            System.out.print("Obstaces:" + obstaces.get(j).getX() +","
                                    + obstaces.get(j).getY() + "\n");
                            cond = true;
                        }
                    }

                    // Check food not on food
                    for(int j = 0; j < foods.size() && !cond; j++) {
                        if(randomX == foods.get(j).getX() &&
                                randomY == foods.get(j).getY()) {
                            cond = true;
                        }
                    }

                    attempts++;

                } while(cond && attempts < maxAttempts);

                if(attempts == maxAttempts) {
                    food_amount -= 1;
                    break;
                }

                foods.add(new Point(randomX,randomY));

                System.out.print("Food " + i + ":");
                System.out.print("X = " + getFoods().get(i).getX());
                System.out.print(" Y = " + getFoods().get(i).getY());
                System.out.print("\n");
            }
            counter = food_amount;
        }
        return counter;
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
        Object snakeAI = new Object(snake);
        object_list.add(snake); object_list.add(snakeAI); // snake(0) snakeAI(1)

        object_list.get(0).score = 0;
        generateObstaces();
        counter = 0;                                    // Tu musi być zerowane
        counter = generateFood(counter);  // food_amount w menu bedzie można ustawić
        setAvoid();

        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    if (object_list.get(0).Direction != LEFT) {
                        object_list.get(0).Direction = RIGHT;
                    }
                } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                    if (object_list.get(0).Direction != RIGHT) {
                        object_list.get(0).Direction = LEFT;
                    }
                } else if (code == KeyCode.UP || code == KeyCode.W) {
                    if (object_list.get(0).Direction != DOWN) {
                        object_list.get(0).Direction = UP;
                    }
                } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                    if (object_list.get(0).Direction != UP) {
                        object_list.get(0).Direction = DOWN;
                    }
                }
            }
        });

        this.timeline = new Timeline(new KeyFrame(Duration.millis(130), e -> {
            try {
                realTimeGame(gc, object_list, foods, stage);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }));
        this.timeline.setCycleCount(Animation.INDEFINITE);
        this.timeline.play();
    }
    private void restartGame(Stage stage) {
        System.out.print("restartGame \n");
        this.root.getChildren().remove(wrapper);

        // Check if the timeline exists and stop it
        if (this.timeline != null) {
            this.timeline.stop();
        }

        this.canvas = new Canvas(width, height);
        root.setCenter(canvas);  // This will center the canvas in the BorderPane
        gc = canvas.getGraphicsContext2D();

        gameOver = false;
        System.out.print("restartGame2 \n");
        Object snake = new Object();
        Object snakeAI = new Object(snake);
        System.out.print("restartGame3 \n");
        object_list.clear();
        object_list.add(snake); object_list.add(snakeAI); // snake(0) snakeAI(1)
        System.out.print("restartGame4 \n");

        avoid.clear();
        foods.clear(); // TODO reszta zmiennych tez bedzie tu musiała być czyszczona
        obstaces.clear();
        object_list.get(0).score = 0;
        generateObstaces();
        counter = 0;
        System.out.print("Food counter = " + food_amount + "\n");
        counter = generateFood(counter);  // food_amount w menu bedzie można ustawić
        setAvoid();

        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    if (object_list.get(0).Direction != LEFT) {
                        object_list.get(0).Direction = RIGHT;
                    }
                } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                    if (object_list.get(0).Direction != RIGHT) {
                        object_list.get(0).Direction = LEFT;
                    }
                } else if (code == KeyCode.UP || code == KeyCode.W) {
                    if (object_list.get(0).Direction != DOWN) {
                        object_list.get(0).Direction = UP;
                    }
                } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                    if (object_list.get(0).Direction != UP) {
                        object_list.get(0).Direction = DOWN;
                    }
                }
            }
        });
        System.out.print("Dochodze do tego momentu");
        this.timeline = new Timeline(new KeyFrame(Duration.millis(130), e -> {
            try {
                realTimeGame(gc, object_list, foods, stage);
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

    private void realTimeGame(GraphicsContext gc, List<Object> object_list, List<Point> foods, Stage stage) throws FileNotFoundException {
        gameOver = isGameOver(object_list);
        if(!gameOver) {
            drawBackground(gc);
            object_list.get(0).printPosition();
            drawoObstaces();
            counter = generateFood(counter);
            drawFood(foods);
            drawSnake();
            switch (object_list.get(0).Direction) {
                case RIGHT:
                    moveRight(object_list.get(0));
                    break;
                case LEFT:
                    moveLeft(object_list.get(0));
                    break;
                case UP:
                    moveUp(object_list.get(0));
                    break;
                case DOWN:
                    moveDown(object_list.get(0));
                    break;
            }
            moveSnakeAI(object_list);
//            System.out.print("gameOver = " + gameOver + "\n");
        }
        else {
            object_list.get(0).printPosition();
            System.out.print("SCORE: " + object_list.get(0).score);
            this.timeline.stop(); // Zatrzymanie gry

            scoreHandler.writeScore(object_list.get(0).score);
            showGameOverScreen(stage, object_list);
        }
    }
    private void showGameOverScreen(Stage stage, List<Object> object_list) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: #429642;");

        VBox layout = new VBox(50);
        layout.setAlignment(Pos.CENTER);


        VBox textLayout = new VBox(20);
        textLayout.setAlignment(Pos.CENTER);

        Label gameOverLabel = new Label("Game Over");
        gameOverLabel.setTextFill(Color.web("#b81414" ));
        gameOverLabel.setFont(new Font("Arial", 100));


        String gameoverText = new String("Your score is " + object_list.get(0).score);
        Label scoreLabel = new Label(gameoverText);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", 46));


        HBox buttonLayout = new HBox(10);
        buttonLayout.setAlignment(Pos.CENTER);

        Button restartButton = new Button("Play again");
        restartButton.setFont(new Font("Arial", 24));
        restartButton.setOnAction(event -> {
            System.out.print("Tutaj jestem \n");
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
    private boolean isGameOver(List<Object> object_list) {
        for(int i = 1; i < object_list.get(0).snakeBody.size(); i++) { // Zjedzenie siebie
           if(object_list.get(0).Head.getX() == object_list.get(0).snakeBody.get(i).getX() &&
                   object_list.get(0).Head.getY() == object_list.get(0).snakeBody.get(i).getY()) {
               return true;
           }
        }
        if(object_list.get(0).Head.getX() < 0 || object_list.get(0).Head.getY() < 0 // Wyjscie za plansze
        || object_list.get(0).Head.getX() > (COLUMNS - 1)  || object_list.get(0).Head.getY() > (COLUMNS - 1)) {
            return true;
        }

        Point tempHead = new Point(object_list.get(0).Head.x, object_list.get(0).Head.y);
        for (Point obstace : obstaces) { // Przeszkody
            if (tempHead.isEqualTo(obstace)) {
                return true;
            }
        }
        for (Point bodyPart : object_list.get(1).snakeBody) { // Zderzenie snake z snakeAI
            if (tempHead.isEqualTo(bodyPart)) {
                return true;
            }
        }

        return false;
    }

    // TODO spawnGame - generujaca przeszkody, węże, później owoce by na siebie nie nachodziły
    // TODO drawGame - układająca co sie rysuje po czym

    private void setAvoid() {
        avoid = new ArrayList<>(obstaces);
        for(int i = 0; i < COLUMNS; i++) { // Boundary (-1,0-39)
            Point pom = new Point(-1, i);
            avoid.add(pom);
        }
        for(int i = 0; i < COLUMNS; i++) { // Boundary (0-39,-1)
            Point pom = new Point(i, -1);
            avoid.add(pom);
        }
        for(int i = 0; i < COLUMNS; i++) { // Boundary (0-39,40)
            Point pom = new Point(i, 40);
            avoid.add(pom);
        }
        for(int i = 0; i < COLUMNS; i++) { // Boundary (40,0-39)
            Point pom = new Point(40, i);
            avoid.add(pom);
        }
        System.out.print("Punkty do unikania: ");
        for(Point avoi : avoid) {
            System.out.print(avoi.getX() + "," + avoi.getY() + " ");
        }
    }

    private void moveSnakeAI(List<Object> object_list) {
        List<Integer> possibleDirections = new ArrayList<>();
        possibleDirections.add(RIGHT); // 0
        possibleDirections.add(LEFT); // 1
        possibleDirections.add(UP); // 2
        possibleDirections.add(DOWN); // 3

        System.out.print("\nMożliwe kierunki pierwotnie ");
        for(Integer possibleDirection : possibleDirections) {
            System.out.print(possibleDirection + " ");
        }

// Wyłączenie z możliwych kierunków ciało węża i przeszkody
        int tempX = new Integer(object_list.get(1).Head.x);
        int tempY = new Integer(object_list.get(1).Head.y);

        // TODO by nie wjezdzal w drugiego weza
        List<Integer> validDirections = new ArrayList<>(possibleDirections);
        for (Integer possibleDirection : possibleDirections) {
            Point tempPoint = new Point(object_list.get(1).Head.x, object_list.get(1).Head.y);
            if (possibleDirection == RIGHT) {
                tempPoint.setX(tempPoint.getX() + 1);
            } else if (possibleDirection == LEFT) {
                tempPoint.setX(tempPoint.getX() - 1);
            } else if (possibleDirection == UP) {
                tempPoint.setY(tempPoint.getY() - 1);
            } else if (possibleDirection == DOWN) {
                tempPoint.setY(tempPoint.getY() + 1);
            }

            for (Point point : avoid) {
                if (tempPoint.isEqualTo(point)) {
                    validDirections.remove(possibleDirection);
                    break;
                }
            }
            for (Point bodyPart : object_list.get(1).snakeBody) {
                if (tempPoint.isEqualTo(bodyPart)) {
                    validDirections.remove(possibleDirection);
                    break;
                }
            }
            for (Point bodyPart : object_list.get(0).snakeBody) {
                if (tempPoint.isEqualTo(bodyPart)) {
                    validDirections.remove(possibleDirection);
                    break;
                }
            }
        }

        possibleDirections.clear();  // Wyczyszczenie pierwotnej listy
        possibleDirections.addAll(validDirections);
        validDirections.clear();

        System.out.print("\nMożliwe kierunki po warunkach ");
        for(Integer validDirection : validDirections) {
            System.out.print(validDirection + " ");
        }


        System.out.print("\nMożliwe kierunki po warunkach ");
        for(Integer possibleDirection : possibleDirections) {
            System.out.print(possibleDirection + " ");
        }

        // Znajdź najbliższy owoc
        Point targetFood = null;
        double minDistance = Double.MAX_VALUE;
        for(Point food : foods){
            double currentDistance = Math.sqrt(Math.pow(object_list.get(1).Head.x - food.x, 2) + Math.pow(object_list.get(1).Head.y - food.y, 2));
            if(currentDistance < minDistance) {
                minDistance = currentDistance;
                targetFood = food;
            }
        }

        // Wyznacz kierunek poruszania węża
        int newDirection = object_list.get(1).Direction;
        if(targetFood != null){
            int diffX = Math.abs(targetFood.x - object_list.get(1).Head.x);
            int diffY = Math.abs(targetFood.y - object_list.get(1).Head.y);

            if(diffX > diffY){
                if(targetFood.x < object_list.get(1).Head.x && possibleDirections.contains(LEFT)){
                    newDirection = LEFT;
                } else if(targetFood.x > object_list.get(1).Head.x && possibleDirections.contains(RIGHT)){
                    newDirection = RIGHT;
                }
            } else if(diffX < diffY) {
                if(targetFood.y > object_list.get(1).Head.y && possibleDirections.contains(DOWN)){
                    newDirection = DOWN;
                } else if(targetFood.y < object_list.get(1).Head.y && possibleDirections.contains(UP)){
                    newDirection = UP;
                }
            } else { // gdy diffX == diffY, wybierz lewo lub dół
                if(targetFood.x < object_list.get(1).Head.x && possibleDirections.contains(LEFT)){
                    newDirection = LEFT;
                } else if(targetFood.y > object_list.get(1).Head.y && possibleDirections.contains(DOWN)){
                    newDirection = DOWN;
                }
            }


        }

        Random rand = new Random();

        // Jeżeli nowy kierunek jest niedostępny, wybierz losowy z dostępnych
        // lub ustaw na -1 jeżeli nie ma dostępnych kierunków
        if(!possibleDirections.isEmpty()){
            if(!possibleDirections.contains(newDirection)){
                int randIndex = rand.nextInt(possibleDirections.size());
                newDirection = possibleDirections.get(randIndex);
            }
        } else {
            newDirection = -1;
        }


        object_list.get(1).Direction = newDirection; // Przypisanie kierunku
        System.out.print(" \n object_list.get(1).direction: " + object_list.get(1).Direction);

        if(object_list.get(1).Direction == RIGHT) {
            moveRight(object_list.get(1));
        } else if(object_list.get(1).Direction == LEFT) {
            moveLeft(object_list.get(1));
        } else if(object_list.get(1).Direction == UP) {
            moveUp(object_list.get(1));
        } else if(object_list.get(1).Direction == DOWN) {
            moveDown(object_list.get(1));
        }
        possibleDirections.clear();
    }

    private void drawoObstaces() {
        gc.setFill(Color.web("5e4028"));
        for (int i = 0; i < obstaces.size(); i++) {
            gc.fillRect(obstaces.get(i).getX() * SQUARE_SIZE, obstaces.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }
    }
    private void drawSnake() {
        // Rysuje głowe
        Point Head = object_list.get(0).getHead();
        gc.setFill(Color.web("494ed5"));
        gc.fillRect(Head.getX() * SQUARE_SIZE, Head.getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

        // Ryusuje ciało
        List<Point> snakeBody = object_list.get(0).getSnakeBody();
        gc.setFill(Color.web("6265bc"));
        for (int i = 1; i < snakeBody.size(); i++) {
            gc.fillRect(snakeBody.get(i).getX() * SQUARE_SIZE, snakeBody.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }

        // Rysuje głowe
        Point Head2 = object_list.get(1).getHead();
        gc.setFill(Color.web("c32d37"));
        gc.fillRect(Head2.getX() * SQUARE_SIZE, Head2.getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

        // Ryusuje ciało
        List<Point> snakeBody2 = object_list.get(1).getSnakeBody();
        gc.setFill(Color.web("b43c44"));
        for (int i = 1; i < snakeBody2.size(); i++) {
            gc.fillRect(snakeBody2.get(i).getX() * SQUARE_SIZE, snakeBody2.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
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

//                // Kolor tekstu
//                gc.setFill(Color.BLACK);
//                gc.setFont(new Font("Verdana", 6));
//
//                // Koordynaty pól
//                String text = "(" + i + ", " + j + ")";
//                double textWidth = new Text(text).getLayoutBounds().getWidth();
//                double textHeight = new Text(text).getLayoutBounds().getHeight();
//
//                gc.fillText(text, (i * SQUARE_SIZE) + (SQUARE_SIZE - textWidth) / 2, (j * SQUARE_SIZE) + SQUARE_SIZE / 2 + textHeight / 4);
            }
        }
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

    // Mniej istotne
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
    }
    public List<Point> getFoods() {
        return foods;
    }
}
