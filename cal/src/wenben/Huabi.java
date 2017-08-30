package wenben;
import java.awt.*;
import javax.swing.*;

public class Huabi extends JPanel{
	public void paint(Graphics g)
	{
		g.setColor(new Color(50,50,50));
		g.fillOval((this.getWidth()-200)/2, (this.getHeight()-150)/2, 200, 150);
		g.setColor(Color.WHITE);
		g.fillRoundRect((this.getWidth()-100)/2, (this.getHeight()-75)/2, 100, 75, 20, 20);
		g.setColor(new Color(200,200,200,200));
		g.fillArc((this.getWidth()-100)/2-20,(this.getHeight()-75)/2-20 , 100, 75, 0, 270);
		g.setColor(Color.BLACK);
		g.drawLine((this.getWidth()-100)/2,(this.getHeight()-75)/2,(this.getWidth()-100)/2+100 , (this.getHeight()-75)/2+75);
	}
	
	public static void main(String[] args)
	{
		Huabi jp=new Huabi();
		JFrame jf=new JFrame();
		jf.add(jp);
		jf.setTitle("huabi");
		jf.setBounds(100,100,300,200);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

}
