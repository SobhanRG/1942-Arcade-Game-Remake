package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class PowerUpComponent extends Component {
    
    public enum PowerUpType {
        RAPID_FIRE(10.0),
        TRIPLE_SHOT(15.0),
        SHIELD(12.0),
        EXTRA_LIFE(2.0),
        SCORE_BOOST(2.0),
        BULLET_SHIELD(8.0);

        public final double duration;

        PowerUpType(double duration) {
            this.duration = duration;
        }

    }

    private PowerUpType type;
    private double duration;
    private double speed = 100; //default = 50

    public PowerUpComponent (PowerUpType type) {
        this.type = type;
        //this.duration = duration;

    }

    @Override 
    public void onUpdate(double tpf) {

        entity.translateY(speed * tpf);

        if (entity.getY() > FXGL.getAppHeight() + 50) {
            entity.removeFromWorld();

        }

    }

    public void applyPowerUp(Entity player) {
        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);

        switch (type) {
            case RAPID_FIRE:
                playerComponent.activeRapidFire(duration);    
                FXGL.getNotificationService().pushNotification("Rapid Fire Acitvated!");
                break;

            case TRIPLE_SHOT:
                playerComponent.activeTripleShot(duration);
                FXGL.getNotificationService().pushNotification("Triple Shot Acitvated!");
                break;

            case SHIELD:
                playerComponent.activateShield(duration);
                FXGL.getNotificationService().pushNotification("Shield Acitvated!");
                break;
            case BULLET_SHIELD:
                playerComponent.activateBulletShield(duration);
                FXGL.getNotificationService().pushNotification("Bullet Shield Acitvated!");
                break;
            case EXTRA_LIFE:
                GameManager.getInstance().addLife();
                FXGL.getNotificationService().pushNotification("Extra Life!");
                break;
            case SCORE_BOOST:
                GameManager.getInstance().activateDoubleScore(duration);
                FXGL.getNotificationService().pushNotification("Score Boost");

        }
        entity.removeFromWorld();
    }
}