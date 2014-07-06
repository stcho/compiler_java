package cse340;

public class SymbolTableItem {
	
	private String type;
	private String scope;
	  
	public SymbolTableItem (String type, String scope) {
		this.type = type;
		this.scope = scope;
	}

	public void insert(String name, String scope, String type) 
	{
			
	}
	public boolean search(String name)
	{
	 return false;
	}
	public boolean search(String name, String scope)
	{
		return false;
	}
	public String getType(String name)
	{
		return "";
	}
	public String getScope(String name)
	{
		return "";
	}	
}
