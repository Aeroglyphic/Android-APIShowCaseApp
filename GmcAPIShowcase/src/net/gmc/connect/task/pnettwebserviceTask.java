package net.gmc.connect.task;

import android.os.AsyncTask;
import net.gmc.pnettwebservice.pnettwebservices.PNetTServicesSoap;

import java.util.Arrays;
import java.util.LinkedList;

public abstract class pnettwebserviceTask<RESULT> extends AsyncTask<PNetTServicesSoap, Void, RESULT> {

    private final pnettwebserviceTaskOutputReceiver<RESULT> resultReceiver;

    public pnettwebserviceTask(pnettwebserviceTaskOutputReceiver<RESULT> resultReceiver) {
        this.resultReceiver = resultReceiver;
    }

    @Override
    protected final void onPostExecute(RESULT result) {
        resultReceiver.onTaskCompleted(result);
    }

    @Override
    protected final RESULT doInBackground(PNetTServicesSoap... soaps) {
        PNetTServicesSoap pNetTServicesSoap = new LinkedList<PNetTServicesSoap>(Arrays.asList(soaps)).getFirst();
        return onProcess(pNetTServicesSoap);
    }

    protected abstract RESULT onProcess(PNetTServicesSoap servicesSoap);
}
