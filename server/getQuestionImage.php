<?php
error_reporting(0);
/*
 Used in: QuestionActivity, Following code will get the questions image
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

$id = intval($_POST["id"]);
// get all friends from friends table
$result = mysql_query("SELECT imgString FROM questionstb WHERE id=$id") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
	
	$row = mysql_fetch_array($result);
    $response["imgString"] = $row["imgString"];
	//OVO NISU SVI ATRIBUTI, ALI SAMO NAM OVI I TREBAJU
	// success
    $response["success"] = 1;
	 echo json_encode($response);
}
    else {
    // no friends found
    $response["success"] = 0;
    $response["message"] = "No friends found";

    // echo no friends JSON
    echo json_encode($response);
}
?>
