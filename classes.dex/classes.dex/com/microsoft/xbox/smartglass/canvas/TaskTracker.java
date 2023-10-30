package com.microsoft.xbox.smartglass.canvas;

import android.os.AsyncTask.Status;
import java.util.ArrayList;
import java.util.List;

public class TaskTracker {
    private List<RunnableObservableTask> _tasks = new ArrayList();

    public void addTask(RunnableObservableTask task) {
        this._tasks.add(task);
    }

    public void removeTask(RunnableObservableTask task) {
        this._tasks.remove(task);
    }

    public void cancelAllTasks() {
        synchronized (this._tasks) {
            for (RunnableObservableTask task : this._tasks) {
                if (task.getStatus() != Status.FINISHED) {
                    task.cancel(true);
                }
            }
            this._tasks.clear();
        }
    }
}
