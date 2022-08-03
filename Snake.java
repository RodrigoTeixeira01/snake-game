package snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Snake extends JFrame implements KeyListener, MouseListener {
	Scanner scan;
	private final long DELAY;
	private final int SCREEN_SIZE;
	private final int GRID_SIZE;
	private final boolean BORDER_LINE;
	private final int COLOR_MAX;
	private final int COLOR_MIN;
	private final boolean LINES;
	private final int SCREEN_SQUARES;
	private final boolean COLOR_FASING;
	private int[] x;
	private int[] y;
	private static final int OFFSET_X = 8;
	private static final int OFFSET_Y = 31;
	private int bodyParts = 1;
	private int dx = 0;
	private int dy = 0;
	private int appleX;
	private int appleY;
	private boolean gameOver = false;
	private boolean win = false;
	Snake() {
		fileIt();
		DELAY=scan.nextLong();
		SCREEN_SIZE=scan.nextInt();
		GRID_SIZE=scan.nextInt();
		BORDER_LINE=scan.nextBoolean();
		COLOR_MAX=scan.nextInt();
		COLOR_MIN=scan.nextInt();
		LINES=scan.nextBoolean();
		COLOR_FASING=scan.nextBoolean();
		SCREEN_SQUARES = SCREEN_SIZE/GRID_SIZE;
		x = new int[SCREEN_SQUARES*SCREEN_SQUARES];
		y = new int[SCREEN_SQUARES*SCREEN_SQUARES];
		
		setIconImage(new ImageIcon("R.png").getImage());
		
		setTitle("Snake Game");
		setBackground(new Color(100, 100, 100));
		setVisible(true);
		setSize(SCREEN_SIZE, SCREEN_SIZE);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(this);
		addMouseListener(this);
		x[0]=OFFSET_X;
		y[0]=OFFSET_Y;
		
		newApple();
		
		while(true) {
			checkGameOver();
			checkApple();
			repaint();
			if(win)break;
			delay();
			move();
		}
		while(true)delay();
		
		
		
	}
	private void checkGameOver() {
		int headX = x[bodyParts-1];
		int headY = y[bodyParts-1];
		if(headX<0) {
			gameOver();
			return;
		}
		if(headY<0) {
			gameOver();
			return;
		}
		if(headX>SCREEN_SIZE-GRID_SIZE) {
			gameOver();
			return;
		}
		if(headY>SCREEN_SIZE-GRID_SIZE) {
			gameOver();
			return;
		}
		
		for(int i=0; i<bodyParts-1; i++) {
			if(headX==x[i]&&headY==y[i]) {
				gameOver();
				return;
			}
		}
		
	}
	
	private void gameOver() {
		gameOver=true;
		
	}
	public void paint(Graphics g) {
		//clear screen
		g.clearRect(0, 0, SCREEN_SIZE, SCREEN_SIZE);
		//replay button
		if(gameOver) {
			g.setColor(Color.RED);
			g.fillRect(SCREEN_SIZE/2-75, SCREEN_SIZE/2-37, 150, 75);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.PLAIN, 25));
			g.drawString("Play Again?", SCREEN_SIZE/2-g.getFontMetrics().stringWidth("Play again?")/2, SCREEN_SIZE/2-g.getFontMetrics().getHeight()/2+20);
			g.setFont(new Font("Arial", Font.PLAIN, 64));
			g.drawString("GAME OVER", SCREEN_SIZE/2-g.getFontMetrics().stringWidth("GAME OVER")/2, g.getFontMetrics().getHeight()+20);
			return;
		}
		//draw win screen
		if(win) {
			g.setFont(new Font("Arial", Font.PLAIN, 64));
			g.drawString("YOU WIN", SCREEN_SIZE/2-g.getFontMetrics().stringWidth("YOU WIN")/2, g.getFontMetrics().getHeight()+20);
			return;
		}
		//draw apple
		g.setColor(Color.RED);
		g.fillOval(appleX, appleY, GRID_SIZE, GRID_SIZE);
		//draw body
		if(BORDER_LINE) {
			g.setColor(Color.BLACK);
			for(int i=0; i<bodyParts; i++) {
				g.drawRect(x[i]-1, y[i]-1, GRID_SIZE+1, GRID_SIZE+1);
			}
		}
		//fill body
		if(COLOR_FASING) {
			for(int i=0; i<bodyParts; i++) {
				g.setColor(new Color(0, i*(COLOR_MAX-COLOR_MIN)/bodyParts+COLOR_MIN, 0));
				g.fillRect(x[i], y[i], GRID_SIZE, GRID_SIZE);
			}
		}else{
			g.setColor(new Color(0, COLOR_MAX, 0));
			for(int i=0; i<bodyParts; i++) {
				g.fillRect(x[i], y[i], GRID_SIZE, GRID_SIZE);
			}
		}
		//draw lines
		g.setColor(Color.BLACK);
		if(LINES)
		for(int i=0; i<SCREEN_SIZE; i+=GRID_SIZE) {
			g.drawLine(i+OFFSET_X, OFFSET_Y, i+OFFSET_X, SCREEN_SIZE);
			g.drawLine(OFFSET_X, i+OFFSET_Y, SCREEN_SIZE, i+OFFSET_Y);
		}
		//print score
		g.setFont(new Font("Arial", Font.PLAIN, 64));
		g.setColor(Color.BLACK);
		String scoreText = "Score: "+(bodyParts-1);
		g.drawString(scoreText, SCREEN_SIZE/2-g.getFontMetrics().stringWidth(scoreText)/2, g.getFontMetrics().getHeight()+20);
		
		
	}
	
	private void checkApple() {
		if(appleX==x[bodyParts-1]&&appleY==y[bodyParts-1]) {
			newApple();
			bodyParts++;
			if(bodyParts>=(SCREEN_SQUARES-1)*(SCREEN_SQUARES-1)) {
				win=true;
			}
		}
		
	}
	private void newApple() {
		Random rand = new Random();
		appleX=rand.nextInt(SCREEN_SQUARES-1)*GRID_SIZE+OFFSET_X;
		appleY=rand.nextInt(SCREEN_SQUARES-1)*GRID_SIZE+OFFSET_Y;
		while(inBody(appleX, appleY)) {
			appleX=rand.nextInt(SCREEN_SQUARES-1)*GRID_SIZE+OFFSET_X;
			appleY=rand.nextInt(SCREEN_SQUARES-1)*GRID_SIZE+OFFSET_Y;
		}
		
	}
	private boolean inBody(int x, int y) {
		for(int i=0; i<bodyParts; i++) {
			if(x==this.x[i]&&y==this.y[i]) {
				return true;
			}
		}
		return false;
	}
	private void move() {
		x[bodyParts]=x[bodyParts-1]+dx;
		y[bodyParts]=y[bodyParts-1]+dy;
		
		for(int i=0; i<bodyParts; i++) {
			x[i]=x[i+1];
			y[i]=y[i+1];
		}
		
	}
	private void delay() {
		try {
			Thread.sleep(DELAY);
		} catch (InterruptedException e) {
			return;
		}
	}
	public static void main(String[] args) {
		new Snake();
		
	}
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case 38:
				dx=0;
				if(dy!=GRID_SIZE) {
					dy=-GRID_SIZE;
				}
				break;
			case 37:
				if(dx!=GRID_SIZE) {
					dx=-GRID_SIZE;
				}
				dy=0;
				break;
			case 39:
				if(dx!=-GRID_SIZE) {
					dx=GRID_SIZE;
				}
				dy=0;
				break;
			case 40:
				dx=0;
				if(dy!=-GRID_SIZE) {
					dy=GRID_SIZE;
				}
				break;
		}
		
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	public void mouseClicked(MouseEvent e) {
		if(gameOver) {
			return;
		}
		int x = e.getX();
		int y = e.getY();
		if(x<SCREEN_SIZE/2-75) {
			return;
		}
		if(x>SCREEN_SIZE/2+75) {
			return;
		}
		if(y<SCREEN_SIZE/2-37) {
			return;
		}
		if(y>SCREEN_SIZE/2+38) {
			return;
		}
		
		fileIt();
		
		this.x = new int[SCREEN_SQUARES*SCREEN_SQUARES];
		this.y = new int[SCREEN_SQUARES*SCREEN_SQUARES];
		this.x[0]=OFFSET_X;
		this.y[0]=OFFSET_Y;
		bodyParts = 1;
		dx = 0;
		dy = 0;
		gameOver = false;
		win=false;
		
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	private void fileIt() {
		try{
			scan = new Scanner(new File("config.txt"));
		}catch(Exception e) {}
	}
}
