package deepjogo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JogoDoMilhao {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private List<Pergunta> perguntas;
    private int faseAtual, pontuacao;
    private Timer timer;
    private int tempoRestante;
    private JLabel perguntaLabel, tempoLabel;
    private JButton[] alternativaBtns;
    private boolean jogoPausado;

    public JogoDoMilhao() {
        inicializarUI();
        carregarPerguntas();
        iniciarJogo();
    }

    private void inicializarUI() {
        JFrame frame = new JFrame("Jogo do Milhão");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        criarTelaInicio();
        criarTelaJogo();
        criarTelaPause();

        mainPanel.add(new JPanel(), "vazio");
        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void criarTelaInicio() {
        JPanel inicioPanel = new JPanel(new BorderLayout());
        inicioPanel.setBackground(new Color(107, 107, 229));

        JLabel tituloLabel = new JLabel("Jogo do Milhão", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 48));
        tituloLabel.setForeground(Color.YELLOW);
        inicioPanel.add(tituloLabel, BorderLayout.CENTER);

        JButton iniciarBtn = new JButton("INICIAR");
        iniciarBtn.setFont(new Font("Arial", Font.BOLD, 24));
        iniciarBtn.setBackground(Color.YELLOW);
        iniciarBtn.setForeground(new Color(107, 107, 229));
        iniciarBtn.addActionListener(_ -> {
            iniciarJogo();
            cardLayout.show(mainPanel, "jogo");
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(iniciarBtn);
        inicioPanel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(inicioPanel, "inicio");
    }

    private void criarTelaJogo() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(new Color(107, 107, 229));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JButton pauseBtn = new JButton("II");
        pauseBtn.setFont(new Font("Arial", Font.BOLD, 18));
        pauseBtn.setBackground(Color.YELLOW);
        pauseBtn.setForeground(new Color(107, 107, 229));
        pauseBtn.addActionListener(_ -> pausarJogo());

        tempoLabel = new JLabel("01:00", SwingConstants.CENTER);
        tempoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        tempoLabel.setForeground(Color.YELLOW);

        topPanel.add(pauseBtn, BorderLayout.WEST);
        topPanel.add(tempoLabel, BorderLayout.EAST);

        perguntaLabel = new JLabel("", SwingConstants.CENTER);
        perguntaLabel.setFont(new Font("Arial", Font.BOLD, 24));
        perguntaLabel.setForeground(Color.WHITE);

        JPanel alternativasPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        alternativasPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        alternativasPanel.setOpaque(false);

        alternativaBtns = new JButton[4];
        for (int i = 0; i < 4; i++) {
            alternativaBtns[i] = new JButton();
            alternativaBtns[i].setFont(new Font("Arial", Font.PLAIN, 18));
            alternativaBtns[i].setBackground(Color.WHITE);
            final int index = i;
            alternativaBtns[i].addActionListener(_ -> verificarResposta(index));
            alternativasPanel.add(alternativaBtns[i]);
        }

        gamePanel.add(topPanel, BorderLayout.NORTH);
        gamePanel.add(perguntaLabel, BorderLayout.CENTER);
        gamePanel.add(alternativasPanel, BorderLayout.SOUTH);

        mainPanel.add(gamePanel, "jogo");
    }

    private void criarTelaPause() {
        JPanel pausePanel = new JPanel(new GridLayout(4, 1, 10, 10));
        pausePanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        pausePanel.setBackground(new Color(107, 107, 229));

        JLabel pauseLabel = new JLabel("JOGO PAUSADO", SwingConstants.CENTER);
        pauseLabel.setFont(new Font("Arial", Font.BOLD, 36));
        pauseLabel.setForeground(Color.YELLOW);
        pausePanel.add(pauseLabel);

        JButton retomarBtn = new JButton("Retomar Jogo");
        estilizarBotaoPause(retomarBtn);
        retomarBtn.addActionListener(_ -> retomarJogo());

        JButton reiniciarBtn = new JButton("Reiniciar Jogo");
        estilizarBotaoPause(reiniciarBtn);
        reiniciarBtn.addActionListener(_ -> reiniciarJogo());

        JButton inicioBtn = new JButton("Voltar ao Início");
        estilizarBotaoPause(inicioBtn);
        inicioBtn.addActionListener(_ -> voltarAoInicio());

        pausePanel.add(retomarBtn);
        pausePanel.add(reiniciarBtn);
        pausePanel.add(inicioBtn);

        mainPanel.add(pausePanel, "pause");
    }

    private void estilizarBotaoPause(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setBackground(Color.YELLOW);
        btn.setForeground(new Color(107, 107, 229));
    }

    private void carregarPerguntas() {
        perguntas = new ArrayList<>();

        // Pergunta 1
        perguntas.add(new Pergunta(
                "Qual é o tipo de retorno de um método que não retorna valor?",
                Arrays.asList("int", "void", "String", "boolean"),
                1
        ));

        // Pergunta 2
        perguntas.add(new Pergunta(
                "Qual palavra-chave é usada para herança em Java?",
                Arrays.asList("implements", "inherits", "extends", "super"),
                2
        ));

        // Pergunta 3
        perguntas.add(new Pergunta(
                "Qual desses NÃO é um tipo primitivo em Java?",
                Arrays.asList("int", "float", "String", "boolean"),
                2
        ));

        // Pergunta 4
        perguntas.add(new Pergunta(
                "Qual método é chamado quando um objeto é criado?",
                Arrays.asList("main()", "constructor", "init()", "start()"),
                1
        ));

        // Pergunta 5
        perguntas.add(new Pergunta(
                "Qual desses é um loop em Java?",
                Arrays.asList("if", "switch", "for", "try"),
                2
        ));

        // Pergunta 6
        perguntas.add(new Pergunta(
                "Qual classe é usada para ler entrada do usuário no console?",
                Arrays.asList("Scanner", "Input", "Reader", "System"),
                0
        ));

        // Pergunta 7
        perguntas.add(new Pergunta(
                "Qual modificador de acesso é o mais restritivo?",
                Arrays.asList("public", "protected", "private", "default"),
                2
        ));

        // Pergunta 8
        perguntas.add(new Pergunta(
                "Qual método é usado para comparar strings em Java?",
                Arrays.asList("equals()", "compare()", "==", "match()"),
                0
        ));

        // Pergunta 9
        perguntas.add(new Pergunta(
                "Qual desses é um operador lógico em Java?",
                Arrays.asList("+", "&&", "++", "="),
                1
        ));

        // Pergunta 10
        perguntas.add(new Pergunta(
                "Qual interface é usada para ordenação em Java?",
                Arrays.asList("Runnable", "Serializable", "Comparable", "Cloneable"),
                2
        ));

        Collections.shuffle(perguntas);
    }

    private void iniciarJogo() {
        faseAtual = 0;
        pontuacao = 0;
        jogoPausado = false;
        Collections.shuffle(perguntas);
        mostrarPerguntaAtual();
    }

    private void mostrarPerguntaAtual() {
        if (faseAtual >= 10) {
            fimDeJogo(true);
            return;
        }

        Pergunta pergunta = perguntas.get(faseAtual);
        perguntaLabel.setText(pergunta.texto());

        List<String> alternativas = pergunta.alternativas();
        for (int i = 0; i < 4; i++) {
            alternativaBtns[i].setText(alternativas.get(i));
        }

        iniciarTemporizador();
    }

    private void iniciarTemporizador() {
        if (timer != null) {
            timer.stop();
        }

        tempoRestante = 60;
        atualizarTempoLabel();

        timer = new Timer(1000, _ -> {
            tempoRestante--;
            atualizarTempoLabel();

            if (tempoRestante <= 0 && !jogoPausado) {
                timer.stop();
                fimDeJogo(false);
            }
        });

        timer.start();
    }

    private void atualizarTempoLabel() {
        int minutos = tempoRestante / 60;
        int segundos = tempoRestante % 60;
        tempoLabel.setText(String.format("%02d:%02d", minutos, segundos));
    }

    private void verificarResposta(int alternativaIndex) {
        if (jogoPausado) return;

        Pergunta pergunta = perguntas.get(faseAtual);
        if (alternativaIndex == pergunta.respostaCorreta()) {
            pontuacao++;
            faseAtual++;
            timer.stop();
            mostrarPerguntaAtual();
        } else {
            fimDeJogo(false);
        }
    }

    private void pausarJogo() {
        if (timer != null) {
            timer.stop();
        }
        jogoPausado = true;
        cardLayout.show(mainPanel, "pause");
    }

    private void retomarJogo() {
        jogoPausado = false;
        cardLayout.show(mainPanel, "jogo");
        iniciarTemporizador();
    }

    private void reiniciarJogo() {
        iniciarJogo();
        cardLayout.show(mainPanel, "jogo");
    }

    private void voltarAoInicio() {
        cardLayout.show(mainPanel, "inicio");
    }

    private void fimDeJogo(boolean vitoria) {
        if (timer != null) {
            timer.stop();
        }

        JPanel fimPanel = new JPanel(new BorderLayout());
        fimPanel.setBackground(new Color(107, 107, 229));

        JLabel mensagemLabel = new JLabel("", SwingConstants.CENTER);
        mensagemLabel.setFont(new Font("Arial", Font.BOLD, 36));
        mensagemLabel.setForeground(Color.YELLOW);

        if (vitoria) {
            mensagemLabel.setText("PARABÉNS! VOCÊ GANHOU!");
        } else {
            mensagemLabel.setText("FIM DE JOGO!");
        }

        JLabel pontuacaoLabel = new JLabel("Pontuação: " + pontuacao + "/10", SwingConstants.CENTER);
        pontuacaoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        pontuacaoLabel.setForeground(Color.WHITE);

        JButton reiniciarBtn = new JButton("Jogar Novamente");
        estilizarBotaoPause(reiniciarBtn);
        reiniciarBtn.addActionListener(_ -> {
            reiniciarJogo();
            cardLayout.show(mainPanel, "jogo");
        });

        JButton inicioBtn = new JButton("Voltar ao Início");
        estilizarBotaoPause(inicioBtn);
        inicioBtn.addActionListener(_ -> voltarAoInicio());

        JPanel botoesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        botoesPanel.setOpaque(false);
        botoesPanel.add(reiniciarBtn);
        botoesPanel.add(inicioBtn);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(mensagemLabel);
        centerPanel.add(pontuacaoLabel);
        centerPanel.add(botoesPanel);

        fimPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(fimPanel, "fim");
        cardLayout.show(mainPanel, "fim");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JogoDoMilhao::new);
    }
}

record Pergunta(String texto, List<String> alternativas, int respostaCorreta) {
    Pergunta(String texto, List<String> alternativas, int respostaCorreta) {
        this.texto = texto;
        this.alternativas = new ArrayList<>(alternativas);
        this.respostaCorreta = respostaCorreta;
    }
}