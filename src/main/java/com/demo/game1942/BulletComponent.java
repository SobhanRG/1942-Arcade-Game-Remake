package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class BulletComponent extends Component {

    private double speed;
    private boolean isPlayerBullet;

    public BulletComponent (double speed, boolean isPlayerBullet) {
        this.speed = speed;
        this.isPlayerBullet = isPlayerBullet;
    }

    @Override
    public void onUpdate(double tpf) {
        if (isPlayerBullet) {
            entity.translateY(-speed * tpf);
        } else {
            entity.translateY(speed * tpf);
        }
        if (entity.getY() < -50 || entity.getY() > FXGL.getAppHeight() + 50) {
            entity.removeFromWorld();
        }
    }
    
}