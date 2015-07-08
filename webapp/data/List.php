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

require_once($install_path . '/database.php');

/**
 * Represents List data
 *
 * General attributes are accessible as public variables.
 *
 */
class UserList {

    public $id, $makerid, $userid;

	/**
	 * User constructor
	 *
	 * @param string $email The name of the user to load
	 */

    function addPhoto($userid, $filename, $listitem) {

        global $adodb;

        $query = "INSERT INTO Photos (userid, filename, listitem) VALUES (%s,%s, %s)";

        try {
            $res = $adodb->Execute(sprintf($query,
            $adodb->qstr($userid),
            $adodb->qstr($filename),
            $adodb->qstr($listitem)
            ));

            $query = "UPDATE UserList SET complete = %s WHERE listid = %s and userid=%s";
            
            $res = $adodb->Execute(sprintf($query, 
            $adodb->qstr("2"),
            $adodb->qstr($listitem),
            $adodb->qstr($userid)
            ));

            $adodb->CacheFlush();
            
        } catch (Exception $e) {
            
            echo $e;

            echo "There was an error";
            
            return null;
        }


    }
    
    function getMakerCategories($makerid) {

        global $adodb;

        $query = "SELECT * FROM Categories WHERE approved = 1 AND makerid=?";
        
        try {
            $res = $adodb->Execute(sprintf($query,
            $adodb->qstr($makerid)
            ));

        } catch (Exception $e) {
            
            //echo $e;

            //echo "There was an error";
            
            return null;
        }
           
    }

    function addToTheList($makerid, $subject, $desc, $url) {

        global $adodb;

        $query = "INSERT INTO List (makerid, title, description, uri, approved) VALUES (%s,%s,%s,%s,1)";

        try {
            $res = $adodb->Execute(sprintf($query,
            $adodb->qstr($makerid),
            $adodb->qstr($subject),
            $adodb->qstr($desc),
            $adodb->qstr($url)
            ));

	    $adodb->CacheFlush();
            
        } catch (Exception $e) {
            
            //echo $e;

            //echo "There was an error";
            
            return null;
        }


    }

    function getUserGallery($userid) {

        global $adodb;

        $query = 'SELECT p.url,l.* From Photos p LEFT JOIN List l on (p.listitem = l.id) WHERE p.userid = ?';
        $params = array();
        $params[] = $userid;

        $res = $adodb->CacheGetAll(50, $query, $params);

        return $res;

    }

    function addToMyList($listitem, $userid){

        global $adodb;

        $query = "SELECT userid, listid from UserList WHERE userid=? and listid=?";
        $params = array();
        $params[] = $userid;
        $params[] = $listitem;
        
        $res = $adodb->CacheGetAll(5, $query, $params);

        if (count($res) == 0) {

            $query = "INSERT INTO UserList (userid, listid, complete) VALUES (%s,%s,0)";

            try {
                $res = $adodb->Execute(sprintf($query,
                $adodb->qstr($userid),
                $adodb->qstr($listitem)
                ));

                $adodb->CacheFlush();
            
            } catch (Exception $e) {
            
                //echo $e;

                // echo "There was an error";
            
                return null;
            }

        }

    }

    function getUserTopList($number, $userid, $offset = 0, $from = false, $to = false) {
        $data = UserList::getListItems($number, $userid, 0, $offset, $from, $to);

        if ($data == null) {
            return array();
        }

        return $data;
    }

    function getMakerProfile($makerid) {

        global $adodb;

        $adodb->SetFetchMode(ADODB_FETCH_ASSOC);

        $params = array();
        $query = 'SELECT * From Makers WHERE id = ?';
        $params[] = $makerid;

        try {
            $res = $adodb->CacheGetAll(500, $query, $params);

        } catch (Exception $e) {

            echo "<h2>" . $query . "</h2>";
            
            echo $e;
           
            return null;
        }

        return $res;

    }

    function getMakerList($number, $makerid, $offset = 0, $from = false, $to = false) {
        $data = UserList::getListItems($number, 0, $makerid, $offset, $from, $to);

        if ($data == null) {
            return array();
	    break;
        }

        return $data;
    }


    function getNewList($number) {
        $data = UserList::getListItems($number, 0, 0, $offset, $from, $to);

        if ($data == null) {
            return array();
        }

        return $data;
    }

    static function getSingleListItem ($listid) {

        global $adodb;

        $adodb->SetFetchMode(ADODB_FETCH_ASSOC);

        $params = array();
        $query = 'SELECT l.*,m.name, m.uri From List l LEFT JOIN Makers m on (l.makerid = m.id) WHERE l.id = ?';
        $params[] = $listid;

        try {
            $res = $adodb->CacheGetAll(15, $query, $params);

        } catch (Exception $e) {

            echo "<h2>" . $query . "</h2>";
            
            echo $e;
           
            return null;
        }

        return $res;

    }

    static function getListItems($number = 10, $userid = false, $makerid = false, $offset = 0, $from = false, $to = false) {
        global $adodb;

        $adodb->SetFetchMode(ADODB_FETCH_ASSOC);

        if ($userid) {
            $params = array();
            $query = 'SELECT ul.*, l.*, m.name, m.uri FROM UserList ul LEFT JOIN List l ON (ul.listid=l.id) LEFT JOIN Makers m ON (l.makerid = m.id) WHERE ul.complete != 2 AND ul.userid=?';
            $params[] = $userid;

        }

        if ($makerid) {
            $params = array();
            $query = 'SELECT l.*, m.name, m.uri FROM List l LEFT JOIN Makers m ON (l.makerid = m.id) WHERE l.makerid = ?';
            $params[] = $makerid;

        }

        if ($query == "") {
            $params = array();
            $query = "SELECT DISTINCT l.*, m.name, m.uri from List l LEFT JOIN Makers m ON (l.makerid = m.id)  WHERE APPROVED=1 ORDER BY RAND()";
        }
        

        $query .= ' LIMIT ? OFFSET ?';
        $params[] = (int) $number;
        $params[] = (int) $offset;

        try {
            $res = $adodb->CacheGetAll(1, $query, $params);

        } catch (Exception $e) {

            echo "<h2>" . $query . "</h2>";
            
            echo $e;
           
            return null;
        }

        return $res;
    }

    static function getListItem($listid) {

        global $adodb;

        $adodb->SetFetchMode(ADODB_FETCH_ASSOC);

            $query = 'SELECT * FROM List WHERE id = ?';
            $params[] = $listid;

        try {
            $res = $adodb->CacheGetAll(60, $query, $params);

        } catch (Exception $e) {

            echo "<h2>" . $query . "</h2>";
            
            echo $e;
           
            return null;
        }

        return $res;

    }

    static function getPhotosList($number = 10, $userid = false, $offset = 0) {
        global $adodb;

        $adodb->SetFetchMode(ADODB_FETCH_ASSOC);

            $query = 'SELECT p.*, l.* FROM Photos p LEFT JOIN List l ON (p.listitem=l.id) WHERE p.url IS NOT NULL AND p.userid=?';
            $params[] = $userid;

        try {
            $res = $adodb->CacheGetAll(15, $query, $params);

        } catch (Exception $e) {

            echo "<h2>" . $query . "</h2>";
            
            echo $e;
           
            return null;
        }

        return $res;
        
    }

    static function getCategories($number = 10) {
        global $adodb;

        $adodb->SetFetchMode(ADODB_FETCH_ASSOC);

            $query = 'SELECT * FROM Categories WHERE approved = 1 ORDER BY RAND() LIMIT ?';
            $params[] = $number;

        try {
            $res = $adodb->CacheGetAll(15, $query, $params);

        } catch (Exception $e) {

            echo "<h2>" . $query . "</h2>";
            
            echo $e;
           
            return null;
        }

        return $res;
        
    }

    static function getCategory($category) {
        global $adodb;

        $adodb->SetFetchMode(ADODB_FETCH_ASSOC);

            $query = 'SELECT * FROM Categories WHERE id=?';
            $params[] = $category;

        try {
            $res = $adodb->CacheGetAll(60, $query, $params);

        } catch (Exception $e) {

            echo "<h2>" . $query . "</h2>";
            
            echo $e;
           
            return null;
        }

        return $res;
        
    }


    static function getCategoriesList($number = 10, $category) {
        global $adodb;

        $adodb->SetFetchMode(ADODB_FETCH_ASSOC);

            $query = 'SELECT * FROM List WHERE approved = 1 AND category =? ORDER BY RAND() LIMIT ?';
            $params[] = $category;
            $params[] = $number;

        try {
            $res = $adodb->CacheGetAll(15, $query, $params);

        } catch (Exception $e) {

            echo "<h2>" . $query . "</h2>";
            
            echo $e;
           
            return null;
        }

        return $res;
        
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


    static function deleteItem ($userid, $listid, $skey) {

        global $adodb;

        $list = new UserList();

        $check = $list->getUserSession ($userid);

        if ($skey == $check['skey']) {

            $query = "DELETE from UserList where userid = %s and listid = %s";

            try {
            
                $res = $adodb->Execute(sprintf($query,
                $adodb->qstr($userid),
                $adodb->qstr($listid)
                ));

                return $listid;

            } catch (Exception $e) {

                http_response_code(400);

            }
            

        }

        else {

            http_response_code(403);
            
        }

    }

    static function addUserCategory ($categoryid, $userid) {

        global $adodb;              

        $query = "INSERT INTO UserCategories (userid, categoryid) VALUES (%s,%s)";

        try {
            $res = $adodb->Execute(sprintf($query,
            $adodb->qstr($userid),
            $adodb->qstr($categoryid)
            ));

	    $adodb->CacheFlush();

            http_response_code(200);
            
        } catch (Exception $e) {
            
            //echo $e;

            //echo "There was an error";

            http_response_code(400);
            
            return null;
        }

    }

    static function deleteUserCategory ($categoryid, $userid) {

        global $adodb;              

        $query = "DELETE FROM UserCategories WHERE userid = %s AND categoryid = %s";

        try {
            $res = $adodb->Execute(sprintf($query,
            $adodb->qstr($userid),
            $adodb->qstr($categoryid)
            ));

	    $adodb->CacheFlush();

            http_response_code(200);
            
        } catch (Exception $e) {
            
            //echo $e;

            //echo "There was an error";

            http_response_code(400);
            
            return null;
        }

    }

    static function getUserCategories ($userid) {
 
        global $adodb;              

        $query = "SELECT categoryid from UserCategories WHERE userid=?";
        $params = array();
        $params[] = $userid;
        
        $res = $adodb->CacheGetAll(5, $query, $params);

        return $res;

    }

    static function getUserSuggestions ($userid) {

        global $adodb;

        $query = "SELECT * from UserSuggestions WHERE userid=?";
        $params = array();
        $params[] = $userid;

        $res = $adodb->CacheGetAll(5, $query, $params);

        return $res;

    }


    static function addUserSuggestion ($userid, $filename, $categoryid, $description, $title) {

        global $adodb;

        $query = "INSERT INTO UserSuggestions (userid, filename, category, description, title) VALUES (%s,%s, %s, %s, %s)";

        try {
            $res = $adodb->Execute(sprintf($query,
            $adodb->qstr($userid),
            $adodb->qstr($filename),
            $adodb->qstr($categoryid),
            $adodb->qstr($description),
            $adodb->qstr($title)
            ));

            $adodb->CacheFlush();
            
        } catch (Exception $e) {
            
            echo $e;

            echo "There was an error";
            
            return null;
        }

    }
               
}
