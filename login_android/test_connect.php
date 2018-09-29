<?php
$host="localhost";
$user="root";
$password="";
$con=mysqli_connect($host,$user,$password);
if($con){
echo '<h1> Connected to MySQL </h1>';
}
else{
echo '<h1> Not connected to MySQL </h1>';
}
?>