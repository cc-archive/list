<?php

/* The List powered by Creative Commons

   Copyright (C) 2014 Creative Commons Corporation

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
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.  

   If not, see <http://www.gnu.org/licenses/>.
*/

require_once($install_path . '/database.php');

/**
 * Represents User data
 *
 * General attributes are accessible as public variables.
 *
 */
class User {

  public $email, $id, $makerid;

  /**
   * User constructor
   *
   * @param string $email The name of the user to load
   */
  function __construct($email) {

    global $adodb;
    $query = 'SELECT * FROM Users WHERE lower(email) = lower(' . $adodb->qstr($email) . ') LIMIT 1';

    $adodb->SetFetchMode(ADODB_FETCH_ASSOC);
    $row = $adodb->CacheGetRow(1, $query);

    if (!$row) {

      $q = sprintf('INSERT INTO Users (email) VALUES (%s)'
                   , $adodb->qstr($email));

      try {
        $res = $adodb->Execute($q);
      } catch (Exception $e) {
        echo "There was an error";
        header('Content-Type: text/plain');
        exit;
      }

      $query = 'SELECT * FROM Users WHERE lower(email) = lower(' . $adodb->qstr($this->email) . ') LIMIT 1';
      $adodb->CacheFlush($query);

      return 1;
    }

    if (is_array($row)) {

      $this->email            = $row['email'];
      $this->id               = $row['id'];
      $this->makerid          = $row['makerid'];
    }
  }



  static function convertAnonymousUser($guid, $email) {
    global $adodb;

    $query = "UPDATE Users SET email = %s WHERE email=%s";
    $res = $adodb->Execute(sprintf($query, $adodb->qstr($email), $adodb->qstr($guid)));
    
    return $res;
  }


  static function mergeUserInfo($userid, $anonymous_id) {
    global $adodb; 
    
    $query = "UPDATE UserList SET userid = %s WHERE userid=%s";
    
    $res = $adodb->Execute(sprintf($query, $adodb->qstr($userid), $adodb->qstr($anonymous_id)));

    $query = "UPDATE UserCategories SET userid = %s WHERE userid=%s";
    $res = $adodb->Execute(sprintf($query, $adodb->qstr($userid), $adodb->qstr($anonymous_id)));

    $query = "DELETE c1 FROM UserCategories c1, UserCategories c2 WHERE c1.id < c2.id AND c1.categoryid = c2.categoryid";
    $res = $adodb->Execute($query);

    $query = "DELETE FROM Users WHERE id=%s";
    $res = $adodb->Execute(sprintf($query, $adodb->qstr($anonymous_id)));

    return $res;
  }

  static function getUserInfo($email) {

    global $adodb;

    $query = 'SELECT * FROM Users WHERE lower(email) = lower(' . $adodb->qstr($email) . ') LIMIT 1';

    $adodb->SetFetchMode(ADODB_FETCH_ASSOC);
    $row = $adodb->GetRow($query);

    return $row;
  }

  static function makeUser ($email) {

    global $adodb;


    $q = sprintf('INSERT INTO Users (email) VALUES (%s)'
                 , $adodb->qstr(strtolower($email)));

    try {
      $res = $adodb->Execute($q);
    } catch (Exception $e) {
      http_response_code(500);                

    }

  }

  static function getUserSession ($userid) {
    global $adodb;

    $query = "SELECT skey, userid FROM UserSessions WHERE userid = " . $adodb->qstr($userid);
    $adodb->SetFetchMode(ADODB_FETCH_ASSOC);
    $row = $adodb->CacheGetRow(1, $query);

    if (count($row) == 0) {

      $query = "INSERT INTO UserSessions (userid, skey, session_start) VALUES (%s,%s, %s)";

      $key = md5(uniqid(rand(), true));

      $res = $adodb->Execute(sprintf($query,
                                     $adodb->qstr($userid),
                                     $adodb->qstr($key),
                                     $adodb->qstr(date("Y-m-d H:i:s"))
                                     ));          

      $foo = array("skey" => $key, "userid" => $userid);
    } else {

      $foo = array("skey" => $row['skey'], "userid" => $row['userid']);

    }

    return $foo;        

  }


  

    function getUserGallery($userid) {

        global $adodb;

        $query = 'SELECT p.url,l.* From Photos p LEFT JOIN List l on (p.listitem = l.id) WHERE p.userid = ?';
        $params = array();
        $params[] = $userid;

        $res = $adodb->CacheGetAll(50, $query, $params);

        return $res;

    }

}
