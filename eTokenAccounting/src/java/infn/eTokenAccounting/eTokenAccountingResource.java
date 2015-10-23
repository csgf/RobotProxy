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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;

public class eTokenAccountingResource {

  private Properties properties;
  private String requestKey;
  private Logger log = Logger.getLogger("infn.eTokenAccounting");
    
  /** Creates a new instance of eTokenAccountingResource */
  private eTokenAccountingResource(String key, Properties p) 
  {
    properties = p;
    requestKey = key;       
  }

  /** Get instance of the eTokenAccountingResource */
  public static eTokenAccountingResource getInstance(String key, Properties p) 
  {
    // The user may use some kind of persistence mechanism
    // to store and restore instances of eTokenAccountingResource class.
    return new eTokenAccountingResource(key, p);
  }

  /**
   * Retrieves representation of an instance of infn.eTokenAccounting.MyProxyResource
   * @return an instance of java.lang.String
   */
  @GET
  @Produces("text/plain")
  public String getText(@Context HttpServletRequest requestContext) 
  {
      String result = null;
      
        try {
            Date currentDate = new Date();
            currentDate.setTime (currentDate.getTime());
        
            log.debug("Accepted a new connection from " 
                + requestContext.getRemoteAddr().toString());
           
            log.debug("=> Received a new request...");
            log.debug("Start processing at " + currentDate);            
            
            if (properties.getProperty("TYPE").equals("totalStatistics"))                
                result = doCreateAccounting(properties);
            
            if (properties.getProperty("TYPE").equals("totalHits"))
                result = doCreateHits(properties);
                        
        } catch (ClassNotFoundException ex) {
            java.util.logging
                    .Logger
                    .getLogger(eTokenAccountingResource.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return result;        
  }
  
  
  private String doCreateAccounting(Properties properties) throws ClassNotFoundException 
  {
    String result = "";
    
    synchronized (eTokensAccountingResourceSingleton.getInstance()) 
    {            
            try {
                log.debug(" ");                
                log.debug("[ Reading preferences ]");
                log.debug("DATABASE_PATH = " + properties.getProperty("DATABASE_PATH"));
                log.debug("DATABASE_FILE = " + properties.getProperty("DATABASE_FILE"));
                log.debug("MD5SUM = " + properties.getProperty("MD5SUM"));
                log.debug("DATE-IN-YEAR = " + properties.getProperty("DATE-IN-YEAR"));
                log.debug("DATE-IN-MONTH = " + properties.getProperty("DATE-IN-MONTH"));                
                log.debug("DATE-OUT-YEAR = " + properties.getProperty("DATE-OUT-YEAR"));
                log.debug("DATE-OUT-MONTH = " + properties.getProperty("DATE-OUT-MONTH"));                
                log.debug("TYPE = " + properties.getProperty("TYPE"));
                
                Class.forName("org.sqlite.JDBC");
                Connection conn = DriverManager.getConnection("jdbc:sqlite:" 
                                    + properties.getProperty("DATABASE_PATH") 
                                    + "/" 
                                    + properties.getProperty("DATABASE_FILE"));
                
                Statement stat = conn.createStatement();

                stat = conn.createStatement();
                String query = "SELECT strftime('%Y', dateInclusion) AS Year," 
                        + "strftime('%m', dateInclusion) AS Month," 
                        + "strftime('%d', dateInclusion) AS Day," 
                        + "ROBOTID, COUNT (ROBOTID) AS Total " 
                        + "FROM etoken_metadata WHERE ROBOTID like '%" 
                        + properties.getProperty("MD5SUM") + "%' " 
                        + "AND Year>='" + properties.getProperty("DATE-IN-YEAR") + "' " 
                        + "AND Month>='" + properties.getProperty("DATE-IN-MONTH") + "' " 
                        + "AND Year<='" + properties.getProperty("DATE-OUT-YEAR") + "' " 
                        + "AND Month<='" + properties.getProperty("DATE-OUT-MONTH") + "' " 
                        + "GROUP BY Year, Month, Day, ROBOTID ORDER BY ROBOTID";

                ResultSet rs = stat.executeQuery(query);

                result = "[";
                while (rs.next())
                    result  += "[\""
                        + rs.getString("Year") + "-"
                        + rs.getString("Month") + "-"
                        + rs.getString("Day") + "\"" + ","
                        + rs.getString("Total")
                        + "],";

                // Skip the last ',' from result
                result = result.substring(0, result.length()-1);
                result += "]";

                // Getting JSON
                log.debug(result);
                log.debug("");

                rs.close();
                conn.close();
                                                
            } catch (SQLException ex) {
                java.util.logging
                        .Logger
                        .getLogger(eTokenAccountingResource.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
    }
    
    return result;
  }
  
  private String doCreateHits(Properties properties) throws ClassNotFoundException 
  {
    String result = "";
    
    synchronized (eTokensAccountingResourceSingleton.getInstance()) 
    {            
            try {
                log.debug(" ");                
                log.debug("[ Reading preferences ]");
                log.debug("DATABASE_PATH = " + properties.getProperty("DATABASE_PATH"));
                log.debug("DATABASE_FILE = " + properties.getProperty("DATABASE_FILE"));
                log.debug("MD5SUM = " + properties.getProperty("MD5SUM"));
                log.debug("DATE-IN-YEAR = " + properties.getProperty("DATE-IN-YEAR"));
                log.debug("DATE-IN-MONTH = " + properties.getProperty("DATE-IN-MONTH"));                
                log.debug("DATE-OUT-YEAR = " + properties.getProperty("DATE-OUT-YEAR"));
                log.debug("DATE-OUT-MONTH = " + properties.getProperty("DATE-OUT-MONTH"));                
                log.debug("TYPE = " + properties.getProperty("TYPE"));
                
                Class.forName("org.sqlite.JDBC");
                Connection conn = DriverManager.getConnection("jdbc:sqlite:" 
                                    + properties.getProperty("DATABASE_PATH") 
                                    + "/" 
                                    + properties.getProperty("DATABASE_FILE"));
                
                Statement stat, stat2 = conn.createStatement();

                stat = conn.createStatement();
                String query = "SELECT DISTINCT(ROBOTID) AS ROBOTID,"                        
                        + "strftime('%Y', dateInclusion) AS Year," 
                        + "strftime('%m', dateInclusion) AS Month," 
                        + "strftime('%d', dateInclusion) AS Day," 
                        + "COUNT (HITS) As Total "
                        + "FROM etoken_metadata WHERE "                        
                        + "Year>='" + properties.getProperty("DATE-IN-YEAR") + "' " 
                        + "AND Month>='" + properties.getProperty("DATE-IN-MONTH") + "' " 
                        + "AND Year<='" + properties.getProperty("DATE-OUT-YEAR") + "' " 
                        + "AND Month<='" + properties.getProperty("DATE-OUT-MONTH") + "' " 
                        + "GROUP BY ROBOTID ORDER BY ROBOTID";

                ResultSet rs = stat.executeQuery(query);

                result = "[";
                while (rs.next()) {
                    
                    // Get the label for each ROBOTID
                    stat2 = conn.createStatement();
                    String query_label = "SELECT label FROM etoken_md5sum "
                                 + "WHERE ROBOTID = " 
                                 + "'" + rs.getString("ROBOTID") + "'";

                    ResultSet rs_label = stat2.executeQuery(query_label);                    

                    result  += "[\""
                        + rs_label.getString("label").trim() + "\"" + ","
                        + rs.getString("Total")
                        + "],";
                    
                    rs_label.close();
                }

                // Skip the last ',' from result
                result = result.substring(0, result.length()-1);
                result += "]";

                // Getting JSON
                log.debug(result);
                log.debug("");

                rs.close();
                conn.close();
                                                
            } catch (SQLException ex) {
                java.util.logging
                        .Logger
                        .getLogger(eTokenAccountingResource.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
    }
    
    return result;
  }
  
  
  
    
  /**
   * PUT method for updating or creating an instance of eTokenAccountingResource
   * @param content representation for the resource
   * @return an HTTP response with content of the updated or created resource.
   */
  @PUT
  @Consumes("text/plain")
  public void putText(String content) { }

  /**
   * DELETE method for resource eTokenAccountingResource
   */
  @DELETE
  public void delete() { }
}
