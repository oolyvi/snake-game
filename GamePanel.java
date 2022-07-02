package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
	
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;         //ilanin olcusu, yeni obyektin
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;    //oyundaki objektlerin sayi
	static final int DELAY = 85;  //gecikme, oyunun sureti
	final int x[] = new int[GAME_UNITS];  //x koordinati oyundaki object sayi qeder
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;   //ilanin bedeninin hisselerinin sayi
	int applesEaten;     //yeyilen alma sayi, initial 0
	int appleX;          //X koordinatindaki alma sayi
	int appleY;          //Y koordinatindaki alma sayi
	char direction = 'R';     //ilanin basladigi istiqamet yux-asagi-sag-sol
	boolean running = false;
	Timer timer;        //Timer funksiyasi
	Random random;
	

	GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());   //klaviatura dinleyicisi
		startGame();        //bu yuxaridaki sey-suylerden sonra game baslayir
	}
	
	public void startGame() {
		newApple();     //yeni alma yaranmaqla oyun start edir
		running = true;
		timer = new Timer(DELAY,this);       //oyun suretini de baslangicda verdik
		timer.start();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	public void draw(Graphics g) {
		if (running) {
			
			//grid layut kimi xetler ederek gorunumu rahatlasdiririq, ancaq optionaldir
		/*	for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);  //uzununa xetler
				g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);  //enine xetler
			//unit_size boyuk olsa yaranmir kicik kvadrat olculeri de boyuyur
			}		
	  	*/		g.setColor(Color.red);         //alma rengi
				g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);     //almanin boyukluyu
			//ilanin bedenini ve basini qurma
			for (int i = 0; i < bodyParts; i++) {
				if (i == 0) {
					g.setColor(Color.green); //ilanin basi
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
			}   else {       //ilanin bedeni
					//g.setColor(new Color(45, 180, 0));   //RGB color
					g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
			}
		}	
			//score paneli
			g.setColor(Color.orange);
			g.setFont(new Font("Monospaced", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Xal:" + applesEaten, (SCREEN_WIDTH-metrics.stringWidth("Xal: "+applesEaten )) / 2, g.getFont().getSize());
	}
		else {
			gameOver(g);       //oz etdiyimiz method
		}
	}
	public void newApple() {
		//random alma cixan yerleri gosterecek
		appleX = random.nextInt((int)SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;  //x uzre random apple yeri
		appleY = random.nextInt((int)SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;  //y uzre random apple yeri
	}
	public void move() {   //ilanin hereketi  //bodyPart-ilanBedenHisseSayi
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		switch (direction) {
		case 'U':           //up
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':           //down
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':           //left
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':           //right
			x[0] = x[0] + UNIT_SIZE;
			break;
		
		}
	}
	public void checkApple() {    //random apple
		if ((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;      //bedenin olcusu artir
			applesEaten++;   //yeyilen alma sayi artir
			newApple();       //yeniden alma yaranir
		}
		
	}
	public void checkCollisions() {   //carpisma yoxlayan
		//ilanin basi ayagiyla toqqusue oyun bitir
		for (int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && ((y[0] == y[i]))) {   //x ilan basi, y ilan ayagi
				running = false;
			}
		}
		//ilan sol divara toxunsa
		if (x[0] < 0) {
			running = false;
		}
		//ilan sag divara toxunsa
		if (x[0] > SCREEN_WIDTH) {
			running = false;
		}
		//ilan basi yuxari toxunsa
		if (y[0] < 0) {
			running = false;
		}
		//ilan basi asagi toxunarsa
		if (y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		if(!running) {
			timer.stop();
		}
		
	}
	public void gameOver(Graphics g) {
		//Sonda uduzanda score'u gosteren
		g.setColor(Color.orange);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Toplanan xal:" + applesEaten, (SCREEN_WIDTH-metrics.stringWidth("Toplanan xal: "+applesEaten )) / 2, g.getFont().getSize());
		//GameOver text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 50));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Oyun bitdi brat :(", (SCREEN_WIDTH-metrics2.stringWidth("Oyun Bitdi brat :(")) / 2, SCREEN_HEIGHT/2);
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			 move();
			 checkApple();
			 checkCollisions();
		}
		repaint();    //java oz methodu
		
	}
	//inner class   //klaviatura ucun
	public class MyKeyAdapter extends KeyAdapter {
		@Override  
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:            //sol duyme
			if (direction != 'R') {
				direction = 'L';
			}
			break;
		case KeyEvent.VK_RIGHT:            //sag duyme
			if (direction != 'L') {
				direction = 'R';
			}
			break;
		case KeyEvent.VK_UP:            //yuxari duyme
			if (direction != 'D') {
				direction = 'U';
			}
			break;
		case KeyEvent.VK_DOWN:            //asagi duyme
			if (direction != 'U') {
				direction = 'D';
			}
			break;
		
		}
	}
		
	}

}
