*********************
eToken v2.0.4 Docs
*********************

============
About
============

.. _1: http://docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html
.. _2: http://wiki.eugridpma.org/Main/CredStoreOperationsGuideline

A standard-based solution developed by the INFN Catania for central management of robot credentials and provisioning of digital proxies to get seamless and secure access to computing e-Infrastructures supporting the X.509 standard for Authorisation.

This is a servlet based on the Java™ Cryptographic Token Interface Standard (PKCS#11). For any further information, please visit the official Java™ PKCS#11 Reference Guide [1_]. By design, the servlet is compliant with the policies reported in these docs [1_][2_].

The business logic of the library, deployed on top of an Apache Tomcat Application Server, combines different programming native interfaces and standards.

The high-level architecture of the eToken servlet is shown in the below figure:

.. image:: images/architecture.jpg
   :align: center

============
Installation
============

- Configure the EGI Trust Anchor repository

.. code:: bash

  ]# cd /etc/yum.repos.d/
  ]# cat egi-trustanchors.repo
  [EGI-trustanchors]
  name=EGI-trustanchors
  baseurl=http://repository.egi.eu/sw/production/cas/1/current/
  gpgkey=http://repository.egi.eu/sw/production/cas/1/GPG-KEY-EUGridPMA-RPM-3
  gpgcheck=1
  enabled=1

- Install the latest EUGridPMA CA rpms

.. code:: bash

  ]# yum install -y ca-policy-egi-core

- Configure the epel repository

.. code:: bash

  ]# cd /etc/yum.repos.d/
  ]# cat /etc/yum.repos.d/epel.repo
  [epel]
  name=Extra Packages for Enterprise Linux 6 - $basearch
  #baseurl=http://download.fedoraproject.org/pub/epel/6/$basearch
  mirrorlist=https://mirrors.fedoraproject.org/metalink?repo=epel-6&arch=$basearch
  failovermethod=priority
  enabled=1
  gpgcheck=1
  gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL-6

  [epel-debuginfo]
  name=Extra Packages for Enterprise Linux 6 - $basearch - Debug
  #baseurl=http://download.fedoraproject.org/pub/epel/6/$basearch/debug
  mirrorlist=https://mirrors.fedoraproject.org/metalink?repo=epel-debug-6&arch=$basearch
  failovermethod=priority
  enabled=0
  gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL-6
  gpgcheck=1

  [epel-source]
  name=Extra Packages for Enterprise Linux 6 - $basearch - Source
  #baseurl=http://download.fedoraproject.org/pub/epel/6/SRPMS
  mirrorlist=https://mirrors.fedoraproject.org/metalink?repo=epel-source-6&arch=$basearch
  failovermethod=priority
  enabled=0
  gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL-6
  gpgcheck=1

- Install and Configure NTP / fetch-crl

.. code:: bash

   ]# yum install -y ntpdate
   ]# ntpdate -u ntp-1.infn.it
   ]# /etc/init.d/ntpd start
   ]# chkconfig --level 2345 ntpd on

- Install the fetch-crl package.

Version 3 does not work properly, so get version 2.8.5 instead

.. code:: bash

   ]# wget ftp://ftp.univie.ac.at/systems/linux/fedora/epel/5/i386/fetch-crl-2.8.5-1.el5.noarch.rpm
   ]# rpm -ivh fetch-crl-2.8.5-1.el5.noarch.rpm

   ]# /etc/init.d/fetch-crl-cron start
   Enabling periodic fetch-crl:                               [  OK  ]

   ]# /etc/init.d/fetch-crl-boot status
   fetch-crl-boot lockfile present                            [  OK  ]

  

- Import the Java applications into your preferred IDE (e.g. Netbeans).

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
   Copied 54 empty directories to 1 empty directory under 
        /home/larocca/eTokenServerREST-OK/eTokenServer/build/web
        
   library-inclusion-in-archive:
   Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
   Copying 1 file to /home/larocca/eTokenServerREST-OK/eTokenServer/build/web/WEB-INF/lib
   [..]
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
   [..]
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
   # Configure a default e-mail to notify the eToken administrator 
   # when a robot certificate is going to expire
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

- Check log files

.. code:: bash

   ]# tail -f <apache-tomcat>logs/eToken.out
   ]# tail -f <apache-tomcat>logs/MyProxy.out
   ]# tail -f <apache-tomcat>logs/catalina.out
   ]# tail -f <apache-tomcat>logs/localhost.<date>.log

============
Usage
============

Here follows a list of RESTFul APIs to interact with the eTokenServer and get valid robot proxies.

- CREATE RFC 3820 complaint proxies (with additional info to account real users)

.. code:: bash

   https://<eTokenServer>:8443/eTokenServer/eToken/bc779e33367eaad7882b9dfaa83a432c?\
           voms=gridit:/gridit&\
           proxy-renewal=true&\
           disable-voms-proxy=false&\
           rfc-proxy=true&\
           cn-label=eToken:LAROCCA

- CREATE full-legacy Globus proxies (old fashioned proxy)

.. code:: bash

   https://<eTokenServer>:8443/eTokenServer/eToken/bc779e33367eaad7882b9dfaa83a432c?\
           voms=gridit:/gridit&\
           proxy-renewal=true&\
           disable-voms-proxy=false&\
           rfc-proxy=false&\
           cn-label=eToken:Empty

- CREATE full-legacy Globus proxies (with more VOMS ACLs)

.. code:: bash

   https://<eTokenServer>:8443/eTokenServer/eToken/b970fe11cf219e9c6644da0bc4845010?\
           voms=vo.eu-decide.eu:/vo.eu-decide.eu/GridSPM/Role=Scientist+vo.eu-decide.eu:/vo.eu-decide.eu/Role=Neurologist&\
           proxy-renewal=true&\
           disable-voms-proxy=false&\
           rfc-proxy=false&\
           cn-label=eToken:Empty

- CREATE plain proxies (without VOMS ACLs)

.. code:: bash

   https://<eTokenServer>:8443/eTokenServer/eToken/bc779e33367eaad7882b9dfaa83a432c?\
           voms=gridit:/gridit&\
           proxy-renewal=false&\
           disable-voms-proxy=true&\
           rfc-proxy=false&\
           cn-label=eToken:Empty

- GET a list of available robot certificates (in JSON format)

.. code:: bash

   https://<eTokenServer>:8443/eTokenServer/eToken?format=json

- GET the MyProxy settings used by the eTokenServer (in JSON format)

.. code:: bash

   https://<eTokenServer>:8443/MyProxyServer/proxy?format=json

- REGISTER long-term proxy on the MyProxy server (only for expert user)

.. code:: bash

   https://<eTokenServer>:8443/MyProxyServer/proxy/x509up_6380887419908824.long
   
============
Support
============
Please feel free to contact us any time if you have any questions or comments.

.. _INFN: http://www.ct.infn.it/

:Authors:

 `Roberto BARBERA <mailto:roberto.barbera@ct.infn.it>`_ - Italian National Institute of Nuclear Physics (INFN_),
 
 `Giuseppe LA ROCCA <mailto:giuseppe.larocca@ct.infn.it>`_ - Italian National Institute of Nuclear Physics (INFN_),
 
 `Salvatore MONFORTE <mailto:salvatore.monforte@ct.infn.it>`_ - Italian National Institute of Nuclear Physics (INFN_)
 
 
:Version: v2.0.4, 2015

:Date: June 4th, 2015 12:50
