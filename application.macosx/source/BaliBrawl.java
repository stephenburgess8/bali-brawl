import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BaliBrawl extends PApplet {

/*
Stephen Burgess
 Goblin Army
 Computer Science I Final
 Fall 2012
 */

// Sound, Fonts and Images


Minim sound;
AudioPlayer music, grunt, snarl, monster, cheering, creak, boo;

PFont font, fontHP;

PImage grass, grassS, road, roadS, water, rock,
        tree, tree1, tree2, tree3,
        home, hro, chest, goblin, goblinKing, goblinKing1,
        fightImg, riddleImg;

// Grid Display
int gridX, gridY;
GridSq[][] grid;
char[][] types;

// Game Scenarios
boolean fail, win, boss;

boolean fightOrRid, fight, riddle, wrong, correct;
String riddles[],ridSols[];
String fightOrRiddle, riddle1, riddle2, answer;
String rid1Sol, rid2Sol;

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


public void setup() {
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
  grass = loadImage("grass.jpg");
  grassS = loadImage("grassS.jpg");
  road = loadImage("road.jpg");
  roadS = loadImage("roadS.jpg");
  tree1 = loadImage("tree1.jpg");
  tree2 = loadImage("tree2.jpg");
  tree3 = loadImage("tree3.jpg");
  rock = loadImage("rock.jpg");
  home = loadImage("home.jpg");
  hro = loadImage("hero.png");
  chest = loadImage("chest.png");
  goblin = loadImage("goblin.png");
  goblinKing = loadImage("goblinking.png");
  goblinKing1 = loadImage("goblinking2.png");
  water = loadImage("water.jpg");
  fightImg = loadImage("fight.png");
  riddleImg = loadImage("riddle.png");
  
  // Sound Loads
  sound = new Minim(this);
  grunt = sound.loadFile("grunt.mp3");
  snarl = sound.loadFile("snarl.mp3");
  monster = sound.loadFile("growl.mp3");
  music = sound.loadFile("music.mp3");
  cheering = sound.loadFile("cheering.mp3");
  creak = sound.loadFile("creak.mp3");
  boo = sound.loadFile("boo.mp3");
  
  
  // Riddle Setup
  timer = 0;
  wrong = false;
  correct = false;
  lastBoss = -1;
  answer = "";
  fightOrRid = false;
  fight = false;
  riddle = false;
  fightOrRiddle = "Fight or Riddle?";
  riddles = new String[5];
  riddles[0] = "What is the answer to the ultimate\n"
              +"question of life, the universe\n"
              +"and everything?";
  riddles[1] = "The man who invented it doesn't want it.\n"
              +"The man who bought it doesn't need it.\n"
              +"The man who needs it doesn't know it.\n"
              +"What is it?  A: a... "; 
  riddles[4] = "What uses four legs at dawn, two legs\n" 
              +"at midday and three legs at twilight?";
          
  riddles[3] = "I'm struck and cut, shaped and cooled,\n"
              +"then bound by rings to release what's\n"
              +"stored. I am a...";
  riddles[2] = "What is the next number in the series:\n"
              +"9 2 7 4 5 6 3 8 __ ?";
  
  ridSols = new String[5];
  ridSols[0] = "@j1(+[#F4+.Z(2";
  ridSols[1] = "coffin";
  ridSols[4] = "man";
  ridSols[3] = "key";
  ridSols[2] = "1";
  
  
  music.play();
}

public void draw () {
  
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
      } else if (fail) {
        music.pause();
        boo.play();
        text("You Lose!", width/2-115, height/2+30);
      }
    }
    
    if (fightOrRid) {
      text(fightOrRiddle, width/2 - 110, height/2-20);
      image(fightImg, 370, 290);
      image(riddleImg, 560, 290);
    }else if(riddle) {
          
      text(riddles[lastBoss], width/2-300, height/2-50);
      text(answer+(frameCount/10 % 2 == 0 ? "_" : ""), width/2+10, height/2+110);
    }else if(wrong) {
      timer++;
      text("Wrong!", width/2-100, height/2-20);
      if (timer > frameRate*3) {
        wrong = false;
        timer = 0;
      }
    } else if (correct) {
      timer++;
      text("Right!", width/2-100, height/2-20);
      if (timer > frameRate*3) {
        correct = false;
        timer = 0;
      }
    }
    
      
}


public void drawBoard() {
  char charT = 'A';
  for (int i =0;i<20;i++) {
    for (int j = 0;j<10;j++) {
      if (!grid[i][j].shown) {
        fill(0);
        rect((int)(i*50), j*50, 50, 50);
      } else {
      charT = grid[i][j].type;
      if (charT == 'G') {
        image(grass, (i*50), (j*50));
      } else if (charT == 'R') {
        image(road, i*50, j*50);
      } else if (charT == 'T') {
        image(tree1, i*50, j*50);
      } else if (charT == 'U') {
        image(tree2, i*50, j*50);
      } else if (charT == 'V') {
        image(tree3, i*50, j*50);
      } else if (charT == 'K') {
        image(rock, i*50, j*50);
      } else if (charT == 'W') {
        image(water, i*50, j*50);
      } else if (charT == 'H') {
        image(home, i*50, j*50);
      } else {
        println("Unexpected scenario");
      } }
    }
  }
}

// @param GridSq[][]
// @return GridSq[][]
public GridSq[][] makeGrid(GridSq[][] g) {
  char cTemp;
  
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
  char[][] t = { { 'T', 'T', 'G', 'G', 'H', 'G', 'G', 'G', 'G', 'T' },
                 { 'U', 'V', 'G', 'G', 'R', 'G', 'G', 'K', 'T', 'U' },
                 { 'T', 'T', 'V', 'G', 'R', 'G', 'G', 'G', 'G', 'G' },
                 { 'T', 'V', 'K', 'G', 'R', 'G', 'G', 'G', 'G', 'T' },
                 { 'V', 'G', 'G', 'G', 'R', 'G', 'G', 'K', 'G', 'G' },
                 { 'T', 'G', 'G', 'R', 'G', 'G', 'G', 'G', 'G', 'G' },
                 { 'G', 'R', 'R', 'G', 'R', 'U', 'G', 'G', 'G', 'V' },
                 { 'R', 'G', 'G', 'G', 'K', 'R', 'G', 'G', 'G', 'W' },
                 { 'G', 'T', 'G', 'G', 'G', 'R', 'G', 'G', 'T', 'W' },
                 { 'U', 'T', 'V', 'G', 'R', 'G', 'K', 'K', 'W', 'W' },
                 { 'T', 'U', 'K', 'G', 'R', 'G', 'G', 'G', 'K', 'W' },
                 { 'T', 'T', 'T', 'V', 'R', 'K', 'K', 'K', 'W', 'W' }, 
                 { 'K', 'G', 'V', 'G', 'R', 'G', 'K', 'W', 'W', 'W' },
                 { 'U', 'G', 'T', 'R', 'G', 'G', 'G', 'W', 'W', 'K' },
                 { 'T', 'G', 'R', 'G', 'G', 'T', 'G', 'G', 'W', 'W' },
                 { 'G', 'R', 'G', 'R', 'K', 'G', 'G', 'G', 'G', 'W' },
                 { 'R', 'V', 'V', 'G', 'R', 'G', 'G', 'K', 'G', 'W' },
                 { 'T', 'U', 'T', 'V', 'R', 'K', 'G', 'G', 'W', 'W' },
                 { 'T', 'G', 'G', 'R', 'G', 'G', 'K', 'W', 'W', 'W' },
                 { 'U', 'T', 'K', 'G', 'G', 'G', 'G', 'G', 'V', 'W' },               
             };
  
  /*
  cTemp (character temp) is used so you only need to call the
  character once to check against conditions. Nested loop that
  checks the square type and initilizes it as movable or not.
  */
  for (int i = 0; i<gridX; i++){
    for (int j = 0; j<gridY; j++) {

      cTemp = t[i][j];
      if (cTemp == 'G' || cTemp == 'R') {
        g[i][j] = new GridSq(cTemp, true, -1, -1);
      } else if (cTemp == 'K' || cTemp == 'T' || 
                 cTemp == 'U' || cTemp == 'V' ||
                 cTemp == 'H' || cTemp == 'W') {
        g[i][j] = new GridSq(cTemp, false, -1, -1);
      } else {
        println("Error, condition for which is unaccounted.");
      }
    } 
  }
  return g;
}


public void drawSprites() {
  
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
public void whereDaMouse() {
  
  char tempType;

  for (int i = -1; i < 2; i++) {
    for (int j = -1; j < 2; j++) {
      if (!(i == 0 && j == 0)) {
        if ((mouseX > posX + (50*i) && mouseX < posX + (50*i + 50)
            && mouseY > posY + (50*j) && mouseY< posY + (50*j + 50) 
            && grid[xBox+i][yBox+j].movable)) {
          tempType = grid[xBox+i][yBox+j].type;
          // Highlights movable squares 
          if(tempType == 'G') {
            image(grassS, posX + i*50, posY + j*50);
          } else if (tempType == 'R') {
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
public void mouseClicked() {
  
  if (fightOrRid) {
   if (mouseX > 370 && mouseX < 495
        && mouseY > 290 && mouseY < 380) {
          fightOrRid = false;
         
   }
   if (mouseX > 560 && mouseX < 685
        && mouseY > 290 && mouseY < 380) {
          fightOrRid = false;
          riddle = true;
          answer = "";
   }
  }      
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
public void moveGoblin(Goblin g) {
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
public void openChest(int c, int x, int y) {
  // Final chest sets win
  if(c == 0) {
    win = true;
  } else {
    creak.play();
    creak.rewind();
    // Prize is either health or power up
    int prize = (int) random(0,2);
    if (prize == 1) {
      hero.level += random(1,3);
    } else {
      hero.hp += random(6,14);
    }
    // Sets grid as opened
    grid[x][y].chest = -1;
    chst[c].closed = false;
  }
}

public void fightGoblin(int g, int x, int y) {
  // Boss fight condition
  if (g < 4) {
    fightGoblinKing(g, x, y);
  } else if (g < 0) { // error check built in
    println("why is this happening?");
  }else {
    
    // Damage done by hero
    int damDone = (int)random(0,4) + hero.level;
    gob[g].hp -= damDone;
        
    // Damage taken by hero
    int damTake = (int)random(0,2);
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
      hero.level += random(0,2);
      hero.hp += random(0,6);
    }
  }
}

public void fightGoblinKing(int g, int x, int y) {
  // Plays monster sound once per boss
  if (!boss) {
    monster.play();
    monster.rewind();
    boss = true;
    fightOrRid = true;
    lastBoss = g;
  }
  
  int damDone = (int)random(0,3) + hero.level;
  gob[g].hp -= damDone;
  
  int damTake = (int)random(0,4);
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
    
    hero.level += random(0,2);
    hero.hp += random(0,6);
  }
}

// source found on processing website
// http://wiki.processing.org/w/Typed_input
// then edited
public void keyReleased() {
  if (key != CODED) {
   
    if (fightOrRid) {
      if (key == 'r') {
        riddle = true;
        fightOrRid = false;
        answer = "";
      } else if (key == 'f') {
        fightOrRid = false;
      }
    }
    switch(key) {
    case 'a':
    if (!riddle) {
      if (xBox - 1 >= 0) {
      if (grid[xBox-1][yBox].movable) {
          if (grid[xBox-1][yBox].goblin < 0 &&
              grid[xBox-1][yBox].chest < 0) {
            xBox -= 1;
            for (int i = -1; i<2; i++) {
              for (int j = -1; j<2; j++) {
                if (xBox + i >= 0 && xBox + i < gridX
                    && yBox + j >= 0 && yBox + j < gridY) {
                  grid[xBox + i][yBox + j].shown = true;
              }
              }
            }  
          } else if (grid[xBox-1][yBox].goblin >= 0) {
            fightGoblin(grid[xBox-1][yBox].goblin, xBox-1, yBox);
          } else if (grid[xBox-1][yBox].chest >= 0) {
            openChest(grid[xBox-1][yBox].chest, xBox-1, yBox);
          }
        }
      posX = xBox * 50;
      hero.xBox = xBox;
    }
    }
    break;
    case 'w': 
      if (!riddle) {
      if (yBox - 1 >= 0) {
        if (grid[xBox][yBox-1].movable) {
            if (grid[xBox][yBox -1].goblin < 0 &&
                grid[xBox][yBox -1].chest < 0) {
              yBox -= 1;
              for (int i = -1; i<2; i++) {
                for (int j = -1; j<2; j++) {
                  if (xBox + i >= 0 && xBox +i < gridX
                      && yBox + j >= 0 && yBox + j < gridY) {
                    grid[xBox + i][yBox + j].shown = true;
                      }
                }
              }  
            } else if (grid[xBox][yBox - 1].goblin >= 0) {
              fightGoblin(grid[xBox][yBox - 1].goblin, xBox, yBox - 1);
            } else if (grid[xBox][yBox - 1].chest >= 0) {
              openChest(grid[xBox][yBox - 1].chest, xBox, yBox - 1);
            }
          }
        posY = yBox * 50;
        hero.yBox = yBox;
      }
      }
      break;
    case 's':
    if (!riddle) {
      if (yBox + 1 < gridY) {
         if (grid[xBox][yBox+1].movable) {
            if (grid[xBox][yBox +1].goblin < 0 &&
                grid[xBox][yBox +1].chest < 0) {
              yBox += 1;
              for (int i = -1; i<2; i++) {
                for (int j = -1; j<2; j++) {
                  if (xBox + i >= 0 && xBox + i < gridX
                      && yBox + j >= 0 && yBox + j < gridY) {
                    grid[xBox + i][yBox + j].shown = true;
                  }
                }
              }  
            } else if (grid[xBox][yBox + 1].goblin >= 0) {
              fightGoblin(grid[xBox][yBox + 1].goblin, xBox, yBox + 1);
            } else if (grid[xBox][yBox + 1].chest >= 0) {
              openChest(grid[xBox][yBox + 1].chest, xBox, yBox + 1);
            }
          }
        posY = yBox * 50;
        hero.yBox = yBox;
      }
    }
      break;
    case 'd':
      if (!riddle) {
      if (xBox + 1 < gridX) {
        if (grid[xBox+1][yBox].movable) {
            if (grid[xBox+1][yBox].goblin < 0 &&
                grid[xBox+1][yBox].chest < 0) {
              xBox += 1;
                          for (int i = -1; i<2; i++) {
                for (int j = -1; j<2; j++) {
                  if (xBox + i >= 0 && xBox +i < gridX
                      && yBox + j >= 0 && yBox + j < gridY) {
                    grid[xBox + i][yBox + j].shown = true;
                      }
                }
              }  
            } else if (grid[xBox+1][yBox].goblin >= 0) {
              fightGoblin(grid[xBox+1][yBox].goblin, xBox+1, yBox);
            } else if (grid[xBox+1][yBox].chest >= 0) {
              openChest(grid[xBox+1][yBox].chest, xBox+1, yBox);
            }
          }
      }
        posX = xBox * 50;
        hero.xBox = xBox;
      }
      break;
          
    case BACKSPACE:
      answer = answer.substring(0,max(0,answer.length()-1));
      break;
    case ENTER:
    case RETURN:
      if (riddle) {
        if (answer.compareToIgnoreCase(ridSols[lastBoss]) == 0) {
          gob[lastBoss].hp = 0 ;
          grid[gob[lastBoss].xBox][gob[lastBoss].yBox].goblin = -1;
          
          riddle = false;
          correct = true;
          boss = false;

          hero.level += random(0,2);
          hero.hp += random(0,4);
          
        } else {
          riddle = false;
          wrong = true;
        }
      }
      break;

    default:
      answer += key;
    }
    
    // Allows for control of hero
    // using directional arrow keys
  } if (key == CODED) {
    switch(keyCode) {
    // Moves hero up
    case UP:
      if (yBox - 1 >= 0) {
        if (grid[xBox][yBox-1].movable) {
            if (grid[xBox][yBox -1].goblin < 0 &&
                grid[xBox][yBox -1].chest < 0) {
              yBox -= 1;
              for (int i = -1; i<2; i++) {
                for (int j = -1; j<2; j++) {
                  if (xBox + i >= 0 && xBox +i < gridX
                      && yBox + j >= 0 && yBox + j < gridY) {
                    grid[xBox + i][yBox + j].shown = true;
                      }
                }
              }  
            } else if (grid[xBox][yBox - 1].goblin >= 0) {
              fightGoblin(grid[xBox][yBox - 1].goblin, xBox, yBox - 1);
            } else if (grid[xBox][yBox - 1].chest >= 0) {
              openChest(grid[xBox][yBox - 1].chest, xBox, yBox - 1);
            }
          }
        posY = yBox * 50;
        hero.yBox = yBox;
      }
      break;
    // Moves hero right
    case RIGHT:
      if (xBox + 1 < gridX) {
        if (grid[xBox+1][yBox].movable) {
            if (grid[xBox+1][yBox].goblin < 0 &&
                grid[xBox+1][yBox].chest < 0) {
              xBox += 1;
                          for (int i = -1; i<2; i++) {
                for (int j = -1; j<2; j++) {
                  if (xBox + i >= 0 && xBox +i < gridX
                      && yBox + j >= 0 && yBox + j < gridY) {
                    grid[xBox + i][yBox + j].shown = true;
                      }
                }
              }  
            } else if (grid[xBox+1][yBox].goblin >= 0) {
              fightGoblin(grid[xBox+1][yBox].goblin, xBox+1, yBox);
            } else if (grid[xBox+1][yBox].chest >= 0) {
              openChest(grid[xBox+1][yBox].chest, xBox+1, yBox);
            }
          }
        posX = xBox * 50;
        hero.xBox = xBox;
      }
      break;
    // etc
    case LEFT:
    if (xBox - 1 >= 0) {
      if (grid[xBox-1][yBox].movable) {
          if (grid[xBox-1][yBox].goblin < 0 &&
              grid[xBox-1][yBox].chest < 0) {
            xBox -= 1;
            for (int i = -1; i<2; i++) {
              for (int j = -1; j<2; j++) {
                if (xBox + i >= 0 && xBox + i < gridX
                    && yBox + j >= 0 && yBox + j < gridY) {
                  grid[xBox + i][yBox + j].shown = true;
              }
              }
            }  
          } else if (grid[xBox-1][yBox].goblin >= 0) {
            fightGoblin(grid[xBox-1][yBox].goblin, xBox-1, yBox);
          } else if (grid[xBox-1][yBox].chest >= 0) {
            openChest(grid[xBox-1][yBox].chest, xBox-1, yBox);
          }
        }
      posX = xBox * 50;
      hero.xBox = xBox;
    }
      break;
    case DOWN:
      if (yBox + 1 < gridY) {
         if (grid[xBox][yBox+1].movable) {
            if (grid[xBox][yBox +1].goblin < 0 &&
                grid[xBox][yBox +1].chest < 0) {
              yBox += 1;
              for (int i = -1; i<2; i++) {
                for (int j = -1; j<2; j++) {
                  if (xBox + i >= 0 && xBox + i < gridX
                      && yBox + j >= 0 && yBox + j < gridY) {
                    grid[xBox + i][yBox + j].shown = true;
                  }
                }
              }  
            } else if (grid[xBox][yBox + 1].goblin >= 0) {
              fightGoblin(grid[xBox][yBox + 1].goblin, xBox, yBox + 1);
            } else if (grid[xBox][yBox + 1].chest >= 0) {
              openChest(grid[xBox][yBox + 1].chest, xBox, yBox + 1);
            }
          }
        posY = yBox * 50;
        
        hero.yBox = yBox;
      }
      break;
    }
  }
}


class Chest {
  
  int number;
  int xBox, yBox;
  int posX, posY;
  boolean victory;
  boolean closed;
  
  Chest(int n, int x, int y, boolean v) {
    number = n;
    xBox = x;
    yBox = y;
    posX = xBox * 50;
    posY = yBox * 50;
    victory = v;
    closed = true;
  }
}
class Goblin {
  
  int number;
  int xBox, yBox;
  int posX, posY;
  int hp;
  boolean boss;
  boolean alive;
  
  Goblin(int n, int x, int y, boolean b) {
    number = n;
    xBox = x;
    yBox = y;
    posX = xBox * 50;
    posY = yBox * 50;
    boss = b;
    if (!b) {
      hp = 8;
    } else if(n == 0) {
      hp = 42;
    } else {
      hp = 22;
    }
    alive = true;
  }
}
class GridSq {
  
  char type;
  boolean movable;
  int goblin;
  int chest;
  boolean shown;
  
  GridSq(char t, boolean m, int g, int c) {
    type = t;
    movable = m;
    goblin = g;
    chest = c;
    shown = false;
  }
  
}
class Hero {
  
  int xBox, yBox;
  int hp;
  int level;
  
  Hero(int x, int y) {
    xBox = x;
    yBox = y;

    hp = 12;
    level = 0;
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "BaliBrawl" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
