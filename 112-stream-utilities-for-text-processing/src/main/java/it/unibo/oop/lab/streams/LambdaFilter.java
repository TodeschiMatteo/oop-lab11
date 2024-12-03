package it.unibo.oop.lab.streams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Modify this small program adding new filters.
 * Realize this exercise using as much as possible the Stream library.
 *
 * 1) Convert to lowercase
 *
 * 2) Count the number of chars
 *
 * 3) Count the number of lines
 *
 * 4) List all the words in alphabetical order
 * 
 * 5) Write the count for each word, e.g. "word word pippo" should output "pippo -> 1 word -> 2"
 *
 */
public final class LambdaFilter extends JFrame {

    private static final long serialVersionUID = 1760990730218643730L;
    private static final String REGEX = "[ \n]+";

    private enum Command {
        /**
         * Commands.
         */
        IDENTITY("No modifications", Function.identity()),
        TOLOWER("Covert to Lower Case", String::toLowerCase),
        NUMBER_COUNT("Count the number of lines", c -> Integer.toString(c.length())),
        LINES_COUNT("Count the number of lines", c -> Long.toString(c.chars().filter(i -> i == '\n').count())),
        ORDER("List all the words in alphabetical order", c -> 
            Arrays.stream(c.split(REGEX))
            .sorted().
            collect(Collectors.joining("\n"))),
        WORD_COUNTER("Write the count for each word", c -> 
            Arrays.stream(c.split(REGEX))
            .map(s -> s + " -> " + s.length() + "\n")
            .collect(Collectors.joining()));

        private final String commandName;
        private final Function<String, String> fun;

        Command(final String name, final Function<String, String> process) {
            commandName = name;
            fun = process;
        }

        @Override
        public String toString() {
            return commandName;
        }

        public String translate(final String s) {
            return fun.apply(s);
        }
    }

    private LambdaFilter() {
        super("Lambda filter GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel1 = new JPanel();
        final LayoutManager layout = new BorderLayout();
        panel1.setLayout(layout);
        final JComboBox<Command> combo = new JComboBox<>(Command.values());
        panel1.add(combo, BorderLayout.NORTH);
        final JPanel centralPanel = new JPanel(new GridLayout(1, 2));
        final JTextArea left = new JTextArea();
        left.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        final JTextArea right = new JTextArea();
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        right.setEditable(false);
        centralPanel.add(left);
        centralPanel.add(right);
        panel1.add(centralPanel, BorderLayout.CENTER);
        final JButton apply = new JButton("Apply");
        apply.addActionListener(ev -> right.setText(((Command) combo.getSelectedItem()).translate(left.getText())));
        panel1.add(apply, BorderLayout.SOUTH);
        setContentPane(panel1);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int sw = (int) screen.getWidth();
        final int sh = (int) screen.getHeight();
        setSize(sw / 4, sh / 4);
        setLocationByPlatform(true);
    }

    /**
     * @param a unused
     */
    public static void main(final String... a) {
        final LambdaFilter gui = new LambdaFilter();
        gui.setVisible(true);
    }
}
