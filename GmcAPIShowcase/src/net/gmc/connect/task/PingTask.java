package net.gmc.connect.task;

import net.gmc.pnettwebservice.pnettwebservices.PNetTServicesSoap;

public class PingTask extends pnettwebserviceTask<String> {

    public PingTask(pnettwebserviceTaskOutputReceiver<String> resultReceiver) {
        super(resultReceiver);
    }

    @Override
    protected String onProcess(PNetTServicesSoap servicesSoap) {
        return servicesSoap.ping();
    }
}
