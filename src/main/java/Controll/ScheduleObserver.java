package Controll;

import Model.Channel;
import Model.Schedule;

import java.util.ArrayList;

public interface ScheduleObserver {
    void scheduleUpdate(Channel channel, ArrayList<Schedule> schedule, boolean isAutoUpdate);
}
