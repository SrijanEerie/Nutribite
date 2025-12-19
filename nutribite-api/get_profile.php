<?php
header("Content-Type: application/json");
require_once "db_connect.php";

$user_id = $_GET['user_id'] ?? null;
if (!$user_id) {
    echo json_encode(["status" => "error", "message" => "Missing user_id"]);
    exit;
}

$sql = "SELECT user_id, weight_kg, height_cm, gender, allergies, dislikes,
               dietary_pref, activity_level, health_goal, water_goal_liters,
               sleep_hours, medical_conditions, updated_at
        FROM user_profiles WHERE user_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(["status" => "success", "profile" => $result->fetch_assoc()]);
} else {
    echo json_encode(["status" => "error", "message" => "Profile not found"]);
}
?>
