package gui;

import logic.NumerosDiezMenos;
import javax.swing.*;

import gui.AlertasUI;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;



public class MostrarMenosDiez extends JFrame {
	

    private final Color COLOR_PRIMARIO = new Color(37, 99, 235); 
    private final Color COLOR_ACENTO = new Color(30, 58, 138);
    private final Color COLOR_FONDO = new Color(243, 244, 246); 
    private final Color COLOR_TEXTO = new Color(17, 24, 39);    
    private final Color COLOR_TEXTAREAS = new Color(255, 255, 255); 
    
    private MouseAdapter cursorMano;
    
    public MostrarMenosDiez(Set<Integer> numerosSeleccionados) {
        this(numerosSeleccionados, null, 0); // delega al constructor principal
    }

    // Constructor
    public MostrarMenosDiez(Set<Integer> numerosSeleccionados, InterfazAhorcado interfaz, int numeroAhorcado) {
        
    	// 🔹 Inicializar cursor de manito
    	cursorMano = new MouseAdapter() {
    	    @Override
    	    public void mouseEntered(MouseEvent e) {
    	        ((JComponent) e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    	    }

    	    @Override
    	    public void mouseExited(MouseEvent e) {
    	        ((JComponent) e.getSource()).setCursor(Cursor.getDefaultCursor());
    	    }
    	};
    	
    	URL iconUrl = getClass().getResource("favicon.png");
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }
        

        setTitle("Hanged Number");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
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
        
        add(scrollPaneArea, BorderLayout.CENTER);//CENTER (así estaba anteriormente)
        
        
     // Botón Cerrar
        JButton cerrar = new JButton("Cerrar");
        cerrar.setFont(new Font("Arial", Font.BOLD, 16));
        cerrar.setBackground(COLOR_PRIMARIO);
        cerrar.setForeground(COLOR_TEXTAREAS);
        cerrar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cerrar.addActionListener(e -> dispose());
        cerrar.addMouseListener(cursorMano);
        cerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { cerrar.setBackground(COLOR_ACENTO); }
            public void mouseExited(java.awt.event.MouseEvent evt) { cerrar.setBackground(COLOR_PRIMARIO); }
        });

     // 1️⃣ Cargar icono original (DECLARAR FINAL)
        final ImageIcon originalIcon = new ImageIcon(getClass().getResource("iconoExportarPdf.png"));

        // 2️⃣ Escalar icono tamaño normal
        final ImageIcon iconoNormal = new ImageIcon(originalIcon.getImage().getScaledInstance(54, 54, Image.SCALE_SMOOTH));

        // 3️⃣ Escalar icono tamaño “hover” (ligeramente más grande)
        final ImageIcon iconoHover = new ImageIcon(originalIcon.getImage().getScaledInstance(58, 58, Image.SCALE_SMOOTH));

        // 4️⃣ Crear botón con icono normal
        JButton btnExportarPDF = new JButton(iconoNormal);
        btnExportarPDF.setBorderPainted(false);
        btnExportarPDF.setContentAreaFilled(false);
        btnExportarPDF.setFocusPainted(false);
        btnExportarPDF.setOpaque(false);
        btnExportarPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportarPDF.setPreferredSize(new Dimension(58, 48));
        btnExportarPDF.setToolTipText("Exportar resultados a PDF");
        btnExportarPDF.addMouseListener(cursorMano);

        // 5️⃣ Hover: cambiar icono a más grande y volver al original
        btnExportarPDF.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnExportarPDF.setIcon(iconoHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnExportarPDF.setIcon(iconoNormal);
            }
        });

        // 6️⃣ Acción al hacer clic
        btnExportarPDF.addActionListener(e -> exportarResultadosPDF(area));
        
     // PANEL SUPERIOR (TÍTULO + BOTONES)
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(COLOR_FONDO);

        // ───────── TÍTULO (IZQUIERDA) ─────────
        JLabel lblSubtitleTabla = new JLabel("NÚMEROS 10 MENOS");
        lblSubtitleTabla.setFont(new Font("Arial", Font.BOLD, 18));
        lblSubtitleTabla.setForeground(COLOR_TEXTO);
        lblSubtitleTabla.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // margen izquierdo

        panelSuperior.add(lblSubtitleTabla, BorderLayout.WEST);

        // ───────── PANEL BOTONES (DERECHA) ─────────
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(COLOR_FONDO);

        // Botones (usar los ya creados con iconos y listeners)
        panelBotones.add(btnExportarPDF);
        panelBotones.add(cerrar);

        panelSuperior.add(panelBotones, BorderLayout.EAST);

        // Finalmente agregar al NORTH de la ventana
        add(panelSuperior, BorderLayout.NORTH);

    }
    private void exportarResultadosPDF(JTextArea area) {
        String texto = area.getText();
        if (texto == null || texto.trim().isEmpty()) {
        	AlertasUI.mostrarAlerta(this, "No hay resultados para exportar.");
            return;
        }

        FileDialog fd = new FileDialog(this, "Guardar resultados en PDF", FileDialog.SAVE);
        fd.setFile("Resultados_MenosDiez.pdf");
        fd.setVisible(true);

        String directory = fd.getDirectory();
        String filename = fd.getFile();
        if (directory == null || filename == null) return;

        File archivo = new File(directory, filename);
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(archivo.getAbsolutePath() + ".pdf");
        }

        String[] lineas = area.getText().split("\n");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            content.setFont(PDType1Font.HELVETICA, 11);

            float margin = 50;
            float leading = 14.5f;
            float yPosition = PDRectangle.LETTER.getHeight() - margin;

            for (String linea : lineas) {
                if (yPosition <= margin) {
                    content.close();
                    page = new PDPage(PDRectangle.LETTER);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    content.setFont(PDType1Font.HELVETICA, 11);
                    yPosition = PDRectangle.LETTER.getHeight() - margin;
                }
                content.beginText();
                content.newLineAtOffset(margin, yPosition);
                content.showText(linea);
                content.endText();
                yPosition -= leading;
            }

            content.close();
            document.save(archivo);

            AlertasUI.mostrarAlerta(this, "PDF exportado correctamente.");

        } catch (IOException ex) {
        	AlertasUI.mostrarAlerta(this, "Error al exportar el PDF:\n" + ex.getMessage());
        }
    }
}