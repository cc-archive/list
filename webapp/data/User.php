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

require_once(dirname(__DIR__) . '/database.php');

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

  static function getUserSession ($userid, $create=true) {
    global $adodb;

    $query = "SELECT skey, userid FROM UserSessions WHERE userid = " . $adodb->qstr($userid);
    $adodb->SetFetchMode(ADODB_FETCH_ASSOC);
    $row = $adodb->CacheGetRow(1, $query);
    
    if (count($row) == 0) {
      if ($create) {
        $query = "INSERT INTO UserSessions (userid, skey, session_start) VALUES (%s,%s, %s)";

        $key = md5(uniqid(rand(), true));

        $res = $adodb->Execute(sprintf($query,
                                       $adodb->qstr($userid),
                                       $adodb->qstr($key),
                                       $adodb->qstr(date("Y-m-d H:i:s"))
                                       ));

        $foo = array("skey" => $key, "userid" => $userid);
      } else {
        $foo = [];
      }
    } else {
        
      $foo = array("skey" => $row['skey'], "userid" => $row['userid']);

    }

    return $foo;        

  }

  /*function getUserGallery($userid) {

        global $adodb;

        $query = 'SELECT p.url,l.* From Photos p LEFT JOIN List l on (p.listitem = l.id) WHERE p.userid = ?';
        $params = array();
        $params[] = $userid;

        $res = $adodb->CacheGetAll(50, $query, $params);

        return $res;

        }*/

    static function sessionIsValid ($userid, $sessionid) {
      try {
        $session = self::getUserSession($userid, false);
        $valid = array_key_exists('skey', $session)
          && $session['skey'] == $sessionid;
      } catch (Exception $e) {
        $valid = false;
      }
      return $valid;
    }

    static function blockPhoto($userid, $sessionid, $photoid) {
      global $adodb;
      $result_code = 401;
      if(self::sessionIsValid ($userid, $sessionid)) {
        $photo_exists_and_user_doesnt_own_it = false;
        $query = 'SELECT * From Photos WHERE id = ?';
        $params = [$photoid];
        try {
          $adodb->SetFetchMode(ADODB_FETCH_ASSOC);
          $res = $adodb->GetRow($query, $params);
          if (!$res) {
            $result_code = 404;
          } elseif ($res['userid'] != $userid) {
            $photo_exists_and_user_doesnt_own_it = true;
          }
        } catch (Exception $e) {
          //TODO: This does swallow worse errors. We should check for them
          $result_code = 401;
        }
        // If the photo exists and isn't owned by the user trying to block it
        if($photo_exists_and_user_doesnt_own_it) {
          try {
            $query =
              "INSERT INTO UserPhotoBlocks (userid, photoid) VALUES (?, ?)";
            $params = [$userid, $photoid];
            $adodb->Execute($query, $params);
            //$adodb->CacheFlush();
            $result_code = 200;
          } catch (Exception $e) {
            $result_code = 500;
          }
        }
      }
      return $result_code;
    }

    static function likePhoto($userid, $photoid) {
        global $adodb;
        $result_code = 200;
        $result = ['success' => false];
        // Check that the photo exists and that the user doesn't own it
        try {
            $query = 'SELECT * FROM Photos WHERE id = ?';
            $params = [$photoid];
            $adodb->SetFetchMode(ADODB_FETCH_ASSOC);
            $res = $adodb->GetRow($query, $params);
            if ($res) {
                // If the user owns the photo, they can't like it
                $result_code = ($res['userid'] != $userid) ? 200 : 403;
            } else {
                $result_code = 404;
            }
        } catch (Exception $e) {
            $result_code = 500;
        }
        if ($result_code == 200) {
            // Get the user's current like state for the photo
            try {
                $query = 'SELECT * FROM UserPhotoLikes WHERE userid = ?
                              AND photoid = ?';
                $params = [$userid, $photoid];
                $adodb->SetFetchMode(ADODB_FETCH_ASSOC);
                $res = $adodb->GetOne($query, $params);
                if ($res) {
                    $result['likes'] = false;
                } else {
                    $result['likes'] = true;
                }
            } catch (Exception $e) {
                $result_code = 500;
            }
            if ($result['likes']) {
                // The user has added a 'like', so insert it
                try {
                    $query = 'INSERT INTO UserPhotoLikes(userid, photoid)
                                  VALUES(?, ?)';
                    $params = [$userid, $photoid];
                    $adodb->Execute($query, $params);
                    $result['success'] = true;
                } catch (Exception $e) {
                    $result_code = 500;
                }
            } else {
                // The user has removed a 'like', so delete it
                try {
                    $query =
                    'DELETE FROM UserPhotoLikes WHERE userid = ?
                         AND photoid = ?';
                    $params = [$userid, $photoid];
                    $adodb->Execute($query, $params);
                    $result['success'] = true;
                } catch (Exception $e) {
                    $result_code = 500;
                }
            }
        }
        return [$result_code, $result];
    }
}
