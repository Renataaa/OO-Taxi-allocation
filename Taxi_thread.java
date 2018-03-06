package taxi_1;

import java.util.ArrayList;

class Taxi_thread implements Runnable {
	/*@overview: lights代表每个坐标点是否有红绿灯 值为0或1
	 * 			  map为读入的地图信息   
	 * 			  taxis为100个出租车动态数组 */
	
	/*@不变式:
	 *  \all int i;  0 <= i  <taxis.size() ; taxis.get(i).repOK == true;
	 *   \all int i, j;  0 <= i  < 80,0 <= j  < 80 ;(map[i][j]==0||map[i][j]==1||map[i][j]==2||map[i][j]==3);
	 *  path_des.repOK == true;
	 *  lt.repOK == true;
	 *     \all int i, j;  0 <= i  < 80,0 <= j  < 80 ;(lights[i][j]==0||lights[i][j]==1);
	 */
	ArrayList<Taxi> taxis = new ArrayList<Taxi>();
	int map[][] = new int[80][80];
	Path_des path_des;
	int lights[][] = new int[80][80];
	Light lt;
	
	public Taxi_thread(ArrayList<Taxi> taxis, int [][]map, Path_des path_des,int[][] lights, Light lt) {
		/*@ REQUIRES: NULL;
		@ MODIFIES:this.taxis,this.map ,this.path_des;
		@ EFFECTS: 
		this.taxis == taxis;
		this.map == map;
		this.path_des== path_des;
		*/
		this.taxis = taxis;
		this.map = map;
		this.path_des= path_des;
		this.lights=lights;
		this.lt = lt;
	}

	@Override
	public void run() {
		/*@ REQUIRES: NULL;
		@ MODIFIES:this.taxis;
		@ EFFECTS: let every taxi which is in the state “2” (waiting for Request) , move one edge per 200ms. And if one car run 20s in the state “2”, let it stop 1s .
		*/
		try{
		// TODO Auto-generated method stub
		while (true) {
			try {
				Thread.sleep(200); // 200ms一个格子
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//System.out.println(repOK()+"taxithread");
			for (int i = 0; i < taxis.size(); i++) {
				int now_x = taxis.get(i).src_x;  ///0-79
				int now_y = taxis.get(i).src_y;  ///0-79
			
				if ((taxis.get(i).state == 2) && (taxis.get(i).waittime<20000)) { /// 等待服务状态的出租车
					int cur_dir = taxis.get(i).direction;
					
					boolean right =  (now_y==79)? false: ((this.map[now_x][now_y] == 1) | (this.map[now_x][now_y] == 3));
					boolean down =  (now_x==79)? false: ((this.map[now_x][now_y] == 2) | (this.map[now_x][now_y] == 3));
					boolean up =  (now_x==0)?false:((this.map[now_x - 1][now_y] == 2) | (this.map[now_x - 1][now_y] == 3));
					boolean left =(now_y==0) ?false:((this.map[now_x][now_y - 1] == 1) | (this.map[now_x][now_y - 1] == 3));
					
					if(this.lights[taxis.get(i).src_x][taxis.get(i).src_y]==0){
						
					}else if(this.lights[taxis.get(i).src_x][taxis.get(i).src_y]==1){ //有红绿灯
						if(cur_dir ==3){ //车头向右
							right  = right && lt.go_zy;
							left = false;
							up = up && !lt.go_zy; 
							//不变 down 
						}
						if(cur_dir == 0){ //车头向上
							//right 为右转 不变
							up = up && lt.go_sx;
							left = left && !lt.go_sx;
							down = false;
						}if(cur_dir ==1){ //车头向下
							//left不变
							down  = down && lt.go_sx;
							right = right && !lt.go_sx;
							up = false;
						}if(cur_dir ==2){ //车头向左
							//up不变
							left = left && lt.go_zy;
							down = down && !lt.go_zy;
							right = false;
						}
					}
			
					ArrayList<Nodee> nodes = new ArrayList<Nodee>();
					nodes.clear();
					if (up) {
						nodes.add(new Nodee(taxis.get(i).src_x - 1, taxis.get(i).src_y));
					}
					if (down) {
						nodes.add(new Nodee(taxis.get(i).src_x + 1, taxis.get(i).src_y));
					}
					if (right) {
						nodes.add(new Nodee(taxis.get(i).src_x, taxis.get(i).src_y + 1));
					}
					if (left) {
						nodes.add(new Nodee(taxis.get(i).src_x, taxis.get(i).src_y - 1));
					}
					///当前流量最小值
					int temp_edge = this.path_des.edge[taxis.get(i).src_x * 80 + taxis.get(i).src_y][ nodes.get(0).x *80 + nodes.get(0).y];
					ArrayList<Integer> min = new ArrayList<Integer>();  //当前流量最小的全部下标
					for(int k = 1 ; k<nodes.size() ; k++){
						if(this.path_des.edge[taxis.get(i).src_x * 80 + taxis.get(i).src_y][ nodes.get(k).x *80 + nodes.get(k).y]<temp_edge){
							for(int del= 0; del<min.size() ;del++){
								nodes.remove(min.get(del));
							}
							min.clear();
							min.add(k);
							temp_edge = this.path_des.edge[taxis.get(i).src_x * 80 + taxis.get(i).src_y][ nodes.get(k).x *80 + nodes.get(k).y];
						}else if(this.path_des.edge[taxis.get(i).src_x * 80 + taxis.get(i).src_y][ nodes.get(k).x *80 + nodes.get(k).y]>temp_edge){
							nodes.remove(k);
						}else if(this.path_des.edge[taxis.get(i).src_x * 80 + taxis.get(i).src_y][ nodes.get(k).x *80 + nodes.get(k).y]==temp_edge){
							min.add(k);
						}
					}
					if(nodes.size()==1){	
						taxis.get(i).move(nodes.get(0).x, nodes.get(0).y);
					}else if(nodes.size()==2){
						int temp_choose = (int)(Math.random()*2);////0或者1
						taxis.get(i).move(nodes.get(temp_choose).x, nodes.get(temp_choose).y);
					
					}else if(nodes.size()==3){
						int temp_choose2 = (int)(Math.random()*3);////012
						taxis.get(i).move(nodes.get(temp_choose2).x, nodes.get(temp_choose2).y);
					
					}else if(nodes.size()==4){
						int temp_choose3 = (int)(Math.random()*4);////0123
						taxis.get(i).move(nodes.get(temp_choose3).x, nodes.get(temp_choose3).y);
					}
					taxis.get(i).setwt(true);////加200ms

				}else if(((taxis.get(i).waittime>=20000)|(taxis.get(i).state == 0)) && (taxis.get(i).stoptime<1000)){///运行了20s
					
					taxis.get(i).setwt(false); //等待服务计数归0
					taxis.get(i).setState(0);  //停止运行
					taxis.get(i).setst(true);  //将停止运行的时间+200ms
					taxis.get(i).move(-1,-1);
					
				}else if(taxis.get(i).stoptime>=1000){
					
					taxis.get(i).setState(2); //回到等待服务状态
					taxis.get(i).setst(false); //停止运行时间归0
				}
			}
			//System.out.println(repOK()+"taxithread");
		}
		
	}catch(Exception e){
		System.out.println("程序异常");
	}
}
	public boolean repOK(){
		 for(int m = 0 ; m<80; m++){
				for(int n=0; n<80; n++){
					if (!((lights[m][n]==0)||(lights[m][n]==1))){
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
}
