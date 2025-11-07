package com.demo.game1942;


import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PlayerComponent extends Component {

    private double speed = 300;
    private boolean canShoot = true;
    private double shootCoolDown = 0.2;
    private boolean active = true;
    private static boolean inputRegistered = false;

    private boolean bulletShieldActive = false;
    private boolean rapidFireActive = false;
    private boolean tripleShotActive = false;
    private boolean shieldActive = false;
    private double powerUpTimer = 0;

    @Override
    public void onAdded() {
        if (!inputRegistered) {
            setupInput();
            inputRegistered = true;
        }
        active = true;
    }

    @Override
    public void onRemoved() {
        active = false;
    }

    private void setupInput() {
        Input input = FXGL.getInput();

            input.addAction(new UserAction("Move Left") {
                @Override
                protected void onAction() {
                    if (active && entity != null) {
                        entity.translateX(-speed * 0.016);
                    }
            }
    }, KeyCode.A);

            input.addAction(new UserAction("Move Right") {
                @Override
                protected void onAction() {
                    if (active && entity != null) {
                        entity.translateX(speed * 0.016);
                    }
                    
                 }
    }, KeyCode.D);

            input.addAction(new UserAction("Move Down") {
                @Override
                protected void onAction() {
                    if (active && entity != null) {
                        entity.translateY(speed * 0.016);
                    }
                }
    }, KeyCode.S);

            input.addAction(new UserAction("Move Up") {
                @Override
                protected void onAction() {
                    if (active && entity != null) {
                        entity.translateY(-speed * 0.016);
                    }
                }
    }, KeyCode.W);

                input.addAction(new UserAction("Shoot") {
                    @Override
                    protected void onAction() {
                        if (canShoot && active) {
                            shoot();
                        }
                    }
                }, KeyCode.SPACE);

                input.addAction(new UserAction("Pause Menu") {
                    @Override
                    protected void onAction() {
                        if (active) {
                            showPauseMenu();
                        }
                    }
                }, KeyCode.F1);


}

    private static void resetInputRegistration() {
        inputRegistered = false;
    }

    private void showPauseMenu() {
        if (!active) return;
        System.out.println("pause menu activated!");

        Rectangle overlay = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), Color.rgb(0, 0, 0, 0.7));

        Text pauseText = FXGL.getUIFactoryService().newText("Game paused", Color.WHITE, 36);

        pauseText.setTranslateX((float)FXGL.getAppWidth() / 2 - 100);
        pauseText.setTranslateX((float)FXGL.getAppWidth() / 2 - 100);

        Button menuButton = createPauseButton("main menu", 40);
        menuButton.setOnAction(e -> returnToMainMenu());

        Button resumeButton = createPauseButton("resume", -30);
        resumeButton.setOnAction(e -> hidePauseMenu(menuButton, overlay, pauseText, resumeButton));

        FXGL.addUINode(overlay);
        FXGL.addUINode(pauseText);
        FXGL.addUINode(resumeButton);
        FXGL.addUINode(menuButton);

        FXGL.getGameController().pauseEngine();

    }

    private Button createPauseButton(String text, double yOffset) {
        Button button = new Button(text);
        button.setStyle(
                        "-fx-font-size: 20px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #2196F3; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: #1976D2; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 8px;"
        );

        button.setTranslateY((float)FXGL.getAppHeight() / 2 + yOffset);
        button.setTranslateX((float)FXGL.getAppWidth() / 2 - 80);

        return button;
    }

    private void hidePauseMenu(Node... nodes) {
        for (Node node : nodes) {
            FXGL.removeUINode(node);
        }

        FXGL.getGameController().resumeEngine();
    }

    private void returnToMainMenu() {
        try {

            active = false;
            Main main = (Main) FXGL.getApp();
            main.returnToMainMenu();

        } catch (Exception e) {
            System.out.println("Error returning to main menu from PlayerComponent: " + e.getMessage());
            e.printStackTrace();
        }
    }
                private void shoot() {
                    if (!active || !canShoot) return;

                    canShoot = false;

                    if (tripleShotActive) {
                        for (int i = -1; i <= 1; i++) {
                            FXGL.spawn("playerBullet",
                                    entity.getX() + entity.getWidth() / 2 - 5 + (i * 15),
                                    entity.getY() - 20
                            );
                        }
                    } else {
                        FXGL.spawn("playerBullet",
                                entity.getX() + entity.getWidth() / 2 - 5,
                                entity.getY() - 20
                        );
                    }
                    double currentCooldown = rapidFireActive ? shootCoolDown * 0.5 : shootCoolDown;

                    FXGL.getGameTimer().runOnceAfter(() -> {
                        canShoot = true;
                    }, Duration.seconds(currentCooldown));

                }
                public void activeRapidFire (double duration) {
                    rapidFireActive = true;
                    startPowerUpTimer(duration);
                    System.out.println("Rapid Fire Activated for " + duration + " seconds!");
                }
                public void activeTripleShot (double duration) {
                    tripleShotActive = true;
                    startPowerUpTimer(duration);
                    System.out.println("Triple Shot Activated for " + duration + " seconds!");
                }

                public void activateShield (double duration) {
                    shieldActive = true;
                    startPowerUpTimer(duration);
                    System.out.println("Shield Activated for " + duration + " seconds!");
                }

                public void activateBulletShield (double duration) {
                    bulletShieldActive = true;
                    startPowerUpTimer(duration);
                    System.out.println("Bullet Shield Activated for " + duration + " seconds!");

                }


                private void startPowerUpTimer (double duration) {
                    powerUpTimer = Math.max(powerUpTimer, duration);
                }


    @Override
    public void onUpdate(double tpf) {
        if (!active || entity == null) return;

        keepInBounds();

        if (powerUpTimer > 0) {
            powerUpTimer -= tpf;

            if (powerUpTimer <=0) {
                deactivateAllPowerUps();
                
            }
        }
    }

    private void deactivateAllPowerUps() {
        rapidFireActive = false;
        tripleShotActive = false;
        shieldActive = false;    
        bulletShieldActive = false;

        System.out.println("All power-ups deactivated!");
    }

    public boolean hasShield() {
        return !shieldActive;
    }
    
    private void keepInBounds() {
        if (entity.getX() < 0) entity.setX(0);
        if (entity.getX() > FXGL.getAppWidth() - entity.getWidth())
            entity.setX(FXGL.getAppWidth() - entity.getWidth());
        if (entity.getY() < 0) entity.setY(0);
        if (entity.getY() > FXGL.getAppHeight() - entity.getHeight())
            entity.setY(FXGL.getAppHeight() - entity.getHeight());
        
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public boolean hasBulletShield() {
        return bulletShieldActive;
    }



    }



    




