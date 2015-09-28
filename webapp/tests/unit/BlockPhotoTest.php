<?php

if (! defined('TESTING')) define ('TESTING', true);

require_once('data/List.php');
require_once('data/User.php');

class BlockPhotoTest extends \Codeception\TestCase\Test
{
    /**
     * @var \UnitTester
     */
    protected $tester;

    protected function _before()
    {
      $this->tester->runMigrations();
      $this->tester->runFixtures(['makers', 'categories', 'list',
                                  'users', 'usersessions', 'photos']);
    }

    protected function _after()
    {
    }

    // tests
    public function testBlocking()
    {
      $skeyUser1 = 'a9225230920079405293a280a508c91d';
      $skeyUser2 = '8ba048289a159a36d581ddf452f4baa0';
      $skeyUser3 = 'd097a3a21df75f96b1f6745e5dbaa5c6';
      $skeyUser4 = '2e975abd0ffa5df70fcee71bfef481bb';
      $idUser1 = 1;
      $idUser2 = 2;
      $idUser3 = 3;
      $idUser4 = 4;
      $photoidUser1 = 1;
      $photoidUser2 = 4;
      $photoidUser3 = 8;
      $photoidUser4 = 11;

      // Sanity check for session values from fixtures
      //$res = User::sessionIsValid($idUser1, $skeyUser1);
      //$this->assertTrue($res);

      // User should be able to block photos they don't own when logged in
      $res = User::blockPhoto($idUser1, $skeyUser1, $photoidUser2);
      $this->assertEquals(200, $res);
      $this->tester->seeInDatabase('UserPhotoBlocks',
                                   ['userid' => $idUser1,
                                   'photoid'=>$photoidUser2]);

      // Re-blocking should succeed without complaint
      $res = User::blockPhoto($idUser1, $skeyUser1, $photoidUser2);
      $this->assertEquals(200, $res);

      // Users shouldn't be able to block their own photos
      $res = User::blockPhoto($idUser1, $skeyUser1, $photoidUser1);
      $this->assertEquals(401, $res);

      // Users who aren't passing an skey shouldn't be able to block photos
      $res = User::blockPhoto($idUser1, '', $photoidUser2);
      $this->assertEquals(401, $res);

      // Users with bad credentials shouldn't be able to block photos
      $res = User::blockPhoto($idUser1, 'not valid', $photoidUser2);
      $this->assertEquals(401, $res);

      // No such photo? Access denied.
      $res = User::blockPhoto($idUser1, $skeyUser1, 0);
      $this->assertEquals(404, $res);

      // A user should not see a photo they have blocked when listing everyone
      $res = UserList::getPhotosList($idUser1);
      $this->assertNotEquals(null, $res);
      $photo_ids = array_map(function($x) { return $x['itemid']; }, $res);
      $this->assertFalse(in_array($photoidUser2, $photo_ids));
      // Buts hsould see photos by other users
      $this->assertTrue(in_array($photoidUser3, $photo_ids));

      // A user should not see a photo they have blocked when listing a user
      $res = UserList::getPhotosList($idUser1, 20, 1, $idUser2);
      $this->assertNotEquals(null, $res);
      $photo_ids = array_map(function($x) { return $x["itemid"]; }, $res);
      $this->assertFalse(in_array($photoidUser2, $photo_ids));
      // Buts hsould see photos by other users
      $this->assertTrue(in_array($photoidUser2 + 1, $photo_ids));
      
      // When three users block a photo...
      $res = User::blockPhoto($idUser3, $skeyUser3, $photoidUser2);
      $this->assertEquals(200, $res);
      $res = User::blockPhoto($idUser4, $skeyUser4, $photoidUser2);
      $this->assertEquals(200, $res);
      // Nobody (exept the uploader) should see it in the global list
      $res = UserList::getPhotosList($idUser1);
      $photo_ids = array_map(function($x) { return $x["itemid"]; }, $res);
      $this->assertFalse(in_array($photoidUser2, $photo_ids));
      // The uploader should see it in the global list
      $res = UserList::getPhotosList($idUser2);
      $photo_ids = array_map(function($x) { return $x["itemid"]; }, $res);
      $this->assertTrue(in_array($photoidUser2, $photo_ids));
      // Nobody (exept the uploader) should see it in that user's list
      $res = UserList::getPhotosList($idUser1, 20, 1, $idUser2);
      $photo_ids = array_map(function($x) { return $x["itemid"]; }, $res);
      $this->assertFalse(in_array($photoidUser2, $photo_ids));
      // The uploader should see it in their userid's list
      $res = UserList::getPhotosList($idUser2, 20, 1, $idUser2);
      $photo_ids = array_map(function($x) { return $x["itemid"]; }, $res);
      $this->assertTrue(in_array($photoidUser2 + 1, $photo_ids));
    }
}
