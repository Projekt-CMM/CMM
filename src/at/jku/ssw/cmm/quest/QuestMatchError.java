package at.jku.ssw.cmm.quest;

public class QuestMatchError {
	
	public QuestMatchError( String msg, Exception e ){
		this.msg = msg;
		this.e = e;
	}
	
	private final String msg;
	private final Exception e;
	
	public String getMessage(){
		return this.msg;
	}
	
	public void print(){
		// Head
		System.out.println("---------------------------------------------------------");
		System.out.println("        Quest output data match failure report");
		System.out.println("---------------------------------------------------------");
		
		// Body + description
		System.out.println("The reason is:		" + msg);
		
		if( this.e != null ){
			System.out.println("Terminated with:	" + e);
			System.out.println("");
			e.printStackTrace();
		}
		else
			System.out.println("... no further information available");
		
		// End
		System.out.println("---------------------------------------------------------");
	}

}
