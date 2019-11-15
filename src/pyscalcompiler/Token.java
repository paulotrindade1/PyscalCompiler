package pyscalcompiler;

public class Token {

    String codigo;
    String lexema;
    int linha;
    int coluna;

    public Token(String codigo, String lexema, int linha, int coluna) {
        this.codigo = codigo;
        this.lexema = lexema;
        this.linha = linha;
        this.coluna = coluna;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

    @Override
    public String toString() {
        return "<" + this.codigo + ", " + this.lexema + ">"
                + " Line " + this.linha + " Column " + (this.coluna);
    }

}
