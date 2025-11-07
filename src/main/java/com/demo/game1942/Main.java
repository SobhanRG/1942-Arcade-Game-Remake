package com.demo.game1942;


import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Main extends GameApplication {

    public enum GameState {
        MAIN_MENU,
        PLAYING,
        GAME_OVER,
        PAUSED
    }

    private GameState currentState = GameState.MAIN_MENU;
    private static boolean entityFactoryAdded = false;
    private UnifiedSpawner currentSpawner = null;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setHeight(800);
        settings.setWidth(600);
        settings.setTitle("1942 Remake");
        settings.setVersion("(demo)");
        settings.setFullScreenAllowed(true);


    }

    @Override
    protected void initGame() {
        if (!entityFactoryAdded) {
            FXGL.getGameWorld().addEntityFactory(new GameEntityFactory());
            entityFactoryAdded = true;
            System.out.println("EntityFactory added successfully");
        }
        showMainMenu();
    }

    private void showMainMenu() {
        currentState = GameState.MAIN_MENU;

        FXGL.getGameScene().clearUINodes();

        try {
            FXGL.getGameWorld().getEntities().forEach(Entity::removeFromWorld);
        } catch (Exception e) {
            System.out.println("Warning: Error clearing entities: " + e.getMessage());
            FXGL.getGameWorld().getEntities().clear();
        }

        Text titleText = FXGL.getUIFactoryService().newText("1942 Arcade Game is back!", Color.GOLD, 48);
        titleText.setStroke(Color.RED);
        titleText.setStrokeWidth(2);
        titleText.setTranslateX((float)FXGL.getAppWidth() / 2 - 100);
        titleText.setTranslateY(150);

        Text subtitletext = FXGL.getUIFactoryService().newText("classic remake of old gem", Color.LIGHTBLUE, 24);
        subtitletext.setTranslateX((float) FXGL.getAppWidth() / 2 - 80);
        subtitletext.setTranslateY(210);

        Button startButton = createMenuButton("start", 300);
        startButton.setOnAction(e -> startGame());

        Button exitButton = createMenuButton("exit", 380);
        exitButton.setOnAction(e -> FXGL.getGameController().exit());

        Text controlText = FXGL.getUIFactoryService().newText("don't worry it is very simple", Color.BISQUE, 16);
        controlText.setTranslateX((float)FXGL.getAppWidth() / 2 - 180);
        controlText.setTranslateY(500);

        FXGL.addUINode(titleText);
        FXGL.addUINode(subtitletext);
        FXGL.addUINode(startButton);
        FXGL.addUINode(exitButton);
        FXGL.addUINode(controlText);

        FXGL.getGameController().resumeEngine();

        System.out.println("main menu displayed!");

    }

    private void cleanupGame() {
        try {
            FXGL.getGameController().pauseEngine();
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameWorld().getEntities().clear();
            FXGL.getInput().clearAll();

            GameManager.getInstance().resetToMenu();

            currentSpawner = null;
        } catch (Exception e) {
            System.out.println("Error during cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Button createMenuButton(String text, double yPosition) {
        Button button = new Button(text);

        button.setStyle(
                "-fx-font-size: 24px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: #4CAF50; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 15px 30px; " +
                "-fx-background-radius: 10px; " +
                "-fx-border-color: #45a049; " +
                "-fx-border-width: 3px; " +
                "-fx-border-radius: 10px;"
        );

        button.setOnMouseEntered(event -> {
            button.setStyle("-fx-font-size: 24px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: #45a049; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 15px 30px; " +
                    "-fx-background-radius: 10px; " +
                    "-fx-border-color: #4CAF50; " +
                    "-fx-border-width: 3px; " +
                    "-fx-border-radius: 10px;");
        });

        button.setOnMouseExited(event -> {
            button.setStyle("-fx-font-size: 24px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: #4CAF50; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 15px 30px; " +
                    "-fx-background-radius: 10px; " +
                    "-fx-border-color: #45a049; " +
                    "-fx-border-width: 3px; " +
                    "-fx-border-radius: 10px;");
        });

        button.setTranslateX((float)FXGL.getAppWidth() / 2 - 100);
        button.setTranslateY(yPosition);

        return button;
    }

    private void startGame() {
        try {
            currentState = GameState.PLAYING;
            cleanupGame();

            FXGL.spawn("player", (float)FXGL.getAppWidth() / 2 - 20, FXGL.getAppHeight() - 100);

            currentSpawner = new UnifiedSpawner();

            FXGL.entityBuilder().with(currentSpawner).buildAndAttach();

            GameManager.getInstance().resetGame();
            CollisionSystem.initCoillisions();

            System.out.println("game started successfully");
        } catch (Exception e) {
            System.out.println("error starting game: " + e.getMessage());
            e.printStackTrace();
            showMainMenu();
        }
    }

    public void returnToMainMenu() {
        showMainMenu();
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState state) {
        this.currentState = state;
    }

    public static void main(String[] args) {
        launch(args);
    }
}