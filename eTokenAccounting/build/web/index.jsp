<%-- 
/**************************************************************************
    Copyright (c) 2011:
    Istituto Nazionale di Fisica Nucleare (INFN), Italy
    Consorzio COMETA (COMETA), Italy

    See https://www.infn.it and and https://www.consorzio-cometa.it for details
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
    <style type="text/css">
      body, html { font-family: Tahoma,Verdana,sans-serif,Arial; font-size: 14px; }
      #selected-attributes .ui-selecting { background: #FECA40; }
      #selected-attributes .ui-selected { background: #F39814; color: white; }
      #selected-attributes, #sorted-attributes { list-style-type: none; margin: 0; padding: 0; width: 50%; }
      #selected-attributes li, #sorted-attributes li { margin: 3px; padding: 4px; font-size: 12px; height: 16px; }
      #steps img { width:32px; border:0;}
      img.architecture { width:600px !important; height: 450px !important;}
    </style>
    <link  HREF="icon.ico">  
    <link href="ui/css/ticker.css" rel="stylesheet" type="text/css"/>
    <link href="ui/css/overcast/jquery-ui-1.8.13.custom.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="ui/js/jquery-1.5.1.min.js"></script>
    <script type="text/javascript" src="ui/js/jquery-ui-1.8.13.custom.min.js"></script>
    <script type="text/javascript" src="ui/js/jQuery.rollChildren.js"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <title>eTokenAccounting Servlet</title>
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
<h1>Create your own statistics!
<a href="https://www.garr.it/">
<img width="250" src="images/GARR_logo.png" 
     border="0" title="Consortium GARR - The Italian Academic & Research Network"></a>

<a href="https://www.infn.it/">
<span>
<img width="150" src="images/weblogo1.gif" border="0" 
     title="The Italian National Institute for Nuclear Physics (INFN), division of Catania, Italy">
</span>
</a>
</h1>


<div id='footer' style="font-family: Tahoma,Verdana,sans-serif,Arial; font-size: 14px;">  
<div>The Italian National Institute for Nuclear Physics (INFN), division of Catania, Italy</div>
<div>eTokenAccounting servlet v0.0.1</div>
<div>Copyright © 2010 - 2015. All rights reserved</div>  
<div>This work has been partially supported by
<a href="https://www.egi.eu/projects/egi-inspire/">
<img width="35" 
     border="0"
     src="images/EGI_Logo_RGB_315x250px.png" 
     title="The European Grid Infrastructure"/>
</a>
</div>  
</div> 

</body>
</html>
