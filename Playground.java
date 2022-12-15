import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import javafx.event.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.scene.media.*;
import javafx.util.Duration;
import org.bytedeco.opencv.presets.opencv_core;

import javax.swing.*;

public class Playground {
    private boolean alreadyHovered = false;
    private Button btnStartGame = new Button();
    private Button btnRestartGame = new Button();
    private TextField tfName = new TextField();

    private AnchorPane playground = new AnchorPane();
    private Label lblCounter = new Label();
    private Label lblLeaderboard = new Label();


    private Player player;

    private AnimationTimer testAnimation;
    double speed;
    Random random = new Random();


    Rectangle[] rectangles = new Rectangle[18];
    double checkSumtest = 0;
    double testAnchor = 0;
    double obstHeightNew = 0;
    int count = 0;
    ImagePattern imagePatternObstacle = new ImagePattern(new Image("file:res/obstacle_1.png"));
    ImagePattern imagePatternObstacleRotated = new ImagePattern(new Image("file:res/obstacle_1_rotated.png"));
    AudioClip acObstaclePassed = new AudioClip("file:res/obstaclePassedSound.mp3");
    AudioClip acGameMusic = new AudioClip("file:res/gamemusic.mp3");
    double speedObstacle = 1;
    double obstDist = 0;
    double obstHeight = 0;
    FileWriter myWriter;

    public Scene createPlayground(int width, int height, int btnWidth, int btnHeight, String btnColor, String btnTextColor, String btnBorderColor, int tfWidth, int tfHeight, String tfBorderColor, double btnLeftAnchor, double btnTopAnchor, double tfLeftAnchor, double tfTopAnchor, double btnWidthScaleBig, double btnWidthScaleRegular, double btnLeftAnchorScaleBig, double btnLeftAnchorScaleRegular, double obstWidth, double obstMaxDist, double obstMinDist) {
        acGameMusic.play(0.2);
        //settings for playground;
        playground.setMinSize(width, height);
        playground.setBackground(setBackground("file:res/FlappyFace_1.gif"));
        getBestPlayer();
        lblLeaderboard.setFont(Font.font("Roboto", FontWeight.EXTRA_LIGHT, 20));
        lblLeaderboard.setStyle(";-fx-text-fill: #ff0000");
        lblLeaderboard.setMaxHeight(120.0);
        AnchorPane.setTopAnchor(lblLeaderboard, 480.0);
        AnchorPane.setLeftAnchor(lblLeaderboard, 1060.0);
        playground.getChildren().add(lblLeaderboard);
        //adding buttons to the playground
        playground.getChildren().add(createButton(btnWidth, btnHeight, btnColor, btnTextColor, btnBorderColor, btnWidthScaleBig, btnWidthScaleRegular, btnLeftAnchorScaleBig, btnLeftAnchorScaleRegular, obstWidth, obstMaxDist, obstMinDist, btnStartGame, "Start Game!", true, false, btnLeftAnchor, btnTopAnchor));
        playground.getChildren().add(createButton(btnWidth, btnHeight, btnColor, btnTextColor, btnBorderColor, btnWidthScaleBig, btnWidthScaleRegular, btnLeftAnchorScaleBig, btnLeftAnchorScaleRegular, obstWidth, obstMaxDist, obstMinDist, btnRestartGame, "Restart Game!", false, true, btnLeftAnchor, btnTopAnchor));
        playground.getChildren().add(createTextField(tfWidth, tfHeight, tfBorderColor));
        AnchorPane.setLeftAnchor(tfName, tfLeftAnchor);
        AnchorPane.setTopAnchor(tfName, tfTopAnchor);
        Scene scene = new Scene(playground);
        scene.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                keyPressed(event);
            }
        });
        return scene;
    }

    private Background setBackground(String path) {
        Image bgImg = new Image(path);
        BackgroundImage bgImage = new BackgroundImage(bgImg, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background bg = new Background(bgImage);
        return bg;
    }

    private Button createButton(int btnWidth, int btnHeight, String btnColor, String btnTextColor, String btnBorderColor, double btnWidthScaleBig, double btnWidthScaleRegular, double btnLeftAnchorScaleBig, double btnLeftAnchorScaleRegular, double obstWidth, double obstMaxDist, double obstMinDist, Button btn, String btnText, boolean btnStart, boolean btnRestart, double btnLeftAnchor, double btnTopAnchor) {
        btn.setMinWidth(btnWidth);
        btn.setMinHeight(btnHeight);
        btn.setText(btnText);
        btn.setFont(Font.font("Roboto", FontWeight.BOLD, 30));
        btn.setAlignment(Pos.CENTER);
        btn.setStyle("-fx-background-color: " + btnColor + ";-fx-text-fill: " + btnTextColor + ";-fx-border-color: " + btnBorderColor + "; -fx-border-width: 4px; -fx-background-radius: 40px; -fx-border-radius: 40px");
        btn.setOpacity(0.8);
        AnchorPane.setLeftAnchor(btn, btnLeftAnchor);
        AnchorPane.setTopAnchor(btn, btnTopAnchor);
        if (btnRestart) {
            btn.setVisible(false);
            btn.setOnAction((event) -> onRestartGameButtonClicked());
            btn.setOnMouseEntered((event -> onButtonHover(btnWidthScaleBig, btnWidthScaleRegular, btnHeight, btnLeftAnchorScaleBig, btnLeftAnchorScaleRegular, btnRestartGame, btnText)));
            btn.setOnMouseExited((event -> onButtonHover(btnWidthScaleBig, btnWidthScaleRegular, btnHeight, btnLeftAnchorScaleBig, btnLeftAnchorScaleRegular, btnRestartGame, btnText)));
        }
        if (btnStart) {
            btn.setOnAction((event) -> onStartGameButtonClicked(obstWidth, obstMaxDist, obstMinDist));
            btn.setOnMouseExited((event -> onButtonHover(btnWidthScaleBig, btnWidthScaleRegular, btnHeight, btnLeftAnchorScaleBig, btnLeftAnchorScaleRegular, btnStartGame, btnText)));
            btn.setOnMouseEntered((event -> onButtonHover(btnWidthScaleBig, btnWidthScaleRegular, btnHeight, btnLeftAnchorScaleBig, btnLeftAnchorScaleRegular, btnStartGame, btnText)));
        }
        return btn;
    }

    private void onStartGameButtonClicked(double obstWidth, double obstMaxDist, double obstMinDist) {
        lblLeaderboard.setVisible(false);
        AudioClip ac = new AudioClip("file:res/btnClickSound.m4a");
        ac.play();
        lblCounter.setFont(Font.font("Roboto", FontWeight.BOLD, 60));
        lblCounter.setStyle("-fx-text-fill: #ffffff");
        AnchorPane.setLeftAnchor(lblCounter, 20.0);
        AnchorPane.setTopAnchor(lblCounter, 20.0);
        playground.getChildren().add(lblCounter);
        playground.setBackground(setBackground("file:res/background.png"));
        btnStartGame.setVisible(false);
        tfName.setVisible(false);
        player = new Player(tfName.getText(), 80.0);
        AnchorPane.setLeftAnchor(player, 640.0);
        AnchorPane.setTopAnchor(player, 360.0);
        playground.getChildren().add(player);
        setObstacles(obstWidth, obstMaxDist, obstMinDist);

        testAnimation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                createActionModTwo();
            }
        };
        testAnimation.start();
    }


    private void onButtonHover(double btnWidthScaleBig, double btnWidthScaleRegular, double btnHeight, double btnLeftAnchorScaleBig, double btnLeftAnchorScaleRegular, Button btn, String btnText) {
        Scale scaleTransformation = new Scale();
        if (alreadyHovered != true) {
            btn.setPrefSize(btnWidthScaleBig + 50, btnHeight);
            btn.getTransforms().add(scaleTransformation);
            AnchorPane.setLeftAnchor(btn, btnLeftAnchorScaleBig);
            alreadyHovered = true;
            btn.setText("Good luck!");
            btn.setOpacity(1.0);
        } else {
            btn.setPrefSize(btnWidthScaleRegular, btnHeight);
            btn.getTransforms().add(scaleTransformation);
            AnchorPane.setLeftAnchor(btn, btnLeftAnchorScaleRegular);
            alreadyHovered = false;
            btn.setText(btnText);
            btn.setOpacity(0.8);
        }
        btn.setCursor(Cursor.HAND);
    }

    private TextField createTextField(int tfWidth, int tfHeight, String tfBorderColor) {
        tfName.setMinSize(tfWidth, tfHeight);
        tfName.setPromptText("Whats your name?");
        tfName.setFont(Font.font("Roboto", FontWeight.EXTRA_LIGHT, 38));
        tfName.setAlignment(Pos.CENTER);
        tfName.setStyle("-fx-border-color: " + tfBorderColor + "; -fx-border-width: 1px; -fx-background-radius: 40px; -fx-border-radius: 40px");
        tfName.setOnMouseClicked((event -> onTextFieldNameClicked(tfBorderColor)));
        return tfName;
    }

    private void onTextFieldNameClicked(String tfBorderColor) {
        tfName.setStyle("-fx-border-color: " + tfBorderColor + "; -fx-border-width: 4px; -fx-background-radius: 40px; -fx-border-radius: 40px");
    }


    public void keyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.SPACE) {
            AnchorPane.setTopAnchor(player, AnchorPane.getTopAnchor(player) - 50);
            speed = 0.5;

        } else if (e.getCode() == KeyCode.DOWN) {
            AnchorPane.setTopAnchor(player, AnchorPane.getTopAnchor(player) + 50);
        }
    }


    public void createActionModTwo() {
        if(acGameMusic.isPlaying() == false){
            acGameMusic.play(0.1);
        }
        //increments speed of flying down (player)
        speed += 0.02d;
        AnchorPane.setTopAnchor(player, AnchorPane.getTopAnchor(player) + speed);
        //change speed of obstacle if count is 5
        if (count == 5) {
            speedObstacle = 2;
        } else if (count == 20) {
            speedObstacle = 3;
        } else if (count == 40) {
            speedObstacle = 4;
        } else if (count == 80) {
            speedObstacle = 5;
        }
        //iterate through all obstacles
        for (int i = 0; i < rectangles.length; i++) {
            //let the obstacles flow left with a velocity of 2
            AnchorPane.setLeftAnchor(rectangles[i], AnchorPane.getLeftAnchor(rectangles[i]) - speedObstacle);
            //inspect if player hits obstacle
            playerHitsObstacle(i);
            if (AnchorPane.getLeftAnchor(rectangles[i]) < 8 /* && AnchorPane.getLeftAnchor(rectangles[i + 1]) < 8 */) {
                //plays sound if obstacles are on left end
                if (acObstaclePassed.isPlaying()) {
                    acObstaclePassed.stop();
                } else {
                    acObstaclePassed.play(0.3);
                }
                //counts how many rectangles are passed
                count += 1;
                lblCounter.setText(String.valueOf(count));

                //setting different distances between obstacles
                obstDist = setRandomDistance(100, 800, 300);
                if (i == 0) {
                    AnchorPane.setLeftAnchor(rectangles[i], AnchorPane.getLeftAnchor(rectangles[rectangles.length - 1]) + obstDist);
                    AnchorPane.setLeftAnchor(rectangles[i + 1], AnchorPane.getLeftAnchor(rectangles[rectangles.length - 1]) + obstDist);
                } else {
                    AnchorPane.setLeftAnchor(rectangles[i], AnchorPane.getLeftAnchor(rectangles[i - 1]) + obstDist);
                    AnchorPane.setLeftAnchor(rectangles[i + 1], AnchorPane.getLeftAnchor(rectangles[i - 1]) + obstDist);
                }
                obstHeight = setRandomHeight();
                rectangles[i].setHeight(obstHeight);
                rectangles[i + 1].setHeight(800 - obstHeight - 200);
                i++;
            }
//Problems: Obstacles arent same X-Position
            //Idea: Make it possible to fly down faster by clicking vk_down (but only five times a game)

        }
    }

    private void playerHitsObstacle(int count) {
        try {
            if (AnchorPane.getBottomAnchor(rectangles[count]) == 0) {
                //+80 because of the height of the player
                if ((AnchorPane.getLeftAnchor(rectangles[count]) >= AnchorPane.getLeftAnchor(player) - 80 && AnchorPane.getLeftAnchor(rectangles[count]) <= AnchorPane.getLeftAnchor(player) + 80) && (800 - rectangles[count].getHeight()) <= AnchorPane.getTopAnchor(player) + 80) {
                    playerDies();
                }
            }
        } catch (NullPointerException e) {
            if ((AnchorPane.getLeftAnchor(rectangles[count]) >= AnchorPane.getLeftAnchor(player) - 80 && AnchorPane.getLeftAnchor(rectangles[count]) <= AnchorPane.getLeftAnchor(player) + 80) && rectangles[count].getHeight() >= AnchorPane.getTopAnchor(player)) {
                playerDies();
            }
        }

    }

    private void playerDies() {
        btnRestartGame.setVisible(true);
        testAnimation.stop();
        for (int i = 0; i < rectangles.length; i++) {
            playground.getChildren().remove(rectangles[i]);
        }
        playground.getChildren().remove(player);
        try {
            Files.write(Paths.get("res/ranking_names.txt"), ('\n' + count + " " + player.getPlName() + '\n').getBytes(), StandardOpenOption.WRITE);
            Files.write(Paths.get("res/ranking_scores.txt"), String.valueOf('\n' + count + '\n').getBytes(), StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void onRestartGameButtonClicked() {
        speedObstacle = 0;
        btnRestartGame.setVisible(false);
        playground.getChildren().remove(lblCounter);
        count = 0;
        onStartGameButtonClicked(100, 800, 300);
    }
    //Obstacles aren't in one x-position

    public void setObstacles(double obstWidth, double obstMaxDist, double obstMinDist) {
        for (int i = 0; i < rectangles.length; i++) {
            rectangles[i] = null;
        }
        int checkSum = 0; //checks if obstacle 1 +2, 3+4, 5+6, usw.
        obstDist = 0;
        obstHeight = 0;
        for (int i = 0; i < rectangles.length; i++) {
            Random random = new Random();
            rectangles[i] = new Rectangle();
            rectangles[i].setWidth(obstWidth);
            if (checkSum == 2) {
                checkSum = 0;
                obstDist = setRandomDistance(obstWidth, obstMaxDist, obstMinDist);
                obstHeight = setRandomHeight();
                rectangles[i].setHeight(obstHeight);

            } else {
                rectangles[i].setHeight(800.0 - obstHeight - 200.0); // 120 illustrates the gap
                rectangles[i].setFill(imagePatternObstacleRotated);
            }
            if (i % 2 == 0) {
                AnchorPane.setBottomAnchor(rectangles[i], 0.0);
                rectangles[i].setFill(imagePatternObstacle);
            }
            if (i == 0) {
                AnchorPane.setLeftAnchor(rectangles[i], 900.0);
                rectangles[i].setHeight(200.0);
            } else if (i == 1) {
                AnchorPane.setLeftAnchor(rectangles[i], 900.0);
                rectangles[i].setHeight(200.0);

            } else {
                AnchorPane.setLeftAnchor(rectangles[i], AnchorPane.getLeftAnchor(rectangles[i - 2]) + obstDist);
            }
            playground.getChildren().add(rectangles[i]);
            checkSum++;
        }
    }

    //gets random distance between obstacles
    private double setRandomDistance(double obstWidth, double obstMaxDist, double obstMinDist) {
        return random.nextDouble(obstMaxDist - obstMinDist) + obstMinDist;
    }

    private double setRandomHeight() {
        return random.nextDouble(500 - 200) + 200;
    }

    private void getBestPlayer() {
        java.util.List<String> ranking;
        try {
            String leaderboard = "";
            ranking = Files.readAllLines(Paths.get("res/ranking_scores.txt"));
            List<Integer> listOfInteger = convertStringListToIntList(
                    ranking,
                    Integer::parseInt);
            Collections.sort(listOfInteger);
            System.out.println(listOfInteger);
            Collections.reverse(listOfInteger);
            ranking = Files.readAllLines(Paths.get("res/ranking_names.txt"));
            for (int i = 0; i < ranking.size(); i++){
                    if (ranking.get(i).startsWith(String.valueOf(listOfInteger.get(0)))) {
                        leaderboard += ranking.get(i) + '\n' + '\n';
                        for (int x = 0; x < ranking.size(); x++){
                            if (ranking.get(x).startsWith(String.valueOf(listOfInteger.get(1)))) {
                                leaderboard += ranking.get(x) + '\n' + '\n';
                                for (int y = 0; y < ranking.size(); y++){
                                    if (ranking.get(y).startsWith(String.valueOf(listOfInteger.get(2)))) {
                                        leaderboard += ranking.get(y) + '\n' + '\n';
                                        continue;
                                    }

                                }
                            }

                        }
                }

            }
            lblLeaderboard.setText(leaderboard);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T, U> List<U>
    convertStringListToIntList(List<T> listOfString,
                               Function<T, U> function)
    {
        return listOfString.stream()
                .map(function)
                .collect(Collectors.toList());
    }

}
