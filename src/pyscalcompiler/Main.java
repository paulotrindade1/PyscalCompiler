package pyscalcompiler;

public class Main {

    public static void main(String[] args) {
        //String fileName = "Pyscal.txt";
        //String fileName = "PyscalTest.txt"; 
        String fileName = "TestePaulo.txt";


        Lexer lexer = new Lexer(fileName);
        Parser parser = new Parser(lexer);

        parser.Programa();

        parser.getLexer().closeFile();

        System.out.println("\n=>Tabela de simbolos:");
        lexer.printTabelaSimbolos();
        System.out.println("\n=>Fim da compilacao.");

    }
}
