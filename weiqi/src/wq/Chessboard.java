package wq;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
public class Chessboard extends JPanel{
	public static final int _gridLen=22,_gridNum=19;
	//Ĭ�ϵ����̷��񳤶ȼ���Ŀ
	private Vector chessman;
	private int alreadyNum;//�Ѿ��µ���Ŀ
    private int currentTurn;//�ֵ�˭��
    private int gridNum, gridLen;//���񳤶ȼ���Ŀ
    private int chessmanLength;//���ӵ�ֱ��
    private Chesspoint[ ][ ] map;//�������ϵ���������,����������������x��y���ڰ׷���
    private Image offscreen;
    private Graphics offg;
    private int size;//���̿�ȼ��߶�
    private int top=13,left=13;//������߼��ϱߵı߾�
    private Point mouseLoc;//����λ�ã���map�����е��±�
    private ControlPanel controlPanel;//�������
    
    //��ÿ��ư�ľ���
    public int getWidth( ){
    	return size+controlPanel.getWidth( )+35;
    }
    public int getHeigh( ){
    	return size;
    	   }
    
    //�����������
    public Chessboard( ){
      gridNum = _gridNum;
      gridLen = _gridLen;
      chessmanLength=gridLen*9/10;
      size=2*left+gridNum*gridLen;
      addMouseListener(new PutChess());
      addMouseMotionListener(new MML());
      setLayout(new BorderLayout());
      controlPanel=new ControlPanel();
      setSize(getWidth(),size);
      add(controlPanel,"West");
      startGame();
      }
    
    public void addNotify( ){
    	super.addNotify( );
    	offscreen= createImage(size,size);
    	offg = offscreen.getGraphics(); 	
    }
    
	public void paint(Graphics g){
    	offg.setColor(new Color(180,150,100));
    	offg.fillRect(0,0,size,size);
    	//�������̸���
    	offg.setColor(Color.black);
    	for (int i=0;i<gridNum+1;i++){
    		int x1=left+i*gridLen;
    		int x2=x1;
    		int y1=top;
    		int y2=top +gridNum*gridLen;
    		offg.drawLine(x1,y1,x2,y2);
    		x1=left;
    		x2=left+gridNum*gridLen;
    		y1=top+i*gridLen;
    		y2=y1;
    		offg.drawLine(x1,y1,x2,y2);
    	}
	
    	
    	//��������
    	for(int i=0;i<gridNum+1;i++)
    		for(int j=0;j<gridNum+1;j++){
    			if(map[i][j]==null)
    				continue;
    			offg.setColor(map[i][j].color==Chesspoint.black?Color.black:Color.white);
    			offg.fillOval(left+i*gridLen-chessmanLength/2,top+j*gridLen-chessmanLength/2,chessmanLength,chessmanLength);
    		}
    		
    		//��������λ�ã�����һ��Ҫ�µ�λ��
    		if(mouseLoc!=null){
    			offg.setColor(currentTurn==Chesspoint.black?Color.gray:new Color(200,200,250));
    			offg.fillOval(left+mouseLoc.x*gridLen-chessmanLength/2,top+mouseLoc.y*gridLen-chessmanLength/2,chessmanLength,chessmanLength);
    		}
    		
    		//һ���Ի�������
    		g.drawImage(offscreen,80,0,this);
    	}

    	//��������
    	public void update(Graphics g){
    		paint(g);
    	}
    		
    	//������
    	class PutChess extends MouseAdapter{  //��һ������
    		public void mousePressed(MouseEvent evt){
    			int xoff=left/2;
    			int yoff=top/2;
    			int x=(evt.getX( )-xoff)/gridLen ;
    			int y=(evt.getY( )-yoff)/gridLen ;
    			if(x< 0 || x>gridNum || y <0 || y>gridNum )   return;
    			if(map[x][y] != null) return;
    			
    			//�����������??????�ⲽ�����������
    			if(alreadyNum<chessman.size( )){
    				int size=chessman.size( );
    						for (int i=size -1;i>=alreadyNum;i--)
    							chessman.removeElementAt(i);   				
    			}
    			
    		    Chesspoint qizi=new Chesspoint(x,y,currentTurn);
    		    map[x][y]=qizi;
    		    chessman.addElement(qizi);
    		    alreadyNum++;
    		    if(currentTurn==Chesspoint.black)
    		    	currentTurn=Chesspoint.white;
    		    else
    		    	currentTurn=Chesspoint.black;
    		    
    		    //�ж���[x,y]���Ӻ��ܷ��ߵ��������
    		    tizi(x,y);
    		    
    		    //�ж��Ƿ������Լ���������Ч
    		    if(allDead(qizi).size( )!=0){
    		    	map[x][y]=null;
    		    	repaint( );
    		    	controlPanel.setMsg("�����Լ�");
    		    	chessman.removeElement(qizi);
    		    	alreadyNum--;
    		    	   if(currentTurn==Chesspoint.black)
    	    		    	currentTurn=Chesspoint.white;
    	    		    else
    	    		    	currentTurn=Chesspoint.black;
    		    	   return;
    		    	}
    		    mouseLoc=null;
    		    //�������
    		    controlPanel.setLabel();
    		}
    		
    		public void mouseExited(MouseEvent evt){//����˳�ʱ�������Ҫ����λ��
    			mouseLoc = null;
    			repaint();
    		}
    	}
    	
    	//ȡ�ý�Ҫ���ӵ�λ��
    	private class MML extends MouseMotionAdapter{
    		public void mouseMoved(MouseEvent evt){
    			int xoff=left/2;
    			int yoff=top/2;
    			int x=(evt.getX( ) -xoff)/gridLen;
    			int y=(evt.getY( ) -yoff)/gridLen;
    			if (x<0 || x>gridNum || y<0 ||y>gridNum)
    				return;
    			if (map[x][y] != null)
    				return;
    			mouseLoc =new Point(x,y);
    			repaint( );
    		}
    	}
    	
    	//�ж���[x,y]���Ӻ��Ƿ��������
    	public static int[ ] xdir={0,0,1,-1};
    	public static int[ ] ydir={1,-1,0,0};
    	public void tizi ( int x, int y ) {
    		Chesspoint qizi;
    		if( (qizi =map[x][y]) == null) 
                return;
            int color=qizi.color;
            //ȡ����������Χ�ļ�����
            Vector v = around(qizi);
            for (int i=0;i<v.size( );i++){
            	Chesspoint q=(Chesspoint) (v.elementAt(i));
            	if(q.color==color)
            		continue;
            	//����ɫ��ͬ���ҵ�����һ���������������
            	Vector dead=allDead(q);
            	//��ȥ������������
          removeAll(dead);
          //������ӣ��򱣴����б��ߵ�������
          if(dead.size( )!=0) {
        	  Object obj= chessman.elementAt(alreadyNum-1);
        	  if(obj instanceof Chesspoint){//���obj�Ǹ�Chesspoint��
        		  qizi = (Chesspoint) (chessman.elementAt(alreadyNum-1));
        		  dead.addElement(qizi);
        	  }else{
        		  Vector vector=(Vector) obj;
        		  for(int l=0;l<vector.size( );l++)
        			  dead.addElement(vector.elementAt(l));//����dead��Ҫ�����ĸ�����������ߵ����ӣ������һ��Ԫ����alreadyNum-1������
        	  }
        	  //����Vector chessman�еĵ�num��Ԫ�أ���ʱ��num��Ԫ���ǣ�vector��dead
        	  chessman.setElementAt(dead,alreadyNum-1);//����alreadyNum-1Ԫ���滻��(vector)dead        	  
        	  }
            }
            repaint( );
    	}
    	
        //�ж�������Χ�Ƿ��пհ�
     public boolean  sideByBlank(Chesspoint qizi) {
    		for (int i=0;i<xdir.length;i++){
    		int x1=qizi.x+xdir[i];
    		int y1=qizi.y+ydir[i];
    		if (x1<0 || x1>gridNum ||y1<0 ||y1 >gridNum)
    			continue;
    		if (map[x1][y1]==null)
    			return true;
    	}
    	return false;
    }
    
    //ȡ����������Χ�ļ�����
    	public Vector around (Chesspoint qizi) {
    		Vector v = new Vector( );
    		for (int i=0;i<xdir.length;i++){
    			int x1=qizi.x+xdir[i];
    			int y1=qizi.y+ydir[i];
    			if (x1<0 ||x1>gridNum ||y1<0 || y1>gridNum ||map[x1][y1] ==null)
    				continue;
    			v.addElement(map[x1][y1]);
    		}
    		return v;
    	}
    	
    	//ȡ������һ���������������
    	public Vector allDead(Chesspoint q) {
    		Vector v= new Vector(  );
    		v.addElement(q);
    		int count=0;
    		while (true) {
    			int origsize= v.size( );
    			for (int i= count; i<origsize;i++) {
    				Chesspoint qizi= (Chesspoint) (v.elementAt(i));
    				if (sideByBlank(qizi))
    					return new Vector( );
    				Vector around = around (qizi);
    				for (int j=0;j<around.size( );j++){
    					 Chesspoint a= (Chesspoint) ( around.elementAt(j));
    					 if (a.color != qizi.color)
    						 continue;
    					 if (v.indexOf(a) < 0)
    						 v.addElement(a);
    				}
    			}
    			if (origsize == v.size( ))
    				break;
    			else
    				count = origsize;
    		}
    		return v;
    	}
    	
    	//����������ȥ����
    	public void removeAll(Vector v) {
    		for( int i=0;i<v.size( );i++ ){
    			Chesspoint q= (Chesspoint) (v.elementAt(i));
    			map[q.x][q.y]=null;
    		}
    		repaint( );
    	}
    	
    	//����
        public void back( ){
        	if (alreadyNum ==0) {
        		controlPanel.setMsg("���ӿɻ�");
        		return;
        	}
        	Object obj=chessman.elementAt(--alreadyNum);
        	if (obj instanceof Chesspoint) {
        		Chesspoint qizi = (Chesspoint) obj;
        		map[qizi.x][qizi.y] =null;
        		currentTurn= qizi.color;
        	} else {
        		Vector v= (Vector) obj;
        		for (int i=0;i<v.size( );i++) {
        			Chesspoint q= (Chesspoint) (v.elementAt(i));
        			if (i==v.size( ) -1) {
        				map[q.x][q.y] =null;
        				int index= chessman.indexOf(v);
        				chessman.setElementAt(q,index);
        				currentTurn =q.color;
        			}else {
        				map[q.x][q.y] =q;
        			}
        		}
        	}
        	controlPanel.setLabel( );
        		repaint( );
        }
        
        //������ٴ�ǰ��
        public void forward( ){
        	if (alreadyNum == chessman.size( )){
        		controlPanel.setMsg("����ǰ��");
        		return;
        	}
        	Object obj =chessman.elementAt(alreadyNum++);
        	Chesspoint qizi;
        	if (obj instanceof Chesspoint) {
                qizi = (Chesspoint) obj;
        		map[qizi.x][qizi.y] =qizi;
        	} else {
        		Vector v=(Vector) obj;
        		qizi= (Chesspoint) (v.elementAt(v.size( )-1));
        		map[qizi.x][qizi.y]=qizi;
        	}
        	if (qizi.color ==Chesspoint.black)
        		currentTurn=Chesspoint.white;
        	else 
        		currentTurn= Chesspoint.black;
        	tizi(qizi.x,qizi.y);
        	controlPanel.setLabel( );
        	repaint( );
        }
        
        //�ؿ���Ϸ
        public void startGame( ){
        	chessman = new Vector( );
        		alreadyNum=0;
        	map = new Chesspoint[gridNum+1][gridNum+1];
        	currentTurn = Chesspoint.black;
        	controlPanel.setLabel( );
        	repaint( );
        }
        
        //���������
        class ControlPanel extends Panel {
        	protected Label lblTurn = new Label ("",Label.CENTER);
        	protected Label lblNum = new Label ("",Label.CENTER);
        	protected Label lblMsg = new Label ("",Label.CENTER);
        	protected Choice choice = new Choice( );
        	protected Button back = new Button("����");
        	protected Button start = new Button("���¿���");
        	public int getWidth( ){
        		return 45;
        	}
        	public int getHeight( ) {
        		return size;
        	}
        	
        	//ѡ�����̴�С
        	public ControlPanel( ){
        		setSize(this.getWidth(),this.getHeight( ));
        		setLayout(new GridLayout(12,1,0,10));
        		setLabel( );
        		choice.add("18 x 18");
        		choice.add("14 x 14");
        		choice.add("12 x 12");
        		choice.add("11 x 11");
        		choice.add(" 7  x  7");
        		choice.addItemListener(new ChessAction());
        		add(lblTurn);
        		add(lblNum);
        		add(start);
        		add(choice);
        		add(lblMsg);
        		add(back);
        		back.addActionListener(new BackChess( ));
        		start.addActionListener(new BackChess( ));
        		setBackground(new Color(120,120,200));
        	}
        	public Insets getInsets( ){
        		return new Insets(5,5,5,5);
        	}
        	
        	//����
        	private class BackChess implements ActionListener {
        		public void actionPerformed(ActionEvent evt) {
        			if (evt.getSource ( )==back)
        				Chessboard.this.back( );
        			else if (evt.getSource( )== start)
        				Chessboard.this.startGame( );
        		} 
        	}
        	
        	//���嶯��
        	private class ChessAction implements ItemListener {
        		public void itemStateChanged(ItemEvent evt) {
        			String s= (String) (evt.getItem( ));
        			int rects = Integer.parseInt(s.substring(0,2).trim( ));
        			if (rects != Chessboard.this.gridNum) {
        				Chessboard.this.gridLen = (gridLen * gridNum) /rects;
        				Chessboard.this.chessmanLength =gridLen * 9/10;
        				Chessboard.this.gridNum=rects;
        				Chessboard.this.startGame ( );
        			}
        		}

				
        	}
        	
        	//���·�����ɫ�벽��
        	public void setLabel ( ) {
        		lblTurn.setText(Chessboard.this.currentTurn == Chesspoint.black?"�ֵ�����":"�ֵ�����");
        		lblTurn.setForeground(Chessboard.this.currentTurn == Chesspoint.black ? Color.black:Color.white);
        		lblNum.setText("�� " + (Chessboard.this.alreadyNum+1)+" ��");
        		lblNum.setForeground(Chessboard.this.currentTurn == Chesspoint.black ? Color.black: Color.white);
        		lblMsg.setText("");
        	}
        	public void setMsg (String msg) {
        		//��ʾ��Ϣ
        		lblMsg.setText(msg);
        	}
        }
    }
        					
        		
        	
        	
        	
    	
    	




