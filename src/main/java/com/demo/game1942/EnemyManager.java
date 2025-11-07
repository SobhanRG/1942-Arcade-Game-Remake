package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

public class EnemyManager extends Component {

    enum EnemyType {
        BASIC(100, 50, 1, 2.0),
        FAST(200, 100, 2, 1.0),
        TANK(50, 150, 3, 1.5),
        BOSS(80, 500, 10, 0.8);

        final double speed;
        final int score;
        final int damage;
        final double shootCooldown;

        EnemyType(double speed, int score, int damage, double cooldown) {
            this.speed = speed;
            this.score = score;
            this.damage = damage;
            this.shootCooldown = cooldown;
        }
    }

    private EnemyType type;
    private double timeToShoot = 0;
    private double movementTime = 0;
    private boolean hasDived = false;

    public EnemyManager() {
        this(EnemyType.BASIC);
    }

    public EnemyManager(EnemyType type) {
        this.type = type;
        this.timeToShoot = type.shootCooldown;
    }

    @Override
    public void onUpdate(double tpf) {
        if (entity == null) return;

        movementTime += tpf;
        timeToShoot -= tpf;

        updateMovement(tpf);

        if (timeToShoot <= 0) {
            shoot();
            timeToShoot = type.shootCooldown;
        }
        checkBounds();

    }

    private void spawnPowerUpOnDeath() {
        double x = entity.getX() + entity.getWidth() / 2;
        double y = entity.getY() + entity.getHeight() / 2;

        PowerUpComponent.PowerUpType[] commonTypes = {
                PowerUpComponent.PowerUpType.RAPID_FIRE,
                PowerUpComponent.PowerUpType.TRIPLE_SHOT,
                PowerUpComponent.PowerUpType.SCORE_BOOST,
                PowerUpComponent.PowerUpType.BULLET_SHIELD,

        };

        PowerUpComponent.PowerUpType selectedType = commonTypes[FXGL.random(0, commonTypes.length - 1)];

        FXGL.spawn("powerUp", new SpawnData(x, y).put("type", selectedType));
    }

    private void updateMovement(double tpf) {
        switch (type) {
            case BASIC -> moveStraightDown(tpf);
            case FAST -> moveFastPattern(tpf);
            case TANK -> moveStraightDown(tpf * 0.5f);
            case BOSS -> moveBossPattern(tpf);
        }
    }

    private void moveStraightDown(double tpf) {
        entity.translateY(type.speed * tpf);
    }

    private void moveFastPattern(double tpf) {
        entity.translateY(type.speed * tpf);
        entity.translateX(Math.sin(movementTime * 5) * 100 * tpf);
    }

    private void moveBossPattern(double tpf) {
        double centerX = (float) FXGL.getAppWidth() / 2;

        if (entity.getY() < 100) {
            entity.translateY(type.speed * tpf);
        } else {
            double targetX = centerX + Math.sin(movementTime) * 200;
            double currentX = entity.getX();
            double dx = (targetX - currentX) * 0.05;

            entity.translateX(dx * type.speed * tpf);
            entity.translateY(type.speed * tpf * 0.1f);
        }

    }

    private void shoot() {
        switch (type) {
            case BASIC, FAST -> shootSingleBullet();
            case TANK -> shootTripleBullet();
            case BOSS -> shootCirclePattern(8);
        }
    }

    private void shootSingleBullet() {
        FXGL.spawn("enemyBullet", entity.getX() + entity.getWidth() / 2 - 4, entity.getY() + entity.getHeight());
    }

    private void shootTripleBullet() {
        for (int i = -1; i <= 1; i++) {
            FXGL.spawn("enemyBullet", entity.getX() + entity.getWidth() / 2 - 4 + (i * 20), entity.getY() + entity.getHeight());
        }
    }

    private void shootCirclePattern(int bulletCounts) {
        for (int i = 0; i < bulletCounts; i++) {
            double angle = (2 * Math.PI / bulletCounts) * i;
            final int index = i;

            FXGL.getGameTimer().runOnceAfter(() -> {
            spawnDirectionalBullet(angle);
            }, Duration.seconds(index * i));
        }
    }

    private void spawnDirectionalBullet(double angle) {
        if (entity == null) return;

        double bulletSpeed = 150;
        double dx = Math.sin(angle) * bulletSpeed;
        double dy = Math.cos(angle) * bulletSpeed;

        FXGL.spawn("enemyBullet",entity.getX() + entity.getWidth()/ 2 - 4, entity.getY() + entity.getHeight());
    }

    private void checkBounds() {
        if (entity.getY() > FXGL.getAppHeight() + 100 ||
                entity.getX() < -100 ||
                entity.getX() > FXGL.getAppWidth() + 100) {
            entity.removeFromWorld();
        }
    }

    public void onRemoved() {
        if (FXGL.random(0.0, 1.0) < 0.25) {
            spawnPowerUpOnDeath();
        }
    }

    public EnemyType getType() {
        return type;
    }

    public int getScoreValue() {
        return type.score;
    }

    public int getDamage() {
        return type.damage;
    }
}











































