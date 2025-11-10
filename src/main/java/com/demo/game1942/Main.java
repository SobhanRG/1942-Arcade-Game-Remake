package com.demo.game1942;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;


/**
 * کلاس اصلی بازی - ارث‌بری از GameApplication در FXGL
 * مسئولیت راه‌اندازی اولیه بازی و تنظیمات پایه
 */
public class Main extends GameApplication {

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
        // تنظیم رنگ پس‌زمینه
        FXGL.getGameScene().setBackgroundColor(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(1, Color.LIGHTBLUE), new Stop(0, Color.ALICEBLUE)));

        // راه‌اندازی اولیه اجزای بازی
        FXGL.getGameWorld().addEntityFactory(new GameEntityFactory());
        FXGL.spawn("player", 400, 500); // اسپاون بازیکن در موقعیت اولیه

        // اسپاونر یکپارچه برای مدیریت دشمنان و پاورآپ‌ها
        FXGL.entityBuilder().with(new UnifiedSpawner()).buildAndAttach();

        // راه‌اندازی مدیریت بازی و سیستم برخورد
        GameManager.getInstance().initialize();
        CollisionSystem.initCollisions();

        System.out.println("Game initialized successfully!");
    }

    /**
     * متد اصلی اجرای بازی
     */
    public static void main(String[] args) {
        launch(args);
    }
}