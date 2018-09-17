package backup;

public enum CapacityType {
	THREE(3),FOUR(4);
	
	private final int value;
	
	CapacityType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
