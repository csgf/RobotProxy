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

import java.io.*;
import java.util.*;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;

import java.util.logging.Level;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.security.auth.x500.X500Principal;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.axis.AxisProperties;
import org.apache.log4j.Logger;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.glite.security.trustmanager.axis.AXISSocketFactory;

import com.google.gson.annotations.Expose;

import eu.emi.security.authn.x509.helpers.proxy.ProxyCertInfoExtension;
import eu.emi.security.authn.x509.proxy.ProxyPolicy;
import java.nio.charset.Charset;
import java.security.cert.Certificate;

public class TokenUtils {

  private static Logger log = Logger.getLogger(TokenUtils.class);

  private static class KeyPair_Cert {

    public KeyPair_Cert(
            java.security.PublicKey puK,
            java.security.PrivateKey prK,
            X509Certificate c) {
      publicKey = puK;
      privateKey = prK;
      cert = c;
    }

    void setPublic(java.security.PublicKey k) {
      publicKey = k;
    }

    void setPrivate(java.security.PrivateKey k) {
      privateKey = k;
    }

    java.security.PublicKey getPublic() {
      return publicKey;
    }

    java.security.PrivateKey getPrivate() {
      return privateKey;
    }

    void setX509Cert(X509Certificate c) {
      cert = c;
    }

    X509Certificate getX509Cert() {
      return cert;
    }
    private java.security.PrivateKey privateKey;
    private java.security.PublicKey publicKey;
    private X509Certificate cert;
  }

  // This function checks the validity of the certificate
  private static long checkCertValidity (Date expirationDate)
  {
        Calendar now = Calendar.getInstance();

        long diff = (expirationDate.getTime() - now.getTimeInMillis());
        return diff;
  }   
  
  private static KeyPair_Cert getTokenData(
          String serialNumber,
          String smtp_host,
          String email,
          String sender_email,
          String expiration,
          String tokenPIN,
          Provider eToken_PKCS11Provider) 
  {
  
    KeyPair_Cert result = null;
    
    try {

      java.security.KeyStore keyStore = null;

      // Install the provider dynamically.
      // Create an instance of the provider with the appropriate configuration file
      log.debug(" ");
      log.debug("Smart Card ..... " + eToken_PKCS11Provider.getInfo());
      log.debug("Provider Name .. " + eToken_PKCS11Provider.getName());
      log.debug("Version ........ " + eToken_PKCS11Provider.getVersion());
      log.debug("Size ........... " + eToken_PKCS11Provider.size());

      keyStore = java.security.KeyStore.getInstance("PKCS11", eToken_PKCS11Provider);
      
      // Token Login
      keyStore.load(null, tokenPIN.toCharArray());

      Enumeration aliases = keyStore.aliases();

      // Check if alias contains TOKEN_SERIAL
      Boolean SerialMatchesAlias = false;
      String alias = null;

      for (Enumeration e = aliases; e.hasMoreElements() && !SerialMatchesAlias;) {
        alias = (String) e.nextElement();

        SerialMatchesAlias = serialNumber.equals(
                ((java.security.cert.X509Certificate) keyStore.getCertificate(alias))
                .getSerialNumber()
                .toString());
      }

      if (SerialMatchesAlias && keyStore.isKeyEntry(alias)) {

        log.debug("Key entry " + alias + " detected. ");

        // Getting the X509Certificate
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
        Certificate[] certChain = keyStore.getCertificateChain(alias);
        log.debug(" ");
        log.debug("X509 SN ........ " + cert.getSubjectX500Principal().getName());
        log.debug("certChain ...... " + certChain[0] + " (" + certChain.length + ")");
        log.debug("Issued by ...... " + cert.getIssuerDN());
        log.debug("Valid from ..... " + cert.getNotBefore());
        log.debug("Valid to ....... " + cert.getNotAfter());
        log.debug("Ser. Number .... " + cert.getSerialNumber());
        log.debug("Signature ...... " + cert.getSigAlgName());
        log.debug("Version ........ " + cert.getVersion());
        log.debug("OID ............ " + cert.getSigAlgOID());
        log.debug("................ ");
        log.debug("SMTP  .......... " + smtp_host);
        log.debug("Email .......... " + email);
        log.debug("Sender.......... " + sender_email);
        
        // Check if the robot certificate is still valid.
        long expired = checkCertValidity(cert.getNotAfter());
        
        log.debug(" ");
        
        // Checking if difference between 2 date is more than 30 days
        if ((expired / (3600 * 1000 * 24) > 0) && 
            (expired / (3600 * 1000 * 24) <= (Integer.parseInt(expiration))))
        {
            log.debug("The following certificate [ "
            + cert.getSerialNumber()
            + " ] is EXPIRING in few days");
            
            // Check if the email is valid            
            if (isValidEmailAddress(email))
                sendHtmlEmail(email, sender_email, smtp_host,
                          cert.getSerialNumber().toString(),
                          cert.getSubjectX500Principal().getName());            
        }
        
        if (expired / (3600 * 1000 * 24) < 0) 
        {
            log.debug("The following certificate [ "
            + cert.getSerialNumber()
            + " ] is EXPIRED");
            
            // Check if the email is valid            
            if (isValidEmailAddress(email))                 
                sendHtmlEmail(email, sender_email, smtp_host,
                          cert.getSerialNumber().toString(),
                          cert.getSubjectX500Principal().getName());            
        }
        else {
                log.debug("The following certificate [ "
                + cert.getSerialNumber()
                + " ] is VALID");

                // Getting the Private Key from token.
                result = new KeyPair_Cert(
                    cert.getPublicKey(),
                    (java.security.PrivateKey) keyStore.getKey(alias, 
                    tokenPIN.toCharArray()),
                    cert);
                
                log.debug(
                    result.getPublic().getFormat() + " "
                    + result.getPublic().getAlgorithm()
                    + " public key:");
                
                log.debug(result.getPublic().toString());
        }
      } else { log.error("Serial " + serialNumber + " not found!"); }
    } catch (Exception e) { log.error(e.getMessage()); }
    
    return result;
  }
  
  // Convert String format -> UTF-8
  private static String convertToUTF8(String CN) 
  {
      Charset UTF8 = Charset.forName("UTF-8");
      Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
      
      String _UTF8 = null;
      byte ptext[] = CN.getBytes(ISO_8859_1);
      _UTF8 = new String(ptext, UTF8);
      
      /*try {          
        _UTF8 = new String(CN.getBytes("UTF-8"), "ISO-8859-1");                            
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger
                    .getLogger(TokenUtils.class.getName())
                    .log(Level.SEVERE, null, ex);
        }*/
      
      return _UTF8.trim();
  }
  
  private static KeyPair_Cert createProxyCertificate(
          int keybit, Boolean rfc, int lifetime, String CN_label,
          X509Certificate tokenCert, java.security.PrivateKey tokenKey) 
  {
      
    KeyPair_Cert result = null;  
    Provider bc = null;
    
    try {
        
      Date lastDate = new Date();
      String proxyDN = "";
      String issuerDN = "";
      
      // Generate the new KeyPair
      bc = new org.bouncycastle.jce.provider.BouncyCastleProvider();      
      Security.insertProviderAt(bc, 1);      
      
      java.security.KeyPairGenerator kpGen = 
              KeyPairGenerator.getInstance("RSA", bc);
      
      SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
      kpGen.initialize(keybit, secureRandom);

      java.security.KeyPair pair = kpGen.generateKeyPair();            
      
      // Initialize the SerialNumber of the certificate.
      Random rand = new Random();
      //BigInteger serialNum = new BigInteger(20, rand);
      
      // Express the validity in milliseconds
      long validity = lifetime * 60 * 60 * 1000;
                              
      if (rfc) {
        // RFC-3280-compliant OID
        log.debug("[1] Creating RFC-3280-complaint proxy with [ " + keybit + " ] keybit");
        issuerDN = tokenCert.getSubjectX500Principal().getName();        
        log.debug("issuerDN = " + issuerDN);
        
        // Create the distinguished name (DN) of the proxy certificate.
        // This DN is the issuer's DN with an extra "CN=" part, which
        // value is a random integer.
        // In the RFC2253 the "CN=" part comes at the beginning.        
        String delegDN = String.valueOf(Math.abs(rand.nextInt()));                
        
        if (!CN_label.isEmpty() && (!CN_label.equals("eToken:Empty"))) {
            //RFC 3820 compliant impersonation proxy!            
            log.debug("Adding additional CN label to generate Per-User Sub-Proxy");            
            proxyDN = "CN=" + CN_label + "," + issuerDN;
            //proxyDN = "CN=" + delegDN + "," + "CN=" + CN_label + "," + issuerDN;            
        } else 
            proxyDN = "CN=" + delegDN + "," + issuerDN;            
        
        log.debug("Creating a RFC3280-compliant OID self-signed certificate for: ");        
        log.debug(proxyDN);
      } else {
        // Proxy draft (pre-RFC) compliant impersonation proxy
        // Create the distinguished name (DN) of the proxy certificate.
        // This DN is the issuer's DN with an extra "CN=proxy" part.
        // In the pre-RFC the "C/N=proxy" part comes at the beginning.
        issuerDN = tokenCert.getSubjectX500Principal().getName();
        log.debug("issuerDN = " + issuerDN);                
        
        //Proxy draft (pre-RFC) compliant impersonation proxy
        proxyDN = "CN=proxy" + "," + issuerDN;        
        
        log.debug("[1] Creating a fully legacy Globus proxy with [ " + keybit + " ] keybit");
        log.debug("DN =" + proxyDN);
        
        // Set the subject distinguished name.
        // The subject describes the entity associated with the public key.
        //certGen.setSubjectDN(new X500Principal(proxyDN));
        log.debug("SubjectDN = " + new X500Principal(proxyDN).getName());
      }
                  
      // Generate self-signed certificate      
      //X500Principal principal = new X500Principal(issuerDN);      
      
      // Generate self-signed certificate
      X509v3CertificateBuilder certGen = 
              new JcaX509v3CertificateBuilder(
                      tokenCert.getSubjectX500Principal(),
                      //principal,
                      tokenCert.getSerialNumber(),
                      new Date(lastDate.getTime()),
                      new Date(lastDate.getTime() + validity),                      
                      new X500Principal(proxyDN),                      
                      pair.getPublic());      
      
      // Add KeyUsage extension(s) to the plain proxy      
      log.debug("");
      log.debug("[2] Adding 'KeyUsage extension(s)' to the plain proxy... ");
      certGen.addExtension(X509Extension.keyUsage, true, 
                new KeyUsage(KeyUsage.digitalSignature |
                             KeyUsage.keyEncipherment |
                             KeyUsage.dataEncipherment));
      
      if (rfc) 
      {                    
          ProxyPolicy policy = new ProxyPolicy(ProxyPolicy.INHERITALL_POLICY_OID);
          
          String oid = ProxyCertInfoExtension.RFC_EXTENSION_OID;          
          
          ProxyCertInfoExtension extValue =                   
                  new ProxyCertInfoExtension(Integer.MAX_VALUE, policy);
          
          log.debug("[2'] Adding 'X509v3 extension(s)' to the RFC proxy... ");
          certGen.addExtension(
                  new ASN1ObjectIdentifier(oid),
                  true, 
                  extValue);
      } else {
          /*certGen.addExtension(X509Extension.keyUsage, true, 
                  new KeyUsage(KeyUsage.keyCertSign | 
                               KeyUsage.digitalSignature | 
                               KeyUsage.keyEncipherment | 
                               KeyUsage.dataEncipherment | 
                               KeyUsage.cRLSign));
                                
          Vector eku = new Vector(5, 1);
          eku.add(KeyPurposeId.id_kp_serverAuth);
          eku.add(KeyPurposeId.id_kp_clientAuth);
          eku.add(KeyPurposeId.anyExtendedKeyUsage);
          certGen.addExtension(X509Extension.extendedKeyUsage, true, 
                  new ExtendedKeyUsage(eku));*/
      }
      
      // --------------------------------------------------------
      // 	Generate the Proxy Certificate
      // --------------------------------------------------------      
      try {
        
          ContentSigner sigGen = 
                new JcaContentSignerBuilder(tokenCert.getSigAlgName())
                //new JcaContentSignerBuilder("sha1WithRSAEncryption")
                //.setProvider(bc) 
                .build(tokenKey);                                
        
        X509CertificateHolder certHolder = certGen.build(sigGen);        
        
        log.debug("[3] Generate the plain proxy ... ");  
        X509Certificate cert = 
              new JcaX509CertificateConverter()
              .setProvider(bc)                
              .getCertificate(certHolder);              
        
        cert.checkValidity(new Date());        
              
        result = new KeyPair_Cert(pair.getPublic(), pair.getPrivate(), cert);
        
      } catch (Exception e) { log.error(e.getMessage()); } 
    } catch (Exception e) { log.error(e.getMessage()); }
      finally { Security.removeProvider(bc.getName()); }
    
    return result;
  } // End method
    

  public static class ACInfo {

    public ACInfo(String vo, Collection<String> fqans) {
      this.vo = vo;
      this.fqans = fqans;
    }
    @Expose
    String vo = null;
    @Expose
    Collection<String> fqans = null;
  }

  public static class TokenInfo {

    public TokenInfo(String serial,
                     String md5sum,
                     String subject, 
                     String label,
                     String email,
                     String issuer,
                     String validfrom,
                     String validto,
                     String signature,
                     String oid,
                     String publiccert,
                     String conf, 
                     Collection<ACInfo> a) {
        
      this.serial = serial;
      this.md5sum = md5sum;
      this.subject = subject;
      this.label = label;      
      this.email = email;
      this.issuer = issuer;
      this.validfrom = validfrom;
      this.validto = validto;
      this.signature = signature;
      this.oid = oid;
      this.publiccert = publiccert;
      this.conf = conf;
      this.attributes = a;
    }
    @Expose
    String serial = null;
    @Expose
    String md5sum = null;
    @Expose
    String subject = null;   
    @Expose
    String label = null;
    @Expose
    String email = null;
    @Expose
    String issuer = null;
    @Expose
    String validfrom = null;
    @Expose
    String validto = null;
    @Expose
    String signature = null;
    @Expose
    String oid = null;
    @Expose
    String publiccert = null;
    @Expose
    Collection<ACInfo> attributes = null;
    String conf = null;    
  }

  public static HashMap<String, TokenInfo> 
          getTokenInfoMap(String tokenConfigPath, String tokenPIN, 
                          HashMap<String, Properties> vomses_map) {
            
    // Save the default list of providers
    Set<Provider> providersBackup = new HashSet<Provider>();
    providersBackup.addAll(Arrays.asList(Security.getProviders()));    

    HashMap<String, TokenInfo> result = new HashMap<String, TokenInfo>();

    File[] configs = new File(tokenConfigPath).listFiles();
    
    String DEFAULT_SSL_CERT_FILE = "/etc/grid-security/hostcert.pem";
    String DEFAULT_SSL_KEY_FILE = "/etc/grid-security/hostkey.pem";
    
    if (AxisProperties.getProperty("axis.socketSecureFactory") == null)
        AxisProperties.setProperty("axis.socketSecureFactory",
        "org.glite.security.trustmanager.axis.AXISSocketFactory");    
                
    java.util.Properties properties = 
        AXISSocketFactory.getCurrentProperties();
                
    properties.setProperty("sslCertFile", DEFAULT_SSL_CERT_FILE);
    properties.setProperty("sslKey", DEFAULT_SSL_KEY_FILE);
    AXISSocketFactory.setCurrentProperties(properties);    
    
    // Making a new Service
    org.glite.security.voms.service.admin.VOMSAdminServiceLocator locator =
    new org.glite.security.voms.service.admin.VOMSAdminServiceLocator();
    
    // Reading each tokenConf file..
    for (File tokenConf : configs) 
    {        
      // Creating an HashMap with:
      // SerialNumber (key)
      // [SERIAL, MD5SUM, SUBJECT, LABEL, ISSUER, VALIDFROM, VALIDTO, SIGNATURE, OID, PUBLICCERT] (Properties)
      HashMap<String, Properties> map = 
              getSerial2PropertiesMap(tokenConf.toString(), tokenPIN);

      log.debug(" "); 
      log.debug("CONTACTING each VOMS server to retrieve groups/roles");
            
      String[] VOMS_SERVERS = null;
      
      for (Map.Entry<String, Properties> entry : map.entrySet()) 
      {
        int count=0;
        Collection<ACInfo> attributes = null;       
        String[] email_address = null;        
        
        for (Map.Entry<String, Properties> voms : vomses_map.entrySet()) 
        {
            
          VOMS_SERVERS = voms.getValue()
                  .getProperty("VOMS_SERVER")
                  .split(";");
                    
          for (int i=0; i<VOMS_SERVERS.length; i++) 
          {
            log.debug(" ");            
            log.debug("=> Available VOMS per VO [ " + 
                       voms.getValue().getProperty("VOMS_SERVER") +
                      ", " + voms.getValue().getProperty("VOMS_NAME") + 
                      " ]");
          
            log.debug("Fetching FQANs for " +
                    entry.getValue().getProperty("SUBJECT"));
            
            String[] GroupsAndRoles = null;                    
          
            try {
                log.debug(" ");
                log.debug("[a.] Fetching GROUPS/ROLES from the VOMS Web Service");                
                GroupsAndRoles = VOMSUtils.listGroupsAndRoles(
                    entry.getValue().getProperty("SUBJECT"),
                    entry.getValue().getProperty("ISSUER"),
                    locator,
                    VOMS_SERVERS[i],                    
                    voms.getValue().getProperty("VOMS_NAME"));
                            
                if (GroupsAndRoles != null && GroupsAndRoles.length > 0) 
                {
                    log.debug("User DN FOUND in the " 
                    + voms.getValue().getProperty("VOMS_NAME") + " VO");
                    
                    log.debug(" ");    
                    log.info("[b.] Fetching e-mail address from the VOMS Web Service");
                    email_address = 
                        java.util.Arrays.asList(VOMSUtils.getEmailAddress(
                        entry.getValue().getProperty("SUBJECT"),
                        entry.getValue().getProperty("ISSUER"),
                        locator,
                        VOMS_SERVERS[i],                    
                        voms.getValue().getProperty("VOMS_NAME"))).toArray(new String[0]);
                    
                } else {                    
                    log.debug("User DN NOT FOUND in the " 
                    + voms.getValue().getProperty("VOMS_NAME") + " VO");
                    
                    count++; 
                    log.debug("[ count, max VOMS ] " 
                    + count + "," + vomses_map.size());
                }                                
            } catch (Exception e) { log.error(e); continue; }
          
            if (GroupsAndRoles != null) 
            {
            
              ACInfo info = new ACInfo(
                    voms.getValue().getProperty("VOMS_NAME"),
                    java.util.Arrays.asList(GroupsAndRoles));

                if (attributes == null) {
                  attributes = new ArrayList<ACInfo>();                  
                }
            
                attributes.add(info);
                break;
            }            
          } // loop VOMS Servers
        }
                
        if (count == vomses_map.size()) 
        {
            // 25022015 - Giuseppe LA ROCCA
            log.debug("");
            log.debug("This is fix for VOMS-Admin v3.3.2 ~ EMI 3 - Update 24");
            log.debug("Adding \"pseudo-generic\" FQANs for the robotID");
            String[] default_fqans = new String[] { "/empty" };
                  
            ACInfo info = new ACInfo(
            "novoms",
            java.util.Arrays.asList(default_fqans));
            
            if (attributes == null)
                  attributes = new ArrayList<ACInfo>();           
            
            if (email_address == null) {
                email_address = new String[1];
                email_address[0] = "no-reply@gmail.com"; 
            }
                  
            attributes.add(info);
            count=0;            
        }
                
        if (attributes!=null) {
            log.debug("");
            log.debug("Feeding the HashMap with the available FQAN(s) ...");
            log.debug("Key = " + entry.getKey());
            log.debug("MD5SUM = " + entry.getValue().getProperty("MD5SUM"));
            log.debug("LABEL = " + entry.getValue().getProperty("LABEL"));
            log.debug("EMAIL = " + email_address[0]);
            log.debug("ISSUER = " + entry.getValue().getProperty("ISSUER"));
            log.debug("VALIDFROM = " + entry.getValue().getProperty("VALIDFROM"));
            log.debug("VALIDTO = " + entry.getValue().getProperty("VALIDTO"));
            log.debug("ISSUER = " + entry.getValue().getProperty("SIGNATURE"));
            log.debug("OID = " + entry.getValue().getProperty("OID"));
            log.debug("PUBLICCERT = " + entry.getValue().getProperty("PUBLICCERT"));
            log.debug("eTOKEN CONF = " + tokenConf.toString());
            log.debug("FQANs = " + attributes.size());
            
            result.put(entry.getKey(), new TokenInfo(
                 entry.getKey(),
                 entry.getValue().getProperty("MD5SUM"),
                 entry.getValue().getProperty("SUBJECT"),
                 entry.getValue().getProperty("LABEL"),
                 email_address[0],                 
                 entry.getValue().getProperty("ISSUER"),
                 entry.getValue().getProperty("VALIDFROM"),
                 entry.getValue().getProperty("VALIDTO"),
                 entry.getValue().getProperty("SIGNATURE"),
                 entry.getValue().getProperty("OID"),
                 entry.getValue().getProperty("PUBLICCERT"),
                 tokenConf.toString(),
                 attributes));                
        }                
      } // loop entries
    }    
    
    locator = null;
    
    // Restore the original list of prividers
    Set<Provider> providers = new HashSet<Provider>();
    providers.addAll(Arrays.asList(Security.getProviders()));
    providers.removeAll(providersBackup);
    for (Provider p : providers) Security.removeProvider(p.getName());
    
    locator=null;
    return result;
  }

  public static HashMap<String, Properties> getSerial2PropertiesMap(String tokenConf, String tokenPIN) 
  {
      
    java.security.KeyStore keyStore = null;

    // Install the provider dynamically.
    // Create an instance of the provider with the appropriate configuration file
    Provider eToken_PKCS11Provider = null;

    eToken_PKCS11Provider = new sun.security.pkcs11.SunPKCS11(tokenConf);

    HashMap<String, Properties> result = null;

    try {
      Security.insertProviderAt(eToken_PKCS11Provider, 1);
      keyStore = java.security.KeyStore.getInstance("PKCS11", eToken_PKCS11Provider);

      // Token Login
      keyStore.load(null, tokenPIN.toCharArray());

      java.security.KeyPairGenerator keyGenerator =
      java.security.KeyPairGenerator.getInstance("RSA", eToken_PKCS11Provider);
      log.debug(" ");
      log.debug("--------------------------------------------------------");
      log.debug("Smart Card    " + keyGenerator.getProvider().getInfo());
      log.debug("Provider Name " + keyGenerator.getProvider().getName());
      log.debug("Version       " + keyGenerator.getProvider().getVersion());
      log.debug("Size          " + keyGenerator.getProvider().size());

      // Retrieving certificate chains from e-Token
      // Get a list of key item(s) from the e-Token if any.
      Enumeration aliases = keyStore.aliases();

      // Check about the number of item(s) available on board of the eToken.
      if (keyStore.size() > 0)
      log.debug(keyStore.size() + " robot(s) detected ");
      else log.error(" No robot(s) detected ");
      log.debug("--------------------------------------------------------");

      result = new HashMap<String, Properties>();

      for (Enumeration e = aliases; e.hasMoreElements();) {

        String alias = (String) e.nextElement();
        java.security.cert.X509Certificate cert = 
                ((java.security.cert.X509Certificate) keyStore.getCertificate(alias));

        String md5sum = 
            TokenUtils.getMD5("/" + PrincipalUtil.getSubjectX509Principal(cert).getName().replaceAll(",", "/"));
                
        Properties p = new Properties();
        p.setProperty("SERIAL", cert.getSerialNumber().toString());
        p.setProperty("LABEL", alias);
        p.setProperty("SUBJECT", "/" + PrincipalUtil.getSubjectX509Principal(cert).getName().replaceAll(",", "/"));
        p.setProperty("MD5SUM", md5sum);
        p.setProperty("ISSUER", "/" + PrincipalUtil.getIssuerX509Principal(cert).getName().replaceAll(",", "/"));
        p.setProperty("VALIDFROM", cert.getNotBefore().toString());
        p.setProperty("VALIDTO", cert.getNotAfter().toString());
        p.setProperty("SIGNATURE", cert.getSigAlgName().toString());
        p.setProperty("OID", cert.getSigAlgOID().toString());
        p.setProperty("PUBLICCERT", cert.getPublicKey().toString());        
        
        // Feeding the HashMap 
        result.put(cert.getSerialNumber().toString(), p);        
        //result.put(md5sum, p);
      }
    } catch (Exception e) { log.error(e.getMessage()); }

    Security.removeProvider(eToken_PKCS11Provider.getName());
    return result;
  } // End method

  public static boolean createProxy(
          String filename, int keybit, Boolean rfc, int lifetime,
          String serialNumber, String smtp_host, String email, 
          String sender_email, String expiration,
          String CN_label, String tokenConf, String tokenPIN) 
  {

    boolean result = false;

    Provider provider = new sun.security.pkcs11.SunPKCS11(tokenConf);

    KeyPair_Cert tokenData  = getTokenData(
             serialNumber, 
             smtp_host, email, sender_email, expiration, 
             tokenPIN, provider);

    if (tokenData != null) {

      Security.addProvider(provider);
      KeyPair_Cert reqData = createProxyCertificate(
                             keybit, rfc, lifetime, CN_label, 
                             tokenData.getX509Cert(), 
                             tokenData.getPrivate());

      if (reqData != null) 
      {

        File proxyFile = new File(filename);
        org.bouncycastle.openssl.PEMWriter pemWriter = null;
        log.debug("Saving self-signed proxy certificate in " + filename);
        
        try {
          java.io.FileOutputStream fos = new FileOutputStream(proxyFile);
          pemWriter = new PEMWriter(new OutputStreamWriter(fos));

          // Export the certificate request
          pemWriter.writeObject(reqData.getX509Cert());

          // Export the Private Key used to sign the request
          pemWriter.writeObject(reqData.getPrivate());

          // Export the User Certificate from the token
          byte[] encoding = tokenData.getX509Cert().getEncoded();

          pemWriter.write("-----BEGIN CERTIFICATE-----\n");

          char[] bufX = new char[64];
          encoding = Base64.encode(encoding);

          for (int i = 0; i < encoding.length; i += bufX.length) 
          {
            int index = 0;
            while (index != bufX.length) {
              if ((i + index) >= encoding.length)
                break;
              
              bufX[index] = (char) encoding[i + index];
              index++;
            }
            
            pemWriter.write(bufX, 0, index);
            pemWriter.write("\n");
          }

          pemWriter.write("-----END CERTIFICATE-----\n");
          pemWriter.close();
          
          log.info("Setting file permissions for [ " + proxyFile + " ]");
               
          Process p = Runtime.getRuntime().exec("chmod 600 " + proxyFile);
          int cmdExitCode = p.waitFor();
          if (cmdExitCode == 0) log.info("Setting file permissions [ DONE ] ");
          else log.info("Setting file permissions [ FAILED ] ");
                              
          result = true;
        } catch (Exception e) { log.error(e.getMessage()); } 
        /*finally {   
            try { pemWriter.close(); } catch (IOException ex) 
            {
                java.util.logging.Logger
                        .getLogger(TokenUtils.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }*/
      }
      
      Security.removeProvider(provider.getName());
    }

    return result;
  } // End method
  
  public static String getMD5(String input)
  {
    String res = "";
    try {
        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(input.getBytes());
        byte[] md5 = algorithm.digest();
        String tmp = "";
        
        for (int i = 0; i < md5.length; i++) {
            tmp = (Integer.toHexString(0xFF & md5[i]));
                if (tmp.length() == 1) res += "0" + tmp;
                else res += tmp;                    
        }
    } catch (NoSuchAlgorithmException ex) { log.error(ex.getMessage()); }
    return res;
 } // End method
  
 public static boolean isValidEmailAddress(String email) 
 {
   boolean result = true;
 
   try {
      InternetAddress emailAddr = new InternetAddress(email);
      emailAddr.validate();
   } catch (AddressException ex) 
   {
       log.error(ex.getMessage());
       result = false;
   }
   return result;
} 
  
 public static void sendHtmlEmail(String email, String sender_email, 
                                  String smtp_host, String serial, 
                                  String subject)
 {
     
     log.debug("[ ALERT ] Sending e-mail notification [ ALERT ]");
     
     // Assuming you are sending email from localhost
     String HOST = "localhost";
     
     // Get system properties
     Properties properties = System.getProperties();
     properties.setProperty(smtp_host, HOST);
     
     // Get the default Session object.
     Session session = Session.getDefaultInstance(properties);
      
     try {
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(sender_email));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
         
         // Set Subject: header field
         message.setSubject(" [ eTokenServer notification ] ");

         // Send the actual HTML message, as big as you like
         message.setContent(
         "<br/><H4>eTokenServer email notification service" + "</H4><hr><br/>" +
         "This is an automatic message sent to inform you that this " +
         "certificate expired or is going to expire!<br/><br/>" +
         "See below certificate details:<br/><br/>" +
         " - SerialNumber = " + serial + "<br/>" +
         " - Subject      = " + subject +  "<br/><br/>" +
         "Please contact your RA to renew the certificate as soon as possible.</br>",
         "text/html");

         // Send message
         Transport.send(message);         
      } catch (MessagingException mex) { log.error(mex.getMessage()); }
 } // End method  
 
 public static boolean doHTTPRequest (String ETOKEN_SERVER, 
                                      int ETOKEN_PORT, 
                                      String proxy) 
         throws Exception
 {
    boolean list = false;
    String request = "";

    log.debug(" ");
    HttpClient httpclient = new DefaultHttpClient();
    HttpGet httpget = null;
            
    try {
        
        // A C T I O N =>  [CONTACTING MYPROXY SERVER]        
        request = "http://"
                  + ETOKEN_SERVER +  ":" + ETOKEN_PORT
                  + "/MyProxyServer/proxy/"
                  + proxy;
                
        httpget = new HttpGet(request);
        log.debug(request);

        // Create a response handler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpclient.execute(httpget, responseHandler);
                
        if (!responseBody.isEmpty()) list = true;                
                
    } finally {
        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();
    }
    
    return list;
}
} // End Class

