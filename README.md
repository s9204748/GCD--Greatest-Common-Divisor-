##Build/Deploy

To deploy to JBoss after compile and package (as WAR, EAR is todo):

`mvn -DskipTests clean package  jboss-as:redeploy`

I deployed on the latest JBoss AS v7.1.1Final using Maven.
##Tests

currently one REsT test fails with a hTTP 406 - an issue with JSON headers I believe; hence skipTests above to compile and package.
then run (integration & unit) tests OR remove skipTests above ; appears to run the same

`mvn test`

http://localhost:8080/resteasy-queue/rest/push/22,31

http://localhost:8080/resteasy-queue/rest/list 
will return JSON like: 
{"queueElements":[22,31,22,31,22,31]}

SOAP tests in unico.gcd.SoapResourceTest
REST tests in unico.gcd.RestResourceTest
Both are deployed as part of Maven test goal

##Non Functional Requirements
All functionality is there but no load test for 20 users as yet.