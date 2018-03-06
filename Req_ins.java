package taxi_1;

import java.util.ArrayList;

class Req_ins {
	Req req ;
	int taxi_x;
	int taxi_y;
	ArrayList<Integer> come = new ArrayList<Integer>();
	ArrayList<Integer> go= new ArrayList<Integer>();
	public Req_ins(int x, int y, Req req ,ArrayList<Integer> come,ArrayList<Integer> go ){
		this.taxi_x = x;
		this.taxi_y = y;
		this.req = req;
		this.go.addAll(go);
		this.come.addAll(come);
	}
}
