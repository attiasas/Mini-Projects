// engine
var context;
var interval;
var timeRemains;
var lastTimeStamp;
var music;

// dynamic settings
var leftControl;
var rightControl;
var upControl;
var downControl;
var numOfBalls;
var easyBallColor;
var normalBallColor;
var rareBallColor;
var totalGameTime;
var numOfMonsters;
var startTemplate;

// game globals
var cellSize;
var board;
var score;
var pacman;
var movingFood;
var candy;
var monsters;

const MAX_LIFES = 5;
const MAX_MONSTERS = 4;

const IDLE = 0;
const LEFT = 1;
const RIGHT = 2;
const UP = 3;
const DOWN = 4;
const CREATE_CHANCE = 20; // 0.02%	

$(document).ready(function() {
	context = canvas.getContext("2d");

	$("#resetGame").on('click',function(){
		window.clearInterval(interval);
		music.pause();
		BuildAndStart();
	});

	music = document.getElementById("gameAudio");
});

class Board
{
	balls;
	board;
	ballsRemain;
	corners;
	templates;
	currentTemplate;

	constructor(templates)
	{
		this.templates = templates;
		this.currentTemplate = 0;
	}

	findRandomEmptyCell()
	{
		let row;
		let col;

		do
		{
			row = Math.floor(Math.random() * this.board.length);
			col = Math.floor(Math.random() * this.board[row].length);
		}
		while (this.board[row][col].food != null || this.board[row][col].type == 15);

		return [row, col];
	}

	getRandomCorner()
	{
		return this.corners[Math.floor(Math.random() * this.corners.length)];
	}

	changeTemplate()
	{
		let tempBoard = new Array();

		// advance template
		this.currentTemplate = (this.currentTemplate + 1) % this.templates.length;

		// copy board info into new Template
		for(let row = 0; row < this.board.length; row++)
		{
			tempBoard[row] = new Array();
			for(let col = 0; col < this.board[row].length; col++)
			{
				tempBoard[row][col] = new Cell(row,col,this.templates[this.currentTemplate][row][col]);
				tempBoard[row][col].food = this.board[row][col].food;
			}
		}
		this.board = tempBoard;
	}

	build(templateIndex,balls)
	{
		// reset
		this.board = new Array();
		this.balls = balls;
		this.currentTemplate = templateIndex;
		this.ballsRemain = 0;

		// generate board
		for(let row = 0; row < this.templates[this.currentTemplate].length; row++)
		{
			this.board[row] = new Array();
			for(let col = 0; col < this.templates[this.currentTemplate][row].length; col++)
			{
				this.board[row][col] = new Cell(row,col,this.templates[this.currentTemplate][row][col]);
			}
		}

		// generate food
		let numOfEasyBalls = Math.floor(balls * 0.6);
		let numOfNormalBalls = Math.floor(balls * 0.3) + numOfEasyBalls;

		while(this.ballsRemain < this.balls)
		{
			let randomCell = this.findRandomEmptyCell();

			if(this.ballsRemain < numOfEasyBalls)
			{
				this.board[randomCell[0]][randomCell[1]].setFood(new Food(5,easyBallColor));
			}
			else if(this.ballsRemain < numOfNormalBalls)
			{
				this.board[randomCell[0]][randomCell[1]].setFood(new Food(15,normalBallColor));
			}
			else
			{
				this.board[randomCell[0]][randomCell[1]].setFood(new Food(25,rareBallColor));
			}
			
			this.ballsRemain++;
		}

		this.corners = [[0,0],[0,this.board.length - 1],[this.board.length - 1,this.board.length - 1],[this.board.length - 1,0]];
	}

	inBoard(row,col)
	{
		return row >= 0 && col >= 0 && row < this.board.length && col < this.board[row].length;
	}

	isBoardEmpty()
	{
		return this.ballsRemain <= 0;
	}

	eatCellFood(row,col)
	{
		if(!this.inBoard(row,col)) return null;

		let cellFood = this.board[row][col].food;
		if(cellFood != null)
		{
			this.board[row][col].setFood(null);
			score += cellFood.points;
			this.ballsRemain--;
		}

		return cellFood;
	}

	canAdvance(gameObject,direction)
	{
		return this.inBoard(gameObject.row,gameObject.col) && this.board[gameObject.row][gameObject.col].canMove(direction);
	}

	draw()
	{
		for(let row = 0; row < this.board.length; row++)
		{
			for(let col = 0; col < this.board[row].length; col++)
			{
				this.board[row][col].draw();
			}
		}
	}
}

class Cell
{
	row;
	col;

	leftWall;
	rightWall;
	topWall;
	bottomWall;

	food;
	type;

	constructor(row,col,type)
	{
		this.row = row;
		this.col = col;

		this.type = type;
		switch(type)
		{
			case 1: this.setWalls(true,false,false,false);break; // left
			case 2: this.setWalls(false,true,false,false);break; // top
			case 3: this.setWalls(false,false,true,false);break; // right
			case 4: this.setWalls(false,false,false,true);break; // bottom
			case 5: this.setWalls(true,true,false,false);break; // left top
			case 6: this.setWalls(true,false,true,false);break; // left right
			case 7: this.setWalls(true,false,false,true);break; // left bottom
			case 8: this.setWalls(false,true,true,false);break; // top right
			case 9: this.setWalls(false,true,false,true);break; // top bottom
			case 10: this.setWalls(false,false,true,true);break; // right bottom
			case 11: this.setWalls(true,true,true,false);break; // left top right
			case 12: this.setWalls(true,true,false,true);break; // left top bottom
			case 13: this.setWalls(true,false,true,true);break; // left right bottom
			case 14: this.setWalls(false,true,true,true);break; // top right bottom
			case 15: this.setWalls(true,true,true,true);break; // all walls
			default: this.setWalls(false,false,false,false);break; // no walls
					
		}

		this.food = null;
	}

	canMove(direction)
	{
		switch(direction)
		{
			case LEFT: return !this.leftWall; break;
			case RIGHT: return !this.rightWall; break;
			case UP: return !this.topWall; break;
			case DOWN: return !this.bottomWall; break;
		}

		return false;
	}

	setWalls(left,top,right,bottom)
	{
		this.leftWall = left;
		this.rightWall = right;
		this.topWall = top;
		this.bottomWall = bottom;
	}

	setFood(x)
	{
		this.food = x;
	}

	draw()
	{

		if(this.leftWall || this.topWall || this.rightWall || this.bottomWall)
		{
			if(this.leftWall && this.topWall && this.rightWall && this.bottomWall)
			{
				context.fillRect(this.col * cellSize, this.row * cellSize, cellSize, cellSize);
			}
			else
			{
				context.beginPath();
	
				if(this.leftWall)
				{
					context.moveTo(this.col * cellSize, this.row * cellSize);
					context.lineTo(this.col * cellSize, (this.row + 1) * cellSize);
				} 
				if(this.topWall)
				{
					context.moveTo(this.col * cellSize, this.row * cellSize);
					context.lineTo((this.col + 1) * cellSize, this.row * cellSize);
				}
				if(this.rightWall)
				{
					context.moveTo((this.col + 1) * cellSize, this.row * cellSize);
					context.lineTo((this.col + 1) * cellSize, (this.row + 1) * cellSize);
				}
				if(this.bottomWall)
				{
					context.moveTo(this.col * cellSize, (this.row + 1) * cellSize);
					context.lineTo((this.col + 1) * cellSize, (this.row + 1) * cellSize);
				}
		
				context.stroke();
			}
		}

		if(this.food != null) this.food.draw(this.row,this.col);
	}

}

class Food
{
	points;
	color;

	constructor(points,color)
	{
		this.points = points;
		this.color = color;
	}

	draw(row,col)
	{
		let centerY = row * cellSize + (cellSize / 2);
		let centerX = col * cellSize + (cellSize / 2);
		let radius = cellSize / 4 - 5;

		context.beginPath();
		context.fillStyle = this.color;
		context.arc(centerX, centerY, radius, 0, 2 * Math.PI);
		context.fill();
	}
}

class Candy
{
	row;
	col;
	color;
	timeRemain;

	constructor(color,row,col)
	{
		this.row = row;
		this.col = col;
		this.color = color;

		this.timeRemain = 5; // 5 sec until dead
	}

	timeOut(elapsed)
	{
		this.timeRemain -= elapsed;
		return this.timeRemain <= 0;
	}

	eat()
	{
		let randomNumber = Math.floor(Math.random() * 100);
		
		if(randomNumber < 20) // 20% for extra 20 points
		{
			score += 20;
		}
		else if(randomNumber < 40) // 20% for reduce 20 points
		{
			score -= 20;
		}
		else if(randomNumber < 60) // 20% change board template
		{
			board.changeTemplate();
		}
		else if(randomNumber < 80) // 20% change in monsters
		{
			if((randomNumber < 70 && monsters.length > 1) || monsters.length >= MAX_MONSTERS) // 10% reduce one monster if not last
			{
				monsters.splice(Math.floor(Math.random() * monsters.length),1);
			}
			else // 10% add one monster if not max
			{
				let newMonster;
				
				randomNumber = Math.floor(Math.random() * 3);
				if(randomNumber == 0) newMonster = new Monster("red");
				else if(randomNumber == 0) newMonster = new SemiMonster("green");
				else newMonster = new TargetMonster("blue");

				let randomCorner = board.getRandomCorner();
				newMonster.setPosition(randomCorner[0],randomCorner[1]);
				monsters.push(newMonster);
			}
		}
		else // 20% change in lifes
		{
			if((randomNumber < 90 && pacman.lifes > 1) || pacman.lifes >= MAX_LIFES) // 10% reduce life if not last
			{
				pacman.lifes--;
			}
			else // 10% add life if not max
			{
				pacman.lifes++;
			}
		}
	}

	draw()
	{
		let centerY = this.row * cellSize + (cellSize / 2);
		let centerX = this.col * cellSize + (cellSize / 2);
		let padd = 5;
		let radius = cellSize * 0.3 - padd;

		context.beginPath();
		context.fillStyle = this.color;
		context.arc(centerX, centerY, radius, 0, 2 * Math.PI);
		context.fill();
		context.beginPath();
		context.moveTo(this.col * cellSize + padd,centerY - radius);
		context.lineTo(this.col * cellSize + padd,centerY + radius);
		context.lineTo(centerX,centerY);
		context.fill();
		context.beginPath();
		context.moveTo((this.col + 1) * cellSize - padd,centerY - radius);
		context.lineTo((this.col + 1) * cellSize - padd,centerY + radius);
		context.lineTo(centerX,centerY);
		context.fill();
	}
}

class GameObject
{
	row;
	col;
	direction;
	color;

	constructor(color)
	{
		this.color = color;
		this.direction = LEFT;
	}

	colide(object)
	{
		return this.row == object.row && this.col == object.col;
	}

	setPosition(row,col)
	{
		this.row = row;
		this.col = col;
	}

	setNextDirection(legalMovments)
	{
		legalMovments.push(IDLE);
		this.direction = legalMovments[Math.floor(Math.random() * legalMovments.length)];
	}

	updatePosition(board)
	{
		// random movement including idle
		let legalMovments = [];
		if(board.canAdvance(this,LEFT)) legalMovments.push(LEFT);
		if(board.canAdvance(this,RIGHT)) legalMovments.push(RIGHT);
		if(board.canAdvance(this,UP)) legalMovments.push(UP);
		if(board.canAdvance(this,DOWN)) legalMovments.push(DOWN);

		this.setNextDirection(legalMovments);
		switch(this.direction)
		{
			case LEFT: this.setPosition(this.row,this.col-1); break;
			case RIGHT: this.setPosition(this.row,this.col+1); break;
			case UP: this.setPosition(this.row-1,this.col); break;
			case DOWN: this.setPosition(this.row+1,this.col); break;
		}
	}

	draw()
	{
		let centerY = this.row * cellSize + (cellSize / 2);
		let centerX = this.col * cellSize + (cellSize / 2);

		context.beginPath();
		context.fillStyle = this.color; //color
		context.arc(centerX, centerY, cellSize / 4, 0, 2 * Math.PI); // circle
		context.fill();
	}

	get row() { return this.row;}
	get col() { return this.col;}
}

class Pacman extends GameObject
{
	lifes;

	constructor()
	{
		super("yellow");
		this.direction = LEFT;
		this.lifes = 5;
	}

	hit()
	{
		score -= 10;
		this.lifes--;
	}

	isDead()
	{
		return this.lifes <= 0;
	}

	updatePosition(board)
	{
		// handle user input
		let nextDirection = GetActionKeyPressed();

		if(nextDirection == LEFT || nextDirection == RIGHT || nextDirection == UP || nextDirection == DOWN)
		{
			// change face direction
			this.direction = nextDirection;
				
			if(board.canAdvance(this,this.direction))
			{
				// advance
				switch(this.direction)
				{
					case LEFT: this.setPosition(this.row,this.col-1); break;
					case RIGHT: this.setPosition(this.row,this.col+1); break;
					case UP: this.setPosition(this.row-1,this.col); break;
					case DOWN: this.setPosition(this.row+1,this.col); break;
				}

				// get food and update
				board.eatCellFood(this.row,this.col);
			}
		}
	}

	draw()
	{
		let centerY = this.row * cellSize + (cellSize / 2);
		let centerX = this.col * cellSize + (cellSize / 2);

		let startAngle;
		let endAngle;
		let side;
		let eyeOffsetX;
		let eyeOffsetY;
		switch(this.direction)
		{
			case LEFT: startAngle = 0.85 * Math.PI; endAngle = 1.15 * Math.PI; side = true; eyeOffsetX = -5; eyeOffsetY = - cellSize / 4; break;
			case RIGHT: startAngle = 0.15 * Math.PI; endAngle = 1.85 * Math.PI; side = false; eyeOffsetX = 5; eyeOffsetY = - cellSize / 4; break;
			case UP: startAngle = 1.35 * Math.PI; endAngle = 1.65 * Math.PI; side = true; eyeOffsetX = 10; eyeOffsetY = 0; break;
			case DOWN: startAngle = 0.35 * Math.PI; endAngle = 0.65 * Math.PI; side = true; eyeOffsetX = 10; eyeOffsetY = 0; break;
		}

		context.beginPath();
		context.fillStyle = this.color; //color
		context.arc(centerX, centerY, cellSize / 2 - 5, startAngle, endAngle,side); // half circle
		context.lineTo(centerX, centerY);
		context.fill();
				
		context.beginPath();
		context.fillStyle = "black"; //color
		context.arc(centerX + eyeOffsetX, centerY + eyeOffsetY, 5, 0, 2 * Math.PI); // circle
		context.fill();
	}
}

class Monster extends GameObject
{
	constructor(color)
	{
		super(color);
	}

	draw()
	{
		let centerY = this.row * cellSize + (cellSize / 2);
		let centerX = this.col * cellSize + (cellSize / 2);
		let radius = cellSize / 2 - 5;

		context.beginPath();
		context.fillStyle = this.color;
		context.arc(centerX, centerY, radius, Math.PI, 2 * Math.PI); // half circle (head)
		// bottom of monster
		context.moveTo(centerX - radius,centerY);
		context.lineTo(centerX - radius, centerY + radius);
		context.lineTo(centerX - (radius / 2), centerY + (radius / 2));
		context.lineTo(centerX, centerY + radius);
		context.lineTo(centerX + (radius / 2),centerY + (radius / 2));
		context.lineTo(centerX + radius, centerY + radius);
		context.lineTo(centerX + radius, centerY);
		context.lineTo(centerX - radius, centerY);
		context.fill();

		context.beginPath();
		context.fillStyle = "white"; // eyes
		context.arc(centerX - (radius / 2),centerY - 5, 5, 0, 2 * Math.PI);
		context.arc(centerX + (radius / 2),centerY - 5, 5, 0, 2 * Math.PI);
		context.fill();
	}
}

class TargetMonster extends Monster
{
	constructor(color)
	{
		super(color)
	}

	setNextDirection(legalMovments)
	{
		if(legalMovments.length == 0)
		{
			this.direction = IDLE;
			return;
		}

		var targetOptions = [];

		if(pacman.col != this.col)
		{
			if(pacman.col < this.col)
			{
				targetOptions.push(LEFT);
				targetOptions.push(RIGHT);
			} 
			else
			{
				targetOptions.push(RIGHT);
				targetOptions.push(LEFT);
			}
		}
		if(pacman.row != this.row)
		{
			if(pacman.row < this.row)
			{
				targetOptions.push(UP);
				targetOptions.push(DOWN);
			} 
			else
			{
				targetOptions.push(DOWN);
				targetOptions.push(UP);
			}
		}
		
		for(let i = 0; i < targetOptions.length; i++)
		{
			for(let j = 0; j < legalMovments.length; j++)
			{
				if(targetOptions[i] == legalMovments[j])
				{
					this.direction = targetOptions[i];
					return;
				}
			}
		}
	}
}

class SemiMonster extends TargetMonster
{
	constructor(color)
	{
		super(color);
	}

	setNextDirection(legalMovments)
	{
		let randomNumber = Math.floor(Math.random() * 2);
		if(randomNumber == 0) super.setNextDirection(legalMovments);
		else
		{
			legalMovments.push(IDLE);
			this.direction = legalMovments[Math.floor(Math.random() * legalMovments.length)];
		}
	}
}

class MovingFood extends GameObject
{
	constructor()
	{
		super("black");
	}

	draw()
	{
		let centerY = this.row * cellSize + (cellSize / 2);
		let centerX = this.col * cellSize + (cellSize / 2);
		let padd = 10;
		// body
		context.fillStyle = this.color;
		context.fillRect(this.col * cellSize + padd, this.row * cellSize + padd, cellSize - 2 * padd, cellSize - 2 * padd);
		// eyes
		context.beginPath();
		context.fillStyle = "white";
		context.arc(centerX - padd, centerY - padd, padd / 2, 0, 2 * Math.PI);
		context.arc(centerX + padd, centerY - padd, padd / 2, 0, 2 * Math.PI);
		context.fill();
	}
}

function GetActionKeyPressed() {
	
	if (keysDown[leftControl]) return LEFT;
	if (keysDown[rightControl]) return RIGHT;
	if (keysDown[upControl]) return UP;
	if (keysDown[downControl]) return DOWN;

	return IDLE;
}

function BuildAndStart()
{
	// settings
	let boardSize = 10;
	cellSize = canvas.width / boardSize;
	score = 0;
	
	// objects init
	pacman = new Pacman();
	movingFood = new MovingFood();
	monsters = [new TargetMonster("blue"),new SemiMonster("green"),new SemiMonster("green"),new Monster("red")];
	for(let i = 0; i < MAX_MONSTERS - numOfMonsters; i++)
	{
		monsters.splice(Math.floor(Math.random() * monsters.length),1);
	}

	boardTemplates = [];
	boardTemplates.push([
		[5,9,9,9,2,2,9,9,9,8],
		[6,5,9,9,0,0,9,9,8,6],
		[6,6,5,2,3,1,2,8,6,6],
		[6,6,1,0,3,1,0,3,6,6],
		[1,0,4,4,0,0,4,4,0,3],
		[1,0,2,2,0,0,2,2,0,3],
		[6,6,1,0,3,1,0,3,6,6],
		[6,6,7,4,3,1,4,10,6,6],
		[6,7,9,9,0,0,9,9,10,6],
		[7,9,9,9,4,4,9,9,9,10]
	]);
	boardTemplates.push([
		[5,2,2,2,2,2,2,2,2,8],
		[1,10,6,7,4,4,10,6,7,3],
		[1,9,0,9,9,9,9,0,9,3],
		[1,8,6,5,8,5,8,6,5,3],
		[1,0,0,0,3,1,0,0,0,3],
		[1,0,0,0,3,1,0,0,0,3],
		[1,10,6,7,10,7,10,6,7,3],
		[1,9,0,9,9,9,9,0,9,3],
		[1,8,6,5,2,2,8,6,5,3],
		[7,4,4,4,4,4,4,4,4,10]
	]);

	board = new Board(boardTemplates);
	
	// build game
	board.build(startTemplate, numOfBalls);
	resetGameObjects();

	// setUp input listener
	keysDown = {};
	addEventListener(
		"keydown",
		function(e) {
			keysDown[e.keyCode] = true;
		},
		false
	);
	addEventListener(
		"keyup",
		function(e) {
			keysDown[e.keyCode] = false;
		},
		false
	);

	// start
	lastTimeStamp = new Date();
	timeRemains = totalGameTime;
	
	music.play();

	interval = setInterval(mainLoop, 150);
}

function resetGameObjects()
{
	let randCell;
	
	do
	{
		randCell = board.findRandomEmptyCell();
	}
	while(randCell[0] == 0 || randCell[0] == board.board.length - 1 || randCell[1] == 0 || randCell[1] == board.board.length - 1);
	pacman.setPosition(randCell[0],randCell[1]);

	randCell = board.getRandomCorner();
	if(movingFood != null) movingFood.setPosition(randCell[0],randCell[1]);

	for(let i = 0; i < monsters.length; i++)
	{
		monsters[i].setPosition(board.corners[i][0],board.corners[i][1]);
	}
}

function mainLoop()
{
	UpdateAll();
	DrawAll();

	// check for game over
	if(timeRemains <= 0)
	{
		window.clearInterval(interval);
		music.pause();

		if(score < 100)
		{
			window.alert("You are better than " + score + " points!");
		}
		else
		{
			window.alert("Winner!!!");
		}
	}
	else if(pacman.isDead())
	{
		window.clearInterval(interval);
		music.pause();
		window.alert("Loser!");
	}
	else if(board.isBoardEmpty())
	{
		window.clearInterval(interval);
		music.pause();
		window.alert("Winner!!!");
	}
}

function UpdateAll()
{
	// update game state
	let stamp = new Date();
	timeRemains -= (stamp - lastTimeStamp) / 1000;

	// update Position and check colisions
	for(let i = 0; i < monsters.length; i++)
	{
		if(pacman.colide(monsters[i]))
		{
			pacman.hit();
			resetGameObjects();
			break;
		}
	}
	for(let i = 0; i < monsters.length; i++)
	{
		monsters[i].updatePosition(board);
	}
	for(let i = 0; i < monsters.length; i++)
	{
		if(pacman.colide(monsters[i]))
		{
			pacman.hit();
			resetGameObjects();
			break;
		}
	}

	pacman.updatePosition(board);
	
	if(movingFood != null && pacman.colide(movingFood))
	{
		score += 50;
		movingFood = null;
	}
	
	if(movingFood != null) movingFood.updatePosition(board);

	if(movingFood != null && pacman.colide(movingFood))
	{
		score += 50;
		movingFood = null;
	}

	if(candy == null)
	{
		// random creation
		let random = Math.floor(Math.random() * 100);
		if(random < CREATE_CHANCE)
		{
			let randomColor = '#'+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6);
			let randomCell = board.findRandomEmptyCell();
			candy = new Candy(randomColor,randomCell[0],randomCell[1]);
		}
	}
	else if(candy.timeOut((stamp - lastTimeStamp) / 1000)) candy = null;
	else if(pacman.colide(candy))
	{
		// handle random candy
		candy.eat();
		candy = null;
	}

	// update game labels
	lastTimeStamp = stamp;
	lblLifes.value = pacman.lifes;
	lblScore.value = score;
	lblTime.value = Math.floor(timeRemains);
}

function DrawAll()
{
	canvas.width = canvas.width; //clean board

	board.draw();

	if(candy != null) candy.draw();
	
	if(movingFood != null) movingFood.draw();
	pacman.draw();

	for(let i = 0; i < monsters.length; i++)
	{
		monsters[i].draw();
	}
}
