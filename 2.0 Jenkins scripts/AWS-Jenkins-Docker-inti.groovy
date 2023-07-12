//aws EC2 instance attached with IAM Role having permissions of EC2containerregistryfullaccess and ECRPublicfullaccess
//Generate access key ID and secrete access key
//In Jenkins install plugins pipeline: AWS Steps, Docker, Dockerpipeline
//Install AWS CLI in EC2 Instance
//Install java, Docker, Jenkins in Server
//Add $USER and jenkins user to Docker group
// give permissions 
// sudo chmod 666 /var/run/docker.sock
//grep /etc/group -e "docker"
//grep /etc/group -e "sudo"
//give aws credentials in the form of above keys in snippet generator (withAWS: set AWS settings for nested block)
//Create repository in ECR
//Create Dockerfile to build docker image
//we have to login docker hub in both instances or create pipeline script for that also
pipeline {
    agent any
      stages {
        stage('Checkout Git Code'){
            steps {
                script {
                    git branch: 'main', credentialsId: 'srinivas-git', url: 'https://github.com/krssrinivas7/AWS-Jenkins-Docker.git'

                }
            }
        }
        stage('AWS Login') {
            steps {
                script {
                    withAWS(credentials: 'srinivas-aws', region: 'us-east-1') {
                        sh "aws --version"
                    }
                }
            }
        }
        
        stage('AWS ECR Login,Creation of Docker Image & Push'){
            steps {
                script {
                    sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 483913793358.dkr.ecr.us-east-1.amazonaws.com"
                       }
                script {
                    sh "docker build -t 483913793358.dkr.ecr.us-east-1.amazonaws.com/srinivas:${env.BUILD_NUMBER} ."
                }
                script {
                    sh "docker push 483913793358.dkr.ecr.us-east-1.amazonaws.com/srinivas:${env.BUILD_NUMBER}"
                }       
            }
        }
        stage('Create Container on Deployment Server'){
            steps{
                sshagent(['srinivas-ssh']) {
                script {
                withAWS(credentials: 'srinivas-aws', region: 'us-east-1') {
                sh "ssh -o StrictHostKeyChecking=no ubuntu@52.205.253.17 aws --version"
                }
                }
                script {
                sh "ssh -o StrictHostKeyChecking=no ubuntu@52.205.253.17 aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 483913793358.dkr.ecr.us-east-1.amazonaws.com"
                       }
                script {
                sh "ssh -o StrictHostKeyChecking=no ubuntu@52.205.253.17 docker pull 483913793358.dkr.ecr.us-east-1.amazonaws.com/srinivas:${env.BUILD_NUMBER}"
                }
                script {
                sh "ssh -o StrictHostKeyChecking=no ubuntu@52.205.253.17 docker rm -f srinivascontainer || true"
                }
                script {
                sh "ssh -o StrictHostKeyChecking=no ubuntu@52.205.253.17 docker run -d --name srinivascontainer -p 8080:8080 483913793358.dkr.ecr.us-east-1.amazonaws.com/srinivas:${env.BUILD_NUMBER}"
                }
            }
        }
    }
}
}