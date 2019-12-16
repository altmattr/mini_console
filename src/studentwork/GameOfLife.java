package studentwork;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Screen;
import pfx.PVector;

public class GameOfLife extends pfx.FXApp {

    public String name(){return "Game of Life - Evan";}
    public String description(){return "Conway via Gillard";}

    int w, h, scale;
    Cell[][] grid;

    public GameOfLife(GraphicsContext g) {
        super(g);
    }

    public void settings()
    {
        size(360,240);
    }

    public void setup()
    {
        scale = 5;
        w = width / scale;
        h = height / scale;
        grid = new Cell[w][h];

        for (int x = 0; x < w; x++) for (int y = 0; y < h; y++)
        { { grid[x][y] = new Cell(x*scale,y*scale); } }

	noStroke();
    }

    public void draw()
    {
        background(0);

        for (int x = 0; x < w; x++) for (int y = 0; y < h; y++)
        { {
            grid[x][y].prev = grid[x][y].state;
            int neighbours = 0;
            for (int xO = -1; xO <= 1; xO++) for (int yO = -1; yO <= 1; yO++)
            {{ if (grid[(x+xO+w)%w][(y+yO+h)%h].prev == 1) { neighbours++; }}}
            neighbours -= grid[x][y].prev;
            if      ((grid[x][y].state == 1) && (neighbours <  2)) grid[x][y].state = 0; // Loneliness
            else if ((grid[x][y].state == 1) && (neighbours >  3)) grid[x][y].state = 0; // Overpopulation
            else if ((grid[x][y].state == 0) && (neighbours == 3)) grid[x][y].state = 1; // Reproduction
            grid[x][y].display();
        } }
    }

    class Cell {
        PVector pos;
        int state, prev;

        Cell(float x, float y) {
            pos = new PVector(x, y);
            state = random(2);
            prev = state;
        }

        void display() {

            if (state == 1) {
                fill(0, 255, 0);
            } else {
                fill(0);
            }

            square(pos.x+1, pos.y+1, scale-2);
        }
    }
}
