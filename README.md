mvn -DskipTests clean package  jboss-as:redeploy

then run (integration) tests:

mvn test

http://localhost:8080/resteasy-queue/rest/push/22,31 will push onto default JBoss java:/queue/test

http://localhost:8080/resteasy-queue/rest/list (will return JSON like: {"queueElements":[22,31,22,31,22,31]})
--
SOAP tests in unico.gcd.SoapResourceTest