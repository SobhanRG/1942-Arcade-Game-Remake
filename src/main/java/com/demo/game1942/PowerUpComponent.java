package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

/**
 * کامپوننت آیتم‌های تقویتی
 * مسئولیت حرکت، اعمال اثر و مدیریت مدت زمان قدرت‌ها
 */
public class PowerUpComponent extends Component {

    /**
     * انواع آیتم‌های تقویتی موجود در بازی
     */
    public enum PowerUpType {
        RAPID_FIRE(10.0, "Rapid Fire"),      // شلیک سریع
        TRIPLE_SHOT(15.0, "Triple Shot"),    // شلیک سه‌تایی
        SHIELD(12.0, "Shield"),              // محافظ
        EXTRA_LIFE(0.0, "Extra Life"),       // جان اضافی
        SCORE_BOOST(0.0, "Score Boost"),     // امتیاز دوبرابر
        BULLET_SHIELD(8.0, "Bullet Shield"); // محافظ گلوله

        public final double duration; // مدت زمان اثر
        public final String displayName; // نام نمایشی

        PowerUpType(double duration, String displayName) {
            this.duration = duration;
            this.displayName = displayName;
        }
    }

    // متغیرهای نمونه
    private PowerUpType type;
    private double speed = 80.0;

    /**
     * سازنده با نوع مشخص
     */
    public PowerUpComponent(PowerUpType type) {
        this.type = type;
    }

    @Override
    public void onUpdate(double tpf) {
        if (entity == null) return;

        // حرکت آرام به پایین
        entity.translateY(speed * tpf);

        // حذف در صورت خروج از صفحه
        if (entity.getY() > FXGL.getAppHeight() + 50) {
            entity.removeFromWorld();
        }
    }

    /**
     * اعمال اثر قدرت بر روی بازیکن
     */
    public void applyPowerUp(Entity player) {
        try {
            PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
            if (playerComponent == null) {
                System.out.println("PlayerComponent not found!");
                return;
            }

            System.out.println("Applying power-up: " + type.displayName);

            MusicManager.getInstance().playSound("powerup.wav");

            // اعمال اثر بر اساس نوع قدرت
            switch (type) {
                case RAPID_FIRE:
                    playerComponent.activeRapidFire(type.duration);
                    break;

                case TRIPLE_SHOT:
                    playerComponent.activeTripleShot(type.duration);
                    break;

                case SHIELD:
                    playerComponent.activateShield(type.duration);
                    break;

                case EXTRA_LIFE:
                    GameManager.getInstance().addLife();
                    break;

                case SCORE_BOOST:
                    GameManager.getInstance().activateDoubleScore(type.duration);
                    break;

                case BULLET_SHIELD:
                    playerComponent.activateBulletShield(type.duration);
                    break;
            }

            // نمایش پیام به بازیکن
            showPowerUpNotification();

            // حذف آیتم از دنیای بازی
            entity.removeFromWorld();

        } catch (Exception e) {
            System.out.println("Error applying power-up: " + e.getMessage());
        }
    }

    /**
     * نمایش نوتیفیکیشن قدرت
     */
    private void showPowerUpNotification() {
        String message = type.displayName + " Activated!";
        if (type.duration > 0) {
            message += " (" + (int)type.duration + "s)";
        }

        // نمایش متن شناور
        GameManager.getInstance().showFloatingText(message, getPowerUpColor());

        // نمایش نوتیفیکیشن در کنسول
        System.out.println("POWER-UP: " + message);
    }

    /**
     * دریافت رنگ متناسب با نوع قدرت
     */
    private javafx.scene.paint.Color getPowerUpColor() {
        switch (type) {
            case RAPID_FIRE: return javafx.scene.paint.Color.YELLOW;
            case TRIPLE_SHOT: return javafx.scene.paint.Color.BLUE;
            case SHIELD: return javafx.scene.paint.Color.CYAN;
            case EXTRA_LIFE: return javafx.scene.paint.Color.GREEN;
            case SCORE_BOOST: return javafx.scene.paint.Color.ORANGE;
            case BULLET_SHIELD: return javafx.scene.paint.Color.GOLD;
            default: return javafx.scene.paint.Color.WHITE;
        }
    }

    // Getter methods
    public PowerUpType getType() { return type; }
    public double getSpeed() { return speed; }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}