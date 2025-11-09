package com.demo.game1942;

import com.almasb.fxgl.entity.component.Component;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

/**
 * کامپوننت مدیریت انفجارها
 * مسئولیت انیمیشن‌های بصری انفجار و حذف خودکار
 */
public class ExplosionComponent extends Component {

    @Override
    public void onAdded() {
        if (entity == null) return;

        // انیمیشن بزرگ شدن
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.3),
                entity.getViewComponent().getParent());
        scale.setFromX(0.1);
        scale.setFromY(0.1);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setOnFinished(e -> {
            // حذف انفجار پس از پایان انیمیشن
            if (entity != null) {
                entity.removeFromWorld();
            }
        });

        // انیمیشن محو شدن
        FadeTransition fade = new FadeTransition(Duration.seconds(0.3),
                entity.getViewComponent().getParent());
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        // اجرای همزمان انیمیشن‌ها
        scale.play();
        fade.play();

        System.out.println("Explosion created at (" + entity.getX() + ", " + entity.getY() + ")");
    }
}