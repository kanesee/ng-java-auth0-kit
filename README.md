angular frontend, java/servlet backend auth0 template

This is a template for building a generic tomcat webapp with auth0 and angular frontend. It uses mvn (pom.xml) to build a .war file that you can drop into tomcat/webapp folder. It uses Jersey for the REST apis

Here's are the things you need to change to adapt it to your new project.

1. Modify pom.xml
  a) Change groupId, artificatId, and version for your project
  b) In the build/plugins section, change the <warName> from mywebapp to whatever you want your webapp to be called. The .war file will also be named after this.
  b) Add your <dependency>
2. Modify src/main/webapp/WEB-INF/web.xml
  a) Under the "JerseyREST" servlet, add packages that will be serving up your APIs in the <param-value> of the "jersey.config.server.provider.packages" param-name section
  b) Optionally, change the servlet-mapping for these APIs. By default, the <url-pattern> is "/api/*" which means your api's will be served at http://SERVER/api/...
3. Add your classes under src/main/java
4. Add any property files or other resources you want to be in the classpath in src/main/resources
5. In src/main/webapp/index.html, change <base href="...">
6. In src/main/webapp/ng/home/controller.js, change the api call to match the webapp name you selected above. $http.get('...')

**Auth0**
There are some auth0 templates. To use it, do the following:
1) In web.xml, uncomment the auth0 section
  a) Modify the Auth0Filter to match your <url-pattern> from step 2b above. Or leave as "/api/* " as default
  b) Change the context-param to match your auth0 app settings
2) In pom.xml, make sure to uncomment the auth0 dependencies
3) Unzip src/main/java/com/auth0.zip (you may need to make some changes there)
4) In src/main/webapp/assets/auth0/auth0-variables.js, modify the auth0 variables as appropriate

When you run "mvn package", it will generate a .war file under target folder.
