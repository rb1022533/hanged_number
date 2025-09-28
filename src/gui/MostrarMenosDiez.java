package gui;

import logic.NumerosDiezMenos;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Set;

public class MostrarMenosDiez extends JFrame {

    private final Color COLOR_PRIMARIO = new Color(37, 99, 235); 
    private final Color COLOR_ACENTO = new Color(30, 58, 138);
    private final Color COLOR_FONDO = new Color(243, 244, 246); 
    private final Color COLOR_TEXTO = new Color(17, 24, 39);    
    private final Color COLOR_TEXTAREAS = new Color(255, 255, 255); 
    
    public MostrarMenosDiez(Set<Integer> numerosSeleccionados) {
        this(numerosSeleccionados, null, 0); // delega al constructor principal
    }

    // Constructor
    public MostrarMenosDiez(Set<Integer> numerosSeleccionados, InterfazAhorcado interfaz, int numeroAhorcado) {
        URL iconUrl = getClass().getResource("favicon.png");
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }

        setTitle("Números 10-");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        // Calcula los números -10 con combinación y número ahorcado
        Set<NumerosDiezMenos> menosDiez = NumerosDiezMenos.obtenerMenosDiez(numerosSeleccionados, interfaz, numeroAhorcado);

        // Área de texto
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 16));
        area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(COLOR_TEXTAREAS);
        area.setForeground(COLOR_TEXTO);

      //PROBAR SCROLL
        JScrollPane scrollPaneArea = new JScrollPane(area);
        scrollPaneArea.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        scrollPaneArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scrollPaneArea.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE)); // ancho vertical
        scrollPaneArea.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, 8)); // alto horizontal

        // Aplicar el scroll moderno
        scrollPaneArea.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPaneArea.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        if (menosDiez.isEmpty()) {
            area.setText("No hay números que sean diez menos entre los seleccionados.");
        } else {
            StringBuilder sb = new StringBuilder();
            menosDiez.stream()
                    .sorted((a, b) -> Integer.compare(a.getNumeroDiezMenos(), b.getNumeroDiezMenos()))
                    .forEach(num -> sb.append(num.toString()).append("\n"));
            area.setText(sb.toString());
        }

        add(scrollPaneArea, BorderLayout.CENTER);

        // Botón Cerrar
        JButton cerrar = new JButton("Cerrar");
        cerrar.setFont(new Font("Arial", Font.BOLD, 14));
        cerrar.setBackground(COLOR_PRIMARIO);
        cerrar.setForeground(COLOR_TEXTAREAS);
        cerrar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cerrar.addActionListener(e -> dispose());

        cerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { cerrar.setBackground(COLOR_ACENTO); }
            public void mouseExited(java.awt.event.MouseEvent evt) { cerrar.setBackground(COLOR_PRIMARIO); }
        });

        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(COLOR_FONDO);
        panelBoton.add(cerrar);
        add(panelBoton, BorderLayout.SOUTH);
    }
}