echo "Comman line Arguments Demo"
echo "Script Name" $0
echo "First Argument" $1
echo "Second Argument" $2
echo "Third Argument" $3
echo "Fourth Argument" $4
echo "Tenth Argument" ${10}
echo "No of Arguments" $#
echo "All Arguments in one string" $*
echo "All Arguments in diff string" $@
echo "Process ID" $$
Date
echo "Previous Command Execution Status" $?