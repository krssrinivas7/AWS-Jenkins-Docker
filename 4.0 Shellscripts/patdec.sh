$1
i=10
for (( i=$1;i>=1;i-- ))
do
for (( j=1;j<=i;j++ ))
do
echo -n "$j  "
done
echo
done