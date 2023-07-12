node{
//def MAVEN_HOME = tool name: "maven3.8.5"
stage('Git Code'){
git branch: 'main', credentialsId: 'KRSSrinivas', url: 'https://github.com/krssrinivas7/krssrinivas-mvn-app.git'   
}
stage('Build'){
bat "$M2_HOME/bin/mvn clean package"    
}
//stage('SonarQubeTest'){
//bat "$M2_HOME/bin/mvn sonar:sonar"
//}
//stage('ArtifactDeploy'){
//bat "$M2_HOME/bin/mvn clean deploy"
//}
stage('Deploy'){
bat 'copy "C:\\JENKINSHOME\\workspace\\krswebapp\\target\\KRS-maven-web-app.war" "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps"\\KRS-maven-web-app.war'
}
}


node{
def MAVEN_HOME = tool name: "maven3.8.5"
stage('Git Code'){
git branch: 'main', credentialsId: 'KRSSrinivas', url: 'https://github.com/krssrinivas7/krssrinivas-mvn-app.git'   
}
stage('Build'){
sh "$MAVEN_HOME/bin/mvn clean package"    
}
stage('SonarQubeTest'){
sh "$MAVEN_HOME/bin/mvn sonar:sonar"
}
stage('ArtifactDeploy'){
sh "$MAVEN_HOME/bin/mvn clean deploy"
}
stage('Deploy'){
sshAgent([sshid]){
sh "scp -o StrictHostKeyChecking=no target/krswebapp.war ec2-user@12.12.12.12:/opt/apache-tomcat-9.076/webapps/krswebapp.war"   
}
}
}