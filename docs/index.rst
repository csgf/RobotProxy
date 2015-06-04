*********************
eToken v2.0.4 Docs
*********************

============
About
============

.. _1: http://docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html
.. _2: http://wiki.eugridpma.org/Main/CredStoreOperationsGuideline
.. _CHAIN_REDS: https://www.chain-project.eu/

A standard-based solution developed by the INFN Catania for central management of robot credentials and provisioning of digital proxies to get seamless and secure access to computing e-Infrastructures supporting the X.509 standard for Authorisation.

This is a servlet based on the Java™ Cryptographic Token Interface Standard (PKCS#11). For any further information, please visit the official Java™ PKCS#11 Reference Guide [1_]. By design, the servlet is compliant with the policies reported in these docs [1_][2_].

The business logic of the library, deployed on top of an Apache Tomcat Application Server, combines different programming native interfaces and standards.

The high-level architecture of the eToken servlet is shown in the below figure:

.. image:: images/architecture.jpg
   :align: left

============
Installation
============
- Import the Java applications in your preferred IDE (e.g. Netbeans).

- Add the needed libraries to your projects. 

- Compile the applications with your IDE. 

In case of successful compilation of the *eTokenServer* servlet, you should get the following output message:

.. code:: bash

   init:
   undeploy-clean:
   deps-clean:
   eTokenUtils.init:

   eTokenUtils.deps-clean:
        Updating property file: /home/larocca/eTokenServerREST-OK/eTokenUtils/build/built-clean.properties
        Deleting directory /home/larocca/eTokenServerREST-OK/eTokenUtils/build
        
   eTokenUtils.clean:
   do-clean:
        Deleting directory /home/larocca/eTokenServerREST-OK/eTokenServer/build
        Deleting directory /home/larocca/eTokenServerREST-OK/eTokenServer/dist

   check-clean:
   clean:
   init:
   deps-module-jar:
   eTokenUtils.init:
   eTokenUtils.deps-jar:
        Created dir: /home/larocca/eTokenServerREST-OK/eTokenUtils/build
        Updating property file: /home/larocca/eTokenServerREST-OK/eTokenUtils/build/built-jar.properties
        Created dir: /home/larocca/eTokenServerREST-OK/eTokenUtils/build/classes
        Created dir: /home/larocca/eTokenServerREST-OK/eTokenUtils/build/empty
        Created dir: /home/larocca/eTokenServerREST-OK/eTokenUtils/build/generated-sources/ap-source-output
        Compiling 3 source files to /home/larocca/eTokenServerREST-OK/eTokenUtils/build/classes
        warning: [options] bootstrap class path not set in conjunction with -source 1.6
        1 warning

   eTokenUtils.compile:
        Created dir: /home/larocca/eTokenServerREST-OK/eTokenUtils/dist
        Copy libraries to /home/larocca/eTokenServerREST-OK/eTokenUtils/dist/lib.
        Building jar: /home/larocca/eTokenServerREST-OK/eTokenUtils/dist/eTokenUtils.jar
        To run this application from the command line without Ant, try:
        java -jar "/home/larocca/eTokenServerREST-OK/eTokenUtils/dist/eTokenUtils.jar"

   eTokenUtils.jar:
        deps-ear-jar:
        deps-jar:
        check-rest-config-props:
        generate-rest-config:
        Created dir: /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/classes
        Created dir: /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/META-INF
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/META-INF
        Copying 490 files to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web
        Copied 54 empty directories to 1 empty directory under /home/larocca/eTokenServerREST-OK/eTokenServer/build/web
        
        library-inclusion-in-archive:
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
        
        library-inclusion-in-manifest:
        Created dir: /home/larocca/eTokenServerREST-OK/eTokenServer/build/empty
        Created dir: /home/larocca/eTokenServerREST-OK/eTokenServer/build/generated-sources/ap-source-output
        Compiling 4 source files to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/classes
        warning: [options] bootstrap class path not set in conjunction with -source 1.6
        1 warning
        Copying 2 files to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/classes
   
        compile:
        compile-jsps:
        Created dir: /home/larocca/eTokenServerREST-OK/eTokenServer/dist
        Building jar: /home/larocca/eTokenServerREST-OK/eTokenServer/dist/eTokenServer.war
        do-dist:
        dist:
        BUILD SUCCESSFUL (total time: 7 seconds)

In case of successful compilation of the *MyProxyServer* servlet, you should get the following output message:

.. code:: bash

   init:
   undeploy-clean:
   deps-clean:
   do-clean:
        Deleting directory /home/larocca/eTokenServerREST-OK/MyProxyServer/build
        Deleting directory /home/larocca/eTokenServerREST-OK/MyProxyServer/dist
   check-clean:
   clean:
   init:
        deps-module-jar:
        deps-ear-jar:
        deps-jar:
   check-rest-config-props:
   generate-rest-config:
        Created dir: /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/classes
        Created dir: /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/META-INF
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/META-INF
        Copying 478 files to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web
 
        library-inclusion-in-archive:
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        Copying 1 file to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/lib
        
        library-inclusion-in-manifest:
        Created dir: /home/larocca/eTokenServerREST-OK/MyProxyServer/build/empty
        Created dir: /home/larocca/eTokenServerREST-OK/MyProxyServer/build/generated-sources/ap-source-output
        Compiling 6 source files to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/classes
        warning: [options] bootstrap class path not set in conjunction with -source 1.6
        1 warning
        Copying 2 files to /home/larocca/eTokenServerREST-OK/MyProxyServer/build/web/WEB-INF/classes

        compile:
        compile-jsps:
        Created dir: /home/larocca/eTokenServerREST-OK/MyProxyServer/dist
        Building jar: /home/larocca/eTokenServerREST-OK/MyProxyServer/dist/MyProxyServer.war
        do-dist:
        dist:
        BUILD SUCCESSFUL (total time: 2 seconds)

- Customize the configuration files for the eTokenServer servlet according to your installation: 

.. code:: bash

   ]# cat eToken.properties
   # VOMS Settings
   # Standard location of configuration files 
   VOMSES_PATH=/etc/vomses
   VOMS_PATH=/etc/grid-security/vomsdir
   X509_CERT_DIR=/etc/grid-security/certificates
   # Default VOMS proxy lifetime (default 12h)
   VOMS_LIFETIME=24

   # Token Settings
   ETOKEN_SERVER=<Add here your eTokenServer IP>
   ETOKEN_PORT=8082
   ETOKEN_CONFIG_PATH=/root/eTokens-2.0.5/config
   PIN=<Add here your eToken PIN password>

   # Proxy Settings
   # Default proxy lifetime (default 12h)
   PROXY_LIFETIME=24
   # Number of bits in key {512|1024|2048|4096}
   PROXY_KEYBIT=1024

   # Administrative Settings
   SMTP_HOST=smtp.gmail.com
   SENDER_EMAIL=<Configure the sender e-mail for notification>
   # Configure a default e-mail to notify the eToken administrator when a robot certificate is going to expire
   DEFAULT_EMAIL=<Configure the default e-mail for notification>
   EXPIRATION=5

- Customize the configuration files for the MyProxyServer servlet according to your installation: 

.. code:: bash

   ]# cat MyProxy.properties 
   # MyProxy Settings
   MYPROXY_SERVER=<Add here your MyProxyServer host>
   MYPROXY_PORT=7512
   # Default MyProxy proxy lifetime (default 1 week)
   MYPROXY_LIFETIME=604800
   # Default proxy temp path
   MYPROXY_PATH=<Configure the default temp path> (e.g.: /root/apache-tomcat-7.0.53/temp)

- Deploy the servlets and restart the Application Server. 

.. code:: bash

   ]# cd apache-tomcat-7.0.53
   ]# rm -rf webapps/eTokenServer
   ]# cp /root/eTokenServer.war webapps/
   ]# cp /root/MyProxyServer.war webapps/

- Wait for a while to let the WAR files to be extracted

.. code:: bash

   # Check if the webapps contains the directories for the two servlets

   ]# drwxr-xr-x 7 root root     4096 May 13 14:59 eTokenServer
   ]# -rw-r--r-- 1 root root 13319302 Mar 25 15:26 eTokenServer.war
   ]# drwxr-xr-x 6 root root     4096 Mar 25 12:03 MyProxyServer
   ]# -rw-r--r-- 1 root root 12471693 Mar 25 12:03 MyProxyServer.war

- Restart the application server with the correct configuration files

.. code:: bash

   ]# ./bin/catalina.sh stop && sleep 5
   ]# cp -f eToken.properties webapps/eTokenServer/WEB-INF/classes/infn/eToken/
   ]# cp -f MyProxy.properties webapps/MyProxyServer/WEB-INF/classes/infn/MyProxy/
   ]# ./bin/catalina.sh start

   ]# tail -f logs/eToken.out

============
Usage
============

============
Support
============
Please feel free to contact us any time if you have any questions or comments.

.. _INFN: http://www.ct.infn.it/

:Authors:

 `Roberto BARBERA <mailto:roberto.barbera@ct.infn.it>`_ - Italian National Institute of Nuclear Physics (INFN_),
 
 `Giuseppe LA ROCCA <mailto:giuseppe.larocca@ct.infn.it>`_ - Italian National Institute of Nuclear Physics (INFN_),
 
 
:Version: v2.0.4, 2015

:Date: June 4th, 2015 12:50
