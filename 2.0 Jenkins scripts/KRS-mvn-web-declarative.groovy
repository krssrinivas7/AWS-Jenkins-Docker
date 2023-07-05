pipeline{
agent any
stages{
//GitHub
stage('SourceCode'){
steps{
git branch: 'main', credentialsId: 'KRSSrinivas', url: 'https://github.com/krssrinivas7/krssrinivas-mvn-app.git'
}
}
//Maven
stage('Build)'{
steps{
bat "$M2_HOME/bin/mvn clean package"
}
}
//Sonarqube
stage('CodeQuality'){
steps{
bat "$M2_HOME/bin/mvn sonar:sonar"
}
}
//Nexus Repository
stage('ArtifactRepo'){
steps{
bat "$M2_HOME/bin/mvn clean deploy"
}
}
//Tomcat Deploy
stage('Deploy'){
steps{
bat 'copy "C:\\JENKINSHOME\\workspace\\srinivas-declarative\\target\\KRS-maven-web-app.war" "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps"\\KRS-maven-web-app.war'
}
}
}
}//node close





pipeline{
agent any
stages{
//GitHub
stage('SourceCode'){
steps{
git branch: 'main', credentialsId: 'KRSSrinivas', url: 'https://github.com/krssrinivas7/krssrinivas-mvn-app.git'
}
}
//Maven
stage('Build)'{
steps{
bat "$M2_HOME/bin/mvn clean package"
}
}
//Tomcat Deploy
stage('Deploy'){
steps{
bat 'copy "C:\\JENKINSHOME\\workspace\\srinivas-pipeline\\target\\KRS-maven-web-app.war" "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps"\\KRS-maven-web-app.war'
}
}
}
}//node close

