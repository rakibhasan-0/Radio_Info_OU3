import View.ChannelView;
import View.MenuBarView;
import View.ProgramView;

import javax.swing.*;
import java.awt.*;

public class GuiTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("GUI Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        MenuBarView menuBarView = new MenuBarView();
        ChannelView channelView = new ChannelView();
        ProgramView programView = new ProgramView();
        frame.setLayout(new BorderLayout());
        initializeNorthPanel(frame, menuBarView);
        frame.setJMenuBar(menuBarView.getMenuBar());
        frame.add(channelView.getScrollChannel(), BorderLayout.WEST);
        frame.add(programView.getProgramScrollPane(), BorderLayout.CENTER);
        menuBarView.startClock();
        frame.setVisible(true);
    }


    private static void initializeNorthPanel(JFrame frame, MenuBarView menuBarView) {
        JPanel combinedNorthPanel = new JPanel(new BorderLayout());
        combinedNorthPanel.add(menuBarView.getTimePanel(), BorderLayout.NORTH);
        frame.add(combinedNorthPanel, BorderLayout.NORTH);
        // change the method's name.
    }
}
