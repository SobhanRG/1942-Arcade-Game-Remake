package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * کامپوننت کنترل بازیکن
 * مسئولیت مدیریت حرکت، شلیک و قدرت‌های بازیکن
 */
public class PlayerComponent extends Component {

    // تنظیمات پایه بازیکن
    private double speed = 300;
    private boolean canShoot = true;
    private double shootCooldown = 0.2;
    private boolean active = true;
    private boolean inputRegistered = false;

    // قدرت‌های فعال
    private boolean rapidFireActive = false;
    private boolean tripleShotActive = false;
    private boolean shieldActive = false;
    private boolean bulletShieldActive = false;
    private double powerUpTimer = 0;

    @Override
    public void onAdded() {
        // راه‌اندازی کنترل‌ها فقط یک بار
        if (!inputRegistered) {
            setupInput();
            inputRegistered = true;
        }
    }

    /**
     * تنظیم کنترل‌های صفحه کلید
     */
    private void setupInput() {
        Input input = FXGL.getInput();

        // حرکت به چپ (کلید A)
        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                if (active && entity != null) {
                    entity.translateX(-speed * 0.016);
                }
            }
        }, KeyCode.A);

        // حرکت به راست (کلید D)
        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                if (active && entity != null) {
                    entity.translateX(speed * 0.016);
                }
            }
        }, KeyCode.D);

        // حرکت به پایین (کلید S)
        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                if (active && entity != null) {
                    entity.translateY(speed * 0.016);
                }
            }
        }, KeyCode.S);

        // حرکت به بالا (کلید W)
        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                if (active && entity != null) {
                    entity.translateY(-speed * 0.016);
                }
            }
        }, KeyCode.W);

        // شلیک (کلید Space)
        input.addAction(new UserAction("Shoot") {
            @Override
            protected void onAction() {
                if (canShoot && active) {
                    shoot();
                }
            }
        }, KeyCode.SPACE);
    }

    /**
     * شلیک گلوله توسط بازیکن
     */
    private void shoot() {
        if (!active || !canShoot) return;

        MusicManager.getInstance().playSound("shoot2.wav");

        canShoot = false;

        if (tripleShotActive) {
            // شلیک سه‌تایی
            for (int i = -1; i <= 1; i++) {

                FXGL.spawn("playerBullet",
                        entity.getX() + entity.getWidth() / 2 - 5 + (i * 15),
                        entity.getY() - 20
                );
            }
        } else {
            // شلیک معمولی
            MusicManager.getInstance().playSound("shoot.wav");
            FXGL.spawn("playerBullet",
                    entity.getX() + entity.getWidth() / 2 - 5,
                    entity.getY() - 20
            );
        }

        // تنظیم کول‌داون بر اساس قدرت فعال
        double currentCooldown = rapidFireActive ? shootCooldown * 0.5 : shootCooldown;

        FXGL.getGameTimer().runOnceAfter(() -> {
            canShoot = true;
        }, Duration.seconds(currentCooldown));
    }

    /**
     * فعال‌سازی شلیک سریع
     */
    public void activeRapidFire(double duration) {
        rapidFireActive = true;
        startPowerUpTimer(duration);
        System.out.println("Rapid Fire Activated for " + duration + " seconds!");
    }

    /**
     * فعال‌سازی شلیک سه‌تایی
     */
    public void activeTripleShot(double duration) {
        tripleShotActive = true;
        startPowerUpTimer(duration);
        System.out.println("Triple Shot Activated for " + duration + " seconds!");
    }

    /**
     * فعال‌سازی محافظ
     */
    public void activateShield(double duration) {
        shieldActive = true;
        startPowerUpTimer(duration);
        System.out.println("Shield Activated for " + duration + " seconds!");
    }

    /**
     * فعال‌سازی محافظ گلوله
     */
    public void activateBulletShield(double duration) {
        bulletShieldActive = true;
        startPowerUpTimer(duration);
        System.out.println("Bullet Shield Activated for " + duration + " seconds!");
    }

    /**
     * شروع تایمر قدرت
     */
    private void startPowerUpTimer(double duration) {
        powerUpTimer = Math.max(powerUpTimer, duration);
    }

    @Override
    public void onUpdate(double tpf) {
        if (!active || entity == null) return;

        // نگه داشتن بازیکن در محدوده صفحه
        keepInBounds();

        // مدیریت تایمر قدرت‌ها
        if (powerUpTimer > 0) {
            powerUpTimer -= tpf;
            if (powerUpTimer <= 0) {
                deactivateAllPowerUps();
            }
        }
    }

    /**
     * غیرفعال کردن تمام قدرت‌ها
     */
    private void deactivateAllPowerUps() {
        rapidFireActive = false;
        tripleShotActive = false;
        shieldActive = false;
        bulletShieldActive = false;
        System.out.println("All power-ups deactivated!");
    }

    /**
     * نگه داشتن بازیکن در محدوده صفحه
     */
    private void keepInBounds() {
        if (entity.getX() < 0) entity.setX(0);
        if (entity.getX() > FXGL.getAppWidth() - entity.getWidth())
            entity.setX(FXGL.getAppWidth() - entity.getWidth());
        if (entity.getY() < 0) entity.setY(0);
        if (entity.getY() > FXGL.getAppHeight() - entity.getHeight())
            entity.setY(FXGL.getAppHeight() - entity.getHeight());
    }

    // Getter methods برای وضعیت قدرت‌ها
    public boolean hasShield() { return shieldActive; }
    public boolean hasBulletShield() { return bulletShieldActive; }
    public boolean isActive() { return active; }

    public void setActive(boolean active) {
        this.active = active;
    }
}