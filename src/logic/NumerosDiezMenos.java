package logic;

import java.util.HashSet;
import java.util.Set;

/**
 * Clase de l�gica para calcular los n�meros que son diez menos entre un conjunto de n�meros.
 */
public class NumerosDiezMenos {

    /**
     * Calcula los n�meros que son 10 menos que cualquier otro n�mero en el conjunto.
     * 
     * @param numerosSeleccionados conjunto de n�meros seleccionados
     * @return conjunto de n�meros que cumplen la condici�n (sin duplicados)
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