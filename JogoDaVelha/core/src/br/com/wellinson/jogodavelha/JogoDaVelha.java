package br.com.wellinson.jogodavelha;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;

public class JogoDaVelha extends ApplicationAdapter {
	SpriteBatch batch;
	Texture desenhoTabuleiro;
	Texture desenhoX;
	Texture desenhoO;
	BitmapFont fonteTitulo;
	BitmapFont fonteInformacaoes;
	
	char[][] tabuleiro;
	
	char computador;
	char jogador;
	char simbolos[] = {'X', 'O'};
	
	boolean vezDoJogador;
	boolean fimDeJogo;
	
	Random random;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		desenhoTabuleiro = new Texture("tabuleiro.png");
		desenhoX = new Texture("x.png");
		desenhoO = new Texture("o.png");
		
		fonteTitulo = criarFonte(25, Color.YELLOW);
		fonteInformacaoes = criarFonte(13, Color.WHITE);
		
		tabuleiro = new char[3][3];
		
		limparTabuleiro();
		
		random = new Random();
		
		definirJogadores();
		
		vezDoJogador = new Random().nextBoolean();
		fimDeJogo = false;
	}
	
	@Override
	public void render () {
		ScreenUtils.clear(0.6f, 0.6f, 0.6f, 1);
		
		batch.begin();
		
		batch.draw(desenhoTabuleiro, 0, 0);
		fonteTitulo.draw(batch, "Jogo da Velha",Gdx.graphics.getWidth()/2f-70, Gdx.graphics.getHeight()/2f+180);
		fonteInformacaoes.draw(batch, "Jogador: "+ jogador,Gdx.graphics.getWidth()/2f-150, Gdx.graphics.getHeight()/2f+150);
		fonteInformacaoes.draw(batch, "Computador: "+ computador,Gdx.graphics.getWidth()/2f-150, Gdx.graphics.getHeight()/2f+130);
		
		desenharJogadas();
		
		if(fimDeJogo) {
			char vencedor = verificarVencedor();
			String mensagem  = vencedor == '-' ? "Deu Velha" : vencedor + " venceu";
			
			fonteTitulo.draw(batch, mensagem, Gdx.graphics.getWidth()/2f-70, Gdx.graphics.getHeight()/2f);
		}
		
		batch.end();
		
		
		//indetificar jogada do usuário
		if(!fimDeJogo && Gdx.input.justTouched()) {
			int x = Gdx.input.getX();
			int y = Gdx.graphics.getHeight() - Gdx.input.getY();
			int linha = y / 100;
			int coluna = x / 100;
			
			if(linha >=0 && linha < 3 && coluna >=0 && coluna < 3 && tabuleiro[linha][coluna] == '-') {
				tabuleiro[linha][coluna] = jogador;
				vezDoJogador = !vezDoJogador;
				
				char vencedor = verificarVencedor();
				if(vencedor != '-') 
					fimDeJogo  = true;
				else if(verificarTabuleiroCheio()) {
					fimDeJogo = true;
				}
			}
		}
		
		//jogada do computador
		if(!fimDeJogo && !vezDoJogador) {
			fazerJogadaComputador();
			vezDoJogador = !vezDoJogador;
			
			char vencedor = verificarVencedor();
			if(vencedor != '-') 
				fimDeJogo  = true;
			else if(verificarTabuleiroCheio()) {
				fimDeJogo = true;
			}
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		desenhoTabuleiro.dispose();
	}
	
	private void desenharJogadas() {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				float posX = j * 100 + 10;
				float posY = i * 100 + 10;
				
				if(tabuleiro[i][j] == 'X')
					batch.draw(desenhoX, posX, posY);
				else if(tabuleiro[i][j] == 'O')
					batch.draw(desenhoO, posX, posY);				
			}
		}
		
	}
	
	private void limparTabuleiro() {
		for(int i = 0; i < 3; i++) 
			for(int j = 0; j < 3; j++)
				tabuleiro[i][j] = '-';
	}
	
	private BitmapFont criarFonte(int tamanho, Color cor) {
		BitmapFont fonte;
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ARIALBD.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parametro = new FreeTypeFontGenerator.FreeTypeFontParameter();
		
		parametro.size = tamanho;
		parametro.color = cor;
		
		fonte = generator.generateFont(parametro);
		
		generator.dispose();
		
		return fonte;
	}
	
	private void definirJogadores() {
		computador = simbolos[random.nextInt(2)];
		jogador  = computador == 'X' ? 'O' : 'X';		
	}
	
	private void fazerJogadaComputador() {
		int linha, coluna;
		
		do {
			linha = random.nextInt(3);
			coluna = random.nextInt(3);
		}while(tabuleiro[linha][coluna] != '-');
		
		tabuleiro[linha][coluna] = computador;
		
	}
	
	private char verificarVencedor() {
		char vencedor = '-';
		
		//verificar as linhas
		for(int i = 0; i < 3; i++) {
			if(tabuleiro[i][0] != '-' && tabuleiro[i][0] == tabuleiro[i][1] && tabuleiro[i][0] == tabuleiro[i][2]) {
				vencedor = tabuleiro[i][0];
				break;
			}
		}
		
		//verificar as colunas
		for(int i = 0; i < 3; i++) {
			if(tabuleiro[0][i] != '-' && tabuleiro[0][i] == tabuleiro[1][i] && tabuleiro[0][i] == tabuleiro[2][i]) {
				vencedor = tabuleiro[0][i];
				break;
			}
		}
		
		//verificar diagonal esquerda para direita
		for(int i = 0; i < 3; i++) {
			if(tabuleiro[0][0] != '-' && tabuleiro[0][0] == tabuleiro[1][1] && tabuleiro[0][0] == tabuleiro[2][2]) {
				vencedor = tabuleiro[0][0];
				break;
			}
		}
		
		//verificar diagonal direira para esquerda
		for(int i = 0; i < 3; i++) {
			if(tabuleiro[0][2] != '-' && tabuleiro[0][2] == tabuleiro[1][1] && tabuleiro[0][2] == tabuleiro[2][0]) {
				vencedor = tabuleiro[0][2];
				break;
			}
		}	
		
		return vencedor;
	}
	
	private boolean verificarTabuleiroCheio() {
		for(int i = 0; i < 3; i++) 
			for(int j = 0; j < 3; j++) {
				if(tabuleiro[i][j] == '-')
					return false;
			}
		
		return true;
	}
}

