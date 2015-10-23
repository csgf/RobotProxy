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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

import java.util.Map;
import java.util.Properties;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/")
public class ProxiesResource {

    @Context
    private UriInfo context;

    /** Creates a new instance of ProxiesResource */
    public ProxiesResource() { }

    /**
     * Retrieves representation of an instance 
     * of infn.eToken.ProxiesResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    public String getText(@QueryParam("format") String format) {

        boolean isJSON = format != null && format.toLowerCase().equals("json");

        String result = new String();

        if (isJSON) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            result = gson.toJson(
                    ProxiesResouceSingleton.getInstance().getTokensMap().values());
        } else {
            for (Map.Entry<String, TokenUtils.TokenInfo> info :
                    ProxiesResouceSingleton
                    .getInstance()
                    .getTokensMap()
                    .entrySet()) 
            {

                result = result.concat("["
                        //+ info.getValue().serial
                        + info.getValue().md5sum
                        + "]\t\t"
                        //+ info.getValue().label
                        + info.getValue().subject +
                        "\n");
            }
        }
        return result;
    }

    /** Sub-resource locator method for Proxy  */
    //@Path("{serial}")
    @Path("{md5sum}")
    public ProxyResource getProxyResource(
            //@PathParam("serial") String serial,
            @PathParam("md5sum") String md5sum,
            @QueryParam("voms") String voms,            
            @QueryParam("proxy-renewal") String proxyrenewal,
            @QueryParam("disable-voms-proxy") String vomsproxy,
            @QueryParam("rfc-proxy") String rfcproxy,
            @QueryParam("dirac-token") String diractoken,
            @DefaultValue("eToken:Empty") @QueryParam("cn-label") String cnlabel)
    {

        Properties properties =
                new Properties(ProxiesResouceSingleton.getInstance().getProperties());
        
        boolean disableVOMSProxy = vomsproxy != null
                && ((vomsproxy.toLowerCase().equals("true")
                || vomsproxy.toLowerCase().equals("yes")
                || vomsproxy.toLowerCase().equals("enable")
                || vomsproxy.toLowerCase().equals("on")));
        
        boolean RFC_proxy = rfcproxy != null
                && ((rfcproxy.toLowerCase().equals("true")
                || rfcproxy.toLowerCase().equals("yes")
                || rfcproxy.toLowerCase().equals("enable")
                || rfcproxy.toLowerCase().equals("on")));
        
        boolean DIRAC_token = diractoken != null
                && ((diractoken.toLowerCase().equals("true")
                || diractoken.toLowerCase().equals("yes")
                || diractoken.toLowerCase().equals("enable")
                || diractoken.toLowerCase().equals("on")));
        
        String FQANs = null;
        
        if (!disableVOMSProxy) {
            FQANs = VOMSUtils.parseVOMSFQAN(voms, " ");

            if (FQANs == null) {

                ResponseBuilderImpl builder = new ResponseBuilderImpl();
                builder.status(Response.Status.NOT_ACCEPTABLE);
                builder.entity("Fail to parse FQANs in your request!");

                Response response = builder.build();
                throw new WebApplicationException(response);
            } else {            
                properties.setProperty("VOMS_FQAN", FQANs);
            }
        }        
        //properties.setProperty("TOKEN_SERIAL", serial);
        properties.setProperty("MD5SUM", md5sum);

        properties.setProperty("ENABLE_PROXY_RENEWAL",
                proxyrenewal != null && ((proxyrenewal.toLowerCase().equals("true")
                || proxyrenewal.toLowerCase().equals("yes")
                || proxyrenewal.toLowerCase().equals("enable")
                || proxyrenewal.toLowerCase().equals("on"))) ? "TRUE" : "FALSE");

        properties.setProperty("DISABLE_VOMS_PROXY",
                disableVOMSProxy ? "TRUE" : "FALSE");
        
        properties.setProperty("ENABLE_RFC_PROXY",
                RFC_proxy ? "TRUE" : "FALSE");
        
        properties.setProperty("ENABLE_DIRAC_TOKEN",
                RFC_proxy ? "TRUE" : "FALSE");
                
        properties.setProperty("ENABLE_CN_LABEL", cnlabel);

        //return ProxyResource.getInstance(serial + ":" + voms, properties);
        //return ProxyResource.getInstance(serial + ":" + voms + vomsproxy, properties);
        //return ProxyResource.getInstance(md5sum + ":" + voms + vomsproxy, properties);
        return ProxyResource.getInstance(md5sum + ":" 
                + voms 
                + vomsproxy 
                + rfcproxy
                + cnlabel,                 
                properties);
    }
}
