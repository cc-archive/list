## The List API

* Updated for version 4.0

### Overview

The List API is implemented by The List App, and is subject to change based on feedback, new features and more.

* There is no API key required to do anything. This will eventually change.

* There's likely some horrible security bug that'll let you delete
  everyone's stuff. Please don't, but please do tell us.

* All of the API calls are made using HTTP GET and POST, and our
  examples are all based on curl.

* The login mechanism for the app is CCID. We pass things directly to
  the CCID server. If you need a CCID, grab one at
  http://login.creativecommons.org

### API calls

POST /api/users/login

* Takes two parameters, `username` and `password`

* Returns JSON, a user object including a unique user ID which is used
  by many of the other functions, and a session key which is used for
  deleting things.

GET /api/category

* Returns a list of categories.

* Optional ID returns items from a particular category.

GET /api/items

* Returns 20 items from the master list

* Optional ID returns detailed information on an item

POST /api/photos/:userid/:id

* Submit a photo to The List in response to a List Item (:id)

POST /api/suggestions/:userid

* Submit a photo 'suggestion' to The List (never shown to public)

GET /api/makers/:id

* Returns the profile of a particular maker. Depreciated.



