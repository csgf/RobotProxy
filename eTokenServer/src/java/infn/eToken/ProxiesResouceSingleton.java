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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.timer.Timer;
import org.apache.log4j.Logger;

public class ProxiesResouceSingleton {

  private static ProxiesResouceSingleton instance = null;
  private Properties properties;
  private HashMap<String, String> labels_map;
  private HashMap<String, String> emails_map;
  private HashMap<String, Properties> vomses_map;
  private HashMap<String, TokenUtils.TokenInfo> tokens_map;
  private HashMap<String, ProxyCacheEntry> proxy_cache;
  
  private static Logger log = Logger.getLogger(ProxiesResouceSingleton.class);  

  public class ProxyCacheEntry {

    public ProxyCacheEntry(Date e, String p) {
      this.expiryTime = e;
      this.proxyValue = p;
    }

    public String getProxy() {
      return this.proxyValue;
    }

    public boolean expiresWithin(long t) {
      return new Date().getTime() + t < this.expiryTime.getTime();
    }
    
    private Date expiryTime = null;
    private String proxyValue = null;
  }

  private ProxiesResouceSingleton() 
  {
    try {
      properties = new Properties();
      properties.load(
      this.getClass().getClassLoader().getResourceAsStream("infn/eToken/eToken.properties"));            
      
      // Initialize a Java HashMap to keep in memory cached grid proxies
      proxy_cache = new HashMap<String, ProxyCacheEntry>();            
      
      // Inizialize the vomses_map HashMap with the grid settings for each VOMS server
      vomses_map = VOMSUtils.readVOMSes(new File(properties.getProperty("VOMSES_PATH")));
      
      // Initialize a Java HashMap to keep in memory the mapping (robotID, subject)
      labels_map = new HashMap<String, String>();
      
      // Initialize a Java HashMap to keep in memory the mapping (robotID, email)
      emails_map = new HashMap<String, String>();
                  
      // Display the HashMap with
      // VO_NAME (key)
      // [VOMS_ALIAS, VOMS_SERVER, VOMS_HOSTDN, VOMS_PORT, VOMS_NAME, VOMS_FILE] (Properties)
      if (!vomses_map.isEmpty()) {
        log.debug(" ");
        log.debug("[ VOMSES_MAP successfully created ]");
        log.debug(vomses_map.toString());
      } else log.debug("[ Some errors occurred during the creation of the VOMSES_MAP ]");
            
      // Creating an HashMap with:
      // SerialNumber (key)
      // [SERIAL, MD5SUM, SUBJECT, LABEL, ISSUER, VALIDFROM, VALIDTO, SIGNATURE, OID, PUBLICCERT] (Properties)      
      tokens_map = TokenUtils.getTokenInfoMap(
              properties.getProperty("ETOKEN_CONFIG_PATH"), 
              properties.getProperty("PIN"), 
              vomses_map);
                  
      if (tokens_map != null && !tokens_map.isEmpty()) 
      {
            log.debug(" "); 
            log.debug("[ TOKENS_MAP successfully created ] "); 
            log.debug(" ");
            log.debug("The eTokenServer completed the start-up!");
            log.debug("Populating the labels_map HashMap ...");
            
            // Populate the labels_map HashMap starting from the tokens_map
            for (Map.Entry<String, TokenUtils.TokenInfo> token : tokens_map.entrySet())
            {   
                Properties p = new Properties();
                //p.setProperty("MD5SUM", TokenUtils.getMD5(token.getValue().subject));
                p.setProperty("TOKEN_SERIAL", token.getValue().serial);
                
                // Feeding the HashMaps...                        
                //labels_map.put(TokenUtils.getMD5(token.getValue().subject), p);                
                labels_map.put(TokenUtils.getMD5(token.getValue().subject), 
                                                 token.getValue().serial);
                
                // Check if the email address is not null
                Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
                Matcher m = pattern.matcher(token.getValue().email);
                boolean matchFound = m.matches();
                if (matchFound)                    
                    emails_map.put(TokenUtils.getMD5(token.getValue().subject), 
                                   token.getValue().email);                
                else
                    // Setting the default e-mail address
                    emails_map.put(TokenUtils.getMD5(token.getValue().subject), 
                                   properties.getProperty("DEFAULT_EMAIL"));
            }
            
            log.debug(" ");
            log.debug("[ LABELS_MAP successfully created ]");
            log.debug(labels_map.toString());
            log.debug(" ");
            log.debug("[ EMAIL_MAP successfully created ]");
            log.debug(emails_map.toString());            
            
      } else log.debug("[ Some errors occurred during the creation of the TOKENS_MAP ]");
      
      java.util.logging.Logger.getLogger(ProxiesResouceSingleton.class.getName()).log(Level.INFO, null, tokens_map);
    } catch (IOException ex) { 
      java.util.logging.Logger.getLogger(ProxiesResouceSingleton.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public static synchronized ProxiesResouceSingleton getInstance() 
  {
    if (instance == null) {
      log.debug("");
      log.debug("Reading server settings.");
      log.debug("eToken server is starting.");      
      log.debug("");
      instance = new ProxiesResouceSingleton();      
      log.debug("E T O K E N * S E R V E R * has started!"); 
      log.debug("Waiting for incoming requests...");
    }
    return instance;
  }

  public Properties getProperties() {
    return properties;
  }

  public HashMap<String, Properties> getVOMSesMap() {
    return vomses_map;
  }

  public HashMap<String, TokenUtils.TokenInfo> getTokensMap() {
    return tokens_map;
  }
  
  public HashMap<String, String> getLabelsMap() {
    return labels_map;
  }
  
  public HashMap<String, String> getEmailsMap() {
    return emails_map;
  }

  public String getProxyFromCache(String requestID) {

    ProxyCacheEntry entry = proxy_cache.get(requestID);
    
    //return entry != null && entry.expiresWithin(3L * Timer.ONE_HOUR) ? entry.getProxy() : null;
    return entry != null && entry.expiresWithin(20L * Timer.ONE_HOUR) ? entry.getProxy() : null;    
  }
  
  public String getProxyExpirationTime(String requestID) 
  {      
      ProxyCacheEntry entry = proxy_cache.get(requestID);
      Date date = entry.expiryTime;
      SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
      String expiry_date = sdf.format(date);
      
      return expiry_date;
  }

  public void setProxyToCache(String requestID, String proxy) 
  {
    long expiry = 
            new Date().getTime() + 
            Integer.parseInt(properties.getProperty("PROXY_LIFETIME")) * 
            Timer.ONE_HOUR;
    
    Date date = new Date(expiry);
    SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
    String expiry_date = sdf.format(date);
    log.debug("The proxy is valid until " + expiry_date);
    
    proxy_cache.put(requestID,
                    new ProxyCacheEntry(new Date(expiry), proxy));
  }
}