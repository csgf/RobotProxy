/**************************************************************************
Copyright (c) 2011-2015: 
Istituto Nazionale di Fisica Nucleare (INFN), Italy
Consorzio COMETA (COMETA), Italy

See http://www.infn.it and and http://www.consorzio-cometa.it for details 
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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import java.security.Provider;
import java.security.Security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.rpc.ServiceException;

import org.apache.http.Header;
import org.apache.log4j.Logger;
import org.bouncycastle.openssl.PEMWriter;

import org.glite.security.voms.User;
import org.glite.security.voms.service.admin.VOMSAdminServiceLocator;
import org.italiangrid.voms.clients.VomsProxyInit;

public class VOMSUtils {
    
    private static Logger log = Logger.getLogger(VOMSUtils.class);
    private static final String DEFAULT_CONNECT_TIMEOUT_IN_SECONDS = "5";
    
    public static String parseVOMSFQAN(String voms, String separator) 
    {

        ArrayList<String> FQANs = new ArrayList<String>();
        for (String s : voms.split(separator)) {

            String[] info = s.split(":");
            String fqan = info[info.length - 1].trim();

            // Prefixing VO name with '/'
            if (fqan.indexOf("/") == -1) {
                fqan = "/" + fqan;
            }

            if (Pattern.matches(
                    "(?:/VO=(?:[\\w_\\x2d]+[\\x2e]{0,1}[\\w_\\x2d]+)/GROUP=)?"
                    + "(/{1}[a-zA-Z]{1}(?:[\\w_\\x2d]+[\\x2e]{0,1}|[\\x2f]{0,1}[\\w_\\x2d]+)*)"
                    + "(?:/Role=([\\w_\\x2d]+))?"
                    + "(?:/Capability=([\\w_\\x2d]+))?",
                    fqan)) {
                FQANs.add(s);
            }

        }
        return FQANs.toString();
    }
    
    private static Properties getVOMSProperties(File file) 
            throws FileNotFoundException, IOException 
    {
        Properties result = null;
        String line = null;

        DataInputStream fis = new DataInputStream(
                new FileInputStream(file.toString()));

        BufferedReader br = 
                new BufferedReader(new java.io.InputStreamReader(fis));
               
        try {
            line = br.readLine();
        } catch (IOException ex) {
           log.error("Unable to read info from file :" + file.getAbsolutePath());
        }

        if (line != null) 
        {
            log.debug("Parsing " + line);
            //final String regexp = "^\\\"([^\\\"]+)\\\" \\\"([^\\\"]+)\\\" \\\"([0-9]+)\\\" \\\"([^\\\"]+)\\\" \\\"([^\\\"]+)\\\".*";
            final String regexp = "^\\\"([^\\\"]+)\\\" \\\"([^\\\"]+)\\\" \\\"(.+)\\\" \\\"([^\\\"]+)\\\" \\\"([^\\\"]+)\\\".*";
            Matcher m = Pattern.compile(regexp).matcher(line);
            
            if (m.find()) 
            {                
                result = new Properties();
                result.setProperty("VOMS_NAME", m.group(1));
                result.setProperty("VOMS_SERVER", m.group(2));
                result.setProperty("VOMS_PORT", m.group(3));
                result.setProperty("VOMS_HOSTDN", m.group(4));
                result.setProperty("VOMS_ALIAS", m.group(5));
                result.setProperty("VOMS_FILE", file.toString());
                
                log.debug("\nVOMS parameters:" +  result.toString());
            }
            else {
                log.error("Failure extracting VOMS info '" + line + 
                          "': doest not match regexp '" + regexp + "'");
            }
        }
        try {
            fis.close();
        } catch (IOException ex) { log.error (ex); }
        
        return result;
    }
    
    public static HashMap<String, Properties> readVOMSes(File dir) 
            throws FileNotFoundException, IOException 
    {
        HashMap<String, Properties> result = new HashMap<String, Properties>();

        File[] files = dir.listFiles();
        
        for (File file : files) {
            log.debug("Loading VOMS settings from " + file);
            Properties properties = VOMSUtils.getVOMSProperties(file);
            
            if (properties != null) 
                result.put(properties.getProperty("VOMS_NAME"),properties);
        }
        
        log.debug(" ");        
        return result;
    }
    
    public static String[] getEmailAddress(String subject, 
                                           String issuer,
                                           VOMSAdminServiceLocator locator,
                                           String VOMS_SERVER,
                                           String VOMS_NAME) 
    {                                              
        String[] result = null;
        
        try {
            String endpoint = "https://" + VOMS_SERVER + ":8443"
                        + "/voms/" + VOMS_NAME
                        + "/services/VOMSAdmin";

            log.info(endpoint);
                
            java.net.URL _vomsURL = new java.net.URL(endpoint);

            // Getting a stub which implements the service.
            org.glite.security.voms.service.admin.VOMSAdmin _vomsadmin =
                locator.getVOMSAdmin(_vomsURL);            
                            
            String[] groups = _vomsadmin.listGroups(subject, issuer);            
            boolean has_groups = (groups != null && groups.length > 0);

            Collection<String> items = new ArrayList<String>();
            
            // Get the e-mail address for notifications: 11/12/2013
            if (has_groups) 
            {
                User[] listMembers = _vomsadmin.listMembers(groups[0]);           
                for (User user:listMembers) 
                {
                    if ( ((user.getDN()).toString()).contains(subject) ) 
                    {
                        log.debug(" => " + user.getMail());
                        if (!user.getMail().isEmpty())
                            items.addAll(Arrays.asList(user.getMail()));
                    }
                }
                
                items.addAll(Arrays.asList(groups));
            }
            
            result = items.toArray(new String[0]);            
            
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(VOMSUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ServiceException ex) { log.error(ex); } 
        catch (RemoteException ex) { log.error(ex); }         
                        
        return result;
    }
    
    public static String[] listGroupsAndRoles(String subject, 
                                              String issuer, 
                                              VOMSAdminServiceLocator locator,
                                              String VOMS_SERVER,
                                              String VOMS_NAME)
    {                    
        String[] result = null;
        
        try {                           
                String endpoint = "https://" + VOMS_SERVER + ":8443"
                        + "/voms/" + VOMS_NAME
                        + "/services/VOMSAdmin";

                log.info(endpoint);                
                java.net.URL _vomsURL = new java.net.URL(endpoint);

                // Getting a stub which implements the service.
                org.glite.security.voms.service.admin.VOMSAdmin _vomsadmin =
                    locator.getVOMSAdmin(_vomsURL);
                                
                String[] roles = _vomsadmin.listRoles(subject, issuer);
                String[] groups = _vomsadmin.listGroups(subject, issuer);                

                boolean has_roles = (roles != null && roles.length > 0);
                boolean has_groups = (groups != null && groups.length > 0);
                
                Collection<String> items = new ArrayList<String>();
                
                if (has_roles || has_groups) 
                {  
                    if (has_roles)
                        items.addAll(Arrays.asList(roles));
                    
                    if (has_groups) 
                        items.addAll(Arrays.asList(groups));                    
                    
                    result = items.toArray(new String[0]);
                    log.debug("[ listGroupsAndRoles ]");
                    for (int k=0; k<result.length; k++)
                        log.debug("["+k+"] " + result[k]);                    
                }                                
                                      
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(VOMSUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            java.util.logging.Logger.getLogger(VOMSUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            java.util.logging.Logger.getLogger(VOMSUtils.class.getName()).log(Level.SEVERE, null, ex);            
        }
                
        return result;
    }
    
    /**
     * @param fqans the list of attributes in FQAN format.
     * @return the request which will be sent to the VOMS Server.
     */
    private static String[] parseVOMSCommands(Collection<String> fqans) 
    {
        String result = null;
        String[] FQAN = new String[fqans.size()];
        int i=0;

        Pattern p = Pattern.compile(
                "(?:/VO=(?:[\\w_\\x2d]+[\\x2e]{0,1}[\\w_\\x2d]+)/GROUP=)?"
                + "(/{1}[a-zA-Z]{1}(?:[\\w_\\x2d]+[\\x2e]{0,1}|[\\x2f]{0,1}[\\w_\\x2d]+)*)"
                + "(?:/Role=([\\w_\\x2d]+))?"
                + "(?:/Capability=([\\w_\\x2d]+))?");

        boolean order_set = false;

        for (String f : fqans) {

            Matcher m = p.matcher(f);

            // Matching groups:
            // 0 -> vo/groups[/subgroups]/Role=role/Capability=capability
            // 1 -> vo/groups[/subgroups]
            // 2 -> role
            // 3 -> capability

            if (m.matches()) {

                boolean has_role = m.group(2) != null;
                boolean has_only_group = (m.group(1) != null)
                        && (m.group(1).split("/").length == 1);
                boolean has_subgroups = (m.group(1) != null)
                        && (m.group(1).split("/").length > 1);

                StringBuilder cmd = new StringBuilder();
                String VO_NAME = "";

                if ((has_only_group || has_subgroups) && !has_role) 
                {
                    String[] parts = m.group(1).split("/");
                    VO_NAME = parts[1];
                    cmd.append(VO_NAME)
                       .append(":")
                       .append(m.group(1));
                } else if (has_only_group  && has_role) {
                    cmd.append("R").append(m.group(2));
                } else if (has_role && has_subgroups) {
                    String[] parts = m.group(1).split("/");
                    VO_NAME = parts[1];
                    cmd.append(VO_NAME)
                       .append(":")
                       .append(m.group(1))
                       .append("/Role=")
                       .append(m.group(2));
                }
                
                if (!order_set) {
                    order_set = true;
                    result = new String();
                }
                
                result = cmd.toString();
                FQAN[i++]=result;                
            } else log.error("AC attribute " + f + " syntax mismatch.");            
        }
        return FQAN;
    }
    
    /**
     *	@param fqans the list of FQANs for contacting the VOMS Server.
     *	@param filename the file which will contain a VOMS proxy certificate.
     *	@param PROXY_KEYBIT the number of bits in key.     
     *	@param VOMSES_PATH the standard location of configuration files.
     *	@param VOMS_PATH the standard location of configuration files.
     *	@param X509_CERT_DIR the standard location of configuration files.
     *	@param VOMS_LIFETIME the lifetime of the VOMS proxy expressed in hours.
     *	@return true if the VOMS ACs have been successfully retrieved.
     */
    static public Boolean createVOMSProxy(Collection<String> fqans, 
                                          String filename,
                                          String keybit,
                                          String VOMSES_PATH,
                                          String VOMS_PATH,
                                          String X509_CERT_DIR,
                                          String VOMS_LIFETIME,
                                          Boolean enable_rfc_proxy) {
        boolean result = false;                 

        try {

            result = getVOMSAC(
                    fqans, filename, keybit,
                    VOMSES_PATH, VOMS_PATH, X509_CERT_DIR, VOMS_LIFETIME,
                    enable_rfc_proxy);

            if (result) log.debug("Create VOMS proxy ... [ OK ] ");
            else log.error("Create VOMS proxy ... [ Failed ] ");

        } catch (Exception e) { log.error(e.getMessage()); } 
          
        return result;
    }
    
    /**
     *	@param fqans the list of FQANs for contacting the VOMS Server.
     *	@param filename the file which will contain a VOMS proxy certificate.
     *	@param PROXY_KEYBIT the number of bits in key.     
     *	@param VOMSES_PATH the standard location of configuration files.
     *	@param VOMS_PATH the standard location of configuration files.
     *	@param X509_CERT_DIR the standard location of configuration files.
     *	@param VOMS_LIFETIME the lifetime of the VOMS proxy expressed in hours.
     *	@return true if the VOMS ACs have been successfully retrieved.
     */
    private static Boolean getVOMSAC(Collection<String> fqans, 
                                     String filename,
                                     String PROXY_KEYBIT,
                                     String VOMSES_PATH,
                                     String VOMS_PATH,
                                     String X509_CERT_DIR,
                                     String VOMS_LIFETIME,
                                     Boolean enable_rfc_proxy) 
    {

        boolean success = false;
        
        Provider bc = new org.bouncycastle.jce.provider.BouncyCastleProvider();      
        Security.insertProviderAt(bc, 1);
        
        Properties p = new Properties(System.getProperties());
        p.setProperty("X509_USER_PROXY", filename);
        System.setProperties(p);
        
        String _LIFETIME = VOMS_LIFETIME.concat(":00");
        String tmp = "";
        
        File voms_arguments = null;
            
        try {
            voms_arguments = File.createTempFile("voms_", ".profile");
            log.debug("Creating voms profile in progress ");
            voms_arguments.deleteOnExit();
        
            log.debug(" ");
            log.debug("Contacting VOMS Server(s) ");
            log.debug("X509_USER_PROXY .... " + filename);
            log.debug("PROXY KEYBIT........ " + PROXY_KEYBIT);        
            log.debug("VOMSES_PATH......... " + VOMSES_PATH);
            log.debug("VOMS_LIFETIME....... " + _LIFETIME);
            log.debug("VOMS_PATH........... " + VOMS_PATH);
            log.debug("X509_CERT_DIR....... " + X509_CERT_DIR);
            log.debug("VOMS_PROFILE........ " + voms_arguments);
            log.debug("FQANs .............. " + fqans.toString());        
        
            String[] FQANs = parseVOMSCommands(fqans);
            PEMWriter pemWriter = null;
            
            try {
                java.io.FileOutputStream fos = 
                        new FileOutputStream(voms_arguments);
                
                pemWriter = 
                        new PEMWriter(new OutputStreamWriter(fos));                
                
                // Saving VOMS profile
                pemWriter.write("--noregen\n");
                pemWriter.write("--vomses=" + VOMSES_PATH + "\n");
                pemWriter.write("--out=" + filename + "\n");
                pemWriter.write("--valid=" + _LIFETIME + "\n");
                pemWriter.write("--vomslife=" + _LIFETIME + "\n");
                pemWriter.write("--bits=" + PROXY_KEYBIT + "\n");
                pemWriter.write("--timeout=" + DEFAULT_CONNECT_TIMEOUT_IN_SECONDS + "\n");
                
                if (enable_rfc_proxy) pemWriter.write("--rfc" + "\n");
                        
                for (int i=0; i<FQANs.length; i++) {
                    tmp = FQANs[i];
                    pemWriter.write("--voms=" + tmp + "\n");                    
                }
                
                pemWriter.write("--ignorewarn" + "\n");
                pemWriter.write("--debug" + "\n");
                
                pemWriter.close();
                log.debug("Saving voms profile  [ DONE ]");
                
            } catch (Exception e) { log.error(e.getMessage()); }              
            
            // ADDING VOMS ACs attributes to the original proxy
            if (enable_rfc_proxy) 
                log.debug("Adding VOMS ACs [ RFC3820 Proxy ]");
            else 
                log.debug("Adding VOMS ACs [ FULL LEGACY Proxy ]");
                                    
                /*VomsProxyInit.main(new String[]{
                    "-conf", voms_arguments.toString()
                });*/
            
                new VomsProxyInit(
                        new String[] { "-conf", voms_arguments.toString() }
                );
                        
            success = true;            
        
        } catch (IOException ex) { log.error(ex); } 
          finally {
                    if (voms_arguments != null) voms_arguments.delete();
                    Security.removeProvider(bc.getName());
          }

        return success;
    }
}
