import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.util.Vector;

class Game extends JFrame implements MouseListener{
	private int width=400,height=450,mapRow=9,mapCol=9; //width:�����e�Bheight:�������BmapRow:�a��row�BmapCol:�a��col�C
	private JButton button[][]=new JButton[width][height]; //���s�C
	private int bombCount=10; //�T�w10�Ӭ��u�C
	private JLabel bombnumber=new JLabel("�ثe���u�ơG"+bombCount);

	private int map[][]=new int[mapRow][mapCol]; //�a�ϡC
	private boolean buttonIsPress[][]=new boolean[mapRow][mapCol]; //�P�_���s�O�_�����C
	private int mapAroundBomb[][]=new int[mapRow][mapCol]; //�P�򦳦h�֬��u�C
	private int direct[][]={{0,0},{0,1},{0,-1},{1,0},{-1,0},{1,1},{-1,-1},{-1,1},{1,-1}}; //8���C
	
	private int timeCount=0; //�ɶ��p�ơC
	private int timeContinue=1; //�ɶ��~��p��or����C1:�~��B0:����C
	
	Game(){
		/*�����C*/
		setSize(width, height); //�]�w�j�p�C
		setResizable(false); //�]�w�j�p�����i�վ�C
		setDefaultCloseOperation(EXIT_ON_CLOSE); //�]�w�������s���ʧ@�C
		setTitle("Minesweeper"); //�]�w���D�C
		setLocationRelativeTo(this); //��ܬ��m���C
		
		/*TopBar�C*/
		JPanel topPanel=new JPanel();
		
		bombnumber.setText("�ثe���u�ơG"+bombCount); //��ܥثe�аO�h�֬��u�C
		topPanel.add(bombnumber);
		
		JButton restart=new JButton("�s��"); //���s�}�l���s�C
		restart.setActionCommand("r"); //�]�w���O�C
		restart.addMouseListener(this); //�[�J��ť�C
		topPanel.add(restart);

		JLabel time=new JLabel("�w�g�L�ɶ��G0"); //��ܥثe�w�g�L�ɶ�(��)�C
		TimerTask timertask=new TimerTask(){
			public void run(){
				if(timeContinue==1)time.setText("�w�g�L�ɶ��G "+(timeCount++));
			}
		};
		new Timer().scheduleAtFixedRate(timertask,0,1000);
		topPanel.add(time);
		
		add(topPanel,BorderLayout.NORTH);

		/*�a�ϫ��s�C*/
		JPanel centerButtonPanel = new JPanel();
        centerButtonPanel.setLayout(new GridLayout(mapRow,mapCol));
        for(int i=0;i<mapRow;i++){
        	for(int j=0;j<mapCol;j++){
        		button[i][j]=new JButton();
        		button[i][j].setBackground(Color.WHITE); //�]�w���s�I���C��C
        		button[i][j].setActionCommand(i+" "+j); //�]�w���s���O�C
        		button[i][j].addMouseListener(this); //���s�[�J��ť�C
        		centerButtonPanel.add(button[i][j]);
        	}
        }
        add(centerButtonPanel,BorderLayout.CENTER);
        
        /*�]�w�a�ϡB��X�y�ЩP�򦳦h�֬��u*/
        setMap();
        aroundBomb();
        
        setVisible(true);
	}
	
	/**********************
	 *�]�w�a�� :�����u:1�B�L���u:0*
	 **********************/
	private void setMap(){
		int count=0;
		while(count!=10){
			int x=(int)(Math.random()*9),y=(int)(Math.random()*9); //�üƳ]�w���u�y�СC
			if(map[x][y]==0){
				map[x][y]=1;
				count++;
			}
		}
	}
	
	/******************
	 *�P�_�y�ЩP��@���h�֬��u*
	 ******************/
	private void aroundBomb(){
		for(int i=0;i<mapRow;i++){
			for(int j=0;j<mapCol;j++){
				if(map[i][j]==1){
					mapAroundBomb[i][j]=-1; //���u�����]�w��-1�C
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
	 *���s�}�l*
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
        bombnumber.setText("�ثe���u�ơG"+bombCount);
        
	}
	
	/******************************************************
	 *�����ƹ��ƥ�GBUTTON1(�ƹ�����)�BBUTTON2(�u��)�BBUTTON3(�ƹ��k��)*
	 ******************************************************/
	@Override
	public void mouseClicked(MouseEvent e){
		String command[]=((JButton)e.getSource()).getActionCommand().split(" ");
		if(command[0].equals("r")){
			/*���s�}�l*/
			
			restart();
		}else{
			int row=Integer.parseInt(command[0]),col=Integer.parseInt(command[1]);
			if(e.getButton()==MouseEvent.BUTTON1){
				if(map[row][col]==1 && !buttonIsPress[row][col]){
					/*�a�p�A�B���s�S���L�C*/
					
					button[row][col].setBackground(Color.RED); //�]�w���s�I��������C
					for(int i=0;i<mapRow;i++)for(int j=0;j<mapCol;j++) if(map[i][j]==1) button[i][j].setText("*"); //�L�X�Ҧ����u�C
					timeContinue=0; //�ɶ�����p�ɡC
					JOptionPane.showMessageDialog(null, "�A���a�p�F"); //��ܥ��ѰT���C
					restart(); //���s�}�l�C
				}else{
					if(mapAroundBomb[row][col]==0 && !buttonIsPress[row][col]){
						/*�����P��S���u�����s�h�X���A�B���s�S���L�C*/
						
						Vector<postion> vector=new Vector<postion>(); //�����ݭn�X�����I�C
						vector.add(new postion(row,col));
						//�P�_�I�O�_�ŦX�X�����ݨD�A����vector����Ƴ��B�z���C
						for(int i=0;i<vector.size();i++){
							for(int j=0;j<direct.length;j++){
								int tempRow=direct[j][0]+vector.get(i).getRow(),tempCol=direct[j][1]+vector.get(i).getCol();
								if((tempRow>=0 && tempRow<mapRow) && (tempCol>=0 && tempCol<mapCol) && mapAroundBomb[tempRow][tempCol]==0){
									boolean flag=false;
									//�ˬd�O�_�w�g�x�s������ơC
									for(int k=0;k<vector.size();k++){
										if(tempRow==vector.get(k).getRow() && tempCol==vector.get(k).getCol()){
											flag=true;
											break;
										}
									}
									if(!flag) vector.add(new postion(tempRow,tempCol)); //���X���I���s�b�h�x�s�_�ӡC
								}
							}
						}
						//�}�l�X���C
						for(int i=0;i<vector.size();i++){
							for(int j=0;j<direct.length;j++){
								int tempRow=direct[j][0]+vector.get(i).getRow(),tempCol=direct[j][1]+vector.get(i).getCol();
								if((tempRow>=0 && tempRow<mapRow) && (tempCol>=0 && tempCol<mapCol)){
									//�D0�Ʀr�~�L�X�ӡC
									if(mapAroundBomb[tempRow][tempCol]!=0) 
										button[tempRow][tempCol].setText(Integer.toString(mapAroundBomb[tempRow][tempCol]));
									button[tempRow][tempCol].setBackground(Color.GRAY); //�]�w���s�I���C��C
									buttonIsPress[tempRow][tempCol]=true; //�]�w���s�����L�C
								}
							}
						}
					}else if(!buttonIsPress[row][col]){
						/*���O���u�B�]�����X�����I�C*/
						
						button[row][col].setText(Integer.toString(mapAroundBomb[row][col])); //�L�X�Ʀr�C
						button[row][col].setBackground(Color.GRAY); //�]�w���s�I���C��C
						buttonIsPress[row][col]=true; //�]�w���s�����L�C
					}
				}
			}else if(buttonIsPress[row][col] && e.getButton()==MouseEvent.BUTTON2){
				/*�����аO�����u���áC*/
				
				buttonIsPress[row][col]=false; //���������C
				button[row][col].setBackground(Color.WHITE); //�]�w���s�I���C��C
				bombCount++; //���u�ơC
				bombnumber.setText("�ثe���u�ơG"+bombCount); 
			}else if(e.getButton()==MouseEvent.BUTTON3 && !buttonIsPress[row][col]){
				/*�аO���u�C�çP�_�O�_�����C���C*/
				
				((JButton)e.getSource()).setBackground(Color.GREEN); //�]�w���s�I���C��C
				buttonIsPress[row][col]=true; //�]�w���s�����L�C
				bombCount--; //���u�ơC
				bombnumber.setText("�ثe���u�ơG"+bombCount);
				
				//�P�_�O�_�����C���C
				if(bombCount==0){
					boolean endGame=true;
					//�P�_���a�p�����s�O�_�w�g�аO�C
					for(int i=0;i<mapRow;i++){
						for(int j=0;j<mapCol;j++){
							if(map[i][j]==1)if(buttonIsPress[i][j]!=true) endGame=false;
						}
					}
					if(endGame){
						timeContinue=0; //�ɶ�����p�ɡC
						JOptionPane.showMessageDialog(null, "���߯}��"); //��ܯ}���T���C
						restart(); //���s�}�l�C���C
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