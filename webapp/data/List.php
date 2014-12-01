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

    function getUserTopList($number, $userid, $offset = 0, $from = false, $to = false) {
        $data = UserList::getListItems($number, $userid, 0, $offset, $from, $to);

        if ($data == null) {
            return array();
        }

        return $data;
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

    static function getListItems($number = 10, $userid = false, $makerid = false, $offset = 0, $from = false, $to = false) {
        global $adodb;

        $adodb->SetFetchMode(ADODB_FETCH_ASSOC);

        if ($userid) {
            $params = array();
            $query = 'SELECT ul.*, l.* FROM UserList ul LEFT JOIN List l ON (ul.listid=l.id) WHERE ul.complete IS NULL AND ul.userid=?';
            $params[] = $userid;

        }

        if ($makerid) {
            $params = array();
            $query = 'SELECT l.*, m.name, m.uri FROM List l LEFT JOIN Makers m ON (l.makerid = m.id) WHERE l.makerid = ?';
            $params[] = $makerid;

        }

        if ($query == "") {
            $params = array();
            $query = "SELECT DISTINCT * from List WHERE APPROVED=1 ORDER BY RAND()";
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
        
}
