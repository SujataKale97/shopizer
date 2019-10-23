node
{
  stage("Build Code")
  {
     git 'https://github.com/SujataKale97/shopizer.git'
    sh 'sudo mvn install'
  }
  stage("Deploy code to tomcat server")
  {
    sh 'sudo cp target/*.war /usr/lib/tomcat/webapps/'
  }
}
