package pl.Vorpack.app.Animations;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TextAnimations {

    private Label label;

    private boolean isFadeIn = true;

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public TextAnimations(Label label) {
        this.label = label;
    }

    private void fadeInLabel(){
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), label);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private void fadeOutLabel(){
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), label);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }

    public void startLabelsPulsing(){

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e ->{
            if(isFadeIn){
                fadeInLabel();
                isFadeIn = false;
            }else{
                fadeOutLabel();
                isFadeIn = true;
            }
        }),
                new KeyFrame(Duration.seconds(2))
        );
        clock.setCycleCount(6);
        clock.play();
    }
}
