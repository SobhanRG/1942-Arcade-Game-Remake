package com.demo.game1942;

import com.almasb.fxgl.core.math.FXGLMath;
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
    private int enemiesInWave = 3;
    private boolean isSpawningWave = false;

    // تنظیمات موج‌ها
    private static final int[] ENEMIES_PER_WAVE = {3, 5, 7, 10, 12, 15};
    private static final double[] WAVE_COOLDOWNS = {3.0, 2.5, 2.0, 1.5, 1.0, 0.8};

    @Override
    public void onUpdate(double tpf) {
        // کاهش تایمرها
        enemySpawnTimer -= tpf;
        powerUpTimer -= tpf;
        bossSpawnTimer -= tpf;

        // اسپاون دشمنان
        if (enemySpawnTimer <= 0 && !isSpawningWave) {
            startNewWave();
            enemySpawnTimer = getWaveCooldown();
        }

        // اسپاون پاورآپ
        if (powerUpTimer <= 0) {
            spawnRandomPowerUp();
            powerUpTimer = FXGLMath.random(15.0, 25.0);
        }

        // اسپاون باس
        if (bossSpawnTimer <= 0) {
            spawnBoss();
            bossSpawnTimer = 60.0; // ریست تایمر باس
        }
    }

    /**
     * شروع موج جدید دشمنان
     */
    private void startNewWave() {
        isSpawningWave = true;
        enemiesInWave = getEnemiesInWave();

        System.out.println("Starting Wave " + waveNumber + " with " + enemiesInWave + " enemies");

        spawnWaveEnemies();
    }

    /**
     * اسپاون دشمنان موج جاری
     */
    private void spawnWaveEnemies() {
        for (int i = 0; i < enemiesInWave; i++) {
            final int enemyIndex = i;

            // تأخیر برای اسپاون تدریجی
            FXGL.getGameTimer().runOnceAfter(() -> {
                spawnEnemyWithPattern(enemyIndex);

                // پس از اسپاون آخرین دشمن، موج پایان می‌یابد
                if (enemyIndex == enemiesInWave - 1) {
                    finishWave();
                }
            }, Duration.seconds(i * 0.8));
        }
    }

    /**
     * اسپاون دشمن با الگوی حرکت مشخص
     */
    private void spawnEnemyWithPattern(int index) {
        double x = calculateSpawnX(index);
        String enemyType = selectEnemyTypeForWave();

        FXGL.spawn(enemyType, x, -50);
    }

    /**
     * محاسبه موقعیت افقی اسپاون
     */
    private double calculateSpawnX(int index) {
        double screenWidth = FXGL.getAppWidth();
        double spacing = screenWidth / (enemiesInWave + 1);
        double baseX = spacing * (index + 1);

        // اضافه کردن کمی randomness
        return baseX + FXGLMath.random(-30, 30);
    }

    /**
     * انتخاب نوع دشمن بر اساس موج جاری
     */
    private String selectEnemyTypeForWave() {
        double rand = FXGLMath.randomDouble();

        if (waveNumber >= 6 && rand < 0.15) {
            return "bossEnemy";
        } else if (waveNumber >= 4 && rand < 0.3) {
            return "tankEnemy";
        } else if (waveNumber >= 2 && rand < 0.5) {
            return "fastEnemy";
        } else {
            return "basicEnemy";
        }
    }

    /**
     * اسپاون پاورآپ تصادفی
     */
    private void spawnRandomPowerUp() {
        try {
            int x = FXGLMath.random(80, FXGL.getAppWidth() - 80);
            int y = -30;

            // تمام انواع پاورآپ موجود
            PowerUpComponent.PowerUpType[] allTypes = PowerUpComponent.PowerUpType.values();
            PowerUpComponent.PowerUpType selectedType = allTypes[FXGLMath.random(0, allTypes.length - 1)];

            System.out.println("Spawning PowerUp: " + selectedType + " at (" + x + ", " + y + ")");

            FXGL.spawn("powerUp",new SpawnData(x, y));

        } catch (Exception e) {
            System.out.println("Error spawning power-up: " + e.getMessage());
        }
    }

    /**
     * اسپاون دشمن باس
     */
    private void spawnBoss() {
        double x = FXGL.getAppWidth() / 2 - 40;
        FXGL.spawn("bossEnemy", x, -100);
        System.out.println("BOSS SPAWNED!");

        // نمایش اخطار به بازیکن
        GameManager.getInstance().showFloatingText("BOSS INCOMING!", javafx.scene.paint.Color.RED);
    }

    /**
     * پایان موج جاری
     */
    private void finishWave() {
        isSpawningWave = false;
        waveNumber++;

        System.out.println("Wave " + (waveNumber - 1) + " completed. Starting next wave in " + getWaveCooldown() + " seconds");

        // نمایش پیام موج جدید
        if (waveNumber > 1) {
            GameManager.getInstance().showFloatingText("Wave " + waveNumber + "!", javafx.scene.paint.Color.GOLD);
        }
    }

    /**
     * محاسبه تعداد دشمنان در موج جاری
     */
    private int getEnemiesInWave() {
        int waveIndex = Math.min(waveNumber - 1, ENEMIES_PER_WAVE.length - 1);
        return ENEMIES_PER_WAVE[waveIndex];
    }

    /**
     * محاسبه زمان استراحت بین موج‌ها
     */
    private double getWaveCooldown() {
        int waveIndex = Math.min(waveNumber - 1, WAVE_COOLDOWNS.length - 1);
        return WAVE_COOLDOWNS[waveIndex];
    }

    /**
     * متد برای تست سریع سیستم اسپاون
     */
    public void spawnTestWave() {
        System.out.println("TEST: Spawning test wave");
        startNewWave();
    }
}