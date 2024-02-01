package Controll;

/**
 * That class has been used within the observer pattern. Those classes which
 * implement this interface will be used as observerble. Its update on specific
 * events will get notified to the observers.
 * @author Gazi Md Rakibul Hasan
 */
public interface ChannelSubject {
    public void registerObserver(ChannelObserver observer);
    public void removeObserver(ChannelObserver observer);
    public void notifyObservers();
}
