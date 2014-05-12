<?php

$response = array();
 
 if (isset($_POST['userId'])) {
    $userId = $_POST['userId'];
    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

    $result = mysql_query("SELECT answer.userId as id, count(_like.id) as counterLiked FROM answer, _like WHERE _like.answerId = answer.id GROUP BY answer.userId ORDER BY counterLiked DESC") or die(mysql_error());
     $MyExpert = array();

    if ($result && mysql_num_rows($result) > 0) {
        $response["myexpert"] = array();
        $count = 1;
        while ($row = mysql_fetch_array($result)) {
            if($row["id"] == $userId){
                $MyExpert["rank"] = $count;
               // $MyExpert["counterLiked"] = $row["counterLiked"];

                //get sum answer liked
                mysql_query("CREATE TEMPORARY TABLE IF NOT EXISTS temp_tb AS (SELECT count(answer.id) as counter FROM answer, _like WHERE answer.id = _like.answerId AND answer.userId = ".$userId." GROUP BY answer.id)");
                $result = mysql_query("SELECT count(counter) as counterLiked FROM temp_tb") or die(mysql_error());
                if ($result && mysql_num_rows($result) > 0) {
                    while ($row = mysql_fetch_array($result)) 
                    {
                        $MyExpert["counterLiked"] = $row["counterLiked"];
                    }
                }else{
                    $MyExpert["counterLiked"] = 0;
                }
                //get sum answer
                $result  = mysql_query("SELECT Count(id) as counterAnswer FROM answer WHERE userId = ".$userId ." GROUP BY userId") or die(mysql_error());
                if ($result && mysql_num_rows($result) > 0) {
                    while ($row = mysql_fetch_array($result)) 
                    {
                        $MyExpert["counterAnswer"] = $row["counterAnswer"];
                    }
                }else{
                    $MyExpert["counterAnswer"] = 0;
                }
                // //get sum question
                $result = mysql_query("SELECT Count(id) as counterQuestion FROM question WHERE userId = ".$userId." GROUP BY userId") or die(mysql_error());
                if ($result && mysql_num_rows($result) > 0) {
                    $response["myexpert"] = array();
                    while ($row = mysql_fetch_array($result)) 
                    {
                        $MyExpert["counterQuestion"] = $row["counterQuestion"];
                    }
                }else{
                    $MyExpert["counterQuestion"] = 0;
                }
                break;
            }
            $count++;
        }

        array_push($response["myexpert"], $MyExpert);
        // success
        $response["success"] = 1;
        $response["message"] = "success";
     
        // echoing JSON response
        echo json_encode($response);
    } else {
        // no products found
        $response["success"] = 0;
        $response["message"] = "No myexpert found";
     
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>