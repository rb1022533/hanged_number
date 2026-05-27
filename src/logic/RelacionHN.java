package logic;

import java.util.*;

public class RelacionHN {

	private static final Map<Integer, int[]> TABLA = new HashMap<>();

	static {

		TABLA.put(1, new int[] { 40, 38 });
		TABLA.put(2, new int[] { 41, 37 });
		TABLA.put(3, new int[] { 42, 36 });
		TABLA.put(4, new int[] { 43, 35 });
		TABLA.put(5, new int[] { 44, 34 });
		TABLA.put(6, new int[] { 45, 33 });
		TABLA.put(7, new int[] { 46, 32 });
		TABLA.put(8, new int[] { 47, 31 });
		TABLA.put(9, new int[] { 48, 30 });
		TABLA.put(10, new int[] { 49, 29 });
		TABLA.put(11, new int[] { 50, 28 });
		TABLA.put(12, new int[] { 51, 27 });
		TABLA.put(13, new int[] { 52, 26 });
		TABLA.put(14, new int[] { 53, 25 });
		TABLA.put(15, new int[] { 54, 24 });
		TABLA.put(16, new int[] { 55, 23 });
		TABLA.put(17, new int[] { 56, 22 });
		TABLA.put(18, new int[] { 57, 21 });
		TABLA.put(19, new int[] { 58, 20 });
		TABLA.put(20, new int[] { 59, 19 });
		TABLA.put(21, new int[] { 60, 18 });
		TABLA.put(22, new int[] { 61, 17 });
		TABLA.put(23, new int[] { 62, 16 });
		TABLA.put(24, new int[] { 63, 15 });
		TABLA.put(25, new int[] { 64, 14 });
		TABLA.put(26, new int[] { 65, 13 });
		TABLA.put(27, new int[] { 66, 12 });
		TABLA.put(28, new int[] { 67, 11 });
		TABLA.put(29, new int[] { 68, 10 });
		TABLA.put(30, new int[] { 69, 9 });
		TABLA.put(31, new int[] { 70, 8 });
		TABLA.put(32, new int[] { 71, 7 });
		TABLA.put(33, new int[] { 72, 6 });
		TABLA.put(34, new int[] { 73, 5 });
		TABLA.put(35, new int[] { 74, 4 });
		TABLA.put(36, new int[] { 75, 3 });
		TABLA.put(37, new int[] { 76, 2 });
		TABLA.put(38, new int[] { 77, 1 });
		TABLA.put(39, new int[] { 78, 100 });
		TABLA.put(40, new int[] { 79, 1 });
		TABLA.put(41, new int[] { 80, 2 });
		TABLA.put(42, new int[] { 81, 3 });
		TABLA.put(43, new int[] { 82, 4 });
		TABLA.put(44, new int[] { 83, 5 });
		TABLA.put(45, new int[] { 84, 6 });
		TABLA.put(46, new int[] { 85, 7 });
		TABLA.put(47, new int[] { 86, 8 });
		TABLA.put(48, new int[] { 87, 9 });
		TABLA.put(49, new int[] { 88, 10 });
		TABLA.put(50, new int[] { 11, 89 });
		TABLA.put(51, new int[] { 12, 90 });
		TABLA.put(52, new int[] { 13, 91 });
		TABLA.put(53, new int[] { 14, 92 });
		TABLA.put(54, new int[] { 15, 93 });
		TABLA.put(55, new int[] { 16, 94 });
		TABLA.put(56, new int[] { 17, 95 });
		TABLA.put(57, new int[] { 18, 96 });
		TABLA.put(58, new int[] { 19, 97 });
		TABLA.put(59, new int[] { 20, 98 });
		TABLA.put(60, new int[] { 21, 99 });
		TABLA.put(61, new int[] { 22, 100 });
		TABLA.put(62, new int[] { 23, 1 });
		TABLA.put(63, new int[] { 24, 2 });
		TABLA.put(64, new int[] { 25, 3 });
		TABLA.put(65, new int[] { 26, 4 });
		TABLA.put(66, new int[] { 27, 5 });
		TABLA.put(67, new int[] { 28, 6 });
		TABLA.put(68, new int[] { 29, 7 });
		TABLA.put(69, new int[] { 30, 8 });
		TABLA.put(70, new int[] { 31, 9 });
		TABLA.put(71, new int[] { 32, 10 });
		TABLA.put(72, new int[] { 33, 11 });
		TABLA.put(73, new int[] { 34, 12 });
		TABLA.put(74, new int[] { 35, 13 });
		TABLA.put(75, new int[] { 36, 14 });
		TABLA.put(76, new int[] { 37, 15 });
		TABLA.put(77, new int[] { 38, 16 });
		TABLA.put(78, new int[] { 39, 17 });
		TABLA.put(79, new int[] { 40, 18 });
		TABLA.put(80, new int[] { 41, 19 });
		TABLA.put(81, new int[] { 42, 20 });
		TABLA.put(82, new int[] { 43, 21 });
		TABLA.put(83, new int[] { 44, 22 });
		TABLA.put(84, new int[] { 45, 23 });
		TABLA.put(85, new int[] { 46, 24 });
		TABLA.put(86, new int[] { 47, 25 });
		TABLA.put(87, new int[] { 48, 26 });
		TABLA.put(88, new int[] { 49, 27 });
		TABLA.put(89, new int[] { 50, 28 });
		TABLA.put(90, new int[] { 51, 29 });
		TABLA.put(91, new int[] { 52, 30 });
		TABLA.put(92, new int[] { 53, 31 });
		TABLA.put(93, new int[] { 54, 32 });
		TABLA.put(94, new int[] { 55, 33 });
		TABLA.put(95, new int[] { 56, 34 });
		TABLA.put(96, new int[] { 57, 35 });
		TABLA.put(97, new int[] { 58, 36 });
		TABLA.put(98, new int[] { 59, 37 });
		TABLA.put(99, new int[] { 60, 38 });
		TABLA.put(100, new int[] { 61, 39 });

	}

	public static String generarResultados(Set<Integer> seleccionados) {

		StringBuilder sb = new StringBuilder();

		List<Integer> ordenados = new ArrayList<>(seleccionados);
		Collections.sort(ordenados);

		Integer ultimoBase = null;

		for (Integer numero : ordenados) {

			if (!TABLA.containsKey(numero))
				continue;

			int[] valores = TABLA.get(numero);

			ultimoBase = evaluarValor(numero, valores[0], seleccionados, sb, ultimoBase);

			ultimoBase = evaluarValor(numero, valores[1], seleccionados, sb, ultimoBase);
		}

		return sb.toString();
	}

	private static Integer evaluarValor(int base, int valor, Set<Integer> seleccionados, StringBuilder sb,
			Integer ultimoBase) {

		boolean menos1 = seleccionados.contains(valor - 1);
		boolean exacto = seleccionados.contains(valor);
		boolean mas1 = seleccionados.contains(valor + 1);

		if (!menos1 && !exacto && !mas1)
			return ultimoBase;

		// CONTROL DE ESPACIADO
		if (sb.length() > 0) {

			if (ultimoBase != null && ultimoBase == base) {
				sb.append("\n");
			} else {
				sb.append("\n\n");
			}
		}

		sb.append(base).append(" = ");

		if (menos1)
			sb.append("(").append(valor - 1).append(") ");

		sb.append(valor);

		if (mas1)
			sb.append(" (").append(valor + 1).append(")");

		return base;
	}

}