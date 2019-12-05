package pyscalcompiler;

public class Parser {

	private Lexer lexer;
	private Token token;
	public String message;
	private Token auxToken;

	public Parser(Lexer lexer) {
		this.lexer = lexer;
		this.token = lexer.nextToken();
	}

	public Lexer getLexer() {
		return this.lexer;
	}

	public void sinalizaErroSintatico(String message) {
		System.out.println(
				"[Erro Sintatico] na linha " + this.token.getLinha() + " e coluna " + this.token.getColuna() + ": ");
		System.out.println(message);
	}
	
	public void advance() {
		System.out.println("[DEBUG] token:" + this.token.toString());
		this.token = this.lexer.nextToken();
	}

	public void skip(String message) {
		sinalizaErroSintatico(message);
		advance();
	}

	public boolean eat(String t) {
		if (this.token.getCodigo().equals(t)) {
			advance();
			return true;
		}
		return false;
	}

	/*
	 ******************************
	 * Inicio das regras da gramatica
	 ******************************
	 * 
	 */
	public void Programa() {
		Classe();
		if (!this.token.getCodigo().equals(Tag.EOF))
			sinalizaErroSintatico("Esperado \"EOF\"; encontrado \"" + this.token.getLexema() + "\"");
	}

	public void Classe() {
		if (eat(Tag.KW_CLASS)) {			
			ID();
			if (!eat(Tag.KW_DOISPONTOS))
				sinalizaErroSintatico("Esperado \":\"; encontrado \"" + this.token.getLexema() + "\"");
			ListaFuncao();
			Main();
			if (!eat(Tag.KW_END))
				sinalizaErroSintatico("Esperado \"end\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_PONTO))
				sinalizaErroSintatico("Esperado \".\"; encontrado \"" + this.token.getLexema() + "\"");
		} else {
			// synch Classe
			if (token.getCodigo().equals(Tag.EOF)) {
				sinalizaErroSintatico("Esperado \"class\"; encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"class\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Classe();
			}
		}
	}

	public No ID() {
		auxToken = new Token(token.getCodigo(), token.getLexema(), token.getLinha(), token.getColuna());
		if (!eat(Tag.ID))
			sinalizaErroSintatico("Esperado \"ID\"; encontrado \"" + this.token.getLexema() + "\"");
		else
			return new No();
						 
		return new No();
	}

	public void ListaFuncao() {
		if (token.getCodigo().equals(Tag.KW_DEF) || token.getCodigo().equals(Tag.KW_DEFSTATIC)) {
			ListaFuncaoLinha();
		} else {
			// synch ListaFuncao()
			if (token.getCodigo().equals(Tag.KW_DEFSTATIC)) {
				sinalizaErroSintatico("Esperado \"def, defstatic\"; encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"def, defstatic\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					ListaFuncao();
			}
		}
	}

	public void Main() {
		if (eat(Tag.KW_DEFSTATIC)) {
			if (!eat(Tag.KW_VOID))
				sinalizaErroSintatico("Esperado \"void\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_MAIN))
				sinalizaErroSintatico("Esperado \"main\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_ABREPAR))
				sinalizaErroSintatico("Esperado \"(\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_STRING))
				sinalizaErroSintatico("Esperado \"String\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_ABRECOL))
				sinalizaErroSintatico("Esperado \"[\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_FECHACOL))
				sinalizaErroSintatico("Esperado \"]\"; encontrado \"" + this.token.getLexema() + "\"");
			ID();
			if (!eat(Tag.KW_FECHAPAR))
				sinalizaErroSintatico("Esperado \")\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_DOISPONTOS))
				sinalizaErroSintatico("Esperado \":\"; encontrado \"" + this.token.getLexema() + "\"");
			RegexDeclaraID();
			ListaCmd();
			if (!eat(Tag.KW_END))
				sinalizaErroSintatico("Esperado \"end\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \";\"; encontrado \"" + this.token.getLexema() + "\"");
		} else {
			// synch Main
			if (token.getCodigo().equals(Tag.KW_END)) {
				sinalizaErroSintatico("Esperado \"defstatic\"; encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"defstatic\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Main();
			}
		}
	}

	public void ListaCmd() {
		if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_END)
				|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_IF)
				|| token.getCodigo().equals(Tag.KW_ELSE) || token.getCodigo().equals(Tag.KW_WHILE)
				|| token.getCodigo().equals(Tag.KW_WRITE)) {
			ListaCmdLinha();
		} else {
			// synch ListaCmd
			if (token.getCodigo().equals(Tag.ELSE) || token.getCodigo().equals(Tag.KW_END)
					|| token.getCodigo().equals(Tag.KW_RETURN)) {
				sinalizaErroSintatico("Esperado \"ID, end, return, if, else, while, write\"; encontrado \""
						+ this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"ID, end, return, if, else, while, write\"; encontrado \"" + this.token.getLexema()
						+ "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					ListaCmd();
			}
		}
	}

	public void ListaCmdLinha() {
		if (token.getCodigo().equals(Tag.ELSE) || token.getCodigo().equals(Tag.KW_IF)
				|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)) {
			Cmd();
			ListaCmdLinha();
		} else if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_END)
				|| token.getCodigo().equals(Tag.KW_RETURN)) {			
			return;
		} else {
			skip("Esperado \"ID, if, while, write\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				ListaCmdLinha();
		}
	}

	public void Cmd() {
		if (token.getCodigo().equals(Tag.KW_IF)) {
			CmdIf();
		} else if (token.getCodigo().equals(Tag.KW_WHILE)) {
			CmdWhile();
		} else if (token.getCodigo().equals(Tag.ID)) {
			ID();
			CmdAtribFunc();
		} else if (token.getCodigo().equals(Tag.KW_WRITE)) {
			CmdWrite();
		} else {
			// synch Cmd
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_IF)
					|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)
					|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_END)
					|| token.getCodigo().equals(Tag.KW_ELSE)) {
				sinalizaErroSintatico(
						"Esperado \"if, while, ID, write\"; encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"ID, if, while, write\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Cmd();
			}
		}
	}

	public void CmdIf() {
		if (eat(Tag.KW_IF)) {
			if (!eat(Tag.KW_ABREPAR))
				sinalizaErroSintatico("Esperado \"(\"; encontrado \"" + this.token.getLexema() + "\"");
			Expressao();
			if (!eat(Tag.KW_FECHAPAR))
				sinalizaErroSintatico("Esperado \")\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_DOISPONTOS))
				sinalizaErroSintatico("Esperado \":\"; encontrado \"" + this.token.getLexema() + "\"");
			ListaCmd();
			CmdIfLinha();
		} else {
			// synch CmdIf
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_IF)
					|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)
					|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_END)
					|| token.getCodigo().equals(Tag.KW_ELSE)) {
				sinalizaErroSintatico("Esperado \"if\"; encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"if\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					CmdIfLinha();
			}
		}
	}

	public void CmdIfLinha() {
		if (eat(Tag.KW_END)) {
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \"if\"; encontrado \"" + this.token.getLexema() + "\"");
		} else if (eat(Tag.KW_ELSE)) {
			if (!eat(Tag.KW_DOISPONTOS))
				sinalizaErroSintatico("Esperado \":\"; encontrado \"" + this.token.getLexema() + "\"");
			ListaCmd();
			if (!eat(Tag.KW_END))
				sinalizaErroSintatico("Esperado \"end\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \"if\"; encontrado \"" + this.token.getLexema() + "\"");
		} else {
			// synch CmdIfLinha
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_IF)
					|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)
					|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_END)
					|| token.getCodigo().equals(Tag.KW_ELSE)) {
				sinalizaErroSintatico("Esperado \"end, else\"; encontrado \"" + this.token.getLexema() + "\"");
				return;

			} else {
				skip("Esperado \"end, else\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					CmdIfLinha();
			}
		}
	}

	public void CmdWhile() {
		if (eat(Tag.KW_WHILE)) {
			if (!eat(Tag.KW_ABREPAR))
				sinalizaErroSintatico("Esperado \"(\"; encontrado \"" + this.token.getLexema() + "\"");
			Expressao();
			if (!eat(Tag.KW_FECHAPAR))
				sinalizaErroSintatico("Esperado \")\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_DOISPONTOS))
				sinalizaErroSintatico("Esperado \":\"; encontrado \"" + this.token.getLexema() + "\"");
			ListaCmd();
			if (!eat(Tag.KW_END))
				sinalizaErroSintatico("Esperado \"end\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \";\"; encontrado \"" + this.token.getLexema() + "\"");
		} else {
			// synch CmdWhile
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_IF)
					|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)
					|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_END)
					|| token.getCodigo().equals(Tag.KW_ELSE)) {
				sinalizaErroSintatico("Esperado \"while\"; encontrado \"" + this.token.getLexema() + "\"");
				return;

			} else {
				skip("Esperado \"while\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					CmdWhile();
			}
		}
	}

	public void CmdWrite() {
		if (eat(Tag.KW_WRITE)) {
			if (!eat(Tag.KW_ABREPAR))
				sinalizaErroSintatico("Esperado \"(\"; encontrado \"" + this.token.getLexema() + "\"");
			Expressao();
			if (!eat(Tag.KW_FECHAPAR))
				sinalizaErroSintatico("Esperado \")\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \";\"; encontrado \"" + this.token.getLexema() + "\"");
		} else {
			// synch CmdWrite
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_IF)
					|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)
					|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_END)
					|| token.getCodigo().equals(Tag.KW_ELSE)) {
				sinalizaErroSintatico("Esperado \"write\"; encontrado \"" + this.token.getLexema() + "\"");
				return;

			} else {
				skip("Esperado \"write\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					CmdWrite();
			}
		}
	}

	public void CmdAtribFunc() {
		if (token.getCodigo().equals(Tag.KW_ATTRIB)) {
			CmdAtribui();
		} else if (token.getCodigo().equals(Tag.KW_ABREPAR)) {
			CmdFuncao();
		} else {
			// synch CmdAtribFunc
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_IF)
					|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)
					|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_END)
					|| token.getCodigo().equals(Tag.KW_ELSE)) {
				sinalizaErroSintatico("Esperado \"=, (\"; encontrado \"" + this.token.getLexema() + "\"");
				return;

			} else {
				skip("Esperado \"=, (\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					CmdAtribFunc();
			}
		}
	}

	public void CmdAtribui() {
		if (eat(Tag.KW_ATTRIB)) {
			Expressao();
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \";\"; encontrado \"" + this.token.getLexema() + "\"");
		} else {
			// synch CmdAtribui
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_IF)
					|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)
					|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_END)
					|| token.getCodigo().equals(Tag.KW_ELSE)) {
				sinalizaErroSintatico("Esperado \"=\"; encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"=\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					CmdAtribui();
			}
		}
	}

	public void CmdFuncao() {
		if (eat(Tag.KW_ABREPAR)) {
			RegexExp();
			if (!eat(Tag.KW_FECHAPAR))
				sinalizaErroSintatico("Esperado \")\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \";\"; encontrado \"" + this.token.getLexema() + "\"");
		} else {
			// synch CmdFuncao
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_IF)
					|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)
					|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_END)
					|| token.getCodigo().equals(Tag.KW_ELSE)) {
				sinalizaErroSintatico("Esperado \"(\"; encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"(\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					CmdFuncao();
			}
		}
	}

	public void ListaFuncaoLinha() {
		if (token.getCodigo().equals(Tag.KW_DEF)) {
			Funcao();
			ListaFuncaoLinha();
		} else if (token.getCodigo().equals(Tag.KW_DEFSTATIC)) {
			return;
		} else {
			skip("Esperado \"def\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				ListaFuncaoLinha();
		}
	}

	public void Funcao() {
		if (eat(Tag.KW_DEF)) {
			TipoPrimitivo();
			ID();
			if (!eat(Tag.KW_ABREPAR))
				sinalizaErroSintatico("Esperado \"(\"; encontrado \"" + this.token.getLexema() + "\"");
			ListaArg();
			if (!eat(Tag.KW_FECHAPAR))
				sinalizaErroSintatico("Esperado \")\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_DOISPONTOS))
				sinalizaErroSintatico("Esperado \":\"; encontrado \"" + this.token.getLexema() + "\"");
			RegexDeclaraID();
			ListaCmd();
			Retorno();
			if (!eat(Tag.KW_END))
				sinalizaErroSintatico("Esperado \"end\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \";\"; encontrado \"" + this.token.getLexema() + "\"");
		} else {
			// synch Funcao
			if (token.getCodigo().equals(Tag.KW_DEFSTATIC)) {
				sinalizaErroSintatico("Esperado \"def\"; encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"def\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Funcao();
			}
		}
	}

	public void RegexDeclaraID() {
		if (token.getCodigo().equals(Tag.KW_VOID) || token.getCodigo().equals(Tag.KW_STRING)
				|| token.getCodigo().equals(Tag.KW_BOOLEAN) || token.getCodigo().equals(Tag.KW_INTEGER)
				|| token.getCodigo().equals(Tag.KW_DOUBLE)) {
			DeclaraID();
			RegexDeclaraID();
		} else if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_END)
				|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_IF)
				|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)) {
			return;
		} else {
			skip("Esperado \"def\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				RegexDeclaraID();
		}
	}

	public void Retorno() {
		if (eat(Tag.KW_RETURN)) {
			Expressao();
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \";\"; encontrado \"" + this.token.getLexema() + "\"");
		} else if (token.getCodigo().equals(Tag.KW_END)) {
			return;
		} else {
			skip("Esperado \"return\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				Retorno();
		}
	}

	public void Expressao() {
		if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.NUM) 
			|| token.getCodigo().equals(Tag.KW_STRING)
			|| token.getCodigo().equals(Tag.KW_TRUE)|| token.getCodigo().equals(Tag.KW_FALSE)
			|| token.getCodigo().equals(Tag.OP_NEGATIVO) || token.getCodigo().equals(Tag.OP_NEGACAO)
			|| token.getCodigo().equals(Tag.KW_ABREPAR)) {
			Exp1();
			ExpLinha();
		} else {
			// synch Expressao
			if (token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_FECHAPAR) 
				|| token.getCodigo().equals(Tag.KW_VIRGULA)) {
				sinalizaErroSintatico("Esperado \";, (, or, and, ,\"; encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \";, (, or, and, ,\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Expressao();
			}
		}
	}

	public void ExpLinha() {
		if (eat(Tag.OP_OR) || eat(Tag.OP_AND)) {
			Exp1();
			ExpLinha();
		} else if (token.getCodigo().equals(Tag.KW_FECHAPAR) 
				   || token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
			return;
		} else {
			skip("Esperado \"or, and\"; " + "encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				ExpLinha();
		}
	}

	public void Exp1() {
		if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.NUM) 
				|| token.getCodigo().equals(Tag.KW_STRING)
				|| token.getCodigo().equals(Tag.KW_TRUE)|| token.getCodigo().equals(Tag.KW_FALSE)
				|| token.getCodigo().equals(Tag.OP_NEGATIVO) || token.getCodigo().equals(Tag.OP_NEGACAO)
				|| token.getCodigo().equals(Tag.KW_ABREPAR)) {
			Exp2();
			Exp1Linha();
		} else {
			// synch Exp1
			if (token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
					|| token.getCodigo().equals(Tag.KW_FECHAPAR)
					|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
				sinalizaErroSintatico("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; "
						+ "encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; " + "encontrado \""
						+ this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Exp1();
			}
		}
	}

	public void Exp1Linha() {
		if (eat(Tag.OP_MENOR) || eat(Tag.OP_MENOR_IGUAL) || eat(Tag.OP_MAIOR) || eat(Tag.OP_MAIOR_IGUAL)
				|| eat(Tag.OP_IGUAL) || eat(Tag.OP_DIFERENTE)) {
			Exp2();
			Exp1Linha();
		} else if (token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
				|| token.getCodigo().equals(Tag.KW_FECHAPAR)
				|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
			return;
		} else {
			skip("Esperado \"<, <=, >, >=, ==, !=\"; " + "encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				Exp1Linha();
		}
	}

	public void Exp2() {
		if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.NUM) 
				|| token.getCodigo().equals(Tag.KW_STRING)
				|| token.getCodigo().equals(Tag.KW_TRUE)|| token.getCodigo().equals(Tag.KW_FALSE)
				|| token.getCodigo().equals(Tag.OP_NEGATIVO) || token.getCodigo().equals(Tag.OP_NEGACAO)
				|| token.getCodigo().equals(Tag.KW_ABREPAR)) {
			Exp3();
			Exp2Linha();
		} else {
			// synch Exp2
			if (token.getCodigo().equals(Tag.OP_MENOR) || token.getCodigo().equals(Tag.OP_MENOR_IGUAL)
					|| token.getCodigo().equals(Tag.OP_MAIOR) || token.getCodigo().equals(Tag.OP_MAIOR_IGUAL)
					|| token.getCodigo().equals(Tag.OP_IGUAL) || token.getCodigo().equals(Tag.OP_DIFERENTE)
					|| token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
					|| token.getCodigo().equals(Tag.KW_FECHAPAR) 
					|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
				sinalizaErroSintatico("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; "
						+ "encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; " + "encontrado \""
						+ this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Exp2();
			}
		}
	}

	public void Exp2Linha() {
		if (eat(Tag.OP_SOMA) || eat(Tag.OP_SUBTRACAO)) {
			Exp3();
			Exp2Linha();
		} else if (token.getCodigo().equals(Tag.OP_MENOR) || token.getCodigo().equals(Tag.OP_MENOR_IGUAL)
				|| token.getCodigo().equals(Tag.OP_MAIOR) || token.getCodigo().equals(Tag.OP_MAIOR_IGUAL)
				|| token.getCodigo().equals(Tag.OP_IGUAL) || token.getCodigo().equals(Tag.OP_DIFERENTE)
				|| token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
				|| token.getCodigo().equals(Tag.KW_FECHAPAR) 
				|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
			return;
		} else {
			skip("Esperado \"+, -\"; " + "encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				Exp2Linha();
		}
	}

	public void Exp3() {
		if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.NUM) 
				|| token.getCodigo().equals(Tag.KW_STRING)
				|| token.getCodigo().equals(Tag.KW_TRUE)|| token.getCodigo().equals(Tag.KW_FALSE)
				|| token.getCodigo().equals(Tag.OP_NEGATIVO) || token.getCodigo().equals(Tag.OP_NEGACAO)
				|| token.getCodigo().equals(Tag.KW_ABREPAR)) {
			Exp4();
			Exp3Linha();
		} else {
			// synch Exp3
			if (token.getCodigo().equals(Tag.OP_SOMA) || token.getCodigo().equals(Tag.OP_SUBTRACAO)
					|| token.getCodigo().equals(Tag.OP_MENOR) || token.getCodigo().equals(Tag.OP_MENOR_IGUAL)
					|| token.getCodigo().equals(Tag.OP_MAIOR) || token.getCodigo().equals(Tag.OP_MAIOR_IGUAL)
					|| token.getCodigo().equals(Tag.OP_IGUAL) || token.getCodigo().equals(Tag.OP_DIFERENTE)
					|| token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
					|| token.getCodigo().equals(Tag.KW_FECHAPAR) 
					|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
				sinalizaErroSintatico("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; "
						+ "encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; " + "encontrado \""
						+ this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Exp3();
			}
		}
	}

	public void Exp3Linha() {
		if (eat(Tag.OP_MULTIPLICAO) || eat(Tag.OP_DIVISAO)) {
			Exp4();
			Exp3Linha();
		} else if (token.getCodigo().equals(Tag.OP_SOMA) || token.getCodigo().equals(Tag.OP_SUBTRACAO)
				|| token.getCodigo().equals(Tag.OP_MENOR) || token.getCodigo().equals(Tag.OP_MENOR_IGUAL)
				|| token.getCodigo().equals(Tag.OP_MAIOR) || token.getCodigo().equals(Tag.OP_MAIOR_IGUAL)
				|| token.getCodigo().equals(Tag.OP_IGUAL) || token.getCodigo().equals(Tag.OP_DIFERENTE)
				|| token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
				|| token.getCodigo().equals(Tag.KW_FECHAPAR) 
				|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
			return;
		} else {
			skip("Esperado \"*, /\"; " + "encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				Exp3Linha();
		}
	}

	public void Exp4() {
		if (token.getCodigo().equals(Tag.ID)) {
			ID();
			Exp4Linha();
		} else if (eat(Tag.OP_NEGATIVO) || eat(Tag.OP_NEGACAO)) {
			Exp4();
		} else if (eat(Tag.KW_ABREPAR)) {
			Expressao();
			if (!eat(Tag.KW_FECHAPAR))
				sinalizaErroSintatico("Esperado \")\"; " + "encontrado \"" + this.token.getLexema() + "\"");
		} else if (eat(Tag.KW_TRUE) || eat(Tag.KW_FALSE) || eat(Tag.NUM) || eat(Tag.KW_STRING)) {
			return;
		} else {
			// synch Exp4
			if (token.getCodigo().equals(Tag.OP_MULTIPLICAO) || token.getCodigo().equals(Tag.OP_DIVISAO)
					|| token.getCodigo().equals(Tag.OP_SOMA) || token.getCodigo().equals(Tag.OP_SUBTRACAO)
					|| token.getCodigo().equals(Tag.OP_MENOR) || token.getCodigo().equals(Tag.OP_MENOR_IGUAL)
					|| token.getCodigo().equals(Tag.OP_MAIOR) || token.getCodigo().equals(Tag.OP_MAIOR_IGUAL)
					|| token.getCodigo().equals(Tag.OP_IGUAL) || token.getCodigo().equals(Tag.OP_DIFERENTE)
					|| token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
					|| token.getCodigo().equals(Tag.KW_FECHAPAR)
					|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
				sinalizaErroSintatico("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; "
						+ "encontrado \"" + this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; " + "encontrado \""
						+ this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Exp4();
			}
		}
	}

	public void Exp4Linha() {
		if (eat(Tag.KW_ABREPAR)) {
			RegexExp();
			if (!eat(Tag.KW_FECHAPAR))
                sinalizaErroSintatico("Esperado \")\"; " + "encontrado \"" + this.token.getLexema() + "\"");
		} else if (token.getCodigo().equals(Tag.OP_MULTIPLICAO) || token.getCodigo().equals(Tag.OP_DIVISAO)
				|| token.getCodigo().equals(Tag.OP_SOMA) || token.getCodigo().equals(Tag.OP_SUBTRACAO)
				|| token.getCodigo().equals(Tag.OP_MENOR) || token.getCodigo().equals(Tag.OP_MENOR_IGUAL)
				|| token.getCodigo().equals(Tag.OP_MAIOR) || token.getCodigo().equals(Tag.OP_MAIOR_IGUAL)
				|| token.getCodigo().equals(Tag.OP_IGUAL) || token.getCodigo().equals(Tag.OP_DIFERENTE)
				|| token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
				|| token.getCodigo().equals(Tag.KW_FECHAPAR) 
				|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
			return;
		} else {
			skip("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; " + "encontrado \""
					+ this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				Exp4Linha();
		}
	}

	public void RegexExp() {
		if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.NUM)
				|| token.getCodigo().equals(Tag.KW_TRUE) || token.getCodigo().equals(Tag.KW_FALSE)
				|| token.getCodigo().equals(Tag.OP_NEGATIVO) || token.getCodigo().equals(Tag.OP_NEGACAO)
				|| token.getCodigo().equals(Tag.KW_ABREPAR)) {
			Expressao();
			RegexExpLinha();
		} else if (token.getCodigo().equals(Tag.KW_FECHAPAR)) {
			return;
		} else {
			skip("Esperado \"(\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				RegexExp();
		}
	}

	public void RegexExpLinha() {
		if (eat(Tag.KW_VIRGULA)) {
			Expressao();
			RegexExpLinha();
		} else if (token.getCodigo().equals(Tag.KW_FECHAPAR)) {
			return;
		} else {
			skip("Esperado \",\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				RegexExpLinha();
		}
	}

	public void DeclaraID() {
		if (token.getCodigo().equals(Tag.KW_VOID) || token.getCodigo().equals(Tag.KW_STRING)
				|| token.getCodigo().equals(Tag.KW_BOOLEAN) || token.getCodigo().equals(Tag.KW_INTEGER)
				|| token.getCodigo().equals(Tag.KW_DOUBLE)) {
			TipoPrimitivo();
			ID();
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \"bool, integer, String, double, void\"; encontrado \""
						+ this.token.getLexema() + "\"");
		} else {
			// synch DeclaraID
			if (token.getCodigo().equals(Tag.KW_VOID) || token.getCodigo().equals(Tag.KW_STRING)
					|| token.getCodigo().equals(Tag.KW_BOOLEAN) || token.getCodigo().equals(Tag.KW_INTEGER)
					|| token.getCodigo().equals(Tag.KW_DOUBLE) || token.getCodigo().equals(Tag.ID)
					|| token.getCodigo().equals(Tag.KW_END) || token.getCodigo().equals(Tag.KW_RETURN)
					|| token.getCodigo().equals(Tag.KW_IF) || token.getCodigo().equals(Tag.KW_WHILE)
					|| token.getCodigo().equals(Tag.KW_WRITE)) {
				sinalizaErroSintatico("Esperado \"bool, integer, String, double, void\"; encontrado \""
						+ this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"bool, integer, String, double, void\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					DeclaraID();
			}
		}
	}

	public void ListaArg() {
		if (token.getCodigo().equals(Tag.KW_VOID) || token.getCodigo().equals(Tag.KW_STRING)
				|| token.getCodigo().equals(Tag.KW_BOOLEAN) || token.getCodigo().equals(Tag.KW_INTEGER)
				|| token.getCodigo().equals(Tag.KW_DOUBLE)) {
			Arg();
			ListaArgLinha();
		} else {
			// synch ListaArg
			if (token.getCodigo().equals(Tag.KW_FECHAPAR)) {
				sinalizaErroSintatico("Esperado \"void, String, bool, integer, double\"; encontrado \""
						+ this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"void, String, bool, integer, double\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					ListaArg();
			}
		}
	}

	public void Arg() {
		if (token.getCodigo().equals(Tag.KW_VOID) || token.getCodigo().equals(Tag.KW_STRING)
				|| token.getCodigo().equals(Tag.KW_BOOLEAN) || token.getCodigo().equals(Tag.KW_INTEGER)
				|| token.getCodigo().equals(Tag.KW_DOUBLE)) {
			TipoPrimitivo();
			ID();
		} else {
			// synch Arg
			if (token.getCodigo().equals(Tag.KW_VIRGULA) || token.getCodigo().equals(Tag.KW_FECHAPAR)) {
				sinalizaErroSintatico("Esperado \"void, String, bool, integer, double\"; encontrado \""
						+ this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"void, String, bool, integer, double\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Arg();
			}
		}
	}

	public void ListaArgLinha() {
		if (eat(Tag.KW_VIRGULA)) {
			ListaArg();
		} else if (token.getCodigo().equals(Tag.KW_FECHAPAR)) {
			return;
		} else {
			skip("Esperado \"void, String, bool, integer, double\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				ListaArgLinha();
		}
	}

	public void TipoPrimitivo() {
		if (!eat(Tag.KW_BOOLEAN) && !eat(Tag.KW_INTEGER) && !eat(Tag.KW_STRING) && !eat(Tag.KW_DOUBLE)
				&& !eat(Tag.KW_VOID)) {
			// Synch TipoPrimitivo
			if (token.getCodigo().equals(Tag.ID)) {
				sinalizaErroSintatico("Esperado \"bool, integer, String, double, void\"; encontrado \""
						+ this.token.getLexema() + "\"");
				return;
			} else {
				skip("Esperado \"bool, integer, String, double, void\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					TipoPrimitivo();
			}
		}
	}
}