/* Image Loader
    Copyright (C) 2015  Creative Commons Corporation

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    */

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
