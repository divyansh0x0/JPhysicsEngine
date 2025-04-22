package physicsemulator;

import material.animation.MaterialFixedTimer;
import material.component.MaterialIconButton;
import material.constants.Size;
import material.containers.MaterialPanel;
import material.utils.Log;
import material.window.MaterialWindow;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.fluentui.FluentUiFilledMZ;
import physicsemulator.engine.EngineProperties;
import physicsemulator.engine.RigidBodyModel;
import physicsemulator.physicalbody.RigidObject;
import physicsemulator.engine.Engine;
import physicsemulator.engine.Scene;
import physicsemulator.physicalbody.rigidbody.Rectangle2D;
import physicsemulator.utils.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * READ ME
 * <p>
 * <p>
 * IN COMPUTERS THE COORDINATE SYSTEM USED FOR GUI IS DIFFERENT THAN THE ONE USED IN MATHS
 * THE ORIGIN LIES ON TOP LEFT CORNER. ALL OBJECTS ARE RENDERED AND POSITIONED ACCORDING TO IT.
 * <p>
 * BUT BECAUSE I AM USING VECTORS IN THIS PROJECT, THE ORIGIN OF THE VECTOR SPACE IS IN THE BOTTOM LEFT CORNER OF THE SCENE PANEL
 */
public class Main {
    private final static Scene scene = new Scene();
    private final static Engine physicsEngine = new Engine(scene);


    private static final MaterialWindow materialWindow = new MaterialWindow("Physics Emulator", new Size(1080, 640));

    public static void main(String[] args) {
//        LineSegment2D line = new LineSegment2D(new Vector2D(-100,50),new Vector2D(100,50));
//        Log.info(Collider2D.getNormalVectorTowardsOrigin(line).getAngleFromPositiveXDirection());
//        Log.info(new LineSegment2D(new Vector2D(10,0),new Vector2D(10,200)).isPointOnLine(new Vector2D(10,300)));
//        Log.info("comp: " + Arrays.toString(new Vector2D(4, 3).resolveVector(new Vector2D(100, 50))));
        scene.addMouseListener(new MouseAdapter() {
            long last;

            @Override
            public void mouseDragged(MouseEvent e) {
                long now = System.nanoTime();
                if (now - last > 1_000_000_0) {
                    if (SwingUtilities.isRightMouseButton(e)) {
//                        physicsEngine.addRandomCircle(e.getPoint());
                        physicsEngine.addCircle(50,scene.convertScenePointToPositionVector(e.getPoint()),Vector2D.ZERO);

                        last = now;
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    physicsEngine.addCircle(50,scene.convertScenePointToPositionVector(e.getPoint()),new Vector2D((float) (Math.random() * 50f), (float) (Math.random() * 50f)));
//                    physicsEngine.addRandomRect(e.getPoint());
                }
            }
        });
        init();
    }

    public static void init() {
        EventQueue.invokeLater(() -> {
            materialWindow.pack();
            materialWindow.setVisible(true);
        });

        MaterialIconButton btn = getPlayPauseButton();
        MaterialPanel root = materialWindow.getRootPanel();
        root.add(scene, "grow");
        root.add(btn, "south,w 120!,h 60!");
        EngineProperties.getInstance().setRestitutionCoefficient(0);


//        float y = 200;
//        RigidObject obj1 = new Rectangle2D(new Vector2D(300,300),new Vector2D(400,400), Color.RED);
//        RigidObject obj1 = new Circle2D(50, new Color(0x51A3F8), new Vector2D(200, y));
//        RigidObject rect1 = new Rectangle2D(new Vector2D(200, 100), new Vector2D(250, 200), Color.RED);
//        RigidObject rect2 = new Rectangle2D(new Vector2D(200, 300), new Vector2D(250, 400), Color.RED);
//        RigidObject rect3 = new Rectangle2D(new Vector2D(200, 500), new Vector2D(250, 600), Color.RED);
//        RigidObject obj2 = new Circle2D(50, new Color(0x4923C1), new Vector2D(400, y));
//        RigidObject obj3 = new Circle2D(50, new Color(0x51A3F8), new Vector2D(500, y));
//        RigidObject obj4 = new Circle2D(50, new Color(0x4923C1), new Vector2D(600, y));

        RigidObject wall1 = new Rectangle2D(new Vector2D(-5100, 0), new Vector2D(-5000, 50000), new Color(0x6D6D6D));
        RigidObject wall2 = new Rectangle2D(new Vector2D(5000, 0), new Vector2D(5100, 50000), new Color(0x6D6D6D));
        RigidObject wall3 = new Rectangle2D(new Vector2D(-20000, -80), new Vector2D(90000, 0), new Color(0x6D6D6D));
        RigidObject wall4 = new Rectangle2D(new Vector2D(-20000, 6000), new Vector2D(90000, 6050), new Color(0x6D6D6D));

        wall1.setStatic(true);
        wall2.setStatic(true);
        wall3.setStatic(true);
        wall4.setStatic(true);
        addRigidObjects(wall1, wall2, wall3, wall4);


        materialWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                physicsEngine.dispose();
                MaterialFixedTimer.disposeAll();
            }
        });


    }


    private static void addRigidObjects(RigidBodyModel... objects) {
        for (RigidBodyModel obj : objects)
            physicsEngine.registerPhysicsObject(obj);
    }

    private static void addCircles() {

        int size = 100;
        for (int i = -30; i < 30; i++) {
            for (int j = 0; j < 20; j++) {
                Vector2D pos = new Vector2D(size * 2f * i + 10, 200 + size * 2 * j + 10);
                physicsEngine.addCircle(size, pos, vel);
            }

        }
    }

    private static void addSquares() {

        int size = 100;
        for (int i = -30; i < 30; i++) {
            for (int j = 0; j < 20; j++) {
                Vector2D pos = new Vector2D(size * i + 10, 200 + size *  j + 10);
                physicsEngine.addSquare(size, pos, vel);
            }

        }
    }
    private static Vector2D p = new Vector2D(0, 4000);
    private static Vector2D vel = new Vector2D(0, 0);
    private static final MaterialFixedTimer timer = new MaterialFixedTimer(1) {
        @Override
        public void tick(float deltaMillis) {
//            physicsEngine.addCircle(50,p,vel);
        }
    };

    @NotNull
    private static MaterialIconButton getPlayPauseButton() {
        MaterialIconButton btn = new MaterialIconButton(FluentUiFilledMZ.PLAY_20, "PLAY");
        btn.addLeftClickListener(e -> {
            synchronized (physicsEngine) {
                Log.info("Play/pause");
                if (physicsEngine.isRunning()) {
                    //Pause
                    timer.stop();
                    physicsEngine.setRunning(false);
                    btn.setText("Play");

                    btn.setIcon(FluentUiFilledMZ.PLAY_20);
                } else {
                    //Play
                    timer.start();
                    physicsEngine.setRunning(true);
                    btn.setText("Pause");
                    btn.setIcon(FluentUiFilledMZ.PAUSE_20);
                }
            }
        });
        return btn;
    }

}