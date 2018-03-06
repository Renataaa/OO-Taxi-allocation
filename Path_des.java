package taxi_1;

import java.awt.Point;

class Path_des implements Runnable{
	/*@overview: Path_des 类 存储了代表各个点是否连通的数组 spot[][]
	 * 						 存储了代表两点间的流量（200ms时间窗 edge[][]
	 * 						 存储了读入的地图 map[][]				 
	 * */
	/*@不变式:\all int i, j;  0 <= i  < 6400,0 <= j  < 6400 ;(spot[i][j]==10000000||spot[i][j]==1);
	 * 		  \all int i, j;  0 <= i  < 6400,0 <= j  < 6400 ;(edge[i][j]>=0);
	 		  \all int i, j;  0 <= i  < 80,0 <= j  < 80 ;(map[i][j]==0||map[i][j]==1||map[i][j]==2||map[i][j]==3);
	 	*/
	int  spot[][] = new int[6400][6400]; // 0 1代表通不通
    int spot_init[][] = new int[6400][6400];
	int edge[][] = new int[6400][6400]; // 数值代表流量
	int map [][] = new int[80][80];
	TaxiGUI gui = new TaxiGUI();

	Path_des(TaxiGUI gui, int spot[][], int map[][]) {
		/*@ REQUIRES: NULL;
		@ MODIFIES: this.gui,this.spot,this.map;
		@ EFFECTS: this.gui==gui;
		          this.spot==spot;
		          this.map==map;
		*/
		this.gui = gui;
		this.spot = spot;
		this.map = map;
		
		for(int i =0 ; i<6400; i++){
			for(int j =0 ; j<6400 ; j++){
				this.spot_init[i][j]=spot[i][j];
			}
		}
	}
	@Override
	public void run() {
		/*@ REQUIRES: NULL;
		@ MODIFIES: this.edge;
		@ EFFECTS: 每隔200ms 流量矩阵清零
		*/
		//System.out.println(repOK()+"pathdes");
		while (true){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i = 0 ;i<6400;i++){
				for(int j =0; j<6400 ; j++){
					this.edge[i][j] = 0;
				}
			}
		//System.out.println(repOK()+"pathdes");
		}
		
	}
	void addedge(int a, int b , int c , int d){
		/*@ REQUIRES: NULL;
		@ MODIFIES: this.edge;
		@ EFFECTS: this.edge[a*80+b][c*80+d] ==this.edge[a*80+b][c*80+d]+1;
				  this.edge[c*80+d][a*80+b]==this.edge[c*80+d][a*80+b];
		*/
		edge[a*80+b][c*80+d] ++;
		edge[c*80+d][a*80+b] ++;
	}

	void change(int a, int b, String dir, String act) {
		/*@ REQUIRES: dir==”UP”||dir==”DOWN”||dir==”LEFT”||dir==”RIGHT”;
        act==”CLOSE”||act==”OPEN”;
	@ MODIFIES: this.spot,this.map;
	@ EFFECTS: if already open|| already closed  then remind the user
     else open or close the required road, and update the map[][] and spot[][]
*/
		/// (a,b),(c,d)要改变的 act是打开还是关闭
		
		boolean flaga =((a<0) |(a>79))? true : false;
		boolean flagb =( (b<0) |(b>79))? true : false;
		boolean flagc = ((a==0)&& dir.equals("UP"))?true:false;
		boolean flagd = ((a==79)&& dir.equals("DOWN") )? true : false;
		boolean flage =((b==0)&& dir.equals("LEFT"))? true : false;
		boolean flagf =((b==79)&& dir.equals("RIGHT"))? true : false;
		
		if(flaga|flagb|flagc|flagd|flage|flagf){
			System.out.println("超出地图范围[("+a+","+b+"),"+dir+","+act+")]");
		}else
		{
		
		
		boolean right = (b == 79) ? false : ((this.map[a][b] == 1) | (this.map[a][b] == 3));
		boolean down = (a == 79) ? false : ((this.map[a][b] == 2) | (this.map[a][b] == 3));
		boolean up = (a == 0) ? false : ((this.map[a - 1][b] == 2) | (this.map[a - 1][b] == 3));
		boolean left = (b == 0) ? false : ((this.map[a][b - 1] == 1) | (this.map[a][b - 1] == 3));

		if (act.equals("OPEN")) {
			boolean repeat = (dir.equals("RIGHT") && right) || (dir.equals("LEFT") && left) || (dir.equals("UP") && up)
					|| (dir.equals("DOWN") && down);
			if (repeat) {
				System.out.println("Already open!!!");
			} else {

				if (dir.equals("RIGHT")) {
					this.spot[a * 80 + b][a * 80 + b + 1] = 1;
					this.spot[a * 80 + b + 1][a * 80 + b] = 1;
					this.map[a][b] = (this.map[a][b] == 0) ? 1 : 3;
					gui.SetRoadStatus(new Point(a,b), new Point(a,b+1), 1);
				} else if (dir.equals("LEFT")) {
					this.spot[a * 80 + b][a * 80 + b - 1] = 1;
					this.spot[a * 80 + b - 1][a * 80 + b] = 1;
					this.map[a][b - 1] = (this.map[a][b - 1] == 0) ? 1 : 3; // 左边的点
					gui.SetRoadStatus(new Point(a,b), new Point(a,b-1), 1);
				} else if (dir.equals("UP")) {
					this.spot[a * 80 + b][(a - 1) * 80 + b] = 1;
					this.spot[(a - 1) * 80 + b][a * 80 + b] = 1;
					this.map[a - 1][b] = (this.map[a - 1][b] == 0) ? 2 : 3; // 上边的点
					gui.SetRoadStatus(new Point(a,b), new Point(a-1,b), 1);
				} else if (dir.equals("DOWN")) {
					this.spot[a * 80 + b][(a + 1) * 80 + b] = 1;
					this.spot[(a + 1) * 80 + b][a * 80 + b] = 1;
					this.map[a][b] = (this.map[a][b] == 0) ? 2 : 3;
					gui.SetRoadStatus(new Point(a,b), new Point(a+1,b), 1);
				}
			}
		} else if (act.equals("CLOSE")) {
			
			
			boolean repeat = (dir.equals("RIGHT") && !right) || (dir.equals("LEFT") && !left) || (dir.equals("UP") && !up)
					|| (dir.equals("DOWN") && !down);
			if (repeat) {
				System.out.println("Already closed!!!");
			} else {

				if (dir.equals("RIGHT")) {
					this.spot[a * 80 + b][a * 80 + b + 1] = 10000000;
					this.spot[a * 80 + b + 1][a * 80 + b] = 10000000;
					this.map[a][b] = (this.map[a][b] == 1) ? 0 : 2;
					gui.SetRoadStatus(new Point(a,b), new Point(a,b+1), 0);
				} else if (dir.equals("LEFT")) {
					this.spot[a * 80 + b][a * 80 + b - 1] = 10000000;
					this.spot[a * 80 + b - 1][a * 80 + b] = 10000000;
					this.map[a][b - 1] = (this.map[a][b - 1] == 1) ? 0 : 2; // 左边的点
					gui.SetRoadStatus(new Point(a,b), new Point(a,b-1), 0);
				} else if (dir.equals("UP")) {
					this.spot[a * 80 + b][(a - 1) * 80 + b] = 10000000;
					this.spot[(a - 1) * 80 + b][a * 80 + b] = 10000000;
					this.map[a - 1][b] = (this.map[a - 1][b] == 2) ? 0 : 1; // 上边的点
					gui.SetRoadStatus(new Point(a,b), new Point(a-1,b), 0);
				} else if (dir.equals("DOWN")) {
					this.spot[a * 80 + b][(a + 1) * 80 + b] = 10000000;
					this.spot[(a + 1) * 80 + b][a * 80 + b] = 10000000;
					this.map[a][b] = (this.map[a][b] == 2) ? 0 : 1;
					gui.SetRoadStatus(new Point(a,b), new Point(a+1,b), 0);
				}
			
			}
			
		}
	}
	}
	public boolean repOK(){
		for(int i = 0 ;i<6400 ; i++){
			for(int j=0; j<6400 ;j++){
				if (!((spot[i][j] == 10000000) || (spot[i][j] == 1) )){
					return false;
				}
			}
		}
		for(int i = 0 ;i<6400 ; i++){
			for(int j=0; j<6400 ;j++){
				if ( edge[i][j]<0){
					return false;
				}
		}
		}
		for(int i = 0 ;i<80 ; i++){
			for(int j=0; j<80 ;j++){
				if (!((map[i][j] == 0) || (map[i][j] == 1)||(map[i][j] == 2)|| (map[i][j] == 3))){
					return false;
				}
		}
		}
		return true;
	}
	
}
