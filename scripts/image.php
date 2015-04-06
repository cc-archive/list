<?php 

$size = $_GET['foo'] . "/";

$img = $_GET['img'];

$path = $size . $img;

if (file_exists($path)) {
   header("Content-type: image/jpeg");
   readfile($path);
   }
else {
   header("Location: https://thelist.creativecommons.org/app/shark.png;");
   http_response_code(404);
}
?>