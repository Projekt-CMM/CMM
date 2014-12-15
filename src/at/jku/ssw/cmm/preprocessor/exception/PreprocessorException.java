package at.jku.ssw.cmm.preprocessor.exception;

public class PreprocessorException extends Exception {
	
	private static final long serialVersionUID = -7059546845441562744L;
	
	public PreprocessorException( String msg, String file, int line){
		this.msg = msg;
		this.file = file;
		this.line = line;
	}
	
	private final String msg;
	private final String file;
	private final int line;
	
	public String getMessage(){
		return msg;
	}
	
	public String getFile(){
		return file;
	}

	public int getLine(){
		return line;
	}
}
