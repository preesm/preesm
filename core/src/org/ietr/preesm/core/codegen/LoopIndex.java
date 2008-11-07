package org.ietr.preesm.core.codegen;

public class LoopIndex extends Variable {

	char indexName;

	public LoopIndex(char name, DataType type) {
		super(((Character) name).toString(), type);
		indexName = name;
	}

	public LoopIndex(String name, DataType type) {
		super(name, type);
	}

	public char getNameAsChar() {
		return indexName;
	}

}
