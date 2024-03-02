package ir.map.socialnetworkapp.Utils;

public interface Observable {

    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();

}
