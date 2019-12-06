package pyscalcompiler;

import com.sun.jndi.ldap.sasl.LdapSasl;

public class Parser {

	private Lexer lexer;
	public Token token;
	private int errosSintaticosEncontrados;

	public Parser(Lexer lexer) {
		this.lexer = lexer;
		this.token = lexer.nextToken();
		this.errosSintaticosEncontrados = 0;
	}

	public Lexer getLexer() {
		return this.lexer;
	}

	public void sinalizaErroSintatico(String message) {
		System.out.println(
				"[Erro Sintatico] na linha " + this.token.getLinha() + " e coluna " + this.token.getColuna() + ": ");
		System.out.println(message);
		this.errosSintaticosEncontrados++;
		if (this.errosSintaticosEncontrados > 4)
			encerrarCompilacao();
	}

	public void sinalizaErroSemantico(String message) {
		System.out.println(
				"[Erro Semantico] na linha " + this.token.getLinha() + " e coluna " + this.token.getColuna() + ": ");
		System.out.println(message);
	}
	
	public void advance() {
		System.out.println("[DEBUG] token:" + this.token.toString());
		this.token = this.lexer.nextToken();
		if (this.token == null) {
			encerrarCompilacao();
		}
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

	public void encerrarCompilacao() {
		System.out.println("\n=>Tabela de simbolos:");
		this.lexer.printTabelaSimbolos();
		System.out.println("\n=>Fim da compilação.");
		System.exit(0);
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
			if (!eat(Tag.ID)) {
				sinalizaErroSintatico("Esperado \"ID\"; encontrado \"" + this.token.getLexema() + "\"");
			} else {
				Token tokenAux = new Token(this.token.codigo, this.token.lexema, this.token.linha, this.token.coluna);
				this.lexer.getTs().setTipo(tokenAux.lexema, Tag.TIPO_VAZIO);
			}

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
			if (!eat(Tag.ID)) {
				sinalizaErroSintatico("Esperado \"ID\"; encontrado \"" + this.token.getLexema() + "\"");
			} else {
				Token tokenAux = new Token(this.token.codigo, this.token.lexema, this.token.linha, this.token.coluna);
				this.lexer.getTs().setTipo(tokenAux.lexema, Tag.TIPO_STRING);
			}
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
		if (token.getCodigo().equals(Tag.KW_ELSE) || token.getCodigo().equals(Tag.KW_END)
				|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_IF)
				|| token.getCodigo().equals(Tag.KW_ELSE) || token.getCodigo().equals(Tag.KW_WHILE)
				|| token.getCodigo().equals(Tag.KW_WRITE)) {
			ListaCmdLinha();
		} else {
			// synch ListaCmd
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_END)
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
		if (token.getCodigo().equals(Tag.KW_ELSE) || token.getCodigo().equals(Tag.KW_IF)
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
			Token tokenAux = new Token(this.token.codigo, this.token.lexema, this.token.linha, this.token.coluna);
			if (!eat(Tag.ID)) {
				sinalizaErroSintatico("Esperado \"ID\"; encontrado \"" + this.token.getLexema() + "\"");
			} else {
				if (tokenAux.getTipo() == null)
					sinalizaErroSemantico("Variavel nao declarada!");
			}
			No noCmdAtribFunc = CmdAtribFunc();
			if(!noCmdAtribFunc.getTipo().equals(Tag.TIPO_VAZIO)
					&& !this.lexer.getTs().getTipo(tokenAux.lexema).equals(noCmdAtribFunc.getTipo()))
				sinalizaErroSemantico("Atribuicao incompativel");
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
			No noExpressao = Expressao();
			if (!eat(Tag.KW_FECHAPAR)) {
				sinalizaErroSintatico("Esperado \")\"; encontrado \"" + this.token.getLexema() + "\"");
			} else {
				if(!noExpressao.getTipo().equals(Tag.TIPO_LOGICO))
					sinalizaErroSemantico("Expressao mal formada.");
			}
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
			No noExpressao = Expressao();
			if (!eat(Tag.KW_FECHAPAR)) {
				sinalizaErroSintatico("Esperado \")\"; encontrado \"" + this.token.getLexema() + "\"");
			} else {
				if(!noExpressao.getTipo().equals(Tag.TIPO_LOGICO))
					sinalizaErroSemantico("Expressao mal formada.");
			}
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
			No noExpressao = Expressao();
			if (!eat(Tag.KW_FECHAPAR)) {
				sinalizaErroSintatico("Esperado \")\"; encontrado \"" + this.token.getLexema() + "\"");
			}
			if (!eat(Tag.KW_PONTOVIRGULA)) {
				sinalizaErroSintatico("Esperado \";\"; encontrado \"" + this.token.getLexema() + "\"");
			} else {
				if(!noExpressao.getTipo().equals(Tag.TIPO_STRING))
					sinalizaErroSemantico("Expressao mal formada.");
			}
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

	public No CmdAtribFunc() {
		No noCmdAtribFunc = new No();
		if (token.getCodigo().equals(Tag.KW_ATTRIB)) {
			No noCmdAtriBui = CmdAtribui();
			noCmdAtribFunc.setTipo(noCmdAtriBui.getTipo());
		} else if (token.getCodigo().equals(Tag.KW_ABREPAR)) {
			CmdFuncao();
			noCmdAtribFunc.setTipo(Tag.TIPO_VAZIO);
		} else {
			// synch CmdAtribFunc
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_IF)
					|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)
					|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_END)
					|| token.getCodigo().equals(Tag.KW_ELSE)) {
				sinalizaErroSintatico("Esperado \"=, (\"; encontrado \"" + this.token.getLexema() + "\"");
				return noCmdAtribFunc;
			} else {
				skip("Esperado \"=, (\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					CmdAtribFunc();
			}
		}
		return noCmdAtribFunc;
	}

	public No CmdAtribui() {
		No CmdAtribui = new No();
		if (eat(Tag.KW_ATTRIB)) {
			No noExpressao = Expressao();
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \";\"; encontrado \"" + this.token.getLexema() + "\"");
			else
				CmdAtribui.setTipo(noExpressao.getTipo());
		} else {
			// synch CmdAtribui
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.KW_IF)
					|| token.getCodigo().equals(Tag.KW_WHILE) || token.getCodigo().equals(Tag.KW_WRITE)
					|| token.getCodigo().equals(Tag.KW_RETURN) || token.getCodigo().equals(Tag.KW_END)
					|| token.getCodigo().equals(Tag.KW_ELSE)) {
				sinalizaErroSintatico("Esperado \"=\"; encontrado \"" + this.token.getLexema() + "\"");
				return CmdAtribui;
			} else {
				skip("Esperado \"=\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					CmdAtribui();
			}
		}
		return CmdAtribui;
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
			No noTipoPrimitivo = TipoPrimitivo();
			if (!eat(Tag.ID)) {
				sinalizaErroSintatico("Esperado \"ID\"; encontrado \"" + this.token.getLexema() + "\"");
			} else {
				Token tokenAux = new Token(this.token.codigo, this.token.lexema, this.token.linha, this.token.coluna);
				this.lexer.getTs().setTipo(tokenAux.lexema, Tag.TIPO_VAZIO);
			}
			if (!eat(Tag.KW_ABREPAR))
				sinalizaErroSintatico("Esperado \"(\"; encontrado \"" + this.token.getLexema() + "\"");
			ListaArg();
			if (!eat(Tag.KW_FECHAPAR))
				sinalizaErroSintatico("Esperado \")\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!eat(Tag.KW_DOISPONTOS))
				sinalizaErroSintatico("Esperado \":\"; encontrado \"" + this.token.getLexema() + "\"");
			RegexDeclaraID();
			ListaCmd();
			No noRetorno = Retorno();
			if (!noRetorno.getTipo().equals(noTipoPrimitivo.getTipo())) {
				sinalizaErroSemantico("Tipo de retorno incompativel");
			}
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

	public No Retorno() {
		No noRetorno = new No();
		if (eat(Tag.KW_RETURN)) {
			No noExpressao = Expressao();
			if (!eat(Tag.KW_PONTOVIRGULA))
				sinalizaErroSintatico("Esperado \";\"; encontrado \"" + this.token.getLexema() + "\"");
			else
				noRetorno.setTipo(noExpressao.getTipo());
		} else if (token.getCodigo().equals(Tag.KW_END)) {
			return noRetorno;
		} else {
			skip("Esperado \"return\"; encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				Retorno();
		}
		return noRetorno;
	}

	public No Expressao() {
		No noExpressao = new No();
		if (token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_ABREPAR)
				|| token.getCodigo().equals(Tag.KW_VIRGULA) || token.getCodigo().equals(Tag.OP_OR)
				|| token.getCodigo().equals(Tag.OP_AND)|| token.getCodigo().equals(Tag.ID)
				|| token.getCodigo().equals(Tag.KW_TRUE)|| token.getCodigo().equals(Tag.KW_FALSE)
				|| token.getCodigo().equals(Tag.STRING) || token.getCodigo().equals(Tag.INTEGER)
				|| token.getCodigo().equals(Tag.DOUBLE)) {
			No noExp1 = Exp1();
			No noExpLinha = ExpLinha();

			if(noExpLinha.getTipo().equals(Tag.TIPO_VAZIO))
				noExpressao.setTipo(noExp1.getTipo());
			else if (noExpLinha.getTipo().equals(noExp1.getTipo()) && noExpLinha.getTipo().equals(Tag.TIPO_LOGICO))
				noExpressao.setTipo(Tag.TIPO_LOGICO);
			else
				noExpressao.setTipo(Tag.TIPO_ERRO);
		} else {
			// synch Expressao
			if (token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_FECHAPAR)
				|| token.getCodigo().equals(Tag.KW_VIRGULA)) {
				sinalizaErroSintatico("Esperado \";, (, or, and, ,\"; encontrado \"" + this.token.getLexema() + "\"");
				return noExpressao;
			} else {
				skip("Esperado \";, (, or, and, ,\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Expressao();
			}
		}
		return noExpressao;
	}

	public No ExpLinha() {
		No noExpLinha = new No();
		if (eat(Tag.OP_OR) || eat(Tag.OP_AND)) {
			No noExp1 = Exp1();
			No noExpLinhaFilho = ExpLinha();

			if (noExpLinhaFilho.getTipo().equals(Tag.TIPO_VAZIO) && noExp1.getTipo().equals(Tag.TIPO_LOGICO))
				noExpLinha.setTipo(Tag.TIPO_LOGICO);
			else if (noExpLinhaFilho.getTipo().equals(noExp1.getTipo()) && noExp1.getTipo().equals(Tag.TIPO_LOGICO))
				noExpLinha.setTipo(Tag.TIPO_LOGICO);
			else
				noExpLinha.setTipo(Tag.TIPO_ERRO);

		} else if (token.getCodigo().equals(Tag.KW_FECHAPAR) 
				   || token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
			noExpLinha.setTipo(Tag.TIPO_VAZIO);
			return noExpLinha;
		} else {
			skip("Esperado \"or, and\"; " + "encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				ExpLinha();
		}
		return noExpLinha;
	}

	public No Exp1() {
		No noExp1 = new No();
		if (token.getCodigo().equals(Tag.ID)
				|| token.getCodigo().equals(Tag.INTEGER) || token.getCodigo().equals(Tag.DOUBLE)
				|| token.getCodigo().equals(Tag.KW_TRUE) || token.getCodigo().equals(Tag.KW_FALSE)
				|| token.getCodigo().equals(Tag.OP_NEGATIVO) || token.getCodigo().equals(Tag.OP_NEGACAO)
				|| token.getCodigo().equals(Tag.KW_ABREPAR) || token.getCodigo().equals(Tag.STRING)) {
			No noExp2 = Exp2();
			No noExp1Linha = Exp1Linha();

			if (noExp1Linha.getTipo().equals(Tag.TIPO_VAZIO))
				noExp1.setTipo(noExp2.getTipo());
			else if (noExp1Linha.getTipo().equals(noExp2.getTipo()) && noExp1Linha.getTipo().equals(Tag.TIPO_NUMERICO))
				noExp1.setTipo(Tag.TIPO_LOGICO);
			else
				noExp1.setTipo(Tag.TIPO_ERRO);

		} else {
			// synch Exp1
			if (token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
					|| token.getCodigo().equals(Tag.KW_FECHAPAR)
					|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
				sinalizaErroSintatico("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; "
						+ "encontrado \"" + this.token.getLexema() + "\"");
				return noExp1;
			} else {
				skip("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; " + "encontrado \""
						+ this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Exp1();
			}
		}
		return noExp1;
	}

	public No Exp1Linha() {
		No noExp1Linha = new No();
		if (eat(Tag.OP_MENOR) || eat(Tag.OP_MENOR_IGUAL) || eat(Tag.OP_MAIOR) || eat(Tag.OP_MAIOR_IGUAL)
				|| eat(Tag.OP_IGUAL) || eat(Tag.OP_DIFERENTE)) {
			No noExp2 = Exp2();
			No noExp1LinhaFilho = Exp1Linha();

			if (noExp1LinhaFilho.getTipo().equals(Tag.TIPO_VAZIO) && noExp2.getTipo().equals(Tag.TIPO_NUMERICO))
				noExp1Linha.setTipo(Tag.TIPO_NUMERICO);
			else if (noExp1LinhaFilho.getTipo().equals(noExp2.getTipo()) && noExp2.getTipo().equals(Tag.TIPO_NUMERICO))
				noExp1Linha.setTipo(Tag.TIPO_NUMERICO);
			else
				noExp1Linha.setTipo(Tag.TIPO_ERRO);

		} else if (token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
				|| token.getCodigo().equals(Tag.KW_FECHAPAR)
				|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
			noExp1Linha.setTipo(Tag.TIPO_VAZIO);
			return noExp1Linha;
		} else {
			skip("Esperado \"<, <=, >, >=, ==, !=\"; " + "encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				Exp1Linha();
		}
		return noExp1Linha;
	}

	public No Exp2() {
		No noExp2 = new No();
		if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.INTEGER)
				|| token.getCodigo().equals(Tag.KW_TRUE) || token.getCodigo().equals(Tag.KW_FALSE)
				|| token.getCodigo().equals(Tag.OP_NEGATIVO) || token.getCodigo().equals(Tag.OP_NEGACAO)
				|| token.getCodigo().equals(Tag.KW_ABREPAR) || token.getCodigo().equals(Tag.STRING)
				|| token.getCodigo().equals(Tag.DOUBLE)) {
			No noExp3 = Exp3();
			No noExp2Linha = Exp2Linha();

			if (noExp2Linha.getTipo().equals(Tag.TIPO_VAZIO))
				noExp2.setTipo(noExp3.getTipo());
			else if (noExp2Linha.getTipo().equals(noExp3.getTipo()) && noExp2Linha.getTipo().equals(Tag.TIPO_NUMERICO))
				noExp2.setTipo(Tag.TIPO_NUMERICO);
			else
				noExp2.setTipo(Tag.TIPO_ERRO);

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
				return noExp2;
			} else {
				skip("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; " + "encontrado \""
						+ this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Exp2();
			}
		}
		return noExp2;
	}

	public No Exp2Linha() {
		No noExp2Linha = new No();
		if (eat(Tag.OP_SOMA) || eat(Tag.OP_SUBTRACAO)) {
			No noExp3 = Exp3();
			No noExp2LinhaFilho = Exp2Linha();

			if (noExp2LinhaFilho.getTipo().equals(Tag.TIPO_VAZIO) && noExp3.getTipo().equals(Tag.TIPO_NUMERICO))
				noExp2Linha.setTipo(Tag.TIPO_NUMERICO);
			else if (noExp2LinhaFilho.getTipo().equals(noExp3.getTipo()) && noExp3.getTipo().equals(Tag.TIPO_NUMERICO))
				noExp2Linha.setTipo(Tag.TIPO_NUMERICO);
			else
				noExp2Linha.setTipo(Tag.TIPO_ERRO);

		} else if (token.getCodigo().equals(Tag.OP_MENOR) || token.getCodigo().equals(Tag.OP_MENOR_IGUAL)
				|| token.getCodigo().equals(Tag.OP_MAIOR) || token.getCodigo().equals(Tag.OP_MAIOR_IGUAL)
				|| token.getCodigo().equals(Tag.OP_IGUAL) || token.getCodigo().equals(Tag.OP_DIFERENTE)
				|| token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
				|| token.getCodigo().equals(Tag.KW_FECHAPAR) 
				|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
			noExp2Linha.setTipo(Tag.TIPO_VAZIO);
			return noExp2Linha;
		} else {
			skip("Esperado \"+, -\"; " + "encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				Exp2Linha();
		}
		return noExp2Linha;
	}

	public No Exp3() {
		No noExp3 = new No();
		if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.INTEGER)
				|| token.getCodigo().equals(Tag.KW_TRUE) || token.getCodigo().equals(Tag.KW_FALSE)
				|| token.getCodigo().equals(Tag.OP_NEGATIVO) || token.getCodigo().equals(Tag.OP_NEGACAO)
				|| token.getCodigo().equals(Tag.KW_ABREPAR) || token.getCodigo().equals(Tag.STRING)
				|| token.getCodigo().equals(Tag.DOUBLE)) {
			No noExp4 = Exp4();
			No noExp3Linha = Exp3Linha();

			if (noExp3Linha.getTipo().equals(Tag.TIPO_VAZIO))
				noExp3.setTipo(noExp4.getTipo());
			else if (noExp3Linha.getTipo().equals(noExp4.getTipo()) && noExp3Linha.getTipo().equals(Tag.TIPO_NUMERICO))
				noExp3.setTipo(Tag.TIPO_NUMERICO);
			else
				noExp3.setTipo(Tag.TIPO_ERRO);

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
				return noExp3;
			} else {
				skip("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; " + "encontrado \""
						+ this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Exp3();
			}
		}
		return noExp3;
	}

	public No Exp3Linha() {
		No noExp3Linha = new No();
		if (eat(Tag.OP_MULTIPLICAO) || eat(Tag.OP_DIVISAO)) {
			No noExp4 = Exp4();
			No noExp3LinhaFilho = Exp3Linha();

			if (noExp3LinhaFilho.getTipo().equals(Tag.TIPO_VAZIO) && noExp4.getTipo().equals(Tag.TIPO_NUMERICO))
				noExp3Linha.setTipo(Tag.TIPO_NUMERICO);
			else if(noExp3LinhaFilho.getTipo().equals(noExp4.getTipo()) && noExp4.getTipo().equals(Tag.TIPO_NUMERICO))
				noExp3Linha.setTipo(Tag.TIPO_NUMERICO);
			else
				noExp3Linha.setTipo(Tag.TIPO_ERRO);
		} else if (token.getCodigo().equals(Tag.OP_SOMA) || token.getCodigo().equals(Tag.OP_SUBTRACAO)
				|| token.getCodigo().equals(Tag.OP_MENOR) || token.getCodigo().equals(Tag.OP_MENOR_IGUAL)
				|| token.getCodigo().equals(Tag.OP_MAIOR) || token.getCodigo().equals(Tag.OP_MAIOR_IGUAL)
				|| token.getCodigo().equals(Tag.OP_IGUAL) || token.getCodigo().equals(Tag.OP_DIFERENTE)
				|| token.getCodigo().equals(Tag.OP_OR) || token.getCodigo().equals(Tag.OP_AND)
				|| token.getCodigo().equals(Tag.KW_FECHAPAR) 
				|| token.getCodigo().equals(Tag.KW_PONTOVIRGULA) || token.getCodigo().equals(Tag.KW_VIRGULA)) {
			noExp3Linha.setTipo(Tag.TIPO_VAZIO);
			return noExp3Linha;
		} else {
			skip("Esperado \"*, /\"; " + "encontrado \"" + this.token.getLexema() + "\"");
			if (!token.getCodigo().equals(Tag.EOF))
				Exp3Linha();
		}
		return noExp3Linha;
	}

	public No Exp4() {
		No noExp4 = new No();
		if (token.getCodigo().equals(Tag.ID)) {
			if (!eat(Tag.ID)) {
				sinalizaErroSintatico("Esperado \"ID\"; encontrado \"" + this.token.getLexema() + "\"");
			}
			Exp4Linha();
			Token tokenAux = new Token(this.token.codigo, this.token.lexema, this.token.linha, this.token.coluna);
			noExp4.setTipo(this.lexer.getTs().getTipo(tokenAux.lexema));

			if (noExp4.getTipo() == null) {
				noExp4.setTipo(Tag.TIPO_ERRO);
				sinalizaErroSemantico("Variavel nao declarada");
			}
		} else if (token.getCodigo().equals(Tag.OP_NEGATIVO) || token.getCodigo().equals(Tag.OP_NEGACAO)) {
			No noOpUnario = OpUnario();
			No noExp4Filho = Exp4();
			if (noExp4Filho.getTipo().equals(noOpUnario.getTipo())
			    && noOpUnario.getTipo().equals(Tag.TIPO_NUMERICO)) {
				noExp4.setTipo(Tag.TIPO_NUMERICO);
			} else if (noExp4Filho.getTipo().equals(noOpUnario.getTipo())
					&& noOpUnario.getTipo().equals(Tag.TIPO_LOGICO)) {
				noExp4.setTipo(Tag.TIPO_LOGICO);
			} else {
				noExp4.setTipo(Tag.TIPO_ERRO);
			}
		} else if (eat(Tag.KW_ABREPAR)) {
			No noExpressao = Expressao();
			if (!eat(Tag.KW_FECHAPAR))
				sinalizaErroSintatico("Esperado \")\"; " + "encontrado \"" + this.token.getLexema() + "\"");
			else
				noExp4.setTipo(noExpressao.getTipo());

		} else if (eat(Tag.KW_TRUE) || eat(Tag.KW_FALSE)) {
			noExp4.setTipo(Tag.TIPO_LOGICO);
		} else if (eat(Tag.DOUBLE) || eat(Tag.INTEGER)) {
			noExp4.setTipo(Tag.TIPO_NUMERICO);
		} else if (eat(Tag.STRING)) {
			noExp4.setTipo(Tag.TIPO_STRING);
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
				return noExp4;
			} else {
				skip("Esperado \"ID, ConstInteger, ConstDouble, ConstString, true, false, -, !, (\"; " + "encontrado \""
						+ this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					Exp4();
			}
		}
		return noExp4;
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
		if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.INTEGER)
				|| token.getCodigo().equals(Tag.KW_TRUE) || token.getCodigo().equals(Tag.KW_FALSE)
				|| token.getCodigo().equals(Tag.OP_NEGATIVO) || token.getCodigo().equals(Tag.OP_NEGACAO)
				|| token.getCodigo().equals(Tag.KW_ABREPAR) || token.getCodigo().equals(Tag.DOUBLE)
				|| token.getCodigo().equals(Tag.STRING)) {
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
			No noTipoPrimitivo = TipoPrimitivo();
			if (!eat(Tag.ID)) {
				sinalizaErroSintatico("Esperado \"ID\"; encontrado \"" + this.token.getLexema() + "\"");
			} else {
				Token tokenAux = new Token(this.token.codigo, this.token.lexema, this.token.linha, this.token.coluna);
				this.lexer.getTs().setTipo(tokenAux.lexema, noTipoPrimitivo.getTipo());
			}
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
			No noTipoPrimitivo = TipoPrimitivo();
			if (!eat(Tag.ID)) {
				sinalizaErroSintatico("Esperado \"ID\"; encontrado \"" + this.token.getLexema() + "\"");
			} else {
				Token tokenAux = new Token(this.token.codigo, this.token.lexema, this.token.linha, this.token.coluna);
				this.lexer.getTs().setTipo(tokenAux.lexema, noTipoPrimitivo.getTipo());
			}
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

	public No TipoPrimitivo() {
		No noTipoPrimitivo = new No();

		if (eat(Tag.KW_BOOLEAN)) {
			noTipoPrimitivo.setTipo(Tag.TIPO_LOGICO);
		} else if (eat(Tag.KW_INTEGER)) {
			noTipoPrimitivo.setTipo(Tag.TIPO_INT);
		} else if (eat(Tag.KW_DOUBLE)) {
			noTipoPrimitivo.setTipo(Tag.TIPO_DOUBLE);
		} else if (eat(Tag.KW_STRING)) {
			noTipoPrimitivo.setTipo(Tag.TIPO_STRING);
		} else if (eat(Tag.KW_VOID)) {
			noTipoPrimitivo.setTipo(Tag.TIPO_VAZIO);
		} else {
			// Synch TipoPrimitivo
			if (token.getCodigo().equals(Tag.ID)) {
				sinalizaErroSintatico("Esperado \"bool, integer, String, double, void\"; encontrado \""
						+ this.token.getLexema() + "\"");
				return noTipoPrimitivo;
			} else {
				skip("Esperado \"bool, integer, String, double, void\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					TipoPrimitivo();
			}
		}
		return noTipoPrimitivo;
	}

	public No OpUnario() {
		No noOpUnario = new No();

		if(eat(Tag.OP_NEGACAO)) {
			noOpUnario.setTipo(Tag.TIPO_LOGICO);
		} else if(eat(Tag.OP_NEGATIVO)) {
			noOpUnario.setTipo(Tag.TIPO_NUMERICO);
		} else {
			//synch OpUnario
			if (token.getCodigo().equals(Tag.ID) || token.getCodigo().equals(Tag.STRING)
				|| token.getCodigo().equals(Tag.KW_TRUE) || token.getCodigo().equals(Tag.KW_FALSE)
				|| token.getCodigo().equals(Tag.DOUBLE) || token.getCodigo().equals(Tag.INTEGER)
				|| token.getCodigo().equals(Tag.KW_FECHAPAR)) {
				sinalizaErroSintatico("Esperado \"-, !\"; encontrado \""
						+ this.token.getLexema() + "\"");
				return noOpUnario;
			} else {
				skip("Esperado \"-, !\"; encontrado \"" + this.token.getLexema() + "\"");
				if (!token.getCodigo().equals(Tag.EOF))
					OpUnario();
			}
		}
		return noOpUnario;
	}
}
