/**
 * 
 */
package org.ietr.preesm.core.architecture.parser;

/**
 * This class represents the IP-XACT identifier containing Vendor,Library,Name
 * and Version of a component.
 * 
 * @author mpelcat
 */
public class VLNV {

	String vendor;
	String library;
	String name;
	String version;

	public VLNV() {
		super();
		this.vendor = "";
		this.library = "";
		this.name = "";
		this.version = "";
	}

	public VLNV(String vendor, String library, String name, String version) {
		super();
		this.vendor = vendor;
		this.library = library;
		this.name = name;
		this.version = version;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VLNV) {
			VLNV cmpVlnv = (VLNV) obj;
			return vendor.equals(cmpVlnv.vendor)
					&& library.equals(cmpVlnv.library)
					&& name.equals(cmpVlnv.name)
					&& version.equals(cmpVlnv.version);
		}
		return false;
	}

	/*
	 * @Override public String toString() { return
	 * vendor+";"+library+";"+name+";"+version; }
	 */
}
