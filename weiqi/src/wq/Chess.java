package wq;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.JFrame;
import sunw.util.EventListener;
public class Chess extends JFrame{
	Chessboard qipan = new Chessboard( );
	public Chess( ){
		this.setTitle("Chess");
		this.setLayout(new BorderLayout( ));
		this.setSize(qipan.getSize( ));
		this.add(qipan,"Center");
		this.setResizable(false);
		this.setLayout(new BorderLayout( ));
		this.setSize(550,490);
		this.setVisible(true);
	}
	public int getWidth( ){
		return qipan.getWidth( );
	}
	
	public int getHeight( ){
		return qipan.getHeight( );
	}
	public static void main(String[] args){
		Chess Igo =new Chess( );
		
		
	}

}
