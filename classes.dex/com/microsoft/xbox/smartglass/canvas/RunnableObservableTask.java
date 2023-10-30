package com.microsoft.xbox.smartglass.canvas;

import android.os.AsyncTask;
import java.util.Observable;

public class RunnableObservableTask extends AsyncTask<Runnable, Void, Observable> {
    protected Observable doInBackground(Runnable... args) {
        Runnable runnable = args[0];
        runnable.run();
        return (Observable) runnable;
    }

    protected void onPostExecute(Observable observable) {
        observable.notifyObservers(this);
    }
}
