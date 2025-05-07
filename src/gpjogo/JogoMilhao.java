package gpjogo;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Classe principal
public class JogoMilhao {
    public static void main(String[] args) {
        // Inicia a interface gráfica na thread correta
        SwingUtilities.invokeLater(TelaInicio::new);
    }
}

// Tela inicial com botão "Iniciar Jogo"
class TelaInicio extends JFrame {
    public TelaInicio() {
        setTitle("Jogo do Milhão");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza

        JButton iniciar = new JButton("Iniciar Jogo");
        iniciar.addActionListener(_ -> {
            dispose(); // Fecha tela atual
            new TelaPergunta(1); // Começa na fase 1
        });

        add(iniciar);
        setVisible(true);
    }
}

// Tela de perguntas (cada fase)
class TelaPergunta extends JFrame {
    private final Pergunta perguntaAtual;
    private Timer timer;
    private final JLabel tempoLabel;
    private int tempo = 60; // 1 minuto por pergunta

    public TelaPergunta(int fase) {
        setTitle("Fase " + fase);
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        perguntaAtual = BancoPerguntas.getPerguntaAleatoria();

        JLabel perguntaLabel = new JLabel("<html>" + perguntaAtual.pergunta + "</html>");
        JButton[] botoes = new JButton[4];

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        tempoLabel = new JLabel("Tempo: 60s");
        painel.add(tempoLabel);
        painel.add(perguntaLabel);

        // Botões das alternativas
        for (int i = 0; i < 4; i++) {
            int index = i;
            botoes[i] = new JButton(perguntaAtual.alternativas[i]);
            botoes[i].addActionListener(_ -> {
                if (index == perguntaAtual.correta) {
                    dispose();
                    if (fase == 10)
                        new TelaFinal(true); // Venceu
                    else
                        new TelaPergunta(fase + 1);
                } else {
                    dispose();
                    new TelaFinal(false); // Errou
                }
                timer.stop();
            });
            painel.add(botoes[i]);
        }

        // Botão de pause
        JButton pauseBtn = new JButton("Pause");
        pauseBtn.addActionListener(_ -> {
            timer.stop();
            new TelaPause(this);
        });
        painel.add(pauseBtn);

        add(painel);
        iniciarTimer();
        setVisible(true);
    }

    private void iniciarTimer() {
        timer = new Timer(1000, _ -> {
            tempo--;
            tempoLabel.setText("Tempo: " + tempo + "s");
            if (tempo <= 0) {
                timer.stop();
                dispose();
                new TelaFinal(false);
            }
        });
        timer.start();
    }

    public void retomar() {
        timer.start();
    }
}

// Tela de pause com opções: retomar, reiniciar e ir para o início
class TelaPause extends JFrame {
    public TelaPause(TelaPergunta tela) {
        setTitle("Pausado");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1));

        JButton retomar = new JButton("Retomar");
        retomar.addActionListener(_ -> {
            dispose();
            tela.retomar();
        });

        JButton reiniciar = new JButton("Reiniciar");
        reiniciar.addActionListener(_ -> {
            dispose();
            tela.dispose();
            new TelaPergunta(1);
        });

        JButton inicio = new JButton("Tela Inicial");
        inicio.addActionListener(_ -> {
            dispose();
            tela.dispose();
            new TelaInicio();
        });

        add(retomar);
        add(reiniciar);
        add(inicio);
        setVisible(true);
    }
}

// Tela final com mensagem de vitória ou derrota
class TelaFinal extends JFrame {
    public TelaFinal(boolean venceu) {
        setTitle("Fim de Jogo");
        setSize(300, 200);
        setLocationRelativeTo(null);

        JLabel mensagem = new JLabel(venceu ? "Você ganhou!" : "Você perdeu!");
        mensagem.setHorizontalAlignment(SwingConstants.CENTER);

        JButton reiniciar = new JButton("Reiniciar");
        reiniciar.addActionListener(_ -> {
            dispose();
            new TelaPergunta(1);
        });

        JButton inicio = new JButton("Tela Inicial");
        inicio.addActionListener(_ -> {
            dispose();
            new TelaInicio();
        });

        setLayout(new GridLayout(3, 1));
        add(mensagem);
        add(reiniciar);
        add(inicio);
        setVisible(true);
    }
}

// Classe modelo para perguntas
class Pergunta {
    String pergunta;
    String[] alternativas;
    int correta;

    public Pergunta(String pergunta, String[] alternativas, int correta) {
        this.pergunta = pergunta;
        this.alternativas = alternativas;
        this.correta = correta;
    }
}

// Banco de perguntas básicas de programação
class BancoPerguntas {
    private static final List<Pergunta> perguntas = Arrays.asList(
            new Pergunta("Qual palavra-chave define uma classe em Java?", new String[]{"define", "class", "struct", "object"}, 1),
            new Pergunta("Qual símbolo é usado para herança em Java?", new String[]{"extends", "implements", "inherits", "use"}, 0),
            new Pergunta("Qual tipo de dado armazena texto?", new String[]{"int", "double", "String", "char"}, 2),
            new Pergunta("Qual método é o ponto de entrada de um programa Java?", new String[]{"start()", "main()", "run()", "init()"}, 1),
            new Pergunta("Como declaramos uma variável inteira?", new String[]{"integer x", "int x", "x int", "num x"}, 1),
            new Pergunta("Qual destes é um laço de repetição?", new String[]{"for", "if", "switch", "case"}, 0),
            new Pergunta("Como comentamos uma linha em Java?", new String[]{"--", "//", "##", "**"}, 1),
            new Pergunta("Qual biblioteca é usada para GUI em Java?", new String[]{"AWT", "Swing", "JavaFX", "Todas as anteriores"}, 3),
            new Pergunta("O que significa JVM?", new String[]{"Java Virtual Machine", "Java Visual Machine", "Joint Virtual Memory", "Java Variable Mode"}, 0),
            new Pergunta("Qual palavra-chave previne herança?", new String[]{"final", "static", "const", "private"}, 0)
    );

    public static Pergunta getPerguntaAleatoria() {
        Collections.shuffle(perguntas);
        return perguntas.getFirst();
    }
}
