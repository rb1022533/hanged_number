package logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import gui.InterfazAhorcado;

/**
 * Clase de lógica para calcular los números que son diez menos entre un
 * conjunto de números.
 */
public class NumerosDiezMenos {
	private int numeroBase;
	private int numeroDiezMenos;
	private String combinacion;
	private int numeroAhorcado;

	// 👉 referencia al objeto InterfazAhorcado
	private InterfazAhorcado interfaz;

	// ✅ Constructor especial para casos 1-91, 2-92, ..., 10-100
	public NumerosDiezMenos(int numeroDiezMenos, int numeroBase, String combinacion, InterfazAhorcado interfaz) {
		this.numeroDiezMenos = numeroDiezMenos; // NO se calcula
		this.numeroBase = numeroBase; // referencia real
		this.combinacion = combinacion;
		this.interfaz = interfaz;
	}

	public NumerosDiezMenos(int numeroBase, String combinacion, InterfazAhorcado interfaz) {
		this.numeroBase = numeroBase;
		this.numeroDiezMenos = numeroBase - 10;
		this.combinacion = combinacion;
		this.interfaz = interfaz;
	}

	public int getNumeroAhorcado() {
		return numeroAhorcado;
	}

	public int getNumeroBase() {
		return numeroBase;
	}

	public int getNumeroDiezMenos() {
		return numeroDiezMenos;
	}

	public String getCombinacion() {
		return combinacion;
	}

	@Override
	public String toString() {
		String resultado = "No. diez menos: " + numeroDiezMenos + "      Referencia: " + numeroBase;

		if (combinacion != null && !combinacion.isEmpty()) {
			resultado += "      " + combinacion;
		}

		return resultado + "\n";
	}

	// 🔹 Aquí va el método que me preguntabas
	public static Set<NumerosDiezMenos> obtenerMenosDiez(Set<Integer> numerosSeleccionados, InterfazAhorcado interfaz,
			int numeroAhorcado) {

		Set<NumerosDiezMenos> resultado = new HashSet<>();

		for (Integer num : numerosSeleccionados) {

			// En tu diseño: "num" aquí es quien será la referencia/base cuando haya num-10
			// seleccionado
			// y para el caso especial 1..10 también num actúa como referencia.
			// Por tanto usamos "referencia = num" en ambos casos según corresponda a la
			// creación del ND.
			// CASO ESPECIAL: 1..10 con su pareja en +90
			if (num >= 1 && num <= 10 && numerosSeleccionados.contains(num + 90)) {
				int referencia = num; // p.ej. 6 en tu ejemplo no es 60 (referencia = 6)
				int diezMenos = num + 90; // p.ej. 96

				String combinacionConcatenada = buscarYConstruirCombinacionesParaReferencia(referencia, interfaz);

				NumerosDiezMenos ndm = new NumerosDiezMenos(diezMenos, referencia, combinacionConcatenada, interfaz);
				resultado.add(ndm);
				continue;
			}

			// CASO NORMAL: existe num - 10 (ej. si num = 60 y 50 está seleccionado ->
			// referencia = 60)
			if (numerosSeleccionados.contains(num - 10)) {
				int referencia = num; // referencia real (ej. 60)
				// numeroDiezMenos será num - 10 (cuando se construya el ND con constructor
				// normal)
				String combinacionConcatenada = buscarYConstruirCombinacionesParaReferencia(referencia, interfaz);

				NumerosDiezMenos ndm = new NumerosDiezMenos(num, combinacionConcatenada, interfaz);
				resultado.add(ndm);
			}
		}

		return resultado;
	}

	/**
	 * Método auxiliar robusto para buscar en todasLasCombinaciones las líneas que
	 * correspondan exactamente a la 'referencia' (comparación por enteros) y
	 * devolver el String concatenado listo.
	 */
	private static String buscarYConstruirCombinacionesParaReferencia(int referencia, InterfazAhorcado interfaz) {

		Map<String, Integer> combinaciones = new LinkedHashMap<>();

		for (String c : interfaz.getTodasLasCombinaciones()) {
			if (c == null)
				continue;

			String[] porPuntoYComa = c.split(";");
			String parteCombo = porPuntoYComa[0].trim();

			String lower = parteCombo.toLowerCase();
			if (lower.startsWith("combinación:")) {
				parteCombo = parteCombo.substring("combinación:".length()).trim();
			} else if (lower.startsWith("combinacion:")) {
				parteCombo = parteCombo.substring("combinacion:".length()).trim();
			}

			String[] partes = parteCombo.split(" - ");
			if (partes.length != 2)
				continue;

			try {
				int n1 = Integer.parseInt(partes[0].trim());
				int n2 = Integer.parseInt(partes[1].trim());

				if (n1 == referencia || n2 == referencia) {
					String comboLimpia = "Combinación: " + n1 + " - " + n2;
					Integer ahorcadoReal = interfaz.getAhorcadoDeCombinacion(n1, n2);

// ✅ la relación queda intacta
					combinaciones.put(comboLimpia, ahorcadoReal);
				}

			} catch (NumberFormatException e) {
// ignorar
			}
		}

		if (combinaciones.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder();
		int i = 0;

		for (Map.Entry<String, Integer> entry : combinaciones.entrySet()) {
			if (i > 0)
				sb.append("\n                                                                       ");

			sb.append(entry.getKey()).append("; Número ahorcado: ").append(entry.getValue());

			i++;
		}

		return sb.toString();
	}

	// 👉 Método que llama registrarCombinacion en InterfazAhorcado
	public void guardarCombinacion() {
		if (interfaz != null) {
			interfaz.registrarCombinacion(combinacion);
		}
	}

	// Evita que salga duplicado el resultado.
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof NumerosDiezMenos))
			return false;
		NumerosDiezMenos otro = (NumerosDiezMenos) o;
		return numeroBase == otro.numeroBase && numeroDiezMenos == otro.numeroDiezMenos;
	}

	@Override
	public int hashCode() {
		return Objects.hash(numeroBase, numeroDiezMenos);
	}
}