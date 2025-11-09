package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;

import javafx.util.Duration;

/**
 * سیستم اسپاون یکپارچه - مدیریت تمام اسپاون‌های بازی
 * جایگزین تمام اسپاونرهای قبلی
 */
public class UnifiedSpawner extends Component {

    // تایمرهای اسپاون
    private double enemySpawnTimer = 2.0;
    private double powerUpTimer = 15.0;
    private double bossSpawnTimer = 60.0; // اسپاون باس هر 60 ثانیه

    // وضعیت موج‌ها
    private int waveNumber = 1;
    private int enemiesInWave = 2;

    @Override
    public void onUpdate(double tpf) {
        enemySpawnTimer -= tpf;
        powerUpTimer -= tpf;
        bossSpawnTimer -= tpf;

        if (enemySpawnTimer <= 0) {
            spawnWave();
            enemySpawnTimer = calculateSpawnDelay();
        }

        if (bossSpawnTimer <= 0) {
            spawnBoss();

        }

        // اسپاون پاورآپ هر 50-40 ثانیه
        if (powerUpTimer <= 0) {
            spawnRandomPowerUp();
            powerUpTimer = FXGL.random(40.0, 50.0); // زمان تصادفی بین اسپاون‌ها
        }
    }

    private void spawnWave() {
        for (int i = 0; i < enemiesInWave; i++) {
            final int enemyIndex = i;
            FXGL.getGameTimer().runOnceAfter(() -> {
                spawnEnemy();

                // بعد از کشته شدن هر 15 دشمن، شانس اسپاون پاورآپ
                if (FXGL.random(0.0, 15.0) < 0.1) { // 10% chance
                    FXGL.getGameTimer().runOnceAfter(() -> {
                        spawnRandomPowerUp();
                    }, Duration.seconds(0.5));
                }
            }, Duration.seconds(i * 0.8));
        }

        waveNumber++;
        enemiesInWave = Math.min(5 + waveNumber, 15);

        if (waveNumber % 5 == 0) {
            FXGL.getGameTimer().runOnceAfter(() -> {
                spawnBoss();
            }, Duration.seconds(3));
        }
    }

    private void spawnEnemy() {
        String enemyType = selectEnemyType();
        int x = FXGL.random(50, FXGL.getAppWidth() - 50);

        FXGL.spawn(enemyType, x, -50);
    }

    private void spawnBoss() {
        FXGL.spawn("bossEnemy", FXGL.getAppWidth() / 2 - 40, -100);
    }

    // **متد جدید برای اسپاون پاورآپ**
    private void spawnRandomPowerUp() {
        try {
            int x = FXGL.random(80, FXGL.getAppWidth() - 80);
            int y = -30;

            PowerUpComponent.PowerUpType[] availableTypes = {
                    PowerUpComponent.PowerUpType.RAPID_FIRE,
                    PowerUpComponent.PowerUpType.TRIPLE_SHOT,
                    PowerUpComponent.PowerUpType.SHIELD,
                    PowerUpComponent.PowerUpType.EXTRA_LIFE,
                    PowerUpComponent.PowerUpType.SCORE_BOOST,
                    PowerUpComponent.PowerUpType.BULLET_SHIELD
            };

            // انتخاب تصادفی نوع پاورآپ
            PowerUpComponent.PowerUpType selectedType = availableTypes[FXGL.random(0, availableTypes.length - 1)];

            System.out.println("Spawning PowerUp: " + selectedType + " at (" + x + ", " + y + ")");

            // اسپاون پاورآپ با نوع انتخاب شده
            FXGL.spawn("powerUp", new SpawnData(x, y).put("type", selectedType));

        } catch (Exception e) {
            System.out.println("Error spawning power-up: " + e.getMessage());
        }
    }

    private String selectEnemyType() {
        double rand = FXGL.random();

        if (waveNumber >= 10 && rand < 0.1) return "bossEnemy";
        else if (waveNumber >= 7 && rand < 0.2) return "tankEnemy";
        else if (waveNumber >= 4 && rand < 0.4) return "fastEnemy";
        else return "basicEnemy";
    }

    private double calculateSpawnDelay() {
        return Math.max(1.0, 3.0 - (waveNumber * 0.1));
    }
}