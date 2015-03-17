/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mlh.game.FileManager;
import com.mlh.game.HMSDreadnought;
import com.mlh.interfaces.ITargetable;
import com.mlh.managers.Jukebox;
import com.mlh.utilities.Debuger;

import java.util.HashMap;
import java.util.Map.Entry;

public class SpaceShip extends SpaceObject {

    // loaded fields
    protected float maxHealth;
    protected float healthRegen;
    protected float maxShield;
    protected float shieldRegen;
    protected String engineSndEft;
    private final HashMap<String, Weapon> weaponArray;
    private final HashMap<String, String> weaponArrayFiles;

    // generated fields
    protected float turning;
    protected float thrusting;
    protected boolean engineSndEftActive;
    protected boolean engineSndEftPlaying;

    public SpaceShip() {
        super();
        weaponArray = new HashMap<String, Weapon>();
        weaponArrayFiles = new HashMap<String, String>();
    }

    public SpaceShip(String name, Object parent, World world, int drawLayer) {
        super(name, parent, world, drawLayer);
        weaponArray = new HashMap<String, Weapon>();
        weaponArrayFiles = new HashMap<String, String>();
    }

    public void spawn(Vector2 position, Vector2 heading) {
        disabled = false;
        remove = false;
        this.position.set(position);
        this.heading.set(heading);
        health = maxHealth;
        shield = maxShield;
    }

    public void fireAt(ITargetable target) {
        Debuger.print(3, this.getName(), "fireAt", target.getName());
        fireWeaponAt(getBestWeapon(target), target, 1);
    }

    public void fireAt(ITargetable target, int count) {
        Debuger.print(3, this.getName(), "fireAt: " + target.getName(), "count: " + count);
        fireWeaponAt(getBestWeapon(target), target, count);
    }

    public void fireWeaponAt(String weapon, ITargetable target) {
        fireWeaponAt(weapon, target, 1);
    }

    public void fireWeaponAt(String weapon, ITargetable target, int count) {
        Debuger.print(3, this.getName(), "phase1 firing: " + weapon + " at " + target.getName(), "count: " + count);
        if (disabled) {
            return;
        }
        if (weaponArray.get(weapon).isReady()) {
            Debuger.print(3, this.getName(), "phase2 firing: " + weapon + " at " + target.getName(), "count: " + count);
            weaponArray.get(weapon).fireAt(anchorSprite.getAnchor(weapon), target, count);
        }
    }

    public void fireAllWeaponsAt(ITargetable target) {
        for (Entry< String, Weapon> weapon : weaponArray.entrySet()) {
            weapon.getValue().fireAt(anchorSprite.getAnchor(weapon.getKey()), target, -1);
        }
    }

    public void fireAllWeaponsAt(ITargetable target, boolean stageredFire) {
        float delay = 0;
        for (Entry< String, Weapon> weapon : weaponArray.entrySet()) {
            weapon.getValue().delayedFireAt(anchorSprite.getAnchor(weapon.getKey()), target, -1, delay);
            if (stageredFire) {
                delay += MathUtils.random(HMSDreadnought.WEAPON_STAGERED_FIRE_MIN, HMSDreadnought.WEAPON_STAGERED_FIRE_MAX);
            }
        }
    }

    public void stopFiring() {
        for (Entry<String, Weapon> weapon : weaponArray.entrySet()) {
            weapon.getValue().stopFiring();
        }
    }

    public void stopFiring(ITargetable target) {
        for (Entry<String, Weapon> weapon : weaponArray.entrySet()) {
            weapon.getValue().stopFiring(target);
        }
    }

    // we do this so if the target is on the right side and we have a weapon open
    // on the right side we will fire from the right side
    public String getBestWeapon(ITargetable target) {
        String canidate = "none";
        float distToTarget = Float.MAX_VALUE;

        for (Entry< String, Weapon> weapon : weaponArray.entrySet()) {
            // look up the weapons anchor point and check the dist to targets origin
            float dist = anchorSprite.getAnchor(weapon.getKey()).dst2(target.getOrigin());
            if (dist < distToTarget && weapon.getValue().isReady()) {
                canidate = weapon.getKey();
                distToTarget = dist;
            }
        }
        return canidate;
    }

    protected void addWeapon(String anchorPoint, Weapon weapon) {
        weaponArray.put(anchorPoint, weapon);
        weaponArrayFiles.put(anchorPoint, FileManager.generatePathFileName(weapon));
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        // sound effects
        if (engineSndEftActive && isValid()) {
            Jukebox.loop(engineSndEft);
        } else if (engineSndEftPlaying && !engineSndEftActive) {
            Jukebox.stop(engineSndEft);
            engineSndEftPlaying = false;
        } 

        // regenerate health/shields
        regenerate(dt);

        // update weaponsArray
        for (Entry< String, Weapon> weapon : weaponArray.entrySet()) {
            weapon.getValue().update(dt, anchorSprite.getAnchor(weapon.getKey()));
        }

    }

    protected void regenerate(float dt) {
        if (health <= 0) {
            disabled = true;
        } else {
            if (health < maxHealth) {
                health += healthRegen * dt;
                if (health > maxHealth) {
                    health = maxHealth;
                }
            }
        }
        if (!disabled) {
            if (shield < maxShield) {
                shield += shieldRegen * dt;
                if (shield > maxShield) {
                    shield = maxShield;
                }
            }
        }
    }

    public void increaseShield(float shield) {
        this.shield += shield;
        if (this.shield > this.maxShield) {
            this.shield = this.maxShield;
        }
    }

    public void increaseHealth(float health) {
        this.health += health;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
    }

    @Override
    public void write(Json json) {
        Debuger.print(3, name, "write", "weaponArray = " + weaponArrayFiles.toString());
        super.write(json);

        json.writeValue("maxHealth", maxHealth);
        json.writeValue("healthRegen", healthRegen);
        json.writeValue("maxShield", maxShield);
        json.writeValue("shieldRegen", shieldRegen);
        json.writeValue("engineSndEft", engineSndEft);

        for (Entry< String, Weapon> weapon : weaponArray.entrySet()) {
            json.writeValue(weapon.getKey(), weaponArrayFiles.get(weapon.getKey()));
        }
    }

    @Override
    public void read(Json json, JsonValue jsonMap) {
        super.read(json, jsonMap);

        maxHealth = jsonMap.get("maxHealth").asFloat();
        healthRegen = jsonMap.get("healthRegen").asFloat();
        maxShield = jsonMap.get("maxShield").asFloat();
        shieldRegen = jsonMap.get("shieldRegen").asFloat();
        engineSndEft = jsonMap.get("engineSndEft").asString();

        JsonValue jName = jsonMap.get("engineSndEft").next();
        while (jName != null) {
            weaponArrayFiles.put(jName.name(), jName.asString());
            Weapon loadedWeapon = Weapon.load(jName.asString());
            weaponArray.put(jName.name, loadedWeapon);
            jName = jName.next();
        }
    }

    public static SpaceShip load(String filePathName, Object owner, World world, int drawLayer) {
        Debuger.print(3, "SpaceShip.load", "load", filePathName);
        Json json = new Json();
        // load the ship
        SpaceShip newSpaceShip = json.fromJson(SpaceShip.class, Gdx.files.internal(filePathName).read());
        // load/set the anchorSprite
        newSpaceShip.setAnchorSprite(AnchorSprite.load(newSpaceShip.getAnchorSpriteFile()));
        // load the weapons
        // this is done in the read, this needs to be fixed at some point

        // after all loads done init ship
        newSpaceShip.initSpaceShip(owner, world, drawLayer);

        Debuger.print(3, "SpaceShip.load", "completed", "successfully");
        return newSpaceShip;
    }

    public void initSpaceShip(Object owner, World world, int drawLayer) {
        this.owner = owner;
        this.world = world;
        this.drawLayer = drawLayer;
        for (Entry< String, Weapon> weapon : weaponArray.entrySet()) {
            // add 1 to the drawlayer so that the projectiles appear above the ships
            // we could do -1 to simulate fireing from below the ship
            // setting it to the same layer will result in unpredictable results
            weapon.getValue().initWeapon(this, world, drawLayer + 1);
        }
    }

    @Override
    public String save() {
        String filePathName = FileManager.generatePathFileName(this);
        FileManager.writeJsonFile(filePathName, this);

        // because we chained the reads and writes we cant use super.save
        anchorSprite.save();

        for (Entry< String, Weapon> weapon : weaponArray.entrySet()) {
            weapon.getValue().save();
        }

        return filePathName;
    }

    @Override
    public String save(String filePathName) {
        FileManager.writeJsonFile(filePathName, this);

        for (Entry< String, Weapon> weapon : weaponArray.entrySet()) {
            weapon.getValue().save();
        }

        return filePathName;
    }

    public boolean isVisible() {
        Vector2 myOrigin = getOrigin();
        return myOrigin.x >= 0
                && myOrigin.x <= world.getVisiableSpaceWidth()
                && myOrigin.y >= 0
                && myOrigin.y <= world.getVisiableSpaceHeight();
    }

    @Override
    public boolean isValid() {
        return super.isValid() && isVisible();
    }

    public boolean isEngineSndEftActive() {
        return engineSndEftActive;
    }

    public void setEngineSndEftActive(boolean active) {
        engineSndEftActive = active;
    }

    public void setEngineSndEft(String engineSndEft) {
        this.engineSndEft = engineSndEft;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    @Override
    public void setType(String type) {
        super.setType(type);
        for (Entry< String, Weapon> weapon : weaponArray.entrySet()) {
            weapon.getValue().setType(type);
        }
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getMaxShield() {
        return maxShield;
    }

}
