package com.demo.game1942;

import javafx.scene.shape.Rectangle;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class GameEntityFactory implements EntityFactory {

    @Spawns("player")
    public Entity spawnPlayer(SpawnData data) {
        Rectangle view = new Rectangle(40, 40, Color.DARKBLUE);
        PlayerComponent component = new PlayerComponent();

        return entityBuilder()
                .from(data)
                .type(EntityType.PLAYER)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(component)
                .build();

    }

    @Spawns("basicEnemy")
    public Entity spawnBasicEnemy(SpawnData data) {
        Rectangle view = new Rectangle(30, 30, Color.RED);
        EnemyComponent component = new EnemyComponent(EnemyManager.EnemyType.BASIC);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(component)
                .build();
    }

    @Spawns("playerBullet")
    public Entity spawnPlayerBullet(SpawnData data) {
        Rectangle view = new Rectangle(10, 20, Color.YELLOW);
        BulletComponent component = new BulletComponent(600, true);

        return entityBuilder()
                .from(data)
                .type(EntityType.PLAYER_BULLET)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(component)
                .build();


    }

    @Spawns("enemy")
    public Entity spawnEnemy(SpawnData data) {
        Rectangle view = new Rectangle(20, 20, Color.GREEN);
        EnemyManager enemyManager = new EnemyManager(EnemyManager.EnemyType.BASIC);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(enemyManager)
                .build();


    }

    @Spawns("enemyBullet")
    public Entity spawnEnemyBullet(SpawnData data) {
        Rectangle view = new Rectangle(8, 15, Color.BLACK);
        BulletComponent component = new BulletComponent(400, false);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY_BULLET)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(component)
                .build();


    }


    @Spawns("fastEnemy")
    public Entity spawnFastEnemy(SpawnData data) {
        Rectangle view = new Rectangle(25, 25, Color.GREENYELLOW);
        EnemyManager enemyManager = new EnemyManager(EnemyManager.EnemyType.FAST);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(enemyManager)
                .build();

    }

    @Spawns("tankEnemy")
    public Entity spawnTankEnemy(SpawnData data) {
        Rectangle view = new Rectangle(50, 50, Color.PURPLE);
        EnemyManager enemyManager = new EnemyManager(EnemyManager.EnemyType.TANK);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(enemyManager)
                .build();

    }

    @Spawns("bossEnemy")
    public Entity spawnBossEnemy(SpawnData data) {
        Rectangle view = new Rectangle(80, 80, Color.DARKRED);
        EnemyManager enemyManager = new EnemyManager(EnemyManager.EnemyType.BOSS);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(enemyManager)
                .build();


    }

    @Spawns("explosion")
    public Entity spawnExplosion(SpawnData data) {
        Circle explosion = new Circle(15, Color.RED);

        return entityBuilder()
                .from(data)
                .view(explosion)
                .with(new ExplosionComponent())
                .build();
    }

    @Spawns("powerUp")
    public Entity spawnPowerUp(SpawnData data) {
        Polygon powerUpShape = new Polygon(
                0.0, 15.0,
                10.0, 0.0,
                20.0, 15.0,
                10.0, 30.0
        );

        PowerUpComponent.PowerUpType type = (PowerUpComponent.PowerUpType) data.getData().getOrDefault("type", PowerUpComponent.PowerUpType.RAPID_FIRE);

        Color color = getPowerUpColor(type);
        powerUpShape.setFill(color);
        powerUpShape.setStroke(Color.BLACK);

        return entityBuilder()
                .from(data)
                .type(EntityType.POWER_UP)
                .viewWithBBox(powerUpShape)
                .with(new CollidableComponent(true))
                .with(new PowerUpComponent(type))
                .build();

    }


    @Spawns("bulletExplosion")
    public Entity spawnBulletExplosion(SpawnData data) {

        Circle explosion = new Circle(10, Color.ORANGE);
        explosion.setStroke(Color.YELLOW);
        explosion.setStrokeWidth(2);

        return entityBuilder()
                .from(data)
                .view(explosion)
                .with(new BulletExplosionComponent())
                .build();
    }

    private Color getPowerUpColor(PowerUpComponent.PowerUpType type) {
        switch (type) {
            case RAPID_FIRE:
                return Color.YELLOW;
            case TRIPLE_SHOT:
                return Color.BLUE;
            case SHIELD:
                return Color.CYAN;
            case EXTRA_LIFE:
                return Color.GREEN;
            case SCORE_BOOST:
                return Color.ORANGE;
            case BULLET_SHIELD:
                return Color.GOLD;
            default:
                return Color.WHITE;
        }


    }
}
