<?php
error_reporting(0);
/*
 Used in: MainActivity, Following code will list all the users and questions
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// get all users from users table
$result = mysql_query("SELECT * FROM usertb") or die(mysql_error());
$resultQ = mysql_query("SELECT * FROM questionstb") or die(mysql_error());
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // users node
    $response["users"] = array();
    $response["questions"]= array();
	
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $user = array();
        $user["id"] = $row["id"];
		$user["username"] = $row["username"];
        $user["name"] = $row["name"];
        $user["surname"] = $row["surname"];
        $user["latitude"] = $row["latitude"];
        $user["longitude"] = $row["longitude"];
        $user["email"] = $row["email"];
		$user["score"] = $row["score"];
		$user["status"] = $row["status"];
		$user["friends"] = array();
        //OVO NISU SVI ATRIBUTI, ALI SAMO NAM OVI I TREBAJU
		
		// get all friends from friends table
		$idk = intval($user['id']);
		$result1 = mysql_query("SELECT * FROM friendstb WHERE userid=$idk") or die(mysql_error());

		// check for empty result
		if (mysql_num_rows($result1) > 0) {
			// looping through all results
			$response["friends"] = array();
			
			while ($row = mysql_fetch_array($result1)) {
				$friend = array();
				$friend["friendid"] = $row["friendid"];
				
				array_push($user["friends"], $friend);
			}
		}
		
        // push single user into final response array
        array_push($response["users"], $user);
    }
	while ($row = mysql_fetch_array($resultQ)) {
				
		$question = array();
        $question["id"] = $row["id"];
		$question["question"] = $row["question"];
        $question["answerA"] = $row["answerA"];
        $question["answerB"] = $row["answerB"];
        $question["answerC"] = $row["answerC"];
        $question["answerD"] = $row["answerD"];
        $question["correct"] = $row["correct"];
		$question["difficulty"] = $row["difficulty"]; 
		$question["latitude"] = $row["latitude"];
		$question["longitude"] = $row["longitude"];
		array_push($response["questions"], $question);
    }
		
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // no users found
    $response["success"] = 0;
    $response["message"] = "No users found";

    // echo no users JSON
    echo json_encode($response);
}
?>
