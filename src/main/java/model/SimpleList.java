package model;

import java.util.ArrayList;

/**
 * Created by michal on 3.10.2016.
 */
public class SimpleList<T> {
    private ArrayList<T> arrayList;
    private ArrayList<SimpleListFunction> listeners;

    public SimpleList() {
        this.arrayList = new ArrayList<T>();
        this.listeners = new ArrayList<SimpleListFunction>();
    }

    public void addListener(SimpleListFunction function){
        listeners.add(function);
    }

    public void add(T o){
        arrayList.add(o);
        //calling listeners
        for (SimpleListFunction function : listeners) {
            function.print(o);
        }
    }

    public void remove(T o){
        arrayList.remove(o);
        //dalsie veci
    }

    public void remove(int num){
        arrayList.remove(num);
    }

}
