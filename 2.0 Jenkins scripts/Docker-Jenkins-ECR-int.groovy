node{

def M2_HOME = tool name: "maven3.8.5"
def BN = BUILD_NUMBER

stage('Git Clone'){
git branch: 'main', credentialsId: 'KRSSrinivas-Git', url: 'https://github.com/krssrinivas7/krssrinivas-mvn-app.git'
}

stage('Build'){
sh "$M2_HOME/bin/mvn clean package"
}

stage('DockerImage into ECR'){
sh "aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 483913793358.dkr.ecr.us-east-2.amazonaws.com"
sh "docker build -t 483913793358.dkr.ecr.us-east-2.amazonaws.com/srinivas-mvn-app:$BN ."
sh "docker push 483913793358.dkr.ecr.us-east-2.amazonaws.com/srinivas-mvn-app:$BN"
}

stage('Dowload Image & Create Container'){
sshagent(['Srinivas']) {
script {
sh "ssh -o StrictHostKeyChecking=no ubuntu@3.19.237.13 aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 483913793358.dkr.ecr.us-east-2.amazonaws.com"
}
sh "ssh -o StrictHostKeyChecking=no ubuntu@3.19.237.13 docker rm -f KRSwebappcontainer || true"
sh "ssh -o StrictHostKeyChecking=no ubuntu@3.19.237.13 docker run -d -p 8080:8080 --name KRSwebappcontainer 483913793358.dkr.ecr.us-east-2.amazonaws.com/srinivas-mvn-app:$BN"
}
}
}
