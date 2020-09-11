public class PaidState extends State {
	public PaidState(VendingMachine vm) {
		vendingMachine = vm;
	}
	
	@Override
	public void insertCoin(int coin) {
		vendingMachine.balance += coin;
	}
	
	@Override
	public int refund() {
		int result = vendingMachine.balance;
		vendingMachine.balance = 0;
		return result;
	}
	
	@Override
	public int vend() {
		vendingMachine.balance -= 200;
		int ref = refund();
		vendingMachine.setCurrentState(vendingMachine.idle);
		return ref;
	}
}
