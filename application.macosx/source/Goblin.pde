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
