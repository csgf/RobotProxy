<%-- 
/**************************************************************************
    Copyright (c) 2010-2015:
    Istituto Nazionale di Fisica Nucleare (INFN), Italy
    Consorzio COMETA (COMETA), Italy

    See https://www.infn.it and https://www.consorzio-cometa.it for details
    on the copyright holders.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    Author(s): Giuseppe La Rocca (INFN), Salvatore Monforte (INFN)
     ****************************************************************************/
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page trimDirectiveWhitespaces="true"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
  "https://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
  <link rel="shortcut icon" type="image/png"
        href="https://www.infn.it/favicon.ico"/>
  <title>[ eTokenServer ]</title>
    <style type="text/css">
      body, html { font-family: Tahoma,Verdana,sans-serif,Arial; font-size: 14px; }
      #selected-attributes .ui-selecting { background: #FECA40; }
      #selected-attributes .ui-selected { background: #F39814; color: white; }
      #selected-attributes, #sorted-attributes { list-style-type: none; margin: 0; padding: 0; width: 50%; }
      #selected-attributes li, #sorted-attributes li { margin: 3px; padding: 4px; font-size: 12px; height: 16px; }
      #steps img { width:32px; border:0; }
      
      .ui-accordion-header.ui-state-active {
        /* FF3.6+ */
        background-image: -moz-linear-gradient(top, #C41E3A 100%, #DC621E 10%);
        /* Chrome, Safari4+ */
        background-image: -webkit-gradient(linear, left top, left bottombottom, color-stop(0%, #C41E3A), color-stop(10%, #DC621E));
        /* Chrome10+, Safari5.1+ */
        background-image: -webkit-linear-gradient(top, #C41E3A 100%, #DC621E 10%);
        /* Opera 11.10+ */
        background-image: -o-linear-gradient(top, #C41E3A 100%, #DC621E 10%);
        /* IE10+ */
        background-image: -ms-linear-gradient(top, #C41E3A 100%, #DC621E 10%);
        /* W3C */
        background-image: linear-gradient(to bottombottom, #C41E3A 100%, #DC621E 10%);
        /* IE6-9 */
        filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#C41E3A', endColorstr='#DC621E',GradientType=0 );
        top: 1px;
        margin: 0px;
        border: 10px !important;
      }
      .ui-accordion-content {
        margin-top: 2px !important;
        margin-bottom: 3px !important;
        box-shadow: inset 0px -1px 0px 0px rgba(0, 0, 0, .4),
                    inset 0px 1px 1px 0px rgba(0, 0, 0, .2);
      }
      .ui-state-active a, .ui-state-active a:link, .ui-state-active a:visited {
        color: #FFFFFF !important;
      }
      .ui-accordion .ui-accordion-header a {
        font-family: "Tahoma, Verdana,sans-serif, Arial" !important;
        font-size: 15px !important;
      }

      img.architecture { width:420px !important; height: 350px !important; }
      img.logo { width:125px !important; height: 30px !important; }
      img.locker { width:50px !important; border:0; }
      md5sum { backgroud-color: orange; }      

    </style>   
    <link href="ui/css/ticker.css" rel="stylesheet" type="text/css"/>
    <!--link href="ui/css/overcast/jquery-ui-1.8.13.custom.css" rel="stylesheet" type="text/css"/-->
    <link href="ui/css/ui-lightness/jquery-ui-1.8.13.custom.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="ui/js/jquery-1.5.1.min.js"></script>
    <script type="text/javascript" src="ui/js/jquery-ui-1.8.13.custom.min.js"></script>
    <script type="text/javascript" src="ui/js/jQuery.rollChildren.js"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    
    <script  type="text/javascript" >
      var attributes = {};
      var associativeArray = [];

      $(document).ready(function() {  
      	$('#footer').rollchildren({  
		delay_time 	   : 3000,  
		loop 		   : true,  
		pause_on_mouseover : true,  
		roll_up_old_item   : true,  
		speed		   : 'slow'   
		});  
	}); 

      $(function()
      {
	var ticker = function()
	{
		setTimeout(function(){
		$('#ticker li:first').animate( {marginTop: '-120px'}, 800, function()
		{
			$(this).detach().appendTo('ul#ticker').removeAttr('style');
		});
		ticker();
	}, 6000);
	};
	ticker();
      });
      
      function enableCN(f)
      {
          if ($('#enable-cn-label').attr('checked')) {
                  $('input[name="cn-label"]').removeAttr('disabled');
                  $('#rfc-proxy').attr("checked",true);
          } else {
                  $('input[name="cn-label"]').val('Empty');
                  $('input[name="cn-label"]').attr('disabled','disabled');
                  $('#rfc-proxy').attr("checked",false);
          }
      }
      
      function enableDirac(f)
      {
          if ($('#dirac-token').attr('checked')) {
                $('#disable-voms-proxy').attr("checked",false);
                $('#rfc-proxy').attr("checked",false);
                $('#enable-cn-label').attr("checked",false);
          }
      }

      function calling_restAPI()
      {
        var rest = $('#request').html();
        var decoded = rest.replace(/&amp;/g, '&');
        console.log("Calling Rest API = " + decoded);

        $.ajax({
            url: decoded,
            type: 'GET',

	    beforeSend: function() {
                $('#loading')
                .html("<img src='images/spinner.gif' width='40' /> Please wait! This operation may take time.");
            },

            complete: function() {
                var content = "<table border='0'>";
                content += "<tr><td>";
                content += "<img src='images/locker.png' class='locker' /> Your proxy has been created";
                content += "</td><td>&nbsp;</td>";
                content += "<td><div id='divrequest' style='display:none;'>";
                content += "<a class='linkrequest' target='_blank' href='#'>";
                content += "<img src='images/link.png' class='locker'/></a>Get your proxy";
                content += "</div>";
                content += "</td></tr>";
                content += "</table>";

                // Display content
                $('#loading')
                .html(content)
                .show();
                
		// Add the new hlink
                $("a.linkrequest").attr("href", decoded);
                $("#divrequest").show();
                
                // Show the new rest API
                $('#linkrequest').attr("href",decoded);
                $('#linkrequest').show();
            },
            
            success: function(html) {

                $('#proxy').html(html);
                $('#proxy').show();
                console.log(html);
            }
        });
      }

      function update_request()
      {
        // Check if at least one AC have been selected.
        if( $( "#selected-attributes li.ui-selected").size() == 0) {
            if ($('input[name="disable-voms-proxy"]').attr('checked')== false) {
            $( "#steps div[name='error2']" ).show();
            $( "#steps" ).accordion('activate', 1);
            }
        } else {
          // For each selected AC(s), we update the request.
          var request ="<%=request.getRequestURL()%>eToken/";
          request +=  $("select#eToken option:selected").first().attr('value');
          var voms =[];
          $( "#sorted-attributes li").each(function() {
            voms.push($(this).attr('name') + ":"+ $(this).html());
          });
          request += "?voms=" + voms.join('+');
          request += "&proxy-renewal=" + $('input[name="long-proxy"]').attr('checked');
          request += "&disable-voms-proxy=" + $('input[name="disable-voms-proxy"]').attr('checked');
          request += "&rfc-proxy=" + $('input[name="rfc-proxy"]').attr('checked');          
          request += "&cn-label=eToken:" + $('input[name="cn-label"]').val();
          //request += "&dirac-token=" + $('input[name="dirac-token"]').attr('checked');
          
          $('#request').html(request);

          // Remove old entries
          $('#loading').html('');
          $('#divrequest').hide();
	  $('#proxy').html('');
          $('#proxy').hide();

        }
      }
      
      function showACAttributes(md5sum) {
         
        $( "#selected-attributes li").each(function() {$(this).remove();});
        $.each(attributes[md5sum], function(i,item){
          var vo = item.vo;
          $.each(item.fqans, function(i,fqan){
            $("<li />", {
              'name':vo,
              'class': 'ui-widget-content',
              html:fqan
            }).appendTo($('div#attributes ul'));
          });
        });
      }
      
      $(function(){
        $.getJSON('/eTokenServer/eToken?format=json', function(data) {
          var options = [];          
	  
          options.push('<option value=-1>Select one certificate from the drop-down list below</option>');
	  
          $.each(data, function(i,item) {
            //options.push('<option value="' + item.serial + '">' + item.label + '</option>');
            options.push('<option value="' + item.md5sum + '">' + item.subject + '</option>');
            attributes[item.md5sum]=item.attributes;

	    // Create an Array to hold all the certificate info.
	    associativeArray[item.md5sum] = item;
	    
          });
          
          $('select#eToken').html(options.join(''));
          showACAttributes($("select#eToken option").first().attr('value'));
          
          $( "#steps" ).accordion('resize');
        });
        
        // Onchange event retrieve the list of ACs for the given serial number
        $('select#eToken').bind('change', function() {
          var md5sum =  $("select#eToken option:selected").first().attr('value');
	  $('#details').css("display", "inline");

	  if (md5sum!=-1) {	  
	  // Removing old info
	  $('#details').html('');
	  var to = (associativeArray[md5sum].validto).split(" ");
	  var d1 = new Date();
	  var d2 = new Date(to[1]+" "+to[2]+", "+to[5]);

	  var days = 1000*60*60*24;
	  var diff = Math.ceil((d2.getTime()-d1.getTime())/(days));
	  
	  // Showing new details
	  $('#details').append("\nSerial\t\t=\t" + associativeArray[md5sum].serial + "\n");
	  $('#details').append("Label\t\t=\t" + associativeArray[md5sum].label + "\n");
          $('#details').append("MD5Sum\t\t=\t" + associativeArray[md5sum].md5sum + "\n");
	  $('#details').append("Subject\t\t=\t" + associativeArray[md5sum].subject + "\n");
	  $('#details').append("Issuer\t\t=\t" + associativeArray[md5sum].issuer + "\n");
	  $('#details').append("Valid from\t=\t" + associativeArray[md5sum].validfrom + "\n");
	  $('#details').append("Valid to\t\t=\t" + associativeArray[md5sum].validto + "\n");
          if (diff>0) $('#details').append("Expiration\t\t=\tThis certificate will expire in " + diff + " days! \n\n");
          else $('#details').append("Expiration\t\t=\tThis certificate expired " + Math.abs(diff) + " days ago! \n\n");
	  $('#details').append("Signature\t\t=\t" + associativeArray[md5sum].signature + "\n");
	  $('#details').append("OID\t\t\t=\t" + associativeArray[md5sum].oid + "\n");
	  $('#details').append("Public\t\t=\t" + associativeArray[md5sum].publiccert + "\n");
	    
          showACAttributes(md5sum);
          } else {
                $('#details').html('');
                $('#details').hide();
                showACAttributes(-1);
          }
        });
        
        $( "#selected-attributes" ).selectable({stop: function() {
            $( "#sorted-attributes li").each(function() {$(this).remove();});
            $( "#selected-attributes li.ui-selected").each(function(i, item) {
              $(item).clone().appendTo($( "#sorted-attributes"));
            });
          }});
        $( "#sorted-attributes" ).sortable({placeholder: "ui-state-highlight"}).enableSelection();        
      });
      
      $(function() {
        $( "#steps" ).accordion({autoHeight: false, navigation: true});
        $('.ui-accordion').bind('accordionchange', function(event, ui) {
                    
        //alert($('input[name="disable-voms-proxy"]').attr('checked'));                    
          if ($("#selected-attributes li.ui-selected").size() == 0)       
          if ($('input[name="disable-voms-proxy"]').attr('checked')==true) {
            $( "#steps div[name='error3']" ).show();
          }
          else {
               $( "#steps div[name='error2']" ).hide();
               $( "#steps div[name='error3']" ).hide();
          }
          if ($(ui.newContent).find('#request').size() == 1) {
            update_request();
          }
        });
      });
</script>

<style>
.slide-out-div {
    padding: 5px;
    width: 250px;
    background: white;
    border: 1px solid #29216d;
}
</style>
</head>
  
<body>
<table border="0">
<tr>
<td>
<h1>Create your proxy request in few steps!</h1>
<div style="width: 600px;">
<p align="justify">
A standard-based solution developed by the INFN Catania for central management of robot
credentials and provisioning of digital proxies to get seamless and secure access to computing
e-Infrastructures supporting the X.509 standard for Authorisation.<br/><br/>
This is a servlet based on the Java&trade; Cryptographic Token Interface Standard (PKCS#11).<br/>
For any further information, please visit the official Java&trade; PKCS#11 Reference Guide
<a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html">[1]</a>
</p>
</div>
</td>

<td>
<a href="https://www.infn.it/">
<span>
<img width="170" src="images/weblogo1.gif" border="0" 
     title="The Italian National Institute for Nuclear Physics (INFN), division of Catania, Italy">
</span>
</a>
</td>
</tr>

<tr>
<td>
<div id="content">
<div id="info" class="block">
<ul id="ticker">

<li>
<span><img style="width:40px !important;" src="images/springer.png"/>
Science Gateways for Virtual Research Communities (VRCs)</span>
<a href="https://link.springer.com/article/10.1007%2Fs10723-012-9242-3">
The "lightweight" crypto library interface is currently supported<br/>
by several thematic and general-purpose Science Gateways to access the<br/>
distributed computing and storage resources
</a>
</li>

<li><span><img style="width:40px !important;" src="images/label_blue_new.png"/>
Accounting of robot certificate</span>
<p align="justify">
Starting with servlet v2.0.2 it is now possible to account<br/>
the users of robot certificates<br/> (only with RFC 3820 proxies)
</p>
</li>

<li><span><img style="width:40px !important;" src="images/EGI_Logo_RGB_315x250px.png"/>
EGI-Engage project</span>
The eToken servlet will be officially used by the EGI-Engage project (EINFRA-1)<br/>
The mission is to accelerate the implementation of the Open Science Commons<br/>
within the European Grid Infrastructure (EGI)<br/>
</li>

</ul>
</div>
</div>
</td>
<td>
<a href="https://www.garr.it/">
<img width="250" src="images/GARR_logo.png" 
     border="0" title="Consortium GARR - The Italian Academic & Research Network"></a>
</td>
</tr>
</table>

<div id="steps" 
     style="width:70%; font-family: Tahoma,Verdana,sans-serif,Arial; font-size: 14px;">
<h2><a href="#">
<span>
<img width="32" 
     align="absmiddle" 
     src="images/png/glass_numbers_1.png"> Choose the certificate</a>
</span>
</a>
</h2>
<div>
<select class="ui-widget ui-widget-content" style="width:100%;" id="eToken" name="eToken"></select>
<textarea class="ui-widget ui-widget-content" id="details" name="details" 
          style="width: 100%; height: 250px; display:none" disabled="disabled">
</textarea>
</div>
      
<h2>
<a href="#">
<img width="32" 
     align="absmiddle" 
     src="images/png/glass_numbers_2.png"> Add AC attributes</a>
</h2>
<div>
<p><img style="width:20px !important;" src="images/help.png"/>
Select AC attributes to add to the generated proxy.</p>
<div name="error2" class="ui-widget" style="float:right;display:none;">
<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
<p>
<span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
<strong>Alert:</strong>
Please select at least one AC Attribute
</p>
</div>
</div>
<div id="attributes">
<ul id="selected-attributes">
</ul>
</div>
<p><small>Hold CTRL to select/deselect multiple entries</small></p>
</div>
  
<h2>
<a href="#">
<img width="32" 
     align="absmiddle"
     src="images/png/glass_numbers_3.png"> AC attributes order</a>
</h2>
<div>
<p><img style="width:20px !important;" src="images/help.png"/>
Choose the FQANs order that best fits your requirements</p>
<div name="error3" class="ui-widget"  style="float:right;display:none;">
<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
<strong>Hey!</strong>
Select some AC Attributes first!
</p>
</div>
</div>
<div>
<ul id="sorted-attributes">
</ul>
</div>
</div>
  
<h2>
<a href="#">
<img width="32" 
     align="absmiddle"
     src="images/png/glass_numbers_4.png"> Choose options</a>
</h2>
<div>
<p><img style="width:20px !important;" src="images/help.png"/>
Use the options below as you need</p>
<label for="long-proxy">Enable Proxy Renewal:</label>
<input type="checkbox" name="long-proxy" id="long-proxy" checked="checked"/>
<label for="disable-voms-proxy">Disable VOMS Proxy:</label>
<input type="checkbox" name="disable-voms-proxy" id="disable-voms-proxy"/>
<label for="rfc-proxy">Create RFC Proxy:</label>
<input type="checkbox" name="rfc-proxy" id="rfc-proxy"/><br/><br/>
<!--label for="dirac-token">Create Dirac Token:</label>
<input type="checkbox" name="dirac-token" id="dirac-token" onchange="enableDirac(this.form);"/><br/><br/-->
<img style="width:20px !important;" src="images/help.png"/>
Add some additional info to account users of robot proxy certificates<br/><br/>
<label for="enable-cn-enable">Enable CN:</label>
<input type="checkbox" name="enable-cn-label" id="enable-cn-label" onchange="enableCN(this.form);"/>
<input type="text" name="cn-label" disabled value="Empty"/>
</div>
    
<h3>
<a href="#">
<img  width="32"  
      align="absmiddle"
      src="images/png/glass_numbers_5.png"> Get your request</a>
</h3>
<div>

<div id="loading"></div>
<p><img style="width:20px !important;" src="images/help.png"/> Here is your request</p>

<div id="request" class="ui-widget ui-state-highlight ui-corner-all" style="width: 100%;"></div>
<br/>
<textarea class="ui-widget ui-widget-content" id="proxy" name="proxy" 
          style="width: 100%; height: 250px; display:none; font-family: 'Courier New', Courier, monospace;"  
          disabled="disabled">
</textarea>

<p><img style="width:20px !important;" src="images/help.png"/> Click here to get your proxy
<input type="image" 
       align="absmiddle"
       style="width:20px !important; heigth:20px !important"
       src="images/create.png" 
       onclick="calling_restAPI();"/>
</p>
</div>    
    
<h3>
<a href="#">
<img  width="32"  
      align="absmiddle"
      src="images/png/glass_numbers_6.png"> About the servlet</a>
</h3>

<table border=0>
<tr>
<td width="30%" align="justify" valign="top">
    The "light-weight" crypto library interface has been designed to provide
   seamless and secure access to computing e-Infrastructures, based on gLite middleware,
   and other middleware supporting X.509 standard for authorization, using robot certificate.<br/><br/>
   By design, the servlet is compliant with the policies reported in these docs <a href="https://www.eugridpma.org/guidelines/pkp/">[1]</a>
   <a href="https://wiki.eugridpma.org/Main/CredStoreOperationsGuideline">[2]</a>/<br/>
   The business logic of the library, deployed on top of an Apache Tomcat Application Server,
   combines different programming native interfaces and standards (see figure).
</td>
<td width="70%" align="center" valign="top">
   <div align="center">
   <img class="architecture" src="images/architecture-2.png" border="0">
   </div>
</td>
</tr>

<tr>
<td colspan="5">
<br/><img style="width:20px !important;" src="images/help.png"/>
&nbsp; Here follows a list of RESTFul APIs to interact with the eTokenServer and get valid robot proxies:<br/>
<ul type="circle">
<li style="color:#0E06E1"><u>Create RFC 3820 complaint proxies (with additional info to account real users)</u></li>
<p>https://eTokenServer:8443/eTokenServer/eToken/<md5sum>bc779e33367eaad7882b9dfaa83a432c</md5sum>?voms=gridit:/gridit&proxy-renewal=true&disable-voms-proxy=false&<mark>rfc-proxy=true&cn-label=eToken:LAROCCA</mark></p>

<li style="color:#0E06E1"><u>Create full-legacy Globus proxies (old fashioned proxy)</u></li>
<p>https://eTokenServer:8443/eTokenServer/eToken/<md5sum>bc779e33367eaad7882b9dfaa83a432c</md5sum>?voms=gridit:/gridit&proxy-renewal=true&disable-voms-proxy=false&<mark>rfc-proxy=false</mark>&cn-label=eToken:Empty</p>

<li style="color:#0E06E1"><u>Create full-legacy Globus proxies (with more VOMS ACLs)</u></li>
<p>https://eTokenServer:8443/eTokenServer/eToken/<md5sum>b970fe11cf219e9c6644da0bc4845010</md5sum>?<mark>voms=vo.eu-decide.eu:/vo.eu-decide.eu/GridSPM/Role=Scientist+vo.eu-decide.eu:/vo.eu-decide.eu/Role=Neurologist</mark>&proxy-renewal=true&disable-voms-proxy=false&rfc-proxy=false&cn-label=eToken:Empty</p>

<li style="color:#0E06E1"><u>Create plain proxies (without VOMS ACLs)</u></li>
<p>https://eTokenServer:8443/eTokenServer/eToken/<md5sum>bc779e33367eaad7882b9dfaa83a432c</md5sum>?voms=gridit:/gridit&proxy-renewal=false&<mark>disable-voms-proxy=true</mark>&rfc-proxy=false&cn-label=eToken:Empty</p>
</ul>

<img style="width:20px !important;" src="images/help.png"/>
&nbsp; Here follows a list of RESTFul APIs to get some useful info:<br/>
<ul type="circle">
<li style="color:#0E06E1"><u>Get a list of available robot certificates (in JSON format)</u></li>
<p>https://eTokenServer:8443/eTokenServer/<mark>eToken?format=json</mark></p>    
    
<li style="color:#0E06E1"><u>Get the MyProxyServer settings used by the eTokenServer (in JSON format)</u></li>
<p>https://eTokenServer:8443/MyProxyServer/<mark>proxy?format=json</mark></p>
</ul>

<img style="width:20px !important;" src="images/help.png"/>
&nbsp; Here follows a list of Advanced RESTFul APIs to interact with the MyProxyServer:<br/>
<ul type="circle">
<li style="color:#0E06E1"><u>Register long-term proxy on the MyProxy server (only for expert user)</u></li>
<p>https://eTokenServer:8443/MyProxyServer/<mark>proxy/x509up_6380887419908824.long</mark></p>
</ul>

<img style="width:20px !important;" src="images/help.png"/>
&nbsp; Learn how to Install and Configure the INFN eToken servlet
<a href="https://github.com/csgf/eToken">
<img width="35" src="images/github-collab-retina-preview.png"/></a>

<p align="right">
<a href="mailto:sg-licence@ct.infn.it?Subject=[eToken] - Contact!">
<img style="width:170px !important;" src="images/contactus.png"/></a>
</p>
</td>
</tr>
</table>   
</div>

<div id='footer' style="font-family: Tahoma,Verdana,sans-serif,Arial; font-size: 14px;">
<div>The Italian National Institute for Nuclear Physics (INFN), division of Catania, Italy</div>
<div>eToken servlet (v2.0.5)</div>
<div>Copyright &copy; 2010 - 2015. All rights reserved</div>  
<div>This work has been partially supported by
<a href="https://www.chain-project.eu/">
<img width="60" 
     border="0"
     src="images/chain-logo-220x124.png" 
     title="The CHAIN-REDS EU FP7 Project"/>
</a>
</div>  
</div> 
<br/><br/>
</body>
</html>
