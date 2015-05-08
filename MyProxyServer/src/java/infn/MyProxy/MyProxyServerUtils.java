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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Provider;
import java.security.Security;

import org.apache.log4j.Logger;

import org.globus.myproxy.CredentialInfo;
import org.globus.myproxy.InfoParams;
import org.globus.myproxy.InitParams;
import org.globus.myproxy.MyProxy;
import org.globus.myproxy.MyProxyException;

import org.globus.util.Util;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

/**
 * Provide a wrapper for calls to the MyProxy Server.
 *
 */
public class MyProxyServerUtils 
{
    // Define the logger for the Java class.

    private static Logger log = Logger.getLogger(MyProxyServerUtils.class);

    /**
     * Read in a credential from a file.  If you do not specify a filename
     * filename (i.e. pass in null), the default Globus filename will be
     * used (e.g. "/tmp/x509up_u_msmith").
     * @param filename The name of the file from which to read the proxy
     *                 credential.  If null, use a default Globus filename.
     * @return A GSS credential read from file if successfully read in, or
     *         null if not.
  */
  public static GSSCredential getCredentialFromFile(String filename) 
            throws IOException, org.ietf.jgss.GSSException 
  {

        org.ietf.jgss.GSSCredential retcred = null;
        
        if (filename.length() == 0)
            log.error("No proxy file specified. ");
        
        log.debug(" ");
        log.debug("Getting GSSCredentials from local file ");
        log.debug("Reading file '" + filename + "'");
        
        File inFile = new File(filename);
        byte[] data = new byte[(int) inFile.length()];
        FileInputStream inStream = new FileInputStream(inFile);
        inStream.read(data);
        //inStream.close();  
                
        ExtendedGSSManager manager = 
                (ExtendedGSSManager) ExtendedGSSManager.getInstance();
        
        try 
        {   
            retcred = manager.createCredential(
                      data, 
                      ExtendedGSSCredential.IMPEXP_OPAQUE,
                      GSSCredential.DEFAULT_LIFETIME, 
                      null, 
                      GSSCredential.INITIATE_AND_ACCEPT);                        
            
            if (retcred != null)
                log.debug("Getting GSSCredential from local file [ DONE ]");
            
        } catch (GSSException ex) { log.error(ex); }
        finally { inStream.close();  }
          
        return retcred;
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
     *	@param MYPROXY_SERVER	 	The hostname of the server to contact.
     *	@param MYPROXY_PORT 		The port on which the server is listening.
     *  @param MYPROXY_PROXYLIFETIME 	The life time for the user proxy.
     *  @param MYPROXY_FILE 		The temporary life time for the user proxy.
     *  @return a boolean
  */
  public static boolean registerProxyRenewal(String MYPROXY_SERVER, 
                                               int MYPROXY_PORT,
                                               int MYPROXY_PROXYLIFETIME,                                               
                                               String MYPROXY_FILE) 
  {
        boolean success = false;
        Provider bc = null;
        org.ietf.jgss.GSSCredential credential = null;
        
        org.globus.myproxy.MyProxy myProxyServer = new MyProxy();        
        myProxyServer.setHost(MYPROXY_SERVER);
        myProxyServer.setPort(MYPROXY_PORT);
        
        log.debug(" ");
        try 
        {
            bc = new org.bouncycastle.jce.provider.BouncyCastleProvider();
            Security.insertProviderAt(bc, 1);
            
            log.debug("MyProxy Server      : " + MYPROXY_SERVER);
            log.debug("MyProxy Server Port : " + MYPROXY_PORT);
            log.debug("MyProxy Lifetime    : " + MYPROXY_PROXYLIFETIME);
            log.debug("MyProxy tmp file    : " + MYPROXY_FILE);

            credential = getCredentialFromFile(MYPROXY_FILE);            
            
            if (credential == null) log.debug("credentials = <NULL>");
            else log.debug("credentials not <NULL>");

            org.globus.myproxy.InitParams params = new InitParams();
            params.setUserName(credential.getName().toString());
            params.setLifetime(credential.getRemainingLifetime());
            
            if (credential != null) 
            {
                // Initialize the MyProxy class object
                myProxyServer = new MyProxy();
                myProxyServer.setHost(MYPROXY_SERVER);
                myProxyServer.setPort(MYPROXY_PORT);

                // Register delegated credentails to the MyProxy Server 
                // using local credentials
                myProxyServer.put(credential, params);

                log.debug(" ");
                log.debug(" Using credential: " + credential.getName());
                log.debug(" A long-term proxy valid for DN " + credential.getName() 
                        + " now DOES EXIST on " 
                        + MYPROXY_SERVER);
                
                log.debug(" Remaining lifetime: " 
                        + credential.getRemainingLifetime() / 3600
                        + " hours (" 
                        + (credential.getRemainingLifetime() / (3600 * 24))
                        + " days)");

                // Retrieving the info about the proxy from the MyProxy Server
                InfoParams infoRequest = new InfoParams();
                infoRequest.setUserName(credential.getName().toString());
                infoRequest.setPassphrase("DUMMY-PASSPHRASE");
                
                String tmp;
                
                try {
                    CredentialInfo[] info = 
                            myProxyServer.info(credential, infoRequest);
	
                    for (int i=0 ;i<info.length; i++) 
                    {
                        log.debug(" ");
                        log.debug(" G E T T I N G info from MyProxyServer! ");
                        log.debug(" Server ....... " + myProxyServer.getHost());
                        log.debug(" Owner ........ " + info[0].getOwner());
                        log.debug(" Start Time ... " + info[i].getStartTime());
                        log.debug(" End Time ..... " + info[i].getEndTime());                        
			
                        long now = System.currentTimeMillis();
                        if (info[i].getEndTime() > now)
                        log.debug(" Time left .... " 
                                + Util.formatTimeSec((info[i].getEndTime()-now)/1000));
                        else log.debug(" Time left .. [ EXPIRED! ] ");
                
                        tmp = info[i].getDescription();
                        if (tmp != null) log.debug(" Description .. " + tmp);
                        else log.debug(" Description .. long-term proxy file for user");
                
                        success = true;
                    }
                } catch(Exception ex) { log.error(ex); }                                
            }

        } catch (MyProxyException ex) { log.error(ex); } 
          catch (GSSException ex) { log.error(ex); } 
          catch (IOException ex) { log.error(ex.getMessage()); }           
          finally { Security.removeProvider(bc.getName()); }
        
        return success;
  }       
}