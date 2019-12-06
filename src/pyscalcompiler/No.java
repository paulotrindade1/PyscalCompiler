package pyscalcompiler;

public class No {
    private String tipo;

    public No() {
        this.tipo = Tag.TIPO_VAZIO;
    }

    public String getTipo(){
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
