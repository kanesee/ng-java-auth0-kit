package com.auth0;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Base64;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

public class RestUtil {

  public static HttpResponse get(String url) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();        
    HttpGet get = new HttpGet(url);

    HttpResponse response = client.execute(get);

    return response;
  }
  
  /**
   * Get with basic auth
   */
  public static HttpResponse get(String url, String user, String pass) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    HttpGet get = new HttpGet(url);
    
    String encoding = Base64.getEncoder().encodeToString( (user+":"+pass).getBytes() );
    get.setHeader("Authorization", "Basic " + encoding);
    
    HttpResponse response = client.execute(get);

    return response;
  }
  
  /**
   * Converts object into json and posts it
   * @param url
   * @param obj
   * @return
   */
  public static HttpResponse post(String url, Object obj) throws IOException {
    return post(url, null, obj);
  }
  
  public static HttpResponse post(String url, Map<String,String> headers, Object obj) throws IOException {
    Gson gson = new Gson();
    String jsonStr = gson.toJson(obj);
    return post(url, headers, jsonStr);
  }
  
  
  public static HttpResponse post(String url, String json) throws IOException {
    return post(url, null, json);
  }
  
  public static HttpResponse post(String url, Map<String,String> headers, String json) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();        
    HttpPost post = new HttpPost(url);
    
    if( headers != null ) {
      for(String header : headers.keySet()) {
        post.setHeader(header, headers.get(header));
      }
    }

    if( json != null ) {
      post.setEntity(new StringEntity(json, 
                     ContentType.create("application/json")));
    }
    HttpResponse response = client.execute(post);

    return response;
  }
  
  public static String parseResponseAsString(HttpResponse response) throws IOException {
    StringWriter writer = new StringWriter();
    IOUtils.copy(response.getEntity().getContent(), writer);
    return writer.toString();
  }
  
  public static JSONObject parseResponseAsJsonObject(HttpResponse response) throws IOException {
    String respStr = parseResponseAsString(response);
    return new JSONObject(respStr);
  }
  
  public static JSONArray parseResponseAsJsonArray(HttpResponse response) throws IOException {
    String respStr = parseResponseAsString(response);
    return new JSONArray(respStr);
  }
}
