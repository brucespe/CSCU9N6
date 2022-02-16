
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import java.awt.*;


import game2D.*;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. By default GameCore
// will handle the 'Escape' key to quit the game but you should
// override this with your own event handler.

/**
 * @author David Cairns
 * Student : 2721301
 *
 */
@SuppressWarnings("serial")

public class Game extends GameCore 
{
	// Useful game constants
	static int screenWidth = 1200;
	static int screenHeight = 500;
	
//	static int screenWidth = 1920;
//	static int screenHeight = 1080;
	
	float	gravity = 0.0001f;
	float scale, plusJump, plusSpeed;
	int gameScreen;
	int prevState = 0;

	// Game state flags
	boolean canJump = false;
	boolean takeDamage = false;
	boolean easy;
	private boolean paused = false;

	// Game resources
	Image menuImage, lvl1Image,lvl2Image;
	Animation guiAnim;
	Animation animStart, animQuit, animUpgr;
	Animation landing;
	Animation idle, idle2, run, damage;
	Animation coinSpin;
	Animation scorpioWalk;
	Animation knightIdle, knightWalk, knightAttack;
	int running = 0;

	ScreenManager screen = new ScreenManager();;

	Sprite dino1 = null;
	Sprite dino2 = null;
	Sprite startButton = null;
	Sprite quitButton = null;
	Sprite uSize = null;
	Sprite uJump = null;
	Sprite uSpeed = null;

	ArrayList<Sprite> scorpion	= new ArrayList<Sprite>();
	ArrayList<Sprite> clouds = new ArrayList<Sprite>();
	ArrayList<Sprite> coins = new ArrayList<Sprite>();
	String[] maps = {"map1.txt", "map2.txt"};
	int currMap;
	int xTiles, yTiles;
	int health, coinScore;
	
    private GraphicsDevice device;


	TileMap tmap = new TileMap();	// Our tile map, note that we load it in init()


	/**
	 * The obligatory main method that creates
	 * an instance of our class and starts it running
	 * 
	 * @param args	The list of parameters this program might use (ignored)
	 */
	public static void main(String[] args) {

		Game gct = new Game();
		//gct = new Game();
		gct.init();
		// Start in windowed mode with the given screen height and width
		gct.run(false,screenWidth,screenHeight);
	


	}

	/**
	 * Initialise the class, e.g. set up variables, load images,
	 * create animations, register event handlers
	 */
	public void init()		//initializes the score, map, screen size and animations
	{      
		//initialise global variables
		coinScore = 0;		//Amount of coins the player has
		currMap = 0;		//The current map
		scale = 1.2f;		//Scale of player sprite
		plusJump = 0.0f;	//Upgraded jump (upgraded in menu)
		plusSpeed = 0.0f;	//Upgraded speed
		gameScreen=0;		//The screen the window is currently on
		easy = false;		//Used to set easy mode


		//Sets the window size and makes it visible
		setSize(screenWidth,screenHeight);
		setVisible(true);


		//Loads the map
		tmap.loadMap("maps", maps[currMap]);


		//background images
		lvl1Image = loadImage("maps/lvl11.png");
		lvl2Image = loadImage("maps/lvl22.png");

		//LOADS THE DINOSAURS SPRITE ANIMATIONS
		idle = new Animation();
		idle.loadAnimationFromSheet("images/d1Sprite.png", 24, 1, 60, 0, 4);

		idle2 = new Animation();
		idle2.loadAnimationFromSheet("images/d2Sprite.png", 24, 1, 60, 0, 4);

		run = new Animation();
		run.loadAnimationFromSheet("images/d1Sprite.png", 24, 1, 60, 4, 14);

		damage = new Animation();
		damage.loadAnimationFromSheet("images/d1Sprite.png", 24, 1, 60, 15, 17);


		//LOADS THE ENEMY SPRITE ANIAMTIONS
		scorpioWalk = new Animation();
		scorpioWalk.loadAnimationFromSheet("images/Scorpio_walk.png", 4, 1, 60, 0, 4);

		knightIdle = new Animation();
		knightIdle.loadAnimationFromSheet("images/knightIdle.png", 4, 1, 60, 0, 4);

		knightWalk = new Animation();
		knightWalk.loadAnimationFromSheet("images/knightWalk.png", 8, 1, 60, 0, 8);

		knightAttack = new Animation();
		knightAttack.loadAnimationFromSheet("images/knightAttack.png", 4, 1, 60, 0, 4);


		//LOADS OTHER ANIMATIONS
		coinSpin = new Animation();		//Coin pickups
		coinSpin.loadAnimationFromSheet("images/coin.png", 4, 4, 200, 0, 15);

		guiAnim = new Animation();		//Menu buttons
		guiAnim.loadAnimationFromSheet("images/gui.png", 9, 2, 1, 0, 17);

		animStart = new Animation();			//Start button animation
		animStart.addFrame(guiAnim.getFrameImage(1), 1);

		startButton = new Sprite(animStart);	//Start button sprite
		startButton.show();

		animUpgr = new Animation();				//Upgrade buttons
		animUpgr.addFrame(guiAnim.getFrameImage(3),1);
		uSize = new Sprite (animUpgr);
		uSize.show();

		uJump = new Sprite (animUpgr);			//Upgrade jump button
		uJump.show();

		uSpeed = new Sprite (animUpgr);			//Upgrade speed button
		uSpeed.show();

		animQuit = new Animation();				//Quit button
		animQuit.addFrame(guiAnim.getFrameImage(8), 1);
		quitButton = new Sprite(animQuit);
		quitButton.show();		


		// Initialise the player with an animation
		dino1 = new Sprite(idle);

		//Initialise the goal (reach this sprite to win)
		dino2 = new Sprite(idle2);

		//Initialise the cloud animation
		Animation ca = new Animation();
		ca.addFrame(loadImage("images/cloud.png"), 1000);

		//Initialises all the sprites on the map, then adds them to the corresponding sprite arraylist
		Sprite s;
		for (int c=0; c<4; c++)		//Coins
		{
			s = new Sprite(coinSpin);
			coins.add(s);
		}

		for (int c=0; c<3; c++)		//Scorpions (enemies)
		{
			s = new Sprite(scorpioWalk);
			scorpion.add(s);
		}
		for (int c=0; c<3; c++)
		{
			s = new Sprite(ca);		//Clouds
			clouds.add(s);
		}
		initialiseGame();

		System.out.println(tmap);	//prints the current map to console
	}

	/**
	 * Sets the sprite back to default health
	 * Sets up the current screen
	 * Includes menu screens and in game screen
	 * Initialises sprite positions
	 */
	public void initialiseGame() {
		tmap.loadMap("maps", maps[currMap]);	//load the current map

		health = 3;						//Set initial health
		xTiles = tmap.getTileWidth();	
		yTiles = tmap.getTileHeight();	//Get map width and height, to set positions easier

		if (gameScreen == 0 || gameScreen==2||gameScreen==4 || gameScreen==5) {		//The menu screens

			paused = true;		//Pause the game, allowing the menu to be accessed without update method fully running

		}else if (gameScreen==1	|| gameScreen==3) {			//The game screens


			int coinXM1[] = {xTiles*8, xTiles*15, xTiles*41, xTiles*41};
			int coinYM1[] = {yTiles*6, yTiles*10, yTiles*3, yTiles*9};
			int coinXM2[] = {xTiles*35, xTiles*13, xTiles*35, xTiles*56};
			int coinYM2[] = {yTiles*3, yTiles*6, yTiles*9, yTiles*8};		//Arrays holding the X and Y positions of the coins(and also scorpions)
			int index = 0;


			for (Sprite s: coins)			//Loop through the coin sprites, giving them a position depending on the map
			{
				if (currMap == 0) {
					s.setX(coinXM1[index]);
					s.setY(coinYM1[index]);
					s.show();
				}else if (currMap == 1) {
					s.setX(coinXM2[index]);
					s.setY(coinYM2[index]);
					s.show();
				}else {
					System.out.println("Error loading the map");
				}
				index++;

			}

			index=1;			//Set index to 1 as I only want to print 3 scorpions
			for (Sprite s: scorpion)		//Loop through the scorpion sprites, giving them a position depending on the map
			{
				s.setVelocityX(-0.1f);		//Give enemies a velocity
				s.setVelocityY(0);
				s.show();

				if (currMap == 0) {			//Sets the positions
					s.setX(coinXM1[index]);
					s.setY(coinYM1[index]);
					s.show();
				}else if (currMap == 1) {
					s.setX(coinXM2[index]);
					s.setY(coinYM2[index]);
					s.show();
				}else {
					System.out.println("Error loading the map");
				}
				index++;
			}

			for (Sprite s: clouds)			//Gives the clouds a position and speed 
			{
				s.setX(screenWidth + (int)(Math.random()*200.0f));
				s.setY(30 + (int)(Math.random()*150.0f));
				s.setVelocityX(-0.02f);
				s.show();
			}


			dino1.setX(100);			//Gives main sprite, and goal sprite, a position (same on both maps)
			dino1.setY(390);
			dino1.setVelocityX(0);
			dino1.setVelocityY(0);
			dino1.show();


			dino2.setX(xTiles*61);
			dino2.setY(yTiles*9);
			dino2.show();

			paused = false;				//Unpauses the game
		}

	}



	/**
	 * Draw the current state of the game
	 */
	public void draw(Graphics2D g)
	{    


		if (gameScreen==0 || gameScreen==2 || gameScreen==4 || gameScreen==5) {	//The menu screens

			//SETS THE POSITIONS OF ALL THE MENU BUTTONS
			startButton.setX((getWidth()/4)+15);
			startButton.setY((getHeight()/2)+20);
			startButton.show();

			quitButton.setX((getWidth()/2)+10);
			quitButton.setY((getHeight()/2)+20);
			quitButton.show();

			uSize.setScale(0.6f);
			uSize.setX((getWidth()/10));
			uSize.setY((getHeight()/8));
			uSize.show();

			uJump.setScale(0.6f);
			uJump.setX((getWidth()/10));
			uJump.setY((getHeight()/8)*3);
			uJump.show();

			uSpeed.setScale(0.6f);
			uSpeed.setX((getWidth()/10));
			uSpeed.setY((getHeight()/8)*5);
			uSpeed.show();

			//SETS  BACKGROUND IMAGE
			g.drawImage(lvl1Image,0,0, null);


			//INITIALISE QUIT STRING TO BE USED ON EVERY MENU
			String quit = String.format("Quit");
			//INITIALISE FONTS TO BE USED
			Font newfont = new Font("SansSerif",Font.BOLD, 40);		//menu font
			Font newfont2 = new Font("SansSerif",Font.BOLD, 80);	//state title font
			Font newfont3 = new Font("SansSerif",Font.BOLD, 25);	//state title font

			//INITIALISE STRINGS TO BE USED FOR UPGRADE MENUS
			String title, start;
			String strSize = String.format("-SIZE");
			String strJump = String.format("+JUMP");
			String strSpeed = String.format("+SPEED");

			if (gameScreen==0) {		//initial menu

				//SETS COLOUR, DECLARE TITLE AND START BUTTON STRINGS
				g.setColor(Color.black);
				title = String.format("Dino Dash");
				start = String.format("Start Game");

			}else if (gameScreen ==2) {	//Completed level 1 (upgrade stage)

				//SETS COLOUR, FONT, AND UPGRADE STRING POSITIONS
				g.setColor(Color.black);
				g.setFont(newfont3);
				g.drawString(strSize, ((getWidth()/10)-15), (getHeight()/8)+60);
				g.drawString(strJump, ((getWidth()/10)-15), ((getHeight()/8)*3)+60);
				g.drawString(strSpeed, ((getWidth()/10)-15), ((getHeight()/8)*5)+60);

				//SETS POSITION FOR COIN SCORE STRING AND INTEGER, AND SETS POSITION
				g.setFont(newfont);
				String strCoins = String.format("+Score: %d", coinScore);
				g.drawString(strCoins, getWidth() -	250, 200);

				//SETS TITLE FOR UGRADE MENU AND LEVEL 2 BUTTON
				title = String.format("Upgrade");
				start = String.format("Level 2");

				//DRAW UPGRADE BUTTONS TRANSFORMED AS THEY ARE DIFFERENT SIZE TO OTHER BUTTONS
				uSize.drawTransformed(g);
				uJump.drawTransformed(g);
				uSpeed.drawTransformed(g);

			}else if (gameScreen ==4) {		//completed level 2 (can play level 1 again with upgrades)

				//DRAWS UPGRADE STRINGS AND COIN STRINGS
				g.setColor(Color.black);				
				g.setFont(newfont3);
				g.drawString(strSize, ((getWidth()/10)-15), (getHeight()/8)+60);
				g.drawString(strJump, ((getWidth()/10)-15), ((getHeight()/8)*3)+60);
				g.drawString(strSpeed, ((getWidth()/10)-15), ((getHeight()/8)*5)+60);
				g.setFont(newfont);
				String strCoins = String.format("+Score: %d", coinScore);
				g.drawString(strCoins, getWidth() -	250, 200);

				//SETS TITLE AND DRAWS UPGRADE BUTTONS
				title = String.format("Upgrade Again");
				uSize.drawTransformed(g);
				uJump.drawTransformed(g);
				uSpeed.drawTransformed(g);
				start = String.format("Level 1");

			}else if (gameScreen ==5) {		//Death screen

				//SET COLOUR TO RED AND DISPLAY DEATH MESSAGE, RESET COIN SCORE TO 0
				g.setColor(Color.red);
				title = String.format("You Have Died");
				coinScore=0;
				start = String.format("Try Again");

			}else {		//If there is an error
				start = String.format("ERROR");
				title = String.format("PLEASE RESTART GAME");
			}
			//SETS FONT, DISPLAY TITLE STRING, START AND QUIT STRINGS
			g.setFont(newfont2);
			g.drawString(title, (getWidth()/4), getHeight()/3);
			g.setFont(newfont);
			g.drawString(start, getWidth()/4, getHeight()/2);
			g.drawString(quit, getWidth()/2, getHeight()/2);

			//DRAW START AND QUIT BUTTONS
			startButton.draw(g);
			quitButton.draw(g);

		}else if (gameScreen==1	|| gameScreen==3){		//Game screen
			paused = false;		//Unpause the game

			int xo = 0;
			int yo = 0;

			// Adjust offset so that the screen follows the player
			xo = -(int)dino1.getX()+300;		
			// ...?
			if (gameScreen==1) {
				g.drawImage(lvl1Image,0,0, null);	//Level 1 background
			}else if (gameScreen==3) {
				g.drawImage(lvl1Image,0,0, null);	
				g.drawImage(lvl2Image,0,0, null);	//Level 2 background (layered on top of lvl 1 background as the sky can be seen through the holes in the cave)
			}

			// Apply offsets to sprites then draw them
			if (currMap ==0) {
				for (Sprite s: clouds)	//Draw clouds
				{
					s.setOffsets(xo,yo);
					s.draw(g);
				}
			}

			for (Sprite s: coins)		//Draw coins
			{
				s.setOffsets(xo,yo);
				s.draw(g);
			}

			for (Sprite s: scorpion)	//Draw scorpions
			{
				s.setOffsets(xo,yo);
				s.drawTransformed(g);
			}

			//DRAW MAIN SPRITE AND SET ITS SCALE
			dino1.setOffsets(xo, yo);
			dino1.setScale(scale);
			dino1.drawTransformed(g);

			//DRAW WIN CONDITION SPRITE
			dino2.draw(g);
			dino2.setOffsets(xo, yo);


			//APPLY OFFSET TO TILEMAP
			tmap.draw(g,xo,yo);    

			//SET SCORE STRING
			g.setColor(Color.black);
			String strCoins = String.format("+Score: %d", coinScore);


			g.setColor(Color.black);
			if(currMap==1) {
				g.setColor(Color.white);	//Set score colour to white if in the cave(lvl 2), to make it visible
			}
			g.drawString(strCoins, getWidth() -	250, 200);

			String strHealth = String.format("+%d", health);	//String to display health
			if (health<2) {
				g.setColor(Color.red);		//set health red if it is low
			}
			else if (currMap == 0){
				g.setColor(Color.black);
			}else if (currMap == 1){
				g.setColor(Color.white);
			}
			Font newfont = new Font("SansSerif",Font.BOLD, 40);
			g.setFont(newfont);
			g.drawString(strHealth, getWidth() - 130, 100);	//Print health, colour is dependent on map
		}else {
			System.out.println("Error loading game state");	//Error loading game state
		}
	}

	/**
	 * Update any sprites and check for collisions
	 * 
	 * @param elapsed The elapsed time between this call and the previous call of elapsed
	 */    
	public void update(long elapsed)
	{
		if (paused)return;		//Update doesnt fully run if game is paused

		// Make adjustments to the speed of the sprite due to gravity
		dino1.setVelocityY(dino1.getVelocityY()+(gravity*elapsed)*5.0f);

		dino1.setAnimationSpeed(1.0f);
		dino2.setAnimationSpeed(1.0f);		//set animation speed of dino sprites

		for (Sprite s: clouds)	//update clouds
			s.update(elapsed);

		for (Sprite s: coins) {	//update coins
			s.update(elapsed);
			if (boundingBoxCollision(dino1, s)) {	//if player is touching coin, add to score and play noise

				if (s.isVisible()) {
					s.hide();
					coinScore++;
					Sound d = new Sound("sounds/complete1.wav", currMap);
					d.start();
				}
			}
		}

		for (Sprite s: scorpion) {	//update the scorpions

			s.setVelocityY(s.getVelocityY()+(gravity*elapsed));
			s.update(elapsed);

			if (boundingBoxCollision(dino1, s) && s.isVisible()) {	//if player is touching a scorpion
				//LOWER HEALTH, PLAY DAMAGE ANIMATION AND KNOCK SPRITE BACK
				if (easy == false) {
					health = health-1;
				}
				dino1.setVelocity(-0.3f, -0.1f);
				dino1.setAnimation(damage);			
				if (health<=0) {	//Death sound
					Sound d = new Sound("sounds/gameEnd.wav", currMap);
					d.start();
				}
				else {				//Damage sound
					Sound d = new Sound("sounds/item.wav", currMap);
					d.start();
				}

				s.stop();
				s.hide();	//Hide the scorpion (it had been killed)
			}
			handleScreenEdge(s, tmap, elapsed);		//Check if off screen
			checkTileCollision(s, tmap, true);		//Check if touching tiles
		}

		// Now update the sprites animation and position
		dino1.update(elapsed);
		dino2.update(elapsed);

		// Then check for any collisions that may have occurred
		handleScreenEdge(dino1, tmap, elapsed);
		checkTileCollision(dino1, tmap, false);


		if (boundingBoxCollision(dino1, dino2)){	//If touching dino2 (win condition)
			if (dino2.isVisible()) {
				//PLAY VICTORY SOUND, UPDATE GAME SCREEN AND CALL INITALISE TO GET TO NEXT MENU SCREEN
				dino2.hide();
				Sound d = new Sound("sounds/complete2.wav", currMap);
				d.start();
				gameScreen++;
				initialiseGame();
			}

		}

		//IF PLAYER HAS DIED, PAUSE GAME, SET TO DEATH MENU SCREEN, SAVE REFERENCE TO PREVIOUS STATE SO PLAYER CAN RESTART THE LEVEL
		if (health<=0) {
			paused = true;
			prevState = gameScreen;
			gameScreen=5;
			initialiseGame();

		}
	}


	/**
	 * Checks and handles collisions with the edge of the screen
	 * 
	 * @param s			The Sprite to check collisions for
	 * @param tmap		The tile map to check 
	 * @param elapsed	How much time has gone by since the last call
	 */
	public void handleScreenEdge(Sprite s, TileMap tmap, long elapsed)
	{
		// This method just checks if the sprite has gone off the bottom screen.
		// Ideally you should use tile collision instead of this approach

		if (s.getY() + s.getHeight() > tmap.getPixelHeight())
		{
			// Put the player back on the map 1 pixel above the bottom
			s.setY(tmap.getPixelHeight() - s.getHeight() - 1); 

			// and make them bounce
			s.setVelocityY(-s.getVelocityY());
		}
	}



	/**
	 * Override of the keyPressed event defined in GameCore to catch our
	 * own events
	 * 
	 *  @param e The event that has been generated
	 */
	public void keyPressed(KeyEvent e) 
	{ 
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_ESCAPE) stop();	//Esc will stop game

		//IF RIGHT KEY, MAKE SPRITE RUN( + UPGRADE AMOUNT) TO THE RIGHT WITH RUNNING ANIMATION
		if (key == KeyEvent.VK_RIGHT) {
			dino1.setVelocityX(0.15f + plusSpeed);
			dino1.setAnimation(run);
		}
		//IF LEFT KEY, MAKE SPRITE RUN( + UPGRADE AMOUNT) TO THE LEFT WITH RUNNING ANIMATION
		if (key == KeyEvent.VK_LEFT) {
			dino1.setVelocityX((-0.15f- plusSpeed));
			dino1.setAnimation(run);
		}
		//IF UP KEY PRESSED AND SPRITE IS IN A POSITION THAT THEY CAN JUMP
		if (key == KeyEvent.VK_UP && canJump==true) {
			dino1.setAnimation(idle);				//stop running
			dino1.setVelocityY(-0.3f - plusJump);	//Set jump velocity plus upgrade amount
			canJump = false;						//Doesn't allow sprite to jump again until they have touched the ground
		}
		if (key == KeyEvent.VK_F) {

			setSize(screenWidth,screenHeight);
			DisplayMode Modes[] = screen.getCompatibleDisplayModes();
			DisplayMode goodMode = screen.findFirstCompatibleMode(Modes);
			screen.setFullScreen(goodMode);
			setVisible(true);
			screen.update();						//Doesn't allow sprite to jump again until they have touched the ground
		}
		if (key == KeyEvent.VK_C) {
			screen.restoreScreen();
		}
		if (key == KeyEvent.VK_E) {			//Change to easy mode
			if (easy == true) {
				easy=false;
			}else {
				easy = true;
			}
			Sound d = new Sound("sounds/easy.wav", currMap);
			d.start();
		}
	}

	public boolean boundingBoxCollision(Sprite s1, Sprite s2)
	{
		//if s2 is on right of s1
		return ((s1.getX() + s1.getImage().getWidth(null) > s2.getX()) &&		//gets s1 x position, and adds its width, checks if that is greater than s2's x position (checks if touching right side of sprite) (position is top left corner of the image)
				//if s2 is on left of s1
				(s1.getX() < (s2.getX() + s2.getImage().getWidth(null))) &&		//gets s1 x position, checks if its less than s2 x position + sprite width (checks if its touching left side of sprite)
				//if s2 is below s1
				((s1.getY() + s1.getImage().getHeight(null) > s2.getY()) &&		//gets s1 y position and adds to height (checking bottom left of sprite) if its greater that the s2 sprite position (if its on top of it)
						//if s2 is on top of s1		
						(s1.getY() < s2.getY() + s2.getImage().getHeight(null))));	//get s1 y position, checks if its less than s2 y position plus image height (if s2 is on top of s1) 
	}

	/**
	 * Check and handles collisions with a tile map for the
	 * given sprite 's'. Initial functionality is limited...
	 * 
	 * @param s			The Sprite to check collisions for
	 * @param tmap		The tile map to check 
	 */

	public void checkTileCollision(Sprite s, TileMap tmap, boolean npc)	//add a boolean that checks if its an npc otherwise every sprite will set canJump to true 
	{
		// Take a note of a sprite's current position
		float sx = s.getX();
		float sy = s.getY();

		// Find out how wide and how tall a tile is
		float tileWidth = tmap.getTileWidth();
		float tileHeight = tmap.getTileHeight();

		//TOP LEFT
		// Divide the sprite’s x coordinate by the width of a tile, to get
		// the number of tiles across the x axis that the sprite is positioned at 
		int	xtile = (int)(sx / tileWidth);
		// The same applies to the y coordinate
		int ytile = (int)(sy / tileHeight);

		// What tile character is at the top left of the sprite s?
		char ch = tmap.getTileChar(xtile, ytile);

		if (ch != '.') // If it's not a dot (empty space), handle it
		{
			if (npc == false) {		//If its the playable character
				s.setVelocityX(0);	
				s.setVelocityY(0);	//Stop player x and y motion
				s.shiftX(2.0f);		
				s.shiftY(2.0f);		//Stop sprite going through tiles
			}else if (npc == true) {	//If its another sprite (scorpion)
				s.setVelocityX(-s.getVelocityX());	//Reverse velocity to move other direction
				s.setY((s.getY()+1));				//Stop scorpion climbing up blocks
			}
		}

		
		//TOP RIGHT
		xtile = (int)((sx + s.getWidth())/tileWidth);
		ytile = (int)(sy/ tileHeight);

		ch = tmap.getTileChar(xtile, ytile);

		if (ch != '.') // If it's not a dot (empty space), handle it
		{
			if (npc == false) {
				s.setVelocityX(0);
				s.setVelocityY(0);		//Stop player x and y motion
				s.shiftX(-2.0f);	
				s.shiftY(2.0f);			//Stop sprite going through tiles
			}else if (npc == true) {
				s.setVelocityX(-(s.getVelocityX()));
				s.setY((s.getY()+1));
			}
		}


		//BOTTOM LEFT
		xtile = (int)(sx / tileWidth);
		ytile = (int)((sy + s.getHeight())/ tileHeight);
		ch = tmap.getTileChar(xtile, ytile);

		if (ch=='p') {
			s.setVelocityY(0);	//Stops player falling through tiles
			s.setY(s.getY()-1);	//Keep player above ground tile
			if (npc == false) {
				canJump = true;		//If back on the ground, the player can jump again
			}
		}


		//BOTTOM RIGHT
		xtile = (int)((sx + s.getWidth())/ tileWidth);
		ytile = (int)((sy + s.getHeight())/ tileHeight);
		ch = tmap.getTileChar(xtile, ytile);

		if (ch == 'p') 	
		{
			s.setVelocityY(0);		//Stops player falling through tiles
			s.setY(s.getY()-1);		//Keep player above ground tile
			if (npc == false) {
				canJump = true;			//If back on the ground, the player can jump again
			}
		}
	}

	public void mouseClicked(MouseEvent e){		//Method for if the mouse is clicked
		int x=e.getX(); 
		int y=e.getY();		//gets the coordinates that was clicked

		//INITIALISE MENU SOUNDS
		Sound d = new Sound("sounds/upgrade2.wav", currMap);
		Sound d2 = new Sound("sounds/upgrade.wav", currMap);
		Sound d3 = new Sound("sounds/click.wav", currMap);

		//IF STARTBUTTON IS CLICKED WHILE IT IS VISIBLE (ON THE MENU SCREEN)
		if (startClicked(x, y) && startButton.isVisible()) {
			startButton.hide();
			quitButton.hide();		//Hide the buttons so it can transition to next stage

			d3.start();				//Play click sound

			if (gameScreen==0){			//First menu - unpause game and move to next game screen
				gameScreen++;
				paused=false;
			}else if (gameScreen==2){	//2nd menu - unpause game, move to next map, and move to next game screen
				currMap++;
				gameScreen++;
				paused=false;
			}
			else if (gameScreen==4){	//3rd menu - unpause game, move to first map, and move to first game stage
				gameScreen=1;
				currMap=0;
				paused=false;

			}
			else if (gameScreen==5){	//Death menu - Loads the previously saved state (map user died on) and unpause game
				gameScreen=prevState;
				paused=false;
			}
			initialiseGame();			//Initialise to the next stage
		}
		else if (quitClicked(x, y) && startButton.isVisible()) {	//Quit game if quit button clicked, set game screen back to 0
			gameScreen=0;
			stop();
		}else if (sizeClicked(x,y)&&uSize.isVisible()) {			//If size upgrade clicked
			if (coinScore>0) {		//Spend coins to decrease player size, can run out of coins
				d.start();
				scale = scale - 0.05f;		//Small increments 
				coinScore--;
			}else{
				d2.start();			//Sound if the player tries to upgrade without coins
			}	
		}else if (jumpClicked(x,y)&&uJump.isVisible()) {			//If size upgrade clicked
			if (coinScore>0) {		//Spend coins to increase jump height, can run out of coins 
				d.start();			//play upgrade noise
				plusJump = plusJump+0.05f;
				coinScore--;
			}else{
				d2.start();			//Sound if the player tries to upgrade without coins
			}
		}else if (speedClicked(x,y)&&uSpeed.isVisible()) {			//If size upgrade clicked
			if (coinScore>0) {		//Increase the speed
				d.start();
				plusSpeed = plusSpeed + 0.05f;
				coinScore--;
			}else{
				d2.start();			//Sound if the player tries to upgrade without coins
			}
		}
	}

	public boolean startClicked(int x, int y) {		//If the click is within the button sprite
		//If click is on the left of button
		return ((startButton.getX() + startButton.getImage().getWidth(null) > x) &&		
				//if click is on the right of button
				(startButton.getX() < x) &&
				//if click is above button
				((startButton.getY() + startButton.getImage().getHeight(null) > y)&&			
						//if click is below button
						(startButton.getY() < y)));	
	}
	public boolean quitClicked(int x, int y) {
		return ((quitButton.getX() + quitButton.getImage().getWidth(null) > x) &&		
				(quitButton.getX() < x) &&
				((quitButton.getY() + quitButton.getImage().getHeight(null) > y)&&
						(quitButton.getY() < y)));	
	}
	public boolean sizeClicked(int x, int y) {
		return ((uSize.getX() + uSize.getImage().getWidth(null) > x) &&		
				(uSize.getX() < x) &&
				((uSize.getY() + uSize.getImage().getHeight(null) > y)&&
						(uSize.getY() < y)));	
	}
	public boolean jumpClicked(int x, int y) {
		return ((uJump.getX() + uJump.getImage().getWidth(null) > x) &&
				(uJump.getX() < x) &&
				((uJump.getY() + uJump.getImage().getHeight(null) > y)&&
						(uJump.getY() < y)));	
	}
	public boolean speedClicked(int x, int y) {
		return ((uSpeed.getX() + uSpeed.getImage().getWidth(null) > x) &&
				(uSpeed.getX() < x) &&
				((uSpeed.getY() + uSpeed.getImage().getHeight(null) > y)&&
						(uSpeed.getY() < y)));	
	}

	public void keyReleased(KeyEvent e) { 

		int key = e.getKeyCode();

		// Switch statement instead of lots of ifs...
		// Need to use break to prevent fall through.
		switch (key)
		{
		case KeyEvent.VK_RIGHT :dino1.setVelocityX(0);dino1.setAnimation(idle);	break;
		case KeyEvent.VK_LEFT : dino1.setVelocityX(0);dino1.setAnimation(idle);	break;
		case KeyEvent.VK_ESCAPE : stop(); break;
		case KeyEvent.VK_UP     : break;
		default :  break;
		}
	}

}
