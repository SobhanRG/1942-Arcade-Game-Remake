package com.demo.game1942;

import com.almasb.fxgl.entity.component.Component;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class ExplosionComponent extends Component {
    @Override
    public void onAdded() {
        if (entity == null) return;

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.3), entity.getViewComponent().getParent());
        scale.setFromX(0.1);
        scale.setFromY(0.1);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setOnFinished(e -> {
            if (entity != null) {
                entity.removeFromWorld();
            }
        });
        scale.play();

        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), entity.getViewComponent().getParent());
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.play();
    }
    
}