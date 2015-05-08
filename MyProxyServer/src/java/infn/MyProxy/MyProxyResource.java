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
package infn.MyProxy;

import org.apache.log4j.Logger;
import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;

public class MyProxyResource {

  private Properties properties;
  private String requestFile;
  private Logger log = Logger.getLogger("infn.MyProxy");
    
  /** Creates a new instance of MyProxyResource */
  private MyProxyResource(String file, Properties p) 
  {
    properties = p;
    requestFile = file;       
  }

  /** Get instance of the MyProxyResource */
  public static MyProxyResource getInstance(String file, Properties p) 
  {
    // The user may use some kind of persistence mechanism
    // to store and restore instances of MyProxyResource class.
    return new MyProxyResource(file, p);
  }

  /**
   * Retrieves representation of an instance of infn.MyProxy.MyProxyResource
   * @return an instance of java.lang.String
   */
  @GET
  @Produces("text/plain")
  public String getText(@Context HttpServletRequest requestContext) 
  {
        String result = null;
        Date currentDate = new Date();
        currentDate.setTime (currentDate.getTime());
    
        log.debug("Accepted a new connection from " 
            + requestContext.getRemoteAddr().toString());
       
        log.debug("=> Received a new request ...");
        log.debug("Start processing at " + currentDate);        
        
        if (doCreateMyProxy()) {
            log.debug(" ");
            log.debug("S T O R E proxy on the MyProxy Server! [ DONE ] ");
            log.debug(" ");
            result = "The proxy has been successfully stored on the MyProxy Server";
        } else
            result = "The proxy file does not exist on the server";
        
        return result;
  }
  
  
  private boolean doCreateMyProxy() 
  {
    boolean result = false;
    
    synchronized (MyProxiesResourceSingleton.getInstance()) 
    {            
        log.debug(" ");
        log.debug("A C T I O N => ~ [ CREATING long term proxy ] ~");
        File longproxy_temp = null;
            
        longproxy_temp = new File (
                properties.getProperty("MYPROXY_PATH") 
                + "/" 
                + requestFile);        
        
        if(longproxy_temp.exists())
        {
            result = MyProxyServerUtils.registerProxyRenewal(
                properties.getProperty("MYPROXY_SERVER"),
                Integer.parseInt(properties.getProperty("MYPROXY_PORT")),
                Integer.parseInt(properties.getProperty("MYPROXY_LIFETIME")),                                         
                longproxy_temp.toString());
                    
            log.info("REGISTERING long-term proxy: [ " + result + " ]");
            if (longproxy_temp != null) 
            {
                log.info("Deleting tmp file [ " + longproxy_temp + " ]");
                longproxy_temp.delete();
            }            
        } else {
            log.info("The file does not exists.");
            log.info("REGISTERING long-term proxy: [ " + result + " ]");
        }
    }
    
    return result;
  }
  
  /*public static GSSCredential convert(String ProxyFile) throws GSSException 
  {
    GSSCredential credential = null;
        
    try {            
        credential = new GlobusGSSCredentialImpl(
                     new GlobusCredential(ProxyFile), 
                     GSSCredential.INITIATE_AND_ACCEPT);            
    } catch (GlobusCredentialException ex) { log.error(ex); }
          
    return credential;
  }*/

  /**
   * PUT method for updating or creating an instance of MyProxyResource
   * @param content representation for the resource
   * @return an HTTP response with content of the updated or created resource.
   */
  @PUT
  @Consumes("text/plain")
  public void putText(String content) { }

  /**
   * DELETE method for resource MyProxyResource
   */
  @DELETE
  public void delete() { }
}
