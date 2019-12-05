package pyscalcompiler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lexer {

    private FileInputStream fis;
    private InputStreamReader isr;
    private PushbackReader pbr;
    private Ts ts;
    private Token lastToken;
    private int col = 1;
    private int line = 1;

    public Lexer(String fileName) {
        try {
            this.fis = new FileInputStream(fileName);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.isr = new InputStreamReader(this.fis);
        this.pbr = new PushbackReader(this.isr);
        this.ts = new Ts();
    }

    public void closeFile() {
        try {
            this.fis.close();
        } catch (IOException ex) {
            Logger.getLogger(Lexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void apresentaErroLexico(String message) {
        System.out.println("[Erro lexico]: " + message);
    }

    public Character proximoCaracter() {
        Character caracter = '\uffff';
        int asciiChar = 0;
        try {
            asciiChar = pbr.read();
        } catch (IOException ex) {
            Logger.getLogger(Lexer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (asciiChar != -1) {
            caracter = (char) asciiChar;
        }

        return caracter;
    }

    public void retornarPonteiro(Character caracter) {
        try {
            this.pbr.unread(caracter);
        } catch (IOException ex) {
            Logger.getLogger(Lexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        col--;
    }

    public Token retornaToken(String codigo, String lexema, int line, int col) {
        Token token = new Token(codigo, lexema, line, col);
        if (this.ts.getToken(lexema) == null) {
            ts.addToken(lexema, token);
        } else {
            token.setCodigo(this.ts.getToken(lexema).getCodigo());
        }

        lastToken = token;
        return token;
    }

    public boolean stringVazia(String lexema) {
        return lexema.length() == 1;
    }

    public void printTabelaSimbolos() {
        this.ts.printTS();
    }

    public Token nextToken() {
        String lexema = "";
        Character c;
        int state = 1;

        while (true) {
            c = proximoCaracter();
            col++;

            switch (state) {
                case 1:
                    if (c == '\uffff') {
                        return new Token(Tag.EOF, lexema, line, col);
                    }
                    if (c == ' ' || c == '\t') {
                        state = 1;
                    } else if (c == '\n') {
                        state = 1;
                        col = 1;
                        line++;
                    } else if (c == '\r') {
                        state = 1;
                    } else if (Character.isLetter(c)) {
                        lexema += c;
                        state = 2;
                    } else if (Character.isDigit(c)) {
                        lexema += c;
                        state = 3;
                    } else if (c == '#') {
                        state = 5;
                    } else if (c == '>') {
                        lexema += c;
                        state = 6;
                    } else if (c == '<') {
                        lexema += c;
                        state = 7;
                    } else if (c == '!') {
                        lexema += c;
                        state = 8;
                    } else if (c == '(') {
                        lexema += c;
                        Token token = retornaToken(Tag.KW_ABREPAR, lexema, line, col);
                        return token;
                    } else if (c == ')') {
                        lexema += c;
                        Token token = retornaToken(Tag.KW_FECHAPAR, lexema, line, col);
                        return token;
                    } else if (c == '[') {
                        lexema += c;
                        Token token = retornaToken(Tag.KW_ABRECOL, lexema, line, col);
                        return token;
                    } else if (c == ']') {
                        lexema += c;
                        Token token = retornaToken(Tag.KW_FECHACOL, lexema, line, col);
                        return token;
                    } else if (c == '-') {
                        Token token;
                        lexema += c;
                        if (this.lastToken.getCodigo().equals(Tag.NUM)
                                || this.lastToken.getCodigo().equals(Tag.KW_FECHAPAR)
                                || this.lastToken.getCodigo().equals(Tag.ID)) {
                            token = retornaToken(Tag.OP_SUBTRACAO, lexema, line, col);
                        } else {
                            token = retornaToken(Tag.OP_NEGATIVO, lexema, line, col);
                        }
                        return token;

                    } else if (c == '+') {
                        lexema += c;
                        Token token = retornaToken(Tag.OP_SOMA, lexema, line, col);
                        return token;
                    } else if (c == '/') {
                        lexema += c;
                        Token token = retornaToken(Tag.OP_DIVISAO, lexema, line, col);
                        return token;
                    } else if (c == '=') {
                        lexema += c;
                        state = 9;
                    } else if (c == ':') {
                        lexema += c;
                        Token token = retornaToken(Tag.KW_DOISPONTOS, lexema, line, col);
                        return token;
                    } else if (c == ';') {
                        lexema += c;
                        Token token = retornaToken(Tag.KW_PONTOVIRGULA, lexema, line, col);
                        return token;
                    } else if (c == ',') {
                        lexema += c;
                        Token token = retornaToken(Tag.KW_VIRGULA, lexema, line, col);
                        return token;
                    } else if (c == '*') {
                        lexema += c;
                        Token token = retornaToken(Tag.OP_MULTIPLICAO, lexema, line, col);
                        return token;
                    } else if (c == '"') {
                        lexema += c;
                        state = 10;
                    } else if (c == '.') {
                    	lexema += c;
                    	Token token = retornaToken(Tag.KW_PONTO, lexema, line, col);
                        return token;
                    } else {
                        apresentaErroLexico(String.format("Invalid caracter '%c' at line %d column %d", c, line, col));
                        return null;
                    }
                    break;

                case 2:
                    if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
                        lexema += c;
                    } else {
                        retornarPonteiro(c);
                        Token token = retornaToken(Tag.ID, lexema, line, col);
                        return token;
                    }
                    break;

                case 3:
                    if (Character.isDigit(c)) {
                        lexema += c;
                    } else if (c == '.') {
                        lexema += c;
                        state = 4;
                    } else {
                        retornarPonteiro(c);
                        Token token = retornaToken(Tag.NUM, lexema, line, col);
                        return token;
                    }
                    break;

                case 4:
                    if (Character.isDigit(c)) {
                        lexema += c;
                    } else {
                        retornarPonteiro(c);
                        Token token = retornaToken(Tag.NUM, lexema, line, col);
                        return token;
                    }
                    break;

                case 5:
                    if (c == '\n') {
                        line++;
                        col = 1;
                        state = 1;
                    }
                    break;

                case 6:
                    if (c == '=') {
                        lexema += c;
                        Token token = retornaToken(Tag.OP_MAIOR_IGUAL, lexema, line, col);
                        return token;
                    } else {
                        retornarPonteiro(c);
                        Token token;
                        token = retornaToken(Tag.OP_MAIOR, lexema, line, col);
                        return token;
                    }

                case 7:
                    if (c == '=') {
                        lexema += c;
                        Token token = retornaToken(Tag.OP_MENOR_IGUAL, lexema, line, col);
                        return token;
                    } else {
                        retornarPonteiro(c);
                        Token token = retornaToken(Tag.OP_MENOR, lexema, line, col);
                        return token;
                    }

                case 8:
                    if (c == '=') {
                        lexema += c;
                        Token token = retornaToken(Tag.OP_DIFERENTE, lexema, line, col);
                        return token;
                    } else {
                        retornarPonteiro(c);
                        Token token = retornaToken(Tag.OP_NEGACAO, lexema, line, col);
                        return token;
                    }

                case 9:
                    if (c == '=') {
                        lexema += c;
                        Token token = retornaToken(Tag.OP_IGUAL, lexema, line, col);
                        return token;
                    } else {
                        retornarPonteiro(c);
                        Token token = retornaToken(Tag.KW_ATTRIB, lexema, line, col);
                        return token;
                    }

                case 10:
                    if (c == '"') {
                        if (stringVazia(lexema)) {
                            apresentaErroLexico(String.format("Empty string! line %d column %d", line, col));
                            return null;
                        } else {
                            lexema += c;
                            Token token = retornaToken(Tag.KW_STRING, lexema, line, col);
                            return token;
                        }
                    } else if (c == '\uffff') {
                        apresentaErroLexico(String.format("String not closed! line %d column %d", line, col));
                        return null;

                    } else if (c == '\n') {
                        line++;
                        col = 1;
                    } else {
                        lexema += c;
                    }
            }
        }
    }

}
