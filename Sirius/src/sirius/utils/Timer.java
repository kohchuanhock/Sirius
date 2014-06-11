package sirius.utils;

public class Timer {
	private long initTime;
	private long previousTime;
	
	public Timer(){
		this(true);
	}
	
	public Timer(boolean feedback){
		if(feedback) System.out.println("Timer created");
		this.initTime = System.currentTimeMillis();
		this.previousTime = System.currentTimeMillis();
	}
		
	public long showTotalTime(){
		long time = System.currentTimeMillis() - this.initTime;
		System.out.print("Total Time: ");
		this.showTime(time);
		return time;
	}
	
	public void showTimeSincePrevious(){
		showTimeSincePrevious("");
	}
	
	public void showTimeSincePrevious(String title){
		long time = System.currentTimeMillis() - this.previousTime;
		this.previousTime = System.currentTimeMillis();
		if(title.length() > 0)
			System.out.print(title + " - Time since previous: ");
		else
			System.out.print("Time since previous: ");
		this.showTime(time);
	}
	
	public void resetTime(){
		this.initTime = System.currentTimeMillis();
	}
	
	private void showTime(long time){
		time = time/1000;
		long timeSec = time % 60;
		long timeMin = time/60 % 60;
		long timeHour = time/(60*60) % 24;
		long timeDay = time/(60*60*24);
		
		if(timeDay > 0){
			System.out.print(timeDay + ":");
		}
		if(timeHour > 0){
			System.out.print(timeHour + ":");
		}
		
		if(timeMin > 0){
			System.out.print(timeMin + ":");
		}
		System.out.println("" + timeSec);
		System.out.println();
	}
	
	public static void main(String[] args){
		Timer t = new Timer();
		while(true){
			try{
				Thread.sleep(1000);
				t.showTimeSincePrevious();
			}catch(Exception e){e.printStackTrace();}
		}
	}
}
