<?php
$response = array();
 
if (isset($_POST['name']) && isset($_POST['note']) && isset($_POST['parrent_id'])) {
 
    $note = $_POST['note'];
    $parrent_id = $_POST['parrent_id'];
    $name = $_POST['name'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();

    $result = mysql_query("INSERT INTO folder(name, note, parrent_id) VALUES('$name', '$note', '$parrent_id')");
       if ($result) {
            $response["success"] = 1;
            $response["message"] = "Create new folder completed.";
     
            echo json_encode($response);
        } else {
            $response["success"] = 0;
            $response["message"] = "Oops! An error occurred.";

            echo json_encode($response);
        }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>