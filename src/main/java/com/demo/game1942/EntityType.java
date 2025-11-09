package com.demo.game1942;

/**
 * انواع موجودیت‌های موجود در بازی
 * برای دسته‌بندی و تشخیص نوع برخوردها استفاده می‌شود
 */
public enum EntityType {
    PLAYER,         // بازیکن اصلی
    ENEMY,          // دشمنان
    PLAYER_BULLET,  // گلوله‌های بازیکن
    ENEMY_BULLET,   // گلوله‌های دشمن
    POWER_UP,       // آیتم‌های تقویتی
    BACKGROUND,     // پس‌زمینه
    BOSS,           // دشمنان اصلی (باس)
    EXPLOSION       // انفجارها
}