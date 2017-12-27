package dbms;

import java.util.ArrayList;

public class tupleElement {
	ArrayList<String> valueList = new ArrayList<String>();

	public tupleElement() {
		
	}
	public tupleElement(ArrayList<String> valueList) {
		this.valueList = valueList;
	}
	public void add(String item) {
		valueList.add(item);
	}
	public String get(int i) {
		return valueList.get(i);
	}
	public int size() {
		return valueList.size();
	}
	public boolean isEmpty(){
		return valueList.isEmpty();
	}
	public void replace(int i, String item) {
		valueList.set(i, item);
	}
	public void clear() {
		valueList.clear();
	}
}
