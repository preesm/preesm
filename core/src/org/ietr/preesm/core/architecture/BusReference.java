/**
 * 
 */
package org.ietr.preesm.core.architecture;

/**
 * As defined in IP-XACT, a bus definition characterizes interfaces and defines
 * their interconnectability 
 * 
 * @author mpelcat
 */
public class BusReference {


	private String id;

	public String getId() {
		return id;
	}

	public BusReference(String id) {
		super();
		this.id = id;
	}

	@Override
	protected Object clone() {

		BusReference newRef = new BusReference(this.id);
		return newRef;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof BusReference)
			if(((BusReference) obj).getId().equals(getId()))
				return true;
		
		return false;
	}
	
}
