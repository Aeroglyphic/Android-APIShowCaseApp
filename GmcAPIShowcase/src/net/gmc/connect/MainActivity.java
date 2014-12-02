package net.gmc.connect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import net.gmc.connect.task.InputData;
import net.gmc.connect.task.RunJobTask;
import net.gmc.connect.task.pnettwebserviceTaskOutputReceiver;
import net.gmc.pnettwebservice.pnettwebservices.ComRunWorkFlowRes;
import net.gmc.pnettwebservice.pnettwebservices.PNetTServices;
import net.gmc.pnettwebservice.pnettwebservices.PNetTServicesSoap;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends Activity {

    private static final String WSDL_URL = "http://api.gmc.net/mobileapi/PNetTServices.asmx?WSDL";

    public static final String USER = "demo";
    public static final String PASS = "pass";

    private ImageView outputImageView;
    private WebView outputWebView;
    private TextView textErrorLog;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {
            Class.forName("net.gmc.connect.transport.HttpTransport");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void proof(View v) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        TextView txtParamName = (TextView) findViewById(R.id.txtParamName);
        TextView txtParamSurname = (TextView) findViewById(R.id.txtParamSurname);


        setContentView(R.layout.proof);
        outputImageView = (ImageView) findViewById(R.id.outputImage);
        outputWebView = (WebView) findViewById(R.id.outputWeb);
        textErrorLog = (TextView) findViewById(R.id.textErrorLog);

        String engine = (String)v.getTag();
        String name = txtParamName.getText().toString();
        String surname = (String) txtParamSurname.getText().toString();

        InputData inputData = new InputData();
        inputData.setName(name);
        inputData.setSurname(surname);

        try {
            PNetTServicesSoap servicesSoap = new PNetTServices(
                    new URL(WSDL_URL)).getPNetTServicesSoap();
            new RunJobTask(new RunJobResponseHandler(engine),engine,inputData).execute(servicesSoap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void goBack(View v) {
        setContentView(R.layout.main);
    }

    private class RunJobResponseHandler implements pnettwebserviceTaskOutputReceiver<ComRunWorkFlowRes>  {

        private String engine;

        public RunJobResponseHandler(String engine) {
            this.engine = engine;
        }

        @Override
        public void onTaskCompleted(ComRunWorkFlowRes result)  {

            String url  =  result.getSource().getUrl();

            if (result.getErrorCollection() != null) {
                ShowErrorLog(result.getLog());
                return;
            }

            if (engine.equals("Image")) {
                outputImageView.setVisibility(View.VISIBLE);
                new LoadImage().execute(url);
            }

            if (engine.equals("HTML")) {
               outputWebView.setVisibility(View.VISIBLE);
                new LoadHTML().execute(url);
            }

            if (engine.equals("Dynamic Documents")) {
                outputWebView.setVisibility(View.VISIBLE);
                WebSettings webSettings = outputWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                new LoadHTML().execute(url);
            }
        }
    }

    public void ShowErrorLog(String log){
        textErrorLog.setVisibility(View.VISIBLE);
        textErrorLog.setText(log);
    }

    public class LoadHTML extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            try {
                String url = args[0];
                outputWebView.loadUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String dismis) {
            progressDialog.dismiss();
        }
    }

    public class LoadImage extends AsyncTask<String, String, Bitmap> {

        protected Bitmap doInBackground(String... args) {
            Bitmap bitmapImage;
            String url = args[0];

            try {
                bitmapImage = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return bitmapImage;
        }

        protected void onPostExecute(Bitmap bitmapImage) {
            if(bitmapImage != null){
                outputImageView.setImageBitmap(bitmapImage);
            }
            progressDialog.dismiss();
        }
    }
}