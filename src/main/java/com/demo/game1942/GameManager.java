package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.demo.game1942.UnifiedSpawner;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * سیستم مدیریت وضعیت بازی - Singleton Pattern
 * مسئولیت مدیریت امتیاز، جان‌ها، مرحله و وضعیت بازی
 */
public class GameManager {

    private static GameManager instance;

    // متغیرهای وضعیت بازی
    private int score = 0;
    private int playerLives = 3;
    private int currentStage = 1;
    private boolean isGameOver = false;
    private int bulletDestroyed = 0;
    private int enemiesDestroyed = 0;
    private int powerUpsCollected = 0;

    // المان‌های UI
    private Text scoreText;
    private Text livesText;
    private Text stageText;

    // Constructor خصوصی برای الگوی Singleton
    private GameManager() {}

    /**
     * دریافت نمونه یکتای GameManager
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * راه‌اندازی اولیه سیستم مدیریت بازی
     */
    public void initialize() {
        createUI();
        System.out.println("GameManager initialized!");
    }

    /**
     * ایجاد رابط کاربری بازی
     */
    private void createUI() {
        // ایجاد متن امتیاز
        scoreText = FXGL.getUIFactoryService().newText("Score: 0", Color.BISQUE, 16);
        scoreText.setTranslateX(10);
        scoreText.setTranslateY(30);

        // ایجاد متن جان‌ها
        livesText = FXGL.getUIFactoryService().newText("Lives: 3", Color.BISQUE, 16);
        livesText.setTranslateX(10);
        livesText.setTranslateY(50);

        // ایجاد متن مرحله
        stageText = FXGL.getUIFactoryService().newText("Stage: 1", Color.BISQUE, 16);
        stageText.setTranslateX(10);
        stageText.setTranslateY(70);

        // اضافه کردن المان‌ها به صحنه
        FXGL.addUINode(scoreText);
        FXGL.addUINode(livesText);
        FXGL.addUINode(stageText);

        System.out.println("UI created successfully!");
    }

    /**
     * افزایش امتیاز بازیکن
     */
    public void addScore(int points) {
        score += points;
        updateUI();
        checkStageProgress();
    }

    /**
     * اعمال آسیب به بازیکن
     */
    public void takeDamage(int damage) {
        try {
            if (isGameOver) return;

            playerLives -= damage;
            System.out.println("Damage taken! Lives left: " + playerLives);

            if (playerLives <= 0) {
                playerLives = 0;
                gameOver();
            }
            updateUI();
        } catch (Exception e) {
            System.out.println("Error in takeDamage: " + e.getMessage());
        }
    }

    /**
     * افزودن جان اضافی به بازیکن
     */
    public void addLife() {
        playerLives = Math.min(playerLives + 1, 5);
        updateUI();
        showFloatingText("Extra Life!", Color.GREEN);
    }

    /**
     * به‌روزرسانی رابط کاربری
     */
    public void updateUI() {
        scoreText.setText("Score: " + score);
        livesText.setText("Lives: " + playerLives);
        stageText.setText("Stage: " + currentStage);
    }

    /**
     * بررسی پیشرفت مرحله بر اساس امتیاز
     */
    private void checkStageProgress() {
        int scoreForNextStage = currentStage * 1000;
        if (score >= scoreForNextStage) {
            currentStage++;
            showFloatingText("Stage " + currentStage + "!", Color.GOLD);
            updateUI();
        }
    }

    /**
     * مدیریت پایان بازی
     */
    public void gameOver() {
        if (isGameOver) return;

        isGameOver = true;
        System.out.println("Game Over triggered!");

        // پاک‌سازی UI موجود
        FXGL.getGameScene().clearUINodes();

        // ایجاد متن Game Over
        Text gameOverText = FXGL.getUIFactoryService().newText("GAME OVER", Color.RED, 48);
        gameOverText.setTranslateX(FXGL.getAppWidth() / 2 - 150);
        gameOverText.setTranslateY(FXGL.getAppHeight() / 2 - 50);

        // ایجاد متن امتیاز نهایی
        Text finalScoreText = FXGL.getUIFactoryService().newText("Final Score: " + score, Color.BURLYWOOD, 24);
        finalScoreText.setTranslateX(FXGL.getAppWidth() / 2 - 100);
        finalScoreText.setTranslateY(FXGL.getAppHeight() / 2 + 20);

        // ایجاد دکمه شروع مجدد
        Button restartButton = new Button("exit");
        restartButton.setStyle("-fx-font-size: 18; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        restartButton.setPrefWidth(150);
        restartButton.setPrefHeight(40);
        restartButton.setTranslateX(FXGL.getAppWidth() / 2 - 75);
        restartButton.setTranslateY(FXGL.getAppHeight() / 2 + 80);

        // اضافه کردن المان‌های UI پایان بازی
        FXGL.addUINode(gameOverText);
        FXGL.addUINode(finalScoreText);
        FXGL.addUINode(restartButton);

        // پاک‌سازی موجودیت‌های بازی
        stopGameEntities();
        disablePlayerControls();

        // توقف موتور بازی بعد از ۲ ثانیه
        FXGL.getGameTimer().runOnceAfter(() -> {
            FXGL.getGameController().pauseEngine();
        }, Duration.seconds(1));
    }

    /**
     * متوقف کردن تمام موجودیت‌های بازی
     */
    private void stopGameEntities() {
        try {
            FXGL.getGameWorld().getEntitiesByType(EntityType.ENEMY).forEach(Entity::removeFromWorld);
            FXGL.getGameWorld().getEntitiesByType(EntityType.PLAYER_BULLET).forEach(Entity::removeFromWorld);
            FXGL.getGameWorld().getEntitiesByType(EntityType.ENEMY_BULLET).forEach(Entity::removeFromWorld);
        } catch (Exception e) {
            System.out.println("Error stopping entities: " + e.getMessage());
        }
    }

    /**
     * غیرفعال کردن کنترل‌های بازیکن
     */
    private void disablePlayerControls() {
        try {
            Entity player = FXGL.getGameWorld().getEntitiesByType(EntityType.PLAYER).get(0);
            if (player != null) {
                PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
                if (playerComp != null) {
                    playerComp.setActive(false);
                }
            }
        } catch (Exception e) {
            System.out.println("Error disabling player controls: " + e.getMessage());
        }
    }

    /**
     * نمایش متن شناور برای رویدادها
     */
    public void showFloatingText(String text, Color color) {
        Text floatingText = FXGL.getUIFactoryService().newText(text, color, 16);
        floatingText.setTranslateX(FXGL.getAppWidth() / 2 - 50);
        floatingText.setTranslateY(FXGL.getAppHeight() / 2);

        FXGL.addUINode(floatingText);

        // انیمیشن حرکت به بالا
        TranslateTransition move = new TranslateTransition(Duration.seconds(1.5), floatingText);
        move.setToY(floatingText.getTranslateY() - 100);

        // انیمیشن محو شدن
        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), floatingText);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> FXGL.removeUINode(floatingText));

        move.play();
        fade.play();
    }

    /**
     * فعال کردن امتیاز دوبرابر
     */
    public void activateDoubleScore(double duration) {
        addScore(500);
        showFloatingText("Double Score!", Color.YELLOW);
    }

    /**
     * افزایش امتیاز نابودی گلوله
     */
    public void addBulletDestructionScore() {
        bulletDestroyed++;
        addScore(25);
        showFloatingText("Bullet Block +25", Color.ORANGE);
    }

    // Getter methods
    public int getCurrentStage() { return currentStage; }
    public int getPlayerLives() { return playerLives; }
    public boolean isGameOver() { return isGameOver; }
    public int getScore() { return score; }
}