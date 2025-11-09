package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.demo.game1942.BulletComponent;
import javafx.scene.paint.Color;
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
        System.out.println("Spawning player at (" + data.getX() + ", " + data.getY() + ")");

        // شکل بصری بازیکن
        Rectangle view = new Rectangle(40, 40, Color.DARKBLUE);
        view.setStroke(Color.WHITE);
        view.setStrokeWidth(2);

        return entityBuilder()
                .from(data)
                .type(EntityType.PLAYER)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(new PlayerComponent())
                .build();
    }

    /**
     * ساخت دشمن پایه
     */
    @Spawns("basicEnemy")
    public Entity spawnBasicEnemy(SpawnData data) {
        Rectangle view = new Rectangle(30, 30, Color.RED);
        view.setStroke(Color.DARKRED);
        view.setStrokeWidth(1);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(new EnemyManager(EnemyManager.EnemyType.BASIC))
                .build();
    }

    /**
     * ساخت دشمن سریع
     */
    @Spawns("fastEnemy")
    public Entity spawnFastEnemy(SpawnData data) {
        Rectangle view = new Rectangle(25, 25, Color.ORANGE);
        view.setStroke(Color.DARKORANGE);
        view.setStrokeWidth(1);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(new EnemyManager(EnemyManager.EnemyType.FAST))
                .build();
    }

    /**
     * ساخت دشمن مقاوم (تانک)
     */
    @Spawns("tankEnemy")
    public Entity spawnTankEnemy(SpawnData data) {
        Rectangle view = new Rectangle(50, 50, Color.PURPLE);
        view.setStroke(Color.DARKVIOLET);
        view.setStrokeWidth(2);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(new EnemyManager(EnemyManager.EnemyType.TANK))
                .build();
    }

    /**
     * ساخت دشمن اصلی (باس)
     */
    @Spawns("bossEnemy")
    public Entity spawnBossEnemy(SpawnData data) {
        Rectangle view = new Rectangle(80, 80, Color.DARKRED);
        view.setStroke(Color.RED);
        view.setStrokeWidth(3);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(new EnemyManager(EnemyManager.EnemyType.BOSS))
                .build();
    }

    /**
     * ساخت گلوله بازیکن
     */
    @Spawns("playerBullet")
    public Entity spawnPlayerBullet(SpawnData data) {
        Rectangle view = new Rectangle(8, 20, Color.CYAN);
        view.setStroke(Color.BLUE);
        view.setStrokeWidth(1);

        return entityBuilder()
                .from(data)
                .type(EntityType.PLAYER_BULLET)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(new BulletComponent(600, true)) // سرعت بالا، متعلق به بازیکن
                .build();
    }

    /**
     * ساخت گلوله دشمن
     */
    @Spawns("enemyBullet")
    public Entity spawnEnemyBullet(SpawnData data) {
        Rectangle view = new Rectangle(8, 15, Color.ORANGE);
        view.setStroke(Color.RED);
        view.setStrokeWidth(1);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY_BULLET)
                .viewWithBBox(view)
                .with(new CollidableComponent(true))
                .with(new BulletComponent(400, false)) // سرعت متوسط، متعلق به دشمن
                .build();
    }

    /**
     * ساخت انفجار
     */
    @Spawns("explosion")
    public Entity spawnExplosion(SpawnData data) {
        Circle explosion = new Circle(20, Color.RED);
        explosion.setStroke(Color.ORANGE);
        explosion.setStrokeWidth(3);

        return entityBuilder()
                .from(data)
                .view(explosion)
                .with(new ExplosionComponent())
                .build();
    }

    /**
     * ساخت آیتم تقویتی
     */
    @Spawns("powerUp")
    public Entity spawnPowerUp(SpawnData data) {
        // ایجاد شکل الماس برای پاورآپ
        Polygon powerUpShape = new Polygon(
                0.0, 15.0,    // بالا
                10.0, 0.0,    // راست-بالا
                20.0, 15.0,   // راست-پایین
                10.0, 30.0    // پایین
        );

        // دریافت نوع پاورآپ از داده‌های اسپاون
        PowerUpComponent.PowerUpType type = (PowerUpComponent.PowerUpType)
                data.getData().getOrDefault("type", PowerUpComponent.PowerUpType.RAPID_FIRE);

        // تنظیم رنگ بر اساس نوع پاورآپ
        Color color = getPowerUpColor(type);
        powerUpShape.setFill(color);
        powerUpShape.setStroke(Color.BLACK);
        powerUpShape.setStrokeWidth(1);

        return entityBuilder()
                .from(data)
                .type(EntityType.POWER_UP)
                .viewWithBBox(powerUpShape)
                .with(new CollidableComponent(true))
                .with(new PowerUpComponent(type))
                .build();
    }

    /**
     * دریافت رنگ متناسب با نوع پاورآپ
     */
    private Color getPowerUpColor(PowerUpComponent.PowerUpType type) {
        switch (type) {
            case RAPID_FIRE: return Color.YELLOW;
            case TRIPLE_SHOT: return Color.BLUE;
            case SHIELD: return Color.CYAN;
            case EXTRA_LIFE: return Color.GREEN;
            case SCORE_BOOST: return Color.ORANGE;
            case BULLET_SHIELD: return Color.GOLD;
            default: return Color.WHITE;
        }
    }

    /**
     * ساخت انفجار گلوله (برای برخورد گلوله‌ها)
     */
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
}