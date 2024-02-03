package Model;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * That class used to cache the information of the channel and its corresponding program's
 * schedules.
 * @author Gazi Md Rakibul Hasan
 **/

public class Cache {
    private final ConcurrentHashMap <Integer, ArrayList<Schedule>> caches;

    /**
     * The constructor of the Cache class, we will use the concurrent hash map in order to
     * prevent multithread problems.
     */
    public Cache() {
        this.caches = new ConcurrentHashMap<>();
    }

    /**
     * It adds the programs schedules corresponding to the given channel.
     * @param channel the channel.
     * @param schedules list of schedules to the given channel.
     */
    public void addSchedules(Channel channel, ArrayList<Schedule> schedules) {
        caches.put(channel.getId(), schedules);
    }

    /**
     * It get the schedule for the given channel.
     * @param channel the channel
     * @return list of programs schedules to the given channel.
     */
    public ArrayList<Schedule> getSchedules(Channel channel) {
        return caches.get(channel.getId());
    }

    /**
     * If checks if the given channel's list of programs schedules mapped
     * in the hash map or what?
     * @param channel the given channel.
     * @return true if the given channel's program schedules mapped in the hash map,
     * false otherwise.
     */
    private boolean hasChannel(Channel channel) {
        return caches.containsKey(channel.getId());
    }

    /**
     * It clears the hash map that contains channels and its programs schedules.
     */
    public void clearCache() {
        caches.clear();
    }


    /**
     * It clears the specified channel's programs schedules from the hash map.
     * @param channel the given channel.
     */
    public void clearCacheForAChannel(Channel channel) {
        caches.remove(channel.getId());
    }

    /**
     * It returns the hash map that contains channels and its programs schedules.
     * @return the hash map aka cahce.
     */
    public ConcurrentHashMap<Integer, ArrayList<Schedule>> getCache(){
        return caches;
    }


}
