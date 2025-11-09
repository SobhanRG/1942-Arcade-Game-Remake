package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * سیستم مدیریت برخوردهای فیزیکی در بازی
 * مسئولیت تشخیص و مدیریت تمام برخوردهای بین موجودیت‌ها
 */
public class CollisionSystem {

    /**
     * راه‌اندازی تمام سیستم‌های برخورد بازی
     */
    public static void initCollisions() {
        initPlayerBulletEnemyCollision();
        initPlayerEnemyBulletCollision();
        initPlayerEnemyCollision();
        initPlayerPowerUpCollision();
        initPlayerBulletEnemyBulletCollision();

        System.out.println("All collision systems initialized!");
    }

    /**
     * برخورد گلوله بازیکن با دشمن
     */
    private static void initPlayerBulletEnemyCollision() {
        FXGL.getPhysicsWorld().addCollisionHandler(
                new CollisionHandler(EntityType.PLAYER_BULLET, EntityType.ENEMY) {

                    @Override
                    protected void onCollisionBegin(Entity bullet, Entity enemy) {
                        try {
                            // حذف گلوله و دشمن
                            bullet.removeFromWorld();
                            enemy.removeFromWorld();

                            // دریافت کامپوننت دشمن برای امتیاز
                            EnemyManager enemyComponent = enemy.getComponent(EnemyManager.class);
                            int scoreValue = (enemyComponent != null) ? enemyComponent.getScoreValue() : 100;

                            // افزودن امتیاز
                            GameManager.getInstance().addScore(scoreValue);

                            // ایجاد انفجار
                            spawnExplosion(enemy.getCenter());

                        } catch (Exception e) {
                            System.out.println("Error in player bullet-enemy collision: " + e.getMessage());
                        }
                    }
                }
        );
    }

    /**
     * برخورد بازیکن با گلوله دشمن
     */
    private static void initPlayerEnemyBulletCollision() {
        FXGL.getPhysicsWorld().addCollisionHandler(
                new CollisionHandler(EntityType.PLAYER, EntityType.ENEMY_BULLET) {

                    @Override
                    protected void onCollisionBegin(Entity player, Entity bullet) {
                        try {
                            // حذف گلوله
                            bullet.removeFromWorld();

                            PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
                            if (playerComp == null) return;

                            // بررسی وجود محافظ
                            if (!playerComp.hasShield() && !playerComp.hasBulletShield()) {
                                System.out.println("Player hit by enemy bullet!");
                                GameManager.getInstance().takeDamage(1);
                                spawnExplosion(player.getCenter());
                            } else {
                                System.out.println("Shield protected player from bullet!");

                                // اگر محافظ گلوله فعال باشد، امتیاز اضافی
                                if (playerComp.hasBulletShield()) {
                                    GameManager.getInstance().addBulletDestructionScore();
                                }
                            }

                        } catch (Exception e) {
                            System.out.println("Error in player-enemy bullet collision: " + e.getMessage());
                        }
                    }
                }
        );
    }

    /**
     * برخورد بازیکن با دشمن
     */
    private static void initPlayerEnemyCollision() {
        FXGL.getPhysicsWorld().addCollisionHandler(
                new CollisionHandler(EntityType.PLAYER, EntityType.ENEMY) {

                    @Override
                    protected void onCollisionBegin(Entity player, Entity enemy) {
                        try {
                            System.out.println("Player collided with enemy!");

                            // حذف دشمن
                            enemy.removeFromWorld();

                            PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
                            if (playerComp == null) return;

                            // بررسی وجود محافظ
                            if (!playerComp.hasShield()) {
                                // دریافت damage از دشمن
                                EnemyManager enemyComponent = enemy.getComponent(EnemyManager.class);
                                int damage = (enemyComponent != null) ? enemyComponent.getDamage() : 2;

                                System.out.println("Applying " + damage + " damage from enemy collision");
                                GameManager.getInstance().takeDamage(damage);
                                spawnExplosion(player.getCenter());
                            } else {
                                System.out.println("Shield protected player from enemy collision!");
                            }

                        } catch (Exception e) {
                            System.out.println("Error in player-enemy collision: " + e.getMessage());
                        }
                    }
                }
        );
    }

    /**
     * برخورد بازیکن با آیتم تقویتی
     */
    private static void initPlayerPowerUpCollision() {
        FXGL.getPhysicsWorld().addCollisionHandler(
                new CollisionHandler(EntityType.PLAYER, EntityType.POWER_UP) {

                    @Override
                    protected void onCollisionBegin(Entity player, Entity powerUp) {
                        try {
                            PowerUpComponent powerUpComp = powerUp.getComponent(PowerUpComponent.class);
                            if (powerUpComp != null) {
                                powerUpComp.applyPowerUp(player);

                                // افزایش شمارنده پاورآپ‌های جمع‌آوری شده
                                System.out.println("Power-up collected!");
                            }

                        } catch (Exception e) {
                            System.out.println("Error in player-powerup collision: " + e.getMessage());
                        }
                    }
                }
        );
    }

    /**
     * برخورد گلوله بازیکن با گلوله دشمن
     */
    private static void initPlayerBulletEnemyBulletCollision() {
        FXGL.getPhysicsWorld().addCollisionHandler(
                new CollisionHandler(EntityType.PLAYER_BULLET, EntityType.ENEMY_BULLET) {

                    @Override
                    protected void onCollisionBegin(Entity playerBullet, Entity enemyBullet) {
                        try {
                            System.out.println("Player bullet hit enemy bullet!");

                            // حذف هر دو گلوله
                            playerBullet.removeFromWorld();
                            enemyBullet.removeFromWorld();

                            // ایجاد انفجار گلوله
                            spawnBulletExplosion(playerBullet.getCenter());

                            // افزودن امتیاز نابودی گلوله
                            GameManager.getInstance().addBulletDestructionScore();

                        } catch (Exception e) {
                            System.out.println("Error in bullet-bullet collision: " + e.getMessage());
                        }
                    }
                }
        );
    }

    /**
     * ایجاد انفجار در موقعیت مشخص
     */
    protected static void spawnExplosion(Point2D position) {
        try {
            FXGL.spawn("explosion", position.getX(), position.getY());
        } catch (Exception e) {
            System.out.println("Error spawning explosion: " + e.getMessage());
        }
    }

    /**
     * ایجاد انفجار مخصوص برخورد گلوله‌ها
     */
    private static void spawnBulletExplosion(Point2D position) {
        try {
            // ایجاد دایره نارنجی برای انفجار گلوله
            Circle explosion = new Circle(8, Color.ORANGE);
            explosion.setStroke(Color.YELLOW);
            explosion.setStrokeWidth(2);

            // ایجاد موجودیت انفجار
            Entity explosionEntity = FXGL.entityBuilder()
                    .at(position.getX(), position.getY())
                    .view(explosion)
                    .build();

            FXGL.getGameWorld().addEntity(explosionEntity);

            // انیمیشن محو شدن
            FadeTransition fade = new FadeTransition(Duration.seconds(0.3), explosion);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> explosionEntity.removeFromWorld());
            fade.play();

            // انیمیشن بزرگ شدن
            ScaleTransition scale = new ScaleTransition(Duration.seconds(0.3), explosion);
            scale.setFromX(0.5);
            scale.setFromY(0.5);
            scale.setToX(1.5);
            scale.setToY(1.5);
            scale.play();

        } catch (Exception e) {
            System.out.println("Error spawning bullet explosion: " + e.getMessage());
        }
    }
}