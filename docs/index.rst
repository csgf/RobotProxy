*********************
eToken v2.0.4 Docs
*********************

============
About
============

.. _1: http://docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html
.. _2: http://wiki.eugridpma.org/Main/CredStoreOperationsGuideline
.. _3: install.rst

A standard-based solution developed by the INFN Catania for central management of robot credentials and provisioning of digital proxies to get seamless and secure access to computing e-Infrastructures supporting the X.509 standard for Authorisation.

This is a servlet based on the Java™ Cryptographic Token Interface Standard (PKCS#11). For any further information, please visit the official Java™ PKCS#11 Reference Guide [1_]. By design, the servlet is compliant with the policies reported in these docs [1_][2_].

The business logic of the library, deployed on top of an Apache Tomcat Application Server, combines different programming native interfaces and standards.

The high-level architecture of the eToken servlet is shown in the below figure:

.. image:: images/architecture.jpg
   :align: center

Installation
-----------------

For more details about how to configure and install the servlet, please refer to this document [3_]

============
Usage
============

For more details about how to work with the servlet, please refer to this document [3_]
   
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
