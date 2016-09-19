# camunda-liferay-tasks
Java Delegates for Camunda Service Tasks, also triggering the custom Liferay Camunda worfklow API (https://github.com/iisys-hof/liferay-camunda-workflow-api)

Configuration: /src/main/resources/liferay-tasks.properties

Installation:

* Import into Eclipse as a Maven project
* execute Maven build with package goal
* place the generated jar file in Camunda's tomcat lib folder
* place https://mvnrepository.com/artifact/org.json/json/20160212 in the same folder
* restart Camunda