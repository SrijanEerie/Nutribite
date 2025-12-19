<?php
header("Content-Type: application/json");
require_once "db_connect.php";

$data = json_decode(file_get_contents("php://input"), true);
$email = $data['email'] ?? '';
$password = $data['password'] ?? '';
$role = strtolower($data['role'] ?? 'user'); // ✅ Normalize role

if (empty($email) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "Email and password required"]);
    exit;
}

$stmt = $conn->prepare("SELECT id, name, email, password_hash, role FROM users WHERE email=? AND LOWER(role)=?");
$stmt->bind_param("ss", $email, $role);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 1) {
    $user = $result->fetch_assoc();

    if (password_verify($password, $user['password_hash'])) {

        if (strtolower($user['role']) === 'admin') { // ✅ Admin detected
            echo json_encode([
                "status" => "admin",
                "message" => "Admin login successful",
                "redirect" => "https://php-6c87f.wasmer.app/admin.php",
                "user" => [
                    "id" => $user['id'],
                    "name" => $user['name'],
                    "email" => $user['email'],
                    "role" => $user['role']
                ]
            ]);
        } else { // ✅ Normal user
            echo json_encode([
                "status" => "success",
                "message" => "User login successful",
                "user" => [
                    "id" => $user['id'],
                    "name" => $user['name'],
                    "email" => $user['email'],
                    "role" => $user['role']
                ]
            ]);
        }

    } else {
        echo json_encode(["status" => "error", "message" => "Invalid password"]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "User not found"]);
}
?>
