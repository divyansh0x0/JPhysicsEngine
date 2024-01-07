package physicsemulator.engine;

import material.component.MaterialComponent;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.ThemeType;
import physicsemulator.engine.colors.VectorColors;
import physicsemulator.utils.PhysicsUtils;
import physicsemulator.utils.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Scene extends MaterialComponent {
    private final ArrayList<RigidBodyModel> rigidBodies = new ArrayList<>();
    private static final double MAX_SCALE = 5f;
    private static final double MIN_SCALE = 0.1f;
    private Polygon triangle;
    private double SCALE = 1f;
    private double INV_SCALE = 1 / SCALE;

    private final Point pointerLocation = new Point();
    private final Point RENDERING_ORIGIN = new Point(100, 100);//This will be changed to the bottom left most point
    private boolean isPressed = false;
    private RigidBodyModel selectedObj;
    private Vector2D correctionVector;
    //    private double LAST_SCALE = SCALE;
    private final Point OLD_DRAG_LOCATION = new Point();
    private final Vector2D OLD_LOCK_ON_LOCATION = new Vector2D();
    private final Vector2D CAMERA_BOTTOM_LEFT = new Vector2D();
    private final Vector2D CAMERA_TOP_RIGHT = new Vector2D();
    //    private final Point OLD_TRANSLATED_ORIGIN = new Point(TRANSLATION_ORIGIN.x,TRANSLATION_ORIGIN.y);
    private final Point scalingPointerLocation = new Point();
    private boolean isDragged = false;
    private RigidBodyModel lockedObj;
    private boolean isAxesDrawingAllowed = true;
    private double axesPointSeparation = 100f;
    private double LAST_SCALE;
    private double FPS;
    private long lastFpsPaintTime;
    private int frameCounter;
    private double physicsTimeStep;

    public Scene() {
        super();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Vector2D point = convertScenePointToPositionVector(e.getX(), e.getY());
                if (SwingUtilities.isLeftMouseButton(e)) {
                    isPressed = true;
                    handleMousePress(point);
                }
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    handleMousePressForLockOn(point);
                } else {
                    if (lockedObj != null) {
                        lockedObj = null;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                isDragged = false;
                if (selectedObj != null) {
                    selectedObj.setSelected(false);
                    selectedObj = null;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isPressed)
                    handleObjectDragging(e);
                if (SwingUtilities.isLeftMouseButton(e)) {
                    isDragged = true;
                    pointerLocation.setLocation(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                pointerLocation.setLocation(e.getX(), e.getY());
                OLD_DRAG_LOCATION.setLocation(pointerLocation);
            }
        });
        addMouseWheelListener(this::handleMouseWheelMove);
    }


    public Point getOrigin() {
        return RENDERING_ORIGIN;
    }

    @Override
    public void updateTheme() {
        setBackground(ThemeManager.getInstance().getThemeType().equals(ThemeType.Dark) ? Color.BLACK : Color.WHITE);
        setForeground(ThemeColors.getInstance().getTextPrimary());
    }

    @Override
    public void addNotify() {
        super.addNotify();

//        if(getCameraBottomLeft().isZero() && getCameraTopRight().isZero()){
//        }
    }
    /* ***********************************************************
     *                      Painter
     ***********************************************************/

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        //Update frame rate
        if (System.nanoTime() - lastFpsPaintTime >= 1_000_000_000) {
            FPS = frameCounter;
            lastFpsPaintTime = System.nanoTime();
            frameCounter = 0; // Reset frame counter after printing
        }
        frameCounter++;

        //Painting  begins here
        Graphics2D g2d = (Graphics2D) g;
        final float fontSize = (float) (getFontSize() * getScale());
        g2d.setFont(getFont().deriveFont(fontSize));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        //Background
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        //Scaling
//        if (!PhysicsUtils.areEqual(LAST_SCALE, SCALE)) {
//
////            Log.info(pointerVec);
////            setCameraCenter(convertScenePointToPositionVector(pointerLocation).subtract(CAMERA_LOCATION_WRT_ORIGIN).multiply(1/SCALE));
////            setCameraPosition(UNSCALED_CAMERA_LOCATION_WRT_ORIGIN.multiply(1/SCALE));
//            LAST_SCALE = SCALE;
////            lastScalePoint.setLocation(pointerLocation);
//        }
        //Object locking
        if (lockedObj != null) {
            setCameraCenter(lockedObj.getPosition());
        }
        //Translation
        if (isDragged && selectedObj == null) {
            int deltaX = (int) (pointerLocation.getX() - OLD_DRAG_LOCATION.getX());
            int deltaY = (int) (pointerLocation.getY() - OLD_DRAG_LOCATION.getY());
            translateCamera(-deltaX, deltaY);
            OLD_DRAG_LOCATION.setLocation(pointerLocation);
        }

        RENDERING_ORIGIN.setLocation(-CAMERA_BOTTOM_LEFT.getX(), getHeight() + CAMERA_BOTTOM_LEFT.getY());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        //Render all components
        renderComponents((Graphics2D) g2d.create());

        //Cartesian Plane
        int fontHeight = fontMetrics.getHeight();
        if (isAxesDrawingAllowed) {
            final int pad = (int) axesPointSeparation;
            final int gap = (int) (5 * SCALE);
            final int carretSize = (int) (fontSize / 2);
            float scaledAxesPointSeparation = (float) (axesPointSeparation * getScale());
            g2d.setColor(ThemeColors.getInstance().getAccent());
            final int clampedYPos = Math.clamp(RENDERING_ORIGIN.y, 1, getHeight());
            final int clampedXPos = Math.clamp(RENDERING_ORIGIN.x, 1, getWidth());
            final int verticalPosition = (int) Math.clamp(RENDERING_ORIGIN.y + fontHeight + carretSize + gap, fontHeight + gap, getHeight() - carretSize - gap);

            final int pointLineY1 = clampedYPos - carretSize;
            final int pointLineY2 = clampedYPos + carretSize;

            final int pointLineX1 = clampedXPos - carretSize;
            final int pointLineX2 = clampedXPos + carretSize;

            //X-AXIS
            g2d.drawLine(0, clampedYPos, getWidth(), clampedYPos);
            //-ve x-axis
            for (float xPos = RENDERING_ORIGIN.x; xPos >= -pad; xPos -= scaledAxesPointSeparation) {
                String xVal = "%.1f".formatted((xPos - RENDERING_ORIGIN.x) * INV_SCALE);
                if (xVal.equals("0.0"))
                    continue;
                g2d.drawLine((int) xPos, pointLineY1, (int) xPos, pointLineY2);
                g2d.drawString(xVal, xPos - fontMetrics.stringWidth(xVal) / 2f, verticalPosition);
            }
            //+ve x-axis
            for (float xPos = RENDERING_ORIGIN.x; xPos < getWidth() + pad; xPos += scaledAxesPointSeparation) {
                String xVal = "%.1f".formatted((xPos - RENDERING_ORIGIN.x) * INV_SCALE);
                if (xVal.equals("0.0"))
                    continue;
                g2d.drawLine((int) xPos, pointLineY1, (int) xPos, pointLineY2);
                g2d.drawString(xVal, xPos - fontMetrics.stringWidth(xVal) / 2f, verticalPosition);
            }
            //y-axis
            g2d.drawLine(clampedXPos, 0, clampedXPos, getHeight());
            //+ve y-axis
            for (float yPos = RENDERING_ORIGIN.y; yPos >= -pad; yPos -= scaledAxesPointSeparation) {
                String yVal = "%.1f".formatted((RENDERING_ORIGIN.y - yPos) * INV_SCALE);
                if (yVal.equals("0.0"))
                    continue;
                int strWidth = fontMetrics.stringWidth(yVal);
                int horizontalPosition = Math.clamp(RENDERING_ORIGIN.x + gap, carretSize + gap, getWidth() - strWidth);

                g2d.drawLine(pointLineX1, (int) yPos, pointLineX2, (int) yPos);
                g2d.drawString(yVal, horizontalPosition, yPos + fontSize * 0.25f);
            }
            //-ve y-axis
            for (float yPos = RENDERING_ORIGIN.y; yPos < getHeight() + pad; yPos += scaledAxesPointSeparation) {
                String yVal = "%.1f".formatted((RENDERING_ORIGIN.y - yPos) * INV_SCALE);
                if (yVal.equals("0.0"))
                    continue;
                int strWidth = fontMetrics.stringWidth(yVal);
                int horizontalPosition = Math.clamp(RENDERING_ORIGIN.x + gap, carretSize + gap, getWidth() - strWidth);
                g2d.drawLine(pointLineX1, (int) yPos, pointLineX2, (int) yPos);
                g2d.drawString(yVal, horizontalPosition, yPos + fontSize * 0.25f);
            }

            //Origin
            if ((RENDERING_ORIGIN.x >= -pad && RENDERING_ORIGIN.x < getWidth() + pad) && (RENDERING_ORIGIN.y > -pad && RENDERING_ORIGIN.y < getHeight() + pad)) {
                int horizontalPosition = Math.clamp(RENDERING_ORIGIN.x + gap, -pad, getWidth() + pad);

                int y = Math.clamp(RENDERING_ORIGIN.y + fontHeight + carretSize + gap, -pad, getHeight() + pad);

                g2d.drawString("0", horizontalPosition, y);
            }
        }
        //FPS AND STUFF
        g2d.setFont(g2d.getFont().deriveFont(getFontSize()));
        fontMetrics = g2d.getFontMetrics();
        fontHeight = (int) getFontSize();
        g2d.setColor(ThemeColors.getInstance().getTextPrimary());

        //FPS and time change
        final int vgap = 10;
        String str = "FPS: " + FPS;
        int fx = getWidth() - fontMetrics.stringWidth(str);
        int fy = fontHeight;
        g2d.drawString(str, fx, fy);
        str = "∆t: %.2fms".formatted(physicsTimeStep);
        fy += fontHeight + vgap;
        fx = getWidth() - fontMetrics.stringWidth(str);
        g2d.drawString(str, fx, fy);
        //Object counter
        str = "Objects:" + rigidBodies.size();
        fy += fontHeight + vgap;
        fx = getWidth() - fontMetrics.stringWidth(str);
        g2d.drawString(str, fx, fy);
        //Scale monitor;
        str = "Scale: " + String.format("%.2f", getScale()) + "x";
        fy += fontHeight + vgap;
        fx = getWidth() - fontMetrics.stringWidth(str);
        g2d.drawString(str, fx, fy);

        Toolkit.getDefaultToolkit().sync();
    }


    protected void drawVector(Graphics2D g, Vector2D v, Vector2D position) {
        if (v.isZero())
            return;
        Point o = getScenePointFromPositionVector(position);
        Point p = getScenePointFromPositionVector(v.add(position));

        drawArrow(g, VectorColors.getColorModel().getPositionColor(), o, p);
    }

    public void drawArrow(Graphics2D g, Vector2D vec) {
        Point p = getScenePointFromPositionVector(vec);
        drawArrow(getGraphics(), ThemeColors.getInstance().getTextSecondary(), RENDERING_ORIGIN, p);
    }

    public void drawArrow(Graphics g, Color color, Point start, Point end) {
        if (g == null)
            return;
        final int ARROW_SIZE = 10;
        if (triangle == null || !PhysicsUtils.areEqual(LAST_SCALE, SCALE)) {
            triangle = new Polygon();
            triangle.addPoint((int) (ARROW_SIZE * SCALE), 0);
            triangle.addPoint((int) (-ARROW_SIZE * SCALE), 0);
            triangle.addPoint(0, (int) (ARROW_SIZE * SCALE));
        }
        Graphics2D g2d = (Graphics2D) g.create();
        final int x1 = start.x;
        final int y1 = start.y;
        final int x2 = end.x;
        final int y2 = end.y;

        g2d.setColor(color);
        g2d.drawLine(x1, y1, x2, y2);


        //Drawing arrow
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        final float angle = (float) Math.atan2(dx, dy);//Angle from horizontal
//        final int len = (int) Math.sqrt(dx*dx+dy*dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x2, y2);
        at.rotate(-angle);

        g2d.transform(at);


        g2d.fill(triangle);

        g2d.dispose();
    }

    private void renderComponents(Graphics2D g) {
        ArrayList<RigidBodyModel> allRigidBodies = rigidBodies;
        for (int i = allRigidBodies.size() - 1; i >= 0; i--) {
            RigidBodyModel obj = allRigidBodies.get(i);
            obj.renderObject((Graphics2D) g.create(), this);
            if (obj.isSelected())
                drawStateVectors(g, obj);
        }
    }

    private void drawStateVectors(Graphics2D g, RigidBodyModel obj) {
        Vector2D pos = obj.getPosition();
        drawVector(g, obj.getForce(), pos);
        drawVector(g, obj.getCurrentVelocity(), pos);
        drawVector(g, obj.getJerk(), pos);
    }


    private void setCameraCenter(double x, double y) {
        x -= getWidth() / 2f;
        y = getHeight() / 2f - y;
        translateCamera(x * INV_SCALE, y * INV_SCALE);
    }

    private void setCameraCenter(Vector2D v) {
        translateCamera(v.subtract(convertScenePointToPositionVector(getWidth() / 2f, getHeight() / 2f)));
    }

    private void setCameraPosition(Vector2D pointerVec) {
        CAMERA_BOTTOM_LEFT.pointTo(pointerVec);
        CAMERA_TOP_RIGHT.pointTo(CAMERA_BOTTOM_LEFT.getX() + getWidth(), CAMERA_BOTTOM_LEFT.getY() + getHeight());
    }

    private void setCameraPosition(double x, double y) {
        this.setCameraPosition(new Vector2D(x, y));
    }

    private void translateCamera(double dx, double dy) {
        CAMERA_BOTTOM_LEFT.pointTo(CAMERA_BOTTOM_LEFT.getX() + dx, CAMERA_BOTTOM_LEFT.getY() + dy);
        CAMERA_TOP_RIGHT.pointTo(CAMERA_BOTTOM_LEFT.getX() + getWidth(), CAMERA_BOTTOM_LEFT.getY() + getHeight());
    }

    private void translateCamera(Vector2D vec) {
        this.translateCamera(vec.getX(), vec.getY());
    }

    public boolean isAxesDrawingAllowed() {
        return isAxesDrawingAllowed;
    }

    public void setAxesDrawingAllowed(boolean axesDrawingAllowed) {
        this.isAxesDrawingAllowed = axesDrawingAllowed;
    }

    public double getAxesPointSeparation() {
        return axesPointSeparation;
    }

    public void setAxesPointSeparation(double axesPointSeparation) {
        this.axesPointSeparation = axesPointSeparation;
    }



    /* **********************************************
     *              UTILITIES
     ****************************************/

    public Point getScenePointFromPositionVector(Vector2D pos) {
        return getScenePointFromPositionVector(pos.getX(), pos.getY());
    }

    public Point getScenePointFromPositionVector(double x, double y) {
        int newX = (int) (x * getScale() + RENDERING_ORIGIN.x);
        int newY = (int) (RENDERING_ORIGIN.y - y * getScale());
        return new Point(newX, newY);
    }

    public Vector2D convertScenePointToPositionVector(Point p) {
        return convertScenePointToPositionVector(p.x, p.y);
    }

    private Vector2D convertScenePointToPositionVector(double X, double Y) {
        double x = X - RENDERING_ORIGIN.x;
        double y = RENDERING_ORIGIN.y - Y;
        return new Vector2D(x * INV_SCALE, y * INV_SCALE);
    }

    private void handleObjectDragging(MouseEvent e) {
        if (selectedObj != null) {
            //Where center of object should be, without correction using subtraction the center will be always at mouse pointer
            Vector2D objectPos = this.convertScenePointToPositionVector(e.getPoint()).subtract(correctionVector);
            selectedObj.setPosition(objectPos);
        }
    }


    private void handleMousePress(Vector2D point) {
        ArrayList<RigidBodyModel> allRigidBodies = rigidBodies;
        for (int i = 0; i < allRigidBodies.size(); i++) {
            RigidBodyModel obj = allRigidBodies.get(i);
            if (obj.isPointIntersecting(point)) {
                selectedObj = obj;
                selectedObj.setSelected(true);
                break;
            }
        }


        if (selectedObj != null) {
            correctionVector = point.subtract(selectedObj.getPosition());
        }
    }


    private void handleMousePressForLockOn(Vector2D point) {
        ArrayList<RigidBodyModel> allRigidBodies = rigidBodies;
        for (int i = 0; i < allRigidBodies.size(); i++) {
            RigidBodyModel obj = allRigidBodies.get(i);
            if (obj.isPointIntersecting(point)) {
                lockedObj = obj;
                OLD_LOCK_ON_LOCATION.pointTo(point);
                break;
            }
        }

    }

    private void handleMouseWheelMove(MouseWheelEvent e) {
        double newScale = getScale() + (e.getPreciseWheelRotation() < 0 ? 0.05f : -0.05f);
        if (PhysicsUtils.areEqual(newScale, 1))
            newScale = 1;
        newScale = Math.clamp(newScale, MIN_SCALE, MAX_SCALE);
        scalingPointerLocation.move(e.getX(), e.getY());
        setScale(newScale);
    }


    public double getScale() {
        return SCALE;
    }

    public Vector2D getCameraBottomLeft() {
        return CAMERA_BOTTOM_LEFT;
    }

    public Vector2D getCameraTopRight() {
        return CAMERA_TOP_RIGHT;
    }

    public double getPhysicsTimeStep() {
        return physicsTimeStep;
    }

    /* **********************************************
     *              SETTERS
     ****************************************/
    public void addObject(RigidBodyModel obj) {
        if (!rigidBodies.contains(obj))
            rigidBodies.add(obj);
    }

    public void removeObject(RigidBodyModel obj) {
        rigidBodies.remove(obj);
    }

    public void setScale(double newScale) {
        LAST_SCALE = this.SCALE;
        this.SCALE = newScale;
        this.INV_SCALE = 1 / newScale;
    }

    public void setPhysicsTimeStep(double physicsTimeStep) {
        this.physicsTimeStep = physicsTimeStep;
    }

//    public void setFPS(double FPS) {
//        this.FPS = FPS;
//    }
}
