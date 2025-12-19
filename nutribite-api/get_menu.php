<?php
header('Content-Type: application/json');
include 'db_connect.php';

$sql = "SELECT day_of_week, meal_type, name, description, tag, allergen 
        FROM menu_items 
        ORDER BY FIELD(day_of_week, 'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), meal_type";

$result = $conn->query($sql);

if (!$result) {
    echo json_encode(["status" => "error", "message" => "Query failed: " . $conn->error]);
    exit;
}

$menu = [];
while ($row = $result->fetch_assoc()) {
    $day = $row['day_of_week'];
    $meal = $row['meal_type'];
    $menu[$day][$meal][] = [
        "name" => $row['name'],
        "description" => $row['description'],
        "tag" => $row['tag'],
        "allergen" => $row['allergen']
    ];
}

if (empty($menu)) {
    echo json_encode(["status" => "error", "message" => "No menu found"]);
} else {
    echo json_encode(["status" => "success", "menu" => $menu]);
}

$conn->close();
?>
