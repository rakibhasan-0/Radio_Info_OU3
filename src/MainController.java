import Controll.Observer;
import Model.Channel;
import Model.Schedule;
import Model.ScheduleWorker;
import Model.XMLParserWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainController implements Observer {
    private XMLParserWorker xmlParserWorker;
    private volatile boolean allTasksCompleted = false;


    public MainController() {
        xmlParserWorker = new XMLParserWorker();
        xmlParserWorker.registerObserver(this);
        xmlParserWorker.execute();

    }



    @Override
    public void channelUpdate(HashSet<String> channelType, HashMap<String, ArrayList<Channel>> channelsWithtypes) {
        for (String channel: channelType){
            ArrayList<Channel> channels = channelsWithtypes.get(channel);
            for(Channel radioChannel: channels){
                System.out.println("Channel"+radioChannel.getChannelName());
                ScheduleWorker scheduleWorker = new ScheduleWorker(radioChannel);
                scheduleWorker.registerObserver(this);
                scheduleWorker.execute();
            }
        }

       allTasksCompleted = true;

    }


    public boolean isFinished() {
        return allTasksCompleted;
    }

    @Override
    public void scheduleUpdate(Channel channel, ArrayList<Schedule> schedules) {
        System.out.println("Schedule for Channel: " + channel.getChannelName());
        for (Schedule schedule : schedules) {
            System.out.println("Program Name: " + schedule.getProgramName());
            System.out.println("Start Time: " + schedule.getStartTime());
            System.out.println("End Time: " + schedule.getEndTime());
            System.out.println("Description: " + schedule.getDescription());
            System.out.println();
        }
    }

    public static void main(String[] args) {

        MainController mainController = new MainController();
        while (!mainController.isFinished()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupted, exiting the application.");
                break;
            }
        }

        System.out.println("All tasks are completed, exiting the application.");
    }

}


