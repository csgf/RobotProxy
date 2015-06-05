****************************************************************************
How-to install and configure the eToken & the MyProxy servlets (v2.0.4) Docs
****************************************************************************

===================
About this document
===================

.. _1: http://www.safenet-inc.it/etoken-pro.html
.. _2: http://www.catania-science-gateways.it/
.. _3: http://www.safenet-inc.it/

.. |warning| image:: images/warning.jpg
.. |download| image:: images/download.jpg

This is the official documentation to configure and install the eTokenServer servlet (v2.0.4).

This document provides an in-depth overview of the light-weight crypto library, a standard-based solution developed by INFN Catania for central management of robot credentials and provisioning of digital proxies to get seamless and secure access to computing e-Infrastructures supporting the X.509 standard for Authorisation.

In this solution robot certificates are available 24h per day on board of USB eToken PRO [1_] 32/64 KBytes smart cards having the following technical specification:

.. image:: images/eToken_specs.jpg
   :align: center

We appreciate attribution. In case you would like to cite the Java light-weight crypto library in your papers, we recomment that you use the following reference:

        V. Ardizzone, R. Barbera, A. Calanducci, M. Fargetta, E. Ingra', I. Porro, 
        G. La Rocca, S. Monforte, R. Ricceri, R. Rotondo, D. Scardaci and A. Schenone
        ***The DECIDE Science Gateway***
        *Journal of Grid Computing (2012) 10:689-70 DOI 10.1007/s10723-012-9242-3*

We also would like to be notified about your publications that involve the use of the Java light-weight crypto libraries, as this will help us to document its usefulness. We like to feature links to these articles, with your permission, on our Web site.
Additional reference to the Java light-weight crypto library and other relevant activities can be fould at [2_].

============
Licence
============
Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.

============
Conventions used in this document
============
The following typographical conventions are used in this document:

*Italic*
        Indicates new terms, URLs, filenames, and file extentions

**Constant width italic**
        Shows text that should be replaced with user-specific values

|warning| This icon indicates a warning or caution.

|download| This icon indicates that there are files to be downloaded.

============
Chapter I - Overview of the light-weight crypto library
============
The "light-weight" crypto library interface has been designed to:

- provide seamless and secure access to computing e-Infrastructure based on middleware supporting X.509 standard for authorization, using robot certificates,

- make user's interaction with security infrastructures easy and transparent.

The high-level architecture of the library interface is shown in the below figure:

.. image:: images/architecture.jpg
      :align: center

The business logic has been conceived to provide "resources" (e.g. complaint VOMS proxies) in a "web-manner" which can be consumed by authorized users, client applications and by portals and Science Gateways. In the current implementation, robot certificates have been safely installed on board of SafeNet [3_] eToken PRO [1_] 32/64 KBytes USB smart cards directly plugged to a remote server which serve, so far, six different Science Gateways.

.. _4: http://tomcat.apache.org/
.. _5: https://jax-rs-spec.java.net/
.. _6: http://www.oracle.com/technetwork/articles/javaee/index-jsp-136246.html
.. _7: http://docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html
.. _8: https://www.bouncycastle.org/
.. _9: https://github.com/jglobus/JGlobus
.. _10: https://github.com/italiangrid/voms-clients
.. _11: https://github.com/italiangrid/voms-admin-server/tree/master/voms-admin-api

The complete list of software, tools and APIs we have used to implement the new crypto library interface are listed below:

- Apache Application Server [4_],

- JAX-RS, the Java API for RESTful Web Services (JSR 311 standard) [5_], 

- Java Technology Standard Edition (Java SE6) [6_],

- The Cryptographic Token Interface Standard (PKCS#11) libraries [7_],

- The open-source BouncyCastle Java APIs [8_],

- The JGlobus-Core Java APIs [9_],

- The VOMS-clients Java APIs [10_],

- The VOMS-Admin Java APIs [11_].

============
Chapter II - System and Software Requirements
============
This chapter provide the list of requirements and the basic information that you need to know to install and configure the servlet.

+---+-----------------------+-------------------------+--------------+------------+--------------+
| # |        Server         |   OS and Architecture   |  Host. Cert  | Disk Space | CPU and RAM  |
+===+=======================+=========================+==============+============+==============+
| 1 | Physical machine with | SL release 5.10 (Boron) |     Yes      |  >= 80 GB  |  >= 4 cores  |
|   | at least 2 USB ports  | x86_64 GNU/Linux        |              |            |  >= 8 GB RAM |
|   | perfectly working     |                         |              |            |  Swap >=4 GB |
+---+-----------------------+------------+------------+--------------+------------+--------------+
| Comments:                                                                                      |
|                                                                                                |
| - The server must be registered to the DNS with direct adn reverse resolution;                 |
|                                                                                                |
| - Please set a **human readable** server hostname for your server (e.g. etoken<your-domain>);  |
|                                                                                                |
| - The OS installation should include the X-server since it is needed to open etProps app;      |
|                                                                                                |
| - This installation has been successfully tested with eToken PRO 32/64 KBytes USB smart cards; |
|                                                                                                |
| - At least 1 USB eToken PRO 75 KBytes must be available before the installation                |
|   (contact SafeNet Inc. [3_] to find a neighbor reseller and get prices).                      |
+------------------------------------------------------------------------------------------------+

===================
OS and repos
===================
Start with a fresh installation of Scientific Linux 5.x (x86_64).

.. code:: bash

  ]# cd /etc/redhat-release
  Scientific Linux release 5.10 (Boron)

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

  ]# yum clean all
  ]# yum install -y ca-policy-egi-core

- Configure the EPEL repository:

.. code:: bash

  ]# cd /etc/yum.repos.d/
  ]# cat /etc/yum.repos.d/epel.repo 
  [epel]
  name=Extra Packages for Enterprise Linux 5 - $basearch
  #baseurl=http://download.fedoraproject.org/pub/epel/5/$basearch
  mirrorlist=http://mirrors.fedoraproject.org/mirrorlist?repo=epel-5&arch=$basearch
  failovermethod=priority
  enabled=1
  gpgcheck=1
  gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL

  [epel-debuginfo]
  name=Extra Packages for Enterprise Linux 5 - $basearch - Debug
  #baseurl=http://download.fedoraproject.org/pub/epel/5/$basearch/debug
  mirrorlist=http://mirrors.fedoraproject.org/mirrorlist?repo=epel-debug-5&arch=$basearch
  failovermethod=priority
  enabled=0
  gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL
  gpgcheck=1

  [epel-source]
  name=Extra Packages for Enterprise Linux 5 - $basearch - Source
  #baseurl=http://download.fedoraproject.org/pub/epel/5/SRPMS
  mirrorlist=http://mirrors.fedoraproject.org/mirrorlist?repo=epel-source-5&arch=$basearch
  failovermethod=priority
  enabled=0
  gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL
  gpgcheck=1

- Install the latest epel release

.. code:: bash

  ]# yum install -y epel-release-5.4.noarch

===================
SELinux configuration
===================

.. _12: fedoraproject.org/wiki/SELinux/setenforce

Be sure that SELinux is disabled (or permissive). Details on how to disable SELinux are here [12_]

.. code:: bash

   ]# getenforce
   Disabled

===================
sendmail
===================

Start the sendmail service at boot. 
Configure access rules to allow connections and open the firewall on port 25.

.. code:: bash

   ]# /etc/init.d/sendmail start
   ]# chkconfig --level 2345 sendmail on

   ]# cat /etc/hosts.allow
   sendmail: localhost

   ]# cat /etc/sysconfig/iptables
   [..]
   -A RH-Firewall-1-INPUT -p tcp -m tcp --dport 25 -s 127.0.0.1 -j ACCEPT

===================
NTP
===================
Use NTP to synchronize the time of the server 

.. code:: bash

   ]# ntpdate ntp-1.infn.it
   ]# /etc/init.d/ntpd start
   ]# chkconfig --level 2345 ntpd on

===================
Host Certificates
===================

.. _12: http://www.eugridpma.org/members/worldmap/
.. _13: https://comodosslstore.com/

Navigate the interactive map and search for your closest Certification Authorities [12_] or, alternatively, buy a multi-domain COMODO [13_] SSL certificate.

Public and Private keys of the host certificate have to be copied in /etc/grid-security/

.. code:: bash

   ]# ll /etc/grid-security/host*
   -rw-r--r--  1 root root 1627 Mar 10 14:55 /etc/grid-security/hostcert.pem
   -rw-------  1 root root 1680 Mar 10 14:55 /etc/grid-security/hostkey.pem

===================
Configuring VOMS Trust Anchors
===================
The VOMS-clients APIs need local configuration to validate the signature on Attribute Certificates issued by trusted VOMS servers.

The VOMS clients and APIs look for trust information in the */etc/grid-security/vomsdir* directory.

The *vomsdir* directory contains a directory for each trusted VO. Inside each VO two types of files can be found:

- An *LSC*  file contains a description of the certificate chain of the certificate used by a VOMS server to sign VOMS attributes.

- An *X509* certificates used by the VOMS server to sign attributes.

These files are commonly named using the following pattern:

.. code:: bash

   <hostname.lsc>
   <hostname.pem>

where *hostname* is the host where the VOMS server is running.

When both *.lsc* and *.pem* files are present for a given VO, the *.lsc* file takes precedence. 
The *.lsc* file contains a list of X.509 subject strings, one on each line, encoded in OpenSSL slash-separate syntax, describing the certificate chain (up and including the CA that issued the certificate). For instance, the *voms.cnaf.infn.it* VOMS server has the following *.lsc* file:

.. code:: bash
  
  /C=IT/O=INFN/OU=Host/L=CNAF/CN=voms.cnaf.infn.it
  /C=IT/O=INFN/CN=INFN CA

.. |warning| image:: images/warning.jpg
.. |download| image:: images/download.jpg

.. _vomsdir: others/

|warning| Install in */etc/grid-security/vomsdir/* directory the *.lsc* for each trusted VO that you want to support.

|download| An example of */etc/grid-security/vomsdir/* directory can be downloaded from here [14_].

===================
Chapter III - Installation & Configuration
===================
This chapter introduces the manual installation of the SafeNet eToken PKI client library on a Linux system, the software that enables eToken USB operations and the implementation of eToken PKI-based solutions. The software also includes all the necessary files and drivers to support the eToken management. 
During the installation, the needed libraries and drivers will be installed in */usr/local/bin*, */usr/local/lib* and */usr/local/etc*.

|warning| Before to start, please check if pcsc- packages are already installed on your server. 

.. code:: bash

   ]# rpm -e pcsc-lite-1.4.4-4.el5_5 \
             pcsc-lite-libs-1.4.4-4.el5_5 \
             pcsc-lite-doc-1.4.4-4.el5_5 \
             pcsc-lite-devel-1.4.4-4.el5_5 \ 
             ccid-1.3.8-2.el5.i386 \
             ifd-egate-0.05-17.el5.i386 \
             coolkey-1.1.0-16.1.el5.i386 \
             esc-1.1.0-14.el5_9.1.i386

|download| Download the correct software packages:

.. _15: http://dag.wieers.com/rpm/packages/pcsc-lite/pcsc-lite-1.3.3-1.el4.rf.i386.rpm
.. _16: http://dag.wieers.com/rpm/packages/pcsc-lite/pcsc-lite-libs-1.3.3-1.el4.rf.i386.rpm
.. _17: http://dag.wieers.com/rpm/packages/pcsc-lite-ccid/pcsc-lite-ccid-1.2.0-1.el4.rf.i386.rpm

- pcsc-lite-1.3.3-1.el4.rf.i386.rpm [15_] 

- pcsc-lite-libs-1.3.3-1.el4.rf.i386.rpm [16_]

- pcsc-lite-ccid-1.2.0-1.el4.rf.i386.rpm [17_]

.. code:: bash

   ]# rpm -ivh pcsc-lite-1.3.3-1.el4.rf.i386.rpm \
               pcsc-lite-ccid-1.2.0-1.el4.rf.i386.rpm \ 
               pcsc-lite-libs-1.3.3-1.el4.rf.i386.rpm

Preparing...            ########################################### [100%]
1:pcsc-lite-libs        ########################################### [ 33%] 
2:pcsc-lite-ccid        ########################################### [ 67%] 
3:pcsc-lite             ########################################### [100%]

Before installing the eToken PKI Client, please check if the PC/SC-Lite pcscd daemon is running:

.. code:: bash

   ]# /etc/init.d/pcscd start

In /var/log/messages you should have the message:

.. code:: bash

   [..]
   Feb 2 09:02:15 giular pcscd: pcscdaemon.c:532:at_exit() cleaning /var/run
   Feb 2 09:02:44 giular pcscd: pcscdaemon.c:533:main() pcsc-lite 1.3.3 daemon ready.
   Feb 2 09:02:44 giular pcscd: hotplug_libusb.c:394:HPEstablishUSBNotifications() Driver ifd-ccid.bundle does not support IFD_GENERATE_HOTPLUG

|warning| Contact the SafeNet Inc. and install the latest eToken PKI Client (ver. 4.55-34) software on your system.

.. code:: bash

   ]$ rpm -ivh pkiclient-full-4.55-34.i386.rpm

   Preparing...             ########################################### [100%] 
   Stopping PC/SC smart card daemon (pcscd): [ OK ]
           1:pkiclient-full ########################################### [100%] 
   Checking installation of pcsc from source... None.
   Starting PC/SC smart card daemon (pcscd): [ OK ] 
   Adding eToken security provider...Done.
   PKIClient installation completed. 

|download| Download the appropriate libraries [18_] for your system and save it as *Mkproxy-rhel4.tar.gz*. The archive contains all the requires libraries for RHEL4 and RHEL5.

.. code:: bash

   ]# tar zxf Mkproxy-rhel4.tar.gz
   ]# chown -R root.root etoken-pro/ 
   ]# tree etoken-pro/
   etoken-pro/ 
   |-- bin
   | |-- cardos-info 
   | |-- mkproxy
   | |-- openssl
   | `-- pkcs11-tool 
   |-- etc
   | |-- hotplug.d 
   | | `-- usb
   | |  `-- etoken.hotplug 
   | |-- init.d
   | | |-- etokend 
   | | `-- etsrvd 
   | |-- openssl.cnf
   | |-- reader.conf.d
   | | `-- etoken.conf 
   | `-- udev
   |    `-- rules.d
   |    `-- 20-etoken.rules 
   `-- lib
        |-- engine_pkcs11.so
        |-- libcrypto.so.0.9.8
        `-- libssl.so.0.9.8

Untar the archive and copy the files to their respective locations.

- Copy binary files

.. code:: bash

   ]# cp -rp etoken-pro/bin/cardos-info /usr/local/bin/
   ]# cp -rp etoken-pro/bin/mkproxy /usr/local/bin/
   ]# cp -rp etoken-pro/bin/pkcs11-tool /usr/local/bin/ ]$ cp -rp etoken-pro/bin/openssl /usr/local/bin/
 
- Copy libraries

.. code:: bash
   
   ]# cp -rp etoken-pro/lib/engine_pkcs11.so /usr/local/lib
   ]# cp -rp etoken-pro/lib/libssl.so.0.9.8 /usr/local/lib
   ]# cp -rp etoken-pro/lib/libcrypto.so.0.9.8 /usr/local/lib

- Copy configuration files

.. code:: bash

   ]# cp -rp etoken-pro/etc/openssl.cnf /usr/local/etc

- Set the PKCS11_MOD environment variable

Edit the */usr/local/bin/mkproxy* script and change the PKCS11_MOD variable settings:

.. code:: bash

   export PKCS11_MOD="/usr/lib/libeTPkcs11.so"

- Create symbolic links

.. code:: bash

   ]# cd /usr/lib/
   ]# ln -s /usr/lib/libpcsclite.so.1.0.0 libpcsclite.so 
   ]# ln -s /usr/lib/libpcsclite.so.1.0.0 libpcsclite.so.

   ]# ll libpcsclite.so*
      lrwxrwxrwx 1 root root 29 Feb 17 09:47 libpcsclite.so -> /usr/lib/libpcsclite.so.1.0.0 
      lrwxrwxrwx 1 root root 29 Feb 17 09:52 libpcsclite.so.0 -> /usr/lib/libpcsclite.so.1.0.0 
      lrwxrwxrwx 1 root root 20 Feb 17 09:04 libpcsclite.so.1 -> libpcsclite.so.1.0.0
      -rwxr-xr-x 1 root root 92047 Jan 26 2007 libpcsclite.so.1.0.0

To administer the USB eToken PRO 64KB and add a new robot certificate, please refer to the Appendix I.

- Testing

.. code:: bash

   ]# export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib
   ]# pkcs11-tool -L --module=/usr/lib/libeTPkcs11.so
        
   Available slots:
   **Slot 0** AKS ifdh 00 00
        token label: **eToken**
        token manuf: Aladdin Ltd. 
        token model: eToken
        token flags: rng, login required, PIN initialized, token initialized, other flags=0x200
        serial num : 001c3401
   **Slot 1** AKS ifdh 01 00
        token label: **eToken1** 
        token manuf: Aladdin Ltd. token model: eToken
        token flags: rng, login required, PIN initialized, token initialized, other flags=0x200
        serial num : 001c0c05 
   [..]

The current version of PKI_Client supports up to **16** different slot! Each slot can host a USB eToken PRO smart card 



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

:Date: June 4th, 2015 17:50
