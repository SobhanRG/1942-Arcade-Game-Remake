package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.paint.Color;

/**
 * کامپوننت آیتم‌های تقویتی
 * مسئولیت حرکت، اعمال اثر و مدیریت مدت زمان قدرت‌ها
 */
public class PowerUpComponent extends Component {

    public enum PowerUpType {
        RAPID_FIRE(10.0),      // 10 ثانیه
        TRIPLE_SHOT(15.0),     // 15 ثانیه
        SHIELD(12.0),          // 12 ثانیه
        EXTRA_LIFE(0.0),       // فوری (نیازی به مدت ندارد)
        SCORE_BOOST(0.0),      // فوری
        BULLET_SHIELD(8.0);    // 8 ثانیه

        public final double duration;

        PowerUpType(double duration) {
            this.duration = duration;
        }
    }

    private PowerUpType type;
    private double speed = 80.0; // سرعت پایین‌تر برای جمع‌آوری راحت‌تر

    public PowerUpComponent(PowerUpType type) {
        this.type = type;
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateY(speed * tpf);

        if (entity.getY() > FXGL.getAppHeight() + 50) {
            entity.removeFromWorld();
        }
    }

    public void applyPowerUp(Entity player) {
        try {
            PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
            if (playerComponent == null) {
                System.out.println("PlayerComponent not found!");
                return;
            }

            System.out.println("Applying power-up: " + type);

            switch (type) {
                case RAPID_FIRE:
                    playerComponent.activeRapidFire(type.duration);
                    FXGL.getNotificationService().pushNotification("Rapid Fire Activated!");
                    break;

                case TRIPLE_SHOT:
                    playerComponent.activeTripleShot(type.duration);
                    FXGL.getNotificationService().pushNotification("Triple Shot Activated!");
                    break;

                case SHIELD:
                    playerComponent.activateShield(type.duration);
                    FXGL.getNotificationService().pushNotification("Shield Activated!");
                    break;

                case EXTRA_LIFE:
                    GameManager.getInstance().addLife();
                    FXGL.getNotificationService().pushNotification("Extra Life!");
                    break;

                case SCORE_BOOST:
                    GameManager.getInstance().activateDoubleScore(type.duration);
                    FXGL.getNotificationService().pushNotification("Score Boost!");
                    break;

                case BULLET_SHIELD:
                    playerComponent.activateBulletShield(type.duration);
                    FXGL.getNotificationService().pushNotification("Bullet Shield Activated!");
                    break;
            }

            entity.removeFromWorld();

        } catch (Exception e) {
            System.out.println("Error applying power-up: " + e.getMessage());
        }
    }
}