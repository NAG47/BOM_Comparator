package dataStructures;

public class HashTable<Key, Value> {
	
	private int M;
	private SymbolTable<Key, Value>[] st;
	private int size;
	
	public HashTable(){
		this(997);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashTable(int M){
		this.M = M;
		st = (SymbolTable<Key, Value>[]) new SymbolTable[M];
		for(int i = 0; i < M; i++){
			st[i] = new SymbolTable();
		}
	}
	
	private int hash(Key key){
		return (key.hashCode() & 0x7fffffff) % M;
	}
	
	public Value get(Key key){
		return (Value)st[hash(key)].get(key);
	}
	
	public void put(Key key, Value val){
		size++;
		st[hash(key)].put(key, val);
	}
	
	public int getSize(){
		return size;
	}
	
	public Bag<Value> toList(){
		Bag<Value> b = new Bag<Value>();
		for (int i = 0; i < st.length; i++) {
			for(Value v : st[i]){
				b.add(v);
			}
		}
		return b;
	}

}
