/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mlh.game.FileManager;
import com.mlh.interfaces.ITargetable;
import com.mlh.managers.Jukebox;
import com.mlh.utilities.Debuger;

public class Weapon implements Json.Serializable {

    // loadable fields
    protected String name;                        // weapon name
    protected float rofPerSecond;                 // rate of fire per second
    protected String sndEffect;                   // sound effect of weapon fire
    protected String projectileFile;              // json load file
    protected Projectile projectile;              // projectile class weapon can fire

    // generated/set fields
    protected SpaceObject owner;                 // owner of weapon
    protected float rofDelay;                           // 1 / rateOfFirePerSecond
    protected float rofTimer;                           // rof timer
    protected ITargetable target;                       // for multi shots
    protected World world;
    protected int fireCount;                            // fire x times
    protected String type;

    public Weapon() {

    }

    public Weapon(
            String name,
            SpaceObject parent,
            World world,
            Projectile projectile,
            float rateOfFirePerSecond,
            String sndEffect
    ) {
        this.name = name;
        this.owner = parent;
        this.world = world;
        this.projectile = projectile;
        this.projectileFile = FileManager.generatePathFileName(projectile);
        this.rofPerSecond = rateOfFirePerSecond;
        this.rofDelay = 1f / rofPerSecond;
        this.sndEffect = sndEffect;
        this.type = "";

        fireCount = 0;
        rofTimer = 0;
    }

    public void fireAt(Vector2 from, ITargetable target, int count) {
        if (count != -1) {
            this.fireCount = count--;
        } else {
            this.fireCount = count;
        }
        this.target = target;
        Vector2 currentPosition = from.cpy().add(owner.getPosition());
        
        Debuger.print(2, "target", "position = " + target.getPosition(), "origin = " + target.getOrigin());

        if (rofTimer <= 0) {
            world.addObject(projectile.fireAt(owner, currentPosition, target));
            rofTimer = rofDelay;
            Jukebox.play(sndEffect);
        }
    }

    public void delayedFireAt(Vector2 from, ITargetable target, int count, float delay) {
        rofTimer += delay;
        fireAt(from, target, count);
    }

    public void update(float dt, Vector2 anchorPoint) {
        // cool down the weapon even if we dont have a target
        if (rofTimer > 0) {
            rofTimer -= dt;
        }

        // check if target is still around, if so shoot at it
        if (fireCount != 0) {
            if (!this.target.isValid()) {
                fireCount = 0;
                target = null;
            } else if (rofTimer <= 0) {
                fireAt(anchorPoint, target, fireCount);
            }
        }
    }

    public boolean isReady() {
        return fireCount == 0 && rofTimer <= 0;
    }

    public void debugValues() {
        System.out.println("name = " + name);
        System.out.println("count = " + fireCount);
        System.out.println("parent = " + owner.getName());
        System.out.println("projectile = " + projectile.getName());
        System.out.println("rofPerSecond = " + rofPerSecond);
        System.out.println("rofDelay = " + rofDelay);
        System.out.println("rofTimer = " + rofTimer);
        System.out.println("sndEffect = " + sndEffect);
        System.out.println("target = " + target.getName());
        System.out.println("drawLayer = " + projectile.getDrawLayer());
    }

    public Weapon cpy() {
        return new Weapon(name,
                owner,
                world,
                projectile,
                rofPerSecond,
                sndEffect);
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("rofPerSecond", rofPerSecond);
        json.writeValue("sndEffect", sndEffect);
        json.writeValue("projectileFile", projectileFile);
    }

    @Override
    public void read(Json json, JsonValue jsonMap) {
        name = jsonMap.get("name").asString();
        rofPerSecond = jsonMap.get("rofPerSecond").asFloat();
        this.rofDelay = 1f / rofPerSecond;
        sndEffect = jsonMap.get("sndEffect").asString();
        projectileFile = jsonMap.get("projectileFile").asString();
    }

    public static Weapon load(String filePathName) {
        Debuger.print(3, "Weapon.load", "load", filePathName);
        Json json = new Json();
        Weapon weapon = json.fromJson(Weapon.class, Gdx.files.internal(filePathName).read());
        Projectile newProjectile = Projectile.load(weapon.getProjectileFile());
        weapon.prvtSetProjectile(newProjectile);
        return weapon;
    }

    public void initWeapon(SpaceObject parent, World world, int projectileDrawLayer) {
        this.owner = parent;
        this.world = world;
        this.projectile.initProjectile(parent, world, projectileDrawLayer);
        this.rofDelay = 1f / rofPerSecond;
    }

    public String save() {
        String filePathName = FileManager.generatePathFileName(this);
        FileManager.writeJsonFile(filePathName, this);
        projectile.save();
        return filePathName;
    }

    public String save(String filePathName) {
        FileManager.writeJsonFile(filePathName, this);
        projectile.save();
        return filePathName;
    }

    public String getName() {
        return name;
    }

    public String getProjectileFile() {
        return projectileFile;
    }

    public void setProjectile(Projectile projectile, SpaceObject parent) {
        this.projectile = projectile.cpy(parent);
    }

    private void prvtSetProjectile(Projectile projectile) {
        this.projectile = projectile;
    }

    public void stopFiring() {
        fireCount = 0;
    }

    public void stopFiring(ITargetable target) {
        if (this.target == target) {
            fireCount = 0;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        projectile.setType(type);
    }
    
    
}
