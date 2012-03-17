package jeu;

public class State {

	private boolean running;
	public GameState gameState;

	public State() {

		running = true;
		gameState = GameState.MENU;

	}

	public GameState getStatue() {
		return gameState;
	}

	public void set(GameState gameState) {
		this.gameState = gameState;
	}

	public boolean isRunning() {

		return running;
	}

	public void setRunning(boolean running) {

		this.running = running;
	}

	enum GameState {

		MENU, GAME, EDIT, DIALOGUE;

	}
}
