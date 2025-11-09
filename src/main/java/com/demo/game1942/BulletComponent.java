package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

/**
 * کامپوننت مدیریت گلوله‌ها - هم برای بازیکن و هم دشمن
 * مسئولیت حرکت و حذف گلوله‌های خارج از صفحه
 */
public class BulletComponent extends Component {

    // مشخصات گلوله
    private double speed;
    private boolean isPlayerBullet;
    private double damage = 1;

    /**
     * سازنده گلوله
     */
    public BulletComponent(double speed, boolean isPlayerBullet) {
        this.speed = speed;
        this.isPlayerBullet = isPlayerBullet;
    }

    /**
     * سازنده گلوله با damage مشخص
     */
    public BulletComponent(double speed, boolean isPlayerBullet, double damage) {
        this(speed, isPlayerBullet);
        this.damage = damage;
    }

    @Override
    public void onUpdate(double tpf) {
        if (entity == null) return;

        // حرکت گلوله به بالا (بازیکن) یا پایین (دشمن)
        if (isPlayerBullet) {
            entity.translateY(-speed * tpf);
        } else {
            entity.translateY(speed * tpf);
        }

        // حذف گلوله در صورت خروج از صفحه
        checkBounds();
    }

    /**
     * بررسی خروج گلوله از صفحه
     */
    private void checkBounds() {
        if (entity.getY() < -50 || entity.getY() > FXGL.getAppHeight() + 50) {
            entity.removeFromWorld();
        }
    }

    // Getter methods
    public boolean isPlayerBullet() { return isPlayerBullet; }
    public double getDamage() { return damage; }
    public double getSpeed() { return speed; }
}