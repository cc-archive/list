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

error_reporting(0);

require_once('database.php');



global $adodb;

$query = "SELECT * FROM Photos WHERE url IS NULL LIMIT 1";
$res = $adodb->GetAll($query);
$res = $res[0]; // just the first item in the array
print_r($res);
if ($res) {
    $foo = exec("file " . $res[filename]);   
    if (strpos($foo, "JPEG")) {
        $filename = $res['filename'];
        $url = "live/" . basename($filename) . ".jpg";
     
        rename($filename, $url);

        $id = $res['id'];
        $listid = $res['listitem'];

        echo "This file is a JPEG! " . $url . "\n";

        // Need to fix the URLs better

        //$url = basename($url);

        $url = "https://thelist.creativecommons.org//" . $url;
        $query = "UPDATE Photos SET url = %s WHERE id=(%s)";

        try {
            $res = $adodb->Execute(sprintf($query,$adodb->qstr($url), $adodb->qstr($id)));
            echo $query;

            try {
                $query = "UPDATE UserList SET complete = 2 WHERE listid=(%s)";
                $res = $adodb->Execute(sprintf($query,$adodb->qstr($listid)));
                $adodb->CacheFlush();
                echo $query;
            }

            catch (Exception $e) {
                echo "Error";
            }
        } catch (Exception $e) {
            //echo $e;
            echo "There was an error";
            return null;
        }
    }
    
    else {

        // Image is not a JPEG

        $id=$res['id'];
        $query = "DELETE from Photos WHERE id=%s";

        try {

            $q = sprintf($query,
            $adodb->qstr($id)
            );

            $res = $adodb->Execute($q);

        } catch (Exception $e) {
            
            //echo $e;

            echo "There was an error";
            return null;
        }
    }
}
?>
