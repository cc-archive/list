<?php

if (! defined('TESTING')) define ('TESTING', true);

require_once('data/List.php');
require_once('data/User.php');

class UserPhotosLikeTest extends \Codeception\TestCase\Test
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
    public function testLiking()
    {
        $idUser1 = 1;
        $idUser2 = 2;
        $idUser3 = 3;
        $idUser4 = 4;
        $photoidUser1 = 1;
        $photoidUser2 = 4;
        $photoidUser3 = 8;
        $photoidUser4 = 11;

        // User should be able to like photos they don't own
        list($result_code, $result) = User::likePhoto($idUser1, $photoidUser2);
        $this->assertEquals(200, $result_code);
        $this->assertTrue($result['success'] == true);
        $this->assertTrue($result['likes'] == true);
        $this->tester->seeInDatabase('UserPhotoLikes',
                                     ['userid' => $idUser1,
                                      'photoid'=>$photoidUser2]);

        // User should be able to unlike photos they don't own
        list($result_code, $result) = User::likePhoto($idUser1, $photoidUser2);
        $this->assertEquals(200, $result_code);
        $this->assertTrue($result['success'] == true);
        $this->assertTrue($result['likes'] == false);
        $this->tester->dontSeeInDatabase('UserPhotoLikes',
                                         ['userid' => $idUser1,
                                          'photoid'=>$photoidUser2]);

        // User should not be be able to like photos they own
        list($result_code, $result) = User::likePhoto($idUser1, $photoidUser1);
        $this->assertEquals(403, $result_code);
        $this->assertTrue($result['success'] == false);
        $this->tester->dontSeeInDatabase('UserPhotoLikes',
                                         ['userid' => $idUser1,
                                          'photoid'=>$photoidUser1]);

        // User should not be be able to like photos that don't exist
        list($result_code, $result) = User::likePhoto($idUser1, 0);
        $this->assertEquals(404, $result_code);
        $this->assertTrue($result['success'] == false);
        $this->tester->dontSeeInDatabase('UserPhotoLikes',
                                         ['userid' => $idUser1,
                                          'photoid'=>0]);
    }
}
