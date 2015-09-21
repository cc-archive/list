## The List API

* Updated for version 4.0

### Overview

The List API is implemented by The List App, and is subject to change based on feedback, new features and more.

* There is no API key required to do anything. This will eventually change.

* There's likely some horrible security bug that'll let you delete
  everyone's stuff. Please don't, but please do tell us.

* All of the API calls are made using HTTP GET and POST, and our
  examples are all based on curl.

* The login mechanism for the app is CCID (our CAS system). We pass things
  directly to the CCID server. If you need a CCID, grab one at
  http://login.creativecommons.org

### API calls

#### POST /api/users/login

* Takes two parameters, `username` and `password` 

* Returns JSON, a user object including a unique user ID which is used
  by many of the other functions, and a session key which is used for
  deleting things.

* Example:
  
  `curl --include --data username=cciduser@example.com&password=ccidpassword" https://thelist.creativecommons.org/api/users/login`

#### GET /api/category

* Returns a list of categories.

* Optional ID returns items from a particular category.

* Example:  
  
  `curl https://thelist.creativecommons.org/api/category`

#### GET /api/items

* Returns 20 items from the master list

* Examples:  
  
  `curl https://thelist.creativecommons.org/api/items`

#### GET /api/items/:itemid

* Returns detailed information on an item

* Examples:  
  
  `curl https://thelist.creativecommons.org/api/items/135`


#### GET /api/photos

* Returns the most recent photos.

* Has optional parameters:
  * count - The number of items to return. 20 if absent, max 200 each time.
  * from - The first item to return. Most recent if absent.
  * userid - The userid whose photos the api should return. Everyone if absent.

* Example:  
  
  `curl https://thelist.creativecommons.org/api/photos`


#### POST /api/photos/:userid/:itemid

* Submit a photo to The List in response to a List Item (:itemid)

* Example:  
  
  ``curl --include --data "filedata=`base64 --wrap=0 ~/small.jpg`" https://thelist.creativecommons.org/api/photos/1/135``

#### POST /api/suggestions/:userid

* Submit a photo 'suggestion' to The List (never shown to public)

* Example:  
  
  ``curl --include --data "categoryid=&175&description=A%20new%20trinket%20image.&title=New%20Trinket&filedata=`base64 --wrap=0 ~/small.jpg`" https://thelist.creativecommons.org/api/suggestions/1``

#### GET /api/makers/:makerid

* Returns the profile of a particular maker. Depreciated.

* Example:  
  
  `curl https://thelist.creativecommons.org/api/makers/1`


#### GET /api/version

* Returns version information for the system.

* Example:  
  
  `curl https://thelist.creativecommons.org/api/version`
