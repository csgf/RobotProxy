# eToken

<h2>Requirements</h2>
- JDK 7+

<h2>About the Servlet</h2>
<p align="justify">
A standard-based solution developed by the INFN Catania for central management of robot credentials and provisioning of digital proxies to get seamless and secure access to computing e-Infrastructures supporting the X.509 standard for Authorisation.</br/></br>

This is a servlet based on the Java™ Cryptographic Token Interface Standard (PKCS#11).
For any further information, please visit the official Java™ PKCS#11 Reference Guide <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html">[1]</a>.

By design, the servlet is compliant with the policies reported in these docs <a href="http://www.eugridpma.org/guidelines/pkp/">[1]</a><a href="http://wiki.eugridpma.org/Main/CredStoreOperationsGuideline">[2]</a>.</br></br>
The business logic of the library, deployed on top of an Apache Tomcat Application Server, combines different programming native interfaces and standards (see Fig. 1)

<img width="300" src="https://etokenserver3.ct.infn.it:8443/eTokenServer/images/architecture-2.png" border="0">

</br></br>
</p>

<h2>Contribute</h2>
- Fork it
- Create a branch (git checkout -b my_markup)
- Commit your changes (git commit -am "My changes")
- Push to the branch (git push origin my_markup)
- Create an Issue with a link to your branch
 
<h2>Sponsors</h2>
<p align="justify">
<a href="http://www.infn.it/"><img width="150" src="http://www.infn.it/logo/weblogo1.gif" border="0" title="The INFN Home Page"></a>

<a href="http://www.chain-project.eu/"><img width="150" src="https://www.chain-project.eu/image/image_gallery?uuid=4b273102-2ed0-49ca-929f-c23379318171&groupId=3456180&t=1424446552904" border="0" title="The CHAIN-REDS Home Page"></a>
</p>
