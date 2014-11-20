var express = require('express');
var _ = require('underscore');
var db = require('../db');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {
  res.render('index', { title: 'The List Test API' });
});

//GET All List Items
router.get('/api/item', function(req, res) {
	var response = {
		status: "ok",
		content: db.item
	};
	res.status(200).send(response);
});

//GET List Items by ID
router.get('/api/item/:id', function(req,res) {
	var id = req.params.id;
	var item = _.find(db.item, function(it){ return id == it.id; });

	if (item) {
		var response = {
			status: "ok",
			content: item
		};
		res.status(200).send(response);
	}
	else {
		var response = {
			status:"error",
			content:"not found"
		}
		res.status(404).send(response);
	}
});

//GET Multiple List Items at once
router.get('api/items', function(req,res) {
	var itemIDs = req.body.items;
	// var itemsArray = [];

	if(items.constructor === Array) {
		var itemsArray = _.filter(db.item, function(item) {
			return _.contains(itemIDs, item.id);
		});

		if(!(itemsArray == null)) {
			var response = {
				status: "ok",
				content: itemsArray
			}
			res.status(200).send(response);
		} else {
			var response = {
				status: "error",
				content: "No items found"
			}
			res.status(404).send(response);
		}
	}
	else {
		var response = {
			status: "error",
			content: "Incorrect data type. Array required."
		}
		res.status(500).send(response);
	}

});


//GET ListMaker for Single Item
//TODO: Add Listmaker Name to regular list item entry instead
router.get('/api/item/:id/maker', function(req,res) {
	var makerItem = {};
	//Store Item Id
	var id = req.params.id;

	//Get item that matches that Id
	var item = _.find(db.item, function(it){ return id == it.id; });
	//Store Item name and id in makerItem object
	makerItem.name = item.name;
	makerItem.id = item.id;
	//Get Maker Name
	var maker = _.find(db.user, function(us){ 
			return item.userId == us.id; 
	});
	makerItem.user = maker.name;

	var response = {
		status: "ok",
		content: makerItem
	}
	res.status(200).send(response);
});

//POST Add User
router.post('/api/user', function(req,res) {
	name = req.body.name;
	email = req.body.email;
	password = req.body.password;
	//category = req.body.category;
	//listItems = req.body.listItems;

	function validateEmail(testedEmail) { 
		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		if (re.test(testedEmail)){
			return re.test(testedEmail);
		}
		else {
			return false;
		}
	}

	function validateName(testedName) {
		//require a full name
		var re = /^[a-z][- a-z]*[- ][- a-z]*[a-z]$/i;
		if (re.test(testedName)){
			return re.test(testedName);
		}
		else {
			return false;
		}
	}

	//input validation: return error if parameters are missing or invalid
	var errors = [];
	if (validateName(name) == false) {
		var response = {
			status:"error",
			content:"Name is invalid"
		}
		errors.push(response);
	}
	if (validateEmail(email) == false) {
		var response = {
			status: "error",
			content: "Email is invalid"
		}
		errors.push(response);
	}
	if(!password || password.length < 7) {
		var response = {
			status: "error",
			content: "Password is invalid"
		}
		errors.push(response);
	}

	//no errors
	if(!errors.length) {
		//Simulate new user ID with random number
		var randomId = _.random(0, 100);

		var user = { 
			id: randomId,
			name: name,
			email: email,
			password: password
		};
		var response = {
			status: "ok",
			content: user
		}
		res.status(200).send(response);
	}
	else {
		var response = {
			status:"bad request",
			content: errors
		}
		res.status(400).send(response);
	}
});

//GET all users
router.get('/api/user', function(req,res) {
	var response = {
		status: "ok",
		content: db.user
	};
	res.status(200).send(response);
});

//GET User by ID
router.get('/api/user/:id', function(req,res) {
	var id = req.params.id;
	var user = _.find(db.user, function(it){ return id == it.id; });

	if (user) {
		var response = {
			status: "ok",
			content: user
		};
		res.status(200).send(response);
	}
	else {
		var response = {
			status:"error",
			content:"not found"
		}
		res.status(404).send(response);
	}
});

//PUT Update User Info
router.put('/api/user/:id', function(req,res) {
	var id = req.params.id;
	var user = _.find(db.user, function(it){ return id == it.id; });
	var reqEmail = req.body.email;
	var reqName = req.body.name;
	var reqCategory = req.body.categories;
	var reqItems = req.body.items;

	function validateEmail(testedEmail) { 
		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		if (re.test(testedEmail)){
			return re.test(testedEmail);
		}
		else {
			return false;
		}
	}

	function validateName(testedName) {
		//require a full name
		var re = /^[a-z][- a-z]*[- ][- a-z]*[a-z]$/i;
		if (re.test(testedName)){
			return re.test(testedName);
		}
		else {
			return false;
		}
	}

	//Array to hold all error objects
	var errors = [];
	//TODO: error:cant send headers after they are sent
	if(user == undefined) {
		var response = {
			status: "error",
			content: "User does not exist"
		}
		res.status(500).send(response);
	}

	//If Email parameter exists
	if(reqEmail) {
		if((validateEmail(reqEmail) == false))  {
			var response = {
				status: "error",
				content: "Email is invalid"	
			}
			errors.push(response);
		}
		else {
			user.email = reqEmail;

		}
	}

	//If Name parameter exists
	if(reqName) {
		if(validateName(reqName) == false) {
			var response = {
				status: "error",
				content: "Full Name is invalid"
			}
			errors.push(response);
		}
		else {
			user.name = reqName;
		}
	}

	//If Category parameter exists
	if(reqCategory) {
		if(!(reqCategory.constructor === Array)) {
			var response = {
				status: "error",
				content: "Incorrect data type. Array required."
			}
			errors.push(response);
		}
		else {
			user.categories = reqCategory;
			console.log(user.categories);
		}
	} else {
		//IM NULL OR UNDEFINED OR FALSE OR A LIST OF NO LENGTH
		// var response = {
		// 	status: "error",
		// 	content: "Category array has no content"
		// }
		// errors.push(response);
	}

	//If Items parameter exists
	if(reqItems) {
		if(!reqItems.constructor === Array) {
			var response = {
				status: "error",
				content: "Incorrect data type. Array required."
			}
			errors.push(response);
		}
		else {
			//TODO: push each item to the user items array TERNARY! …nope.
			_.each(reqItems, function(it){
				user.items.push(it);
			});
			console.log(user.items);
		}
	}


	//If there are no errors send updated user object
	if(!errors.length) {
		var response = {
			status: "ok",
			content: user
		}
		res.status(200).send(response);
	}
	else {
		var response = {
			status: "error",
			content: errors
		}
		console.log(errors);
		res.status(500).send(response);
	}
});


//GET all photos by one user
router.get('/api/user/:id/photo', function(req,res) {
	var id = req.params.id;
	var userPhotos = _.filter(db.photo, function(it){ return id == it.userId; });

	if (userPhotos) {
		var response = {
			status: "ok",
			content: userPhotos
		};
		res.status(200).send(response);
	}
	else {
		var response = {
			status:"error",
			content:"not found"
		}
		res.status(404).send(response);
	}
});

//GET all list items saved by one user
router.get('/api/user/:id/list', function(req,res) {
	var id = req.params.id;
	var user = _.find(db.user, function(us){ return id == us.id; });
	var userItemIds = user.items;
	var userItems = _.filter(db.item, function(item) { 
		return _.contains(userItemIds, item.id); 
	});

	if (userItems && userItems.length) {
		var response = {
			status: "ok",
			content: userItems
		};
		res.status(200).send(response);
	}
	else {
		var response = {
			status:"error",
			content:"not found"
		}
		res.status(404).send(response);
	}	
});

//POST photo
router.post('/api/photo', function(req,res) {

	userID = req.body.userID;
	itemID = req.body.itemID;

	//create new photo object

	//set current date
	var dateCreated = new Date();
	var dd = dateCreated.getDate();
	var mm = dateCreated.getMonth()+1; //January is 0!
	var yyyy = dateCreated.getFullYear();
	if(dd<10) {
	    dd='0'+dd
	} 
	if(mm<10) {
	    mm='0'+mm
	} 
	dateCreated = mm+'/'+dd+'/'+yyyy;

	//generate random photo ID
	var randomId = _.random(0, 100);

	var photo = { 
		id: randomId,
		itemID: itemID,
		userID: userID,
		photoUrl: "http://http://cdn2.gamefront.com/wp-content/uploads/2014/06/Assassins-Creed-Unity.jpg",
		dateCreated: dateCreated
	};

	var response = {
		status: "ok",
		content: photo
	};
	res.status(200).send(response);

});

//GET all photos
router.get('/api/photo', function(req,res) {
	var response = {
		status: "ok",
		content: db.photo
	};
	res.status(200).send(response);
});

//Get Photo by ID
router.get('/api/photo/:id', function(req,res) {
	var id = req.params.id;
	var photo = _.find(db.photo, function(it){ return id == it.id; });

	if (photo) {
		var response = {
			status: "ok",
			content: photo
		};
		res.status(200).send(response);
	}
	else {
		var response = {
			status:"error",
			content:"not found"
		}
		res.status(404).send(response);
	}
});

//GET All categories
router.get('/api/category', function(req, res) {
		var response = {
			status: "ok",
			content: db.category
		}
	res.status(200).send(response);
});

//GET Category by ID
router.get('/api/category/:id', function(req, res) {
	var id = req.params.id;
	var category = db.category[id];

	if (category) {
		var response = {
			status: "ok",
			content: category
		}
		res.status(200).send(response);
	} else {
		var response = {
			status: "error",
			content: "not found"
		}
		res.status(404).send(response);
	}
});

module.exports = router;
