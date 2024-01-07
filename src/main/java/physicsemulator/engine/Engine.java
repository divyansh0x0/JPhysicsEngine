package physicsemulator.engine;

import material.utils.Log;
import physicsemulator.engine.collider.Collider2D;
import physicsemulator.engine.collider.CollisionResolver;
import physicsemulator.physicalbody.RigidObject;
import physicsemulator.physicalbody.rigidbody.Circle2D;
import physicsemulator.physicalbody.rigidbody.Rectangle2D;
import physicsemulator.utils.Vector2D;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Time is being stored in nanoseconds
 */
public class Engine {
    private static final Color[] colors = new Color[]{
            new Color(0x60D5B5),
            new Color(0x6969E6),
            new Color(0xAA6DC8),
            new Color(0x66C05A),
            new Color(0xB7A75A),
            new Color(0xB95B5B)
    };
    private static final Random random = new Random();
    private final ArrayList<RigidBodyModel> rigidBodies = new ArrayList<>();
    private final Scene scene;
    private final Thread UpdateThread;
    private final Thread RenderThread;

    private final int DESIRED_FPS = 60;
    private int CURRENT_FPS = 0;
    //    private final long targetFrameTime = 1000_000_000L;
    private final AtomicBoolean isDisposed = new AtomicBoolean(false);

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private double currentStepTime = 0f;
    private short SubSteps = 100;
    private final long PhysicsUpdateDeltaMinNanos = 1_000_000_000/1000000;

    public Engine(Scene scene) {
        this.scene = scene;
        //Physics thread
        UpdateThread = new Thread(() -> {
            long lastUpdateTime = System.nanoTime();
            while (!isDisposed()) {

                while (isRunning()) {
                    long now = System.nanoTime();
                    long deltaT = now - lastUpdateTime;

                    if (deltaT > PhysicsUpdateDeltaMinNanos) {
                        update(deltaT);
                        lastUpdateTime = now;
                    }
                }
                //If the engine is not running then we keep the last frame time equal to current time to avoid
                //large values of delta time
                if (!isRunning()) {
                    lastUpdateTime = System.nanoTime();
                }
            }
            Log.success("Engine disposed successfully");
        });
        UpdateThread.setName("Engine Thread");
        //Rendering thread
        RenderThread = new Thread(() -> {
            long lastPrintTime = System.nanoTime();
            long frameDuration = 1_000_000_000 / DESIRED_FPS; // it is equal to 16.67 ms
            long lastFrameTime = System.nanoTime();
            while (!isDisposed()) {

                    long now = System.nanoTime();
                    long deltaT = now - lastFrameTime;

                    if (deltaT > frameDuration) {
                        render();
                        currentStepTime = (double) (deltaT * 1e-6);
                        lastFrameTime = now;
                    }



                }
                //If the engine is not running then we keep the last frame time equal to current time to avoid
                //large values of delta time
        });
        RenderThread.setName("Rendering Thread");

        UpdateThread.start();
        RenderThread.start();
    }


    private void update(long deltaT) {
        deltaT /= SubSteps;
        double inSeconds = deltaT / 1e9f;
        scene.setPhysicsTimeStep(inSeconds);
        for (int i = SubSteps - 1; i >= 0; i--) {
            checkAndHandleCollisions();
            for (int j = 0; j < rigidBodies.size(); j++) {
                RigidBodyModel obj = rigidBodies.get(j);
                obj.updatePhysics(inSeconds);
            }
        }

    }

    private void render() {
        scene.repaint();

//        for (int i = 0; i < getAllRigidBodies().size(); i++) {
//            RigidBodyModel r = getAllRigidBodies().get(i);
//                momentum = momentum.add(r.getLinearMomentum());
//        }
//        Log.info("Net Momentum: " + momentum);
    }

    private void checkAndHandleCollisions() {
        //Check if any object is colliding
        for (int i = 0; i < rigidBodies.size(); i++) {
            RigidBodyModel obj1 = rigidBodies.get(i);
            for (int j = i + 1; j < rigidBodies.size(); j++) {
                RigidBodyModel obj2 = rigidBodies.get(j);
                if ((obj1.isStatic() && obj2.isStatic()) || obj1.isSelected() || obj2.isSelected())
                    continue;
                if (obj1 instanceof Rectangle2D && obj2 instanceof Rectangle2D) {
                    Collider2D.handleCollision(((Rectangle2D) obj1), ((Rectangle2D) obj2));
                } else if (obj1 instanceof Rectangle2D && obj2 instanceof Circle2D) {
                    Collider2D.handleCollision(((Rectangle2D) obj1), ((Circle2D) obj2));
                } else if (obj2 instanceof Rectangle2D && obj1 instanceof Circle2D) {
                    Collider2D.handleCollision(((Rectangle2D) obj2), ((Circle2D) obj1));
                } else if (obj1 instanceof Circle2D && obj2 instanceof Circle2D) {
                    Collider2D.handleCollision((Circle2D) obj1, (Circle2D) obj2);
                }
            }
        }
        for (int i = 0; i < rigidBodies.size(); i++) {
            RigidBodyModel obj1 = rigidBodies.get(i);
            CollisionResolver.resolveCollision(obj1);
        }
    }


    /**
     * UTILS
     */
    public void dispose() {
        synchronized (this) {
            try {
                Log.warn("Disposing engine...");
                setRunning(false);
                isDisposed.set(true);
                Log.success("Engine disposed successfully");
            } catch (Exception e) {
                Log.error("Error occurred while disposing engine: " + e);
            }
        }
    }

    /**
     * GETTERS
     */

    public int getCurrentFrameRate() {
        return CURRENT_FPS;
    }

    private boolean isDisposed() {
        return isDisposed.get();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public ArrayList<RigidBodyModel> getAllRigidBodies() {
        return rigidBodies;

    }

    public double getCurrentStepTime() {
        return currentStepTime;
    }

    /**
     * SETTERS
     */
    public void registerPhysicsObject(RigidBodyModel obj) {
        if (!rigidBodies.contains(obj)) {
            rigidBodies.add(obj);
            scene.addObject(obj);
        }

    }

    public void addRandomRect(Point scenePoint) {
        int height = random.nextInt(20, 100);
        int width = random.nextInt(20, 100);
        Vector2D pos = scene.convertScenePointToPositionVector(scenePoint);
        final double mass = width * height * 0.30f;
        Rectangle2D rect = new Rectangle2D(pos, pos.add(width, height), colors[random.nextInt(0, colors.length)], Math.round(mass));
//        rect.setRotationDegrees(random.nextInt(0, 360));
        registerPhysicsObject(rect);
    }

    public void addRandomCircle(Point scenePoint) {
//        int radius = random.nextInt(10, 50);
        int radius = 50;
        Vector2D pos = scene.convertScenePointToPositionVector(scenePoint);
        final double mass = (double) (Math.PI * radius * radius * 0.30f);
        Circle2D circle2D = new Circle2D(radius, colors[random.nextInt(0, colors.length)], pos, Math.round(mass));
//        c.setRotationDegrees(random.nextInt(0,360));
        registerPhysicsObject(circle2D);
        checkAndHandleCollisions();
    }
    public void addRandomCircle(Vector2D pos, Vector2D vel) {
        int radius = random.nextInt(10, 50);
        final double mass = (double) (Math.PI * radius * radius * 0.30f);
        Circle2D circle2D = new Circle2D(radius, colors[random.nextInt(0, colors.length)], pos, Math.round(mass));
        circle2D.setLinearVelocity(vel);
//        c.setRotationDegrees(random.nextInt(0,360));
        registerPhysicsObject(circle2D);
        checkAndHandleCollisions();
    }

    public void addCircle(int radius, Vector2D pos, Vector2D vel) {
        double mass = (double) (Math.PI * radius * radius * 0.30f);
        Circle2D circle2D = new Circle2D(radius,colors[random.nextInt(0, colors.length)], pos, Math.round(mass));
        circle2D.setLinearVelocity(vel);
        registerPhysicsObject(circle2D);
        checkAndHandleCollisions();
    }
    public void addSquare(int size, Vector2D p, Vector2D vel) {
        final double mass = size * size * 0.30f;
        Rectangle2D rect = new Rectangle2D(p, p.add(size,size), colors[random.nextInt(0, colors.length)], Math.round(mass));
        rect.setLinearVelocity(vel);
        registerPhysicsObject(rect);
        checkAndHandleCollisions();
    }
    public void setRunning(boolean running) {
        synchronized (this) {
            try {
                isRunning.set(running);
            } catch (Exception e) {
                Log.error("Error occurred while trying to run the engine!" + e);
            }
        }
    }


    private boolean isUp;
    private boolean isDown;
    private boolean isLeft;
    private boolean isRight;
    private RigidObject player;

    public void setPlayer(RigidObject obj1) {
        player = obj1;
        scene.setFocusable(true);

        scene.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getExtendedKeyCode()) {
                    case KeyEvent.VK_UP -> isUp = true;
                    case KeyEvent.VK_DOWN -> isDown = true;
                    case KeyEvent.VK_RIGHT -> isRight = true;
                    case KeyEvent.VK_LEFT -> isLeft = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getExtendedKeyCode()) {
                    case KeyEvent.VK_UP -> isUp = false;
                    case KeyEvent.VK_DOWN -> isDown = false;
                    case KeyEvent.VK_RIGHT -> isRight = false;
                    case KeyEvent.VK_LEFT -> isLeft = false;
                }
            }
        });
    }

    private synchronized void handlePlayer() {
        if (player != null) {
            scene.requestFocus();
            double mag = 100f;
            Vector2D linearVelocity = Vector2D.ZERO;
            if (isUp) {
                linearVelocity = linearVelocity.add(0, 1);
            }
            if (isDown) {
                linearVelocity = linearVelocity.add(0, -1);
            }
            if (isRight) {
                linearVelocity = linearVelocity.add(1, 0);
            }
            if (isLeft) {
                linearVelocity = linearVelocity.add(-1, 0);
            }
            if (!linearVelocity.isZero() && linearVelocity.getMagnitudeSquared() <= mag * mag)
                player.applyImpulse(linearVelocity.normalize().multiply(mag));
        }
    }


}
