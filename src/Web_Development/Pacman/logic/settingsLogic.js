var keyboardMap;

$(document).ready(function() 
{	
	initMap();

    document.getElementById("leftContolInput").addEventListener('keydown', function(event){
		setControl("leftContolInput",event.keyCode);
    });
    document.getElementById("rightControlInput").addEventListener('keydown', function(event){
		setControl("rightControlInput",event.keyCode);
    });
    document.getElementById("upControlInput").addEventListener('keydown', function(event){
		setControl("upControlInput",event.keyCode);
    });
    document.getElementById("downControlInput").addEventListener('keydown', function(event){
		setControl("downControlInput",event.keyCode);
	});


    $("#settingsForm").validate({
        rules: {
            totalGameTimeInput: {
				requried: true,
                min: 60
            },
            numOfMonstersInput: {
				requried: true,
                min: 1,
                max: 4
			},
			numOfBallsInput: {
				requried: true,
                min: 50,
                max: 90
            }
        },
        submitHandler: function() {
            let left = document.getElementById("leftContolInput").name;
            let right = document.getElementById("rightControlInput").name;
            let up = document.getElementById("upControlInput").name;
			let down = document.getElementById("downControlInput").name;

			console.log("Left: " + left + " | Right: " + right + " | Up: " + up + " | Down: " + down);
		
			// validate 
			let legal = (left != "") && (right != "") && (up != "") && (down != "");
            if(legal) legal = left != right && left != up && left != down && right != up && right != down && up != down;
			
			if(!legal) alert("All Controls Must be Diffrent and not empty");
			else
			{
				// set game inputs
				leftControl = left;
				rightControl = right;
				upControl = up;
				downControl = down;

				easyBallColor = document.getElementById("easyBallColorInput").value;
				normalBallColor = document.getElementById("normalBallColorInput").value;
				rareBallColor = document.getElementById("rareBallColorInput").value;

				numOfBalls = parseInt(document.getElementById("numOfBallsInput").value);
				totalGameTime = parseInt(document.getElementById("totalGameTimeInput").value);
                numOfMonsters = parseInt(document.getElementById("numOfMonstersInput").value);
                
                startTemplate = Math.floor(Math.random() * 2);

				// show settings
				document.getElementById("leftContol").value = document.getElementById("leftContolInput").value;
				document.getElementById("rightControl").value = document.getElementById("rightControlInput").value;
				document.getElementById("upControl").value = document.getElementById("upControlInput").value;
				document.getElementById("downControl").value = document.getElementById("downControlInput").value;
				document.getElementById("easyBallColor").value = easyBallColor;
				document.getElementById("normalBallColor").value = normalBallColor;
				document.getElementById("rareBallColor").value = rareBallColor;
				document.getElementById("totalGameTime").value = totalGameTime;
				document.getElementById("numOfBalls").value = numOfBalls;
                document.getElementById("numOfMonsters").value = numOfMonsters;
                
                document.getElementById("lblName").value = loggedUser.name;

                // start game
				showDiv("gameState");
                //Start();
                BuildAndStart();
			}
		}
    });
});

function setControl(id,code)
{
	if(typeof keyboardMap[code] != 'undefined')
	{
		document.getElementById(id).value = keyboardMap[code];
	}
	else document.getElementById(id).value = String.fromCharCode(code);
	
	document.getElementById(id).name = code;
}

function randomSettings()
{
	setControl("leftContolInput",37);
	setControl("rightControlInput",39);
	setControl("upControlInput",38);
	setControl("downControlInput",40);

	let randomColor = '#'+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6);
	let randomColor2 = '#'+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6);
	while(randomColor == randomColor2) randomColor2 = '#'+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6);
	let randomColor3 = '#'+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6);
	while(randomColor == randomColor3 || randomColor2 == randomColor3) randomColor3 = '#'+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6);

	document.getElementById("easyBallColorInput").value = randomColor;
	document.getElementById("normalBallColorInput").value = randomColor2;
	document.getElementById("rareBallColorInput").value = randomColor3;

	document.getElementById("numOfBallsInput").value = Math.floor(Math.random() * 41) + 50;
	document.getElementById("totalGameTimeInput").value = Math.floor(Math.random() * 600) + 60;
	document.getElementById("numOfMonstersInput").value = Math.floor(Math.random() * 4) + 1;
}

function initMap()
{
	keyboardMap = [];
	keyboardMap[65] = 'a';
	keyboardMap[66] = 'b';
	keyboardMap[67] = 'c';
	keyboardMap[68] = 'd';
	keyboardMap[69] = 'e';
	keyboardMap[70] = 'f';
	keyboardMap[71] = 'g';
	keyboardMap[72] = 'h';
	keyboardMap[73] = 'i';
	keyboardMap[74] = 'j';
	keyboardMap[75] = 'k';
	keyboardMap[76] = 'l';
	keyboardMap[77] = 'm';
	keyboardMap[78] = 'n';
	keyboardMap[79] = 'o';
	keyboardMap[80] = 'p';
	keyboardMap[81] = 'q';
	keyboardMap[82] = 'r';
	keyboardMap[83] = 's';
	keyboardMap[84] = 't';
	keyboardMap[85] = 'u';
	keyboardMap[86] = 'v';
	keyboardMap[87] = 'w';
	keyboardMap[88] = 'x';
	keyboardMap[89] = 'y';
	keyboardMap[90] = 'z';

	keyboardMap[48] = '0';
	keyboardMap[49] = '1';
	keyboardMap[50] = '2';
	keyboardMap[51] = '3';
	keyboardMap[52] = '4';
	keyboardMap[53] = '5';
	keyboardMap[54] = '6';
	keyboardMap[55] = '7';
	keyboardMap[56] = '8';
	keyboardMap[57] = '9';

	keyboardMap[96] = "NumPad 0";
	keyboardMap[97] = "NumPad 1";
	keyboardMap[98] = "NumPad 2";
	keyboardMap[99] = "NumPad 3";
	keyboardMap[100] = "NumPad 4";
	keyboardMap[101] = "NumPad 5";
	keyboardMap[102] = "NumPad 6";
	keyboardMap[103] = "NumPad 7";
	keyboardMap[104] = "NumPad 8";
	keyboardMap[105] = "NumPad 9";

	keyboardMap[37] = "Left";
	keyboardMap[38] = "Up";
	keyboardMap[39] = "Right";
	keyboardMap[40] = "Down";

	keyboardMap[9] = "Tab";
	keyboardMap[13] = "Enter";
	keyboardMap[32] = "Space";
	keyboardMap[18] = "Alt";
	keyboardMap[17] = "Control";
	keyboardMap[16] = "Shift";
	keyboardMap[20] = "CapsLock";
}

