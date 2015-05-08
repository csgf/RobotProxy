/**************************************************************************
Copyright (c) 2011-2015: 
Istituto Nazionale di Fisica Nucleare (INFN), Italy
Consorzio COMETA (COMETA), Italy

See http://www.infn.it and http://www.consorzio-cometa.it for details 
on the copyright holders.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Author(s): Giuseppe La Rocca (INFN), Salvatore Monforte (INFN)
 ****************************************************************************/
package infn.eToken;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

public class ProxyResource {

  private Properties properties;
  private String requestKey;
  private Logger log = Logger.getLogger("infn.eToken");

  /** Creates a new instance of ProxyResource */
  private ProxyResource(String key, Properties p) {
    properties = p;
    requestKey = key;       
  }

  /** Get instance of the ProxyResource */
  public static ProxyResource getInstance(String key, Properties p) {
    // The user may use some kind of persistence mechanism
    // to store and restore instances of ProxyResource class.
    return new ProxyResource(key, p);
  }

  /**
   * Retrieves representation of an instance of infn.eToken.ProxyResource
   * @return an instance of java.lang.String
   */
  @GET
  @Produces("text/plain")
  public String getText(@Context HttpServletRequest requestContext) {

    String result = null;
    String expiry_date = null;
    File temp = null;
    
    Date currentDate = new Date();
    currentDate.setTime (currentDate.getTime());
    
    log.debug("A C C E P T E D  connection  from  [ " 
    + requestContext.getRemoteAddr().toString()
    + " ]");
    
    //log.debug("=> Received a new request ...");
    log.debug("Start processing at " + currentDate);    
    
    result = ProxiesResouceSingleton.getInstance().getProxyFromCache(requestKey);
    
    if (result == null) {    

      // Before creating a new proxy we check whether the requested proxy 
      // may be generated or not
      log.debug("RequestKey NOT FOUND in cache! ... Generating a new proxy certificate");
                               
      HashMap<String, String> labels_map = 
              ProxiesResouceSingleton.getInstance().getLabelsMap();
      
      HashMap<String, String> emails_map = 
              ProxiesResouceSingleton.getInstance().getEmailsMap();
      
      properties.setProperty("TOKEN_SERIAL", 
              labels_map.get(properties.getProperty("MD5SUM")));            
      
      properties.setProperty("EMAIL_ADDRESS", 
              emails_map.get(properties.getProperty("MD5SUM")));
      
      TokenUtils.TokenInfo tokeninfo = ProxiesResouceSingleton.getInstance().
              getTokensMap().get(labels_map.get(properties.getProperty("MD5SUM")));
      
      if (tokeninfo == null) {
        ResponseBuilderImpl builder = new ResponseBuilderImpl();
        builder.status(Response.Status.NOT_FOUND);
        builder.entity("The requested certificate does not exist.");

        Response response = builder.build();
        throw new WebApplicationException(response);        
      }
            
      //String fqan = properties.getProperty("VOMS_FQAN");   
      
      /*if (fqan!=null) {      
      checkFQANs(
              tokeninfo.label,
              tokeninfo.attributes,
              fqan
              );
      }*/
      
      try {
        temp = File.createTempFile("x509up_", ".token");
        temp.deleteOnExit();
        properties.setProperty("PROXY_FILE", temp.toString());
        if (doCreateProxy(properties)) {
          log.debug(" ");
          log.debug("C A C H I N G proxy [ " + 
                     properties.getProperty("MD5SUM") + " ] ");         
          
          result = Utils.readFileAsString(temp.toString());
          
          ProxiesResouceSingleton
          .getInstance()
          .setProxyToCache(requestKey, result);
          
        } else result = Utils.readFileAsString(temp.toString());
      } catch (IOException ex) { log.error(ex); } 
        finally { temp.delete(); }
    } else {
        log.debug("Sending a cached proxy for [ " 
                + properties.getProperty("MD5SUM") + " ]");
        
        expiry_date = 
                ProxiesResouceSingleton
                .getInstance()
                .getProxyExpirationTime(requestKey);
        
        log.debug("The proxy is valid until " + expiry_date);        
    }
        
    log.debug(" ");
    
    return result;   
  }
  
  private boolean insertACAttributes(String VOMSfqan,                                      
                                     String filename,
                                     String keybit,
                                     String VOMSES_PATH,
                                     String VOMS_PATH,
                                     String X509_CERT_DIR,
                                     String VOMS_LIFETIME,
                                     Boolean enable_rfc_proxy) 
  {
    // Transform VOMSfqan back to a vector of Strings trimming the [ ]
    // [vo:fqan1,vo:fqan2,...,vo:fqanN]

    String[] fqans = VOMSfqan.substring(1, VOMSfqan.length() - 1).split(",");

    String VOname = fqans[0].split(":")[0]; // takes the VOname from the first entry

    boolean result=false;

    ArrayList<String> ACs = new ArrayList<String>();

    for (String f : fqans) {

      String[] info = f.split(":");
      String fqan = info[info.length - 1].trim();

      if (fqan.length() > 0) {
        // Prefixing VO name with '/'
        if (fqan.indexOf("/") == -1) {
          fqan = "/" + fqan;
        }

        ACs.add(fqan);
      }
    }    

    // Check if HashMap contains the VOMS configuration 
    // parameters before to contact the VOMS Server
    if (ProxiesResouceSingleton.getInstance().getVOMSesMap().get(VOname) != null) 
    {

      result = VOMSUtils.createVOMSProxy(              
              ACs,
              filename,
              keybit,
              VOMSES_PATH,
              VOMS_PATH,
              X509_CERT_DIR,
              VOMS_LIFETIME,
              enable_rfc_proxy);

      log.info("Adding '" + ACs.toString() + "' AC attributes ... [ " + result + " ]");
    } else log.error("Failures to add AC attributes ... ");
    return result;
  }

  private boolean doCreateProxy(Properties properties) {

    boolean result = false;    

    synchronized (ProxiesResouceSingleton.getInstance()) 
    {
        // Check if the proxy renewal flag is enabled
        boolean requiresLongProxy = 
            properties.getProperty("ENABLE_PROXY_RENEWAL").equals("TRUE");
                
        // Check if the proxy type is 'RFC' or 'FULL LEGACY'
        boolean enable_rfc_proxy =
            properties.getProperty("ENABLE_RFC_PROXY").equals("TRUE");
        
        //if (requiresLongProxy) 
        if ((requiresLongProxy) && (enable_rfc_proxy))
        {
            log.debug(" ");
            log.debug("A C T I O N => ~ [ CREATING long term proxy ] ~");            
            log.debug(properties);
            File longproxy_temp = null;
            
            try {
                longproxy_temp = File.createTempFile("x509up_", ".long");                
               
                // Create the plain long proxy
                result = TokenUtils.createProxy(
                         longproxy_temp.toString(),
                         Integer.parseInt(properties.getProperty("PROXY_KEYBIT")),                         
                         enable_rfc_proxy,
                         168,
                         properties.getProperty("TOKEN_SERIAL"),
                         properties.getProperty("SMTP_HOST"),
                         properties.getProperty("EMAIL_ADDRESS"),
                         properties.getProperty("SENDER_EMAIL"),
                         properties.getProperty("EXPIRATION"),
                         properties.getProperty("ENABLE_CN_LABEL"),                            
                         ProxiesResouceSingleton
                            .getInstance()
                            .getTokensMap()
                            .get(properties
                            .getProperty("TOKEN_SERIAL"))
                            .conf,
                         properties.getProperty("PIN"));

                log.info("CREATING long proxy: [ " + result + " ]");
                log.debug("LONG PROXY:" 
                        + Utils.readFileAsString(longproxy_temp.toString()));
                                                
                if (result) 
                {
                    log.info("Sending HTTP request in progress... ");
                    try {
                        result = TokenUtils.doHTTPRequest(
                                 properties.getProperty("ETOKEN_SERVER"),
                                 Integer.parseInt(properties.getProperty("ETOKEN_PORT")),                        
                                 longproxy_temp.getName());
                        
                    } catch (Exception ex) { log.error(ex); } 
                 }
                    
                log.info("REGISTERING long-term proxy: [ " + result + " ]");
            } catch (IOException ex) { log.error(ex); } 
                  finally {
                    if (longproxy_temp != null) longproxy_temp.delete();                    
            }
        }
                
        log.debug(" ");
        log.debug("A C T I O N => ~ [ CREATING proxy ] ~");                  
        log.debug(properties);

        // Create the plain proxy
        result = TokenUtils.createProxy(
                 properties.getProperty("PROXY_FILE"),
                 Integer.parseInt(properties.getProperty("PROXY_KEYBIT")),                          
                 enable_rfc_proxy,
                 Integer.parseInt(properties.getProperty("PROXY_LIFETIME")),
                 properties.getProperty("TOKEN_SERIAL"),
                 properties.getProperty("SMTP_HOST"),
                 properties.getProperty("EMAIL_ADDRESS"),
                 properties.getProperty("SENDER_EMAIL"),
                 properties.getProperty("EXPIRATION"),
                 properties.getProperty("ENABLE_CN_LABEL"),
                 ProxiesResouceSingleton.getInstance()
                                        .getTokensMap()
                                        .get(properties
                                        .getProperty("TOKEN_SERIAL"))
                                        .conf,
                                        properties.getProperty("PIN"));        
                                
        log.info("Generating proxy: [ " + result + " ]");
                
        // If the plain proxy certificate has been successfully create,
        // request the AC attributes contacting the VOMS Server.      
        boolean proxy_OK = false;
                
        if (result) 
        {                  
            String VOMSfqan = properties.getProperty("VOMS_FQAN");        
            boolean DisableVOMSproxy = 
                    properties
                    .getProperty("DISABLE_VOMS_PROXY")
                    .equals("TRUE");

            if ((VOMSfqan != null) && (!DisableVOMSproxy)) 
            {
                log.debug(" ");
                log.debug("A C T I O N => ~ [ ADDING AC Attributes ] ~ ");                    
                log.debug("");
                
                proxy_OK = 
                    insertACAttributes(
                            VOMSfqan,                            
                            properties.getProperty("PROXY_FILE"),
                            properties.getProperty("PROXY_KEYBIT"),
                            properties.getProperty("VOMSES_PATH"),
                            properties.getProperty("VOMS_PATH"),
                            properties.getProperty("X509_CERT_DIR"),
                            properties.getProperty("VOMS_LIFETIME"),
                            enable_rfc_proxy);          
            } else proxy_OK=true; // No ACLs have been requested
        }
        
        result = ((new File(properties.getProperty("PROXY_FILE")).exists()) &&
                  (proxy_OK) &&
                  (new File(properties.getProperty("PROXY_FILE")).length() > 0));            
    }
    
    return result;
  }
  
  /*private class supportsVO implements Predicate<TokenUtils.ACInfo> 
  {
    public supportsVO(String vo) { this.vo = vo; }

    public boolean apply(TokenUtils.ACInfo arg) 
    {
      return arg.vo.equals(this.vo);
    }
     
    private String vo;
  }*/
  
  /*private void checkFQANs(String label, 
                          Collection<TokenUtils.ACInfo> TokenACs, 
                          String VOMSfqan) {

    String[] fqans = VOMSfqan.substring(1, VOMSfqan.length() - 1).split(",");

    boolean isAnyFQANSupported = false;
    TokenUtils.ACInfo ac = null;
    
    for (String fqan : fqans) {

      String[] info = fqan.split(":");
      String VOname = info[0];

      try {
        ac =Iterables.find(TokenACs, new supportsVO(VOname));
      } catch(java.util.NoSuchElementException e) {}
      
      if (ac == null) {
        ResponseBuilderImpl builder = new ResponseBuilderImpl();
        builder.status(Response.Status.NOT_ACCEPTABLE);
        builder.entity("The certificate '" + 
                       label +
                       "' does not supports the VO '" + 
                       VOname + 
                       "'");

        Response response = builder.build();
        throw new WebApplicationException(response);
      }

      String f = info[info.length - 1].trim();
      if (f.length() > 0) {
        // Prefixing VO name with '/'
        if (f.indexOf("/") == -1) {
          f = "/" + f;
        }
      }
      if (ac.fqans.contains(f)) {
        isAnyFQANSupported = true;
        break;
      }
    }
    if (!isAnyFQANSupported) {
      ResponseBuilderImpl builder = new ResponseBuilderImpl();
        builder.status(Response.Status.NOT_ACCEPTABLE);
        
        builder.entity(
                "The certificate '" + 
                label + 
                "' does not support any of  " + 
                Arrays.asList(fqans).toString() + 
                " FQANs\n\n" +
                "Available values for this certificate are: " + 
                ac.fqans.toString().replaceAll(",", "\n"));
        
        Response response = builder.build();
        
        throw new WebApplicationException(response);
    }
  }*/

  /**
   * PUT method for updating or creating an instance of ProxyResource
   * @param content representation for the resource
   * @return an HTTP response with content of the updated or created resource.
   */
  @PUT
  @Consumes("text/plain")
  public void putText(String content) { }

  /** DELETE method for resource ProxyResource */
  @DELETE
  public void delete() { }
}
