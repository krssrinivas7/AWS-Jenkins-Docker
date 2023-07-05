case $1 in
start)
echo "SonarQube Server Starting"
echo "SonarQube Server Started"
;;
stop)
echo "SonarQube Server Stopping"
echo "SonarQube Server Stopped"
;;
restart)
echo "SonarQube Server Restarting"
echo "SonarQube Server Restarted"
;;
*)
echo "Invalid Argument"
echo "Usage: sh $0 start | stop | restart"
esac