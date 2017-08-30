package wq;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
public class Chessboard extends JPanel{
	public static final int _gridLen=22,_gridNum=19;
	//默认的棋盘方格长度及数目
	private Vector chessman;
	private int alreadyNum;//已经下的数目
    private int currentTurn;//轮到谁下
    private int gridNum, gridLen;//长格长度及数目
    private int chessmanLength;//棋子的直径
    private Chesspoint[ ][ ] map;//在棋盘上的所有棋子,包含三个参数，（x，y，黑白方）
    private Image offscreen;
    private Graphics offg;
    private int size;//棋盘宽度及高度
    private int top=13,left=13;//棋盘左边及上边的边距
    private Point mouseLoc;//鼠标的位置，即map数组中的下标
    private ControlPanel controlPanel;//控制面板
    
    //获得控制板的距离
    public int getWidth( ){
    	return size+controlPanel.getWidth( )+35;
    }
    public int getHeigh( ){
    	return size;
    	   }
    
    //绘制棋盘外观
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
    	//画出棋盘格子
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
	
    	
    	//画出棋子
    	for(int i=0;i<gridNum+1;i++)
    		for(int j=0;j<gridNum+1;j++){
    			if(map[i][j]==null)
    				continue;
    			offg.setColor(map[i][j].color==Chesspoint.black?Color.black:Color.white);
    			offg.fillOval(left+i*gridLen-chessmanLength/2,top+j*gridLen-chessmanLength/2,chessmanLength,chessmanLength);
    		}
    		
    		//画出鼠标的位置，即下一步要下的位置
    		if(mouseLoc!=null){
    			offg.setColor(currentTurn==Chesspoint.black?Color.gray:new Color(200,200,250));
    			offg.fillOval(left+mouseLoc.x*gridLen-chessmanLength/2,top+mouseLoc.y*gridLen-chessmanLength/2,chessmanLength,chessmanLength);
    		}
    		
    		//一次性画出画面
    		g.drawImage(offscreen,80,0,this);
    	}

    	//更新棋盘
    	public void update(Graphics g){
    		paint(g);
    	}
    		
    	//下棋子
    	class PutChess extends MouseAdapter{  //放一颗棋子
    		public void mousePressed(MouseEvent evt){
    			int xoff=left/2;
    			int yoff=top/2;
    			int x=(evt.getX( )-xoff)/gridLen ;
    			int y=(evt.getY( )-yoff)/gridLen ;
    			if(x< 0 || x>gridNum || y <0 || y>gridNum )   return;
    			if(map[x][y] != null) return;
    			
    			//清除多余棋子??????这步真的有意义吗
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
    		    
    		    //判断在[x,y]落子后能否踢掉对面的子
    		    tizi(x,y);
    		    
    		    //判断是否挤死了自己，落子无效
    		    if(allDead(qizi).size( )!=0){
    		    	map[x][y]=null;
    		    	repaint( );
    		    	controlPanel.setMsg("挤死自己");
    		    	chessman.removeElement(qizi);
    		    	alreadyNum--;
    		    	   if(currentTurn==Chesspoint.black)
    	    		    	currentTurn=Chesspoint.white;
    	    		    else
    	    		    	currentTurn=Chesspoint.black;
    		    	   return;
    		    	}
    		    mouseLoc=null;
    		    //更新面板
    		    controlPanel.setLabel();
    		}
    		
    		public void mouseExited(MouseEvent evt){//鼠标退出时，清除将要落子位置
    			mouseLoc = null;
    			repaint();
    		}
    	}
    	
    	//取得将要落子的位置
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
    	
    	//判断在[x,y]落子后，是否可以踢子
    	public static int[ ] xdir={0,0,1,-1};
    	public static int[ ] ydir={1,-1,0,0};
    	public void tizi ( int x, int y ) {
    		Chesspoint qizi;
    		if( (qizi =map[x][y]) == null) 
                return;
            int color=qizi.color;
            //取得棋子四周围的几个子
            Vector v = around(qizi);
            for (int i=0;i<v.size( );i++){
            	Chesspoint q=(Chesspoint) (v.elementAt(i));
            	if(q.color==color)
            		continue;
            	//若颜色不同，找到连在一起的所有死掉的子
            	Vector dead=allDead(q);
            	//移去所有已死的子
          removeAll(dead);
          //如果踢子，则保存所有被踢掉的棋子
          if(dead.size( )!=0) {
        	  Object obj= chessman.elementAt(alreadyNum-1);
        	  if(obj instanceof Chesspoint){//如果obj是个Chesspoint类
        		  qizi = (Chesspoint) (chessman.elementAt(alreadyNum-1));
        		  dead.addElement(qizi);
        	  }else{
        		  Vector vector=(Vector) obj;
        		  for(int l=0;l<vector.size( );l++)
        			  dead.addElement(vector.elementAt(l));//更新dead，要包括四个方向的所有踢掉的子，且最后一个元素是alreadyNum-1的棋子
        	  }
        	  //更新Vector chessman中的第num个元素，此时第num个元素是（vector）dead
        	  chessman.setElementAt(dead,alreadyNum-1);//将第alreadyNum-1元素替换成(vector)dead        	  
        	  }
            }
            repaint( );
    	}
    	
        //判断棋子周围是否有空白
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
    
    //取得棋子四周围的几个子
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
    	
    	//取得连在一起的所有已死的子
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
    	
    	//从棋盘上移去棋子
    	public void removeAll(Vector v) {
    		for( int i=0;i<v.size( );i++ ){
    			Chesspoint q= (Chesspoint) (v.elementAt(i));
    			map[q.x][q.y]=null;
    		}
    		repaint( );
    	}
    	
    	//悔棋
        public void back( ){
        	if (alreadyNum ==0) {
        		controlPanel.setMsg("无子可悔");
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
        
        //悔棋后再次前进
        public void forward( ){
        	if (alreadyNum == chessman.size( )){
        		controlPanel.setMsg("不能前进");
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
        
        //重开游戏
        public void startGame( ){
        	chessman = new Vector( );
        		alreadyNum=0;
        	map = new Chesspoint[gridNum+1][gridNum+1];
        	currentTurn = Chesspoint.black;
        	controlPanel.setLabel( );
        	repaint( );
        }
        
        //控制面板类
        class ControlPanel extends Panel {
        	protected Label lblTurn = new Label ("",Label.CENTER);
        	protected Label lblNum = new Label ("",Label.CENTER);
        	protected Label lblMsg = new Label ("",Label.CENTER);
        	protected Choice choice = new Choice( );
        	protected Button back = new Button("悔棋");
        	protected Button start = new Button("重新开局");
        	public int getWidth( ){
        		return 45;
        	}
        	public int getHeight( ) {
        		return size;
        	}
        	
        	//选择棋盘大小
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
        	
        	//悔棋
        	private class BackChess implements ActionListener {
        		public void actionPerformed(ActionEvent evt) {
        			if (evt.getSource ( )==back)
        				Chessboard.this.back( );
        			else if (evt.getSource( )== start)
        				Chessboard.this.startGame( );
        		} 
        	}
        	
        	//下棋动作
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
        	
        	//待下方的颜色与步数
        	public void setLabel ( ) {
        		lblTurn.setText(Chessboard.this.currentTurn == Chesspoint.black?"轮到黑子":"轮到白子");
        		lblTurn.setForeground(Chessboard.this.currentTurn == Chesspoint.black ? Color.black:Color.white);
        		lblNum.setText("第 " + (Chessboard.this.alreadyNum+1)+" 手");
        		lblNum.setForeground(Chessboard.this.currentTurn == Chesspoint.black ? Color.black: Color.white);
        		lblMsg.setText("");
        	}
        	public void setMsg (String msg) {
        		//提示信息
        		lblMsg.setText(msg);
        	}
        }
    }
        					
        		
        	
        	
        	
    	
    	




