package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class CollisionSystem {

    public static void initCoillisions() {

        initPlayerBulletEnemyCollision();
        initPlayerEnemyBulletCollision();
        initPlayerEnemyCollision();
        initPlayerPowerUpCollision();
        initPlayerBulletEnemyBulletCollision();

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_BULLET, EntityType.ENEMY) {
            
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();
                enemy.removeFromWorld();

                GameManager.getInstance().addScore(100);
                spawnExplosion(enemy.getX() + enemy.getWidth()/2, enemy.getY() + enemy.getHeight()/2);

            }
        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ENEMY_BULLET) {
            @Override
            protected void onCollisionBegin(Entity player, Entity bullet) {
                bullet.removeFromWorld();
                
                PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
                if (playerComp.hasShield()) {

                    System.out.println("Player hit by enemy bullet!");

                    GameManager.getInstance().takeDamage(1);
                    spawnExplosion(player.getX() + player.getWidth()/2, player.getY() + player.getHeight()/2);

                } else {
                    System.out.println("Shield protected player from bullet!");
                }

            }
        
        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity player, Entity enemy) {
                enemy.removeFromWorld();
                PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
                
                if(playerComp.hasShield()) {
                    GameManager.getInstance().takeDamage(2);
                    spawnExplosion(player.getX() + player.getWidth()/2, player.getY() + player.getHeight()/2);


                }

            }

        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.POWER_UP) {
            @Override
            protected void onCollisionBegin(Entity player, Entity powerUp) {
                PowerUpComponent powerUpComp = powerUp.getComponent(PowerUpComponent.class);
                powerUpComp.applyPowerUp(player);


            }
        });

    }

    protected static void spawnExplosion(double x, double y) {
        try {
            FXGL.spawn("explosion", x, y);
        } catch (Exception e) {
            System.out.println("Error spawning explosion: " + e.getMessage());
        }

    }

    private static void initPlayerBulletEnemyCollision() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_BULLET, EntityType.ENEMY) {

            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {

                //System.out.println("Player bullet hit enemy");

                bullet.removeFromWorld();
                enemy.removeFromWorld();

                GameManager.getInstance().addScore(100);

                spawnExplosion(enemy.getX() + enemy.getWidth()/2, enemy.getY() + enemy.getHeight()/2);
            }
        });
    }

    private static void initPlayerEnemyBulletCollision() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ENEMY_BULLET) {
            @Override
            protected void onCollisionBegin(Entity player, Entity bullet) {

                System.out.println("Player hit by enemy bullet");

                bullet.removeFromWorld();

                PlayerComponent playerComp = player.getComponent(PlayerComponent.class);

                if (playerComp != null && playerComp.hasShield()) {
                    
                    System.out.println("Applying damage to player");

                    GameManager.getInstance().takeDamage(1);
                    spawnExplosion(player.getX() + player.getWidth()/2, player.getY() + player.getHeight()/2);

                
                }
            }

        });
    }

    private static void initPlayerEnemyCollision() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity player, Entity enemy) {
                System.out.println("Player collided with enemy");

                enemy.removeFromWorld();

                PlayerComponent playerComp = player.getComponent(PlayerComponent.class);

                if (playerComp != null && playerComp.hasShield()) {
                    System.out.println("Applying damage from enemy collision");

                    GameManager.getInstance().takeDamage(2);

                    spawnExplosion(player.getX() + player.getWidth()/2, player.getY() + player.getHeight()/2);

                }

            }
        });
    }

   

    private static void initPlayerBulletEnemyBulletCollision() {

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_BULLET, EntityType.ENEMY_BULLET) {

            @Override
            protected void onCollisionBegin(Entity playerBullet, Entity enemyBullet) {
                System.out.println("Player bullet hit enemy bullet!");

                playerBullet.removeFromWorld();
                enemyBullet.removeFromWorld();

                spawnBulletExplosion(playerBullet.getX(), playerBullet.getY());



                //GameManager.getInstance().addScore(25);
                GameManager.getInstance().addBulletDestructionScore();



            }


            
        });

    }

    private static void spawnBulletExplosion(double x, double y) {
    try {    
        Circle explosion = new Circle(8, Color.ORANGE);
        explosion.setStroke(Color.YELLOW);
        explosion.setStrokeWidth(2);

        Entity explosionEntity = FXGL.entityBuilder().at(x, y).view(explosion).build();

        FXGL.getGameWorld().addEntities(explosionEntity);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), explosion);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> explosionEntity.removeFromWorld());
        fade.play();

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

    private static void initPlayerPowerUpCollision() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.POWER_UP) {

            @Override
            protected void onCollisionBegin(Entity player, Entity powerUp) {
                
                PowerUpComponent powerUpComp = powerUp.getComponent(PowerUpComponent.class);

                if (powerUpComp != null) {
                    powerUpComp.applyPowerUp(player);
                }
            }
            
        });
    }


}

