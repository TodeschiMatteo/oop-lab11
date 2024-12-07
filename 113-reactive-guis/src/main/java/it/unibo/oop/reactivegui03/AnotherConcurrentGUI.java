package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");
    private final Agent agent = new Agent();

    /**
     * Builds a new CGUI.
     * CPD-START warning has been suppressed in order to bypass transient error.
     */
    @SuppressWarnings("CPD-START")
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        /*
         * Create the counter agent and start it. This is actually not so good:
         * thread management should be left to
         * java.util.concurrent.ExecutorService
         */
        new Thread(agent).start();

        /*
         * Create the stopping agent eit lambda and starting it
         */
        new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.stop();
        }).start();

        /*
         * Register a listener that stops it
         */
        up.addActionListener((e) -> agent.upCounting());
        down.addActionListener((e) -> agent.downCounting());
        stop.addActionListener((e) -> agent.stopCounting());
    }

    private void stop() {
        agent.stopCounting();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                stop.setEnabled(false);
                up.setEnabled(false);
                down.setEnabled(false);
            }
        });

    }

    private final class Agent implements Runnable, Serializable {
        /*
         * Stop is volatile to ensure visibility. Look at:
         * 
         * http://archive.is/9PU5N - Sections 17.3 and 17.4
         * 
         * For more details on how to use volatile:
         * 
         * http://archive.is/4lsKW
         * 
         */
        @Serial
        private static final long serialVersionUID = 1L;
        private volatile boolean stop;
        private volatile boolean descending;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    counter += descending ? -1 : 1;
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        public void upCounting() {
            this.descending = false;
        }

        public void downCounting() {
            this.descending = true;
        }
    }
}
