import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        
            int boardWidth = 1920;
            int boardHeight = 800;

            JFrame frame = new JFrame("Jogo do Lixo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setSize(boardWidth, boardHeight);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            JogoDoLixo jogoDoLixo = new JogoDoLixo();
            frame.add(jogoDoLixo);
            
            frame.setVisible(true);
    }
}
