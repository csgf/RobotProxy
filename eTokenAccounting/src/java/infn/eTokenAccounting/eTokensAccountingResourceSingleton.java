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
package infn.eTokenAccounting;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class eTokensAccountingResourceSingleton 
{

  private static eTokensAccountingResourceSingleton instance = null;
  private Properties properties;  
  
  private static Logger log = 
          Logger.getLogger(eTokensAccountingResourceSingleton.class);  

  private eTokensAccountingResourceSingleton() 
  {
        
    try {
      properties = new Properties();
      properties.load(
        this.getClass()
              .getClassLoader()
              .getResourceAsStream("infn/eTokenAccounting/Accounting.properties"));
                                  
    } catch (IOException ex) { 
      log.error (ex);
    }
  }
  
  public static synchronized eTokensAccountingResourceSingleton getInstance() 
  {
    if (instance == null) {      
      instance = new eTokensAccountingResourceSingleton();      
      log.debug("The eTokenAccounting servlet has started!"); 
      log.debug("Waiting for incoming requests...");
    }
    return instance;
  }

  public Properties getProperties() 
  {
    return properties;
  }   
}
