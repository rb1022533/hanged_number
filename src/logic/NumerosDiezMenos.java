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
    
    public NumerosDiezMenos(int numeroBase, String combinacion) {
        this.numeroBase = numeroBase;
        this.numeroDiezMenos = numeroBase - 10;
        this.combinacion = combinacion;
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
               "      Combinación: " + combinacion;
    }

    // 🔹 Aquí va el método que me preguntabas
    public static Set<NumerosDiezMenos> obtenerMenosDiez(Set<Integer> numerosSeleccionados) {
    	
        Set<NumerosDiezMenos> resultado = new HashSet<>();
        
        for (Integer sel : numerosSeleccionados) {
            for (Integer otro : numerosSeleccionados) {
                if (!sel.equals(otro) && (sel - 10 == otro || sel + 10 == otro)) {
                    int menor = Math.min(sel, otro); // el "diez menos" real (ya seleccionado)
                    int mayor = Math.max(sel, otro); // el número de referencia
                    
                    // ⚠️ aquí deberías obtener la combinación real asociada a "mayor"
                    String combinacion = "(-)";
                    
                    resultado.add(new NumerosDiezMenos(mayor, combinacion));
                }
            }
        }

        return resultado;
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