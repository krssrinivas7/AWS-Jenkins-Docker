if [ $# -eq 2 ]
then
echo "first Argument" $1
echo "second Argument" $2
echo "addition of $1 and $2 is" `expr $1 + $2`
echo "substraction of $1 and $2 is" `expr $1 - $2`
echo "substraction of $2 and $1 is" `expr $2 - $1`
else
echo "Provide only two Arguments"
echo "usage: sh assignment.sh arg1 arg2"
fi