package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class EnemyComponent extends Component {
    
    private double speed = 200;
    private double shootCooldown = 2.0;
    private double timeToShoot = 2.0;

    public EnemyComponent(com.demo.game1942.EnemyManager enemyType) {
    }

    public void onUpdate (double tpf) {

        entity.translateY(speed * tpf);

        timeToShoot -= tpf;
        if (timeToShoot <= 0) {
            shoot();
            timeToShoot = shootCooldown;

        }

        if (entity.getY() > FXGL.getAppHeight() + 50) {
            entity.removeFromWorld();
        }
    }

    private void shoot() {
        FXGL.spawn("enemyBullet", entity.getX() + entity.getWidth() / 2 - 5, entity.getY() + entity.getHeight());
    }


    
}