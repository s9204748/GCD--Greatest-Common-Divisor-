mvn -DskipTests clean package  jboss-as:redeploy

then run (integration) tests:

mvn test

http://localhost:8080/resteasy-queue/rest/push/22,31

http://localhost:8080/resteasy-queue/rest/list