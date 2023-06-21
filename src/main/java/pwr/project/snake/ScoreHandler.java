package pwr.project.snake;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class ScoreHandler {
    private ArrayList<Integer> Scores;
    int var_score;
    public ScoreHandler() {
        Scores = new ArrayList<>();
    }

    @FXML
    private Label scoreTitle;

    @FXML
    private ListView<String> scoreList;

    @FXML
    private Button btnBack;
    private VBox rootContainer;

    @FXML
    public void initialize() throws FileNotFoundException {
        // Jeśli jest pusty to wypełnia zerami

        Scores = readScore();
        if(Scores.size() == 0) { for(int i = 0; i < 10; i++) { Scores.add(0); } }

        for(int i = 0; i < Scores.size(); i++) {
            scoreList.getItems().add(Scores.get(i).toString());
//            System.out.print("Wyniki " + Scores.get(i) + "\n");
        }
    }

    public void setRootContainer(VBox rootContainer) {
        this.rootContainer = rootContainer;
    }
    @FXML
    protected void onBackButtonClick() {
        FXMLLoader fxmlLoader = new FXMLLoader(SnakeMenu.class.getResource("hello-view.fxml"));
        try {
            Node menuView = fxmlLoader.load();
            rootContainer.getChildren().setAll(menuView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> readScore() throws FileNotFoundException {
        try (BufferedReader br = new BufferedReader(new FileReader("Scoreboard.txt"))) {
            Scores.clear(); // Czyszcze kolekcje
            String line;

            while ((line = br.readLine()) != null) {
                var_score = Integer.parseInt(line);
                Scores.add(var_score);
//                System.out.print("Czytany wynik: " + var_score + " \n");
//                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Scores;
    }
    public void writeScore(int score) throws FileNotFoundException {
        ArrayList<Integer> var = readScore(); // Wpisuje aktualne wyniki
        int count_scores = var.size(); // To samo ale prościej

        if (count_scores < 10) {
            var.add(score);
        } else if (count_scores == 10 && score > var.get(count_scores - 1)) {
            for (int i = 0; i < count_scores; i++) {
                if (var.get(i) < score) {
                    var.add(i, score);
                    var.remove(var.size() - 1); // Usuwa ostatni element
                    break;
                }
            }
        }
        Collections.sort(var, Collections.reverseOrder()); // Sortuje od największego do najmniejszego

        try (PrintWriter write = new PrintWriter("Scoreboard.txt")) {
            for (int scoreItem : var) {
                write.println(scoreItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

// TODO testy jednostkowe cmd + shift + t