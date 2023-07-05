node{
def MavenHome = tool name: "maven3.8.5"
//Git Repository
stage('SourceCode'){
git credentialsId: 'srinivas', url: 'https://github.com/KRSSrinivas/maven-web-application.git'
}
//Maven
stage('Build'){
sh "$MavenHome/bin/mvn clean package"
}
//Sonarqube
stage('CodeReview'){
sh "$MavenHome/bin/mvn sonar:sonar"
}
//Nexus Repo
stage('Artifactupload'){
sh "$MavenHome/bin/mvn deploy"
}
//Tomcat Server
stage('Deploy'){
sshagent(['KRSsrinivasjenkins']) {
sh "scp -o StrictHostKeyChecking=no target/maven-web-application.war ec2-user@52.15.113.129:/opt/apache-tomcat-9.0.74/webapps"
}
}
}//node closing
