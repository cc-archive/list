<?php

  /* The List powered by Creative Commons

     Copyright (C) 2014, 2015 Creative Commons Corporation

     based on:

   GNU FM -- a free network service for sharing your music listening habits
   GNU Archie Framework -- a web application framework derived from GNU FM

   Copyright (C) 2009, 2015 Free Software Foundation, Inc

   This program is free software: you can redistribute it and/or modify
   it under the terms of either the GNU Affero General Public License or
   the GNU General Public License as published by the
   Free Software Foundation, either version 3 of the Licenses, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.  

   If not, see <http://www.gnu.org/licenses/>.

  */

require_once('database.php');
require 'data/User.php';
require 'data/List.php';

require_once 'templating.php';

if ($auth) {

  if (isset($_FILES['list-image'])) {

    $path = dirname($_SERVER['PHP_SELF']). '/list-uploads/foo.gif';

    $tmp = "" . tempnam("./list-uploads/", "process-me-" . $userid . "-");

       if (move_uploaded_file($_FILES["list-image"]["tmp_name"], $tmp))
         {

             $list = new UserList;       
             $filename = $tmp;
             $id = $_POST['id'];

             $result = $list->addPhoto($userid, $filename, $id);

             header('Location: my-list.php?msg=1');

         }
       else
         {
           echo "<h1>Error: " . $_FILES["list-image"]["error"] . "</h1>";
	   
	   echo "Make sure your server can support a file this large as a PHP upload.";


         }
  } else {

      echo "File naming error! Please try again. This is a bug we're aware of";
      

  }

} else {

    echo "Authentication error!";
  
}

?>
