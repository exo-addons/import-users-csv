Addon Import Users from CSV
=============================


Build & install
=================

Checkout the code, go at root folder and type 

    mvn clean install
  
In exo-addons-portlets-importUsersFromCSV-pkg/target, a zip is created. Copy it and unzip it in your eXo installation, in extensions folder. Then type

    ./extension.sh -i importUsersFromCSV

to deploy the portlet.

Start your exo server, log with an administrator and go in "Administration" -> "Applications" menu. Add the portlet "Import Users From CSV" in a category. Then go on your home page and add the portlet on the right column. 

Congratulations, you can now import list of users from a csv file.
