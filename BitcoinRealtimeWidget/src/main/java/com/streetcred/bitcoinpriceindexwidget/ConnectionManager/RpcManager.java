package com.streetcred.bitcoinpriceindexwidget.ConnectionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

/**
 * Created by denniskong on 10/30/13.
 */

public class RpcManager {

  private static RpcManager INSTANCE = null;

  public static RpcManager getInstance() {

    if(INSTANCE == null) {
      INSTANCE = new RpcManager();
    }

    return INSTANCE;
  }

  private static enum RequestVerb {
    GET,
    POST,
    PUT,
    DELETE;
  }

  private RpcManager() {

  }

  public JSONObject callGet(Context context, String source, String method) throws IOException, JSONException {

    return call(context, source, method, RequestVerb.GET, null);
  }

  public JSONObject callGet(Context context, String source, String method, Collection<BasicNameValuePair> params) throws IOException, JSONException {

    return call(context, source, method, RequestVerb.GET, params);
  }

  public JSONObject callPost(Context context, String source, String method, Collection<BasicNameValuePair> params) throws IOException, JSONException {

    return call(context, source, method, RequestVerb.POST, params);
  }

  public JSONObject callPut(Context context, String source, String method, Collection<BasicNameValuePair> params) throws IOException, JSONException {

    return call(context, source, method, RequestVerb.PUT, params);
  }

  public JSONObject callDelete(Context context, String source, String method, Collection<BasicNameValuePair> params) throws IOException, JSONException {

    return call(context, source, method, RequestVerb.DELETE, params);
  }

  private JSONObject call(Context context, String source, String method, RequestVerb verb, Collection<BasicNameValuePair> params) throws IOException, JSONException {

    DefaultHttpClient client = new DefaultHttpClient();

    String url = source + method;

    HttpUriRequest request = null;

    if(verb == RequestVerb.POST || verb == RequestVerb.PUT) {

      // Post body is used.
      switch(verb) {
      case POST:
        request = new HttpPost(url);
        break;
      case PUT:
        request = new HttpPut(url);
        break;
      default:
        throw new RuntimeException("RequestVerb not implemented: " + verb);
      }

      List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();

      if(params != null) {
        parametersBody.addAll(params);
      }

      ((HttpEntityEnclosingRequestBase) request).setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));
    } else {

      // URL parameters are used (GET and DELETE).
      if(params != null) {
        List<BasicNameValuePair> paramsList = (params instanceof List<?>) ? (List<BasicNameValuePair>)params : new ArrayList<BasicNameValuePair>(params);
        url = url + "?" + URLEncodedUtils.format(paramsList, "UTF-8");
      }

      if(verb == RequestVerb.GET) {
        request = new HttpGet(url);
      } else if(verb == RequestVerb.DELETE) {
        request = new HttpDelete(url);
      }
    }

    HttpResponse response = client.execute(request);
    int code = response.getStatusLine().getStatusCode();

    if(code != 200) {
      throw new IOException("HTTP response " + code);
    }

    String responseString = EntityUtils.toString(response.getEntity());

    if(responseString.startsWith("[")) {
      // Is an array
      responseString = "{response:" + responseString + "}";
    }

    JSONObject content = new JSONObject(new JSONTokener(responseString));
    return content;
  }
}
