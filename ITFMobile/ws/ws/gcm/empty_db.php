<?php

$dbhandle = sqlite_open('data.db', 0666, $error);
if (!$dbhandle) die ($error);


$stm = "delete from devices";
$ok = sqlite_exec($dbhandle, $stm, $error);

if (!$ok)
   die("Cannot execute query. $error");

echo "Database devices empty successfully";

$query = "delete from pushs";
            
$ok2 = sqlite_exec($dbhandle, $query, $error);

if (!$ok2)
   die("Cannot execute query. $error");

echo "\n Database pushs empty successfully";
            
sqlite_close($dbhandle);
?>