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