FROM eclipse-temurin:17-jdk-alpine
COPY ./build/libs/*SNAPSHOT.jar project.jar
ENTRYPOINT ["java", \
 "-javaagent:/home/ubuntu/pinpoint-agent-2.5.3/pinpoint-bootstrap-2.5.3.jar", \
 "-Dpinpoint.agentId=aws-ec2-1", \
 "-Dpinpoint.applicationName=api-server", \
 "-Dspring.profiles.active=staging", \
 "-jar", "project.jar"]