import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class JogoDoLixo extends JPanel implements ActionListener {

    // Adicione a variável para controlar o estado de "game over"
    boolean gameOver = false;
    boolean restartRequested = false;

    int boardWidth = 2000;
    int boardHeight = 750;

    Image cenarioImg;
    Image personagemLixeiroEsquerdaImg;
    Image personagemLixeiroDireitaImg;
    Image reciclavelImg;
    Image aguaImg;
    Image bananaImg;
    Image macaImg;
    Image sacoDeLixoImg;
    Image venenoImg;
    Image placaImg;

    // Tamanho do personagem
    int lixeiroWidth = 200;
    int lixeiroHeight = 150;

    // Tamanho dos itens
    int itemWidth = 100;
    int itemHeight = 100;

    // Posição do personagem
    int lixeiroInitialX = boardWidth / 4;
    int lixeiroX = lixeiroInitialX;
    int lixeiroY = boardHeight - 240;

    // Tamanho da placa de pontos
    int placaWidth = 200;
    int placaHeight = 150;

    // Posição da placa de pontos
    int placaX = 20;
    int placaY = 30;

    // Movimentação do personagem
    int lixeiroSpeed = 10;
    int lixeiroJump = +250;

    boolean gravidade = false;
    boolean lixeiroPula = false;
    boolean lixeiroMoveEsquerda = false;
    boolean lixeiroMoveDireita = false;
    boolean lixeiroNoChao = true;

    int pontos = 0;
    int vidas = 3;

    Timer gameLoop;
    ItemManager itemManager;

    boolean jogadorVivo = true;
    boolean jogoEmExecucao = true;

    JButton btnPlayAgain; // Botão "Play Again"

    public JogoDoLixo() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        cenarioImg = new ImageIcon(getClass().getResource("/cenario.jpg")).getImage();
        personagemLixeiroEsquerdaImg = new ImageIcon(getClass().getResource("/personagem_lixeiro_esquerda.png")).getImage();
        personagemLixeiroDireitaImg = new ImageIcon(getClass().getResource("/personagem_lixeiro_direita.png")).getImage();
        reciclavelImg = new ImageIcon(getClass().getResource("/reciclavel.png")).getImage();
        aguaImg = new ImageIcon(getClass().getResource("/agua.png")).getImage();
        bananaImg = new ImageIcon(getClass().getResource("/banana.png")).getImage();
        macaImg = new ImageIcon(getClass().getResource("/maca.png")).getImage();
        sacoDeLixoImg = new ImageIcon(getClass().getResource("/SacoDeLixo.png")).getImage();
        venenoImg = new ImageIcon(getClass().getResource("/veneno.png")).getImage();
        placaImg = new ImageIcon(getClass().getResource("/placa.png")).getImage();

        // Crie o botão "Play Again"
        btnPlayAgain = new JButton("JOGAR NOVAMENTE");
        btnPlayAgain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame(); // Chame o método para reiniciar o jogo
            }
        });
        btnPlayAgain.setBounds((boardWidth - 150) / 2, (boardHeight - 150) / 2, 150, 150);
        btnPlayAgain.setVisible(false); // Inicialmente oculto

        

        // Adicione o botão à tela
        add(btnPlayAgain);

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        itemManager = new ItemManager(); // Inicializa o ItemManager

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_LEFT) {
                    lixeiroMoveEsquerda = true;
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    lixeiroMoveDireita = true;
                } else if (keyCode == KeyEvent.VK_UP && lixeiroNoChao) {
                    lixeiroPula = true;
                    lixeiroNoChao = false;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_LEFT) {
                    lixeiroMoveEsquerda = false;
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    lixeiroMoveDireita = false;
                }
            }
        });

        setFocusable(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (jogoEmExecucao) {
            if (!gameOver) {
                if (lixeiroMoveEsquerda && lixeiroX > 0) {
                    lixeiroX -= lixeiroSpeed;
                }
                if (lixeiroMoveDireita && lixeiroX < 1075) {
                    lixeiroX += lixeiroSpeed;
                }

                if (lixeiroPula) {
                    if (lixeiroY > lixeiroJump) {
                        lixeiroY -= lixeiroSpeed;
                    } else {
                        lixeiroPula = false;
                    }
                } else if (lixeiroY < boardHeight - 240) {
                    // Simulação da gravidade
                    lixeiroY += lixeiroSpeed;
                    // Se o personagem cair para a posição inicial do eixo Y, defina-o como no chão
                    if (lixeiroY >= boardHeight - 240) {
                        lixeiroY = boardHeight - 240;
                        lixeiroNoChao = true;
                    }
                }

                itemManager.update(); // Atualiza os itens

                // Verifica a colisão com os itens
                for (int i = 0; i < itemManager.itens.size(); i++) {
                    Item item = itemManager.itens.get(i);
                    if (colisao(lixeiroX, lixeiroY, lixeiroWidth, lixeiroHeight, item.getX(), item.getY(), itemWidth, itemHeight)) {
                        if (item.getImg() == sacoDeLixoImg || item.getImg() == venenoImg) {
                            vidas--;
                            if (vidas == 0) {
                                gameOver = true; // Define o estado de Game Over
                            }
                        } else {
                            pontos++;
                        }
                        itemManager.itens.remove(i);
                        i--;
                    }
                }
            } else { // Se o jogo estiver no estado de Game Over
                btnPlayAgain.setVisible(true); // Exibe o botão "Play Again"
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenha o cenário
        if (cenarioImg != null) {
            g.drawImage(cenarioImg, 0, 0, boardWidth, boardHeight, null);
        }

        // Desenha os itens
        itemManager.draw(g);

        // Desenha o personagem
        Image lixeiroImg = lixeiroMoveEsquerda ? personagemLixeiroEsquerdaImg : personagemLixeiroDireitaImg;
        g.drawImage(lixeiroImg, lixeiroX, lixeiroY, lixeiroWidth, lixeiroHeight, null);

        // Desenha a placa de pontos
        g.drawImage(placaImg, placaX, placaY, placaWidth, placaHeight, null);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString(Integer.toString(pontos), placaX + 86, placaY + 90);

        // Desenha a contagem de vidas em vermelho
        g.setColor(Color.red);
        g.drawString("Vidas: " + vidas, 1100, 90);

    }

    // Método para verificar colisão entre dois retângulos
    private boolean colisao(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    // Método para reiniciar o jogo
    private void restartGame() {
        vidas = 3;
        pontos = 0;
        gameOver = false;
        lixeiroX = lixeiroInitialX;
        lixeiroY = boardHeight - 240;
        itemManager.itens.clear(); // Limpa todos os itens
        btnPlayAgain.setVisible(false); // Oculta o botão "Play Again"
        repaint(); // Redesenha o jogo
    }

    class ItemManager {
        private ArrayList<Item> itens;
        private Random random;

        public ItemManager() {
            itens = new ArrayList<>();
            random = new Random();
        }

        public void update() {
            // Gera novos itens aleatoriamente
            if (random.nextDouble() < 0.02) { // Ajuste a probabilidade conforme necessário
                int x = random.nextInt(boardWidth - itemWidth); // Posição aleatória no eixo x
                int y = -100; // Posição inicial acima da tela
                Image img = null;
                // Escolha aleatoriamente uma imagem para o novo item
                int itemIndex = random.nextInt(6); // 6 imagens diferentes, ajuste conforme necessário
                switch (itemIndex) {
                    case 0:
                        img = reciclavelImg;
                        break;
                    case 1:
                        img = aguaImg;
                        break;
                    case 2:
                        img = bananaImg;
                        break;
                    case 3:
                        img = macaImg;
                        break;
                    case 4:
                        img = sacoDeLixoImg;
                        break;
                    case 5:
                        img = venenoImg;
                        break;
                    // Adicione mais cases conforme necessário para mais imagens
                }
                itens.add(new Item(x, y, img)); // Adiciona um novo item com a imagem correspondente
            }

            // Move os itens para baixo
            for (Item item : itens) {
                item.move();
            }

            // Remove os itens que saíram da tela
            itens.removeIf(item -> item.getY() > boardHeight); // Ajuste conforme necessário
        }

        public void draw(Graphics g) {
            for (Item item : itens) {
                item.draw(g);
            }
        }
    }

    class Item {
        private int x;
        private int y;
        private Image img;

        public Item(int x, int y, Image img) {
            this.x = x;
            this.y = y;
            this.img = img;
        }

        public void move() {
            y += 5; // Ajuste a velocidade conforme necessário
        }

        public void draw(Graphics g) {
            g.drawImage(img, x, y, itemWidth, itemHeight, null);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Image getImg() {
            return img;
        }
    }
}
