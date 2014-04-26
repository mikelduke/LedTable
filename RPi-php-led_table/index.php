<html>
	<head>
	<title>Led Table Controller</title>
	</head>
	<body>
	<h1>Led Table Controller</h1>
	<hr>
	<br>
<?php
	/**
	 * Led Table Web Interface
	 * 
	 */
	
	$host = "127.0.0.1";
	$username = "user";
	$password = "password";
	$dbname = "led_table";
	
	$con = mysqli_connect($host,$username,$password,$dbname);
	if (mysqli_connect_errno()) {
		echo "Failed to connect to db: " . mysqli_connect_errno();
		return;
	}
	
	$modes = [
		0 =>	"Off",
		1 =>    "Demo",
		2 => 	"Demo2",
		3 =>  	"Color Fade",
		4 =>	"RPi Program",
		5 => 	"Set Color",
		6 =>	"Random Fade",
		7 =>    "Test Image",
		8 =>	"Test Animation"
	];
	
	if (isset($_GET["mode"]) && $_GET["mode"] != "") {
		//mode set
		$selected_mode = $_GET["mode"];
		echo ("Selected mode: $selected_mode<br>\n");
		
		//these modes take no extra parameters
		if ($selected_mode == 0 || $selected_mode == 1 || $selected_mode == 2 || $selected_mode == 3 || $selected_mode == 6)
			mysqli_query($con, "insert into selection (selection_mode, selection_date) values ($selected_mode, now());");
		else if ($selected_mode == 5) {
			if ((isset($_GET["r"]) && isset($_GET["g"]) && isset($_GET["b"])) || (isset($_POST["r"]) && isset($_POST["g"]) && isset($_POST["b"]))) {
				//if color is set, add color param
				if (isset($_GET["r"])) {
					$r = $_GET["r"];
					$g = $_GET["g"];
					$b = $_GET["b"];
				}
				else {
					$r = $_POST["r"];
					$g = $_POST["g"];
					$b = $_POST["b"];
				}
				
				echo "Selected color: $r, $g, $b<br>\n";
				
				mysqli_query($con, "insert into selection (selection_mode, selection_date, selection_parm1, selection_parm2, selection_parm3) values ($selected_mode, now(), '$r', '$g', '$b');");
			}
			else { //show color selection
				echo "<form action=\"" . $_SERVER['REQUEST_URI'] . "\" method=\"post\">
				Red   (0-255): <input type=\"number\" name=\"r\" min=\"0\" max=\"255\"><br>
				Green (0-255): <input type=\"number\" name=\"g\" min=\"0\" max=\"255\"><br>
				Blue  (0-255): <input type=\"number\" name=\"b\" min=\"0\" max=\"255\"><br>
				<input type=\"submit\">
				</form>";
			}
		}
		else if ($selected_mode == 7) {
			mysqli_query($con, "insert into selection (selection_mode, selection_date, selection_parm1) values ($selected_mode, now(), '/home/mikel/mdp3/rpi/java/LedTable/test.bmp');"); 
		}
		else if ($selected_mode == 8) {
			mysqli_query($con, "insert into selection (selection_mode, selection_date, selection_parm1, selection_parm2) values ($selected_mode, now(), '/home/mikel/mdp3/rpi/java/LedTable/testImages/', 100);");
		}
		echo "<hr>";
	}
	
	//show mode selection links
	echo("<br>\n<br>\n");
	for ($i = 0; $i < count($modes); $i++)
		echo ("<a href='?mode=$i'>$modes[$i]</a><br>\n");
?>

	</body>
</html>