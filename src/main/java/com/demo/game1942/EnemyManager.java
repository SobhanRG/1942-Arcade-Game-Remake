package com.demo.game1942;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

/**
 * سیستم مدیریت دشمنان - جایگزین تمام کامپوننت‌های دشمن قبلی
 * مسئولیت حرکت، شلیک و رفتار انواع دشمنان
 */
public class EnemyManager extends Component {

    /**
     * انواع دشمنان موجود در بازی
     */
    public enum EnemyType {
        BASIC(100, 50, 1, 2.0),      // دشمن پایه
        FAST(200, 100, 2, 1.0),      // دشمن سریع
        TANK(50, 150, 3, 1.5),       // دشمن مقاوم
        BOSS(80, 500, 10, 0.8);      // دشمن اصلی (باس)

        // مشخصات هر نوع دشمن
        public final double speed;
        public final int scoreValue;
        public final int damage;
        public final double shootCooldown;

        EnemyType(double speed, int score, int damage, double cooldown) {
            this.speed = speed;
            this.scoreValue = score;
            this.damage = damage;
            this.shootCooldown = cooldown;
        }
    }

    // متغیرهای نمونه
    private EnemyType type;
    private double timeToShoot = 0;
    private double movementTime = 0;
    private boolean hasDived = false;

    /**
     * سازنده با نوع پیش‌فرض (BASIC)
     */
    public EnemyManager() {
        this(EnemyType.BASIC);
    }

    /**
     * سازنده با نوع مشخص
     */
    public EnemyManager(EnemyType type) {
        this.type = type;
        this.timeToShoot = type.shootCooldown; // شلیک فوری پس از اسپاون
    }

    @Override
    public void onUpdate(double tpf) {
        if (entity == null) return;

        // به‌روزرسانی تایمرها
        movementTime += tpf;
        timeToShoot -= tpf;

        // به‌روزرسانی حرکت و شلیک
        updateMovement(tpf);

        if (timeToShoot <= 0) {
            shoot();
            timeToShoot = type.shootCooldown;
        }

        // بررسی خروج از صفحه
        checkBounds();
    }

    /**
     * به‌روزرسانی حرکت دشمن بر اساس نوع آن
     */
    private void updateMovement(double tpf) {
        switch (type) {
            case BASIC:
                moveStraightDown(tpf);
                break;
            case FAST:
                moveFastPattern(tpf);
                break;
            case TANK:
                moveStraightDown(tpf * 0.5f); // حرکت آهسته‌تر
                break;
            case BOSS:
                moveBossPattern(tpf);
                break;
        }
    }

    /**
     * حرکت مستقیم به پایین
     */
    private void moveStraightDown(double tpf) {
        entity.translateY(type.speed * tpf);
    }

    /**
     * حرکت الگوی سریع (موجی)
     */
    private void moveFastPattern(double tpf) {
        entity.translateY(type.speed * tpf);
        // حرکت موجی به چپ و راست
        entity.translateX(Math.sin(movementTime * 5) * 100 * tpf);
    }

    /**
     * حرکت الگوی باس
     */
    private void moveBossPattern(double tpf) {
        double centerX = FXGL.getAppWidth() / 2;

        if (entity.getY() < 100) {
            // حرکت به پایین تا رسیدن به موقعیت مناسب
            entity.translateY(type.speed * tpf);
        } else {
            // حرکت موجی در عرض صفحه
            double targetX = centerX + Math.sin(movementTime) * 200;
            double currentX = entity.getX();
            double dx = (targetX - currentX) * 0.05; // حرکت نرم به سمت هدف

            entity.translateX(dx * type.speed * tpf);
            entity.translateY(type.speed * tpf * 0.1f); // حرکت بسیار آهسته به پایین
        }
    }

    /**
     * شلیک بر اساس نوع دشمن
     */
    private void shoot() {
        switch (type) {
            case BASIC:
            case FAST:
                shootSingleBullet();
                break;
            case TANK:
                shootTripleBullet();
                break;
            case BOSS:
                shootCirclePattern(8);
                break;
        }
    }

    /**
     * شلیک تک گلوله
     */
    private void shootSingleBullet() {
        FXGL.spawn("enemyBullet",
                entity.getX() + entity.getWidth() / 2 - 4,
                entity.getY() + entity.getHeight()
        );
    }

    /**
     * شلیک سه گلوله هم‌زمان
     */
    private void shootTripleBullet() {
        for (int i = -1; i <= 1; i++) {
            FXGL.spawn("enemyBullet",
                    entity.getX() + entity.getWidth() / 2 - 4 + (i * 20),
                    entity.getY() + entity.getHeight()
            );
        }
    }

    /**
     * شلیک الگوی دایره‌ای (برای باس)
     */
    private void shootCirclePattern(int bulletCount) {
        for (int i = 0; i < bulletCount; i++) {
            double angle = (2 * Math.PI / bulletCount) * i;
            final int index = i;

            // تأخیر برای ایجاد الگوی زیبا
            FXGL.getGameTimer().runOnceAfter(() -> {
                spawnDirectionalBullet(angle);
            }, javafx.util.Duration.seconds(index * 0.1));
        }
    }

    /**
     * شلیک گلوله در جهت مشخص
     */
    private void spawnDirectionalBullet(double angle) {
        double bulletSpeed = 150;
        double dx = Math.sin(angle) * bulletSpeed;
        double dy = Math.cos(angle) * bulletSpeed;

        // ایجاد گلوله جهت‌دار (نیاز به پیاده‌سازی در فکتوری)
        FXGL.spawn("enemyBullet",
                entity.getX() + entity.getWidth() / 2 - 4,
                entity.getY() + entity.getHeight()
        );
    }

    /**
     * بررسی خروج دشمن از صفحه
     */
    private void checkBounds() {
        if (entity.getY() > FXGL.getAppHeight() + 100 ||
                entity.getX() < -100 ||
                entity.getX() > FXGL.getAppWidth() + 100) {
            entity.removeFromWorld();
        }
    }

    @Override
    public void onRemoved() {
        // 25% شانس افتادن پاورآپ هنگام مرگ دشمن
        if (FXGL.random(0.0, 1.0) < 0.25) {
            spawnPowerUpOnDeath();
        }
    }

    /**
     * اسپاون پاورآپ در موقعیت مرگ دشمن
     */
    private void spawnPowerUpOnDeath() {
        try {
            double x = entity.getX() + entity.getWidth() / 2;
            double y = entity.getY() + entity.getHeight() / 2;

            // انواع متداول پاورآپ‌ها
            PowerUpComponent.PowerUpType[] commonTypes = {
                    PowerUpComponent.PowerUpType.RAPID_FIRE,
                    PowerUpComponent.PowerUpType.TRIPLE_SHOT,
                    PowerUpComponent.PowerUpType.SCORE_BOOST,
            };

            PowerUpComponent.PowerUpType selectedType =
                    commonTypes[FXGL.random(0, commonTypes.length - 1)];

            FXGL.spawn("powerUp", new SpawnData(x, y).put("type", selectedType));

        } catch (Exception e) {
            System.out.println("Error spawning death power-up: " + e.getMessage());
        }
    }

    // Getter methods
    public EnemyType getType() { return type; }
    public int getScoreValue() { return type.scoreValue; }
    public int getDamage() { return type.damage; }
}