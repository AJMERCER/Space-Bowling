//April 25th 2018 version



	// First we declare the variables that hold the objects we need
	// in the animation code
	var scene, renderer;  // all threejs programs need these
	var camera, avatarCam, edgeCam;  // we have two cameras in the main scene
	var avatar;
	// here are some mesh objects ...
	var suzanne;
	var endScene, endCamera, endText;
	var startScene, startCamera, startText;

	var controls =
	     {fwd:false, bwd:false, left:false, right:false,
				speed:30, fly:false, reset:false,
		    camera:camera}

	var gameState =
	     {score:0, health:10, scene:'main', camera:'none'}

	// Here is the main game control
  init(); //
	initControls();
	animate();  // start the animation loop!

	function createEndScene(){
		endScene = initScene();
		endText = createSkyBox('youwon.png',10);
		//endText.rotateX(Math.PI);
		endScene.add(endText);
		var light1 = createPointLight();
		light1.position.set(0,200,20);
		endScene.add(light1);
		endCamera = new THREE.PerspectiveCamera( 90, window.innerWidth / window.innerHeight, 0.1, 1000 );
		endCamera.position.set(0,50,1);
		endCamera.lookAt(0,0,0);
	}
/*
	  To initialize the scene, we initialize each of its components
	*/
	function init(){
			createStartScene();
      initPhysijs();
			scene = initScene();
			initRenderer();
			createMainScene();
			createEndScene();
			initSuzanne();
			initSuzanneOBJ();

	}
	function createStartScene(){
			startScene = initScene();
			startText = createStart('bowling.png',10);

			startScene.add(startText);
			var light1 = createPointLight();
			light1.position.set(0,200,20);
			startScene.add(light1);
			startCamera = new THREE.PerspectiveCamera( 90, window.innerWidth / window.innerHeight, 0.1, 1000 );
			startCamera.position.set(0,50,1);
			startCamera.lookAt(0,0,0);
			gameState.scene = 'open';
		}



		function createStart(image,k){
			// creating a textured plane which receives shadows
			var geometry = new THREE.PlaneGeometry( 100, 100, 100 );
			var texture = new THREE.TextureLoader().load( '../images/'+image );
			var material = new THREE.MeshLambertMaterial( { color: 0xffffff,  map: texture ,side:THREE.DoubleSide} );
			var mesh = new THREE.Mesh( geometry, material, 0 );

			mesh.receiveShadow = false;
			mesh.rotateX(Math.PI/2);

			return mesh

		}





	function createMainScene(){
      // setup lighting
			var light1 = createPointLight();
			light1.position.set(0,200,20);
			scene.add(light1);
			var light0 = new THREE.AmbientLight( 0xffffff,0.25);
			scene.add(light0);

			// create main camera
			camera = new THREE.PerspectiveCamera( 90, window.innerWidth / window.innerHeight, 0.1, 1000 );
			camera.position.set(0,50,0);
			camera.lookAt(0,0,0);

			addPins();


			// create the ground and the skybox
			var catcher = createCatcher('wf.jpg');
			catcher.position.set(0,-50,0);
			scene.add(catcher);
			var ground = createGround('wf.jpg');
			scene.add(ground);
			var skybox = createSkyBox('space.jpg',1);
			scene.add(skybox);
			//var target = createTargetZone('black.png');
			//target.se
			//scene.add(target);

			// create the avatar
			avatarCam = new THREE.PerspectiveCamera( 60, window.innerWidth / window.innerHeight, 0.1, 1000 );
			avatar = createAvatar();

			avatar.translateY(5);
			avatar.translateZ(-25);
			avatarCam.translateY(-4);
			avatarCam.translateZ(3);
			scene.add(avatar);
			gameState.camera = avatarCam;

      edgeCam = new THREE.PerspectiveCamera( 60, window.innerWidth / window.innerHeight, 0.1, 1000 );
      edgeCam.position.set(20,20,10);

	}

	function randN(n){
		return Math.random()*n;
	}

	function playGameMusic(){
		// create an AudioListener and add it to the camera
		var listener = new THREE.AudioListener();
		camera.add( listener );

		// create a global audio source
		var sound = new THREE.Audio( listener );

		// load a sound and set it as the Audio object's buffer
		var audioLoader = new THREE.AudioLoader();
		audioLoader.load( '/sounds/loop.mp3', function( buffer ) {
			sound.setBuffer( buffer );
			sound.setLoop( true );
			sound.setVolume( 0.05 );
			sound.play();
		});
	}

	function soundEffect(file){
		// create an AudioListener and add it to the camera
		var listener = new THREE.AudioListener();
		camera.add( listener );

		// create a global audio source
		var sound = new THREE.Audio( listener );

		// load a sound and set it as the Audio object's buffer
		var audioLoader = new THREE.AudioLoader();
		audioLoader.load( '/sounds/'+file, function( buffer ) {
			sound.setBuffer( buffer );
			sound.setLoop( false );
			sound.setVolume( 0.5 );
			sound.play();
		});
	}

	/* We don't do much here, but we could do more!
	*/
	function initScene(){
		//scene = new THREE.Scene();
    var scene = new Physijs.Scene();
		return scene;
	}

  function initPhysijs(){
    Physijs.scripts.worker = '/js/physijs_worker.js';
    Physijs.scripts.ammo = '/js/ammo.js';
  }
	/*
		The renderer needs a size and the actual canvas we draw on
		needs to be added to the body of the webpage. We also specify
		that the renderer will be computing soft shadows
	*/
	function initRenderer(){
		renderer = new THREE.WebGLRenderer();
		renderer.setSize( window.innerWidth, window.innerHeight-50 );
		document.body.appendChild( renderer.domElement );
		renderer.shadowMap.enabled = true;
		renderer.shadowMap.type = THREE.PCFSoftShadowMap;
	}

				function initSuzanne(){
			var loader = new THREE.JSONLoader();
			loader.load("../models/pin.obj",
						function ( geometry, materials ) {
							console.log("loading suzanne");
							var material = //materials[ 0 ];
							new THREE.MeshLambertMaterial( { color: 0x00ff00 } );
							suzanne = new THREE.Mesh( geometry, material );
							var suzy2 = suzanne.clone(false);
							console.log("created suzanne mesh");
							console.log(JSON.stringify(suzanne.scale));// = new THREE.Vector3(4.0,1.0,1.0);
							scene.add( suzanne  );
							var s = .5;
							suzanne.scale.y=s;
							suzanne.scale.x=s;
							suzanne.scale.z=s;
							suzanne.position.z = -5;
							suzanne.position.y = 3;
							suzanne.position.x = -5;
							suzanne.castShadow = true;


							suzy2.position.x = 1;
							suzy2.position.y = 2;
							scene.add(suzy2);
							suzy2.castShadow = true;

							//
						},
						function(xhr){
							console.log( (xhr.loaded / xhr.total * 100) + '% loaded' );},
						function(err){console.log("error in loading: "+err);}
					)
		}


		function initSuzanneOBJ(){
			var loader = new THREE.OBJLoader();
			loader.load("../models/pin.obj",
						function ( obj) {
							console.log("loading obj file");
							obj.scale.x=1;
							obj.scale.y=1;
							obj.scale.z=1;
							obj.position.y = 2;
							obj.position.z = 0;

							scene.add(obj);
							obj.castShadow = true;

							//
						},
						function(xhr){
							console.log( (xhr.loaded / xhr.total * 100) + '% loaded' );},

						function(err){
							console.log("error in loading: "+err);}
					)
		}



	function createPointLight(){
		var light;
		light = new THREE.PointLight( 0xffffff);
		light.castShadow = true;
		//Set up shadow properties for the light
		light.shadow.mapSize.width = 2048;  // default
		light.shadow.mapSize.height = 2048; // default
		light.shadow.camera.near = 0.5;       // default
		light.shadow.camera.far = 500      // default
		return light;
	}



	function createBoxMesh(color){
		var geometry = new THREE.BoxGeometry( 1, 1, 1);
		var material = new THREE.MeshLambertMaterial( { color: color} );
		mesh = new Physijs.BoxMesh( geometry, material );
    //mesh = new Physijs.BoxMesh( geometry, material,0 );
		mesh.castShadow = true;
		return mesh;
	}


	function createCatcher(image){
		// creating a textured plane which receives shadows
		var geometry = new THREE.PlaneGeometry( 300, 2550, 1 );
		var texture = new THREE.TextureLoader().load( '../images/'+image );
		texture.wrapS = THREE.RepeatWrapping;
		texture.wrapT = THREE.RepeatWrapping;
		texture.repeat.set( 15, 15 );
		var material = new THREE.MeshLambertMaterial( { color: 0xffffff,  map: texture ,side:THREE.DoubleSide} );
		var pmaterial = new Physijs.createMaterial(material,0.9,0.05);
		//var mesh = new THREE.Mesh( geometry, material );
		var mesh = new Physijs.BoxMesh( geometry, pmaterial, 0 );
		mesh.receiveShadow = true;



		mesh.rotateX(Math.PI/2);
		return mesh


		// we need to rotate the mesh 90 degrees to make it horizontal not vertical
	}


	function createGround(image){
		// creating a textured plane which receives shadows
		var geometry = new THREE.PlaneGeometry( 30, 155, 1 );
		var texture = new THREE.TextureLoader().load( '../images/'+image );
		texture.wrapS = THREE.RepeatWrapping;
		texture.wrapT = THREE.RepeatWrapping;
		texture.repeat.set( 15, 15 );
		var material = new THREE.MeshLambertMaterial( { color: 0xffffff,  map: texture ,side:THREE.DoubleSide} );
		var pmaterial = new Physijs.createMaterial(material,0.9,0.05);
		//var mesh = new THREE.Mesh( geometry, material );
		var mesh = new Physijs.BoxMesh( geometry, pmaterial, 0 );
		mesh.receiveShadow = true;

		mesh.rotateX(Math.PI/2);

		return mesh


		// we need to rotate the mesh 90 degrees to make it horizontal not vertical
	}

	// function createTargetZone(image){
	// 	var geometry = new THREE.PlaneGeometry( 300, 300, 1 );
	// 	var texture = new THREE.TextureLoader().load( '../images/'+image );
	// 	texture.wrapS = THREE.RepeatWrapping;
	// 	texture.wrapT = THREE.RepeatWrapping;
	// 	texture.repeat.set( 15, 15 );
	// 	var material = new THREE.MeshLambertMaterial( { color: 0xffffff,  map: texture ,side:THREE.DoubleSide} );
	// 	var pmaterial = new Physijs.createMaterial(material,0.9,0.05);
	// 	//var mesh = new THREE.Mesh( geometry, material );
	// 	var mesh = new Physijs.BoxMesh( geometry, pmaterial, 0 );
	// 	mesh.receiveShadow = true;
	//
	// 	mesh.rotateX(Math.PI/2);
	//
	// 	return mesh
	//
	// }


	function createSkyBox(image,k){
		// creating a textured plane which receives shadows
		var geometry = new THREE.SphereGeometry( 120, 120, 120 );
		var texture = new THREE.TextureLoader().load( '../images/'+image );
		texture.wrapS = THREE.RepeatWrapping;
		texture.wrapT = THREE.RepeatWrapping;
		texture.repeat.set( k, k );
		var material = new THREE.MeshLambertMaterial( { color: 0xffffff,  map: texture ,side:THREE.DoubleSide} );
		var mesh = new THREE.Mesh( geometry, material, 0 );
		mesh.receiveShadow = false;
		return mesh



	}

	function createAvatar(){
		//var geometry = new THREE.SphereGeometry( 4, 20, 20);
		var geometry = new THREE.SphereGeometry( 2, 100, 100);
		var material = new THREE.MeshLambertMaterial( { color: 0xffff00} );
		var pmaterial = new Physijs.createMaterial(material,0.9,0.5);
		//var mesh = new THREE.Mesh( geometry, material );
		var mesh = new Physijs.BoxMesh( geometry, pmaterial );
		mesh.setDamping(0.1,0.1);
		mesh.castShadow = true;

		avatarCam.position.set(0,15,-15);
		avatarCam.lookAt(0,4,10);
		mesh.add(avatarCam);

		return mesh;
	}





	function createBall(){
		//var geometry = new THREE.SphereGeometry( 4, 20, 20);
		var geometry = new THREE.SphereGeometry( 1, 32, 3);
		var material = new THREE.MeshLambertMaterial( { color: 0xffffff} );
		var pmaterial = new Physijs.createMaterial(material,0.9,0.95);
    var mesh = new Physijs.BoxMesh( geometry, pmaterial );
		mesh.setDamping(0.1,0.1);
		mesh.castShadow = true;
		return mesh;
	}

	function addPins(){



//fourth level pins
for (k=0;k<4;k++){
	if (k == 3){
		var pin4= createBall();
		pin4.position.set(-4+(2*k + 1),3,46);
		scene.add(pin4);
	}
	if (k == 2){
	var pin3= createBall();
	pin3.position.set(-4+(2*k + 1),3,46);
	scene.add(pin3);
	}
	if (k == 1){
	var pin2 = createBall();
	pin2.position.set(-4+(2*k + 1),3,46);
	scene.add(pin2);
	}
	if (k == 0){
	var pin1 = createBall();
	pin1.position.set(-4+(2*k + 1),3,46);
	scene.add(pin1);
	}
	}
//third level pins
for (k = 0; k <3; k++){
	if (k == 2){
	var pin7= createBall();
	pin7.position.set(-4+(2*k + 2),3,42);
	scene.add(pin7);
}
if (k == 1){
	var pin6 = createBall();
	pin6.position.set(-4+(2*k + 2),3,42);
	scene.add(pin6);
}
if (k == 0){
	var pin5 = createBall();
	pin5.position.set(-4+(2*k + 2),3,42);
	scene.add(pin5);
}
}
//seccond level pins
for(k = 0; k <2; k++){
	if (k == 0){
	var pin8 = createBall();
	pin8.position.set(-4+(2*k + 3),3,38);
	scene.add(pin8);
}
if (k == 1){
	var pin9 = createBall();
	pin9.position.set(-4+(2*k + 3),3,38);
	scene.add(pin9);
}
}
//top pins
for(k = 0; k <1; k++){
	var pin10 = createBall();
	pin10.position.set(-4+(2*k + 4),3,34);
	scene.add(pin10);
}
	}
	ball.addEventListener( 'collision',
		function( other_object, relative_velocity, relative_rotation, contact_normal ) {
			if (other_object==catcher){
				soundEffect('good.wav');
				gameState.score += 1;
				console.log(gameStae.score); // add one to the score
			}
			}
		)

	var clock;

	function initControls(){
		// here is where we create the eventListeners to respond to operations

		  //create a clock for the time-based animation ...
			clock = new THREE.Clock();
			clock.start();

			window.addEventListener( 'keydown', keydown);
			window.addEventListener( 'keyup',   keyup );
  }

	function keydown(event){
		console.log("Keydown: '"+event.key+"'");
		//console.dir(event);
		// first we handle the "play again" key in the "youwon" scene
		if (gameState.scene == 'youwon' && event.key=='r') {
			gameState.scene = 'main';
			gameState.score = 0;
			return;
		}

		// this is the regular scene
		switch (event.key){
			// change the way the avatar is moving
			case "w": controls.fwd = true;  break;
			case "a": controls.left = true; break;
			case "d": controls.right = true; break;
			case "m": controls.speed = 30; break;


			// switch cameras
			case "1": gameState.camera = camera; break;
			case "2": gameState.camera = avatarCam; break;
      case "3": gameState.camera = edgeCam; break;

			// move the camera around, relative to the avatar

		}

	}

	function keyup(event){
		//console.log("Keydown:"+event.key);
		//console.dir(event);
		switch (event.key){
			case "w": controls.fwd   = false;  break;
			case "a": controls.left  = false; break;
			case "d": controls.right = false; break;
			case "m": controls.speed = 10; break;
			case "p": gameState.scene = 'main'; break;
			case "r": for (k=0;k<4;k++){
				if (k == 3){
				pin4.position.set(-4+(2*k + 2),3,42);
				}
				if (k == 2){
				pin3.position.set(-4+(2*k + 2),3,42);
				}
				if (k == 1){
				pin2.position.set(-4+(2*k + 2),3,42);
				}
				if (k == 0){
				pin1.position.set(-4+(2*k + 2),3,42);
				}
			}
				for (k = 0; k <3; k++){
					if (k == 2){

					pin7.position.set(-4+(2*k + 2),3,42);

				}
				if (k == 1){

					pin6.position.set(-4+(2*k + 2),3,42);

				}
				if (k == 0){

					pin5.position.set(-4+(2*k + 2),3,42);

				}
				}
				//seccond level pins
				for(k = 0; k <2; k++){
					if (k == 0){

					pin8.position.set(-4+(2*k + 3),3,38);

				}
				if (k == 1){

					pin9.position.set(-4+(2*k + 3),3,38);
				}
				}
				//top pins
				for(k = 0; k <1; k++){

					pin10.position.set(-4+(2*k + 4),3,34);

				}
			 break;
		}
	}



  function updateAvatar(){
		"change the avatar's linear or angular velocity based on controls state (set by WSAD key presses)"

		var forward = avatar.getWorldDirection();

		if (controls.fwd){
			avatar.setLinearVelocity(forward.multiplyScalar(controls.speed));
		} else if (controls.bwd){
			avatar.setLinearVelocity(forward.multiplyScalar(-controls.speed));
		} else {
			var velocity = avatar.getLinearVelocity();
			velocity.x=velocity.z=0;
			avatar.setLinearVelocity(velocity); //stop the xz motion
		}

    if (controls.fly){
      avatar.setLinearVelocity(new THREE.Vector3(0,controls.speed,0));
    }

		if (controls.left){
			avatar.setAngularVelocity(new THREE.Vector3(0,controls.speed*0.1,0));
		} else if (controls.right){
			avatar.setAngularVelocity(new THREE.Vector3(0,-controls.speed*0.1,0));
		}

    if (controls.reset){
      avatar.__dirtyPosition = true;
      avatar.position.set(40,10,40);
    }

	}



	function animate() {

		requestAnimationFrame( animate );

		switch(gameState.scene) {

			case "open":
			renderer.render( startScene, startCamera );
			break;

			case "youwon":
				endText.rotateY(0.005);
				renderer.render( endScene, endCamera );
				break;

			case "main":
				updateAvatar();
        edgeCam.lookAt(avatar.position);
	    	scene.simulate();
				if (gameState.camera!= 'none'){
					renderer.render( scene, gameState.camera );
				}
				break;

			default:
			  console.log("don't know the scene "+gameState.scene);
		}

		var info = document.getElementById("info");
		info.innerHTML='<div style="font-size:24pt">     Press P to play!!!! //////////    F5 to reset the game '+ '</div>';


	}
