<?php

  /* The List powered by Creative Commons

     Copyright (C) 2014 Creative Commons

     based on:

     GNU FM -- a free network service for sharing your music listening habits

     Copyright (C) 2009 Free Software Foundation, Inc

     This program is free software: you can redistribute it and/or modify
     it under the terms of the GNU Affero General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     (at your option) any later version.

     This program is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU Affero General Public License for more details.

     You should have received a copy of the GNU Affero General Public License
     along with this program.  If not, see <http://www.gnu.org/licenses/>.

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

        global $adodb;
        
        $filename = $tmp;

        $query = "INSERT INTO PhotosTBA (userid, filename) VALUES (%s,%s)";

        try {
	  $res = $adodb->Execute(sprintf($query,
					 $userid,
					 $adodb->qstr($filename)
					 ));

	  $adodb->CacheFlush();

	  header('Location: /my-list.php');

            
        } catch (Exception $e) {
            
	  //echo $e;

	  echo "There was an error";
            
	  return null;
        }

         }
       else
         {
           echo $_FILES["list-image"]["error"];
         }
      }
  
 }

?>
