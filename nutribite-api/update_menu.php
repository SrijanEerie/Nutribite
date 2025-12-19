<?php
include 'db_connect.php';

$day_of_week = $_POST['day_of_week'] ?? '';
$meal_type = $_POST['meal_type'] ?? '';
$name = $_POST['name'] ?? '';
$description = $_POST['description'] ?? '';
$tag = $_POST['tag'] ?? '';
$allergen = $_POST['allergen'] ?? '';

if(empty($day_of_week) || empty($meal_type) || empty($name)) {
    die("Required fields are missing.");
}

// Check if item already exists for that day and meal
$stmt = $conn->prepare("SELECT id FROM menu_items WHERE day_of_week = ? AND meal_type = ? AND name = ?");
$stmt->bind_param("sss", $day_of_week, $meal_type, $name);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    // Update existing item
    $id = $result->fetch_assoc()['id'];
    $update_stmt = $conn->prepare("UPDATE menu_items SET description = ?, tag = ?, allergen = ? WHERE id = ?");
    $update_stmt->bind_param("sssi", $description, $tag, $allergen, $id);
    $update_stmt->execute();
    $update_stmt->close();
    echo "Item updated successfully!";
} else {
    // Insert new item
    $insert_stmt = $conn->prepare("INSERT INTO menu_items (day_of_week, meal_type, name, description, tag, allergen) VALUES (?, ?, ?, ?, ?, ?)");
    $insert_stmt->bind_param("ssssss", $day_of_week, $meal_type, $name, $description, $tag, $allergen);
    $insert_stmt->execute();
    $insert_stmt->close();
    echo "Item added successfully!";
}

$stmt->close();
$conn->close();

echo '<br><a href="admin.php">Back to Admin Panel</a>';
?>