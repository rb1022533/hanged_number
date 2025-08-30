package gui;

import javax.swing.SwingUtilities;

import java.util.List;
import java.util.Scanner;

public class Principal {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> new InterfazBienvenida().setVisible(true));
		InterfazAhorcado ia = new InterfazAhorcado();
		/*
		 * Scanner lectura = new Scanner(System.in); int[][] pares = { { 1, 81 }, //
		 * válido, deben devolver 91 { 1, 41 }, // inválido, deben devolver null { 2, 82
		 * }, // válido, deben devolver 92 { 13, 93 }, // válido, deben devolver 3 { 24,
		 * 84 }, // válido, deben devolver 4 { 75, 15 }, // válido, deben devolver 95 {
		 * 70, 30 }, // válido, deben devolver 50 { 60, 20 }, // inválido, más de 5
		 * entre ellos { 5, 95 }, // válido, deben devolver 1 { 10, 90 } // inválido si
		 * hay más de 5 entre ellos };
		 * 
		 * for (int[] par : pares) { int num1 = par[0]; int num2 = par[1];
		 * 
		 * Integer resultado = ia.encontrarAhorcadosFilaExtendidaParteCircular(num1,
		 * num2);
		 * 
		 * System.out.println("Entre " + num1 + " y " + num2 + " → Ahorcados: " +
		 * resultado);
		 * 
		 * }
		 */

	}
	/*
	 * System.out.println("Ingrese el primer número: "); int num1 =
	 * lectura.nextInt(); System.out.println("Ingrese el segundo número: "); int
	 * num2 = lectura.nextInt();
	 * 
	 * System.out.println("Ingrese la columna: "); int columna = lectura.nextInt();
	 * 
	 */
	/*
	 * List<int[]> x = ia.encontrarCoordenadasEnTableroExtendido(num1); for (int[]
	 * pos1 : x) { System.out.println("[" + pos1[0] + "];[" + pos1[1] + "]"); }
	 */
	// testDiagonalDescendenteExtendida();
	/*
	 * int[][] pruebas = { {1, 81}, {2, 82}, {13, 93}, // Circularidad {24, 84}, //
	 * Mismo rango fila {75, 15}, // Mismo número {70, 30} // Diferente fila };
	 * 
	 * for (int[] par : pruebas) { int num1 = par[0]; int num2 = par[1]; int
	 * resultado = ia.contarNumerosEntreFilaExtendida(num1, num2);
	 * System.out.println("Entre " + num1 + " y " + num2 + " hay " + resultado +
	 * " números (fila extendida)"); }}
	 */

	/*
	 * public static void testDiagonalDescendenteExtendida() { InterfazAhorcado ia =
	 * new InterfazAhorcado(); int[][] combinacionesTrue = { { 96, 18 }, { 22, 100
	 * }, { 87, 9 }, { 94, 16 }, { 89, 11 }, { 90, 12 } };
	 * 
	 * int[][] combinacionesFalse = { { 34, 88 }, { 24, 78 }, { 14, 68 }, { 4, 58 },
	 * { 2, 56 }, { 12, 66 }, { 22, 76 } };
	 * 
	 * // System.out.println("### Combinaciones que deben ser TRUE:"); for (int[]
	 * par : combinacionesTrue) { boolean resultado =
	 * ia.diagonalDescendenteExtendida(par[0], par[1]); //
	 * System.out.printf("  %d - %d => %s\n", par[0], par[1], resultado ? "✅ true" :
	 * "❌ false"); }
	 * 
	 * // System.out.println("\n### Combinaciones que deben ser FALSE:"); for (int[]
	 * par : combinacionesFalse) { boolean resultado =
	 * ia.diagonalDescendenteExtendida(par[0], par[1]); //
	 * System.out.printf("  %d - %d => %s\n", par[0], par[1], !resultado ? "✅ false"
	 * : "❌ true"); } }
	 */
};