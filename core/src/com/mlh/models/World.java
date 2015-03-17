/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mlh.interfaces.ICollidable;
import com.mlh.interfaces.IDrawable;
import com.mlh.interfaces.IUpdateable;
import com.mlh.managers.CollisionManager;

import java.util.*;

/**
 *
 * @author michael
 */
public class World {

    public static final int BOUNDRY_WRAP = 1;
    public static final int BOUNDRY_REMOVE = 2;
    public static final int BOUNDRY_IGNORE = 3;

    private final ArrayList<IUpdateable> updateables;
    private final ArrayList<Entity> entities;
    private final ArrayList<ICollidable> collidables;
    private final ArrayList<Vehicle> agents;
    private final SortedMap<Integer, ArrayList<IDrawable>> drawables;

    private final CollisionManager collisionManager;

    private int visiableSpaceWidth;
    private int visiableSpaceHeight;
    private int boundarySpaceWidth;     // how far space extends off the screen
    private int boundarySpaceHeight;    // how far space extends off the screen

    public World(
            int visiableSpaceWidth, int visiableSpaceHeight,
            int boundarySpaceWidth, int boundarySpaceHeight
    ) {
        this.visiableSpaceWidth = visiableSpaceWidth;
        this.visiableSpaceHeight = visiableSpaceHeight;
        this.boundarySpaceWidth = boundarySpaceWidth;
        this.boundarySpaceHeight = boundarySpaceHeight;

        updateables = new ArrayList<IUpdateable>();
        entities = new ArrayList<Entity>();
        collidables = new ArrayList<ICollidable>();
        drawables = new TreeMap<Integer, ArrayList<IDrawable>>();
        collisionManager = new CollisionManager(collidables);
        agents = new ArrayList<Vehicle>();
    }

    public void update(float dt) {

        // enchanced for loop will break here
        // java.util.ConcurrentModificationException
        for (int i = 0; i < updateables.size(); i++) {
            updateables.get(i).update(dt);
            if (updateables.get(i).shouldRemove()) {
                updateables.remove(i);
            }
        }

        collisionManager.update(dt);

        Entity ent;
        for (int i = 0; i < entities.size(); i++) {
            ent = entities.get(i);
            checkBoundry(ent);

            if (ent.shouldRemove()) {
                if (ent instanceof ICollidable) {
                    collidables.remove(ent);
                }
                entities.remove(ent);
            }
        }

        for (int i = 0; i < collidables.size(); i++) {
            if (collidables.get(i).shouldRemove()) {
                collidables.remove(i);
            }
        }

        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).shouldRemove()) {
                agents.remove(i);
            }
        }

        // purge drawables
        for (Map.Entry<Integer, ArrayList<IDrawable>> entry : drawables.entrySet()) {
            for (int n = 0; n < entry.getValue().size(); n++) {
                if (entry.getValue().get(n).shouldRemove()) {
                    entry.getValue().remove(n);
                }
            }
        }
    }

    private void checkBoundry(Entity entity) {
        int boundryResp = entity.getBoundryResponce();
        if (boundryResp == World.BOUNDRY_IGNORE || entity.shouldRemove()) {
            return;
        }
        float bx = entity.getPosition().x;
        float by = entity.getPosition().y;
        boolean outsideBoundry = false;
        if (bx > (visiableSpaceWidth + boundarySpaceWidth)
                || bx < (0 - boundarySpaceWidth)
                || by > (visiableSpaceHeight + boundarySpaceHeight)
                || by < (0 - boundarySpaceHeight)) {
            outsideBoundry = true;
        }
        if (outsideBoundry) {
            switch (boundryResp) {
                case BOUNDRY_WRAP:
                    wrapEntity(entity);
                    break;
                case BOUNDRY_REMOVE:
                    entity.remove();
                    break;
            }
        }
    }

    public void draw(ShapeRenderer sr, SpriteBatch sb) {
        for (Map.Entry<Integer, ArrayList<IDrawable>> entry : drawables.entrySet()) {
            for (IDrawable drawable : entry.getValue()) {
                drawable.draw(sr, sb);
            }
        }
    }

    private void addDrawableSprite(IDrawable drawable) {
        if (!this.drawables.containsKey(drawable.getDrawLayer())) {
            this.drawables.put(drawable.getDrawLayer(), new ArrayList<IDrawable>());
        }
        this.drawables.get(drawable.getDrawLayer()).add(drawable);
    }

    private void addCollidable(ICollidable collidable) {
        collidables.add(collidable);
    }

    public void addObject(Object object) {

        if (object instanceof Vehicle) {
            agents.add((Vehicle) object);
        }
        if (object instanceof IUpdateable) {
            updateables.add((IUpdateable) object);
        }
        if (object instanceof Entity) {
            entities.add((Entity) object);
        }
        if (object instanceof IDrawable) {
            addDrawableSprite((IDrawable) object);
        }
        if (object instanceof ICollidable) {
            addCollidable((ICollidable) object);
        }

    }

    private void wrapEntity(Entity entity) {
        if (entity.getPosition().x < (0 - boundarySpaceWidth)) {
            entity.setPosition(
                    visiableSpaceWidth + boundarySpaceWidth,
                    entity.getPosition().y);
        }
        if (entity.getPosition().x > (visiableSpaceWidth + boundarySpaceWidth)) {
            entity.setPosition(
                    0f - boundarySpaceWidth,
                    entity.getPosition().y);
        }
        if (entity.getPosition().y < (0 - boundarySpaceHeight)) {
            entity.setPosition(
                    entity.getPosition().x,
                    visiableSpaceHeight + boundarySpaceHeight);
        }
        if (entity.getPosition().y > (visiableSpaceHeight + boundarySpaceHeight)) {
            entity.setPosition(
                    entity.getPosition().x,
                    0 - boundarySpaceHeight);
        }

    }

    public int getVisiableSpaceWidth() {
        return visiableSpaceWidth;
    }

    public int getVisiableSpaceHeight() {
        return visiableSpaceHeight;
    }

    public int getBoundarySpaceWidth() {
        return boundarySpaceWidth;
    }

    public int getBoundarySpaceHeight() {
        return boundarySpaceHeight;
    }

    public boolean hasEnity(String nameOfEntity) {
        for (Entity e : entities) {
            if (e.getName().equals(nameOfEntity)) {
                return true;
            }
        }
        return false;
    }

    /*
     public Object Walls() {
     throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }*/
    public List<ICollidable> Obstacles() {
        //todo 
        return Collections.unmodifiableList(collidables);
    }

    public List<Vehicle> getAgents() {
        return Collections.unmodifiableList(agents);
    }
    /*
     public Object CellSpace() {
     throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }
     */

    public void TagVehiclesWithinViewRange(ICollidable vehicle, float viewDistance) {
        for (int i = 0; i < collidables.size(); i++) {
            ICollidable other = collidables.get(i);
            float dist = vehicle.getOrigin().dst2(other.getOrigin());
            if (dist <= viewDistance) {
                if (dist <= vehicle.getBoundingRadius() + other.getBoundingRadius()) {
                    other.setTagged(true);
                }
            }
        }
    }

    // todo
    public void TagObstaclesWithinViewRange(ICollidable vehicle, float viewDistance) {
        TagVehiclesWithinViewRange(vehicle, viewDistance);
    }

    public void removeObject(Object object) {
        if (object instanceof IUpdateable) {
            updateables.remove(object);
        }
        if (object instanceof Entity) {
            entities.remove(object);
        }
        if (object instanceof ICollidable) {
            collidables.remove(object);
        }
        if (object instanceof Vehicle) {
            agents.remove(object);
        }
    }
}
