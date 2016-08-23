import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.util.Vector;

class Game extends JFrame implements MouseListener{
	private int width=400,height=450,mapRow=9,mapCol=9; //width:視窗寬、height:視窗長、mapRow:地圖row、mapCol:地圖col。
	private JButton button[][]=new JButton[width][height]; //按鈕。
	private int bombCount=10; //固定10個炸彈。
	private JLabel bombnumber=new JLabel("目前炸彈數："+bombCount);

	private int map[][]=new int[mapRow][mapCol]; //地圖。
	private boolean buttonIsPress[][]=new boolean[mapRow][mapCol]; //判斷按鈕是否按壓。
	private int mapAroundBomb[][]=new int[mapRow][mapCol]; //周圍有多少炸彈。
	private int direct[][]={{0,0},{0,1},{0,-1},{1,0},{-1,0},{1,1},{-1,-1},{-1,1},{1,-1}}; //8方位。
	
	private int timeCount=0; //時間計數。
	private int timeContinue=1; //時間繼續計數or停止。1:繼續、0:停止。
	
	Game(){
		/*視窗。*/
		setSize(width, height); //設定大小。
		setResizable(false); //設定大小為不可調整。
		setDefaultCloseOperation(EXIT_ON_CLOSE); //設定關閉按鈕的動作。
		setTitle("Minesweeper"); //設定標題。
		setLocationRelativeTo(this); //顯示為置中。
		
		/*TopBar。*/
		JPanel topPanel=new JPanel();
		
		bombnumber.setText("目前炸彈數："+bombCount); //顯示目前標記多少炸彈。
		topPanel.add(bombnumber);
		
		JButton restart=new JButton("新局"); //重新開始按鈕。
		restart.setActionCommand("r"); //設定指令。
		restart.addMouseListener(this); //加入監聽。
		topPanel.add(restart);

		JLabel time=new JLabel("已經過時間：0"); //顯示目前已經過時間(秒)。
		TimerTask timertask=new TimerTask(){
			public void run(){
				if(timeContinue==1)time.setText("已經過時間： "+(timeCount++));
			}
		};
		new Timer().scheduleAtFixedRate(timertask,0,1000);
		topPanel.add(time);
		
		add(topPanel,BorderLayout.NORTH);

		/*地圖按鈕。*/
		JPanel centerButtonPanel = new JPanel();
        centerButtonPanel.setLayout(new GridLayout(mapRow,mapCol));
        for(int i=0;i<mapRow;i++){
        	for(int j=0;j<mapCol;j++){
        		button[i][j]=new JButton();
        		button[i][j].setBackground(Color.WHITE); //設定按鈕背景顏色。
        		button[i][j].setActionCommand(i+" "+j); //設定按鈕指令。
        		button[i][j].addMouseListener(this); //按鈕加入監聽。
        		centerButtonPanel.add(button[i][j]);
        	}
        }
        add(centerButtonPanel,BorderLayout.CENTER);
        
        /*設定地圖、找出座標周圍有多少炸彈*/
        setMap();
        aroundBomb();
        
        setVisible(true);
	}
	
	/**********************
	 *設定地圖 :有炸彈:1、無炸彈:0*
	 **********************/
	private void setMap(){
		int count=0;
		while(count!=10){
			int x=(int)(Math.random()*9),y=(int)(Math.random()*9); //亂數設定炸彈座標。
			if(map[x][y]==0){
				map[x][y]=1;
				count++;
			}
		}
	}
	
	/******************
	 *判斷座標周圍共有多少炸彈*
	 ******************/
	private void aroundBomb(){
		for(int i=0;i<mapRow;i++){
			for(int j=0;j<mapCol;j++){
				if(map[i][j]==1){
					mapAroundBomb[i][j]=-1; //炸彈本身設定為-1。
				}else{
					for(int k=0;k<direct.length;k++){
						int row=i+direct[k][0],col=j+direct[k][1];
						if((row>=0 && row<mapCol && col>=0 && col<mapCol) && map[row][col]==1) mapAroundBomb[i][j]++;
					}
				}
			}
		}
	}
	
	/*******
	 *重新開始*
	 *******/
	private void restart(){
		timeCount=1;
		timeContinue=1;
		for(int i=0;i<mapRow;i++) Arrays.fill(map[i],0); //initial map array
		for(int i=0;i<mapRow;i++) Arrays.fill(buttonIsPress[i],false); //initial buttonIsPress
		for(int i=0;i<mapRow;i++) Arrays.fill(mapAroundBomb[i],0); //initial mapAroundBomb
		
		for(int i=0;i<mapRow;i++){
        	for(int j=0;j<mapCol;j++){
        		button[i][j].setBackground(Color.WHITE);
        		button[i][j].setText("");
        	}
        }
		setMap();
		aroundBomb();
        bombCount=10;
        bombnumber.setText("目前炸彈數："+bombCount);
        
	}
	
	/******************************************************
	 *紀錄滑鼠事件：BUTTON1(滑鼠左鍵)、BUTTON2(滾輪)、BUTTON3(滑鼠右鍵)*
	 ******************************************************/
	@Override
	public void mouseClicked(MouseEvent e){
		String command[]=((JButton)e.getSource()).getActionCommand().split(" ");
		if(command[0].equals("r")){
			/*重新開始*/
			
			restart();
		}else{
			int row=Integer.parseInt(command[0]),col=Integer.parseInt(command[1]);
			if(e.getButton()==MouseEvent.BUTTON1){
				if(map[row][col]==1 && !buttonIsPress[row][col]){
					/*地雷，且按鈕沒按過。*/
					
					button[row][col].setBackground(Color.RED); //設定按鈕背景為紅色。
					for(int i=0;i<mapRow;i++)for(int j=0;j<mapCol;j++) if(map[i][j]==1) button[i][j].setText("*"); //印出所有炸彈。
					timeContinue=0; //時間停止計時。
					JOptionPane.showMessageDialog(null, "你踩到地雷了"); //顯示失敗訊息。
					restart(); //重新開始。
				}else{
					if(mapAroundBomb[row][col]==0 && !buttonIsPress[row][col]){
						/*當按到周圍沒炸彈的按鈕則擴散，且按鈕沒按過。*/
						
						Vector<postion> vector=new Vector<postion>(); //紀錄需要擴散的點。
						vector.add(new postion(row,col));
						//判斷點是否符合擴散的需求，直到vector的資料都處理完。
						for(int i=0;i<vector.size();i++){
							for(int j=0;j<direct.length;j++){
								int tempRow=direct[j][0]+vector.get(i).getRow(),tempCol=direct[j][1]+vector.get(i).getCol();
								if((tempRow>=0 && tempRow<mapRow) && (tempCol>=0 && tempCol<mapCol) && mapAroundBomb[tempRow][tempCol]==0){
									boolean flag=false;
									//檢查是否已經儲存此筆資料。
									for(int k=0;k<vector.size();k++){
										if(tempRow==vector.get(k).getRow() && tempCol==vector.get(k).getCol()){
											flag=true;
											break;
										}
									}
									if(!flag) vector.add(new postion(tempRow,tempCol)); //此擴散點不存在則儲存起來。
								}
							}
						}
						//開始擴散。
						for(int i=0;i<vector.size();i++){
							for(int j=0;j<direct.length;j++){
								int tempRow=direct[j][0]+vector.get(i).getRow(),tempCol=direct[j][1]+vector.get(i).getCol();
								if((tempRow>=0 && tempRow<mapRow) && (tempCol>=0 && tempCol<mapCol)){
									//非0數字才印出來。
									if(mapAroundBomb[tempRow][tempCol]!=0) 
										button[tempRow][tempCol].setText(Integer.toString(mapAroundBomb[tempRow][tempCol]));
									button[tempRow][tempCol].setBackground(Color.GRAY); //設定按鈕背景顏色。
									buttonIsPress[tempRow][tempCol]=true; //設定按鈕為按過。
								}
							}
						}
					}else if(!buttonIsPress[row][col]){
						/*不是炸彈、也不須擴散的點。*/
						
						button[row][col].setText(Integer.toString(mapAroundBomb[row][col])); //印出數字。
						button[row][col].setBackground(Color.GRAY); //設定按鈕背景顏色。
						buttonIsPress[row][col]=true; //設定按鈕為按過。
					}
				}
			}else if(buttonIsPress[row][col] && e.getButton()==MouseEvent.BUTTON2){
				/*取消標記的炸彈按紐。*/
				
				buttonIsPress[row][col]=false; //取消按壓。
				button[row][col].setBackground(Color.WHITE); //設定按鈕背景顏色。
				bombCount++; //炸彈數。
				bombnumber.setText("目前炸彈數："+bombCount); 
			}else if(e.getButton()==MouseEvent.BUTTON3 && !buttonIsPress[row][col]){
				/*標記炸彈。並判斷是否結束遊戲。*/
				
				((JButton)e.getSource()).setBackground(Color.GREEN); //設定按鈕背景顏色。
				buttonIsPress[row][col]=true; //設定按鈕為按過。
				bombCount--; //炸彈數。
				bombnumber.setText("目前炸彈數："+bombCount);
				
				//判斷是否結束遊戲。
				if(bombCount==0){
					boolean endGame=true;
					//判斷有地雷的按鈕是否已經標記。
					for(int i=0;i<mapRow;i++){
						for(int j=0;j<mapCol;j++){
							if(map[i][j]==1)if(buttonIsPress[i][j]!=true) endGame=false;
						}
					}
					if(endGame){
						timeContinue=0; //時間停止計時。
						JOptionPane.showMessageDialog(null, "恭喜破關"); //顯示破關訊息。
						restart(); //重新開始遊戲。
					}
				}
			}
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
class postion{
	private int row,col;
	postion(int row,int col){
		this.row=row;
		this.col=col;
	}
	public int getRow(){
		return row;
	}
	public int getCol(){
		return col;
	}
}
class minesweeper{
	public static void main(String args[]){
		Game g=new Game();
	}
}