package com.demo.game1942;

import javafx.util.Duration;

import com.almasb.fxgl.entity.component.Component;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;

public class BulletExplosionComponent extends Component {

    @Override
    public void onAdded() {

        FadeTransition fade = new FadeTransition(Duration.seconds(0.4), entity.getViewComponent().getParent());
        fade.setFromValue(1.0);
        fade.setOnFinished(e -> entity.removeFromWorld());
        fade.play();

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.4), entity.getViewComponent().getParent());
        scale.setFromX(0.3);
        scale.setFromY(0.3);
        scale.setToX(1.8);
        scale.setToY(1.8);
        scale.play();

    }
    
}