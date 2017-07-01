<?php
error_reporting(0);
/*
 Used in: QuestionActivity, Following code will update users score after playing game
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();
if(isset($_POST["userId"]) && isset($_POST["newScore"]))
{
	$userId = intval($_POST["userId"]);
    $newScore = intval($_POST["newScore"]);
}
// get all users from users table
$result  = mysql_query("UPDATE usertb SET score=$newScore WHERE id=$userId") or die(mysql_error());
// check for empty result
if ($result) {		
    // success
    $response["success"] = 1;
    // echoing JSON response
    echo json_encode($response);
} else {
    // no users found
    $response["success"] = 0;
    $response["message"] = "Error during update";

    // echo no users JSON
    echo json_encode($response);
}
?>
