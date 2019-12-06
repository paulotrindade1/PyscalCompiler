package pyscalcompiler;

import java.util.Hashtable;
import java.util.Map;

public class Ts {
    private Hashtable<String, Token> ts = new Hashtable<String, Token>();
    
    public Ts() {
    	carregaTabelaSimbolos();
    }

	public Token getToken(String lexema) {
        Token token = ts.get(lexema);
        return token;
    }
    
    public void addToken(String lexema, Token token) {
        ts.put(lexema, token);
    }

	public String getTipo(String lexema) {
		return ts.get(lexema).getTipo();
	}

    public void setTipo(String lexema, String tipo) {
		ts.get(lexema).setTipo(tipo);
	}
    
    public void printTS() {
        for(Map.Entry<String, Token> entry : ts.entrySet()) {
            Token token = entry.getValue();
            
            System.out.println("<" + token.getCodigo() + ", " + token.getLexema()+ ">"
                    + " Line " + token.getLinha() + " Column " + token.getColuna());
        }
    }
    
    private void carregaTabelaSimbolos() {
    	
    	this.ts.put("class", new Token(Tag.KW_CLASS, "class", 0, 0));
    	this.ts.put(":", new Token(Tag.KW_DOISPONTOS, ":", 0, 0));
    	this.ts.put("end", new Token(Tag.KW_END, "end", 0, 0));
    	this.ts.put(".", new Token(Tag.KW_PONTO, ".", 0, 0));
    	this.ts.put(";", new Token(Tag.KW_PONTOVIRGULA, ";", 0, 0));
    	this.ts.put("def", new Token(Tag.KW_DEF, "def", 0, 0));
    	this.ts.put("(", new Token(Tag.KW_ABREPAR, "(", 0, 0));
    	this.ts.put(")", new Token(Tag.KW_FECHAPAR, ")", 0, 0));
    	this.ts.put(",", new Token(Tag.KW_VIRGULA, ",", 0, 0));
    	this.ts.put("return", new Token(Tag.KW_RETURN, "return", 0, 0));
    	this.ts.put("defstatic", new Token(Tag.KW_DEFSTATIC, "defstatic", 0, 0));
    	this.ts.put("void", new Token(Tag.KW_VOID, "void", 0, 0));
    	this.ts.put("main", new Token(Tag.KW_MAIN, "main", 0, 0));
    	this.ts.put("String", new Token(Tag.KW_STRING, "String", 0, 0));
    	this.ts.put("[", new Token(Tag.KW_ABRECOL, "[", 0, 0));
    	this.ts.put("]", new Token(Tag.KW_FECHACOL, "]", 0, 0));
    	this.ts.put("bool", new Token(Tag.KW_BOOLEAN, "bool", 0, 0));
    	this.ts.put("integer", new Token(Tag.KW_INTEGER, "integer", 0, 0));
    	this.ts.put("double", new Token(Tag.KW_DOUBLE, "double", 0, 0));
    	this.ts.put("if", new Token(Tag.KW_IF, "if", 0, 0));
    	this.ts.put("else", new Token(Tag.KW_ELSE, "else", 0, 0));
    	this.ts.put("while", new Token(Tag.KW_WHILE, "while", 0, 0));
    	this.ts.put("write", new Token(Tag.KW_WRITE, "write", 0, 0));
    	this.ts.put("=", new Token(Tag.KW_ATTRIB, "=", 0, 0));
    	this.ts.put("true", new Token(Tag.KW_TRUE, "true", 0, 0));
    	this.ts.put("false", new Token(Tag.KW_FALSE, "false", 0, 0));
    	this.ts.put("or", new Token(Tag.OP_OR, "or", 0, 0));
    	this.ts.put("and", new Token(Tag.OP_AND, "and", 0, 0));
    	this.ts.put("<", new Token(Tag.OP_MENOR, "<", 0, 0));
    	this.ts.put("<=", new Token(Tag.OP_MENOR_IGUAL, "<=", 0, 0));
    	this.ts.put(">", new Token(Tag.OP_MAIOR, ">", 0, 0));
    	this.ts.put(">=", new Token(Tag.OP_MAIOR_IGUAL, ">=", 0, 0));
    	this.ts.put("==", new Token(Tag.OP_IGUAL, "==", 0, 0));
    	this.ts.put("!=", new Token(Tag.OP_DIFERENTE, "!=", 0, 0));
    	this.ts.put("/", new Token(Tag.OP_DIVISAO, "/", 0, 0));
    	this.ts.put("*", new Token(Tag.OP_MULTIPLICAO, "*", 0, 0));
    	this.ts.put("-", new Token(Tag.OP_SUBTRACAO, "-", 0, 0));
    	this.ts.put("+", new Token(Tag.OP_SOMA, "+", 0, 0));
    	this.ts.put("-", new Token(Tag.OP_NEGATIVO, "-", 0, 0));
    	this.ts.put("!", new Token(Tag.OP_NEGACAO, "!", 0, 0));
		
	}
}
