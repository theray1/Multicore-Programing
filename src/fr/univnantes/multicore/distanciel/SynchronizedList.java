package fr.univnantes.multicore.distanciel;

import java.util.ArrayList;
import java.util.List;

public class SynchronizedList<T> {

    List<T> list = new ArrayList<T>();
    final Object lock = new Object();

    public void add(T o) {
        synchronized (lock){
            list.add(o);
        }
    }

    public int size() {
        int val;
        synchronized (lock){
            val = list.size();
        }

        return val;
    }

    public T get(int i) {
        T val;
        synchronized (lock){
            val = list.get(i);
        }

        return val;
    }
}
