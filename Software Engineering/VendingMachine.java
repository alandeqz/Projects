public class VendingMachine {
    public final State idle = new IdleState(this);
    public final State enteringCoins = new EnteringCoinsState(this);
    public final State paid = new PaidState(this);
    
    int balance;
    
    private State currentState;
    
    public VendingMachine() {
    	balance = 0;
    	setCurrentState(idle);
    }
    
    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
    
    public int getBalance() {
        return balance;
    }
    
    public void insertCoin(int coin) {
    	if(coin != 50 && coin != 100) {
			throw new IllegalArgumentException("Unacceptable coin!");
		}
    	currentState.insertCoin(coin);
    }
    
    public int refund() {
    	int ref = currentState.refund();
    	balance = 0;
    	setCurrentState(idle);
    	return ref;
    }
    
    public int vend() {
    	int result = currentState.vend();
    	return result;
    }
}
