import Controll.Controller;
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
        ProgramView programView = new ProgramView();
        frame.setLayout(new BorderLayout());
        initializeNorthPanel(frame, menuBarView);
        frame.setJMenuBar(menuBarView.getMenuBar());
        Controller controller = new Controller(menuBarView, programView);
        frame.add(programView.getScrollChannel(), BorderLayout.WEST);
        frame.add(programView.getProgramScrollPane(), BorderLayout.CENTER);
        frame.setVisible(true);
    }


    private static void initializeNorthPanel(JFrame frame, MenuBarView menuBarView) {
        JPanel combinedNorthPanel = new JPanel(new BorderLayout());
        combinedNorthPanel.add(menuBarView.getTimePanel(), BorderLayout.NORTH);
        frame.add(combinedNorthPanel, BorderLayout.NORTH);
        // change the method's name.
    }
}
