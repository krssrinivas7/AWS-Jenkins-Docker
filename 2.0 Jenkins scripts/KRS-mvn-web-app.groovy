//with variable & localhost jenkins, Tomcat & Global tool maven in Jenkins

node{
def M2_HOME = tool name: "maven3.8.8"
//Git Repository
stage('SourceCode'){
git credentialsId: 'KRSSrinivas', url: 'https://github.com/krssrinivas7/krssrinivas-mvn-app.git'
}
//Maven
stage('Build'){
bat "$M2_HOME/bin/mvn clean package"
}
//Tomcat
stage('Deploy'){
bat 'copy "C:\\JENKINSHOME\\workspace\\srinivas-pipeline\\target\\KRS-maven-web-app.war" "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps"\\KRS-maven-web-app.war'
}
}//node close



//with variable & localhost Jenkins, Tomcat & Maven
node{
//Git Repository
stage('SourceCode'){
git branch: 'main', credentialsId: 'KRSSrinivas', url: 'https://github.com/krssrinivas7/krssrinivas-mvn-app.git'
}
//Maven
stage('Build'){
bat "$M2_HOME/bin/mvn clean package"
}
//Tomcat
stage('Deploy'){
bat 'copy "C:\\JENKINSHOME\\workspace\\srinivas-pipeline\\target\\KRS-maven-web-app.war" "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps"\\KRS-maven-web-app.war'
}
}//node close
