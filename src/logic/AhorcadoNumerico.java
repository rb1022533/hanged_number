package logic;

public class AhorcadoNumerico {
	private final int FILAS = 10;
	private final int COLUMNAS = 10;
	private final int[][] matriz;

	public AhorcadoNumerico() {
		this.matriz = new int[FILAS][COLUMNAS];
		inicializarMatriz();
	}

	private void inicializarMatriz() {
		int numeroActual = 1;
		for (int columna = 0; columna < COLUMNAS; columna++) {
			for (int fila = 0; fila < FILAS; fila++) {
				matriz[fila][columna] = numeroActual++;
			}
		}
	}

	public void mostrarMatriz() {
		for (int i = 0; i < FILAS; i++) {
			for (int j = 0; j < COLUMNAS; j++) {
				System.out.printf("%4d", matriz[i][j]);
			}
			System.out.println();
		}
	}

	public void encontrarNumerosAhorcados(int num1, int num2) {
	    // Asegurar que num1 sea menor que num2
	    if (num1 > num2) {
	        int temp = num1;
	        num1 = num2;
	        num2 = temp;
	    }
	    
	    System.out.println("Números seleccionados: " + num1 + " y " + num2);
	    System.out.println("Números ahorcados:");
	    
	    // Encontrar el número del medio
	    int numeroMedio = (num1 + num2) / 2;
	    
	    // Buscar el número del medio en la matriz
	    boolean encontrado = false;
	    for (int i = 0; i < FILAS && !encontrado; i++) {
	        for (int j = 0; j < COLUMNAS && !encontrado; j++) {
	            int valorActual = matriz[i][j];
	            if (valorActual == numeroMedio) {
	                System.out.printf("[%d,%d]: %d%n", i, j, valorActual);
	                encontrado = true;
	            }
	        }
	    }
	    
	    // Si no se encontró el número medio, buscar el siguiente número en el patrón circular
	    if (!encontrado) {
	        // Calcular el siguiente número en el patrón circular

	        int siguienteNumero = numeroMedio + 10;
	        if (siguienteNumero > 100) {
	            siguienteNumero = siguienteNumero % 100;
	        }
	        
	        // Buscar el siguiente número en la matriz
	        encontrado = false;
	        for (int i = 0; i < FILAS && !encontrado; i++) {
	            for (int j = 0; j < COLUMNAS && !encontrado; j++) {
	                int valorActual = matriz[i][j];
	                if (valorActual == siguienteNumero) {
	                    System.out.printf("[%d,%d]: %d%n", i, j, valorActual);
	                    encontrado = true;
	                }
	            }
	        }
	        
	        if (!encontrado) {
	            System.out.println("No se encontró ningún número ahorcado.");
	        }
	    } else {
	        System.out.println("No se encontró ningún número ahorcado adicional.");
	    }
	}

	public int[][] getMatriz() {
		return matriz;
	}
}
