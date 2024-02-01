package Controll;

import Model.*;
import View.*;
import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * It is the controller class it will be responsible to be the bridge between GUI and Modell.
 * @author Gazi Md Rakibul Hasan
 */
public class Controller implements ChannelListener {
    private final MenuBarView menuBarView;
    private final Cache cache;
    private Channel selectedChannel;
    private Timer automaticUpdateTimer;
    private final UIManager uiManager;
    private final APIManager apiManager;
    private int updateInterval = 60 * 1000;
    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private  HashMap<String,ArrayList<Channel>> channelWithTypeForTesting = new HashMap<String,ArrayList<Channel>>();


    /**
     * Constructor of the controller class. It takes two GUI classes as parameters and instantiates
     * the cache class and API manager class too.
     * @param menuBarView the menu view which holds information which related to the menu bar.
     * @param programView it holds gui information about the program view and channel view on the gui.
     */
    public Controller(MenuBarView menuBarView, ProgramView programView) {
        this.menuBarView = menuBarView;
        this.uiManager = new UIManager(programView, menuBarView, this);
        this.cache = new Cache();
        apiManager = new APIManager(this);
        apiManager.fetchChannelDataFromAPI();
        setupMenuListeners();
    }


    /**
     * That method is responsible for update the schedules of the selected channel. When the update
     * occurs, it makes sure that it clears the cache for the selected channel, reset timer and fetch
     * the schedule from API
     */
    private void UpdateSchedule() {
        if (selectedChannel != null) {
            uiManager.setScheduleIsUpdatingLabel();
            cache.clearCacheForAChannel(selectedChannel);
            apiManager.fetchScheduleForChannel(selectedChannel, false);
            // resetAutomaticUpdates();
        }else {
            JOptionPane.showMessageDialog(null, "Please select a channel first before updating the schedule.");
        }
    }


    /**
     * It updates the channel by fetching data from the API and reset, timer and
     * it shows the user that update is occurring.
     */
    private void updateChannels() {
        uiManager.setChannelUpdatingLabel();
        apiManager.fetchChannelDataFromAPI();
        // in that case the cache will get updated too.
        updateCache();
        //resetAutomaticUpdates();
    }


    /**
     * That method responisble for updating channels and a selected channel's programs schedules.
     */
    private void setupMenuListeners() {
        menuBarView.addUpdateChannelListener(e -> {
            updateChannels();
        });

        menuBarView.addUpdateScheduleListener(e -> {
            UpdateSchedule();
        });
    }

    
    /**
     * That method resets the timer responsible for automatic updates.
     * If an existing timer is running, it is stopped before a new update cycle is started.
     */
    private void resetAutomaticUpdates() {
        if (automaticUpdateTimer != null && automaticUpdateTimer.isRunning()) {
            automaticUpdateTimer.stop();
        }
        startAutomaticUpdates();
    }


    /**
     * That method initialises and starts the time for the automatic update
     * channel and program schedules. This update occurs every hour by clearing
     * cache and fetches data from the API.
     */
    public void startAutomaticUpdates() {
        automaticUpdateTimer = new Timer(updateInterval, e -> {
            menuBarView.setCurrentTimeLabel(LocalDateTime.now());
            // cache.clearCache(); // we will not clear all the data.
            activeTasks.set(0);
            System.out.println("Initial Active tasks: "+activeTasks);
            updateCache();
        });
        automaticUpdateTimer.start();
    }

    


    /**
     * That method is responsible for updating the cache. It will fetch the schedule for each channel that exits in the cache.
     */
    private void updateCache(){
        List<Channel> channelsToUpdate = new ArrayList<>(cache.getCache().keySet()); // get keys in a list.
        if(channelsToUpdate.isEmpty()){
            // it means the cache is empty currently
            return;
        }
        uiManager.setCacheIsUpdatingLabel();
        channelsToUpdate.forEach(channel -> {
            //cache.clearCacheForAChannel(channel);
            apiManager.fetchScheduleForChannel(channel, true);
            activeTasks.incrementAndGet();
        });
        System.out.println("Current Active tasks: "+activeTasks);
    }


    /**
     * That method is responsible for adding channels based on its type on the GUI. It clears cache
     * and resets the timer for the automatic update.
     * @param types it represents the categories of the channels.
     * @param channelWithType A map that contains categories and its corresponding channels.
     */
    public void updatedChannels (HashSet<String> types, HashMap<String,ArrayList<Channel>> channelWithType) {
        channelWithTypeForTesting = channelWithType;
        //cache.clearCache();
        SwingUtilities.invokeLater(() -> {
            if(!types.isEmpty()){
                uiManager.setupChannelButtons(types,channelWithType);
                uiManager.setChannelUpdatedLabel();
                resetAutomaticUpdates();
            }
        });
    }


    /**
     * get chennels, it has been used for the testing
     */
    public HashMap<String, ArrayList<Channel>> getChannels(){
        return this.channelWithTypeForTesting;
    }

    // TODO: check for the shareed resources.


    /**
     * That method will cache the given channel and its program schedules.
     * @param channel the channel.
     * @param schedules the given channel's programs schedules.
     */
    public void processChannelAndScheduleForAutomaticUpdate(Channel channel, ArrayList<Schedule> schedules){
        // I will not use synchronize method here. Since cache is a concurrent hashmap, it will be thread safe.
        cache.addSchedules(channel, schedules);
        System.out.println("Active tasks: "+activeTasks);
        if(activeTasks.decrementAndGet() == 0){
            SwingUtilities.invokeLater(uiManager::setCacheIsUpdatedLabel);
        }
        System.out.println("Active tasks after decrementing : "+activeTasks+ " for channel: "+channel.getChannelName());
    }


    /**
     * That method will cache the given channel and its program schedules.
     * It will be responsible for updating the GUI by showing those channels on UI.
     * We made that method as synchronized so that we will get one thread's work done at
     * a time.
     * @param channel the channel.
     * @param schedules the given channel's programs schedules.
     */
    public synchronized void processChannelAndSchedule(Channel channel, ArrayList<Schedule> schedules) {
        cache.addSchedules(channel, schedules);
        SwingUtilities.invokeLater(() -> {
            selectedChannel  = channel;
            uiManager.updateProgramTable(channel, schedules);
            uiManager.setScheduleUpdatedLabel(channel.getChannelName());
        });
    }


    /**
     *  It responsible for updating the GUI by showing the selected channel's programs schedules
     *  on the GUI. It tries to utilize the cache. If the information of the given channel is not
     *  cached, then it will fetch data from the API.
     *  @param channel the selected channel.
     */
    @Override
    public void onChannelSelected(Channel channel) {
        ArrayList<Schedule> schedules = cache.getSchedules(channel);
        selectedChannel = channel;
        if (schedules != null) {
            SwingUtilities.invokeLater(() -> {
                uiManager.updateProgramTable(channel, schedules);
            });
        }else {
            SwingUtilities.invokeLater(uiManager::setScheduleIsUpdatingLabel);
            apiManager.fetchScheduleForChannel(selectedChannel, false);
        }
    }


    /**
     * Whenever a program from a specific channel has been clicked, then it will show more
     * information of the program on the gui.
     * @param schedule the schedule to display.
     */
    @Override
    public void onButtonClick(Schedule schedule) {
        uiManager.showDetailsOfProgram(schedule);
        //uiManager.setScheduleUpdatedLabel();
    }


    /**
     * for the testing purposes
     */

    public JLabel getDescriptionLabelFromTable(){
        return uiManager.getDesctiptiopnLabel();
    }

    /**
     * for the testing purposes
     */
    public Cache getCache(){
        return this.cache;
    }


    /**
     *  for the testing purposes
     */
    public void setUpdateInterval(int intervalMillis) {
        this.updateInterval = intervalMillis;
    }

    /**
     * for the testing purposes
     */
    public APIManager getApiManager(){
        return this.apiManager;
    }



}