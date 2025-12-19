<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>NutriBite Admin Panel</title>
    <style>
        body { font-family: sans-serif; margin: 2em; }
        h1, h2 { color: #333; }
        form { border: 1px solid #ccc; padding: 1em; border-radius: 8px; }
        input, select { width: 200px; padding: 8px; margin-bottom: 10px; }
        input[type="submit"] { background: #000; color: #fff; border: none; cursor: pointer; }
    </style>
</head>
<body>
    <h1>NutriBite Admin Panel</h1>
    <h2>Add / Update Menu Item</h2>
    <form action="update_menu.php" method="post">
        <label for="day_of_week">Day:</label><br>
        <select id="day_of_week" name="day_of_week">
            <option>Monday</option><option>Tuesday</option><option>Wednesday</option>
            <option>Thursday</option><option>Friday</option><option>Saturday</option><option>Sunday</option>
        </select><br>

        <label for="meal_type">Meal Type:</label><br>
        <select id="meal_type" name="meal_type">
            <option>Breakfast</option><option>Lunch</option><option>Dinner</option>
        </select><br>

        <label for="name">Item Name:</label><br>
        <input type="text" id="name" name="name" required><br>

        <label for="description">Description:</label><br>
        <input type="text" id="description" name="description"><br>

        <label for="tag">Tag (Veg/Vegan/Non-Veg):</label><br>
        <input type="text" id="tag" name="tag"><br>

        <label for="allergen">Allergen:</label><br>
        <input type="text" id="allergen" name="allergen"><br>

        <input type="submit" value="Add/Update Item">
    </form>
</body>
</html>