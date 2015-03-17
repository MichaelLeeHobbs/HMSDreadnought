/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class SpaceShipTemplate extends SpaceShip {

    // ship
    private static final String SHIP_NAME = "Songbird";
    private static final int SHIP_COLLISION_DAMAGE = 50;
    private static final int SHIP_MAX_HEALTH = 15;
    private static final int SHIP_MAX_SHIELD = 15;
    private static final float SHIP_MAX_SPEED = 400f;
    private static final float SHIP_MAX_FORCE = 400f;
    private static final float SHIP_MAX_TURN_RATE = 360;
    private static final int SHIP_HEALTH_REGEN = 1;
    private static final int SHIP_SHIELD_REGEN = 2;
    private static final String SHIP_ENGINE_SND_EFT = "thruster";

    // weapon
    private static final String WEAPON_NAME = "Phaser Cannon";
    private static final float RATE_OF_FIRE = 1.0f;
    private static final String WEAPON_FIRE_SND_EFT = "shoot";

    // projectile
    private static final String PROJECTILE_NAME = "Phaser Bolt";
    private static final String PROJECTILE_TEXTURE = "core/assets/textures/greenBolt.png";
    private static final int PROJECTILE_SPRITE_WIDTH = 20;
    private static final int PROJECTILE_SPRITE_HEIGHT = 20;
    private static final float PROJECTILE_SPRITE_ROTATION = -90f;
    private static final int PROJECTILE_SPEED = 1000;
    private static final int PROJECTILE_DAMAGE_VALUE = 2;
    private static final String PROJECTILE_COLLISION_SND_EFT = "laserHit";

    // anchor sprite
    private static final String SHIP_SPRITE_TEXTURE = "core/assets/textures/songbird.png";
    private static final int SHIP_SPRITE_WIDTH = 39;
    private static final int SHIP_SPRITE_HEIGHT = 48;
    private static final float SHIP_SPRITE_ROTATION = -90f;
    private static final HashMap<String, Vector2> SHIP_ANCHOR_POINTS = new HashMap<String, Vector2>();

    // *** anchor points ***
    static {
        SHIP_ANCHOR_POINTS.put("origin", new Vector2(SHIP_SPRITE_WIDTH / 2, SHIP_SPRITE_HEIGHT / 2));
        SHIP_ANCHOR_POINTS.put("weapon_1", new Vector2(20, 48));
        /*
        SHIP_ANCHOR_POINTS.put("weapon_2", new Vector2(42, 100));
        SHIP_ANCHOR_POINTS.put("weapon_3", new Vector2(96, 102));
        SHIP_ANCHOR_POINTS.put("weapon_4", new Vector2(18, 152));
        SHIP_ANCHOR_POINTS.put("weapon_5", new Vector2(56, 142));
        SHIP_ANCHOR_POINTS.put("weapon_6", new Vector2(86, 142));
        SHIP_ANCHOR_POINTS.put("weapon_7", new Vector2(70, 184));
        SHIP_ANCHOR_POINTS.put("weapon_8", new Vector2(70, 240));
        SHIP_ANCHOR_POINTS.put("weapon_9", new Vector2(150, 86));
        SHIP_ANCHOR_POINTS.put("weapon_10", new Vector2(150, 145));
        SHIP_ANCHOR_POINTS.put("weapon_11", new Vector2(238, 55));
        SHIP_ANCHOR_POINTS.put("weapon_12", new Vector2(202, 100));
        SHIP_ANCHOR_POINTS.put("weapon_13", new Vector2(256, 102));
        SHIP_ANCHOR_POINTS.put("weapon_14", new Vector2(212, 152));
        SHIP_ANCHOR_POINTS.put("weapon_15", new Vector2(242, 142));
        SHIP_ANCHOR_POINTS.put("weapon_16", new Vector2(280, 145));
        SHIP_ANCHOR_POINTS.put("weapon_17", new Vector2(225, 184));
        SHIP_ANCHOR_POINTS.put("weapon_18", new Vector2(225, 240));
                */
    }

    public SpaceShipTemplate(Object parent, World world, int drawLayer) {
        super(SHIP_NAME, parent, world, drawLayer);

        anchorSprite = new AnchorSprite(SHIP_NAME, SHIP_SPRITE_TEXTURE, SHIP_SPRITE_WIDTH, SHIP_SPRITE_HEIGHT, SHIP_SPRITE_ROTATION, SHIP_SPRITE_WIDTH / 2, SHIP_SPRITE_HEIGHT / 2, SHIP_ANCHOR_POINTS);
        anchorSprite.setOriginCenter();
        setAnchorSprite(anchorSprite);

        maxSpeed = SHIP_MAX_SPEED;
        maxForce = SHIP_MAX_FORCE;
        health = SHIP_MAX_HEALTH;
        maxHealth = SHIP_MAX_HEALTH;
        shield = SHIP_MAX_SHIELD;
        maxShield = SHIP_MAX_SHIELD;
        maxTurnRate = SHIP_MAX_TURN_RATE;
        healthRegen = SHIP_HEALTH_REGEN;
        shieldRegen = SHIP_SHIELD_REGEN;
        engineSndEft = SHIP_ENGINE_SND_EFT;
        engineSndEftActive = false;
        collisionDamage = SHIP_COLLISION_DAMAGE;

        // *** int projectile ***
        AnchorSprite projectileAnchorSprite
                = new AnchorSprite(
                        PROJECTILE_NAME,
                        PROJECTILE_TEXTURE,
                        PROJECTILE_SPRITE_WIDTH,
                        PROJECTILE_SPRITE_HEIGHT,
                        PROJECTILE_SPRITE_ROTATION,
                        PROJECTILE_SPRITE_WIDTH / 2,
                        PROJECTILE_SPRITE_HEIGHT / 2
                );
        projectileAnchorSprite.setOriginCenter();

        Projectile projectile = new Projectile(
                PROJECTILE_NAME,
                this,
                world,
                drawLayer,
                projectileAnchorSprite,
                PROJECTILE_SPEED,
                PROJECTILE_COLLISION_SND_EFT,
                PROJECTILE_DAMAGE_VALUE
        );
        //projectile.setAnchorSprite(projectileAnchorSprite);

        // *** init weapon array ***
        Weapon weapon = new Weapon(
                WEAPON_NAME,
                this,
                world,
                projectile,
                RATE_OF_FIRE,
                WEAPON_FIRE_SND_EFT
        );
        addWeapon("weapon_1", weapon.cpy());
        /*
        addWeapon("weapon_2", weapon.cpy());
        addWeapon("weapon_3", weapon.cpy());
        addWeapon("weapon_4", weapon.cpy());
        addWeapon("weapon_5", weapon.cpy());
        addWeapon("weapon_6", weapon.cpy());
        addWeapon("weapon_7", weapon.cpy());
        addWeapon("weapon_8", weapon.cpy());
        addWeapon("weapon_9", weapon.cpy());
        addWeapon("weapon_10", weapon.cpy());
        addWeapon("weapon_11", weapon.cpy());
        addWeapon("weapon_12", weapon.cpy());
        addWeapon("weapon_13", weapon.cpy());
        addWeapon("weapon_14", weapon.cpy());
        addWeapon("weapon_15", weapon.cpy());
        addWeapon("weapon_16", weapon.cpy());
        addWeapon("weapon_17", weapon.cpy());
        addWeapon("weapon_18", weapon.cpy());
                */
    }
}
