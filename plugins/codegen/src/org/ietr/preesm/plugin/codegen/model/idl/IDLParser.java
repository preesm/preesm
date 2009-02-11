package org.ietr.preesm.plugin.codegen.model.idl;

import org.jacorb.idl.ConstDecl;
import org.jacorb.idl.GlobalInputStream;
import org.jacorb.idl.IDLTreeVisitor;
import org.jacorb.idl.NameTable;
import org.jacorb.idl.TypeMap;
import org.jacorb.idl.lexer;
import org.jacorb.idl.parser;

public class IDLParser extends parser {

	public static void parse(String filePath, IDLTreeVisitor visitor) {
		try {
			init();
			setGenerator(visitor);
			GlobalInputStream.init();
			GlobalInputStream.setInput(filePath);

			/* reset tables everywhere */
			lexer.reset();
			NameTable.init();
			ConstDecl.init();
			TypeMap.init();

			new parser().parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
