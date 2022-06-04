import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;




public class PongGame{
    public static void main(String[] args){
        GameFrame frame = new GameFrame();
    }
}
 class GameFrame extends JFrame{

    GamePanel panel;
    ImageIcon image;

    GameFrame(){
        panel = new GamePanel();
        this.add(panel);
        this.setTitle("Pong Game");
        this.setResizable(false);
        this.setBackground(Color.black);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        image = new ImageIcon("Ping-Pong.png");
        this.setIconImage(image.getImage());


    }
}
class GamePanel extends JPanel implements Runnable{

    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH * (0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;

    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    paddle paddle1;
    paddle paddle2;
    Ball ball;
    Score score;
    int count1 = 0;
    int count2 = 0;


    GamePanel(){
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new Al());
        this.setPreferredSize(SCREEN_SIZE);

        gameThread = new Thread(this);
        gameThread.start();
    }
    public void newBall(){
        random = new Random();
        ball = new Ball((GAME_WIDTH/2) - (BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER), BALL_DIAMETER,BALL_DIAMETER);
    }
    public void newPaddles(){
        paddle1 = new paddle(0,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,1);
        paddle2 = new paddle(GAME_WIDTH-PADDLE_WIDTH,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,2);

    }
    public void paint(Graphics g){
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image,0,0,this);
    }
    public void draw(Graphics g){
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
    }
    public void move(){
        paddle1.move();
        paddle2.move();
        ball.move();
    }
    public void checkCollision(){

        //for bounce ball top and bottom
        if(ball.y <=0){
            ball.setYDirection(-ball.yVelocity);
        }
        if(ball.y >= GAME_HEIGHT - BALL_DIAMETER){
            ball.setYDirection(-ball.yVelocity);
        }
        // bounce ball off paddle
        if(ball.intersects(paddle1)){
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity += 0.2;
            if(ball.yVelocity>0){
                ball.yVelocity+=0.7;
            }else{
                ball.yVelocity--;
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }


        if(ball.intersects(paddle2)){
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++;
            if(ball.yVelocity>0){
                ball.yVelocity+=0.7;
            }else{
                ball.yVelocity--;
            }
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        //for stop paddles at window edge
        if(paddle1.y<=0){
            paddle1.y = 0;
        }
        if(paddle1.y >= (GAME_HEIGHT-PADDLE_HEIGHT)){
            paddle1.y = GAME_HEIGHT - PADDLE_HEIGHT;
        }

        if(paddle2.y<=0){
            paddle2.y =0;
        }
        if(paddle2.y >= (GAME_HEIGHT-PADDLE_HEIGHT)){
            paddle2.y = GAME_HEIGHT - PADDLE_HEIGHT;
        }

        //for point and creates new paddles & ball

        
        if(ball.x <=0){
            score.player2++;
            count2++;
            newBall();
            System.out.println("Player2: "+score.player2);
        }
        if(ball.x >= GAME_WIDTH -BALL_DIAMETER){
            score.player1++;
            count1++;
            newBall();
            System.out.println("player1: "+score.player1);
        }
        if(count1 == 10 || count2 == 10){
            score.player2=0;
            score.player1=0;
            count1=0;
            count2=0;
        }
        
    }
    public void run(){
        //game loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while(true){
            long now = System.nanoTime();
            delta += (now - lastTime)/ns;
            lastTime = now;
            if(delta >= 1){
                move();
                checkCollision();
                repaint();
                delta--;
                System.out.println("TEST");
            }
        }
    }
    public class Al extends KeyAdapter{
        public void keyPressed(KeyEvent e){
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);
        }
        public void keyReleased(KeyEvent e){
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }
} 
class paddle extends Rectangle{

    int id;
    int yVelocity;
    int speed = 10;

    paddle(int x, int y, int PADDLE_WIDTH, int PADDLE_HEIGHT, int id){
        super(x,y, PADDLE_WIDTH, PADDLE_HEIGHT);
        this.id = id;

    }
    public void keyPressed(KeyEvent e){
        switch(id){
            case 1:
                if(e.getKeyCode() == KeyEvent.VK_W){
                    setYDirection(-speed);
                    move();
                }
                if(e.getKeyCode() == KeyEvent.VK_S){
                    setYDirection(speed);
                    move();
                }
                break;

            case 2:
                if(e.getKeyCode() == KeyEvent.VK_UP){
                    setYDirection(-speed);
                    move();
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    setYDirection(speed);
                    move();
                }
                break;
        }
    }
    public void keyReleased(KeyEvent e){

        switch(id){
            case 1:
                if(e.getKeyCode() == KeyEvent.VK_W){
                    setYDirection(0);
                    move();
                }
                if(e.getKeyCode() == KeyEvent.VK_S){
                    setYDirection(0);
                    move();
                }
                break;

            case 2:
                if(e.getKeyCode() == KeyEvent.VK_UP){
                    setYDirection(0);
                    move();
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    setYDirection(0);
                    move();
                }
                break;
        }
    }
    public void setYDirection(int yDirection){
        yVelocity = yDirection;
    }
    public void move(){
        y = y + yVelocity;
    }
    public void draw(Graphics g){
        if(id == 1){
            g.setColor(new Color(109,63,189));
        }else{
            g.setColor(new Color(255,58,58));
        }
        g.fillRect(x, y, width, height);
    }
}
class Ball extends Rectangle{

    Random random;
    int xVelocity;
    int yVelocity;
    int initialSpeed = 2;

    
    Ball(int x,int y,int width, int height){
        super(x,y,width, height);
        random = new Random();

        int randomXDirection = random.nextInt(2);
        if(randomXDirection ==0){
            randomXDirection--;
        }
        setXDirection(randomXDirection * initialSpeed);

        int randomYDirection = random.nextInt(2);
        if(randomYDirection ==0){
            randomYDirection--;
        }
        setYDirection(randomYDirection * initialSpeed);

    }
    public void setXDirection(int randomXDirection){
        xVelocity = randomXDirection;
    }
    public void setYDirection(int randomYDirection){
        yVelocity = randomYDirection;
    }
    public void move(){
        x += xVelocity;
        y += yVelocity;
    }
    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.fillOval(x, y, height, width);
    }
}
class Score extends Rectangle{

    static int GAME_WIDTH;
    static int GAME_HEIGHT;
    int player1;
    int player2;

    Score(int GAME_WIDTH, int GAME_HEIGHT){
        Score.GAME_WIDTH = GAME_WIDTH;
        Score.GAME_HEIGHT = GAME_HEIGHT;
    }
    public void draw(Graphics g){
        g.setColor(Color.white);
        g.setFont(new Font("Consolas",Font.PLAIN,60));
        g.drawLine(GAME_WIDTH/2, 0, GAME_WIDTH/2, GAME_HEIGHT);
        g.drawOval(GAME_WIDTH/2-50, GAME_HEIGHT/2-50, 100, 100);

        g.drawString(String.valueOf(player1/10)+String.valueOf(player1%10), (GAME_WIDTH/2)-85, 50);
        g.drawString(String.valueOf(player2/10)+String.valueOf(player2%10), (GAME_WIDTH/2)+20, 50);
    }
}