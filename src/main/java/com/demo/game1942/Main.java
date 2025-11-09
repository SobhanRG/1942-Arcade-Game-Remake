package com.demo.game1942;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


/**
 * کلاس اصلی بازی - ارث‌بری از GameApplication در FXGL
 * مسئولیت راه‌اندازی اولیه بازی و تنظیمات پایه
 */
public class Main extends GameApplication {

    // وضعیت‌های مختلف بازی
    private enum GameState {
        MAIN_MENU,
        PLAYING,
        GAME_OVER
    }

    private GameState currentState = GameState.MAIN_MENU;

    @Override
    protected void initSettings(GameSettings settings) {
        // تنظیمات پایه پنجره بازی
        settings.setHeight(800);
        settings.setWidth(600);
        settings.setTitle("1942 Remake");
        settings.setVersion("(demo)");
        settings.setFullScreenAllowed(true);
    }

    @Override
    protected void initGame() {
        // نمایش منوی اصلی در ابتدای بازی
        showMainMenu();
    }

    /**
     * نمایش منوی اصلی بازی
     */
    private void showMainMenu() {
        currentState = GameState.MAIN_MENU;

        // پاک کردن صحنه از هر گونه المان قبلی
        FXGL.getGameScene().clearUINodes();
        FXGL.getGameWorld().getEntities().forEach(Entity::removeFromWorld);

        // ایجاد عنوان بازی
        Text titleText = FXGL.getUIFactoryService().newText("1942 REMake", Color.GOLD, 48);
        titleText.setStroke(Color.DARKBLUE);
        titleText.setStrokeWidth(2);
        titleText.setTranslateX(FXGL.getAppWidth() / 2 - 180);
        titleText.setTranslateY(150);

        // ایجاد زیرعنوان
        Text subtitleText = FXGL.getUIFactoryService().newText("بازسازی کلاسیک", Color.LIGHTBLUE, 24);
        subtitleText.setTranslateX(FXGL.getAppWidth() / 2 - 80);
        subtitleText.setTranslateY(210);

        // ایجاد دکمه شروع بازی
        Button startButton = createMenuButton("شروع بازی", 300);
        startButton.setOnAction(e -> startGame());

        // ایجاد دکمه خروج
        Button exitButton = createMenuButton("خروج", 380);
        exitButton.setOnAction(e -> FXGL.getGameController().exit());

        // ایجاد راهنما
        Text controlsText = FXGL.getUIFactoryService().newText(
                "کنترل‌ها: WASD برای حرکت - Space برای شلیک",
                Color.LIGHTGRAY,
                16
        );
        controlsText.setTranslateX(FXGL.getAppWidth() / 2 - 180);
        controlsText.setTranslateY(500);

        // اضافه کردن تمام المان‌ها به صحنه
        FXGL.addUINode(titleText);
        FXGL.addUINode(subtitleText);
        FXGL.addUINode(startButton);
        FXGL.addUINode(exitButton);
        FXGL.addUINode(controlsText);

        System.out.println("Main menu displayed");
    }

    /**
     * ایجاد دکمه منو با استایل یکسان
     */
    private Button createMenuButton(String text, double yPosition) {
        Button button = new Button(text);

        // استایل دکمه
        button.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 15px 30px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-border-color: #45a049; " +
                        "-fx-border-width: 3px; " +
                        "-fx-border-radius: 10px;"
        );

        // افکت‌های hover
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-font-size: 24px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-color: #45a049; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 15px 30px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-border-color: #4CAF50; " +
                            "-fx-border-width: 3px; " +
                            "-fx-border-radius: 10px;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-font-size: 24px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-color: #4CAF50; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 15px 30px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-border-color: #45a049; " +
                            "-fx-border-width: 3px; " +
                            "-fx-border-radius: 10px;"
            );
        });

        // موقعیت‌یابی دکمه
        button.setTranslateX(FXGL.getAppWidth() / 2 - 100);
        button.setTranslateY(yPosition);

        return button;
    }

    /**
     * شروع بازی اصلی
     */
    private void startGame() {
        currentState = GameState.PLAYING;

        // پاک کردن منو
        FXGL.getGameScene().clearUINodes();

        // راه‌اندازی اجزای بازی
        FXGL.getGameWorld().addEntityFactory(new GameEntityFactory());
        FXGL.spawn("player", 400, 500);

        // اسپاونر یکپارچه برای مدیریت دشمنان و پاورآپ‌ها
        FXGL.entityBuilder().with(new UnifiedSpawner()).buildAndAttach();

        // راه‌اندازی مدیریت بازی و سیستم برخورد
        GameManager.getInstance().initialize();
        CollisionSystem.initCollisions();

        System.out.println("Game started successfully!");
    }

    /**
     * بازگشت به منوی اصلی
     */
    public void returnToMainMenu() {
        showMainMenu();
    }

    /**
     * دریافت وضعیت فعلی بازی
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * متد اصلی اجرای بازی
     */
    /**
     * متد اصلی اجرای بازی
     */
    public static void main(String[] args) {
        launch(args);
    }
}