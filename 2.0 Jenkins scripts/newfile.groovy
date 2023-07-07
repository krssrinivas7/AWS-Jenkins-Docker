pipeline{
agent any
environment {
        AWS_ACCOUNT_ID="483913793358"
        AWS_DEFAULT_REGION="us-eest-2"
        IMAGE_REPO_NAME="srinivas-mvn-app"
        IMAGE_TAG="BUILD_NUMBER"
        REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}"
}
stages{
stage('git'){
steps{
git branch: 'main', credentialsId: 'KRSSrinivas', url: 'https://github.com/krssrinivas7/krssrinivas-mvn-app.git'
}
}
stage('build'){
steps{
sh "M2_HOME/bin/mvn clean package"
}
}
stage('log into AWS ECR'){
steps{
script{
sh "aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
}
}
}
}
}
