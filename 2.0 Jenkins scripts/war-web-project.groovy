node{
def MavenHome = tool name: "maven3.8.5"
//Git Repository
stage('SourceCode'){
git credentialsId: 'Github-Srinivas', url: 'https://github.com/KRSSrinivas/war-web-project.git'
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
sh "scp -o StrictHostKeyChecking=no target/wwp-1.0.0-SNAPSHOT.war ec2-user@3.129.24.161:/opt/apache-tomcat-9.0.74/webapps"
}
}
}//node closing