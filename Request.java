package taxi_1;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

class Request implements Runnable{
	/*@overview: 存储了用户输入请求的坐标和请求时间 请求发出地和目的地
	 *          的横纵坐标均为0-79的int型整数 包括0和79  time为大于0的long型数 
	 *          lights代表没个坐标点是否有红绿灯 值为0或1*/
	/*@不变式：
	 * src_x>=0 && src_x<=79;
	 * 	src_y>=0 && src_y<=79;
	 * 	dst_x>=0 && dst_x<=79;
	 * 	dst_y>=0 && dst_y<=79;
	 *  time>=0;
	 *  path_des.repOK == true;
	 *  lt.repOK == true;
	 *  \all int i, j;  0 <= i  < 80,0 <= j  < 80 ;(lights[i][j]==0||lights[i][j]==1);
	 *  \all int i;  0 <= i  <taxis.size() ; taxis.get(i).repOK == true;
	 */
	ArrayList<Integer> come = new ArrayList<Integer>();
	ArrayList<Integer> go = new ArrayList<Integer>();
	int src_x = 0; // 请求发出的横坐标
	int src_y = 0; //请求发出的纵坐标
	int dst_x = 0;//请求的目的横坐标
	int dst_y = 0;//请求的目的纵坐标
	long time = 0; //请求发出的时间 精度100ms
	int tmpx = 0;
	int tmpy = 0;
	Object lock  = new Object();
	Path_des path_des;
	TaxiGUI gui=new TaxiGUI();
	ArrayList<Taxi> taxis = new ArrayList<Taxi>(); //存储全部100出租车的动态数组
	int [][]lights = new int [80][80];
	Light lt;
	//int[][] init_spot = new int[6400][6400];
	public Request(int src_x, int src_y, int dst_x, int dst_y, long time, ArrayList<Taxi> taxis,TaxiGUI gui,Object lock,Path_des path_des,int [][]lights , Light lt) {
		/*@ REQUIRES: NULL;
		@MODIFIES: this.src_x,this.src_y,this.dst_x,this.dst_y,this.taxis,this.time,this.gui,this.lock,this.path_des;
		@ EFFECTS:this.src_x == src_x;
				this.src_y == src_y;
				this.dst_x == dst_x;
				this.dst_y == dst_y;
				this.taxis == taxis;
				this.time == time;
				this.gui == gui;
				this.lock == lock;
				this.path_des == path_des;
		*/
		this.src_x = src_x;
		this.src_y = src_y;
		this.dst_x = dst_x;
		this.dst_y = dst_y;
		this.taxis = taxis;
		this.time = time;
	
		this.gui = gui;
		this.lock = lock;
		this.path_des = path_des;
		this.lights= lights;
		this.lt=lt;
		//this.init_spot=path_des.spot;
		
	}

	public void run() {
		
		/*@ REQUIRES: NULL;
		@ MODIFIES: this.taxis;
		@ EFFECTS: find a lucky taxi which is appropriate for this paticular request, and let it run as the shorttest path found by Dijkstra;
		*/
		try{
			//System.out.println(repOK()+"request");
		String path_op = "("+ String.valueOf(src_x)+","+String.valueOf(src_y)+")"+".txt";
		String path_opp = "("+ String.valueOf(src_x)+","+String.valueOf(src_y)+")";
		gui.RequestTaxi(new Point(src_x,src_y), new Point(dst_x,dst_y));
		long now = System.currentTimeMillis();
		/////////////////////////////////////////////////为了输出
		ArrayList<Taxi> taxi_hit_op = new ArrayList<Taxi>(); // 抢单的出租车动态数组
		for (int i = 0; i < this.taxis.size(); i++) {
			boolean flag1 = false;
			boolean flag2 = false;
			flag1 = (taxis.get(i).state == 2); // 出租车为等待服务状态״̬
			flag2 = (Math.abs(taxis.get(i).src_x - this.src_x) <= 4) // 4*4以内
					&& (Math.abs(taxis.get(i).src_y - this.src_y) <= 4);
		
			if (flag1 && flag2) {
				taxi_hit_op.add(taxis.get(i));
			}

		}
		for(int op = 0 ; op<taxi_hit_op.size() ; op++){
			toFile(path_opp + "发出时刻: car -" + taxi_hit_op.get(op).ID + "信用值:" + taxi_hit_op.get(op).credit +"状态"+taxi_hit_op.get(op).state +"\r\n",path_op);
		}
		///////////////////////////////////////////////////////////////////
		now = (now / (long) 100) * (long) 100; // 精度为100ms
		long now2 = now + 3000; /// 发出请求 3s
		ArrayList<Taxi> taxi_hit = new ArrayList<Taxi>(); // 抢单的出租车动态数组
		
		while (true) {
			long now3 = System.currentTimeMillis();
			if (now3 > now2) { /// 3s
				break;
			} else {
				for (int i = 0; i < this.taxis.size(); i++) {
					boolean flag1 = false;
					boolean flag2 = false;
					flag1 = (taxis.get(i).state == 2); // 出租车为等待服务状态״̬
					flag2 = (Math.abs(taxis.get(i).src_x - this.src_x) <= 4) // 4*4以内
							&& (Math.abs(taxis.get(i).src_y - this.src_y) <= 4);
					boolean flag_repeat = false;
					
					for(int rp = 0 ; rp<taxi_hit.size() ; rp++){
						if(taxis.get(i).ID ==taxi_hit.get(rp).ID ){
							flag_repeat = true;
							break;
						}
					}
					if (flag1 && flag2 && !flag_repeat) { ////等待服务状态在4*4以内
					
						taxi_hit.add(taxis.get(i));
					}

				}
			}
		}

		
		for (int k = 0; k < taxi_hit.size(); k++) {
			int distance =(Math.abs(this.src_x - taxi_hit.get(k).src_x))*(Math.abs(this.src_x - taxi_hit.get(k).src_x))
					      + (Math.abs(this.src_y - taxi_hit.get(k).src_y)) *(Math.abs(this.src_y - taxi_hit.get(k).src_y));
			taxi_hit.get(k).distance = distance;
		}
		if(taxi_hit.size()==0){
			System.out.println("[CR,("+this.src_x+","+this.src_y+"),("+this.dst_x+","+this.dst_y+")]:无出租车相应");
		}else{
		///// 将抢单的出租车信用值+1 并进行排序
		for (int up_c = 0; up_c < taxi_hit.size(); up_c++) {
			taxi_hit.get(up_c).addcredit(1);
		}
		ComparatorTaxi comparator = new ComparatorTaxi();
		synchronized (lock){
			
			for(int del = 0 ; del<taxi_hit.size();del++){
				if(taxi_hit.get(del).state!=2){
					taxi_hit.remove(del);
				}
			}
			if(taxi_hit.size()==0){
				System.out.println("[CR,("+this.src_x+","+this.src_y+"),("+this.dst_x+","+this.dst_y+")]无出租车相应***");
			}else{
				Collections.sort(taxi_hit, comparator);
				taxi_hit.get(0).setState(3);
			}
		}
		if(taxi_hit.size()!=0){
		for(int op = 0 ; op<taxi_hit.size() ; op++){
					toFile("抢单的出租车: car -" + taxi_hit.get(op).ID + "信用值:" + taxi_hit.get(op).credit +"状态"+taxi_hit.get(op).state + 
					         "当前位置(" + taxi_hit.get(op).src_x +","+taxi_hit.get(op).src_y+")"+"距离"+taxi_hit.get(op).distance+"\r\n",path_op);
		}
		
	
		toFile("系统选择的出租车： car --" + taxi_hit.get(0).ID +"\r\n",path_op);
		
		taxi_hit.get(0).move(-1, -1);////显示一下
	
		 tmpx =taxi_hit.get(0).src_x;
		 tmpy =taxi_hit.get(0).src_y;
		
		int taxi_position = (taxi_hit.get(0).src_x) * 80 + (taxi_hit.get(0).src_y); 
		int rqst_position = (this.src_x ) * 80 + (this.src_y );
		int goal_position = (this.dst_x ) * 80 + (this.dst_y);
		int goal_temp = goal_position;
		
		int path_total[]  = findshort(taxi_hit.get(0).special,rqst_position);//= Dijkstra(rqst_position, 6400, this.path_des.spot);
	
		
		while (taxi_position != rqst_position) {
			int k = path_total[taxi_position];
			// System.out.println(k);
			come.add(k);
			taxi_position = k;
		}

		while (goal_position != rqst_position) {
			int k = path_total[goal_position];
			go.add(0,k);
			goal_position = k;
		}
		
		go.remove(0);
		go.add(goal_temp);

		//////////////////////// 出租车从所在地到乘客
		
		
		for ( int move_come = 0; move_come < come.size(); move_come++) {
			boolean break_flag = false;

			 int temp_come = come.get(move_come) ; ///
			 int temp_come_x = temp_come / 80;
			 int temp_come_y = temp_come % 80;

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for(int k = move_come+1 ; k<come.size() ; k++){
				if(this.path_des.spot[come.get(k)][come.get(k-1)]!=1){
					break_flag = true;
					break_flag = break_flag && !taxi_hit.get(0).special;
					break;
				}
			}
	
			if(!break_flag){
				if(this.lights[taxi_hit.get(0).src_x][taxi_hit.get(0).src_y]==0){ //该路口没有红绿灯
					taxi_hit.get(0).move(temp_come_x, temp_come_y);
				}else if (this.lights[taxi_hit.get(0).src_x][taxi_hit.get(0).src_y]==1){
					
					while((lights[taxi_hit.get(0).src_x][taxi_hit.get(0).src_y]==1)
						     && (((taxi_hit.get(0).direction == 3) && (!lt.go_zy) && temp_come_y == taxi_hit.get(0).src_y+1 ) //车头向右 左右为红灯 想向右走
						   || ((taxi_hit.get(0).direction == 3) && lt.go_zy && temp_come_x == taxi_hit.get(0).src_x-1 )//车头向右 左右可通行 想向上走
						   || ((taxi_hit.get(0).direction == 0) && (!lt.go_sx) && temp_come_x == taxi_hit.get(0).src_x-1 ) //车头向上 上下为红灯 想向上走
						   || ((taxi_hit.get(0).direction == 0) && (lt.go_sx) && temp_come_y == taxi_hit.get(0).src_y-1) //车头向上 上下为绿灯 想向左走
						   || ((taxi_hit.get(0).direction == 2) && (!lt.go_zy) && temp_come_y == taxi_hit.get(0).src_y-1 ) //车头向左 左右为红灯 想向左走
						   || ((taxi_hit.get(0).direction == 2) && (lt.go_zy) && temp_come_x == taxi_hit.get(0).src_x+1 ) //车头向左 左右为绿灯 想向下走
						   || ((taxi_hit.get(0).direction == 1) && (!lt.go_sx) && temp_come_x == taxi_hit.get(0).src_x+1 ) //车头向下 上下为红灯 想向下走
						   || ((taxi_hit.get(0).direction == 1) && (lt.go_sx) && temp_come_y == taxi_hit.get(0).src_y+1 )) //车头向下 上下为绿灯 想向右走
			             ){  try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}}
					taxi_hit.get(0).move(temp_come_x, temp_come_y);
				}
				
				
				//taxi_hit.get(0).move(temp_come_x, temp_come_y);
				//mark=move_come;
			}else{
				
				int tmp = come.size();
				 for(int i = move_come ; i<tmp ; i++){
					  come.remove(move_come);
				  }
			  int path_total_cb[] = Dijkstra(rqst_position, 6400, this.path_des.spot);
			  while ((taxi_hit.get(0). src_x * 80 +taxi_hit.get(0). src_y )!= rqst_position) {
				int k = path_total_cb[taxi_position];
				come.add(k);
				taxi_position = k;
				}
			  move_come--;
	
		}
		}
		////////////////////////////////////// ͣ停1s
		taxi_hit.get(0).setState(0);
		taxi_hit.get(0).move(-1, -1);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/////////////////////////////////////////////// 出租车从乘客到目的地
		taxi_hit.get(0).setState(1);/////服务中
		taxi_hit.get(0).move(-1, -1);

		for (int move_go = 0; move_go < go.size(); move_go++) {
			boolean break_flag_go = false;
			taxi_hit.get(0).setState(1);
			taxi_hit.get(0).move(-1, -1);
			int temp_go = go.get(move_go) ; ///
			int temp_go_x = temp_go / 80;
			int temp_go_y = temp_go % 80;

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(move_go+"/////");
			for(int k = move_go ; k<go.size()-1 ; k++){
				if(this.path_des.spot[go.get(k+1)][go.get(k)]!=1){
					break_flag_go = true;
					break_flag_go = break_flag_go && !taxi_hit.get(0).special;
				
					break;
				}
			}
			if(!break_flag_go){ 
				
				while((lights[taxi_hit.get(0).src_x][taxi_hit.get(0).src_y]==1)
					     && (((taxi_hit.get(0).direction == 3) && (!lt.go_zy) && temp_go_y == taxi_hit.get(0).src_y+1 ) //车头向右 左右为红灯 想向右走
						   || ((taxi_hit.get(0).direction == 3) && lt.go_zy && temp_go_x == taxi_hit.get(0).src_x-1 )//车头向右 左右可通行 想向上走
						   || ((taxi_hit.get(0).direction == 0) && (!lt.go_sx) && temp_go_x == taxi_hit.get(0).src_x-1 ) //车头向上 上下为红灯 想向上走
						   || ((taxi_hit.get(0).direction == 0) && (lt.go_sx) && temp_go_y == taxi_hit.get(0).src_y-1) //车头向上 上下为绿灯 想向左走
						   || ((taxi_hit.get(0).direction == 2) && (!lt.go_zy) && temp_go_y == taxi_hit.get(0).src_y-1 ) //车头向左 左右为红灯 想向左走
						   || ((taxi_hit.get(0).direction == 2) && (lt.go_zy) && temp_go_x == taxi_hit.get(0).src_x+1 ) //车头向左 左右为绿灯 想向下走
						   || ((taxi_hit.get(0).direction == 1) && (!lt.go_sx) && temp_go_x == taxi_hit.get(0).src_x+1 ) //车头向下 上下为红灯 想向下走
						   || ((taxi_hit.get(0).direction == 1) && (lt.go_sx) && temp_go_y == taxi_hit.get(0).src_y+1 ))){
						  try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						   
					}
				//taxi_hit.get(0).move(temp_come_x, temp_come_y);
				taxi_hit.get(0).move(temp_go_x, temp_go_y);
			}else{	
				 System.out.println("break");
				
					int tmp = go.size();
				 for(int i = move_go ; i<tmp ; i++){
					  go.remove(move_go);
				  }
		
				taxi_position = taxi_hit.get(0).src_x*80 + taxi_hit.get(0).src_y;
				int path_total_gb[] = Dijkstra(taxi_position, 6400, this.path_des.spot);

				goal_position  = this.dst_x*80+this.dst_y;
				
				while ((goal_position)!= taxi_position) {
					int k = path_total_gb[goal_position];
					go.add(move_go,k);
					goal_position = k;
				}
				go.add(this.dst_x*80+this.dst_y);
				
				move_go--;
			}
		
		}
		///////////////////////////////////////////////////////////////////////////////////////
		toFile("路线输出",path_op);

	  for (int move_come1 = 0; move_come1 < come.size(); move_come1++) {

			int temp_come1 = come.get(move_come1) ; ///
			int temp_come_x1 = temp_come1 / 80;
			int temp_come_y1 = temp_come1 % 80;
		
			toFile( "("+temp_come_x1+","+temp_come_y1+")"+"\r\n",path_op);

		}
		for (int move_go = 0; move_go < go.size(); move_go++) {

			int temp_go = go.get(move_go) ; ///
			int temp_go_x = temp_go / 80;
			int temp_go_y = temp_go % 80;
			
			toFile( "("+temp_go_x+","+temp_go_y+")"+"\r\n",path_op);
		}
	

		/////////////////////////////////////////////////////出租车到目的地睡1s
		
		taxi_hit.get(0).setState(0);
		taxi_hit.get(0).move(-1, -1);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		taxi_hit.get(0).addcredit(3);
		taxi_hit.get(0).setState(2);
		taxi_hit.get(0).move(-1, -1);
		
		//////////////////////////////可追踪出租车
		if(taxi_hit.get(0).special){
			Req tmprq =  new Req(src_x,src_y,dst_x,dst_y,time);
			taxi_hit.get(0).req_ins.add(new Req_ins(this.tmpx,this.tmpy, tmprq,this.come,this.go));
		}
		if(taxi_hit.get(0).special){
			testLI();
		}
		}
		}
		
		}catch(Exception e){
		System.out.println("程序异常");
	}
	}

	public static int[] Dijkstra(int v0, int VNUM, int[][] Weights) {
		/*@ REQUIRES: v0<VNUM;
		@ MODIFIES: NULL;
		@ EFFECTS: \result==Spath[];
		         (the result  is not the direct path, but user can find the precursor node for every node int the map)
		*/
		int i = 0;
		int j = 0;
		int v = 0;
		int minweight = 0;
		int Spath[] = new int[VNUM];
		int Sweight[] = new int[VNUM];
		boolean wfound[] = new boolean[VNUM];
		for (int o = 0; o < VNUM; o++) {
			wfound[o] = false;
		}
		for (i = 0; i < VNUM; i++) {
			Sweight[i] = Weights[v0][i];
			Spath[i] = v0;
		}
	
		Sweight[v0] = 0;
		wfound[v0] = true;

		for (i = 0; i < VNUM - 1; i++) {
			minweight = 100000000;

			for (j = 0; j < VNUM; j++) {
				if (!wfound[j] && (Sweight[j] < minweight)) {
					v = j;
					minweight = Sweight[v];
				}
			}
			wfound[v] = true; //

			for (j = 0; j < VNUM; j++) // ֵ
				if ((!wfound[j]) && ((minweight + Weights[v][j]) < Sweight[j])) {

					Sweight[j] = minweight + Weights[v][j];
					Spath[j] = v; //
				}
		}
		return Spath;
	}

	public static void toFile(String str, String path){
		/*@ REQUIRES: path is a legal File Path ;
		@ MODIFIES: new File(path);
		@ EFFECTS: write str into new File(path)
		*/
		Charset charset = Charset.forName("UTF-8");
		FileOutputStream out;
		try {
			out = new FileOutputStream(path , true);
			
				try {
					out.write(str.getBytes(charset));
				} catch (IOException e1) {
					e1.printStackTrace();
				}		
			try {
				out.close();
			} catch (IOException e) {		
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public  int[] findshort(boolean a, int origin){
		if(a){
		
			int path_total[] = Dijkstra(origin, 6400, this.path_des.spot_init);
			return path_total;
		}else{
			int path_total2[] = Dijkstra(origin, 6400, this.path_des.spot);
			return path_total2;
		}
	}

	public boolean repOK(){
	    if(src_x<0 ||src_x >79 ||src_y<0 ||src_y >79){
			return false;
		}else if(dst_x<0 ||dst_x >79 ||dst_y<0 ||dst_y >79){
			return false;
		}else if(time<0){
			return false;
		}
	    for(int m = 0 ; m<80; m++){
			for(int n=0; n<80; n++){
				if (!((lights[m][n]==0)||(lights[m][n]==1))){
					return false;
				}
			}
		}
	    for(int j = 0;j<taxis.size();j++){
	    	if(!taxis.get(j).repOK()){
	    		return false;
	    	}
	    }
	    if(!path_des.repOK()){
	    	return false;
	    }
	    if(!lt.repOK()){
	    	return false;
	    }
	    return true;
	}

	public void testLI(){
		for(int i = 0 ; i<taxis.size() ; i++){
			if(taxis.get(i).req_ins.size()!=0){
				System.out.println("///////////从程序启动开始可追踪"+taxis.get(i).ID+"号出租车共执行了"+taxis.get(i).req_ins.size()+"次单");
				ListIterator a = taxis.get(i).LI();
				while(a.hasNext()){
					Req_ins b = (Req_ins) a.next();
					
					System.out.println("出租车抢到的单：time:"+b.req.time+":[CR,("+b.req.src_x+","+b.req.src_y+"),"+"("+b.req.dst_x+","+b.req.dst_y+")]");
					System.out.println("出租车接单时的位置：(" +b.taxi_x+","+b.taxi_y+")");
					System.out.println("出租车前往乘客的路线");
					  for (int move_come1 = 0; move_come1 < b.come.size(); move_come1++) {

							int temp_come1 = b.come.get(move_come1) ; ///
							int temp_come_x1 = temp_come1 / 80;
							int temp_come_y1 = temp_come1 % 80;
						
							System.out.print( "("+temp_come_x1+","+temp_come_y1+")");

						}
					  System.out.println();
					  System.out.println("出租车接到乘客前往目的地的路线");
						for (int move_go = 0; move_go < b.go.size(); move_go++) {

							int temp_go = b.go.get(move_go) ; ///
							int temp_go_x = temp_go / 80;
							int temp_go_y = temp_go % 80;
							
							System.out.print( "("+temp_go_x+","+temp_go_y+")");
						}
						System.out.println();
				
				}
				
			}
		}
	}
}