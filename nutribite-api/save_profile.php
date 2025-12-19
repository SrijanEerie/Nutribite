<?php
header("Content-Type: application/json");
require_once "db_connect.php";

$data = json_decode(file_get_contents("php://input"), true);

if (!$data || !isset($data['user_id'])) {
    echo json_encode(["status" => "error", "message" => "Invalid input"]);
    exit;
}

// Helper: convert empty or non-numeric values to NULL
function to_nullable_float($val) {
    if (!isset($val) || $val === "" || !is_numeric($val)) return null;
    return (float)$val;
}

$user_id = (int)$data['user_id'];
$weight_kg = to_nullable_float($data['weight_kg'] ?? null);
$height_cm = to_nullable_float($data['height_cm'] ?? null);
$gender = $data['gender'] ?? null;
$allergies = $data['allergies'] ?? null;
$dislikes = $data['dislikes'] ?? null;
$dietary_pref = $data['dietary_pref'] ?? null;
$activity_level = $data['activity_level'] ?? null;
$health_goal = $data['health_goal'] ?? null;
$water_goal_liters = to_nullable_float($data['water_goal_liters'] ?? null);
$sleep_hours = $data['sleep_hours'] ?? null;
$medical_conditions = $data['medical_conditions'] ?? null;

// INSERT ... ON DUPLICATE KEY UPDATE
$sql = "INSERT INTO user_profiles 
(user_id, weight_kg, height_cm, gender, allergies, dislikes, dietary_pref, activity_level, health_goal, water_goal_liters, sleep_hours, medical_conditions, updated_at)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
ON DUPLICATE KEY UPDATE 
weight_kg=VALUES(weight_kg),
height_cm=VALUES(height_cm),
gender=VALUES(gender),
allergies=VALUES(allergies),
dislikes=VALUES(dislikes),
dietary_pref=VALUES(dietary_pref),
activity_level=VALUES(activity_level),
health_goal=VALUES(health_goal),
water_goal_liters=VALUES(water_goal_liters),
sleep_hours=VALUES(sleep_hours),
medical_conditions=VALUES(medical_conditions),
updated_at=NOW()";

$stmt = $conn->prepare($sql);
if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Prepare failed: " . $conn->error]);
    exit;
}

/*
 types order (12 params):
 1: user_id -> i
 2: weight_kg -> d (nullable)
 3: height_cm -> d (nullable)
 4: gender -> s
 5: allergies -> s
 6: dislikes -> s
 7: dietary_pref -> s
 8: activity_level -> s
 9: health_goal -> s
 10: water_goal_liters -> d (nullable)
 11: sleep_hours -> s
 12: medical_conditions -> s
*/
$types = "iddssssssdss";

if (!$stmt->bind_param(
    $types,
    $user_id,
    $weight_kg,
    $height_cm,
    $gender,
    $allergies,
    $dislikes,
    $dietary_pref,
    $activity_level,
    $health_goal,
    $water_goal_liters,
    $sleep_hours,
    $medical_conditions
)) {
    echo json_encode(["status" => "error", "message" => "Bind failed: " . $stmt->error]);
    exit;
}

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Profile saved successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => $stmt->error]);
}
?>
