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

    function addToMyList($listitem, $userid){

        global $adodb;

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
            $res = $adodb->CacheGetAll(15, $query, $params);

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

    

        
}
