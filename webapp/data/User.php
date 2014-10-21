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
 * Represents User data
 *
 * General attributes are accessible as public variables.
 *
 */
class User {

    public $email, $id;

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
            }
        }
}
