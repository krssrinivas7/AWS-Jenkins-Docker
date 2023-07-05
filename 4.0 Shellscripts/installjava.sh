var="javac -version"
$var
if [ $var -eq ""]
then
echo "java installed already"
else
yum install java-11-openjdk-devel -y
$var
fi