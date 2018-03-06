package taxi_1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class NewTaxi extends Taxi{

	boolean special ;
	//ArrayList<Req_ins> req_ins;

	
	public NewTaxi(int src_x, int src_y, int ID, TaxiGUI gui, Path_des path_des,int k) {
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
		 *     super.special == true;
		 */
	
		super(src_x, src_y, ID, gui, path_des);
		super.special = true;	
		
	}
	
/*	
	public ListIterator LI(){
	ListIterator<Req_ins> a = req_ins.listIterator();
		return a;
	}
	*/
	public boolean repOK(){
	
		if(!super.repOK()){
			return false;
		}
		if(!super.special){
			return false;
		}
		return true;
	}
}
