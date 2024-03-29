package View;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class represents the GUI components of thw menu items, menu and menu bar.
 */
public class MenuBarView {
    private final JMenuBar menuBar;
    private final JLabel currentTimeLabel;
    private final JLabel channelUpdatedLabel;
    private final JPanel timePanel;
    private final JLabel selectedChannelLabel;
    private final JMenu channelTypes;
    private final JLabel programUpdatedLabel;

    private ActionListener updateChannelListener;

    private ActionListener updateScheduleListener;


    public MenuBarView() {
        menuBar = new JMenuBar();
        JMenu channel = new JMenu("Channel");
        JMenu schedule = new JMenu("Schedule");
        channelTypes = new JMenu("Channel Types");
        JMenuItem updateChannel = new JMenuItem("Update");
        JMenuItem updateSchedule = new JMenuItem("Update");
        channel.add(updateChannel);
        schedule.add(updateSchedule);
        menuBar.add(channel);
        menuBar.add(schedule);
        menuBar.add(channelTypes);
        timePanel = new JPanel(new GridLayout(2, 4));
        currentTimeLabel = new JLabel();
        programUpdatedLabel = new JLabel("Program updated: ");
        channelUpdatedLabel = new JLabel("Updating . . . .");
        timePanel.add(currentTimeLabel);
        timePanel.add(programUpdatedLabel);
        timePanel.add(channelUpdatedLabel);
        selectedChannelLabel = new JLabel("Selected Channel: ");
        timePanel.add(selectedChannelLabel);
    }



    /**
     * Getter method to get the JLabel which will be used to display
     * when the channels are upadated on the gui.
     * @return JLabel which will be used to display the channel's update.
     */
    public JLabel getChannelUpdatedLabel(){
        return channelUpdatedLabel;
    }

    /**
     * It adds listeners to the channel menu item.
     * @param listener action listener of the menut item which represents the channel.
     */
    public void addUpdateChannelListener(ActionListener listener) {
        JMenuItem updateChannelMenuItem = menuBar.getMenu(0).getItem(0);
        this.updateChannelListener = listener;
        updateChannelMenuItem.addActionListener(updateScheduleListener);
    }


    /**
     * removes the listener from the schedule menu item.
     */
    public void removeUpdateScheduleListener() {
        JMenuItem scheduleListener = menuBar.getMenu(1).getItem(0);
        scheduleListener.removeActionListener(this.updateScheduleListener);
    }

    /**
     * updates the listener of the schedule menu item.
     */
    public void updateScheduleListener() {
        JMenuItem scheduleItem = menuBar.getMenu(1).getItem(0);
        if(scheduleItem  != null) {
            scheduleItem.removeActionListener(this.updateScheduleListener);
            scheduleItem.addActionListener(this.updateScheduleListener);
        }
    }


    /**
     * It removes the listener from the channel menu item.
     */
    public void removeUpdateChannelListener() {
        if (this.updateChannelListener != null) {
            JMenuItem updateChannelMenuItem = menuBar.getMenu(0).getItem(0);
            updateChannelMenuItem.removeActionListener(this.updateChannelListener);
        }
    }

    /**
     * it upadates the listener of the channel menu item.
     */
    public void updateChannelListener(){
        JMenuItem updateChannelMenuItem = menuBar.getMenu(0).getItem(0);
        if(updateChannelMenuItem != null) {
            updateChannelMenuItem.addActionListener(this.updateChannelListener);
        }
    }

    /**
     * for the test purpose
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * It adds listeners to the schedule update menu item.
     * @param listener action listener of the menu item which represents the update for
     *                 specified channel'update.
     */
    public void addUpdateScheduleListener(ActionListener listener) {
        this.updateScheduleListener = listener;
        JMenuItem scheduleListener = menuBar.getMenu(1).getItem(0);
        scheduleListener.addActionListener(this.updateScheduleListener);
    }


    /**
     * It used to set a channel name on the GUI.
     * @param channelName the name of the channel.
     */
    public void setSelectedChannelLabel(String channelName) {
        selectedChannelLabel.setText("Selected Channel: " + channelName);
    }


    /**
     * Getter method to return that panel which holds when the program of a
     * specified channel is updated.
     * @return the panel which holds when the program of a specified updated.
     */
    public JLabel getProgramUpdatedLabel(){
        return programUpdatedLabel;
    }

    /**
     * Getter method to return that panel which holds information about the time.
     * @return panel which holds information about the time of update.
     */

    public JPanel getTimePanel() {
        return timePanel;
    }

    /**
     *  It starts the timer.
     */
    public void startClock() {
        Timer clockTimer = new Timer(1000, e -> {
            LocalDateTime currentTime = LocalDateTime.now();
            SwingUtilities.invokeLater(() -> setCurrentTimeLabel(currentTime));
        });
        clockTimer.start();
    }

    /**
     * It sets the current time on the panel.
     * @param currentTime the current local time.
     */
    public void setCurrentTimeLabel(LocalDateTime currentTime) {
        String formattedTime = "Current Time: " + currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        SwingUtilities.invokeLater(() -> currentTimeLabel.setText(formattedTime));
    }

    /**
     * By invoking this method, the user will get JMenu which holds information about the channels
     * type.
     * @return JMenu which holds information about the channels' types.
     */
    public JMenu getChannelsTypeMenu() {
        return channelTypes;
    }



    /**
     * For the testing purpose only
     */
    public JLabel getCurrentTimeLabel() {
        return currentTimeLabel;
    }

    /**
     * For the testing purpose only
     */
    public JLabel getSelectedChannelLabel(){
        return selectedChannelLabel;
    }
}
