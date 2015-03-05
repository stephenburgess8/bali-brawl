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
