node
{
  stage("Build Code")
  {
     git 'https://github.com/SujataKale97/shopizer.git'
     sh '''
        sudo mvn clean install docker:build
     '''
  }
  stage("Deploy code to tomcat server")
  {
    //sh 'sudo cp sm-shop/target/*.war /usr/share/tomcat/webapps/'
  }
}
