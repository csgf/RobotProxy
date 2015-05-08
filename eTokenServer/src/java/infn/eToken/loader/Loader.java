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

package infn.eToken.loader;

import infn.eToken.ProxiesResouceSingleton;
import javax.servlet.ServletException;

public class Loader extends com.sun.jersey.spi.container.servlet.ServletContainer {

  @Override
  public void init() throws ServletException {
    super.init();
    ProxiesResouceSingleton.getInstance();
  }
}
//    TimerTask FQANSPollingTask = new TimerTask() {
//
//      @Override
//      public void run() {
//        synchronized (ProxiesResouceSingleton.getInstance()) {
//
//          HashMap<String, TokenUtils.TokenInfo> map = TokenUtils.getTokenInfoMap(
//                  ProxiesResouceSingleton.getInstance().getProperties().getProperty("ETOKEN_CONFIG_PATH"),
//                  ProxiesResouceSingleton.getInstance().getProperties().getProperty("PIN"),
//                  ProxiesResouceSingleton.getInstance().getVOMSesMap());
//
//          if (map != null && !map.isEmpty()) {
//            ProxiesResouceSingleton.getInstance().getTokensMap().clear();
//            ProxiesResouceSingleton.getInstance().getTokensMap().putAll(map);
//          }
//        }
//      }
//    };
//
//    new java.util.Timer().schedule(FQANSPollingTask, 0,
//            1000 * Long.parseLong(ProxiesResouceSingleton.getInstance().getProperties().getProperty("FQANS_POLLING_SCHEDULE")));
//  }
//}
