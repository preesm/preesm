package net.sf.graphiti.grammar.idl;

import net.sf.graphiti.io.IAntlrProxy;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Parser;

public class IDLAntlrProxy implements IAntlrProxy {

	@Override
	public Parser createParser(CharStream stream) {
		Lexer lexer = new IDLLexer(stream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new IDLParser(tokens);
	}

}
