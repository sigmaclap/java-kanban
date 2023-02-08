package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.historyTaskManager.HistoryManager;
import manager.historyTaskManager.InMemoryHistoryManager;
import server.HttpTaskManager;
import server.utils.LocalDateTimeAdapter;

import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
