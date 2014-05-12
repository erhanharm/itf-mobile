<?php

$dbhandle = sqlite_open('data.db', 0666, $error);
if (!$dbhandle) die ($error);


$stm = "CREATE TABLE devices(regID text PRIMARY KEY, type integer)";
$ok = sqlite_exec($dbhandle, $stm, $error);

if (!$ok)
   die("Cannot execute query. $error");

echo "Database devices created successfully";

$query = "CREATE TABLE pushs(
            pushID INTEGER PRIMARY KEY,
            message TEXT NOT NULL,                      
            created_date DATETIME
            )";
            
$ok2 = sqlite_exec($dbhandle, $query, $error);

if (!$ok2)
   die("Cannot execute query. $error");

echo "\n Database pushs created successfully";
            
sqlite_close($dbhandle);
?>