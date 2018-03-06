package taxi_1;

import java.util.Comparator;

public class ComparatorTaxi implements Comparator{

	 public int compare(Object obj0, Object obj1) {
		/*@ REQUIRES: obj0!=NULL,obj1!=NULL ;
		 @ MODIFIES: None;
		 @ EFFECTS: 
		 If(obj0.credit>obj1.credit)==>\result==-1;
		 If(obj0.credit<obj1.credit)==>\result==1;
		 If((obj0.distance<obj1.distance)&&(obj0.credit==obj1.credit))==>\result==-1;
		 If((obj0.distance>obj1.distance)&&(obj0.credit==obj1.credit))==>\result==1;
		 If((obj0.distance==obj1.distance)&&(obj0.credit==obj1.credit))==>\result==0;
		 */
	  Taxi taxi0=(Taxi)obj0;
	  Taxi taxi1=(Taxi)obj1;

	  int flag= (taxi0.credit==taxi1.credit) ? 0 :((taxi0.credit>taxi1.credit)? -1 :1 );
		  if(flag==0){
		   return (taxi0.distance==taxi1.distance) ? 0 :((taxi0.distance<taxi1.distance)? -1 :1 );
		  }else{
		   return flag;
		  }  
	 }
	 
}
