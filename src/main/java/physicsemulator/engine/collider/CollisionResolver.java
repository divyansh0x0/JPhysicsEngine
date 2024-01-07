package physicsemulator.engine.collider;

import material.utils.Log;
import physicsemulator.engine.RigidBodyModel;
import physicsemulator.utils.Vector2D;

import java.util.concurrent.ConcurrentHashMap;

public class CollisionResolver {
    //This map stores rigid body and its collision impulse
    private static final ConcurrentHashMap<RigidBodyModel, CollisionDetails> dynamicCollisionDetails = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<RigidBodyModel, CollisionDetails> staticCollisionDetails = new ConcurrentHashMap<>();

    public static void saveDynamicCollision(RigidBodyModel dynamicBody, double collisionDepthObj1, Vector2D impulseOnObj1, Vector2D collisionNormal) {
        if (!dynamicBody.isStatic()) {
            if (dynamicCollisionDetails.containsKey(dynamicBody)) {
                CollisionDetails r = dynamicCollisionDetails.get(dynamicBody);
                r.handleDepth(collisionDepthObj1);
                r.handleImpulse(impulseOnObj1);
                r.handleCollisionNormal(collisionNormal);
//                Log.formattedSuccess("depth: %.2f | impulse %s | normal:  %s", collisionDepthObj1, impulseOnObj1.toString(),collisionNormal.toString());

            } else {
                dynamicCollisionDetails.put(dynamicBody, new CollisionDetails(collisionDepthObj1, impulseOnObj1, collisionNormal));
            }
        }
    }

    public static void saveStaticCollision(RigidBodyModel dynamicBody, double depth, Vector2D impulseOnDynamicBody, Vector2D collisionNormal) {
        if (staticCollisionDetails.containsKey(dynamicBody)) {
            CollisionDetails r = staticCollisionDetails.get(dynamicBody);
            r.handleDepth(depth);
            r.handleImpulse(impulseOnDynamicBody);
            r.handleCollisionNormal(collisionNormal);
//                Log.formattedSuccess("depth: %.2f | impulse %s | normal:  %s", collisionDepthObj1, impulseOnObj1.toString(),collisionNormal.toString());

        } else {
            staticCollisionDetails.put(dynamicBody, new CollisionDetails(depth, impulseOnDynamicBody, collisionNormal));
        }
    }

    public static void resolveCollision(RigidBodyModel obj) {
        CollisionDetails dynamicCollisionDetails = CollisionResolver.dynamicCollisionDetails.get(obj);
        CollisionDetails staticCollisionDetails = CollisionResolver.staticCollisionDetails.get(obj);
        Vector2D impulse = Vector2D.ZERO;
        Vector2D displ = Vector2D.ZERO;
        Vector2D normal_reaction = Vector2D.ZERO;
        if (dynamicCollisionDetails != null) {
            //Dynamic collision
            displ = dynamicCollisionDetails.getCollisionNormal().multiply(dynamicCollisionDetails.getDepth());
            impulse = dynamicCollisionDetails.getImpulse();
            dynamicCollisionDetails.reset();
            normal_reaction = dynamicCollisionDetails.getCollisionNormal().multiply(-dynamicCollisionDetails.getCollisionNormal().dotProduct(obj.getForce()));
        }
//        Resolve collision with static body
        if (staticCollisionDetails != null) {
            normal_reaction = normal_reaction.add(staticCollisionDetails.getCollisionNormal().multiply(-staticCollisionDetails.getCollisionNormal().dotProduct(obj.getForce())));
            displ = displ.add(staticCollisionDetails.getCollisionNormal().multiply(staticCollisionDetails.getDepth()));

//            Fix if final values are pushing the body inside static body
            if (impulse.dotProduct(staticCollisionDetails.getCollisionNormal()) < 0) {
                impulse.pointTo(-impulse.getX(), -impulse.getY());
            }
                if (displ.dotProduct(staticCollisionDetails.getCollisionNormal()) < 0) {
                displ.pointTo(-displ.getX(), -displ.getY());
            }
//            if (normal_reaction.dotProduct(staticCollisionDetails.getCollisionNormal()) < 0)
//                normal_reaction.pointTo(-normal_reaction.getX(), -normal_reaction.getY());
            impulse = impulse.add(staticCollisionDetails.getImpulse());
            staticCollisionDetails.reset();

        }
        else{
            displ = displ.multiply(0.5f);
        }
        obj.addDisplacement(displ);
        obj.applyImpulse(impulse);
        obj.applyInstantaneousForce(normal_reaction);
    }
}
