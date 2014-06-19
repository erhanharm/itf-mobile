<?php
$response = array();
 
 if (isset($_POST['nTop'])) {
     
    $nTop = $_POST['nTop'];
    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();
     
    $result = mysql_query("SELECT reply.user_id as user_id, user.name as userName, user.email, count(_like.id) as counterLiked FROM reply, _like, user WHERE reply.id = _like.reply_id AND reply.user_id = user.id AND user.id<>-1 GROUP BY reply.user_id ORDER BY counterLiked DESC LIMIT ".$nTop) or die(mysql_error());
     
    if ($result && mysql_num_rows($result) > 0) {
        $response["topmembers"] = array();
        while ($row = mysql_fetch_array($result)) {
            $user = array();
            $user["id"] = $row["user_id"];
            $user["name"] = $row["userName"];
            $user["email"] = $row["email"];
            $user["counterLiked"] = $row["counterLiked"];

            array_push($response["topmembers"], $user);
        }
        $response["success"] = 1;
        $response["message"] = "success";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No member found";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>