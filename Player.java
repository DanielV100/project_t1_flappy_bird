import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Player extends Rectangle {
    private String plName;

    public Player(String plName, double plSIZE) {
        this.plName = plName;
        this.setHeight(plSIZE);
        this.setWidth(plSIZE);
        ImagePattern imagePattern = new ImagePattern(setPhoto());
        this.setFill(imagePattern);

    }

    public String getPlName() {
        return plName;
    }

    private Image setPhoto()  {
        FrameGrabber webcamCapturer = new OpenCVFrameGrabber(0);
        try {
            webcamCapturer.start();
            Frame frame = webcamCapturer.grab();
            OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
            IplImage img = converter.convert(frame);
            opencv_imgcodecs.cvSaveImage("res/selfie.png", img);
            Image imgPlayer = new Image("file:res/selfie.png");
            webcamCapturer.stop();
            return imgPlayer;
        } catch (FrameGrabber.Exception e) {
            noWebcam();
            throw new RuntimeException(e);
        }
    }
    private void noWebcam(){

    }
}
