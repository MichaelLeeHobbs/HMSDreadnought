/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mlh.interfaces.IDrawable;
import com.mlh.interfaces.ITargetable;
import com.mlh.interfaces.IUpdateable;
import com.mlh.managers.BehaviorAI;
import com.mlh.models.Deceleration;
import com.mlh.models.Path_Old;
import com.mlh.models.SpaceShip;
import com.mlh.models.World;
import com.mlh.utilities.Debuger;

import java.util.ArrayList;

/**
 *
 * @author michael
 */
public abstract class Actor implements IDrawable, IUpdateable {

    private final String name;
    protected SpaceShip spaceShip;
    protected World world;
    protected final ArrayList<ITargetable> targets;
    protected boolean remove;
    protected boolean dead;
    protected BehaviorAI behaviorAI;
    protected int drawLayer = 255;
    protected float lifeTime = 0;

    public Actor(String name, World world) {
        this.name = name;
        this.world = world;
        targets = new ArrayList<ITargetable>();
        remove = false;
        behaviorAI = new BehaviorAI(this);
    }

    public void setShip(SpaceShip spaceShip, Vector2 spaceShipPosition, Vector2 spaceShipHeading) {
        this.spaceShip = spaceShip;
        this.spaceShip.spawn(spaceShipPosition, spaceShipHeading);
        world.addObject(spaceShip);
    }

    public abstract void init(int drawLayer);
    
    public void init(String ship, Vector2 position, Vector2 heading, int drawLayer) {
        Debuger.print(3, this.getName(), "in init > setShip()", ship);
        setShip(SpaceShip.load(ship, this, world, drawLayer), position, heading);
        Debuger.print(3, this.getName(), "in init > setShip()", "done");
    }

    public void fireAt(ITargetable target) {
        Debuger.print(1, this.getName(), "fireAt", target.getName());
        spaceShip.fireAt(target);
    }

    public void fireAt(ITargetable target, int count) {
        Debuger.print(1, this.getName(), "fireAt: " + target.getName(), "count: " + count);
        spaceShip.fireAt(target, count);
    }
    
    public void fireAllWeaponsAt(Actor target, boolean stageredFire){
        spaceShip.fireAllWeaponsAt(target.getTarget(), stageredFire);
    }


    @Override
    public void update(float dt) {
        lifeTime += dt;
        if (spaceShip.isDisabled()) {
            behaviorAI.stopAll();
            handleDisabled();
            
            //todo add after death actions
            return;
        }
        
        behaviorAI.update(dt);
        doActions();
    }

    public boolean shouldRemove() {
        return remove;
    }

    public void remove(boolean remove) {
        this.remove = remove;
    }

    public boolean isDead() {
        return dead;
    }

    public String getName() {
        return name;
    }

    public Vector2 getPosition() {
        return spaceShip.getPosition();
    }

    public Vector2 getVelocity() {
        return spaceShip.getVelocity();
    }

    public void setVelocity(Vector2 newVelocity) {
        spaceShip.setVelocity(newVelocity);
    }

    public Vector2 getOrigin() {
        return spaceShip.getOrigin();
    }

    public Vector2 getHeading() {
        return spaceShip.getHeading();
    }

    public float getMaxSpeed() {
        return spaceShip.getMaxSpeed();
    }

    public float getMaxForce() {
        return spaceShip.getMaxForce();
    }

    public void seek(Vector2 targetPosition) {
        behaviorAI.setTarget(targetPosition);
        behaviorAI.seekOn();
    }

    public void arrive(Vector2 targetPosition, Deceleration deceleration) {
        behaviorAI.setTarget(targetPosition);
        behaviorAI.arriveOn(deceleration);
    }

    public void followPath(Path_Old path, Deceleration deceleration) {
        behaviorAI.setPath(path);
        behaviorAI.followPathOn(deceleration);
    }

    public void fireAt(Actor actor) {
        // -1 = continus fire
        spaceShip.fireAt(actor.getTarget(), -1);
    }

    public void stopFiring() {
        spaceShip.stopFiring();
    }

    public ITargetable getTarget() {
        return spaceShip;
    }

    public boolean shouldDrawTarget() {
        return behaviorAI.shouldDrawTarget();
    }

    public void setDrawTarget(boolean drawTarget) {
        behaviorAI.setDrawTarget(drawTarget);
    }

    @Override
    public void draw(ShapeRenderer sr, SpriteBatch sb) {
        behaviorAI.draw(sr, sb);
    }

    @Override
    public int getDrawLayer() {
        return drawLayer;
    }

    public void setDrawLayer(int layer) {
        drawLayer = layer;
    }

    public void setUseOriginAsPosition(boolean onOff) {
        behaviorAI.setUseAgentOrigin(onOff);
    }

    public boolean shouldUseOriginAsPosition(boolean onOff) {
        return behaviorAI.shouldUseAgentOrigin();
    }

    protected abstract void doActions();
    
    public void setHeading(Vector2 heading) {
        spaceShip.setHeading(heading);
    }
    
    protected abstract void handleDisabled();
    
    public void setType(String type) {
        spaceShip.setType(type);
    }
    
    public String getType(){
        return spaceShip.getType();
    }
}
