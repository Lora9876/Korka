<?php
error_reporting(0);
/*
 Used in: AddQuestion Activity
 */
 
// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['answerB']) &&isset($_POST['answerC']) &&isset($_POST['question']) && isset($_POST['answerA']) && isset($_POST['answerD'])&& isset($_POST['correct'])) {
    $difficulty = $_POST['difficulty'];
    $question = $_POST['question'];
    $answerA = $_POST['answerA'];
	$answerB = $_POST['answerB'];
	$answerC = $_POST['answerC'];
    $answerD = $_POST['answerD'];
	$correct = $_POST['correct'];
	$imgString = $_POST['imgString'];
	$latitude = $_POST['latitude'];
	$longitude = $_POST['longitude'];
    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

		// mysql inserting a new row
		$result = mysql_query("INSERT INTO questionstb (question, answerA, answerD, answerB, answerC, correct,imgString, latitude, longitude, difficulty) VALUES('$question', '$answerA', '$answerD','$answerB','$answerC', '$correct','$imgString','$latitude', '$longitude', '$difficulty')");

		// check if row inserted or not
		if ($result) {
			// successfully inserted into database
			
			$result1 = mysql_query("SELECT id FROM questionstb WHERE question='$question'" ) or die(mysql_error());
			 while ($row = mysql_fetch_array($result1)) {
				$response["success"] = 1;
				$response["questionId"] = intval($row["id"]);
				$response["message"] = "Question successfully added.";

				// echoing JSON response
				echo json_encode($response);
			 }
		} else {
			// failed to insert row
			$response["success"] = 0;
			$response["message"] = "Oops! An error occurred.";
			
			// echoing JSON response
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