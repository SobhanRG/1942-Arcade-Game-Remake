package com.demo.game1942;

import com.almasb.fxgl.entity.component.Component;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

/**
 * کامپوننت مدیریت انفجارهای کوچک (برخورد گلوله‌ها)
 * نسخه ساده‌تر از ExplosionComponent برای کارایی بهتر
 */
public class BulletExplosionComponent extends Component {

    @Override
    public void onAdded() {
        if (entity == null) return;

        // انیمیشن محو شدن سریع
        FadeTransition fade = new FadeTransition(Duration.seconds(0.4),
                entity.getViewComponent().getParent());
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> {
            // حذف پس از پایان انیمیشن
            if (entity != null) {
                entity.removeFromWorld();
            }
        });

        // انیمیشن بزرگ شدن سریع
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.4),
                entity.getViewComponent().getParent());
        scale.setFromX(0.3);
        scale.setFromY(0.3);
        scale.setToX(1.8);
        scale.setToY(1.8);

        // اجرای انیمیشن‌ها
        fade.play();
        scale.play();
    }
}