/*
Stephen Burgess
 Goblin Army
 Computer Science I Final
 Fall 2012
 */

// Sound, Fonts and Images
import ddf.minim.*;

Minim sound;
AudioPlayer music, grunt, snarl, monster, cheering, creak, boo;

PFont font, fontHP;

PImage grass, grassS, road, roadS, water, rock,
        tree, tree1, tree2, tree3,
        home, hro, chest, goblin, goblinKing, goblinKing1;

// Grid Display
int gridX, gridY;
GridSq[][] grid;
String[][] types;

// Game Scenarios
boolean fail, win, boss;

boolean fight;

int lastBoss, timer;

// Players and Objects
Hero hero;
int xBox, yBox;
int posX, posY;

int numbOfChests;
Chest[] chst;

int numbOfGobs;
Goblin[] gob;

int goblinSpeed;


void setup() {
  size(1000, 500);
  background(255);
  stroke(1);
  smooth();
  frameRate(18);
  
  // Sets Grid Size
  gridX = 20;
  gridY = 10;
  grid = makeGrid(new GridSq[gridX][gridY]);
  
  // Sets Hero
  xBox = 1;
  yBox = 4;
  posX = xBox * 50;
  posY = yBox * 50;
  hero = new Hero(xBox,yBox);
  
  // Sets intial shown squares
  for (int i = -1; i<2; i++) {
    for (int j = -1; j<2; j++) {
      if ((xBox + i >= 0 && xBox +i < gridX)
          && (yBox + j >= 0 && yBox + j < gridY)) {
        grid[xBox + i][yBox + j].shown = true;
      }
    }
  }  
  
  // Sets Chests
  numbOfChests = 4;
  chst = new Chest[numbOfChests];
  chst[0] = new Chest(0, 18, 1, true);
  chst[1] = new Chest(1, 10,7, false);
  chst[2] = new Chest(2, 2,9, false);
  chst[3] = new Chest(3, 12,1, false);
  
  for(int i = 0; i < numbOfChests; i++) {
    grid[chst[i].xBox][chst[i].yBox].chest = i;
  }
  
  // Sets Goblins
  boss = false;
  numbOfGobs = 9;
  gob = new Goblin[numbOfGobs];
  gob[0] = new Goblin(0, 18, 2, true);
  gob[1] = new Goblin(1, 10, 6, true);
  gob[2] = new Goblin(2, 2, 8, true);
  gob[3] = new Goblin(3, 13, 1, true);
  gob[4] = new Goblin(4, 0, 6, false);
  gob[5] = new Goblin(5, 5, 1, false);
  gob[6] = new Goblin(6, 7, 3, false);
  gob[7] = new Goblin(7, 17,7, false);
  gob[8] = new Goblin(8, 15,2, false);
    
  goblinSpeed = 100;
    
  for(int i = 0; i<numbOfGobs; i++) {
    grid[gob[i].xBox][gob[i].yBox].goblin = i;
  }

  // Font Load
  font = loadFont("DejaVuSans-36.vlw");
  fontHP = loadFont("Helvetica-14.vlw");
  textFont(font);

  // Image Loads
  grass = loadImage("pde/BaliBrawl/data/grass.jpg");
  grassS = loadImage("pde/BaliBrawl/data/grassS.jpg");
  road = loadImage("pde/BaliBrawl/data/road.jpg");
  roadS = loadImage("pde/BaliBrawl/data/roadS.jpg");
  tree1 = loadImage("pde/BaliBrawl/data/tree1.jpg");
  tree2 = loadImage("pde/BaliBrawl/data/tree2.jpg");
  tree3 = loadImage("pde/BaliBrawl/data/tree3.jpg");
  rock = loadImage("pde/BaliBrawl/data/rock.jpg");
  home = loadImage("pde/BaliBrawl/data/home.jpg");
  hro = loadImage("pde/BaliBrawl/data/hero.png");
  chest = loadImage("pde/BaliBrawl/data/chest.png");
  goblin = loadImage("pde/BaliBrawl/data/goblin.png");
  goblinKing = loadImage("pde/BaliBrawl/data/goblinking.png");
  goblinKing1 = loadImage("pde/BaliBrawl/data/goblinking2.png");
  water = loadImage("pde/BaliBrawl/data/water.jpg");
  
  // Sound Loads
  sound = new Minim(this);
  grunt = sound.loadFile("pde/BaliBrawl/data/grunt.mp3");
  snarl = sound.loadFile("pde/BaliBrawl/data/snarl.mp3");
  monster = sound.loadFile("pde/BaliBrawl/data/growl.mp3");
  music = sound.loadFile("pde/BaliBrawl/data/music.mp3");
  cheering = sound.loadFile("pde/BaliBrawl/data/cheering.mp3");
  creak = sound.loadFile("pde/BaliBrawl/data/creak.mp3");
  boo = sound.loadFile("pde/BaliBrawl/data/boo.mp3");
  
  
  // Riddle Setup
  timer = 0;
  lastBoss = -1;
  fight = false;

  
  music.play();
}

void draw () {
  
  if(!win && !fail) {
     drawBoard();
     
     fill(255);
     drawSprites();

     whereDaMouse();

    textFont(font);
    text("Health: " + hero.hp, 10, 40);
    text("Power Up: " + hero.level, 10, 80);
    } else {
      
      if (win) {
        text("You Win!", width/2-115, height/2+30);
        music.pause();
        cheering.play();
        while(k<500){k++;}
        exit();
      } else if (fail) {
        music.pause();
        boo.play();
        
        text("You Lose!", width/2-115, height/2+30);
        while(k<500){k++;}
        exit();
      }
    }
    
    
      
}


void drawBoard() {
  String charT = "A";
  for (int i =0;i<20;i++) {
    for (int j = 0;j<10;j++) {
      if (!grid[i][j].shown) {
        fill(0);
        rect((int)(i*50), j*50, 50, 50);
      } else {
      charT = grid[i][j].type;
      if (charT == "G") {
        image(grass, (i*50), (j*50));
      } else if (charT == "R") {
        image(road, i*50, j*50);
      } else if (charT == "T") {
        image(tree1, i*50, j*50);
      } else if (charT == "U") {
        image(tree2, i*50, j*50);
      } else if (charT == "V") {
        image(tree3, i*50, j*50);
      } else if (charT == "K") {
        image(rock, i*50, j*50);
      } else if (charT == "W") {
        image(water, i*50, j*50);
      } else if (charT == "H") {
        image(home, i*50, j*50);
      } else {
        println("Unexpected scenario");
      } }
    }
  }
}

// @param GridSq[][]
// @return GridSq[][]
GridSq[][] makeGrid(GridSq[][] g) {
  String cTemp;
  
  /*
  The chart for the layout of the grid squares is flipped.
  Box 0,0 remains the same, but 1,0 is down one instead of
  right one. This is because of the way the double array is
  initialized. Legend:
  T, U, V = tree
  G = grass
  R = road
  K = rock
  W = water
  */
  String[][] t = { { "T", "T", "G", "G", "H", "G", "G", "G", "G", "T" },
                 { "U", "V", "G", "G", "R", "G", "G", "K", "T", "U" },
                 { "T", "T", "V", "G", "R", "G", "G", "G", "G", "G" },
                 { "T", "V", "K", "G", "R", "G", "G", "G", "G", "T" },
                 { "V", "G", "G", "G", "R", "G", "G", "K", "G", "G" },
                 { "T", "G", "G", "R", "G", "G", "G", "G", "G", "G" },
                 { "G", "R", "R", "G", "R", "U", "G", "G", "G", "V" },
                 { "R", "G", "G", "G", "K", "R", "G", "G", "G", "W" },
                 { "G", "T", "G", "G", "G", "R", "G", "G", "T", "W" },
                 { "U", "T", "V", "G", "R", "G", "K", "K", "W", "W" },
                 { "T", "U", "K", "G", "R", "G", "G", "G", "K", "W" },
                 { "T", "T", "T", "V", "R", "K", "K", "K", "W", "W" }, 
                 { "K", "G", "V", "G", "R", "G", "K", "W", "W", "W" },
                 { "U", "G", "T", "R", "G", "G", "G", "W", "W", "K" },
                 { "T", "G", "R", "G", "G", "T", "G", "G", "W", "W" },
                 { "G", "R", "G", "R", "K", "G", "G", "G", "G", "W" },
                 { "R", "V", "V", "G", "R", "G", "G", "K", "G", "W" },
                 { "T", "U", "T", "V", "R", "K", "G", "G", "W", "W" },
                 { "T", "G", "G", "R", "G", "G", "K", "W", "W", "W" },
                 { "U", "T", "K", "G", "G", "G", "G", "G", "V", "W" },               
             };
  
  /*
  cTemp (character temp) is used so you only need to call the
  character once to check against conditions. Nested loop that
  checks the square type and initilizes it as movable or not.
  */
  for (int i = 0; i<gridX; i++){
    for (int j = 0; j<gridY; j++) {

      cTemp = t[i][j];
      if (cTemp == "G" || cTemp == "R") {
        g[i][j] = new GridSq(cTemp, true, -1, -1);
      } else if (cTemp == "K" || cTemp == "T" || 
                 cTemp == "U" || cTemp == "V" ||
                 cTemp == "H" || cTemp == "W") {
        g[i][j] = new GridSq(cTemp, false, -1, -1);
      } else {
        println("Error, condition for which is unaccounted.");
      }
    } 
  }
  return g;
}


void drawSprites() {
  
  // Sets small font for HP display
  textFont(fontHP);
  
  // Draws hero
  image(hro, posX, posY);
  text(hero.hp, posX+12, posY);
  
  // Draws Chests
  for(int i = 0; i < numbOfChests; i++) {
    if (grid[chst[i].xBox][chst[i].yBox].shown && 
        grid[chst[i].xBox][chst[i].yBox].chest == i) {
        image(chest, chst[i].posX, chst[i].posY);
      
    }
  }
  
  // Draws Goblins and HP
  if (grid[gob[0].xBox][gob[0].yBox].shown && 
    grid[gob[0].xBox][gob[0].yBox].goblin == 0) {
    text(gob[0].hp, gob[0].posX+12, gob[0].posY);
    image(goblinKing1, gob[0].posX, gob[0].posY);
  }
  if (grid[gob[1].xBox][gob[1].yBox].shown && 
      grid[gob[1].xBox][gob[1].yBox].goblin == 1) {
    text(gob[1].hp, gob[1].posX+12, gob[1].posY);
    image(goblinKing, gob[1].posX, gob[1].posY);
  }
  if (grid[gob[2].xBox][gob[2].yBox].shown && 
      grid[gob[2].xBox][gob[2].yBox].goblin == 2) {
    text(gob[2].hp, gob[2].posX+12, gob[2].posY);
    image(goblinKing, gob[2].posX, gob[2].posY);
  }
 if (grid[gob[3].xBox][gob[3].yBox].shown && 
     grid[gob[3].xBox][gob[3].yBox].goblin == 3) {
    text(gob[3].hp, gob[3].posX+12, gob[3].posY);
    image(goblinKing, gob[3].posX, gob[3].posY);
  }
  for(int i = 4; i < numbOfGobs; i++) {
    if (grid[gob[i].xBox][gob[i].yBox].shown && 
        grid[gob[i].xBox][gob[i].yBox].goblin == i) {
      if (random(goblinSpeed) > goblinSpeed - 1) {
        moveGoblin(gob[i]);
      }
      text(gob[i].hp, gob[i].posX+12, gob[i].posY);
      image(goblin, gob[i].posX, gob[i].posY);
    }
  }
}

// Locates mouse position and highlights squares adjacent to hero
// when mouseover.
void whereDaMouse() {
  
  String tempType;

  for (int i = -1; i < 2; i++) {
    for (int j = -1; j < 2; j++) {
      if (!(i == 0 && j == 0)) {
        if ((mouseX > posX + (50*i) && mouseX < posX + (50*i + 50)
            && mouseY > posY + (50*j) && mouseY< posY + (50*j + 50) 
            && grid[xBox+i][yBox+j].movable)) {
          tempType = grid[xBox+i][yBox+j].type;
          // Highlights movable squares 
          if(tempType == "G") {
            image(grassS, posX + i*50, posY + j*50);
          } else if (tempType == "R") {
            image(roadS, posX + i*50, posY + j*50);
          } else {
            println("Unexpected scenario encountered mouseover");
          }
          // Draws goblins over movable squares
          if (grid[xBox + i][yBox + j].goblin >= 0) {
            if (grid[xBox + i][yBox + j].goblin < 4) {
              if (grid[xBox + i][yBox + j].goblin == 0) {
                image(goblinKing1, posX + i*50, posY + j*50);
              }else {
                image(goblinKing, posX + i*50, posY + j*50);
              }
            } else {
              image(goblin, posX + i*50, posY + j*50);
            }
          }
          // Draws chests
          if (grid[xBox + i][yBox + j].chest  >= 0) {
            image(chest, posX + i*50, posY + j*50);
          }
        }
      }
    }
  }
}

// Checks mouse position after mouseClicked. If square is movable,
// moves hero. If square has goblin or chest, calls relevant method.
void mouseClicked() {
  
     
  for (int i = -1; i < 2; i++) {
    for (int j = -1; j < 2; j++) {
      if (!(i == 0 && j == 0)) {
        if (mouseX > posX + i*50 && mouseX < posX + i*50 + 50
            && mouseY > posY + j*50 && mouseY < posY + j*50 + 50
            && grid[xBox+i][yBox+j].movable)
          if (grid[xBox + i][yBox + j].goblin < 0 &&
              grid[xBox + i][yBox + j].chest < 0) {
                // Moves hero
            xBox += i;
            yBox += j;
            for (int k = -1; k<2; k++) {
              for (int l = -1; l<2; l++) {
                if ((xBox + k >= 0 && xBox +k < gridX)
                    && (yBox + l >= 0 && yBox + l < gridY))
                  grid[xBox + k][yBox + l].shown = true;
              }
            }  
          } else if (grid[xBox + i][yBox + j].goblin >= 0) {
            // Calls fightGoblin
            fightGoblin(grid[xBox + i][yBox + j].goblin, xBox + i, yBox + j);
          } else if (grid[xBox + i][yBox + j].chest >= 0) {
            // Calls openChest
            openChest(grid[xBox + i][yBox + j].chest, xBox + i, yBox + j);
          }
        }
    }
  }        
    // Sets hero location
    posX = xBox * 50;
    posY = yBox * 50;
    
    hero.xBox = xBox;
    hero.yBox = yBox;
}

// Has minor goblins move randomly
void moveGoblin(Goblin g) {
  // stores previous goblin coordinates
  int newGobx = g.xBox, newGoby = g.yBox;
  
  // moves goblin randomly between -1 and 1
  newGobx += (int) random(-2,2);
  
  // for staying within bounds
  if (newGobx >= gridX) {
    newGobx = gridX-1;
  }else if (newGobx < 0) {
    newGobx = 1;
  }
  
  newGoby += (int) random(-2,2);
  
  if (newGoby >= gridY) {
    newGoby = gridY - 1;
  } else if(newGoby < 0) {
    newGoby = 1;
  }
  
  // if new location is movable and there is no goblin
  if (grid[newGobx][newGoby].movable && 
      grid[newGobx][newGoby].goblin < 0 &&
      (newGobx != xBox && newGoby != yBox)) {
    // set new square for goblin at location
      grid[g.xBox][g.yBox].goblin = -1;
      grid[newGobx][newGoby].goblin = g.number;
      g.xBox = newGobx;
      g.yBox = newGoby;
      g.posX = newGobx * 50;
      g.posY = newGoby * 50;
      
  }
}

// Opens chest
void openChest(int c, int x, int y) {
  // Final chest sets win
  if(c == 0) {
    win = true;
  } else {
    creak.play();
    creak.rewind();
    // Prize is either health or power up
    int prize = round(random(0,2));
    if (prize == 1) {
      hero.level += round(random(1,3));
    } else {
      hero.hp += round(random(6,14));
    }
    // Sets grid as opened
    grid[x][y].chest = -1;
    chst[c].closed = false;
  }
}

void fightGoblin(int g, int x, int y) {
  // Boss fight condition
  if (g < 4) {
    fightGoblinKing(g, x, y);
  } else if (g < 0) { // error check built in
    println("why is this happening?");
  }else {
    
    // Damage done by hero
    int damDone = round(random(0,4) + hero.level);
    gob[g].hp -= damDone;
        
    // Damage taken by hero
    int damTake = round(random(0,2));
    hero.hp -= damTake;

    // Plays grunt when hero takes damage
    if(damTake > 0) {
      grunt.play();
      grunt.rewind();
    }
    
    // Plays snarl when goblin takes damage
    if(damDone > 0) {
      snarl.play();
      snarl.rewind();
    }
    // Game lost if heros health falls to 0
    if (hero.hp <= 0) {
      fail = true;
    } 
    // Goblin defeated
    else if (gob[g].hp <= 0) {
      grid[x][y].goblin = -1;
      
      // Gain health and/or power up
      hero.level += round(random(0,2));
      hero.hp += round(random(0,6));
    }
  }
}

void fightGoblinKing(int g, int x, int y) {
  // Plays monster sound once per boss
  if (!boss) {
    monster.play();
    monster.rewind();
    boss = true;
    lastBoss = g;
  }
  
  int damDone = round(random(0,3) + hero.level);
  gob[g].hp -= damDone;
  
  int damTake = round(random(0,3));
  hero.hp -= damTake;

   if(damTake > 0) {
      grunt.play();
      grunt.rewind();
    }
  if (hero.hp <= 0) {
    fail = true;
  } else if (gob[g].hp <= 0) {
    grid[x][y].goblin = -1;
    boss = false;
    
    hero.level += round(random(0,2));
    hero.hp += round(random(0,6));
  }
}


