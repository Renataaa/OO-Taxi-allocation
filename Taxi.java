package taxi_1;

import java.awt.Point;
import java.util.ArrayList;
import java.util.ListIterator;

class Taxi {
	/* @overview :出租车类属性包括该出租车的位置，ID，信用值，状态 ，处于等待状态的时间累计，处于停滞状态的时间累计
	 * 			  ，到发出请求位置的距离*/
	/*@不变式：
	 *    ID>=1 && ID<=100;
	 *    src_x>=0 && src_x<=79;
	 * 	   src_y>=0 && src_y<=79;
	 *     state ==0 ||state ==1 ||state ==2 ||state ==3;
	 *     credit>=0;
	 *     distance>=0;
	 *     waittime>=0;
	 *     stoptime>=0;
	 *     direction ==0 ||direction ==1 ||direction ==2 ||direction ==3;
	 *     path_des.repOK==true;
	 *    
	 */
	ArrayList<Req_ins> req_ins = new ArrayList<Req_ins>();
	int ID = 0;
	int src_x = 0;
	int src_y = 0;

	int state = 2; // 0 停止运行 黄色； 1 服务中蓝色； 2 等待服务 红色； 3接单 绿色；
	int credit = 0;

	int distance = 0; // 到请求的距离

	long waittime = 0; ///ms为单位s
	long stoptime = 0 ;
	int direction = 0; // 0为上 1为下2为左 3为右
	Path_des path_des;
	TaxiGUI gui=new TaxiGUI();
	boolean special;
	
	public ListIterator LI(){
		ListIterator<Req_ins> a = req_ins.listIterator();
			return a;
		}
	public Taxi(int src_x, int src_y, int ID , TaxiGUI gui , Path_des path_des) {
	  /*@ REQUIRES: NULL;
		@ MODIFIES:this.src_x,this.src_y ,this.ID,this.gui,this.path_des
		@ EFFECTS: 
				this.src_x == src_x;
				this.src_y == src_y;
				this.ID == ID;
				this.gui == gui;
				this.path_des == path_des;
		*/
		this.src_x = src_x;
		this.src_y = src_y;
		this.ID = ID;
		this.gui = gui;
		this.path_des = path_des;
		special = false;
		
	}

	synchronized void move(int goal_x, int goal_y) {
		
		//System.out.println(repOK()+"taxi");
	  /* @ REQUIRES: NULL;
		@ MODIFIES: this.src_x ,this.src_y,this.path_des;
		@ EFFECTS:  this.src_x == goal_x;
					this.src_y == goal_y;
		@ THREAD_REQUIRES: \locked(move);
		@ THREAD_EFFECTS: \locked(); //对整个方法进行同步控制
		@ */
		
		if(goal_x!=-1){ ///正常情况
			
			if (this.src_x == goal_x) {
				if(goal_y>src_y){
					direction = 3;
				}else{
					direction = 2;
				}
				
				this.src_y = goal_y;
			} else if(this.src_y == goal_y){
				
				if(goal_x>src_x){
					direction = 1;
				}else{
					direction = 0;
				}
				this.src_x = goal_x;
			}
			path_des.addedge(this.src_x,this.src_y, goal_x , goal_y);
		}
		gui.SetTaxiStatus(this.ID, new Point(this.src_x,this.src_y), this.state);
		
		///System.out.println(repOK()+"taxi");
	}
	
	synchronized void addcredit(int a) {
		  /* @ REQUIRES: NULL;
				@ MODIFIES: this.credit;
				@ EFFECTS:  this.credit == this.credit+a;
				@ THREAD_REQUIRES:\locked(addcredit);
				@ THREAD_EFFECTS: \locked() //对整个方法进行同步控制
				@ */		
		this.credit = this.credit + a;
	}

	synchronized void setState(int k) {
		  /* @ REQUIRES: NULL;
		@ MODIFIES: this.state;
		@ EFFECTS:  this.state == k;
		@ THREAD_REQUIRES:\locked(setState);
		@ THREAD_EFFECTS: \locked() //对整个方法进行同步控制
		@ */	
		this.state = k;
	}

	synchronized void setwt(boolean a) {
		/* @ REQUIRES: NULL;
		@ MODIFIES: this.waittime;
		@ EFFECTS:  if(a) then this.waittime = this.waittime + 200
		             else this.waittime = 0;
		@ THREAD_REQUIRES:\locked(setwt);
		@ THREAD_EFFECTS: \locked(); //对整个方法进行同步控制
		@ */	
		if(a){
			this.waittime = this.waittime + 200;
		}else {
			this.waittime = 0;
		}
	}
	
	synchronized void setst(boolean a) {
		/* @ REQUIRES: NULL;
		@ MODIFIES: this.stoptime;
		@ EFFECTS:  if(a) then this.stoptime = this.stoptime + 200
		             else this.stoptime = 0;
		@ THREAD_REQUIRES:\locked(setst);
		@ THREAD_EFFECTS: \locked() //对整个方法进行同步控制
		@ */	
		if(a){
			this.stoptime = this.stoptime + 200;
		}else {
			this.stoptime = 0;
		}
	}
	public boolean repOK(){
		
		if(ID<1 || ID>100){
			return false;
		}
		if(src_x<0 ||src_x >79 ||src_y<0 ||src_y >79){
			return false;
		}
		if(credit<0){
			return false;
		}
		if(distance<0){
			return false;
		}
		if (!(( state ==0 )||( state ==1 )||( state ==2 )||( state ==3 ))){
			return false;
		}
		if((waittime<0) |(stoptime <0)){
			
			return false;
		}
		if(!(( direction ==0 )||( direction ==1 )||( direction ==2 )||( direction ==3 ))){
			return false;
		}
		if(!path_des.repOK()){
			return false;
		}  
		    return true;
	}
}
