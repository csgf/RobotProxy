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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Properties;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/")
public class MyProxiesResource {

    @Context
    private UriInfo context;

    /** Creates a new instance of MyProxiesResource */
    public MyProxiesResource() { }

    /**
     * Retrieves representation of an instance 
     * of infn.eToken.MyProxiesResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    public String getText(@QueryParam("format") String format) 
    {
        String result = new String();
        
        //Retrieving MyProxyServer settings in JSON format only
        boolean isJSON = format != null && format.toLowerCase().equals("json");
        
        if (isJSON) {
            Gson gson = 
                    new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            
            result = gson.toJson(MyProxiesResourceSingleton
                                 .getInstance()
                                 .getProperties());                                                       
        }
               
        return result;                
    }

    /**
     * Sub-resource locator method for MyProxy
     */    
    @Path("{file}")
    public MyProxyResource getMyProxyResource(            
            @PathParam("file") String file)
    {
        Properties properties =
                new Properties(MyProxiesResourceSingleton
                .getInstance()
                .getProperties());
                
        return MyProxyResource.getInstance(file, properties);        
    }
}
