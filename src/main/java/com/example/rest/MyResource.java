package com.example.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path ("/myresource")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MyResource {
  

  @GET
  @Path("/user")
  public String getUser(@Context HttpServletRequest req) {
    try {
      String user = (String) req.getAttribute("user");
      if( user != null ) {
        return "{\"user\":\""+user+"\"}";
      } else {
        return null;
      }
    } catch(Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }
  
  
}
