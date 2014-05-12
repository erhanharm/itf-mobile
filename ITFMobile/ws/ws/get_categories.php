<?php

$response = array();

require_once __DIR__ . '/db_connect.php';
 
$db = new DB_CONNECT();
 
$result = mysql_query("SELECT * FROM category ORDER BY id DESC") or die(mysql_error());
 
if ($result && mysql_num_rows($result) > 0) {
    $response["categories"] = array();
    $count = 0;
    while ($row = mysql_fetch_array($result)) {
        $category = array();
        $category["id"] = $row["id"];
        $category["categoryName"] = $row["categoryName"];
        array_push($response["categories"], $category);
    }
    $response["success"] = 1;
    $response["message"] = "success";

    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No categories found";
 
    echo json_encode($response);
}
?>