FROM tomcat:7-jre8
COPY sm-shop/target/*.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["/usr/local/tomcat/bin/catalina.sh","run"]
