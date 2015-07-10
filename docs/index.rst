*********************
ETOKEN
*********************

============
About
============

.. _1: http://docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html
.. _2: http://wiki.eugridpma.org/Main/CredStoreOperationsGuideline
.. _3: http://www.safenet-inc.it/
.. _4: http://www.safenet-inc.it/etoken-pro.html

A standard-based solution developed by the INFN Catania for central management of robot credentials and provisioning of digital proxies to get seamless and secure access to computing e-Infrastructures supporting the X.509 standard for Authorisation.

This is a servlet based on the Java™ Cryptographic Token Interface Standard (PKCS#11). For any further information, please visit the official Java™ PKCS#11 Reference Guide [1_]. By design, the servlet is compliant with the policies reported in these docs [1_][2_].

The business logic of the library, deployed on top of an Apache Tomcat Application Server, combines different programming native interfaces and standards.

The high-level architecture of the eToken servlet is shown in the below figure:

.. image:: images/architecture.jpg
   :align: center

The business logic has been conceived to provide "resources" (e.g. complaint VOMS proxies) in a "web-manner" which can be consumed by authorized users, client applications and by portals and Science Gateways. In the current implementation, robot certificates have been safely installed on board of SafeNet [3_] eToken PRO [4_] 32/64 KBytes USB smart cards directly plugged to a remote server which serve, so far, six different Science Gateways.

.. _5: http://tomcat.apache.org/
.. _6: https://jax-rs-spec.java.net/
.. _7: http://www.oracle.com/technetwork/articles/javaee/index-jsp-136246.html
.. _8: http://docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html
.. _9: https://www.bouncycastle.org/
.. _10: https://github.com/jglobus/JGlobus
.. _11: https://github.com/italiangrid/voms-clients
.. _12: https://github.com/italiangrid/voms-admin-server/tree/master/voms-admin-api

The complete list of software, tools and APIs we have used to implement the new crypto library interface are listed below:

- Apache Application Server [5_],

- JAX-RS, the Java API for RESTful Web Services (JSR 311 standard) [6_],

- Java Technology Standard Edition (Java SE6) [7_],

- The Cryptographic Token Interface Standard (PKCS#11) libraries [8_],

- The open-source BouncyCastle Java APIs [9_],

- The JGlobus-Core Java APIs [10_],

- The VOMS-clients Java APIs [11_],

- The VOMS-Admin Java APIs [12_].


============
Installation
============

For more details about how to configure and install the servlet, please refer to the installation document.

============
Usage
============

For more details about how to work with the servlet, please refer to the installation document.
   
============
Contributor(s)
============
Please feel free to contact us any time if you have any questions or comments.

.. _INFN: http://www.ct.infn.it/

:Authors:

 Roberto BARBERA - Italian National Institute of Nuclear Physics (INFN_),
 
 Giuseppe LA ROCCA - Italian National Institute of Nuclear Physics (INFN_),
 
 Salvatore MONFORTE - Italian National Institute of Nuclear Physics (INFN_)

.. toctree::
   :maxdepth: 2

   install


