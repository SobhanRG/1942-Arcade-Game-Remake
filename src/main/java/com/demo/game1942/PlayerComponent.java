package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
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
                    entity.translateX(-speed  * 0.016);
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

        // بازگشت به منو (کلید F)
        input.addAction(new UserAction("Pause Menu") {
            @Override
            protected void onAction() {
                showPauseMenu();
            }
        }, KeyCode.F1);
    }

    /**
     * نمایش منوی توقف بازی
     */
    private void showPauseMenu() {
        if (!active) return; // اگر بازی فعال نیست، منو نشان نده

        System.out.println("Pause menu activated");

        // ایجاد overlay تیره
        javafx.scene.shape.Rectangle overlay = new javafx.scene.shape.Rectangle(
                FXGL.getAppWidth(), FXGL.getAppHeight(),
                javafx.scene.paint.Color.rgb(0, 0, 0, 0.7)
        );

        // ایجاد متن توقف
        javafx.scene.text.Text pauseText = FXGL.getUIFactoryService().newText(
                "Game Paused",
                javafx.scene.paint.Color.WHITE,
                36
        );
        pauseText.setTranslateX(FXGL.getAppWidth() / 2 - 100);
        pauseText.setTranslateY(FXGL.getAppHeight() / 2 - 100);

        // ایجاد دکمه ادامه
        javafx.scene.control.Button resumeButton = createPauseButton("ادامه بازی", -30);
        javafx.scene.control.Button menuButton = createPauseButton("منوی اصلی", 40);

        // ایجاد دکمه منوی اصلی
        menuButton.setOnAction(e -> returnToMainMenu());
        resumeButton.setOnAction(e -> hidePauseMenu(overlay, pauseText, resumeButton, menuButton));

        // اضافه کردن المان‌ها به صحنه
        FXGL.addUINode(overlay);
        FXGL.addUINode(pauseText);
        FXGL.addUINode(resumeButton);
        FXGL.addUINode(menuButton);

        // توقف موقت بازی
        FXGL.getGameController().pauseEngine();
    }

    /**
     * ایجاد دکمه برای منوی توقف
     */
    private javafx.scene.control.Button createPauseButton(String text, double yOffset) {
        javafx.scene.control.Button button = new javafx.scene.control.Button(text);

        button.setStyle(
                "-fx-font-size: 20px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #2196F3; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: #1976D2; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 8px;"
        );

        button.setTranslateX(FXGL.getAppWidth() / 2 - 80);
        button.setTranslateY(FXGL.getAppHeight() / 2 + yOffset);

        return button;
    }

    /**
     * پنهان کردن منوی توقف
     */
    private void hidePauseMenu(javafx.scene.Node... nodes) {
        for (javafx.scene.Node node : nodes) {
            FXGL.removeUINode(node);
        }
        // ادامه بازی
        FXGL.getGameController().resumeEngine();
    }

    /**
     * بازگشت به منوی اصلی
     */
    private void returnToMainMenu() {
        try {
            // پاک‌سازی
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameWorld().getEntities().forEach(Entity::removeFromWorld);

            // بازگشت به منوی اصلی
            Main main = (Main) FXGL.getApp();
            main.returnToMainMenu();

            // ادامه بازی (برای منو)
            FXGL.getGameController().resumeEngine();

        } catch (Exception e) {
            System.out.println("Error returning to main menu: " + e.getMessage());
        }
    }

    /**
     * شلیک گلوله توسط بازیکن
     */
    private void shoot() {
        if (!active || !canShoot) return;

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