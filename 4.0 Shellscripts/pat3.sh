$1
$2
k=1
for (( i=1;i<=$1;i++ ))
do
for (( j=1;j<=i;j++ ))
do
echo -n "$k "
k=$((k + 1 ))
done
echo
done