package kz.edu.nu.cs.se;

public class IdleState extends State {
	public IdleState(VendingMachine vm) {
		vendingMachine = vm;
		vm.balance = 0;
	}
	
	@Override
	public void insertCoin(int coin) {
		vendingMachine.balance += coin;
		vendingMachine.setCurrentState(vendingMachine.enteringCoins);
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