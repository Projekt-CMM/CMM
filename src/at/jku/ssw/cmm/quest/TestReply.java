package at.jku.ssw.cmm.quest;

public interface TestReply {
	public void output( final String msg );
	public void finished( final QuestMatchError e );
}
