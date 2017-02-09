package testFunctions;


public class Show<Item> implements Function<Item>{

	@Override
	public void execute(Item p) {
		System.out.println(p);
		
	}

}
