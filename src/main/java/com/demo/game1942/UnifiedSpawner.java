package com.demo.game1942;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class UnifiedSpawner extends Component {

    private double enemySpawnTimer = 2.0; //default = 0
    private double powerUpTimer = 15.0; //default = 0
    private double bossSpawnTimer = 60.0;

    private int waveNumber = 1;
    private int enemiesInWave = 3;
    private boolean isSpawningWave = false;

    private static final int[] ENEMIES_PER_WAVE = {3, 5, 7, 10, 12, 15};
    private static final double[] WAVE_COOLDOWNS = {3.0, 2.5, 2.0, 1.5, 1.0, 0.8};

    @Override
    public void onUpdate(double tpf) {
        Main main = (Main) FXGL.getApp();
        if (main.getCurrentState() != Main.GameState.PLAYING) {
            return;
        }

        enemySpawnTimer -= tpf;
        powerUpTimer -= tpf;
        bossSpawnTimer -= tpf;

        if (enemySpawnTimer <= 0 && !isSpawningWave) {
            startNewWave();
            enemySpawnTimer = getWaveCooldown();
        }

        if (powerUpTimer <= 0) {
            spawnRandomPowerUp();
            powerUpTimer = FXGL.random(15.0, 25.0);
        }

        if (bossSpawnTimer <= 0) {
            spawnBoss();
            bossSpawnTimer = 60.0;
        }
    }

    private void startNewWave() {
        isSpawningWave = true;
        enemiesInWave = getEnemiesInWave();

        System.out.println("Starting Wave " + waveNumber + " with " + enemiesInWave + " enemies");
        spawnWaveEnemies();

    }

    private void spawnWaveEnemies() {
        for (int i = 0; i < enemiesInWave; i++) {
            final int enemyIndex = i;

            FXGL.getGameTimer().runOnceAfter(() -> {
                spawnEnemyWithPattern(enemyIndex);

                if (enemyIndex == enemiesInWave - 1) {
                    finishWave();
                }

            }, Duration.seconds(i * 0.8));
        }
    }

    private void spawnEnemyWithPattern(int index) {
        double x = calculateSpawnX(index);
        String enemyType = selectEnemyTypeForWave();

        FXGL.spawn(enemyType, x, -50);
    }

    private double calculateSpawnX(int index) {
        double screenWidth = FXGL.getAppWidth();
        double spacing = screenWidth / (enemiesInWave + 1);
        double baseX = spacing * (index + 1);

        return baseX + FXGLMath.random(-30, 30);
    }

    private String selectEnemyTypeForWave() {
        double rand = FXGL.random();

        if (waveNumber >= 6 && rand < 0.15) return "bossEnemy";
        else if (waveNumber >= 4 && rand < 0.3) return "tankEnemy";
        else if (waveNumber >= 2 && rand < 0.5) return "fastEnemy";
        else return "basicEnemy";

    }

    private void spawnBoss() {
        double x = (float) FXGL.getAppWidth() / 2 - 40;
        FXGL.spawn("bossEnemy", x, -100);
        System.out.println("boss spawned!");

        GameManager.getInstance().showFloatingText("BOSS INCOMING!", Color.RED);

    }

    private double calculateSpawnDelay() {
        return Math.max(1.0, 3.0 - (waveNumber * 0.1));
    }

    private void spawnRandomPowerUp() {
        try {
            int x = FXGLMath.random(80, FXGL.getAppWidth() - 80);
            int y = -30;

            PowerUpComponent.PowerUpType[] allTypes = PowerUpComponent.PowerUpType.values();
            PowerUpComponent.PowerUpType selectType = allTypes[FXGLMath.random(0, allTypes.length - 1)];

            PowerUpComponent.PowerUpType selectedType = allTypes[FXGL.random(0, allTypes.length - 1)];

            System.out.println("Spawn PowerUp: " + selectType);

            FXGL.spawn("powerUp", new SpawnData(x, y).put("type", selectedType));

        } catch (Exception e) {
            System.out.println("Error spawning power-up: " + e.getMessage());
        }
    }

    private void finishWave() {
        isSpawningWave = false;
        waveNumber++;

        System.out.println("Wave " + (waveNumber - 1) + " completed ");

        if (waveNumber > 1) {
            GameManager.getInstance().showFloatingText("Wave " + waveNumber + "!", Color.GOLDENROD);
        }
    }

    private double getWaveCooldown() {
        int waveIndex = Math.min(waveNumber - 1, WAVE_COOLDOWNS.length - 1);
        return WAVE_COOLDOWNS[waveIndex];
    }

    private int getEnemiesInWave() {
        int waveIndex = Math.min(waveNumber - 1, ENEMIES_PER_WAVE.length - 1);
        return ENEMIES_PER_WAVE[waveIndex];
    }
}
