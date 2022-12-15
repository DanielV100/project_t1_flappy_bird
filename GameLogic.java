import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;

import java.util.Random;

public class GameLogic extends Application {
    //Playground
    private final int WIDTH = 1400;
    private final int HEIGHT = 800;
    private Playground playground = new Playground();

    //Button Styles
    private final int BTNWIDTH = 300;
    private final int BTNHEIGHT = 80;
    private final String BTNCOLOR = "#4D84DB";
    private final String BTNTEXTCOLOR = "#ffffff";
    private final String BTNBORDERCOLOR = "#576BF9";
    private final double BTNTOPANCHOR = 500.0;
    private final double BTNLEFTANCHOR = 575.0;

    //TextField
    private final int TFWIDTH = 400;
    private final int TFHEIGHT = 80;
    private final String TFBORDERCOLOR = "#576BF9";
    private final double TFTOPANCHOR = 400.0;
    private final double TFLEFTANCHOR = 500.0;

    //Transform of button
    private double btnWidthScaleBig = TFWIDTH;
    private double btnWidthScaleRegular = BTNWIDTH;
    private double btnLeftAnchorScaleBig = TFLEFTANCHOR;
    double btnLeftAnchorScaleRegular = BTNLEFTANCHOR;

    //Obstacles
    private double obstWidth = 100;
    private double obstMaxDist = 800;
    private double obstMinDist = 300;


    public static void main(String[] args) {
       launch(args);
    }

    @Override
    public void start(Stage primaryStage)  {
        primaryStage.setTitle("CustomFlappyBird");
        primaryStage.setResizable(false);
        primaryStage.setScene(playground.createPlayground(WIDTH, HEIGHT, BTNWIDTH, BTNHEIGHT, BTNCOLOR, BTNTEXTCOLOR, BTNBORDERCOLOR, TFWIDTH, TFHEIGHT, TFBORDERCOLOR, BTNLEFTANCHOR, BTNTOPANCHOR, TFLEFTANCHOR, TFTOPANCHOR, btnWidthScaleBig,  btnWidthScaleRegular,  btnLeftAnchorScaleBig, btnLeftAnchorScaleRegular, obstWidth, obstMaxDist, obstMinDist));
        primaryStage.show();
    }
}