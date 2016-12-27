package fr.upmc.javassist;

import java.util.HashMap;

public abstract class AbstractClassFactory {
	
	protected static final String PUBLIC = "public";
	protected static final String SPACE = " ";
	protected static final String ARG = "arg";
	protected static final String END = "#";
	protected static final String LEFT_PARENTHESIS = "(";
	protected static final String RIGHT_PARENTHESIS = ")";
	protected static final String LEFT_BRACKET = "[";
	protected static final String RIGHT_BRACKET = "]";
	protected static final String LEFT_BRACE = "{";
	protected static final String RIGHT_BRACE = "}";
	protected static final String THROWS = "throws";
	protected static final String EMPTY = "";
	protected static final String COMMA = ",";
	protected static final String UNDERSCORE = "_";
	protected static final String CRLF = "\n";
	
	protected static HashMap<String, Class<?>> classes = new HashMap<>();
	
	protected static String canonicalPrefix = AbstractClassFactory.class.getCanonicalName().replace("AbstractClassFactory", "");
	
}
