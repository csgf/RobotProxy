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

import java.util.Properties;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/")
public class eTokensAccountingResource {

    @Context
    private UriInfo context;

    /** Creates a new instance of eTokensAccountingResource */
    public eTokensAccountingResource() { }
    
    /**
     * Sub-resource locator method for eTokenAccounting
     */    
    @Path("{md5sum}")
    public eTokenAccountingResource geteTokenAccountingResource(            
            @PathParam("md5sum") String md5sum,
            @QueryParam("datein-year") String dateinyear,
            @QueryParam("datein-month") String dateinmonth,            
            @QueryParam("dateout-year") String dateoutyear,
            @QueryParam("dateout-month") String dateoutmonth,
            @QueryParam("type") String type)
    {
        Properties properties =
                new Properties(eTokensAccountingResourceSingleton
                .getInstance()
                .getProperties());
        
        properties.setProperty("MD5SUM", md5sum);
        properties.setProperty("DATE-IN-YEAR", dateinyear);
        properties.setProperty("DATE-IN-MONTH", dateinmonth);        
        properties.setProperty("DATE-OUT-YEAR", dateoutyear);
        properties.setProperty("DATE-OUT-MONTH", dateoutmonth);
        properties.setProperty("TYPE", type);
                
        return eTokenAccountingResource.getInstance(md5sum, 
                properties);
    }
}
