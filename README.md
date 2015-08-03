To deploy to JBoss after compile and package (as WAR, EAR is todo):

mvn -DskipTests clean package  jboss-as:redeploy

then run (integration & unit) tests OR remove skipTests above ; appears to run the same

mvn test

http://localhost:8080/resteasy-queue/rest/push/22,31

http://localhost:8080/resteasy-queue/rest/list 
will return JSON like: 
{"queueElements":[22,31,22,31,22,31]}

SOAP tests in unico.gcd.SoapResourceTest
REST tests in unico.gcd.RestResourceTest
Both are deployed as part of Maven test goal

All functionality is there but no load test for 20 users as yet.