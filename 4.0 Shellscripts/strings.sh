var="Hi I am Srinivas, Working as DevOps Engineer"
#To see full string
echo ${var}
#Also displays full string
echo ${var:0}
#Displays last 8 Chars
echo ${var: -8}
#Displays full string by ignoring first 5 chars,and requires 11 chars
echo ${var:5:11}
#if we want first 7 letters
echo ${var:0:7}
