package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Tabuleiro implements CampoObservador{
	
	private final int linhas;
	private final int colunas;
	private final int minas;
	
	private final List<Campo> campos = new ArrayList<>();
	private final List<Consumer<Boolean>> observadores =
			new ArrayList<>();
	
	public Tabuleiro( int linhas, int colunas, int minas) {
		this.linhas = linhas;
		this.colunas = colunas;
		this.minas = minas;
		
		gerarCampos();
		associarVizinhos();
		sortearMinas();
	}
	
	public void paraCadaCampo(Consumer<Campo> funcao) {
		campos.forEach(funcao);
		
    }
	
	public void registrarObservador(Consumer<Boolean> observador) {
		observadores.add(observador);
	}
	
	private void notificarObservadores(boolean resultado) {
		observadores.stream()
		.forEach(o -> o.accept(resultado));
	}
	
	// Metodo para abrir e procurar a linha e coluna que foi clicada
	
	public void abrir(int linha, int coluna) {
		campos.parallelStream()
		.filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
		.findFirst()
		.ifPresent(c -> c.abrir());
	}
	

	// Metodo marcar para alternar a linha e a coluna selecionada
	
	public void alternarMarcacao(int linha, int coluna) {
		campos.parallelStream()
		.filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
		.findFirst()
		.ifPresent(c -> c.alternarMarcacao());
	}
	
	
	// Gerando os campos do campo minado
	
	private void gerarCampos() {
			for (int linha = 0; linha < linhas; linha++) {
				for (int coluna = 0; coluna < colunas; coluna++) {
					Campo campo = new Campo(linha, coluna);
					campo.registrarObservador(this);
					campos.add(campo);
				}
			}
	}
	
	// Associação das casa vizinhas do tabuleiro do campo minado
	
	private void associarVizinhos() {
		for(Campo c1: campos) {
			for(Campo c2: campos) {
				c1.adicionarVizinho(c2);
			}
		}
	}
	// Mineirando a quantidade de minas que ficaram armadas no campo
	
	private void sortearMinas() {
		long minasArmadas = 0;
		Predicate<Campo> minado = c -> c.isMinado();
		
	do {
	
	// Sempre que entrar no laçado, saberá a quantidade de minas armadas
		int aleatorio = (int) (Math.random() * campos.size());
		campos.get(aleatorio).minar();
		minasArmadas = campos.stream().filter(minado).count();	
	} while(minasArmadas < minas);
  }
	// Função para verificar se o objetivo foi  alcançado(Se ganhou)

	public boolean objetivoAlcancado() {
		return campos.stream().allMatch(c -> c.objetivoAlcancado());
	}
	// Função de reiniciar o jogo
	
	public void reiniciar() {
		campos.stream().forEach(c -> c.reiniciar());
		sortearMinas();
	}
	
	
	
	public int getLinhas() {
		return linhas;
	}

	public int getColunas() {
		return colunas;
	}

	@Override
	public void eventoOcorreu(Campo campo, CampoEvento evento) {
		if(evento == CampoEvento.EXPLODIR) {
			System.out.println("Perdeu... :(");
			mostrarMinas();
			notificarObservadores(false);
		} else if(objetivoAlcancado()) {
			notificarObservadores(true);
		}
	}
	
	private void mostrarMinas() {
		campos.stream() 
		.filter(c -> c.isMinado())
		.filter(c -> !c.isMinado())
		.forEach(c -> c.setAberto(true));

	}
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

