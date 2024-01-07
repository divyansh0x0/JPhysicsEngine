package physicsemulator.engine.collider;

import material.utils.Log;
import physicsemulator.engine.EngineProperties;
import physicsemulator.engine.RigidBodyModel;
import physicsemulator.physicalbody.rigidbody.Circle2D;
import physicsemulator.physicalbody.rigidbody.Polygon2D;
import physicsemulator.utils.LineSegment2D;
import physicsemulator.utils.PhysicsUtils;
import physicsemulator.utils.Vector2D;

public class Collider2D {
    //Uses separating axis theorem for intersection
    public static void handleCollision(Polygon2D polyA, Polygon2D polyB) {
        Vector2D[] verticesA = polyA.getVertices();
        Vector2D[] verticesB = polyB.getVertices();
        double collisionDepth = Float.MAX_VALUE;
        //For A
        Vector2D normal = Vector2D.ZERO;
        for (int i = 0; i < verticesA.length; i++) {
            Vector2D vertex1 = verticesA[i];
            Vector2D vertex2 = verticesA[(i + 1) % verticesA.length];
            Vector2D axis = getNormalOutward(vertex1, vertex2); //no need to normalize

            double[] minMaxA = getProjectionsOnAxis(verticesA, axis);//min and max pair of A
            double[] minMaxB = getProjectionsOnAxis(verticesB, axis);//min and max pair of B
            if (minMaxA[0] >= minMaxB[1] || minMaxB[0] >= minMaxA[1])
                return;
            double axisDepth = Math.min(minMaxB[1] - minMaxA[0], minMaxA[1] - minMaxB[0]);
            if (axisDepth < collisionDepth) {
                collisionDepth = axisDepth;
                normal = axis;
            }
        }
        //For B
        for (int i = 0; i < verticesB.length; i++) {
            Vector2D vertex1 = verticesB[i];
            Vector2D vertex2 = verticesB[(i + 1) % verticesB.length];
            Vector2D axis = getNormalOutward(vertex1, vertex2);//no need to normalize

            double[] minMaxA = getProjectionsOnAxis(verticesA, axis);//min and max pair of A
            double[] minMaxB = getProjectionsOnAxis(verticesB, axis);//min and max pair of B

            if (minMaxA[0] >= minMaxB[1] || minMaxB[0] >= minMaxA[1])
                return;
            double axisDepth = Math.min(minMaxB[1] - minMaxA[0], minMaxA[1] - minMaxB[0]);
            if (axisDepth < collisionDepth) {
                collisionDepth = axisDepth;
                normal = axis;
            }
        }
        Vector2D directionVec = polyB.getCenter().subtract(polyA.getCenter());
        if (directionVec.dotProduct(normal) < 0) {
            normal = normal.multiply(-1);
        }
        handleCollisionResponse(polyA, polyB, collisionDepth, normal);
    }

    //TODO fix corner collision
    public static void handleCollision(Polygon2D poly, Circle2D circle) {
        Vector2D[] verticesA = poly.getVertices();
        double collisionDepth = Float.MAX_VALUE;
        //For A
        Vector2D normal = Vector2D.ZERO;
        for (int i = 0; i < verticesA.length; i++) {
            Vector2D vertex1 = verticesA[i];
            Vector2D vertex2 = verticesA[(i + 1) % verticesA.length];
            Vector2D axis = getNormalOutward(vertex1, vertex2);
            Vector2D[] verticesB = getCircleVerticesInDirection(circle, axis);

            double[] minMaxA = getProjectionsOnAxis(verticesA, axis);//min and max pair of A
            double[] minMaxB = getProjectionsOnAxis(verticesB, axis);//min and max pair of B
            if (minMaxA[0] >= minMaxB[1] || minMaxB[0] >= minMaxA[1])
                return;
            double axisDepth = Math.min(minMaxB[1] - minMaxA[0], minMaxA[1] - minMaxB[0]);
            if (axisDepth < collisionDepth) {
                collisionDepth = axisDepth;
                normal = axis;
            }
        }

        //We do the above for the vector from center of circle closest to a point on polygon

        Vector2D directionVec = circle.getCenter().subtract(getClosestPointOnPolygon(circle, poly)).normalize();
        Vector2D[] verticesB = getCircleVerticesInDirection(circle, directionVec);

        double[] minMaxA = getProjectionsOnAxis(verticesA, directionVec);//min and max pair of A
        double[] minMaxB = getProjectionsOnAxis(verticesB, directionVec);//min and max pair of B
        if (minMaxA[0] >= minMaxB[1] || minMaxB[0] >= minMaxA[1])
            return;

        double axisDepth = Math.min(minMaxB[1] - minMaxA[0], minMaxA[1] - minMaxB[0]);
        if (axisDepth < collisionDepth) {
            normal = directionVec;
            collisionDepth = axisDepth;
        }
        //TODO fix the glitch here
        if (directionVec.dotProduct(normal) < 0) {
            normal = normal.multiply(-1);
        }
        handleCollisionResponse(poly, circle, collisionDepth, normal);
    }

    public static void handleCollision(Circle2D obj1, Circle2D obj2) {
        Vector2D axis = obj2.getPosition().subtract(obj1.getPosition());
        double depth = axis.getMagnitude() - (obj1.getRadius() + obj2.getRadius());
        //if distance is less than sum of radii then collision must have happened
        if (depth < 0) {
            depth *= -1;
            handleCollisionResponse(obj1, obj2, depth, axis.normalize());
        }
    }


    //TODO fix clipping
    private static void handleCollisionResponse(RigidBodyModel obj1, RigidBodyModel obj2, double collisionDepthObj2, Vector2D collisionNormalObj2) {
        if (!PhysicsUtils.areEqual(0, collisionDepthObj2))
            handleCollisionResponseToResolver(obj1, obj2, Math.abs(collisionDepthObj2), collisionNormalObj2);
    }

    private static void handleCollisionResponseToResolver(RigidBodyModel obj1, RigidBodyModel obj2, double collisionDepthObj2, Vector2D collisionNormalObj2) {
        if (collisionDepthObj2 == 0)
            return;
        collisionDepthObj2 = Math.abs(collisionDepthObj2);
//        handleCollisionUsingImpulse(obj1,obj2,collisionDepthObj2,collisionNormalObj2);
        Vector2D collisionNormalObj1 = collisionNormalObj2.multiply(-1);
        double collisionDepthObj1 = collisionDepthObj2;
        if (!obj1.isStatic() && !obj2.isStatic()) {
            collisionDepthObj1 *= 0.5f;
            collisionDepthObj2 *= 0.5f;
        }
        double restitution = EngineProperties.getInstance().getRestitutionCoefficient();//coefficient of restitution between bodies


//        if you don't know whats going on here, see formula of impulseAlongCollision in 1d in theory directory
//        double relVelocity = Math.abs(obj1.getLinearVelocity().dotProduct(collisionNormalObj1) - (obj2.getLinearVelocity().dotProduct(collisionNormalObj2)));
        double uA = obj1.getCurrentVelocity().dotProduct(collisionNormalObj2);
        double uB = obj2.getCurrentVelocity().dotProduct(collisionNormalObj1);
        if (uA > 0 || uB > 0) {//checks if it is possible to impart impulse on either one of the objects. This prevents a bug. DO NOT REMOVE
            Vector2D relativeVel = obj1.getCurrentVelocity().subtract(obj2.getCurrentVelocity());
            double relativeVelAlongNormal = Math.abs(relativeVel.dotProduct(collisionNormalObj1));
            double impulse = Math.abs(relativeVelAlongNormal * ((1f + restitution) / (obj1.getInvMass() + obj2.getInvMass())));
            Vector2D impulseOnObj1 = collisionNormalObj1.multiply(impulse);
            Vector2D impulseOnObj2 = collisionNormalObj2.multiply(impulse);

            if (!obj1.isStatic() && !obj2.isStatic()) {
                CollisionResolver.saveDynamicCollision(obj1, collisionDepthObj1, impulseOnObj1, collisionNormalObj1);
                CollisionResolver.saveDynamicCollision(obj2, collisionDepthObj2, impulseOnObj2, collisionNormalObj2);
            } else {
                if (obj1.isStatic())//if obj1 is static
                    CollisionResolver.saveStaticCollision(obj2, collisionDepthObj2, impulseOnObj2, collisionNormalObj2);
                else//if obj2 is static
                    CollisionResolver.saveStaticCollision(obj1, collisionDepthObj1, impulseOnObj1, collisionNormalObj1);
//
            }
        }
    }

    private static void handleCollisionUsingImpulse(RigidBodyModel obj1, RigidBodyModel obj2, double depth, Vector2D collisionNormalObj2) {
        Vector2D collisionNormalObj1 = collisionNormalObj2.multiply(-1);
        if (!obj1.isStatic() && !obj2.isStatic()) {
            obj1.addDisplacement(collisionNormalObj1.multiply(depth * 0.5f));
            obj2.addDisplacement(collisionNormalObj2.multiply(depth * 0.5f));
        } else {//One of the object is static
            if (obj1.isStatic()) {
                obj2.addDisplacement(collisionNormalObj2.multiply((int) (depth)));
            } else {
                obj1.addDisplacement(collisionNormalObj1.multiply((int) (depth)));
            }
        }
        double restitution = EngineProperties.getInstance().getRestitutionCoefficient();//coefficient of restitution between bodies


        double uA = obj1.getCurrentVelocity().dotProduct(collisionNormalObj2);
        double uB = obj2.getCurrentVelocity().dotProduct(collisionNormalObj1);
        Vector2D relativeVel = obj1.getCurrentVelocity().subtract(obj2.getCurrentVelocity());
        double relativeVelAlongNormal = Math.abs(relativeVel.dotProduct(collisionNormalObj1));
        if (uA > 0 || uB > 0) {//checks if it is possible to impart impulse on either one of the objects. This prevents a bug DO NOT REMOVE
            double impulse = Math.abs(relativeVelAlongNormal * ((1f + restitution) / (obj1.getInvMass() + obj2.getInvMass())));
            Vector2D impulseOnObj1 = collisionNormalObj1.multiply(impulse);
            Vector2D impulseOnObj2 = collisionNormalObj2.multiply(impulse);
            obj2.applyImpulse(impulseOnObj2);
            obj1.applyImpulse(impulseOnObj1);

        }

    }

    private static Vector2D getClosestPointOnPolygon(Circle2D circle2D, Polygon2D poly) {
        double min = Float.MAX_VALUE;
        Vector2D closestPoint = Vector2D.ZERO;
        Vector2D[] vertices = poly.getVertices();
        for (int i = 0, verticesLength = vertices.length; i < verticesLength; i++) {
            Vector2D p = vertices[i];
            double distSq = circle2D.getCenter().subtract(p).getMagnitudeSquared();
            if (distSq < min) {
                min = distSq;
                closestPoint = p;
            }
        }
        return closestPoint;
    }

    private static Vector2D[] getCircleVerticesInDirection(Circle2D circle, Vector2D normalize) {
        Vector2D p1 = normalize.multiply(circle.getRadius()).add(circle.getCenter());
        Vector2D p2 = normalize.multiply(-circle.getRadius()).add(circle.getCenter());
        return new Vector2D[]{p1, p2};
    }

    //A min and max pair will be returned
    private static double[] getProjectionsOnAxis(Vector2D[] vertices, Vector2D axis) {
        double min = Float.MAX_VALUE;
        double max = Float.MIN_VALUE;
        for (int i = 0; i < vertices.length; i++) {
            double proj = vertices[i].dotProduct(axis);
            if (proj < min) {
                min = proj;
            }
            if (proj > max) {
                max = proj;
            }
        }
        return new double[]{min, max};
    }

    private static Vector2D getNormalOutward(Vector2D start, Vector2D end) {
        return end.subtract(start).rotateBy(90, Vector2D.ZERO).normalize();
    }

    private static Vector2D getNormalInward(Vector2D start, Vector2D end) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        return new Vector2D(dy, -dx);
    }

    public static Vector2D[] getNormalsOfLineSegment(LineSegment2D line) {
        double dx = line.getEnd().getX() - line.getStart().getX();
        double dy = line.getEnd().getY() - line.getStart().getY();
        return new Vector2D[]{new Vector2D(-dy, dx), new Vector2D(dy, -dx)};
    }

    /**
     * @return a unit vector pointing to normal. A null vector is returned if normal does not exist
     */

    public static Vector2D getNormalVectorTowardsOrigin(LineSegment2D line) {
        return getNormalVectorTowardsOrigin(line.getStart(), line.getEnd());
    }

    /**
     * @return a unit vector pointing to normal. A null vector is returned if normal does not exist
     */
    public static Vector2D getNormalVectorTowardsOrigin(Vector2D startVec, Vector2D endVec) {
        //Formula = (dist x start) x dist

        Vector2D distVec = startVec.subtract(endVec);
        double[] dist = {distVec.getX(), distVec.getY(), 0};
        double[] start = {startVec.getX(), startVec.getY(), 0};
        double[] result = getCrossProduct(dist, getCrossProduct(dist, start));
        return new Vector2D(result[0], result[1]).normalize();
    }

    public static double[] getCrossProduct(double[] a, double[] b) {
        double x = a[1] * b[2] - b[1] * a[2];
        double y = a[0] * b[2] - b[0] * a[2];
        double z = a[0] * b[1] - b[0] * a[1];
        return new double[]{x, -y, z};
    }


}

