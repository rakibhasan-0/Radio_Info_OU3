package Controll;

import Model.Channel;
import Model.Schedule;
import View.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Gazi Md Rakibul Hasan
 * That class reponsible for updating GUI-related components.  Furthermore, It is responsible for
 * displaying channel and program information on the GUI based on the user interaction on the GUI.
 */

public class UIManager {
    private final ProgramView programView;
    private final MenuBarView menuBarView;
    private final ChannelListener channelListener;
    private final ProgramDetails programDetails;
    private HashMap<String, ArrayList<Channel>> channelsWithTypes;
    private HashSet<String> types;
    private final ConcurrentHashMap<Integer, Boolean> channelUpdateStatus = new ConcurrentHashMap<Integer, Boolean>();


    /**
     * it is a constructor method of UIManager class, it initializes the UIManager with
     * the provided class which relates with GUI and a channelListener.
     */
    public UIManager(ProgramView programView, MenuBarView menuBarView, ChannelListener channelListener) {
        this.programView = programView;
        this.menuBarView = menuBarView;
        this.channelListener = channelListener;
        programDetails = new ProgramDetails(programView);
    }


    /**
     * It adds channel types on the given menu. Those channel types will be added as
     * menu item on the menu.
     */
    public void addChannelType() {

        JMenu channelTypeMenu = menuBarView.getChannelsTypeMenu();
        channelTypeMenu.removeAll();

        for (String types : types){
            JMenuItem channelType = new JMenuItem(types);
            channelTypeMenu.add(channelType);
            channelType.addActionListener(e->displayChannels(types));
        }
    }



    /**
     * That method will display channels on the GUI based on the given
     * type of the channel.
     * @param channelType the channel type.
     */

    public void displayChannels(String channelType){
        programView.clearChannelButtons();
        ArrayList<Channel> channels = channelsWithTypes.get(channelType);
        for (Channel channel : channels) {
            JButton button = new JButton(channel.getChannelName());
            button.setIcon(channel.getIcon());
            button.addActionListener(e -> {
                //System.out.println("Selected channel from UI Manager Class:"+channel.getChannelName()+"Current Thread"+ Thread.currentThread().getName());
                //System.out.println("Selected channel:"+channel.getChannelName());
                Boolean isUpdating = channelUpdateStatus.getOrDefault(channel.getId(), Boolean.FALSE);
                //System.out.println("Is Updating status:"+isUpdating);
                if (isUpdating) {
                    JOptionPane.showMessageDialog(null, "The channel is updating. Please wait for a while.");
                    return;
                } else {
                    // Mark the channel as updating
                    channelUpdateStatus.put(channel.getId(), Boolean.TRUE);
                    channelListener.onChannelSelected(channel);
                }
            });
            programView.addChannelButton(button);
        }
    }


    /**
     * The hash map that contains the information about the channel update status.
     * @return the hash map that contains the information about the channel update status.
     */
    public ConcurrentHashMap<Integer, Boolean> getChannelUpdateStatus() {
        return channelUpdateStatus;
    }


    /**
     * That methos set up channel buttons on the UI based on the provided channel
     * types and their associated channels.
     * @param types The set of channel types.
     * @param channelsWithTypes A map of channel types to their associated channels.
     */
    public void setupChannelButtons(HashSet<String> types, HashMap<String,ArrayList<Channel>>channelsWithTypes) {
        this.types = types;
        this.channelsWithTypes = channelsWithTypes;
        addChannelType();
    }


    /**
     * That method is responsible for showing a program's schedules on the GUI.
     * @param schedule the schedule.
     */
    public void showDetailsOfProgram(Schedule schedule){
        programDetails.showProgramDetails(schedule);
    }


    /**
     * That method is responsible for populating the schedule details on the table.
     * where the program details will be shown. At the same time, it will show
     * the channel which program schedule has been updated on the GUI.
     * @param channel the channel.
     * @param schedules the list of schedules.
     */
    public void updateProgramTable(Channel channel, ArrayList<Schedule> schedules) {
        if(SwingUtilities.isEventDispatchThread()){
            populateProgramTable(schedules);
            menuBarView.setSelectedChannelLabel(channel.getChannelName());
        }
    }


    /**
     * That method is responsible for populating the schedule details on table.
     * @param schedules list of schedules.
     */

    private void populateProgramTable(ArrayList<Schedule> schedules) {
        String[] columnNames = {"Program Name", "Start Time", "End Time"};
        Object[][] data = new Object[schedules.size()][3];

        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            data[i][0] = schedule.getProgramName();
            data[i][1] = schedule.getStartTime();
            data[i][2] = schedule.getEndTime();
        }

        programView.getProgramTable().setModel(new DefaultTableModel(data, columnNames));
        programView.getProgramTable().getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
        programView.getProgramTable().getColumnModel().getColumn(0).setCellEditor(
                new ButtonEditor(schedules, channelListener)
        );

        if (schedules.isEmpty()) {
            DefaultTableModel model = (DefaultTableModel) programView.getProgramTable().getModel();
            model.setRowCount(0);
            model.setColumnCount(0);
            model.addColumn("There is no schedule for this program");
        }
    }


    /**
     * when the channel is updated, it will be displayed that the channel has been updated
     * and it will display the time when the channels have been updated.
     */
    public void setChannelUpdatedLabel() {
        LocalDateTime lastUpdatedTime = LocalDateTime.now();
        String formattedTime = "Channels Updated: " + lastUpdatedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        menuBarView.getChannelUpdatedLabel().setText(formattedTime);
    }


    /**
     * when the channel is about to update, it will be displayed on the GUI that the channel
     * is updating.
     */
    public void setChannelUpdatingLabel(){
        menuBarView.getChannelUpdatedLabel().setText("Updating---");
    }


    /**
     * It will be displayed which channel's program schedules have been updated.
     * @param channelName the name of the channel.
     */
    public void setScheduleUpdatedLabel(String channelName) {
        LocalDateTime lastUpdatedTime = LocalDateTime.now();
        String formattedTime = "Schedule Updated: " + " << "+ channelName +" >> "+ lastUpdatedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        menuBarView.getProgramUpdatedLabel().setText( formattedTime );
    }



    /**
     * It will dispaly on the gui that the program schedule is updating.
     */
    public void setScheduleIsUpdatingLabel(){
        menuBarView.getProgramUpdatedLabel().setText("Fetching Schedule ----");
    }



    /**
     * for the testing purpose
     */
    public JLabel getDesctiptiopnLabel() {
        return programDetails.getDescriptionLabel();
    }

}
