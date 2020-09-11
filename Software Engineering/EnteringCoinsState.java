package kz.edu.nu.cs.se;

public class EnteringCoinsState extends State {
	public EnteringCoinsState(VendingMachine vm) {
		vendingMachine = vm;
	}
	
	@Override
	public void insertCoin(int coin) {
		vendingMachine.balance += coin;
		if (vendingMachine.balance >= 200) {
			vendingMachine.setCurrentState(vendingMachine.paid);
		}
	}
	
	@Override
	public int refund() {
		int result = vendingMachine.balance;
		vendingMachine.balance = 0;
		return result;
	}
	
	@Override
	public int vend() {
		throw new IllegalStateException("Error in vending!");
	}
}