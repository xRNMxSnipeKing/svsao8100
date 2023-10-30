package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.network.XLEThreadPool;

public abstract class XLEAsyncTask<Result> {
    private Runnable doBackgroundAndPostExecuteRunnable = null;
    private XLEThreadPool threadPool = null;

    protected abstract Result doInBackground();

    protected abstract void onPostExecute(Result result);

    protected abstract void onPreExecute();

    public XLEAsyncTask(XLEThreadPool threadPool) {
        this.threadPool = threadPool;
        this.doBackgroundAndPostExecuteRunnable = new Runnable() {
            public void run() {
                final Result r = XLEAsyncTask.this.doInBackground();
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        XLEAsyncTask.this.onPostExecute(r);
                    }
                });
            }
        };
    }

    public void execute() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        onPreExecute();
        this.threadPool.run(this.doBackgroundAndPostExecuteRunnable);
    }
}
