package taxi_1;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import java.util.Vector;



public class testTaxi {
	//static TaxiGUI gui=new TaxiGUI();
	public static void main(String[] args) throws Exception {
		try{
		// TODO Auto-generated method stub
		ArrayList<Req> req = new ArrayList<Req>();
		String regex =  "\\[CR\\,\\(\\+?[0-9]{1,}\\,\\+?[0-9]{1,}\\)\\,\\(\\+?[0-9]{1,}\\,\\+?[0-9]{1,}\\)\\]";
		String regex2 = "\\[TAXI\\,\\+?[0-9]{1,}\\]";
		String regex3 = "\\[STATE\\,\\+?[0-9]{1,}\\]";
		String regex4 = "\\[CLOSE\\,\\(\\+?[0-9]{1,}\\,\\+?[0-9]{1,}\\)\\,(UP|DOWN|RIGHT|LEFT)\\]";
		String regex5 = "\\[OPEN\\,\\(\\+?[0-9]{1,}\\,\\+?[0-9]{1,}\\)\\,(UP|DOWN|RIGHT|LEFT)\\]";
		Object lock = 1;
	 
		TaxiGUI gui=new TaxiGUI();
		//gui.LoadMap(path_des.map, 80); ///////////////////////画出地图
		String map_path = "D:\\map.txt";
		String light_path = "D:\\light.txt";
		int map[][] = read(map_path);
		int lights[][] = read(light_path);

	
		//////////////////////////////////////////////形成6400地图
		int INF=10000000;
		int spot[][] = new int[6400][6400];
		for(int init1 = 0 ; init1<6400 ; init1 ++){
			for(int init2 = 0 ; init2<6400 ; init2++){
				spot[init1][init2]=INF;
			}
		}
		
		for(int m = 0 ; m<80; m ++){
			for(int n2 = 0 ; n2<80; n2++){
				switch(map[m][n2]){
				   case 0:break;
				   case 1:spot[m*80+n2][m*80+n2+1]=1;spot[m*80+n2+1][m*80+n2]=1;break;
				   case 2:spot[m*80+n2][(m+1)*80+n2]=1;spot[(m+1)*80+n2][m*80+n2]=1;break;
				   case 3:spot[m*80+n2][m*80+n2+1]=1;spot[m*80+n2+1][m*80+n2]=1;spot[m*80+n2][(m+1)*80+n2]=1;spot[(m+1)*80+n2][m*80+n2]=1;break;
				   default:break;
				}
			}
		}
		int edge[][] = new int[6400][6400];
		for(int init1 = 0 ; init1<6400 ; init1 ++){
			for(int init2 = 0 ; init2<6400 ; init2++){
				edge[init1][init2]=0;
			}
		}
		Path_des path_des = new Path_des(gui,spot,map);
		new Thread(path_des).start();
		gui.LoadMap(path_des.map, 80); ///////////////////////画出地图
		Light light = new Light(lights,gui);
	    new Thread(light).start();
		///////////////////////////////////////////////////////////////////////////////////初始化出租车
		ArrayList<Taxi> taxis =new ArrayList<Taxi>(); //=  init_taxi(gui,path_des).clone();
		
		taxis.addAll(init_taxi(gui,path_des));
		
		

		Taxi_thread taxi_thread = new Taxi_thread(taxis,path_des.map,path_des,lights,light);  
		new Thread(taxi_thread).start();
		
		while (true) {
			
			
			long rqst_time = 0;
			BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
			String str = null;
			try {
				str = strin.readLine();
				rqst_time = System.currentTimeMillis();
				
			} catch (IOException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strs[] = str.split(";");
			for(int k = 0 ; k<strs.length ; k++){
			
				strs[k] = strs[k].replace(" ", "");
				if(strs[k].matches(regex)|strs[k].matches(regex2)|strs[k].matches(regex3)|strs[k].matches(regex4)|strs[k].matches(regex5)){
					strs[k] = strs[k].replace(" ", "");
					strs[k] = strs[k].replace("(", "");
					strs[k] = strs[k].replace(")", "");
					strs[k] = strs[k].replace("[", "");
					strs[k] = strs[k].replace("]", "");
					String str_comma[] = strs[k].split(",");
	                if(str_comma[0].equals("TAXI")){
	                	int num = Integer.parseInt(str_comma[1]);
	                	for(int f = 0 ; f<100 ; f++){
	                		if(taxis.get(f).ID == num){
	                			System.out.println(System.currentTimeMillis()+ ":出租车"+num+"号的位置是("+taxis.get(f).src_x+","+taxis.get(f).src_y+"),状态是"+taxis.get(f).state
	                					+"信用值是"+taxis.get(f).credit);
	                		}
	                	}
	                }else if(str_comma[0].equals("STATE")){
	                	int num = Integer.parseInt(str_comma[1]);
	                	System.out.println("处于" + num +"状态下的所有出租车ID为");
	                	for(int f = 0 ; f<100 ; f++){
	                		if(taxis.get(f).state == num){
	                			System.out.print(taxis.get(f).ID+",");
	                		}
	                	}
	                	System.out.println();
	                }else if (str_comma[0].equals("CLOSE")){
	                	int a = Integer.parseInt(str_comma[1]);
						int b = Integer.parseInt(str_comma[2]);
				
	                	path_des.change(a,b,str_comma[3],"CLOSE");
	                	
	                }else if (str_comma[0].equals("OPEN")){
	                	int a = Integer.parseInt(str_comma[1]);
						int b = Integer.parseInt(str_comma[2]);
	                	path_des.change(a,b,str_comma[3],"OPEN");
	                }
	                	else{
	                	boolean flag = false;
	                	rqst_time = (rqst_time / (long) 100) * (long) 100;
	                	int a = Integer.parseInt(str_comma[1]);
						int b = Integer.parseInt(str_comma[2]);
						int c = Integer.parseInt(str_comma[3]);
						int d = Integer.parseInt(str_comma[4]);
	                	for(int reqq = 0 ;reqq<req.size() ; reqq++){
	                		if ((req.get(reqq).src_x == a) && (req.get(reqq).src_y == b)&&(req.get(reqq).dst_x == c)&&(req.get(reqq).dst_y == d)&&(req.get(reqq).time == rqst_time)){
	                			flag = true;
	                			break;
	                		}
	                	}
						if(flag){
							System.out.println("相同请求！:"+strs[k]);
						}else{
						boolean a1 =( (a<0) |(a>79))? true : false;
						boolean b1 =( (b<0) |(b>79))? true : false;
						boolean c1 =( (c<0) |(c>79))? true : false;
						boolean d1 =( (d<0) |(d>79))? true : false;
						boolean e1 = ((a==c) && (b==d))?  true : false;
					
						if(a1|b1|c1|d1){
							System.out.println("坐标不在地图内:"+strs[k]);
						}else if (e1){
							System.out.println("目的地和出发地相同:"+strs[k]);
						}else{
							
							Request rqst = new Request(a, b, c, d, rqst_time, taxis , gui ,lock,path_des,lights,light);
							new Thread(rqst).start();
						}
						req.add(new Req(a,b,c,d ,rqst_time));
	                }
	                }
				}else{
					System.out.println("输入格式不对:"+strs[k]);
				}
				
			}
			
		}
		
		
	}catch(Exception e){
		System.out.println("极异常输入！！");
	}
	}
	
	public static int[][] read(String s) throws Exception{
		int result[][] = new int[80][80];
		FileReader fr = new FileReader(s);
		BufferedReader br = new BufferedReader(fr);
		int n = 0;
		String str_f = null;
		while((str_f = br.readLine()) != null){
			str_f = str_f.replace(" ", "").replace("\t", "");
			if(str_f.length() == 80){
				for(int i = 0; i < 80; i++){
					if(str_f.charAt(i) >= '0' && str_f.charAt(i) <= '3'){
						result[n][i] = str_f.charAt(i) - '0';
					}else{
						System.out.println("wrong input");
					}
				}
				if(n < 80){
					n++;
				}else{
					System.out.println("wrong input");
				}
			}else{
				System.out.println("wrong input");
			}
		}
		br.close();
		return result;
	}

	public static ArrayList<Taxi> init_taxi(TaxiGUI gui, Path_des path_des){
	   /*@ REQUIRES: NULL;
		@MODIFIES:;
		@ EFFECTS:\result == taxis;
				  taxis.size()==100;
		*/
		ArrayList<Taxi> taxis = new ArrayList<Taxi>();
		
		for (int i = 0; i <30; i++) {   ///普通出租车 可以改动个数
			taxis.add(new Taxi((new Random().nextInt(80)), (new Random().nextInt(80)),(i+1),gui,path_des));
			gui.SetTaxiType(i+1, 0);
		}
		for (int j =30; j < 100; j++) {  ///可追踪出租车 可以改动个数
			int k = 0;
			taxis.add(new NewTaxi((new Random().nextInt(80)), (new Random().nextInt(80)),(j+1),gui,path_des, k));
			gui.SetTaxiType(j+1, 1);
		}
	
		return taxis;
		
	
	}

}
		

