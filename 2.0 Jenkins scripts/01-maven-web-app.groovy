node{
def MavenHome = tool name: "maven3.8.5"
//Git Repository
stage('SourceCode'){
git credentialsId: 'Git-srinivas', url: 'https://github.com/KRSSrinivas/maven-web-app.git'
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
sshagent(['srinivas']) {
sh "scp -o StrictHostKeyChecking=no target/01-maven-web-app.war ec2-user@18.117.99.125:/opt/apache-tomcat-9.0.74/webapps"
}
}
}//node closing
