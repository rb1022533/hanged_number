package gui;


import logic.NumerosDiezMenos;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class MostrarMenosDiez extends JFrame {

    // Colores y estilo de la GUI principal
	private final Color COLOR_PRIMARIO = new Color(37, 99, 235); //  Azul  
	private final Color COLOR_ACENTO = new Color(30, 58, 138); //  Hover
	private final Color COLOR_FONDO = new Color(243, 244, 246); // Fondo gris claro
	private final Color COLOR_TEXTO = new Color(17, 24, 39);    // Texto normal
	private final Color COLOR_TEXTAREAS = new Color(255, 255, 255); // Color blanco
	private final Color COLOR_BORDE = new Color(200, 200, 200); // Gris claro para bordes

    public MostrarMenosDiez(Set<Integer> numerosSeleccionados) {
    	URL iconUrl = getClass().getResource("favicon.png");
    	System.out.println(iconUrl != null ? "Cargado: " + iconUrl : "❌ No encontrado");

    	if (iconUrl != null) {
    		ImageIcon icono = new ImageIcon(iconUrl);
    		setIconImage(icono.getImage());
    	}
        setTitle("Números 10-");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        // Calcula los números -10
        Set<NumerosDiezMenos> menosDiez = NumerosDiezMenos.obtenerMenosDiez(numerosSeleccionados);
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
        
        scrollPaneArea.getVerticalScrollBar().setPreferredSize(new Dimension(4, Integer.MAX_VALUE)); // ancho vertical
        scrollPaneArea.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, 4)); // alto horizontal

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

        // Botón Cerrar con estilo de la GUI principal
        JButton cerrar = new JButton("Cerrar");
        cerrar.setFont(new Font("Arial", Font.BOLD, 14));
        cerrar.setBackground(COLOR_PRIMARIO);
        cerrar.setForeground(COLOR_TEXTAREAS);
        cerrar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cerrar.addActionListener(e -> dispose());

        // Hover igual que los botones principales
        cerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cerrar.setBackground(COLOR_ACENTO);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cerrar.setBackground(COLOR_PRIMARIO);
            }
        });

        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(COLOR_FONDO);
        panelBoton.add(cerrar);
        add(panelBoton, BorderLayout.SOUTH);
    }
    
    

//    // Método de ejemplo para probar la ventana
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            Set<Integer> ejemplo = new HashSet<>(Set.of(5, 15, 25, 35));
//            new MostrarMenosDiez(ejemplo).setVisible(true);
//        });
//    }
}
