package logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import gui.InterfazAhorcado;

/**
 * Clase de lógica para calcular los números que son diez menos entre un conjunto de números.
 */
public class NumerosDiezMenos {
    private int numeroBase;          
    private int numeroDiezMenos;     
    private String combinacion; 
    private int numeroAhorcado;
    
    // 👉 referencia al objeto InterfazAhorcado
    private InterfazAhorcado interfaz;
    
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
        return "No. diez menos: " + numeroDiezMenos +
               "      Referencia: " + numeroBase +
               "      "  + combinacion + 
               "\n";
    }

    // 🔹 Aquí va el método que me preguntabas
    public static Set<NumerosDiezMenos> obtenerMenosDiez(Set<Integer> numerosSeleccionados, InterfazAhorcado interfaz, int numeroAhorcado) {
        Set<NumerosDiezMenos> resultado = new HashSet<>();

        for (Integer num : numerosSeleccionados) {
            if (numerosSeleccionados.contains(num - 10)) {

                List<String> combinacionesCorrectas = new ArrayList<>();
                List<Integer> numerosAhorcado = new ArrayList<>();

                for (String c : interfaz.getTodasLasCombinaciones()) {
                    if (c.contains(String.valueOf(num))) {
                        combinacionesCorrectas.add(c);

                        String[] partes = c.split(" - ");
                        if (partes.length == 2) {
                            try {
                                int n1 = Integer.parseInt(partes[0].trim());
                                int n2 = Integer.parseInt(partes[1].trim());
                                int na = (n1 == num) ? n2 - 10 : n1 - 10;
                                numerosAhorcado.add(na);
                            } catch (NumberFormatException e) {
                                // No agregamos nada si no se puede calcular
                            }
                        }
                    }
                }

                // Concatenar todas las combinaciones con su número ahorcado
                StringBuilder combinacionConcatenada = new StringBuilder();
                for (int i = 0; i < combinacionesCorrectas.size(); i++) {
                    if (i > 0) combinacionConcatenada.append("\n                                                                       ");
                    combinacionConcatenada.append(combinacionesCorrectas.get(i));
                    if (i < numerosAhorcado.size()) { // Solo si existe número ahorcado calculado
                        combinacionConcatenada.append("; Número ahorcado: ").append(numerosAhorcado.get(i));
                    }
                }

                // Si no hay combinaciones, dejamos el string vacío
                NumerosDiezMenos ndm = new NumerosDiezMenos(num, combinacionConcatenada.toString(), interfaz);
                resultado.add(ndm);
            }
        }

        return resultado;
    }
    
    // 👉 Método que llama registrarCombinacion en InterfazAhorcado
    public void guardarCombinacion() {
        if (interfaz != null) {
            interfaz.registrarCombinacion(combinacion);
        }
    }
    
    
    //Evita que salga duplicado el resultado.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumerosDiezMenos)) return false;
        NumerosDiezMenos otro = (NumerosDiezMenos) o;
        return numeroBase == otro.numeroBase &&
               numeroDiezMenos == otro.numeroDiezMenos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroBase, numeroDiezMenos);
    }
}