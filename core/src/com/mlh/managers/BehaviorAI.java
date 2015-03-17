/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mlh.actors.Actor;
import com.mlh.interfaces.IDrawable;
import com.mlh.models.Deceleration;
import com.mlh.models.Path_Old;
import com.mlh.utilities.Debuger;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michael
 */
public class BehaviorAI implements IDrawable {

    private Vector2 target;
    private final Actor agent;
    private Deceleration deceleration;
    private Path_Old path;

    private Vector2 oldDesiredVelocity = new Vector2();

    private boolean seek = false;
    private boolean arrive = false;
    private boolean followPath = false;
    private boolean drawTarget = false;
    private boolean useAgentOrigin = false;

    private static final float DECELERATION_TWEAKER = 0.05f;
    private static final float WAYPOINT_DIST_SEEK = 30.0f;
    private static final int MIN_VELOCITY_CHANGE = 20;
    private static final int DRAW_TARGET_SIZE = 5;

    public BehaviorAI(Actor agent) {
        this.agent = agent;
        Vector2 targetOld = new Vector2();
    }

    private void seek() {
        //agent.setHeading(target);
        //agent.setAccelerationForce(agent.getMaxForce());
        /*
         Debuger.print(1, agent.getName(), "origin", agent.getOrigin().toString());

 
        

         agent.setHeading(target.cpy());
         agent.setVelocity(target.cpy().scl(agent.getMaxForce()));

         //agent.setHeading(VectorUtilities.vectorToTarget(getAgentPosition(), target, Vector2.Zero, agent.getMaxSpeed()));
         //agent.setAccelerationForce(agent.getMaxForce());
         Debuger.print(1, agent.getName(), "seeking", "1:desiredVelocity = " + agent.getVelocity());
         */

        Vector2 desiredVelocity = target.cpy().sub(getAgentPosition());
         //Vector2 desiredVelocity = target.cpy().sub(agent.getHeading());

        //Debuger.print(1, agent.getName(), "seeking", "1:desiredVelocity = " + desiredVelocity);
        desiredVelocity.nor();
        //Debuger.print(1, agent.getName(), "seeking", "2:desiredVelocity = " + desiredVelocity);
        desiredVelocity.scl(agent.getMaxForce());
        //Debuger.print(1, agent.getName(), "seeking", "3:desiredVelocity = " + desiredVelocity);

        //desiredVelocity.sub(agent.getVelocity());
        //Debuger.print(1, agent.getName(), "seeking", "4:desiredVelocity = " + desiredVelocity);
        
        // smoother
        desiredVelocity.x = MathUtils.round(desiredVelocity.x);
        desiredVelocity.y = MathUtils.round(desiredVelocity.y);
        
        if ( Math.abs(desiredVelocity.x - oldDesiredVelocity.x) < MIN_VELOCITY_CHANGE
                || Math.abs(desiredVelocity.y - oldDesiredVelocity.y) < MIN_VELOCITY_CHANGE){
            desiredVelocity.set(oldDesiredVelocity);
        }
        
        Debuger.print(1, agent.getName(), "seeking", "5:desiredVelocity = " + desiredVelocity);
        
        agent.setVelocity(desiredVelocity);
        oldDesiredVelocity.set(desiredVelocity);
    }

    private void arrive() {
        Vector2 toTarget = target.cpy().sub(getAgentPosition());

        float dist = getAgentPosition().dst(target);

        if (dist > 0) {
            float speed = dist / ((float) (deceleration.getValue()) * DECELERATION_TWEAKER);

            speed = Math.min(speed, agent.getMaxForce());

            Vector2 desiredVelocity = toTarget.scl(speed / dist);
            desiredVelocity.sub(agent.getVelocity());
            agent.setVelocity(desiredVelocity);
        }
    }

    private void followPath() {
        if (target == null || (getAgentPosition().dst(target) < WAYPOINT_DIST_SEEK)) {
            target = path.getNextWayPoint();

            Debuger.print(3, agent.getName(), "followPath", "moving to > " + target);

            // if path not done seek
            if (!path.isDone()) {
                seekOn();
                arriveOff();
                //target = path.getCurrentWayPoint();
            } else {
                arriveOn(deceleration);
                seekOff();
                followPathOff();
                //target = path.getCurrentWayPoint();
            }
        }
    }

    public void update(float dt) {
        if (followPath) {
            followPath();
        }

        if (seek) {
            if (getAgentPosition().dst(target) < WAYPOINT_DIST_SEEK) {
                agent.setVelocity(Vector2.Zero);
                seekOff();
            } else {
                seek();
            }
        }

        if (arrive) {
            if (getAgentPosition().dst(target) < WAYPOINT_DIST_SEEK) {
                agent.setVelocity(Vector2.Zero);
                arriveOff();
            } else {
                arrive();
            }
        }

        // clear target if no seek && arrive
        // this is needed by follow path to move to the first waypoint 
        if (!seek && !arrive) {
            target = null;
        }
    }

    public void arriveOn(Deceleration deceleration) {
        this.deceleration = deceleration;
        arrive = true;
        seek = false;
    }

    public void arriveOff() {
        arrive = false;
    }

    public void seekOn() {
        seek = true;
        arrive = false;
    }

    public void seekOff() {
        seek = false;
    }

    public void setTarget(Vector2 target) {
        this.target = target.cpy();
    }

    public void followPathOn(Deceleration deceleration) {
        Debuger.print(3, agent.getName(), "followPath", "on");
        if (path == null) {
            String ex = "attempted to follow a null path! BehaviorAI.setPath(Path path) must be called before BehaviorAI.followPathOn is called!";
            Logger.getLogger(BehaviorAI.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        this.deceleration = deceleration;
        followPath = true;
    }

    public void followPathOff() {
        followPath = false;
    }

    public void setPath(Path_Old path) {
        this.path = path.cpy(path);
    }

    public void drawTarget(ShapeRenderer sr) {
        if (target == null) {
            return;
        }

        sr.setAutoShapeType(true);
        sr.setColor(Color.RED);
        sr.begin();
        sr.line(target.x - DRAW_TARGET_SIZE,
                target.y,
                target.x + DRAW_TARGET_SIZE,
                target.y
        );
        sr.line(target.x,
                target.y - DRAW_TARGET_SIZE,
                target.x,
                target.y + DRAW_TARGET_SIZE
        );

        sr.end();
    }

    public boolean shouldDrawTarget() {
        return drawTarget;
    }

    public void setDrawTarget(boolean drawTarget) {
        this.drawTarget = drawTarget;
    }

    @Override
    public void draw(ShapeRenderer sr, SpriteBatch sb) {
        if (drawTarget) {
            drawTarget(sr);
        }
    }

    @Override
    public int getDrawLayer() {
        return agent.getDrawLayer();
    }

    private Vector2 getAgentPosition() {
        if (useAgentOrigin) {
            return agent.getOrigin();
        }
        return agent.getPosition();
    }

    public boolean isUseAgentOrigin() {
        return useAgentOrigin;
    }

    public void setUseAgentOrigin(boolean useAgentOrigin) {
        this.useAgentOrigin = useAgentOrigin;
    }

    public boolean shouldUseAgentOrigin() {
        return useAgentOrigin;
    }

    public void debug() {
        System.out.println("agent.getPosition = " + agent.getPosition());
        System.out.println("agent.getVelocity = " + agent.getVelocity());
        System.out.println("agent.getHeading = " + agent.getHeading());
        System.out.println("agent.getMaxSpeed = " + agent.getMaxSpeed());
    }

    public Vector2 getCurrentWayPoint() {
        return path.getCurrentWayPoint();
    }

    public Vector2 getWayPoint(int index) {
        return path.getWayPoint(index);
    }

    public void stopAll() {
        seek = false;
        arrive = false;
        followPath = false;
        agent.setVelocity(Vector2.Zero);
    }

    public boolean pathDone() {
        return followPath;
    }
    
    public boolean shouldRemove(){
        return agent.shouldRemove();
    }

}
