package at.jku.ssw.cmm.quest;

public interface TestReply {
	public void finished( final QuestMatchError e );
	
	public void setInputData(String data);
	public void setCorrectOutput(String data);
	public void setUserOutput(String data);
}
