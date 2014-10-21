<?php

/* The List powered by Creative Commons

   Copyright (C) 2014 Creative Commons

   based on:
   
   phpCAS Gateway Example

   https://github.com/Jasig/phpCAS/blob/master/docs/examples/example_gateway.php

   Copyright (c) Joachim Fritschi <jfritschi@freenet.de>
   Copyright (c) Adam Franco <afranco@middlebury.edu>
   a file under http://www.apache.org/licenses/LICENSE-2.0

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

require 'config.php';
require 'vendor/jasig/phpcas/CAS.php';

// Enable debugging
phpCAS::setDebug();

// Initialize phpCAS
phpCAS::client(CAS_VERSION_2_0, $cas_host, $cas_port, $cas_context);

// For production use set the CA certificate that is the issuer of the cert
// on the CAS server and uncomment the line below
// phpCAS::setCasServerCACert($cas_server_ca_cert_path);
// For quick testing you can disable SSL validation of the CAS server.
// THIS SETTING IS NOT RECOMMENDED FOR PRODUCTION.
// VALIDATING THE CAS SERVER IS CRUCIAL TO THE SECURITY OF THE CAS PROTOCOL!

phpCAS::setNoCasServerValidation();

// check CAS authentication

if (isset($_REQUEST['logout'])) {
phpCAS::logout();
}

if (isset($_REQUEST['login'])) {
phpCAS::forceAuthentication();
}

$auth = phpCAS::checkAuthentication();
?>
