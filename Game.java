package graficos;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 * Trabalhando com Frames e Canvas para criação de Janelas para execucao de
 * Games
 */

@SuppressWarnings("serial")
public class Game extends Canvas implements Runnable {

	/**
	 * Variaveis com proporcoes do tamanho da tela. Definidas como final porque nao
	 * quero mudar o tamanho da janela.
	 */

	private static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	private final int WIDTH = 240;
	private final int HEIGHT = 160;
	private final int SCALE = 2;

	/**
	 * criacão de sprite
	 */

	private BufferedImage image;
	private Spritesheet sheet;
	private BufferedImage[] player;

	/**
	 * animação por frames;
	 */

	private int frames = 0;
	private int maxFrames = 10;
	private int curAnimation = 0, maxAnimation = 2;

	/**
	 * Metodo Construtor.
	 * criacao de personagem.
	 */

	public Game() {
		sheet = new Spritesheet("/spritesheet.png");
		player = new BufferedImage[3];
		player[0] = sheet.getSprite(0, 0, 16, 16);
		player[1] = sheet.getSprite(16, 0, 16, 16);
		player[2] = sheet.getSprite(32, 0, 16, 16);
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		image = new BufferedImage(160, 140, BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * Metodo que cria a janela de execucao.
	 */

	public void initFrame() {
		frame = new JFrame("GraphicsJAVA");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Metodo que start o jogo.
	 */

	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	/**
	 * Metodo que para a execucao.
	 */

	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Game newGame = new Game();
		newGame.start();
	}

	/**
	 * metodo de atualizacao do game.
	 */

	public void tick() {
		frames++;
		if (frames > maxFrames) {
			frames = 0;
			curAnimation++;
			if (curAnimation > maxAnimation) {
				curAnimation = 0;
			}
		}

	}
	
	/**
	 * metodo de renderizacao do game.
	 */

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		// fundo

		Graphics g = image.getGraphics();
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		// texto

		g.setFont(new Font("Arial", Font.BOLD, 12));
		g.setColor(Color.BLACK);
		g.drawString("Contri estraike", 30, 54);

		// rotacao de sprite

		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(player[curAnimation], 60, 60, null);

		// otimização e graficos

		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		bs.show();
	}

	/**
	 * Game looping profissional limitando looping e FPS.
	 * 
	 * o game e startado e entra em um looping no qual o mesmo e atualizado e
	 * renderizado ate que a execucao seja interrompida.
	 * 
	 */

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();

		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer = System.currentTimeMillis();
			}
		}

		stop();
	}
}