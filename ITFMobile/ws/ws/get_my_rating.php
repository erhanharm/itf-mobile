<?php
$response = array();
 
 if (isset($_POST['nTop'])) {
     
    $nTop = $_POST['nTop'];
    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();
     
    $result = mysql_query("SELECT user.id , user.name, user.email, user.isAnonymous, count(user.id) as counterLiked FROM answer, user, _like WHERE answer.userId = user.id AND answer.id = _like.answerId AND answer.userId = ".$userId." GROUP BY user.id LIMIT ".$nTop) or die(mysql_error());
     
    if ($result && mysql_num_rows($result) > 0) {
        $response["TopExpert"] = array();
        while ($row = mysql_fetch_array($result)) {
            $expert = array();
            $expert["id"] = $row["id"];
            $expert["name"] = $row["name"];
            $expert["email"] = $row["email"];
            $expert["counterLiked"] = $row["counterLiked"];

            array_push($response["TopExpert"], $expert);
        }
        $response["success"] = 1;
        $response["message"] = "success";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No expert found";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>