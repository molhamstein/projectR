package com.brainSocket.socialrosary.tempPKG;

import android.R.integer;

public class ZickerArr {

	 Zicker[] ArrayOfZicker={
							new Zicker("allh", 1),
							new Zicker("sb7anAllah", 2),
							new Zicker("laellah..", 3),
							new Zicker("ashhd..", 4)
								};
	
	  
	public CharSequence[] getZickers(){
		
		CharSequence[] ZickerAsArray=new CharSequence[ArrayOfZicker.length];
		for (int i = 0; i < ZickerAsArray.length; i++) 
			ZickerAsArray[i]=ArrayOfZicker[i].toString();
		
		return ZickerAsArray;
	}
	

}

class Zicker{
	int i;
	String contenString;
	public Zicker(String content,int j){
		contenString=content;
		i=j;
	}
	public int getCount(){return i;}
	
	public String toString(){
		return "zicker num : "+i+" with content :"+contenString;
	}
	
}	