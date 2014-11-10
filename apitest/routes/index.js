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


//GET ListMaker for Single Item
//TODO: Add Listmaker Name to regular list item entry instead
router.get('/api/item/:id/maker', function(req,res) {
	var makerItem = {};
	//Store Item Id
	var id = req.params.id;

	//Get item that matches that Id
	var item = _.find(db.item, function(it){ return id == it.id; });
	//Store Item name in makerItem object
	makerItem.item = item.name;
	//Get Maker Name
	var maker = _.find(db.user, function(us){ 
			return item.userId == us.id; 
	});

	makerItem.maker = maker.name;

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
		//Simulate new user Id with random number
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

	var errors = [];
	//TODO: error:cant send headers after they are sent
	if(user == undefined) {
		var response = {
			status: "error",
			content: "User does not exist"
		}
		res.status(500).send(response);
	}

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

	//TODO: add categories

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

	var randomId = _.random(0, 100);

	var photo = { 
		id: randomId,
		photoUrl: "http://http://cdn2.gamefront.com/wp-content/uploads/2014/06/Assassins-Creed-Unity.jpg"
	};

	var response = {
		status: "ok",
		content: photo
	};
	res.status(200).send(response);

//Other properties (category,name) for photo will come from: list item it is responding to

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
