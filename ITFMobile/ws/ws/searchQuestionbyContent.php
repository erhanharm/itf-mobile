<?php 

$response = array();
 
if (isset($_POST['content'])) {
     
    $content = $_POST['content'];

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();
     
    $result = mysql_query("SELECT question.id as id, question.forUserId, content, time, categoryId, category.categoryName, userId, user.name as userName, isAnswered FROM question, user, category WHERE question.userId = user.id AND question.categoryId = category.id AND content like '%".$content."%' ORDER BY time DESC LIMIT 50") or die(mysql_error());
     
    if ($result && mysql_num_rows($result) > 0) {
        $response["questions"] = array();
        while ($row = mysql_fetch_array($result)) {
                $question = array();
                $question["id"] = $row["id"];
                $question["forUserId"] = $row["forUserId"];
                $question["content"] = $row["content"];
                $question["time"] = $row["time"];
                $question["categoryId"] = $row["categoryId"];
                $question["categoryName"] = $row["categoryName"];
                $question["userId"] = $row["userId"];
                $question["userName"] = $row["userName"];
                $question["isAnswered"] = $row["isAnswered"];

                array_push($response["questions"], $question);
        }
        $response["success"] = 1;
        $response["message"] = "success";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No questions found";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    echo json_encode($response);
}
?>