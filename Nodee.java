package taxi_1;

public class Nodee {
	int x= 0;
	int y = 0;
public Nodee(int x , int y ){
	/*@ overview: 记录点坐标信息，x为横坐标，y为纵坐标，均为0-79之间的数包括0和79*/
    // @不变式:  x>=0 && x<=79; 
	//			  y>=0 && y<=79;
	
	/*@ REQUIRES: NULL;
	@ MODIFIES: this.x,this.y;
	@ EFFECTS: this.x==x;
	          this.y==y;
	*/
	this.x = x;
	this.y = y;
}
public boolean repOK(){
	if((x<0)||(x>79)){
		return false;
	}if((y<0)||(y>79)){
		return false;
	}
	return true;
}
}
