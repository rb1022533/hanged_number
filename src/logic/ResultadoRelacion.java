package logic;

public class ResultadoRelacion {

	private final int numeroBase;
	private final String resultado;

	public ResultadoRelacion(int numeroBase, String resultado) {
		this.numeroBase = numeroBase;
		this.resultado = resultado;
	}

	public int getNumeroBase() {
		return numeroBase;
	}

	public String getResultado() {
		return resultado;
	}

}