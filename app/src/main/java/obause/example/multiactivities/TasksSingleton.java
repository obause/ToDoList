package obause.example.multiactivities;

import java.util.ArrayList;
import java.util.List;

public class TasksSingleton {
    private static final TasksSingleton SELF = new TasksSingleton();

    private ArrayList<String> listItems = new ArrayList<String>();

    private TasksSingleton() {
        // Don't want anyone else constructing the singleton.
    }

    public static TasksSingleton getInstance() {
        return SELF;
    }

    public ArrayList<String> getTasks() {
        return listItems;
    }
}
