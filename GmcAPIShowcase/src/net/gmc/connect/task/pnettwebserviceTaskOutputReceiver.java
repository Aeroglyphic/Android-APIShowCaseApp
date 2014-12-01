package net.gmc.connect.task;

/**
 * Created by Petr Havlena (p.havlena@gmc.net) on 11/25/14.
 */
public interface pnettwebserviceTaskOutputReceiver<T> {
    public void onTaskCompleted(T result);
}
