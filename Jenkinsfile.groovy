node
{
  stage("Build Code")
  {
     git 'https://github.com/SujataKale97/shopizer.git'
    sh '
    cd sm-shop
   sudo mvn spring-boot:run
    '
  }
  stage("Deploy code to tomcat server")
  {
    sh 'sudo cp sm-shop/target/*.war /usr/share/tomcat/webapps/'
  }
}
