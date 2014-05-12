<?php

$response = array();
 
if (isset($_POST['questionId']) && isset($_POST['userId'])) {
     
    $questionId = $_POST['questionId'];

    if(isset($_POST['userId']))
        $userCurrentId = $_POST['userId'];
    else
        $userCurrentId = -1;

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();
     
    $result = mysql_query("SELECT answer.id as id, questionId, content, userId, user.name as userName, time FROM answer, user where answer.userId = user.id AND questionId = ".$questionId) or die(mysql_error());
     
    if ($result && mysql_num_rows($result) > 0) {
        $response["answers"] = array();
     
        while ($row = mysql_fetch_array($result)) {
            $answer = array();
            $answer["id"] = $row["id"];
            $answer["questionId"] = $row["questionId"];
            $answer["content"] = $row["content"];
            $answer["userId"] = $row["userId"];
            $answer["userName"] = $row["userName"];
            $answer["time"] = $row["time"];

            $result_temp = mysql_query("SELECT count(id) as countLiked FROM _like WHERE answerId = ".$answer["id"]." group by answerId") or die(mysql_error());
            if ($result_temp && mysql_num_rows($result_temp) > 0) {
                while ($row = mysql_fetch_array($result_temp)) {
                $answer["countLiked"] = $row["countLiked"];
                }
            }else{
                $answer["countLiked"] = 0;
            }

            $result_temp = mysql_query("SELECT count(id) as countDisLiked FROM _dislike WHERE answerId = ".$answer["id"]." group by answerId") or die(mysql_error());
            if ($result_temp && mysql_num_rows($result_temp) > 0) {
                while ($row = mysql_fetch_array($result_temp)) {
                $answer["countDisLiked"] = $row["countDisLiked"];
                }
            }else{
                $answer["countDisLiked"] = 0;
            }

            $result_temp = mysql_query("SELECT id FROM _like WHERE answerId = ".$answer["id"]." AND userId = ".$userCurrentId) or die(mysql_error());
            if ($result_temp && mysql_num_rows($result_temp) > 0) {
                while ($row = mysql_fetch_array($result_temp)) {
                    $answer["isLiked"] = 1;
                }
            }else{
                $answer["isLiked"] = 0;
            }

            $result_temp = mysql_query("SELECT id FROM _dislike WHERE answerId = ".$answer["id"]." AND userId = ".$userCurrentId) or die(mysql_error());
            if ($result_temp && mysql_num_rows($result_temp) > 0) {
                while ($row = mysql_fetch_array($result_temp)) {
                    $answer["isDisLiked"] = 1;
                }
            }else{
                    $answer["isDisLiked"] = 0;
            }

            array_push($response["answers"], $answer);
        }
        // success
        $response["success"] = 1;
        $response["message"] = "success";
     
        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No answers found";
     
        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>