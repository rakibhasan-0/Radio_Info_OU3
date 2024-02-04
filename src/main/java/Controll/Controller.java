package Controll;

import Model.*;
import View.*;
import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


/**
 * It is the controller class it will be responsible to be the bridge between GUI and Modell.
 * @author Gazi Md Rakibul Hasan
 */
public class Controller implements ChannelListener {
    private final MenuBarView menuBarView;
    private final Cache cache;
    private final AtomicReference<Channel> selectedChannel = new AtomicReference<>();
    private Timer automaticUpdateTimer;
    private final UIManager uiManager;
    private final APIManager apiManager;
    private int updateInterval = 60 * 60 * 1000;
    private  HashMap<String,ArrayList<Channel>> channelWithTypeForTesting = new HashMap<String,ArrayList<Channel>>();
    private final ConcurrentHashMap<Integer, Channel> idToChannelMap = new ConcurrentHashMap<Integer, Channel>();



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
        if (selectedChannel.get() != null){
            //System.out.println("Thread name (just clicked): "+Thread.currentThread().getName());
            uiManager.setScheduleIsUpdatingLabel();
            cache.clearCacheForAChannel(selectedChannel.get());
            menuBarView.removeUpdateScheduleListener(); // to prevent unecessary update request.
            apiManager.fetchScheduleForChannel(selectedChannel.get(), false);
            // resetAutomaticUpdates();
        }else {
            //System.out.println("Thread name to show option: "+Thread.currentThread().getName());
            JOptionPane.showMessageDialog(null, "Please select a channel first before updating the schedule.");
        }
    }


    /**
     * It updates the channel by fetching data from the API and reset, timer and
     * it shows the user that update is occurring.
     */
    private void updateChannels() {
        menuBarView.removeUpdateChannelListener(); // to prevent unecessary update request.
        uiManager.setChannelUpdatingLabel();
        apiManager.fetchChannelDataFromAPI();
        updateCache();
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
            updateChannels();
        });
        automaticUpdateTimer.start();
    }

    


    /**
     * That method is responsible for updating the cache. It will fetch the schedule for each channel that exits in the cache.
     */
    private void updateCache(){
        List<Integer> channelsToUpdate = new ArrayList<>(cache.getCache().keySet()); // get keys in a list.
        channelsToUpdate.forEach(channel -> {
            //cache.clearCacheForAChannel(channel);
            apiManager.fetchScheduleForChannel(idToChannelMap.get(channel), true);
        });
    }


    /**
     * That method is responsible for adding channels based on its type on the GUI. It clears cache
     * and resets the timer for the automatic update.
     * @param types it represents the categories of the channels.
     * @param channelWithType A map that contains categories and its corresponding channels.
     */
    public void updatedChannelsAndCategory(HashSet<String> types, HashMap<String,ArrayList<Channel>> channelWithType) {
        channelWithTypeForTesting = channelWithType;
        //cache.clearCache();
        SwingUtilities.invokeLater(() -> {
            if(!types.isEmpty()){
                uiManager.setupChannelButtons(types,channelWithType);
                uiManager.setChannelUpdatedLabel();
                resetAutomaticUpdates();
            }
            menuBarView.updateChannelListener();
        });
    }


    /**
     * get chennels, it has been used for the testing
     */
    public HashMap<String, ArrayList<Channel>> getChannels(){
        return this.channelWithTypeForTesting;
    }


    /**
     * That method will cache the given channel and its program schedules.
     * @param channel the channel.
     * @param schedules the given channel's programs schedules.
     */
    public synchronized void processChannelAndScheduleForAutomaticUpdate(Channel channel, ArrayList<Schedule> schedules){
        cache.addSchedules(channel, schedules);
        SwingUtilities.invokeLater(uiManager::setCacheIsUpdatingLabel);
        System.out.println("For channel Automatic : " + channel.getChannelName());
        //uiManager.getChannelUpdateStatus().put(channel.getId(), Boolean.FALSE);
        //System.out.println("[" + Thread.currentThread().getName() + " at " + System.currentTimeMillis() + "] for channel Automatic : " + channel.getChannelName());
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
        System.out.println(" for channel not Automatic : "+channel.getChannelName());
        //System.out.println("[" + Thread.currentThread().getName() + " at " + System.currentTimeMillis() + "] for channel manuel : " + channel.getChannelName());
        //System.out.println("channel name: "+channel.getChannelName()+ "status: "  + uiManager.getChannelUpdateStatus().get(channel.getId()));
        uiManager.getChannelUpdateStatus().put(channel.getId(), Boolean.FALSE);
        //System.out.println("channel name: "+channel.getChannelName()+ "status after update: "  + uiManager.getChannelUpdateStatus().get(channel.getId()));
        cache.addSchedules(channel, schedules);
        SwingUtilities.invokeLater(() -> {
            selectedChannel.set(channel);
            uiManager.updateProgramTable(channel, schedules);
            uiManager.setScheduleUpdatedLabel(channel.getChannelName());
            //menuBarView.updateScheduleListener();'
            menuBarView.updateScheduleListener();
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
        idToChannelMap.put(channel.getId(), channel);
        ArrayList<Schedule> schedules = cache.getSchedules(channel);
        selectedChannel.set(channel);
        //System.out.println("[" + Thread.currentThread().getName() + " at " + System.currentTimeMillis() + "] for selected Channel : " + selectedChannel.get().getChannelName());
        if (schedules != null) {
            if(SwingUtilities.isEventDispatchThread()){
               // System.out.println("This code block is running on the EDT. if the schedule is not in the cache");
                uiManager.getChannelUpdateStatus().put(channel.getId(), Boolean.FALSE);
                SwingUtilities.invokeLater(() -> {
                    uiManager.updateProgramTable(channel, schedules);
                });
            }
        } else {
//            if(SwingUtilities.isEventDispatchThread()){
//                System.out.println("This label is about to update on the EDT");
//            }
            SwingUtilities.invokeLater(uiManager::setScheduleIsUpdatingLabel);
            apiManager.fetchScheduleForChannel(selectedChannel.get(), false);
        }

    }


    /**
     * Whenever a program from a specific channel has been clicked, then it will show more
     * information of the program on the gui.
     * @param schedule the schedule to display.
     */
    @Override
    public void onButtonClick(Schedule schedule) {
        //System.out.println("Check the schedule thread "+Thread.currentThread().getName());
        uiManager.showDetailsOfProgram(schedule);
//        if(SwingUtilities.isEventDispatchThread()){
//            System.out.println("This code is running on the EDT");
//        }else{
//            System.out.println("This code is NOT running on the EDT, when the user clicks a schdule to get the details of the schedule of a program.");
//        }
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