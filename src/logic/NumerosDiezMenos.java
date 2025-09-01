package logic;

import java.util.HashSet;
import java.util.Set;

/**
 * Clase de lógica para calcular los números que son diez menos entre un conjunto de números.
 */
public class NumerosDiezMenos {

    /**
     * Calcula los números que son 10 menos que cualquier otro número en el conjunto.
     * 
     * @param numerosSeleccionados conjunto de números seleccionados
     * @return conjunto de números que cumplen la condición (sin duplicados)
     */
    public static Set<Integer> obtenerMenosDiez(Set<Integer> numerosSeleccionados) {
        Set<Integer> menosDiez = new HashSet<>();

        for (Integer sel : numerosSeleccionados) {
            for (Integer otro : numerosSeleccionados) {
                if (!sel.equals(otro) && (sel - 10 == otro || sel + 10 == otro)) {
                    menosDiez.add(Math.min(sel, otro)); // agrega el menor de ambos
                }
            }
        }

        return menosDiez;
    }
}