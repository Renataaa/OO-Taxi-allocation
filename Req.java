package taxi_1;

import java.util.ArrayList;

public class Req {
	/*@overview: 存储了用户输入请求的坐标和请求时间 请求发出地和目的地
	 *          的横纵坐标均为0-79的int型整数 包括0和79  time为大于0的long型数 */
	/*@不变式：src_x>=0 && src_x<=79;
	 * 	src_y>=0 && src_y<=79;
	 * 	dst_x>=0 && dst_x<=79;
	 * 	dst_y>=0 && dst_y<=79;
	 *  time>=0;
	 */
	int src_x = 0; // 请求发出的横坐标
	int src_y = 0; //请求发出的纵坐标
	int dst_x = 0;//请求的目的横坐标
	int dst_y = 0;//请求的目的纵坐标
	long time = 0; //请求发出的时间 精度100ms
	public Req(int src_x, int src_y, int dst_x, int dst_y, long time) {
		/*@ REQUIRES: NULL;
		@ MODIFIES: this.src_x,this.src_y,this.dst_x,this.dst_y,this.time;
		@ EFFECTS: this.src_x == src_x;
				  this.src_y == src_y;
				  this.dst_x == dst_x;
				  this.dst_y == dst_y;
		           this.time == time;
		*/
		this.src_x = src_x;
		this.src_y = src_y;
		this.dst_x = dst_x;
		this.dst_y = dst_y;
        this.time = time;
	}
	public boolean repOK(){
	    if(src_x<0 ||src_x >79 ||src_y<0 ||src_y >79){
			return false;
		}else if(dst_x<0 ||dst_x >79 ||dst_y<0 ||dst_y >79){
			return false;
		}else if(time<0){
			return false;
		}
	    return true;
	}
}
