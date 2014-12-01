package net.gmc.connect.transport;

import android.util.Base64;

import net.gmc.connect.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jinouts.ws.JinosService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

class HttpTransport implements org.jinouts.transport.HttpTransport {

    static {
        JinosService.registerHttpTransport(new HttpTransport());
    }

    private HttpTransport() {}

    public String sendRequestAndGetRespXML(String soapAction, String reqXMLString, String url) throws IOException {
        HttpPost post = new HttpPost(url);
        StringEntity body = new StringEntity(reqXMLString, HTTP.UTF_8);
        body.setChunked(true);
        post.setEntity(body);
        post.setHeader("Content-Type", "text/xml;charset=" + HTTP.UTF_8);
        post.setHeader("Accept-Charset", HTTP.UTF_8);
        post.setHeader("SOAPAction", soapAction);
        String credentialsBase64 = getBase64String( MainActivity.USER,  MainActivity.PASS);
        post.setHeader("Authorization", "Basic " + credentialsBase64);

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 6000);

        HttpResponse response = new DefaultHttpClient(httpParameters).execute(post);
        int status = response.getStatusLine().getStatusCode();
        if (status != 200 || response.getEntity() == null) {
            throw new HttpResponseException(status, "Invalid response");
        }
        return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
    }

    private String getBase64String(String user, String pass) throws UnsupportedEncodingException {
        String credentials = user + ":" + pass;
        byte[] credentialsData = credentials.getBytes("UTF-8");
        return Base64.encodeToString(credentialsData, Base64.DEFAULT).trim();
    }

    @Override
    public String getDescription() {
        return "default HttpTransport implementation";
    }
}
