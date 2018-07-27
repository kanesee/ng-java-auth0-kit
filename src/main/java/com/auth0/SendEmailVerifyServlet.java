package com.auth0;


import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import com.inferlink.common.rest.RestUtil;

public class SendEmailVerifyServlet extends HttpServlet {

    private AuthenticationController authenticationController;
    private String domain;
    private String client_id;
    private String api_client_id;
    private String api_client_secret;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        domain = config.getServletContext().getInitParameter("com.auth0.domain");
        client_id = config.getServletContext().getInitParameter("com.auth0.clientId");
        api_client_id = config.getServletContext().getInitParameter("com.auth0.api_clientId");
        api_client_secret = config.getServletContext().getInitParameter("com.auth0.api_client_secret");
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
      String user_id = req.getParameter("userid");
      
      String access_token = getMgmtToken();
      sendEmailVerification(access_token, user_id);
      
      res.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private String getMgmtToken() {
      String url = "https://"+domain+"/oauth/token";
      HashMap<String,String> data = new HashMap<String,String>();
      data.put("grant_type", "client_credentials");
      data.put("client_id", api_client_id);
      data.put("client_secret", api_client_secret);
      data.put("audience", "https://"+domain+"/api/v2/");
      try {
        HttpResponse resp = RestUtil.post(url, data);
        JSONObject json = RestUtil.parseResponseAsJsonObject(resp);
        return json.getString("access_token");
      } catch(IOException ioE) {
        ioE.printStackTrace();
      }
      return null;
    }
    
    private void sendEmailVerification(String access_token, String user_id) {
      String url = "https://"+domain+"/api/v2/jobs/verification-email";
      HashMap<String,String> headers = new HashMap<String,String>();
      headers.put("Authorization", "Bearer "+access_token);
      
      HashMap<String,String> data = new HashMap<String,String>();
      data.put("user_id", user_id);
      data.put("client_id", client_id); // this will determine which application will be sending the email
      try {
        HttpResponse resp = RestUtil.post(url, headers, data);
      } catch(IOException ioE) {
        ioE.printStackTrace();
      }
    }

    
    public static void main(String[] args) {
      SendEmailVerifyServlet servlet = new SendEmailVerifyServlet();
      servlet.domain = "inferlink.auth0.com";
      servlet.client_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
      servlet.api_client_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
      servlet.api_client_secret = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
      
      String user_id = "auth0|5adfa2fa41aacd1daa8a152f";
      String access_token = servlet.getMgmtToken();
      servlet.sendEmailVerification(access_token, user_id);
          
    }
}
