package at.jku.ssw.cmm.quest;

public class TestReplyMock implements TestReply {

	@Override
	public void output(String msg) {
		
		System.out.println("[TestReply] " + msg);
	}

	@Override
	public void finished(QuestMatchError e) {
		
		System.out.println("[TestFinished] " + e);
	}
}
