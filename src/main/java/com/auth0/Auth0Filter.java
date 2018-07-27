package com.auth0;

import java.io.IOException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;

/**
 * Filter class to check if a valid session exists. This will be true if the User Id is present.
 */
public class Auth0Filter implements Filter {

    private static final int UNVERIFIED_EMAIL_HTTP_STATUS = 491;
    
    private JWTVerifier verifier = null;
    
    @Override
    public void init(FilterConfig config) throws ServletException {

//      verifier = buildHS256Verifier(config);
      verifier = buildRS256Verifier(config);
    }

    /**
     *
     * @param request  the received request
     * @param response the response to send
     * @param next     the next filter chain
     **/
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
//        System.out.println("authorization: "+req.getHeader("Authorization"));
        String token = getBearerToken(req);
        if( token != null ) {
          try {
              DecodedJWT jwt = verifier.verify(token);
//              System.out.println("JWT Payload: ");
//              for(Entry<String,Claim> claim : jwt.getClaims().entrySet()) {
//                System.out.println(claim.getKey()+": "+claim.getValue().asString());
//              }
              /***
               *  Sample payload of claims:
               {
                  "nickname": "john",
                  "name": "john@email.com",
                  "picture": "https://s.gravatar.com/avatar/b631bc.png",
                  "updated_at": "2018-07-25T22:05:00.639Z",
                  "email": "john@email.com",
                  "email_verified": true,
                  "iss": "https://AUTH0_DOMAIN/",
                  "sub": "auth0|USER_ID",
                  "aud": "CLIENT_ID",
                  "iat": 1532556300,
                  "exp": 1532592300,
                  "at_hash": "EphqANudwOsYZBRyJqmggg",
                  "nonce": "K.0wfTg4at7L-duCTrgk9xYo0wNDvHgk"
                }
               */
              request.setAttribute("user", jwt.getClaim("sub").asString());
              Claim email_verified = jwt.getClaim("email_verified");
              if( email_verified == null
              ||  !email_verified.asBoolean() ) {
                System.err.println(jwt.getClaim("sub").asString() + " email not verified");
                res.sendError(UNVERIFIED_EMAIL_HTTP_STATUS, "Unverified email");
                return;
              }
              next.doFilter(request, response);
              return;
          } catch (JWTVerificationException jwtvE){
              //Invalid signature/claims
            System.err.println(jwtvE.getMessage());
            jwtvE.printStackTrace();
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized Access");
            return;
          }

        } else {
          res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized Access");
        }
    }

    @Override
    public void destroy() {
    }
    
    private JWTVerifier buildRS256Verifier(FilterConfig config) {
      String AUTH0_DOMAIN = config.getServletContext().getInitParameter("com.auth0.domain");
      
      try {
        JwkProvider jwkprovider = new UrlJwkProvider("https://"+AUTH0_DOMAIN+"/");
        
        RSAKeyProvider keyProvider = new RSAKeyProvider() {
          @Override
          public RSAPublicKey getPublicKeyById(String kid) {
              //Received 'kid' value might be null if it wasn't defined in the Token's header
            try {
              PublicKey publicKey = jwkprovider.get(kid).getPublicKey();
              return (RSAPublicKey) publicKey;
            } catch(JwkException jwkE) {
              jwkE.printStackTrace(); 
              return null;
            }
          }

          @Override
          public RSAPrivateKey getPrivateKey() {
              return null;
          }

          @Override
          public String getPrivateKeyId() {
              return null;
          }
        };

        Algorithm algorithm = Algorithm.RSA256(keyProvider);
        return JWT.require(algorithm)
            .withIssuer("https://"+AUTH0_DOMAIN+"/")
            .build();
      
      } catch(Exception jwkE) {
        System.err.println("Could not Instantiate RS256 JWTVerifier");
        jwkE.printStackTrace();
      }
      
      return null;
    }
    
    private JWTVerifier buildHS256Verifier(FilterConfig config) {
      String AUTH0_DOMAIN = config.getServletContext().getInitParameter("com.auth0.domain");
      String CLIENT_SECRET = config.getServletContext().getInitParameter("com.auth0.clientSecret");
      
      // HS256 verifier
      try {
        Algorithm algorithm = Algorithm.HMAC256(CLIENT_SECRET);
        return JWT.require(algorithm)
            .withIssuer("https://"+AUTH0_DOMAIN+"/")
            .build();
      } catch(Exception e) {
        System.err.println("Could not Instantiate HS256 JWTVerifier");
        e.printStackTrace();
      }
      return null;      
    }
    
    private String getBearerToken(HttpServletRequest req) {
      String token = req.getHeader("Authorization");
      if( token != null
      &&  token.startsWith("Bearer ") ) {
        return token.substring(7);
      } else {
        return null;
      }
    }
}