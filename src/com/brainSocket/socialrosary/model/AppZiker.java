package com.brainSocket.socialrosary.model;


public class AppZiker  {		

	int i;
	String content;
	
	public  AppZiker(String content,int j){
		this.content=content;
		i=j;
	}
	public int getCount(){return i;}
	
	public String toString(){
		return content;
	}
        
	
	public String getContent() {
		return content;
	}
}
