package net.gmc.connect.task;

import net.gmc.pnettwebservice.pnettwebservices.ComCommand;
import net.gmc.pnettwebservice.pnettwebservices.ComDataDefinition;
import net.gmc.pnettwebservice.pnettwebservices.ComResponseDefinition;
import net.gmc.pnettwebservice.pnettwebservices.ComRunWorkFlowReq;
import net.gmc.pnettwebservice.pnettwebservices.ComRunWorkFlowRes;
import net.gmc.pnettwebservice.pnettwebservices.ComSource;
import net.gmc.pnettwebservice.pnettwebservices.ComWorkFlowDefinition;
import net.gmc.pnettwebservice.pnettwebservices.PNetTServicesSoap;
import net.gmc.pnettwebservice.pnettwebservices.SimResponseType;

public class RunJobTask extends pnettwebserviceTask<ComRunWorkFlowRes> {

    private String SIMPLE_WFD = "vcs:/data/GmcApiShowCase/wfd/mobileProof.wfd";
    private String DYNAMIC_WFD = "vcs:/data/GmcApiShowCase/wfd/mobileProofDynamicDocument.wfd";

    private String engine;
    private InputData inputData;

    public RunJobTask(pnettwebserviceTaskOutputReceiver<ComRunWorkFlowRes> resultReceiver, String engine, InputData inputData) {
        super(resultReceiver);
        this.engine = engine;
        this.inputData = inputData;
    }

    @Override
    protected ComRunWorkFlowRes onProcess(PNetTServicesSoap servicesSoap) {
        String corrId;

        ComRunWorkFlowReq workFlowReq = new ComRunWorkFlowReq();
        ComWorkFlowDefinition workFlowDefinition = new ComWorkFlowDefinition();
        workFlowReq.setMsgVersion(700);
        if (engine.equals("Dynamic Documents"))
            corrId = DYNAMIC_WFD;
         else
            corrId = SIMPLE_WFD;
        ComSource comSource = new ComSource();
        comSource.setCorrelationId(corrId);
        workFlowDefinition.setSource(comSource);
        workFlowReq.setWorkFlowDefinition(workFlowDefinition);

        ComDataDefinition dataDefinition = new ComDataDefinition();
        dataDefinition.setDataModuleName("XMLDataInput");
        ComSource comSourceDataDefinition = new ComSource();

        ComSource.XmlData xmlData = new ComSource.XmlData();
        xmlData.setAny(inputData);

        comSourceDataDefinition.setXmlData(xmlData);
        dataDefinition.setSource(comSourceDataDefinition);
        workFlowReq.getDataDefinition().add(dataDefinition);

        ComResponseDefinition comResponseDefinition = new ComResponseDefinition();
        comResponseDefinition.setSynchronous(true);
        comResponseDefinition.setAuthenticateUrl(true);
        comResponseDefinition.getResponseType().add(SimResponseType.T_TEMPORARY_URL);
        workFlowReq.setResponseDefinition(comResponseDefinition);

        ComCommand commandEngine = new ComCommand();
        commandEngine.setCommandName("-e");
        commandEngine.setCommandValue(engine);
        workFlowReq.getCommand().add(commandEngine);

        ComCommand commandOutput = new ComCommand();
        commandOutput.setCommandName("-o");
        commandOutput.setCommandValue("OutputMobile");
        workFlowReq.getCommand().add(commandOutput);

        return servicesSoap.runJob(workFlowReq);
    }
}
