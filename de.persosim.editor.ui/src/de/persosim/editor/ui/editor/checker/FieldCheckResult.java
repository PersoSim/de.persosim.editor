package de.persosim.editor.ui.editor.checker;

public class FieldCheckResult {
	
	public enum State {
		OK, WARNING, ERROR
	}
	
	public static final FieldCheckResult OK = new FieldCheckResult("", State.OK);

	private String reason;
	private State state;
	
	public FieldCheckResult(String reason, State state) {
		super();
		this.reason = reason;
		this.state = state;
	}

	public String getReason(){
		return reason;
	}
	
	public State getState(){
		return state;
	}
}
