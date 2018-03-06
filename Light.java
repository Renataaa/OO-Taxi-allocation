package taxi_1;

import java.awt.Point;



class Light implements Runnable {
	/* @overview : Light类为线程类，统一控制所有的红绿灯的变换颜色，对于有红绿灯的路口，南北方向和
	 *              东西方向一个为绿灯一个为红灯，lights是存储80*80的路口是否有红绿灯的数组，该坐
	 *              标值为1则证明有红绿灯为0没有
	 * @不变式：go_sx && go_zy == false;
	 *         \all int i, j;  0 <= i  < 80,0 <= j  < 80 ;(lights[i][j]==0||lights[i][j]==1);
	 * 		
	 */
	
	boolean go_sx = true;
	boolean go_zy = false;
	
	int lights[][] = new int[80][80];
	TaxiGUI gui = new TaxiGUI();
	
	public Light(int [][] lights,TaxiGUI gui){
	
		/*@ REQUIRES: NULL;
		@ MODIFIES: this.lights,this.gui;
		@ EFFECTS: this.lights==lights;
		          this.gui == gui;
		*/
		
		this.lights = lights;
		this.gui = gui;
	}
	@Override
	public void run() {
		try{
			//System.out.println(repOK()+"light");
		/*@ REQUIRES: NULL;
		@ MODIFIES: this.go_sx,this.go_zy;
		@ EFFECTS: this.go_sx , this.go_zy 每隔100ms变化;
		*/
		
		// TODO Auto-generated method stub
		while(true){
			int status = go_zy? 1 : 2;
			for(int i = 0; i<80; i++){
				for(int j = 0; j<80; j++){
					if(this.lights[i][j]==1){
						gui.SetLightStatus(new Point(i,j),status);
					}else if(this.lights[i][j]==0){
						gui.SetLightStatus(new Point(i,j),0);
					}
				}
			}
			
			try {
				int a = (int)(50*Math.random()+50);
			
				Thread.sleep(a);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//红绿灯变化的时间
			
			if(go_sx && !go_zy){
				
				go_sx=false;
				go_zy=true;
				
			}else{
				go_sx=true;
				go_zy=false;
			}
			//System.out.println(repOK()+"light");
			
		}
			
		}catch(Exception e){
			System.out.println("极异常情况！！");
		}
	}
	public boolean repOK(){
		if(go_sx && go_zy){
			return false;
		}else if( !go_sx && !go_zy){
			return false;
		}
		for(int m = 0 ; m<80; m++){
			for(int n=0; n<80; n++){
				if (!((lights[m][n]==0)||(lights[m][n]==1))){
					return false;
				}
			}
		}
		return true;
	}

}
