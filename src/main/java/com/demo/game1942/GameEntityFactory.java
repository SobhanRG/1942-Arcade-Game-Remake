package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * فکتوری ساخت تمام موجودیت‌های بازی
 * مسئولیت ایجاد و پیکربندی تمام انواع موجودیت‌ها
 */
public class GameEntityFactory implements EntityFactory {

    /**
     * ساخت موجودیت بازیکن
     */
    @Spawns("player")
    public Entity spawnPlayer(SpawnData data) {
        // شکل بصری بازیکن
        Texture playerTexture = FXGL.texture("player.png");
        playerTexture.setScaleY(1.1);
        playerTexture.setScaleX(1.1);

        System.out.println("Texture loaded successfully: " + playerTexture.getWidth() + "x" + playerTexture.getHeight());

            return FXGL.entityBuilder()
                    .from(data)
                    .type(EntityType.PLAYER)
                    .viewWithBBox(playerTexture)
                    .with(new CollidableComponent(true))
                    .with(new PlayerComponent())
                    .build();


    }

    /**
     * ساخت دشمن پایه
     */
    @Spawns("basicEnemy")
    public Entity spawnBasicEnemy(SpawnData data) {
        Texture basicEnemyTexture = FXGL.texture("basic.png");
        basicEnemyTexture.setScaleY(1.6);
        basicEnemyTexture.setScaleX(1.6);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(basicEnemyTexture)
                .with(new CollidableComponent(true))
                .with(new EnemyManager(EnemyManager.EnemyType.BASIC))
                .build();
    }

    /**
     * ساخت دشمن سریع
     */
    @Spawns("fastEnemy")
    public Entity spawnFastEnemy(SpawnData data) {
        Texture fastEnemyTexture = FXGL.texture("fast.png");
        fastEnemyTexture.setScaleX(1.4);
        fastEnemyTexture.setScaleY(1.4);


        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(fastEnemyTexture)
                .with(new CollidableComponent(true))
                .with(new EnemyManager(EnemyManager.EnemyType.FAST))
                .build();
    }

    /**
     * ساخت دشمن مقاوم (تانک)
     */
    @Spawns("tankEnemy")
    public Entity spawnTankEnemy(SpawnData data) {
        Texture tankEnemyTexture = FXGL.texture("tank.png");
        tankEnemyTexture.setScaleY(1.2);
        tankEnemyTexture.setScaleX(1.2);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(tankEnemyTexture)
                .with(new CollidableComponent(true))
                .with(new EnemyManager(EnemyManager.EnemyType.TANK))
                .build();
    }

    /**
     * ساخت دشمن اصلی (باس)
     */
    @Spawns("bossEnemy")
    public Entity spawnBossEnemy(SpawnData data) {
        Texture bossEnemyTexture = FXGL.texture("boss.png");
        bossEnemyTexture.setScaleY(2);
        bossEnemyTexture.setScaleX(2);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(bossEnemyTexture)
                .with(new CollidableComponent(true))
                .with(new EnemyManager(EnemyManager.EnemyType.BOSS))
                .build();
    }

    /**
     * ساخت گلوله بازیکن
     */
    @Spawns("playerBullet")
    public Entity spawnPlayerBullet(SpawnData data) {
        Texture playerBulletTexture = FXGL.texture("playerbullet.png");

        return entityBuilder()
                .from(data)
                .type(EntityType.PLAYER_BULLET)
                .viewWithBBox(playerBulletTexture)
                .with(new CollidableComponent(true))
                .with(new BulletComponent(600, true)) // سرعت بالا، متعلق به بازیکن
                .build();
    }

    /**
     * ساخت گلوله دشمن
     */
    @Spawns("enemyBullet")
    public Entity spawnEnemyBullet(SpawnData data) {
        Texture enemyBulletTexture = FXGL.texture("enemyBullet.png");

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY_BULLET)
                .viewWithBBox(enemyBulletTexture)
                .with(new CollidableComponent(true))
                .with(new BulletComponent(400, false)) // سرعت متوسط، متعلق به دشمن
                .build();
    }

    /**
     * ساخت انفجار
     */
    @Spawns("explosion")
    public Entity spawnExplosion(SpawnData data) {
        Texture explosionTexture = FXGL.texture("explosion.png");
        explosionTexture.setScaleX(7);
        explosionTexture.setScaleY(7);

        return entityBuilder()
                .from(data)
                .viewWithBBox(explosionTexture)
                .with(new ExplosionComponent())
                .build();
    }

    /**
     * ساخت آیتم تقویتی
     */
    @Spawns("powerUp")
    public Entity spawnPowerUp(SpawnData data) {
        Texture powerUpTexture = FXGL.texture("powerUp.png");

        // دریافت نوع پاورآپ از داده‌های اسپاون
        PowerUpComponent.PowerUpType type = (PowerUpComponent.PowerUpType)
                data.getData().getOrDefault("type", PowerUpComponent.PowerUpType.RAPID_FIRE);

        return entityBuilder()
                .from(data)
                .type(EntityType.POWER_UP)
                .viewWithBBox(powerUpTexture)
                .with(new CollidableComponent(true))
                .with(new PowerUpComponent(type))
                .build();
    }

    /**
     * ساخت انفجار گلوله (برای برخورد گلوله‌ها)
     */
    @Spawns("bulletExplosion")
    public Entity spawnBulletExplosion(SpawnData data) {


        return entityBuilder()
                .from(data)

                .with(new BulletExplosionComponent())
                .build();
    }

    @Spawns("background")
    public Entity spawnBackground(SpawnData data) {
        Image skyImage = FXGL.image("sky.png");

        ImagePattern pattern = new ImagePattern(skyImage, 0, 0, 256, 256, false);

        Rectangle background = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight());
        background.setFill(pattern);

        return FXGL.entityBuilder()
                .at(0, 0)
                .view(background)
                .zIndex(-1000)
                .build();
    }
}