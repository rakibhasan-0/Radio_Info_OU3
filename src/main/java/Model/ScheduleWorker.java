package Model;
import Controll.ScheduleObserver;
import Controll.ScheduleSubject;
import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ScheduleWorker extends SwingWorker<ArrayList<Schedule>,Void> implements ScheduleSubject {
    private ArrayList<Schedule> schedules;
    private final ArrayList<ScheduleObserver> observers;
    private final Channel channel;
    private final boolean isAutoUpdate;

    public ScheduleWorker(Channel channel, boolean isAutoUpdate){
        this.channel = channel;
        observers = new ArrayList<ScheduleObserver>();
        this.schedules = new ArrayList<Schedule>();
        this.isAutoUpdate = isAutoUpdate;
    }

    @Override
    protected ArrayList<Schedule> doInBackground()  {
        System.out.println("Thread name" + Thread.currentThread().getName());
        DataFetchStrategy<Schedule> parser = new ScheduleParser(channel);
        return parser.fetchData();
    }

    @Override
    protected void done() {
        try {
            System.out.println("done() Thread name: " + Thread.currentThread().getName()); // This should print the EDT thread name.
            schedules = get();
            notifyObservers();
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null, "Interrupted: " + "Could not get schedules");
        } catch (ExecutionException e) {
           JOptionPane.showMessageDialog(null, "Execution: " + "Could not get schedules");
        }

    }

    @Override
    public void registerObserver(ScheduleObserver o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(ScheduleObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (ScheduleObserver o : observers) {
            o.scheduleUpdate(channel,schedules, isAutoUpdate);
        }
    }
}
