package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class GameManager {

    private static GameManager instance;

    private int score = 0;
    private int playerLives = 3;
    private int currentStage = 1;
    private boolean isGameOver = false;

    private int bulletDestroyed = 0;
    private int enemiesDestroyed = 0;
    private int powerUpsCollected = 0;

    private Text scoreText;
    private Text livesText;
    private Text stageText;

    private GameManager() {}
        public static GameManager getInstance() {
            if (instance == null) {
                instance = new GameManager();

            }
            return instance;
        }

        public void initialize() {
            if (scoreText == null) {
                createUI();
            }
        }
        

        public void resetGame() {
        try {
            score = 0;
            playerLives = 3;
            currentStage = 1;
            isGameOver = false;

            if (scoreText == null) {
                createUI();
            }
            updateUI();

            System.out.println("Game reset successfully");
        } catch (Exception e) {
            System.out.println("Error resetting game: " + e.getMessage());
            e.printStackTrace();
        }
    
        }

        public void resetToMenu() {
            try {
                score = 0;
                playerLives = 3;
                currentStage = 1;
                isGameOver = false;

                    scoreText = null;
                    livesText = null;
                    stageText = null;

                System.out.println("reset to menu completed");
            } catch (Exception e) {
                System.out.println("Error reseting to menu: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void createUI() {
            scoreText = FXGL.getUIFactoryService().newText("Score: 0", Color.BLACK, 16);
            scoreText.setTranslateX(10);
            scoreText.setTranslateY(30);

            livesText = FXGL.getUIFactoryService().newText("Lives: 3", Color.BLACK, 16);
            livesText.setTranslateX(10);
            livesText.setTranslateY(50);

            stageText = FXGL.getUIFactoryService().newText("Stage: 1", Color.BLACK, 16);
            stageText.setTranslateX(10);
            stageText.setTranslateY(70);

            FXGL.addUINode(scoreText);
            FXGL.addUINode(livesText);
            FXGL.addUINode(stageText);

            System.out.println("UI created successfully!");

        }

        public void addScore(int points) {
            if (scoreText != null) {
                score += points;
                updateUI();
                checkStageProgress();
            }

        }

        public void takeDamage (int damage) {
            try {
                if (isGameOver || scoreText == null) return;

            playerLives -= damage;
            System.out.println("Damage taken! Lives left: " + playerLives);
            
            if (playerLives <= 0) {
                playerLives = 0;
                gameOver();
            }
            updateUI();

        } catch(Exception e) {
            System.out.println("Error in takeDamage: " + e.getMessage());
        }

        }

        public void addLife() {
            playerLives = Math.min(playerLives + 1, 5);
            updateUI();
            showFloatingText("Extra Life!", Color.GREEN);
        }

        public void updateUI() {
            if (scoreText != null && livesText != null && stageText != null) {
                scoreText.setText("Score: " + score);
                livesText.setText("Lives: " + playerLives);
                stageText.setText("Stage: " + currentStage);
            }
        } 

        private void checkStageProgress() {
            int enemiesToNextStage = currentStage * 1000;
            if (score >= enemiesToNextStage) {
                currentStage++;
                showFloatingText("Stage " + currentStage + "!", Color.GOLD);
                updateUI();
            }
        }

       public void gameOver() {

                if (isGameOver) return;

                isGameOver = true;
                System.out.println("Game Over triggered!");


                Text gameOverText = FXGL.getUIFactoryService().newText("GAME OVER", Color.RED, 48);
                gameOverText.setTranslateX((double) FXGL.getAppWidth() / 2 - 150);
                gameOverText.setTranslateY((double)FXGL.getAppHeight() / 2 - 100);

                Text finalScoreText = FXGL.getUIFactoryService().newText("Final Score: " + score, Color.BURLYWOOD, 24);
                finalScoreText.setTranslateX((double)FXGL.getAppHeight() / 2 - 100);
                finalScoreText.setTranslateY((double)FXGL.getAppWidth() / 2 - 30);

                Button restartButton = createMenuButton("Play Again", (float)FXGL.getAppHeight() / 2 + 20);
                restartButton.setOnAction(event -> restartGame());

                Button menuButton = createMenuButton("Main Menu", (float)FXGL.getAppHeight() / 2 + 90);
                menuButton.setOnAction(event -> returnToMainMenu());


                FXGL.addUINode(gameOverText);
                FXGL.addUINode(finalScoreText);
                FXGL.addUINode(restartButton);
                FXGL.addUINode(menuButton);

                stopGameEntities();
                disablePlayerControls();

                FXGL.getGameTimer().runOnceAfter(() -> {
                    FXGL.getGameController().pauseEngine();
                }, Duration.seconds(1));
            }

            private Button createMenuButton(String text, double yPosition) {
                Button button = new Button(text);

                button.setStyle("-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: #45a049; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 8px;");

                button.setTranslateX((float)FXGL.getAppWidth() / 2 - 75);
                button.setTranslateY(yPosition);

                return button;
            }

            private void stopGameEntities() {
                try {

                    FXGL.getGameWorld().getEntities().clear();

                } catch (Exception e) {

                    System.out.println("Error stop entites: " + e.getMessage());
                }
            }

            private void disablePlayerControls() {
                try {
                    Entity player = FXGL.getGameWorld().getSingleton(EntityType.PLAYER);

                    if (player != null) {
                        PlayerComponent playerComp = player.getComponent(PlayerComponent.class);

                        if (playerComp != null) {
                            playerComp.setActive(false);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error disabling player controls" + e.getMessage());
                }
            }


            public int getCurrentStage() {
                return currentStage;
            }
            public int getPlayerLives() {
                return playerLives;
            }
            public boolean isGameOver() {
                return isGameOver;
            }

            public void addBulletDestructionScore() {
                bulletDestroyed++;
                addScore(25);
                showFloatingText("Bullet Block +25", Color.GOLDENROD);
                updateStatusUI();

            }

            public void showFloatingText(String text, Color color) {
                Text floatingText = FXGL.getUIFactoryService().newText(text, color, 16);
                floatingText.setTranslateX((double)FXGL.getAppWidth() / 2 - 50);
                floatingText.setTranslateY((double)FXGL.getAppHeight() / 2);

                FXGL.addUINode(floatingText);

                TranslateTransition move = new TranslateTransition(Duration.seconds(1.5), floatingText);
                move.setToY(floatingText.getTranslateY() - 100);

                FadeTransition fade = new FadeTransition(Duration.seconds(1.5), floatingText);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.setOnFinished(e -> FXGL.removeUINode(floatingText));

                move.play();
                fade.play();

            }

            private void updateStatusUI() {
                System.out.println("Bullets Destroyed: " + bulletDestroyed);

            }

            public void activateDoubleScore(double duration) {

                addScore(500);
                showFloatingText("Double Score!", Color.CORAL);
            }

            private void restartGame() {
                try {
                    FXGL.getGameScene().clearUINodes();

                    Main main = (Main) FXGL.getApp();


                    System.out.println("Game restarted!");
                } catch (Exception e) {
                    System.out.println("Error restarting game: " + e.getMessage());
                    e.printStackTrace();
                }
            }

                public void returnToMainMenu() {
                    try {
                        Main main = (Main) FXGL.getApp();
                        main.returnToMainMenu();

                        System.out.println("returned to main menu");
                    } catch (Exception e) {
                        System.out.println("Error returning to main menu");
                        e.printStackTrace();
                    }
                }





}



            
            






            

    

    
