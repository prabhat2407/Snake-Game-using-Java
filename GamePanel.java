import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener{
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE =25;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	static int DELAY = 75;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 4;
	int applesEaten;
	int appleX;
	int appleY;
	int Score = applesEaten;
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random random;
	static boolean gameOn = false;
	private Image gameImage = null;
	private boolean isStartImage = true;
	Graphics g;
	
	GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}
	
	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY,this);
		timer.restart();
	}
	
	public void pause() {
		GamePanel.gameOn = true;
		timer.stop();
	}
	
	public void reset() {
		GameFrame n=new GameFrame();
		n.add(new GamePanel());
		n.setTitle("Snake");
		n.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		n.setResizable(false);
		n.pack();
		n.setVisible(true);
		n.setLocationRelativeTo(null);
		bodyParts=4;
		Score=0;
		startGame();
	}

	public void resume() {
		GamePanel.gameOn = false;
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
		
	}
	
	public void draw(Graphics g) {
		if(running) {
			//can be used to create matrix to check size of snake and fruit
			/*for(int i=0; i<SCREEN_HEIGHT/UNIT_SIZE; i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			}*/
			if(isStartImage)
			{
				drawImage(g);
			}
			else {
				g.setColor(Color.red);
				g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
				for(int i=0; i<bodyParts; i++) {
					if(i == 0) {
						g.setColor(Color.green);
						g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
					}
					else {
						g.setColor(new Color(45,180,0));
						g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
					}
				}
				g.setColor(Color.red);
				g.setFont(new Font("Ink Free", Font.BOLD,40));
				FontMetrics metrics = getFontMetrics(g.getFont());
				g.drawString("Score: "+Score, (SCREEN_WIDTH - metrics.stringWidth("Score: "+Score))/2, g.getFont().getSize());
				
			}
			
			
		}
		else {
			gameOver(g);
		}
	}
	
	public void drawImage(Graphics g) {
		if(gameImage == null) {
			try {
				URL imagePath = GamePanel.class.getResource("Game.png");
				gameImage = Toolkit.getDefaultToolkit().getImage(imagePath);
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		g.drawImage(gameImage, 0, 0, SCREEN_HEIGHT, SCREEN_WIDTH,  this);
	}
	
	public void newApple()//printing apples at random position on the frame
	{
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
		
	}
	
	public void move() {
		for(int i=bodyParts; i>0; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}
	
	public void checkApple() //checking if apple is eaten
	{
		if((x[0] == appleX)&&(y[0] == appleY)) {
			bodyParts++;
			Score++;
			DELAY = DELAY - 2;
			newApple();
		}
	}
	
	public void checkCollisions() //checking snakes collision
	{
		//checking if head of snake collides with its body
		for(int i = bodyParts; i>0; i--) {
			if((x[0] == x[i])&&(y[0] == y[i])) {
				running = false;
			}
		}
		//checking if head of snake collides with left wall
		if(x[0] < 0) {
			running = false;
		}
		//checking if head of snake collides with right wall
		if(x[0] > SCREEN_WIDTH) {
			running = false;
		}
		//checking if head of snake collides with top wall
		if(y[0] < 0) {
			running = false;
		}
		//checking if head of snake collides with bottom wall
		if(y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		if(!running) {
			timer.stop();
		}
	}
	
	public void gameOver(Graphics g) {
		//Score
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD,40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: "+Score, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+Score))/2, g.getFont().getSize());
		//Game Over
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD,75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
		//Reset
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD,40));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("Press R to Restart", (SCREEN_WIDTH - metrics3.stringWidth("Press R to Restart"))/2, 3*(SCREEN_HEIGHT)/4);
		//Close the Game
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD,40));
		FontMetrics metrics4 = getFontMetrics(g.getFont());
		g.drawString("Press C to close the game", (SCREEN_WIDTH - metrics4.stringWidth("Press C to close the game"))/2, 7*(SCREEN_HEIGHT)/8);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(isStartImage==false) {
		if(running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
		}	
	}
	public class MyKeyAdapter extends  KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:{
				if(direction  != 'R') {
					direction = 'L';
				}
				break;
			}
			case KeyEvent.VK_RIGHT:{
				if(direction  != 'L') {
					direction = 'R';
				}
				break;
			}
			case KeyEvent.VK_UP:{
				if(direction  != 'D') {
					direction = 'U';
				}
				break;
			}
			case KeyEvent.VK_DOWN:{
				if(direction  != 'U') {
					direction = 'D';
				}
				break;
			}
			case KeyEvent.VK_SPACE:{
				if(GamePanel.gameOn) {
					resume();
				} else {
					pause();
				}
				break;
			}	
			case KeyEvent.VK_R:{
				reset();
				break;
			}
			case KeyEvent.VK_C:{
			     System.exit(0);
			     break;
			}
			case KeyEvent.VK_ENTER:{
				isStartImage = false;
				break;
			}
		}
	}
	}

}

 class GameFrame extends JFrame{
	GameFrame(){
		this.add(new GamePanel());
		this.setTitle("Snake");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		}
}