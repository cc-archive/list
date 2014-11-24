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

error_reporting(0);

require_once('database.php');



global $adodb;

$query = "SELECT * FROM PhotosTBA LIMIT 1";

try {
  
  $res = $adodb->CacheGetAll(15, $query);

  $res = $res[0]; // just the first item in the array

  print_r($res);

  $foo = exec("file " . $res[filename]);

  if (strpos($foo, "JPEG")) {

    echo "This file is a JPEG!";

    $query = "UPDATE Photos SET (userid, url) VALUES (%s, %s)";

    try {
      $res = $adodb->Execute(sprintf($query,
				     $makerid,
				     $adodb->qstr($subject),
				     $adodb->qstr($desc),
				     $adodb->qstr($url)
				     ));

      $adodb->CacheFlush();

      header('Location: /add.php');

            
    } catch (Exception $e) {
            
      //echo $e;

      echo "There was an error";
            
      return null;
    }

  }
           
} catch (Exception $e) {
            
  //echo $e;

  echo "There was an error";
            
  return null;
  }


?>
