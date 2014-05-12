<?php
$response = array();
 
 if (isset($_POST['nTop'])) {
     
    $nTop = $_POST['nTop'];
    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();
     
    $result = mysql_query("SELECT answer.userId as userId, user.name as userName, user.email, count(_like.id) as counterLiked FROM answer, _like, user WHERE answer.id = _like.answerId AND answer.userId = user.id GROUP BY answer.userId ORDER BY counterLiked DESC LIMIT ".$nTop) or die(mysql_error());
     
    if ($result && mysql_num_rows($result) > 0) {
        $response["TopExpert"] = array();
        while ($row = mysql_fetch_array($result)) {
            $expert = array();
            $expert["id"] = $row["userId"];
            $expert["name"] = $row["userName"];
            $expert["email"] = $row["email"];
            $expert["counterLiked"] = $row["counterLiked"];

            $result_tmp = mysql_query("SELECT answer.userId as userId, count(_dislike.id) as counterDisLiked FROM answer, _dislike, user WHERE answer.id = _dislike.answerId AND answer.userId = user.id AND answer.userId = 1 GROUP BY answer.userId") or die(mysql_error());
            if ($result_tmp && mysql_num_rows($result_tmp) > 0) 
            {
                while ($row_tmp = mysql_fetch_array($result_tmp)) {
                    $expert["counterDisLiked"] = $row_tmp["counterDisLiked"];
                    break;
                }
            }else{
                $expert["counterDisLiked"] = 0;
            }

            $isHave = 0;
            $result_tmp = mysql_query("SELECT Count(id) as counterAsked FROM question WHERE forUserId = ".$row["userId"]." GROUP BY forUserId") or die(mysql_error());
            if ($result_tmp && mysql_num_rows($result_tmp) > 0) 
            {
                while ($row_tmp = mysql_fetch_array($result_tmp)) {
                    $expert["counterAsked"] = $row_tmp["counterAsked"];
                    $isHave = 1;
                    break;
                }
            }else{
                $expert["counterAsked"] = 0;
            }
            if ($isHave == 0) {
                $expert["counterAsked"] = 0;
            }

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