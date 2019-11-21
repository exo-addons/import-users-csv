Addon Import Users from CSV
=============================

Versions and Features
=================
Version 1.2.4 : Add the possibility to fill user profile


Build & install
=================



Checkout the code, go at root folder and type 

    mvn clean install
  

Copy file import-users-csv\webapps\target\import-users-csv.war into Platform-Tomcat/webapps

Copy file import-users-csv\services\target\import-users-csv-service-1.0.x-SNAPSHOT.jar into Platform-Tomcat/lib

Start your exo server, log with an administrator.

Follow this [documentation](http://docs.exoplatform.com/public/index.jsp?topic=%2FPLF42%2FPLFDevGuide.DevelopingApplications.DevelopingPortlet.Deployment.UI.html) to add the "ImportUsersFromCSV" portlet to a page.

Congratulations, you can now import list of users from a csv file.

You can use the following csv sample to import users: [sample](https://raw.githubusercontent.com/exo-addons/import-users-csv/master/assets/sample.csv)
