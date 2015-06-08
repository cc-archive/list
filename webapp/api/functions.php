<?php

function generateGUID() {
	mt_srand((double)microtime()*10000);//optional for php 4.2.0 and up.
	$charid = strtoupper(md5(uniqid(rand(), true)));

	return strtolower($charid);
}

?>
