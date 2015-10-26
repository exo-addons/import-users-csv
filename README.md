Addon Import Users from CSV
=============================


Build & install
=================

Checkout the code, go at root folder and type 

    mvn clean install
  

Copy file exo-addons-portlets-importUsersFromCSV-webapps\target\importUsersFromCSV-portlets.war into Platform-Tomcat/webapps

Copy file\exo-addons-portlets-importUsersFromCSV-service\target\exo-addons-portlets-importUsersFromCSV-service-1.0-SNAPSHOT.jar and marketplace-extension/config/target/marketplace-extension-config-1.0.x-SNAPSHOT.jar into Platform-Tomcat/lib

Start your exo server, log with an administrator.

Follow this [documentation](http://docs.exoplatform.com/public/index.jsp?topic=%2FPLF42%2FPLFDevGuide.DevelopingApplications.DevelopingPortlet.Deployment.UI.html) to add the "ImportUsersFromCSV" portlet to a page.

Congratulations, you can now import list of users from a csv file.

You can use the following csv sample to import users: [sample](https://raw.githubusercontent.com/exo-addons/import-users-csv/master/assets/sample.csv)