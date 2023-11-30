FROM tomcat:10.0-jdk17-openjdk
WORKDIR /usr/local/tomcat
ADD ./target/scc2324-lab1-1.0.war webapps
EXPOSE 8080