FROM ubuntu
USER root
RUN apt update -y \
    && apt install openjdk-11-jdk -y
ARG user=jenkins
ARG group=jenkins
ARG uid=1001
ARG gid=1001
ENV JENKINS_HOME=/home
RUN groupadd -g ${gid} ${group} \
    && useradd -d $JENKINS_HOME -u ${uid} -g ${gid} -m -s /bin/bash ${user} \
    && chown -R ${user}:${group} $JENKINS_HOME \
    && chmod -R 777 $JENKINS_HOME
WORKDIR $JENKINS_HOME
ADD https://get.jenkins.io/war-stable/2.401.2/jenkins.war $JENKINS_HOME
RUN chown -R ${user}:${group} $JENKINS_HOME/jenkins.war \
    && chmod -R 777 $JENKINS_HOME/jenkins.war
USER ${user}
CMD ["java", "-jar", "jenkins.war"]
