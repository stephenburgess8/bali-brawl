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
