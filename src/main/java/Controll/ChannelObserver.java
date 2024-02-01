package Controll;

import Model.Channel;
import Model.Schedule;

import java.util.ArrayList;

/**
 * This class represents the obsevers, on a specific event all observers will get notified.
 * In that case, it will get notified when channels and programs schedules for the specific
 * channel get updated/fetched from the API.
 */
public interface ChannelObserver {
    void channelUpdate(ArrayList<Channel> channels);

}

