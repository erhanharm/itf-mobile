<?php
   
$dbhandle = sqlite_open('data.db', 0666, $error);

if (!$dbhandle) die ($error);


$query = "SELECT *  from pushs";
$result = sqlite_query($dbhandle, $query);
if (!$result) die("Cannot execute query.");

echo "pushID - Message - created";
echo "<br>";

while ($row = sqlite_fetch_array($result, SQLITE_ASSOC)) {
    echo $row['pushID']  . " : " . $row['message'] . " :" . $row['created_date'];
    echo "<br>";
}


sqlite_close($dbhandle);

?>