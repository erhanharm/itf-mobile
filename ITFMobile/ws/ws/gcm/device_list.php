<?php
   
$dbhandle = sqlite_open('data.db', 0666, $error);

if (!$dbhandle) die ($error);


$query = "SELECT regID, type  from devices";
$result = sqlite_query($dbhandle, $query);
if (!$result) die("Cannot execute query.");

echo "regID : type" . "<br>";
 
while ($row = sqlite_fetch_array($result, SQLITE_ASSOC)) {
    echo $row['regID']  . " : " . $row['type'];
    echo "<br>";
}




sqlite_close($dbhandle);

?>