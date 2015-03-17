/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.math.Vector2;
import com.mlh.interfaces.IUpdateable;
import com.mlh.managers.SteeringBehavior;
import com.mlh.utilities.SmootherV2;

import static com.mlh.utilities.VectorUtilities.perpendicular;

/**
 *
 * @author michael
 */
public abstract class Vehicle extends MovingEntity implements IUpdateable {
    private static final float MIN_VELOCITY = 0.00000001f;
    private static final int NUMSAMPLESFORSMOOTHING = 10;
    private static final float RESONABLE_FORCE = 1000;
    private static final float RESONABLE_SPEED = 1000;
    private static final float RESONABLE_TURN = 360;
    
    // from Entity
    //protected String name;
    //protected Object owner;
    //protected World world;
    //protected final Vector2 position;
    //protected boolean remove;
    //protected int boundryResponce;

    // from MovingEntity
    //protected final Vector2 velocity;
    //a normalized vector pointing in the direction the entity is heading. 
    //protected final Vector2 heading;
    //a vector perpendicular to the heading vector
    //protected final Vector2 side;
    //protected float mass;
    //the maximum speed this entity may travel at.
    //protected float maxSpeed;
    //the maximum force this entity can produce to power itself 
    //(think rockets and thrust)
    //protected float maxForce;
    //the maximum rate (radians per second)this vehicle can rotate         
    //protected float maxTurnRate;
    
    //the steering behavior class
    private SteeringBehavior steering;

    //some steering behaviors give jerky looking movement. The
    //following members are used to smooth the vehicle's heading
    private SmootherV2<Vector2> headingSmoother;

    private Vector2 smoothedHeading;
    //when true, smoothing is active
    private boolean smoothingOn;
    //keeps a track of the most recent update time. (some of the
    //steering behaviors make use of this - see Wander)
    private float timeElapsed;
    
    protected float boundingRadius;

    public Vehicle() {
        this("null", 
            null, 
            null,
            new Vector2(0f,0f),
            0f,
            new Vector2(0f,0f),
            1f,
            RESONABLE_FORCE,
            RESONABLE_SPEED,
            RESONABLE_TURN);
    }
    
    public Vehicle(String name, 
            Object owner, 
            World world,
            Vector2 position,
            float rotation,
            Vector2 velocity,
            float mass,
            float maxForce,
            float maxSpeed,
            float maxTurnRate) {
        super(name, 
            owner, 
            world,
            position,
                velocity,
                maxSpeed,
                new Vector2((float)Math.sin(rotation), (float)-Math.cos(rotation)),
                mass,
                maxTurnRate,
                maxForce);

        smoothedHeading = new Vector2(0, 0);
        smoothingOn = false;
        timeElapsed = 0.0f;
        boundingRadius = 0.0f;
        //InitializeBuffer();

        //set up the steering behavior class
        steering = new SteeringBehavior(this);

        //set up the smoother
        headingSmoother = new SmootherV2<Vector2>(NUMSAMPLESFORSMOOTHING, new Vector2(0.0f, 0.0f));
    }
    
        /**
    *  Updates the vehicle's position and orientation from a series of steering behaviors
     * @param deltaTime
    */
    @Override
    public void update(float deltaTime) {
        
        //update the time elapsed
        timeElapsed = deltaTime;

        //keep a record of its old position so we can update its cell later
        //in this method
        Vector2 oldPos = getPosition();

        Vector2 steeringForce;

        //calculate the combined force from each steering behavior in the 
        //vehicle's list
        steeringForce = steering.calculate();

        //Acceleration = Force/Mass
        Vector2 acceleration = steeringForce.scl(maxForce/mass);

        //update velocity
        velocity.mulAdd(acceleration, deltaTime);

        //make sure vehicle does not exceed maximum velocity
        velocity.limit(maxSpeed);

        //update the position
        position.mulAdd(velocity, deltaTime);

        //update the heading if the vehicle has a non zero velocity
        if (velocity.len() > MIN_VELOCITY) {
            heading.set(velocity.nor());

            side.set(perpendicular(heading));
        }

        //EnforceNonPenetrationConstraint(this, World()->Agents());

        //update the vehicle's current cell if space partitioning is turned on
        /*
        if (Steering().isSpacePartitioningOn()) {
            World().CellSpace().UpdateEntity(this, OldPos);
        }
        */
        
        if (isSmoothingOn()) {
            smoothedHeading = headingSmoother.Update(getHeading());
        }
        
    }

    // accessors
    public SteeringBehavior getSteering(){
        return steering;
    }
    
    public Vector2 getSmoothedHeading() {
        return smoothedHeading.cpy();
    }
    
    public boolean isSmoothingOn() {
        return smoothingOn;
    }
    
    public void smoothingOn() {
        smoothingOn = true;
    }
    public void smoothingOff() {
        smoothingOn = false;
    }
    
    public void toggleSmoothing() {
        smoothingOn = !smoothingOn;
    }
    
    /**
     * @return time elapsed from last update
     */
    public float getTimeElapsed() {
        return timeElapsed;
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    public void setBoundingRadius(float boundingRadius) {
        this.boundingRadius = boundingRadius;
    }
}
