/**
 * Desc: class to encapsulate steering behaviors for a Vehicle
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.mlh.managers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mlh.interfaces.ICollidable;
import com.mlh.interfaces.ITargetable;
import com.mlh.models.Path;
import com.mlh.models.Vehicle;
import com.mlh.utilities.VectorUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author michael
 */
public class SteeringBehavior {

    //the radius of the constraining circle for the wander behavior
    final float WanderRad = 1.2f;
    //distance the wander circle is projected in front of the agent
    final float WanderDist = 2.0f;
    //the maximum amount of displacement along the circle each frame
    final float WanderJitterPerSec = 80.0f;
    //used in path following
    final float WaypointSeekDist = 20;

    final float HALF_PI = MathUtils.PI / 2f;

    private ITargetable target;

    public static enum summing_method {

        weighted_average(0),
        prioritized(1),
        dithered(2);

        summing_method(int i) {
        }
    }

    private enum behavior_type {

        none(0x00000),
        seek(0x00002),
        flee(0x00004),
        arrive(0x00008),
        wander(0x00010),
        cohesion(0x00020),
        separation(0x00040),
        allignment(0x00080),
        obstacle_avoidance(0x00100),
        wall_avoidance(0x00200),
        follow_path(0x00400),
        pursuit(0x00800),
        evade(0x01000),
        interpose(0x02000),
        hide(0x04000),
        flock(0x08000),
        offset_pursuit(0x10000);
        private int flag;

        behavior_type(int flag) {
            this.flag = flag;
        }

        public int flag() {
            return this.flag;
        }
    }

    //a pointer to the owner of this instance
    private final Vehicle m_pVehicle;
    //the steering force created by the combined effect of all
    //the selected behaviors
    private Vector2 m_vSteeringForce = new Vector2(0, 0);
    //these can be used to keep track of friends, pursuers, or prey
    private Vehicle m_pTargetAgent1;
    private Vehicle m_pTargetAgent2;

    //length of the 'detection box' utilized in obstacle avoidance
    private float m_dDBoxLength;
    //a vertex buffer to contain the feelers rqd for wall avoidance  
    private final List<Vector2> m_Feelers;
    //the length of the 'feeler/s' used in wall detection
    private final float m_dWallDetectionFeelerLength;
    //the current position on the wander circle the agent is
    //attempting to steer towards
    private final Vector2 m_vWanderTarget;
    //explained above
    private final float m_dWanderJitter;
    private final float m_dWanderRadius;
    private final float m_dWanderDistance;
    //multipliers. These can be adjusted to effect strength of the  
    //appropriate behavior. Useful to get flocking the way you require
    //for example.
    private final float m_dWeightSeparation;
    private final float m_dWeightCohesion;
    private final float m_dWeightAlignment;
    private final float m_dWeightWander;
    private final float m_dWeightObstacleAvoidance;
    private final float m_dWeightSeek;
    private final float m_dWeightFlee;
    private final float m_dWeightArrive;
    private final float m_dWeightPursuit;
    private final float m_dWeightOffsetPursuit;
    private final float m_dWeightInterpose;
    private final float m_dWeightHide;
    private final float m_dWeightEvade;
    private final float m_dWeightFollowPath;
    //how far the agent can 'see'
    private final float m_dViewDistance;
    //pointer to any current path
    private Path m_pPath;
    //the distance (squared) a vehicle has to be from a path waypoint before
    //it starts seeking to the next waypoint
    private final float m_dWaypointSeekDistSq;
    //any offset used for formations or offset pursuit
    private Vector2 m_vOffset;
    //binary flags to indicate whether or not a behavior should be active
    private int m_iFlags;

    //Arrive makes use of these to determine how quickly a vehicle
    //should decelerate to its target
    private enum Deceleration {

        slow(3), normal(2), fast(1);
        private int dec;

        Deceleration(int d) {
            this.dec = d;
        }

        public int value() {
            return dec;
        }
    }
    //default
    private Deceleration m_Deceleration;
    //is cell space partitioning to be used or not?
    private boolean m_bCellSpaceOn;
    //what type of method is used to sum any active behavior
    private summing_method m_SummingMethod;

    //this function tests if a specific bit of m_iFlags is set
    private boolean On(behavior_type bt) {
        return (m_iFlags & bt.flag()) == bt.flag();
    }

    /**
     *
     * This function calculates how much of its max steering force the vehicle
     * has left to apply and then applies that amount of the force to add.
     */
    private boolean AccumulateForce(Vector2 RunningTot,
            Vector2 ForceToAdd) {

        //calculate how much steering force the vehicle has used so far
        float MagnitudeSoFar = RunningTot.len();

        //calculate how much steering force remains to be used by this vehicle
        float MagnitudeRemaining = m_pVehicle.getMaxForce() - MagnitudeSoFar;

        //return false if there is no more force left to use
        if (MagnitudeRemaining <= 0.0) {
            return false;
        }

        //calculate the magnitude of the force we want to add
        float MagnitudeToAdd = ForceToAdd.len();

        //if the magnitude of the sum of ForceToAdd and the running total
        //does not exceed the maximum force available to this vehicle, just
        //add together. Otherwise add as much of the ForceToAdd vector is
        //possible without going over the max.
        if (MagnitudeToAdd < MagnitudeRemaining) {
            RunningTot.add(ForceToAdd);
        } else {
            //add it to the steering force
            RunningTot.mulAdd(ForceToAdd.cpy().nor(), MagnitudeRemaining);
        }

        return true;
    }

    /**
     * Creates the antenna utilized by WallAvoidance
     */
    private void CreateFeelers() {
        m_Feelers.clear();
        //feeler pointing straight in front
        m_Feelers.add(m_pVehicle.getPosition().mulAdd(m_pVehicle.getHeading(), m_dWallDetectionFeelerLength));

        //feeler to left
        Vector2 temp = m_pVehicle.getHeading();
        temp = VectorUtilities.vec2RotateAroundOrigin(temp, HALF_PI * 3.5f);
        //Vec2DRotateAroundOrigin(temp, HALF_PI * 3.5f);
        m_Feelers.add(m_pVehicle.getPosition().mulAdd(temp, m_dWallDetectionFeelerLength / 2.0f));

        //feeler to right
        temp = m_pVehicle.getHeading();
        temp = VectorUtilities.vec2RotateAroundOrigin(temp, HALF_PI * 3.5f);
        //Vec2DRotateAroundOrigin(temp, HALF_PI * 0.5f);
        m_Feelers.add(m_pVehicle.getPosition().mulAdd(temp, m_dWallDetectionFeelerLength / 2.0f));
    }

    /////////////////////////////////////////////////////////////////////////////// START OF BEHAVIORS
    /**
     * Given a target, this behavior returns a steering force which will direct
     * the agent towards the target
     */
    private Vector2 Seek(Vector2 TargetPos) {
        Vector2 DesiredVelocity = TargetPos.sub(m_pVehicle.getPosition()).nor().scl(m_pVehicle.getMaxSpeed());
        return DesiredVelocity.sub(m_pVehicle.getVelocity());
    }

    /**
     * Does the opposite of Seek
     */
    private Vector2 Flee(Vector2 TargetPos) {
        //only flee if the target is within 'panic distance'. Work in distance
        //squared space.
        /* const float PanicDistanceSq = 100.0f * 100.0;
         if (Vec2DDistanceSq(m_pVehicle.getPosition(), target) > PanicDistanceSq)
         {
         return new Vector2(0,0);
         }
         */

        Vector2 DesiredVelocity = m_pVehicle.getPosition().sub(TargetPos).nor().scl(m_pVehicle.getMaxSpeed());

        return DesiredVelocity.sub(m_pVehicle.getVelocity());
    }

    /**
     * This behavior is similar to seek but it attempts to arrive at the target
     * with a zero velocity
     */
    private Vector2 Arrive(Vector2 TargetPos, Deceleration deceleration) {
        Vector2 ToTarget = TargetPos.sub(m_pVehicle.getPosition());

        //calculate the distance to the target
        float dist = ToTarget.len();

        if (dist > 0) {
            //because Deceleration is enumerated as an int, this value is required
            //to provide fine tweaking of the deceleration..
            final float DecelerationTweaker = 0.3f;

            //calculate the speed required to reach the target given the desired
            //deceleration
            float speed = dist / (deceleration.value() * DecelerationTweaker);

            //make sure the velocity does not exceed the max
            speed = Math.min(speed, m_pVehicle.getMaxSpeed());

            //from here proceed just like Seek except we don't need to normalize 
            //the ToTarget vector because we have already gone to the trouble
            //of calculating its length: dist. 
            Vector2 DesiredVelocity = ToTarget.scl(speed / dist);

            return DesiredVelocity.sub(m_pVehicle.getVelocity());
        }

        return new Vector2(0, 0);
    }

    /**
     * this behavior creates a force that steers the agent towards the evader
     */
    private Vector2 Pursuit(final Vehicle evader) {
        //if the evader is ahead and facing the agent then we can just seek
        //for the evader's current position.
        Vector2 ToEvader = evader.getPosition().sub(m_pVehicle.getPosition());

        float RelativeHeading = m_pVehicle.getHeading().dot(evader.getHeading());

        if ((ToEvader.dot(m_pVehicle.getHeading()) > 0)
                && (RelativeHeading < -0.95)) //acos(0.95)=18 degs
        {
            return Seek(evader.getPosition());
        }

        //Not considered ahead so we predict where the evader will be.
        //the lookahead time is propotional to the distance between the evader
        //and the pursuer; and is inversely proportional to the sum of the
        //agent's velocities
        float LookAheadTime = ToEvader.len()
                / (m_pVehicle.getMaxSpeed() + evader.getSpeed());

        //now seek to the predicted future position of the evader
        return Seek(evader.getPosition().add(evader.getVelocity().scl(LookAheadTime)));
    }

    /**
     * similar to pursuit except the agent Flees from the estimated future
     * position of the pursuer
     */
    private Vector2 Evade(final Vehicle pursuer) {
        // Not necessary to include the check for facing direction this time

        Vector2 ToPursuer = pursuer.getPosition().sub(m_pVehicle.getPosition());

        //uncomment the following two lines to have Evade only consider pursuers 
        //within a 'threat range'
        final float ThreatRange = 100.0f;
        if (ToPursuer.len2() > ThreatRange * ThreatRange) {
            return new Vector2();
        }

        //the lookahead time is propotional to the distance between the pursuer
        //and the pursuer; and is inversely proportional to the sum of the
        //agents' velocities
        float LookAheadTime = ToPursuer.len()
                / (m_pVehicle.getMaxSpeed() + pursuer.getSpeed());

        //now flee away from predicted future position of the pursuer
        return Flee(pursuer.getPosition().mulAdd(pursuer.getVelocity(), LookAheadTime));
    }

    /**
     * This behavior makes the agent wander about randomly
     */
    private Vector2 Wander() {
        //this behavior is dependent on the update rate, so this line must
        //be included when using time independent framerate.
        float JitterThisTimeSlice = (m_dWanderJitter * m_pVehicle.getTimeElapsed());

        //first, add a small random vector to the target's position
        m_vWanderTarget.add(new Vector2(MathUtils.random(-1, 1) * JitterThisTimeSlice,
                MathUtils.random(-1, 1) * JitterThisTimeSlice));

        //reproject this new vector back on to a unit circle
        m_vWanderTarget.nor();

        //increase the length of the vector to the same as the radius
        //of the wander circle
        m_vWanderTarget.scl(m_dWanderRadius);

        //move the target into a position WanderDist in front of the agent
        Vector2 target = m_vWanderTarget.add(new Vector2(m_dWanderDistance, 0));

        //project the target into world space
        Vector2 Target = VectorUtilities.pointToWorldSpace(target,
                m_pVehicle.getHeading(),
                m_pVehicle.getSide(),
                m_pVehicle.getPosition());

        //and steer towards it
        return Target.sub(m_pVehicle.getPosition());
    }

    /**
     * Given a vector of obstacles, this method returns a steering force that
     * will prevent the agent colliding with the closest obstacle
     */
    private Vector2 ObstacleAvoidance(List<ICollidable> obstacles) {
        //the detection box length is proportional to the agent's velocity
        m_dDBoxLength = MinDetectionBoxLength
                + (m_pVehicle.getSpeed() / m_pVehicle.getMaxSpeed())
                * MinDetectionBoxLength;

        //tag all obstacles within range of the box for processing
        m_pVehicle.getWorld().TagObstaclesWithinViewRange(m_pVehicle, m_dDBoxLength);

        //this will keep track of the closest intersecting obstacle (CIB)
        ICollidable ClosestIntersectingObstacle = null;

        //this will be used to track the distance to the CIB
        float DistToClosestIP = Float.MAX_VALUE;

        //this will record the transformed local coordinates of the CIB
        Vector2 LocalPosOfClosestObstacle = new Vector2();

        ListIterator<ICollidable> it = obstacles.listIterator();

        while (it.hasNext()) {
            //if the obstacle has been tagged within range proceed
            ICollidable curOb = it.next();
            if (curOb.isTagged()) {
                //calculate this obstacle's position in local space
                Vector2 LocalPos = VectorUtilities.pointToLocalSpace(curOb.getOrigin(),
                        m_pVehicle.getHeading(),
                        m_pVehicle.getSide(),
                        m_pVehicle.getPosition());

                //if the local position has a negative x value then it must lay
                //behind the agent. (in which case it can be ignored)
                if (LocalPos.x >= 0) {
                    //if the distance from the x axis to the object's position is less
                    //than its radius + half the width of the detection box then there
                    //is a potential intersection.
                    float ExpandedRadius = curOb.getBoundingRadius() + m_pVehicle.getBoundingRadius();

                    if (Math.abs(LocalPos.y) < ExpandedRadius) {
                        //now to do a line/circle intersection test. The center of the 
                        //circle is represented by (cX, cY). The intersection points are 
                        //given by the formula x = cX +/-sqrt(r^2-cY^2) for y=0. 
                        //We only need to look at the smallest positive value of x because
                        //that will be the closest point of intersection.
                        float cX = LocalPos.x;
                        float cY = LocalPos.y;

                        //we only need to calculate the sqrt part of the above equation once
                        float SqrtPart = (float) Math.sqrt(ExpandedRadius * ExpandedRadius - cY * cY);

                        float ip = cX - SqrtPart;

                        if (ip <= 0.0) {
                            ip = cX + SqrtPart;
                        }

                        //test to see if this is the closest so far. If it is keep a
                        //record of the obstacle and its local coordinates
                        if (ip < DistToClosestIP) {
                            DistToClosestIP = ip;

                            ClosestIntersectingObstacle = curOb;

                            LocalPosOfClosestObstacle = LocalPos;
                        }
                    }
                }
            }
        }

        //if we have found an intersecting obstacle, calculate a steering 
        //force away from it
        Vector2 SteeringForce = new Vector2();

        if (ClosestIntersectingObstacle != null) {
            //the closer the agent is to an object, the stronger the 
            //steering force should be
            float multiplier = 1.0f + (m_dDBoxLength - LocalPosOfClosestObstacle.x)
                    / m_dDBoxLength;

            //calculate the lateral force
            SteeringForce.y = (ClosestIntersectingObstacle.getBoundingRadius()
                    - LocalPosOfClosestObstacle.y) * multiplier;

            //apply a braking force proportional to the obstacles distance from
            //the vehicle. 
            final float BrakingWeight = 0.2f;

            SteeringForce.x = (ClosestIntersectingObstacle.getBoundingRadius()
                    - LocalPosOfClosestObstacle.x)
                    * BrakingWeight;
        }

        //finally, convert the steering vector from local to world space
        return VectorUtilities.VectorToWorldSpace(SteeringForce,
                m_pVehicle.getHeading(),
                m_pVehicle.getSide());
    }

    /* ================= not used ================= todo    
     // * This returns a steering force that will keep the agent away from any
     // * walls it may encounter
     // *
     private Vector2 WallAvoidance(final List<Wall2D> walls) {
     //the feelers are contained in a std::vector, m_Feelers
     CreateFeelers();

     float DistToThisIP = 0.0f;
     float DistToClosestIP = Float.MAX_VALUE;

     //this will hold an index into the vector of walls
     int ClosestWall = -1;

     Vector2 SteeringForce = new Vector2(),
     point = new Vector2(), //used for storing temporary info
     ClosestPoint = new Vector2();  //holds the closest intersection point

     //examine each feeler in turn
     for (int flr = 0; flr < m_Feelers.size(); ++flr) {
     //run through each wall checking for any intersection points
     float DistToThisIPRef = DistToThisIP;
     for (int w = 0; w < walls.size(); ++w) {
     if (LineIntersection2D(m_pVehicle.getPosition(),
     m_Feelers.get(flr),
     walls.get(w).From(),
     walls.get(w).To(),
     DistToThisIPRef,
     point)) {
     DistToThisIP = DistToThisIPRef;
     //is this the closest found so far? If so keep a record
     if (DistToThisIP < DistToClosestIP) {
     DistToClosestIP = DistToThisIP;

     ClosestWall = w;

     ClosestPoint = point;
     }
     }
     }//next wall

     //if an intersection point has been detected, calculate a force  
     //that will direct the agent away
     if (ClosestWall >= 0) {
     //calculate by what distance the projected position of the agent
     //will overshoot the wall
     Vector2 OverShoot = m_Feelers.get(flr).sub(ClosestPoint);

     //create a force in the direction of the wall normal, with a 
     //magnitude of the overshoot
     SteeringForce = walls.get(ClosestWall).nor().scl(OverShoot.len());
     }

     }//next feeler

     return SteeringForce;
     }
     */ //================= not used ================= todo
    /**
     * this calculates a force repelling from the other neighbors
     */
    Vector2 Separation(final List<Vehicle> neighbors) {
        Vector2 SteeringForce = new Vector2();

        for (int a = 0; a < neighbors.size(); ++a) {
            //make sure this agent isn't included in the calculations and that
            //the agent being examined is close enough. ***also make sure it doesn't
            //include the evade target ***
            if ((neighbors.get(a) != m_pVehicle) && neighbors.get(a).isTagged()
                    && (neighbors.get(a) != m_pTargetAgent1)) {
                Vector2 ToAgent = m_pVehicle.getPosition().sub(neighbors.get(a).getPosition());

                //scale the force inversely proportional to the agents distance  
                //from its neighbor.
                SteeringForce.add(ToAgent.nor().scl(ToAgent.len()));
            }
        }

        return SteeringForce;
    }

    /**
     * returns a force that attempts to align this agents heading with that of
     * its neighbors
     */
    private Vector2 Alignment(final List<Vehicle> neighbors) {
        //used to record the average heading of the neighbors
        Vector2 AverageHeading = new Vector2();

        //used to count the number of vehicles in the neighborhood
        int NeighborCount = 0;

        //iterate through all the tagged vehicles and sum their heading vectors  
        for (int a = 0; a < neighbors.size(); ++a) {
            //make sure *this* agent isn't included in the calculations and that
            //the agent being examined  is close enough ***also make sure it doesn't
            //include any evade target ***
            if ((neighbors.get(a) != m_pVehicle) && neighbors.get(a).isTagged()
                    && (neighbors.get(a) != m_pTargetAgent1)) {
                AverageHeading.add(neighbors.get(a).getHeading());

                ++NeighborCount;
            }
        }

        //if the neighborhood contained one or more vehicles, average their
        //heading vectors.
        if (NeighborCount > 0) {
            AverageHeading.scl((float) NeighborCount);
            AverageHeading.sub(m_pVehicle.getHeading());
        }

        return AverageHeading;
    }

    /**
     * returns a steering force that attempts to move the agent towards the
     * center of mass of the agents in its immediate area
     */
    private Vector2 Cohesion(final List<Vehicle> neighbors) {
        //first find the center of mass of all the agents
        Vector2 CenterOfMass = new Vector2(), SteeringForce = new Vector2();

        int NeighborCount = 0;

        //iterate through the neighbors and sum up all the position vectors
        for (int a = 0; a < neighbors.size(); ++a) {
            //make sure *this* agent isn't included in the calculations and that
            //the agent being examined is close enough ***also make sure it doesn't
            //include the evade target ***
            if ((neighbors.get(a) != m_pVehicle) && neighbors.get(a).isTagged()
                    && (neighbors.get(a) != m_pTargetAgent1)) {
                CenterOfMass.add(neighbors.get(a).getPosition());

                ++NeighborCount;
            }
        }

        if (NeighborCount > 0) {
            //the center of mass is the average of the sum of positions
            CenterOfMass.scl((float) NeighborCount);

            //now seek towards that position
            SteeringForce = Seek(CenterOfMass);
        }

        //the magnitude of cohesion is usually much larger than separation or
        //allignment so it usually helps to normalize it.
        return SteeringForce.nor();
    }

    /* NOTE: the next three behaviors are the same as the above three, except
     that they use a cell-space partition to find the neighbors
     */
    /**
     * this calculates a force repelling from the other neighbors
     *
     * USES SPACIAL PARTITIONING
     */
    /*
     private Vector2 SeparationPlus(final List<Vehicle> neighbors) {
     Vector2 SteeringForce = new Vector2();

     //iterate through the neighbors and sum up all the position vectors
     for (Entity pV = m_pVehicle.getWorld().CellSpace().begin();
     !m_pVehicle.getWorld().CellSpace().end();
     pV = m_pVehicle.getWorld().CellSpace().next()) {
     //make sure this agent isn't included in the calculations and that
     //the agent being examined is close enough
     if (pV != m_pVehicle) {
     Vector2 ToAgent = m_pVehicle.getPosition().sub(pV.getPosition());

     //scale the force inversely proportional to the agents distance  
     //from its neighbor.
     SteeringForce.add(ToAgent.nor().scl(1f/ToAgent.len()));
     }

     }

     return SteeringForce;
     }
     */
    /**
     * returns a force that attempts to align this agents heading with that of
     * its neighbors
     *
     * USES SPACIAL PARTITIONING
     */
    /* todo
     private Vector2 AlignmentPlus(final List<Vehicle> neighbors) {
     //This will record the average heading of the neighbors
     Vector2 AverageHeading = new Vector2();

     //This count the number of vehicles in the neighborhood
     float NeighborCount = 0.0f;

     //iterate through the neighbors and sum up all the position vectors
     for (MovingEntity pV = m_pVehicle.getWorld().CellSpace().begin();
     !m_pVehicle.getWorld().CellSpace().end();
     pV = m_pVehicle.getWorld().CellSpace().next()) {
     //make sure *this* agent isn't included in the calculations and that
     //the agent being examined  is close enough
     if (pV != m_pVehicle) {
     AverageHeading.add(pV.getHeading());
     ++NeighborCount;
     }
     }

     //if the neighborhood contained one or more vehicles, average their
     //heading vectors.
     if (NeighborCount > 0.0) {
     AverageHeading.scl(NeighborCount);
     AverageHeading.sub(m_pVehicle.getHeading());
     }

     return AverageHeading;
     }
     */
    /**
     * returns a steering force that attempts to move the agent towards the
     * center of mass of the agents in its immediate area
     *
     * USES SPACIAL PARTITIONING
     */
    /* todo
     private Vector2 CohesionPlus(final List<Vehicle> neighbors) {
     //first find the center of mass of all the agents
     Vector2 CenterOfMass = new Vector2(), SteeringForce = new Vector2();

     int NeighborCount = 0;

     //iterate through the neighbors and sum up all the position vectors
     for (Entity pV = m_pVehicle.getWorld().CellSpace().begin();
     !m_pVehicle.getWorld().CellSpace().end();
     pV = m_pVehicle.getWorld().CellSpace().next()) {
     //make sure *this* agent isn't included in the calculations and that
     //the agent being examined is close enough
     if (pV != m_pVehicle) {
     CenterOfMass.add(pV.getPosition());

     ++NeighborCount;
     }
     }

     if (NeighborCount > 0) {
     //the center of mass is the average of the sum of positions
     CenterOfMass.scl((float) NeighborCount);

     //now seek towards that position
     SteeringForce = Seek(CenterOfMass);
     }

     //the magnitude of cohesion is usually much larger than separation or
     //allignment so it usually helps to normalize it.
     return SteeringForce.nor();
     }
     */
    /**
     * Given two agents, this method returns a force that attempts to position
     * the vehicle between them
     */
    private Vector2 Interpose(final Vehicle AgentA, final Vehicle AgentB) {
        //first we need to figure out where the two agents are going to be at 
        //time T in the future. This is approximated by determining the time
        //taken to reach the mid way point at the current time at at max speed.
        Vector2 MidPoint = AgentA.getPosition().add(AgentB.getPosition()).scl(2.0f);

        float TimeToReachMidPoint = m_pVehicle.getPosition().dst(MidPoint)
                / m_pVehicle.getMaxSpeed();

        //now we have T, we assume that agent A and agent B will continue on a
        //straight trajectory and extrapolate to get their future positions
        Vector2 APos = AgentA.getPosition().add(AgentA.getVelocity().scl(TimeToReachMidPoint));
        Vector2 BPos = AgentB.getPosition().add(AgentB.getVelocity().scl(TimeToReachMidPoint));

        //calculate the mid point of these predicted positions
        MidPoint = APos.add(BPos).scl(2.0f);

        //then steer to Arrive at it
        return Arrive(MidPoint, Deceleration.fast);
    }

    private Vector2 Hide(final Vehicle hunter, final List<ICollidable> obstacles) {
        float DistToClosest = Float.MAX_VALUE;
        Vector2 BestHidingSpot = new Vector2();

        ListIterator<ICollidable> it = obstacles.listIterator();
        ICollidable closest;

        while (it.hasNext()) {
            ICollidable curOb = it.next();
            //calculate the position of the hiding spot for this obstacle
            Vector2 HidingSpot = GetHidingPosition(curOb.getOrigin(),
                    curOb.getBoundingRadius(),
                    hunter.getPosition());

            //work in distance-squared space to find the closest hiding
            //spot to the agent
            float dist = HidingSpot.dst(m_pVehicle.getPosition());

            if (dist < DistToClosest) {
                DistToClosest = dist;

                BestHidingSpot = HidingSpot;

                closest = curOb;
            }
        }//end while

        //if no suitable obstacles found then Evade the hunter
        if (DistToClosest == Float.MAX_VALUE) {
            return Evade(hunter);
        }

        //else use Arrive on the hiding spot
        return Arrive(BestHidingSpot, Deceleration.fast);
    }

    /**
     * Given the position of a hunter, and the position and radius of an
     * obstacle, this method calculates a position DistanceFromBoundary away
     * from its bounding radius and directly opposite the hunter
     */
    private Vector2 GetHidingPosition(final Vector2 posOb,
            final float radiusOb,
            final Vector2 posHunter) {
        //calculate how far away the agent is to be from the chosen obstacle's
        //bounding radius
        final float DistanceFromBoundary = 30.0f;
        float DistAway = radiusOb + DistanceFromBoundary;

        //calculate the heading toward the object from the hunter
        Vector2 ToOb = posOb.sub(posHunter).nor();

        //scale it to size and add to the obstacles position to get
        //the hiding spot.
        return ToOb.scl(DistAway).add(posOb);
    }

    /**
     * Given a series of Vector2s, this method produces a force that will move
     * the agent along the waypoints in order. The agent uses the 'Seek'
     * behavior to move to the next waypoint - unless it is the last waypoint,
     * in which case it 'Arrives'
     */
    private Vector2 FollowPath() {
        //move to next target if close enough to current target (working in
        //distance squared space)
        if (m_pPath.CurrentWaypoint().dst2(m_pVehicle.getPosition()) < m_dWaypointSeekDistSq) {
            m_pPath.SetNextWaypoint();
        }

        if (!m_pPath.Finished()) {
            return Seek(m_pPath.CurrentWaypoint());
        } else {
            return Arrive(m_pPath.CurrentWaypoint(), Deceleration.normal);
        }
    }

    /**
     * Produces a steering force that keeps a vehicle at a specified offset from
     * a leader vehicle
     */
    private Vector2 OffsetPursuit(final Vehicle leader,
            final Vector2 offset) {
        //calculate the offset's position in world space
        Vector2 WorldOffsetPos = VectorUtilities.pointToWorldSpace(offset,
                leader.getHeading(),
                leader.getSide(),
                leader.getPosition());

        Vector2 ToOffset = WorldOffsetPos.sub(m_pVehicle.getPosition());

        //the lookahead time is propotional to the distance between the leader
        //and the pursuer; and is inversely proportional to the sum of both
        //agent's velocities
        float LookAheadTime = ToOffset.len()
                / (m_pVehicle.getMaxSpeed() + leader.getSpeed());

        //now Arrive at the predicted future position of the offset
        return Arrive(WorldOffsetPos.add(leader.getVelocity().scl(LookAheadTime)), Deceleration.fast);
    }

//------------------------- ctor -----------------------------------------
//
//------------------------------------------------------------------------
    //use these values to tweak the amount that each steering force
//contributes to the total steering force
    private static final float SeparationWeight = 1.0f;
    private static final float AlignmentWeight = 1.0f;
    private static final float CohesionWeight = 2.0f;
    private static final float ObstacleAvoidanceWeight = 10.0f;
    private static final float WallAvoidanceWeight = 10.0f;
    private static final float WanderWeight = 1.0f;
    private static final float SeekWeight = 1.0f;
    private static final float FleeWeight = 1.0f;
    private static final float ArriveWeight = 1.0f;
    private static final float PursuitWeight = 1.0f;
    private static final float OffsetPursuitWeight = 1.0f;
    private static final float InterposeWeight = 1.0f;
    private static final float HideWeight = 1.0f;
    private static final float EvadeWeight = 0.01f;
    private static final float FollowPathWeight = 0.05f;

    //how close a neighbour must be before an agent perceives it (considers it
    //to be within its neighborhood)
    private static final float ViewDistance = 50.0f;

    //used in obstacle avoidance
    private static final float MinDetectionBoxLength = 40.0f;

    //used in wall avoidance
    private static final float WallDetectionFeelerLength = 40.0f;

    //these are the probabilities that a steering behavior will be used
    //when the Prioritized Dither calculate method is used to sum
    //combined behaviors
    private static final float prWallAvoidance = 0.5f;
    private static final float prObstacleAvoidance = 0.5f;
    private static final float prSeparation = 0.2f;
    private static final float prAlignment = 0.3f;
    private static final float prCohesion = 0.6f;
    private static final float prWander = 0.8f;
    private static final float prSeek = 0.8f;
    private static final float prFlee = 0.6f;
    private static final float prEvade = 1.0f;
    private static final float prHide = 0.8f;
    private static final float prArrive = 0.5f;

    public SteeringBehavior(Vehicle agent) {

        m_pVehicle = agent;
        m_iFlags = 0;
        m_dDBoxLength = MinDetectionBoxLength;
        m_dWeightCohesion = CohesionWeight;
        m_dWeightAlignment = AlignmentWeight;
        m_dWeightSeparation = SeparationWeight;
        m_dWeightObstacleAvoidance = ObstacleAvoidanceWeight;
        m_dWeightWander = WanderWeight;
        float m_dWeightWallAvoidance = WallAvoidanceWeight;
        m_dViewDistance = ViewDistance;
        m_dWallDetectionFeelerLength = WallDetectionFeelerLength;
        m_Feelers = new ArrayList<Vector2>(3);
        m_Deceleration = Deceleration.normal;
        m_pTargetAgent1 = null;
        m_pTargetAgent2 = null;
        m_dWanderDistance = WanderDist;
        m_dWanderJitter = WanderJitterPerSec;
        m_dWanderRadius = WanderRad;
        m_dWaypointSeekDistSq = WaypointSeekDist * WaypointSeekDist;
        m_dWeightSeek = SeekWeight;
        m_dWeightFlee = FleeWeight;
        m_dWeightArrive = ArriveWeight;
        m_dWeightPursuit = PursuitWeight;
        m_dWeightOffsetPursuit = OffsetPursuitWeight;
        m_dWeightInterpose = InterposeWeight;
        m_dWeightHide = HideWeight;
        m_dWeightEvade = EvadeWeight;
        m_dWeightFollowPath = FollowPathWeight;
        m_bCellSpaceOn = false;
        m_SummingMethod = summing_method.prioritized;

        //stuff for the wander behavior
        float theta = MathUtils.random(1) * MathUtils.PI2;

        //create a vector to a target position on the wander circle
        m_vWanderTarget = new Vector2(m_dWanderRadius * MathUtils.cos(theta),
                m_dWanderRadius * MathUtils.sin(theta));

        //create a Path
        m_pPath = new Path();
        m_pPath.LoopOn();

    }

    //a vertex buffer rqd for drawing the detection box
    //static ArrayList<Vector2> box = new ArrayList<Vector2>(4);
    /////////////////////////////////////////////////////////////////////////////// CALCULATE METHODS 
    /**
     * calculates the accumulated steering force according to the method set in
     * m_SummingMethod
     *
     * @return
     */
    public Vector2 calculate() {
        //reset the steering force
        m_vSteeringForce.setZero();

        //use space partitioning to calculate the neighbours of this vehicle
        //if switched on. If not, use the standard tagging system
        if (!isSpacePartitioningOn()) {
            //tag neighbors if any of the following 3 group behaviors are switched on
            if (On(behavior_type.separation) || On(behavior_type.allignment) || On(behavior_type.cohesion)) {
                m_pVehicle.getWorld().TagVehiclesWithinViewRange(m_pVehicle, m_dViewDistance);
            }
        } else {
            //calculate neighbours in cell-space if any of the following 3 group
            //behaviors are switched on
            if (On(behavior_type.separation) || On(behavior_type.allignment) || On(behavior_type.cohesion)) {
                // todo m_pVehicle.getWorld().CellSpace().CalculateNeighbors(m_pVehicle.getPosition(), m_dViewDistance);
            }
        }

        switch (m_SummingMethod) {

            case weighted_average:

                m_vSteeringForce = CalculateWeightedSum();
                break;

            case prioritized:

                m_vSteeringForce = CalculatePrioritized();
                break;

            case dithered:

                m_vSteeringForce = CalculateDithered();
                break;

            default:
                m_vSteeringForce = new Vector2(0, 0);

        }//end switch

        return m_vSteeringForce;
    }

    /**
     * returns the forward component of the steering force
     *
     * @return
     */
    public float ForwardComponent() {
        return m_pVehicle.getHeading().dot(m_vSteeringForce);
    }

    /**
     * returns the side component of the steering for
     *
     * @return ce
     */
    public float SideComponent() {
        return m_pVehicle.getSide().dot(m_vSteeringForce);
    }

    /**
     * this method calls each active steering behavior in order of priority and
     * acumulates their forces until the max steering force magnitude is
     * reached, at which time the function returns the steering force
     * accumulated to that point
     */
    private Vector2 CalculatePrioritized() {
        Vector2 force = new Vector2();

        if (On(behavior_type.wall_avoidance)) {
            // todo force = WallAvoidance(m_pVehicle.getWorld().Walls()).scl(m_dWeightWallAvoidance);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.obstacle_avoidance)) {
            force = ObstacleAvoidance(m_pVehicle.getWorld().Obstacles()).scl(m_dWeightObstacleAvoidance);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.evade)) {
            assert m_pTargetAgent1 != null : "Evade target not assigned";

            force = Evade(m_pTargetAgent1).scl(m_dWeightEvade);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.flee)) {
            force = Flee(getTarget().getOrigin()).scl(m_dWeightFlee);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        //these next three can be combined for flocking behavior (wander is
        //also a good behavior to add into this mix)
        if (!isSpacePartitioningOn()) {
            if (On(behavior_type.separation)) {
                force = Separation(m_pVehicle.getWorld().getAgents()).scl(m_dWeightSeparation);

                if (!AccumulateForce(m_vSteeringForce, force)) {
                    return m_vSteeringForce;
                }
            }

            if (On(behavior_type.allignment)) {
                force = Alignment(m_pVehicle.getWorld().getAgents()).scl(m_dWeightAlignment);

                if (!AccumulateForce(m_vSteeringForce, force)) {
                    return m_vSteeringForce;
                }
            }

            if (On(behavior_type.cohesion)) {
                force = Cohesion(m_pVehicle.getWorld().getAgents()).scl(m_dWeightCohesion);

                if (!AccumulateForce(m_vSteeringForce, force)) {
                    return m_vSteeringForce;
                }
            }
        } else {

            if (On(behavior_type.separation)) {
                // todo force = SeparationPlus(m_pVehicle.getWorld().Agents()).scl(m_dWeightSeparation);

                if (!AccumulateForce(m_vSteeringForce, force)) {
                    return m_vSteeringForce;
                }
            }

            if (On(behavior_type.allignment)) {
                // todo force = AlignmentPlus(m_pVehicle.getWorld().Agents()).scl(m_dWeightAlignment);

                if (!AccumulateForce(m_vSteeringForce, force)) {
                    return m_vSteeringForce;
                }
            }

            if (On(behavior_type.cohesion)) {
                // todo force = CohesionPlus(m_pVehicle.getWorld().Agents()).scl(m_dWeightCohesion);

                if (!AccumulateForce(m_vSteeringForce, force)) {
                    return m_vSteeringForce;
                }
            }
        }

        if (On(behavior_type.seek)) {
            force = Seek(getTarget().getOrigin()).scl(m_dWeightSeek);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.arrive)) {
            force = Arrive(getTarget().getOrigin(), m_Deceleration).scl(m_dWeightArrive);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.wander)) {
            force = Wander().scl(m_dWeightWander);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.pursuit)) {
            assert m_pTargetAgent1 != null : "pursuit target not assigned";

            force = Pursuit(m_pTargetAgent1).scl(m_dWeightPursuit);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.offset_pursuit)) {
            assert m_pTargetAgent1 != null : "pursuit target not assigned";
            assert !m_vOffset.isZero() : "No offset assigned";

            force = OffsetPursuit(m_pTargetAgent1, m_vOffset);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.interpose)) {
            assert m_pTargetAgent1 != null && m_pTargetAgent2 != null : "Interpose agents not assigned";

            force = Interpose(m_pTargetAgent1, m_pTargetAgent2).scl(m_dWeightInterpose);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.hide)) {
            assert m_pTargetAgent1 != null : "Hide target not assigned";

            force = Hide(m_pTargetAgent1, m_pVehicle.getWorld().Obstacles()).scl(m_dWeightHide);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.follow_path)) {
            force = FollowPath().scl(m_dWeightFollowPath);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        return m_vSteeringForce;
    }

    /**
     * this simply sums up all the active behaviors X their weights and
     * truncates the result to the max available steering force before returning
     */
    private Vector2 CalculateWeightedSum() {
        if (On(behavior_type.wall_avoidance)) {
            // todo m_vSteeringForce.add(WallAvoidance(m_pVehicle.getWorld().Walls()).scl(m_dWeightWallAvoidance));
        }

        if (On(behavior_type.obstacle_avoidance)) {
            m_vSteeringForce.add(ObstacleAvoidance(m_pVehicle.getWorld().Obstacles()).scl(m_dWeightObstacleAvoidance));
        }

        if (On(behavior_type.evade)) {
            assert m_pTargetAgent1 != null : "Evade target not assigned";

            m_vSteeringForce.add(Evade(m_pTargetAgent1).scl(m_dWeightEvade));
        }

        //these next three can be combined for flocking behavior (wander is
        //also a good behavior to add into this mix)
        if (!isSpacePartitioningOn()) {
            if (On(behavior_type.separation)) {
                m_vSteeringForce.add(Separation(m_pVehicle.getWorld().getAgents()).scl(m_dWeightSeparation));
            }

            if (On(behavior_type.allignment)) {
                m_vSteeringForce.add(Alignment(m_pVehicle.getWorld().getAgents()).scl(m_dWeightAlignment));
            }

            if (On(behavior_type.cohesion)) {
                m_vSteeringForce.add(Cohesion(m_pVehicle.getWorld().getAgents()).scl(m_dWeightCohesion));
            }
        } else {
            if (On(behavior_type.separation)) {
                // todo m_vSteeringForce.add(SeparationPlus(m_pVehicle.getWorld().Agents()).scl(m_dWeightSeparation));
            }

            if (On(behavior_type.allignment)) {
                // todo m_vSteeringForce.add(AlignmentPlus(m_pVehicle.getWorld().Agents()).scl(m_dWeightAlignment));
            }

            if (On(behavior_type.cohesion)) {
                // todo m_vSteeringForce.add(CohesionPlus(m_pVehicle.getWorld().Agents()).scl(m_dWeightCohesion));
            }
        }

        if (On(behavior_type.wander)) {
            m_vSteeringForce.add(Wander().scl(m_dWeightWander));
        }

        if (On(behavior_type.seek)) {
            m_vSteeringForce.add(Seek(target.getOrigin()).scl(m_dWeightSeek));
        }

        if (On(behavior_type.flee)) {
            m_vSteeringForce.add(Flee(target.getOrigin()).scl(m_dWeightFlee));
        }

        if (On(behavior_type.arrive)) {
            m_vSteeringForce.add(Arrive(target.getOrigin(), m_Deceleration).scl(m_dWeightArrive));
        }

        if (On(behavior_type.pursuit)) {
            assert m_pTargetAgent1 != null : "pursuit target not assigned";

            m_vSteeringForce.add(Pursuit(m_pTargetAgent1).scl(m_dWeightPursuit));
        }

        if (On(behavior_type.offset_pursuit)) {
            assert m_pTargetAgent1 != null : "pursuit target not assigned";
            assert !m_vOffset.isZero() : "No offset assigned";

            m_vSteeringForce.add(OffsetPursuit(m_pTargetAgent1, m_vOffset).scl(m_dWeightOffsetPursuit));
        }

        if (On(behavior_type.interpose)) {
            assert m_pTargetAgent1 != null && m_pTargetAgent2 != null : "Interpose agents not assigned";

            m_vSteeringForce.add(Interpose(m_pTargetAgent1, m_pTargetAgent2).scl(m_dWeightInterpose));
        }

        if (On(behavior_type.hide)) {
            assert m_pTargetAgent1 != null : "Hide target not assigned";

            m_vSteeringForce.add(Hide(m_pTargetAgent1, m_pVehicle.getWorld().Obstacles()).scl(m_dWeightHide));
        }

        if (On(behavior_type.follow_path)) {
            m_vSteeringForce.add(FollowPath().scl(m_dWeightFollowPath));
        }

        m_vSteeringForce.limit(m_pVehicle.getMaxForce());

        return m_vSteeringForce;
    }

    /**
     * this method sums up the active behaviors by assigning a probabilty of
     * being calculated to each behavior. It then tests the first priority to
     * see if it should be calcukated this simulation-step. If so, it calculates
     * the steering force resulting from this behavior. If it is more than zero
     * it returns the force. If zero, or if the behavior is skipped it continues
     * onto the next priority, and so on.
     *
     * NOTE: Not all of the behaviors have been implemented in this method, just
     * a few, so you get the general idea
     */
    private Vector2 CalculateDithered() {
        //reset the steering force
        m_vSteeringForce.setZero();

        if (On(behavior_type.wall_avoidance) && MathUtils.random(1) < prWallAvoidance) {
            //todo m_vSteeringForce = WallAvoidance(m_pVehicle.getWorld().Walls()).scl(m_dWeightWallAvoidance / prWallAvoidance);

            if (!m_vSteeringForce.isZero()) {
                m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.obstacle_avoidance) && MathUtils.random(1) < prObstacleAvoidance) {
            m_vSteeringForce.add(ObstacleAvoidance(m_pVehicle.getWorld().Obstacles()).scl(m_dWeightObstacleAvoidance / prObstacleAvoidance));

            if (!m_vSteeringForce.isZero()) {
                m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                return m_vSteeringForce;
            }
        }

        if (!isSpacePartitioningOn()) {
            if (On(behavior_type.separation) && MathUtils.random(1) < prSeparation) {
                m_vSteeringForce.add(Separation(m_pVehicle.getWorld().getAgents()).scl(m_dWeightSeparation / prSeparation));

                if (!m_vSteeringForce.isZero()) {
                    m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                    return m_vSteeringForce;
                }
            }
        } else {
            if (On(behavior_type.separation) && MathUtils.random(1) < prSeparation) {
                //todo m_vSteeringForce.add(SeparationPlus(m_pVehicle.getWorld().Agents()).scl(m_dWeightSeparation / prSeparation));

                if (!m_vSteeringForce.isZero()) {
                    m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                    return m_vSteeringForce;
                }
            }
        }

        if (On(behavior_type.flee) && MathUtils.random(1) < prFlee) {
            m_vSteeringForce.add(Flee(target.getOrigin()).scl(m_dWeightFlee / prFlee));

            if (!m_vSteeringForce.isZero()) {
                m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.evade) && MathUtils.random(1) < prEvade) {
            assert m_pTargetAgent1 != null : "Evade target not assigned";

            m_vSteeringForce.add(Evade(m_pTargetAgent1).scl(m_dWeightEvade / prEvade));

            if (!m_vSteeringForce.isZero()) {
                m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                return m_vSteeringForce;
            }
        }

        if (!isSpacePartitioningOn()) {
            if (On(behavior_type.allignment) && MathUtils.random(1) < prAlignment) {
                m_vSteeringForce.add(Alignment(m_pVehicle.getWorld().getAgents()).scl(
                        m_dWeightAlignment / prAlignment));

                if (!m_vSteeringForce.isZero()) {
                    m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                    return m_vSteeringForce;
                }
            }

            if (On(behavior_type.cohesion) && MathUtils.random(1) < prCohesion) {
                m_vSteeringForce.add(Cohesion(m_pVehicle.getWorld().getAgents()).scl(
                        m_dWeightCohesion / prCohesion));

                if (!m_vSteeringForce.isZero()) {
                    m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                    return m_vSteeringForce;
                }
            }
        } else {
            if (On(behavior_type.allignment) && MathUtils.random(1) < prAlignment) {
                //todo m_vSteeringForce.add(AlignmentPlus(m_pVehicle.getWorld().Agents()).scl(m_dWeightAlignment / prAlignment));

                if (!m_vSteeringForce.isZero()) {
                    m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                    return m_vSteeringForce;
                }
            }

            if (On(behavior_type.cohesion) && MathUtils.random(1) < prCohesion) {
                //todo m_vSteeringForce.add(CohesionPlus(m_pVehicle.getWorld().Agents()).scl(m_dWeightCohesion / prCohesion));

                if (!m_vSteeringForce.isZero()) {
                    m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                    return m_vSteeringForce;
                }
            }
        }

        if (On(behavior_type.wander) && MathUtils.random(1) < prWander) {
            m_vSteeringForce.add(Wander().scl(m_dWeightWander / prWander));

            if (!m_vSteeringForce.isZero()) {
                m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.seek) && MathUtils.random(1) < prSeek) {
            m_vSteeringForce.add(Seek(target.getOrigin()).scl(m_dWeightSeek / prSeek));

            if (!m_vSteeringForce.isZero()) {
                m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                return m_vSteeringForce;
            }
        }

        if (On(behavior_type.arrive) && MathUtils.random(1) < prArrive) {
            m_vSteeringForce.add(Arrive(target.getOrigin(), m_Deceleration).scl(m_dWeightArrive / prArrive));

            if (!m_vSteeringForce.isZero()) {
                m_vSteeringForce.limit(m_pVehicle.getMaxForce());

                return m_vSteeringForce;
            }
        }

        return m_vSteeringForce;
    }

    public void SetTargetAgent1(Vehicle Agent) {
        m_pTargetAgent1 = Agent;
    }

    public void SetTargetAgent2(Vehicle Agent) {
        m_pTargetAgent2 = Agent;
    }

    public void SetOffset(final Vector2 offset) {
        m_vOffset = offset;
    }

    public Vector2 GetOffset() {
        return m_vOffset;
    }

    public void SetPath(List<Vector2> new_path) {
        m_pPath.Set(new_path);
    }

    public void CreateRandomPath(int num_waypoints, int mx, int my, int cx, int cy) {
        m_pPath.CreateRandomPath(num_waypoints, mx, my, cx, cy);
    }

    public Vector2 Force() {
        return m_vSteeringForce;
    }

    public void ToggleSpacePartitioningOnOff() {
        m_bCellSpaceOn = !m_bCellSpaceOn;
    }

    public boolean isSpacePartitioningOn() {
        return m_bCellSpaceOn;
    }

    public void SetSummingMethod(summing_method sm) {
        m_SummingMethod = sm;
    }

    public void FleeOn() {
        m_iFlags |= behavior_type.flee.flag();
    }

    public void SeekOn() {
        m_iFlags |= behavior_type.seek.flag();
    }

    public void ArriveOn() {
        m_iFlags |= behavior_type.arrive.flag();
    }

    public void WanderOn() {
        m_iFlags |= behavior_type.wander.flag();
    }

    public void PursuitOn(Vehicle v) {
        m_iFlags |= behavior_type.pursuit.flag();
        m_pTargetAgent1 = v;
    }

    public void EvadeOn(Vehicle v) {
        m_iFlags |= behavior_type.evade.flag();
        m_pTargetAgent1 = v;
    }

    public void CohesionOn() {
        m_iFlags |= behavior_type.cohesion.flag();
    }

    public void SeparationOn() {
        m_iFlags |= behavior_type.separation.flag();
    }

    public void AlignmentOn() {
        m_iFlags |= behavior_type.allignment.flag();
    }

    public void ObstacleAvoidanceOn() {
        m_iFlags |= behavior_type.obstacle_avoidance.flag();
    }

    public void WallAvoidanceOn() {
        m_iFlags |= behavior_type.wall_avoidance.flag();
    }

    public void FollowPathOn() {
        m_iFlags |= behavior_type.follow_path.flag();
    }

    public void InterposeOn(Vehicle v1, Vehicle v2) {
        m_iFlags |= behavior_type.interpose.flag();
        m_pTargetAgent1 = v1;
        m_pTargetAgent2 = v2;
    }

    public void HideOn(Vehicle v) {
        m_iFlags |= behavior_type.hide.flag();
        m_pTargetAgent1 = v;
    }

    public void OffsetPursuitOn(Vehicle v1, final Vector2 offset) {
        m_iFlags |= behavior_type.offset_pursuit.flag();
        m_vOffset = offset;
        m_pTargetAgent1 = v1;
    }

    public void FlockingOn() {
        CohesionOn();
        AlignmentOn();
        SeparationOn();
        WanderOn();
    }

    public void FleeOff() {
        if (On(behavior_type.flee)) {
            m_iFlags ^= behavior_type.flee.flag();
        }
    }

    public void SeekOff() {
        if (On(behavior_type.seek)) {
            m_iFlags ^= behavior_type.seek.flag();
        }
    }

    public void ArriveOff() {
        if (On(behavior_type.arrive)) {
            m_iFlags ^= behavior_type.arrive.flag();
        }
    }

    public void WanderOff() {
        if (On(behavior_type.wander)) {
            m_iFlags ^= behavior_type.wander.flag();
        }
    }

    public void PursuitOff() {
        if (On(behavior_type.pursuit)) {
            m_iFlags ^= behavior_type.pursuit.flag();
        }
    }

    public void EvadeOff() {
        if (On(behavior_type.evade)) {
            m_iFlags ^= behavior_type.evade.flag();
        }
    }

    public void CohesionOff() {
        if (On(behavior_type.cohesion)) {
            m_iFlags ^= behavior_type.cohesion.flag();
        }
    }

    public void SeparationOff() {
        if (On(behavior_type.separation)) {
            m_iFlags ^= behavior_type.separation.flag();
        }
    }

    public void AlignmentOff() {
        if (On(behavior_type.allignment)) {
            m_iFlags ^= behavior_type.allignment.flag();
        }
    }

    public void ObstacleAvoidanceOff() {
        if (On(behavior_type.obstacle_avoidance)) {
            m_iFlags ^= behavior_type.obstacle_avoidance.flag();
        }
    }

    public void WallAvoidanceOff() {
        if (On(behavior_type.wall_avoidance)) {
            m_iFlags ^= behavior_type.wall_avoidance.flag();
        }
    }

    public void FollowPathOff() {
        if (On(behavior_type.follow_path)) {
            m_iFlags ^= behavior_type.follow_path.flag();
        }
    }

    public void InterposeOff() {
        if (On(behavior_type.interpose)) {
            m_iFlags ^= behavior_type.interpose.flag();
        }
    }

    public void HideOff() {
        if (On(behavior_type.hide)) {
            m_iFlags ^= behavior_type.hide.flag();
        }
    }

    public void OffsetPursuitOff() {
        if (On(behavior_type.offset_pursuit)) {
            m_iFlags ^= behavior_type.offset_pursuit.flag();
        }
    }

    public void FlockingOff() {
        CohesionOff();
        AlignmentOff();
        SeparationOff();
        WanderOff();
    }

    public boolean isFleeOn() {
        return On(behavior_type.flee);
    }

    public boolean isSeekOn() {
        return On(behavior_type.seek);
    }

    public boolean isArriveOn() {
        return On(behavior_type.arrive);
    }

    public boolean isWanderOn() {
        return On(behavior_type.wander);
    }

    public boolean isPursuitOn() {
        return On(behavior_type.pursuit);
    }

    public boolean isEvadeOn() {
        return On(behavior_type.evade);
    }

    public boolean isCohesionOn() {
        return On(behavior_type.cohesion);
    }

    public boolean isSeparationOn() {
        return On(behavior_type.separation);
    }

    public boolean isAlignmentOn() {
        return On(behavior_type.allignment);
    }

    public boolean isObstacleAvoidanceOn() {
        return On(behavior_type.obstacle_avoidance);
    }

    public boolean isWallAvoidanceOn() {
        return On(behavior_type.wall_avoidance);
    }

    public boolean isFollowPathOn() {
        return On(behavior_type.follow_path);
    }

    public boolean isInterposeOn() {
        return On(behavior_type.interpose);
    }

    public boolean isHideOn() {
        return On(behavior_type.hide);
    }

    public boolean isOffsetPursuitOn() {
        return On(behavior_type.offset_pursuit);
    }

    public float DBoxLength() {
        return m_dDBoxLength;
    }

    public List<Vector2> GetFeelers() {
        return m_Feelers;
    }

    public float WanderJitter() {
        return m_dWanderJitter;
    }

    public float WanderDistance() {
        return m_dWanderDistance;
    }

    public float WanderRadius() {
        return m_dWanderRadius;
    }

    public float SeparationWeight() {
        return m_dWeightSeparation;
    }

    public float AlignmentWeight() {
        return m_dWeightAlignment;
    }

    public float CohesionWeight() {
        return m_dWeightCohesion;
    }

    public ITargetable getTarget() {
        return target;
    }

    public void setTarget(ITargetable target) {
        this.target = target;
    }

}
