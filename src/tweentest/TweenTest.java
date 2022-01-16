/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tweentest;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Sine;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.time.Instant;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author shane.whitehead
 */
public class TweenTest {

    public static void main(String[] args) {
        new TweenTest();
    }

    public TweenTest() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        private Dot dot = new Dot(new Point2D.Float(0, 95));
        private TweenManager manager = new TweenManager();

        private Instant lastUpdate;

        private ActionListener mainLoop = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (lastUpdate == null) {
                    lastUpdate = Instant.now();
                }

                Duration runTime = Duration.between(lastUpdate, Instant.now());
                float elapsedTime = runTime.toMillis();
                manager.update(elapsedTime);

                lastUpdate = Instant.now();

                repaint();
            }
        };

        private Timer timer = new Timer(5, mainLoop);

        public TestPane() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (timer.isRunning()) {
                        return;
                    }
                    // The duration seems to be confusing, but I've got
                    // it to work by using millisecon percision
                    lastUpdate = null;
                    Tween.to(dot, Dot.LOCATION, 1000f)
                            .target(190, 95)
                            .ease(Sine.INOUT)
                            .repeatYoyo(1, 0)
                            .setCallback(new TweenCallback() {
                                @Override
                                public void onEvent(int type, BaseTween<?> source) {
                                    switch (type) {
                                        case TweenCallback.COMPLETE:
                                            timer.stop();
                                            lastUpdate = null;
                                            break;
                                    }
                                }
                            }).start(manager);
                    timer.start();
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            Rectangle2D box = new Rectangle2D.Double(0, 0, 10, 10);
            g2d.translate(dot.getLocation().getX(), dot.getLocation().getY());
            g2d.setColor(Color.RED);
            g2d.fill(box);
            g2d.dispose();
        }

    }

    public static class Dot {

        static {
            Tween.registerAccessor(Dot.class, new DotTweenAccessor());
        }

        static final int LOCATION = 1;

        private Point2D.Float location;

        public Dot(Point2D.Float location) {
            this.location = location;
        }

        public Point2D.Float getLocation() {
            return location;
        }

    }

    public static class DotTweenAccessor implements TweenAccessor<Dot> {

        @Override
        public int getValues(Dot target, int tweenType, float[] returnValues) {
            switch (tweenType) {
                case Dot.LOCATION:
                    returnValues[0] = (float) target.getLocation().getX();
                    returnValues[1] = (float) target.getLocation().getY();
                    return 2;
            }
            return 0;
        }

        @Override
        public void setValues(Dot target, int tweenType, float[] newValues) {
            switch (tweenType) {
                case Dot.LOCATION:
                    target.getLocation().setLocation(newValues[0], newValues[1]);
                    break;
            }
        }

    }

}
