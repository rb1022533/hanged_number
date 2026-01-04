package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;

import logic.NumerosDiezMenos;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import gui.AlertasUI;

public class InterfazAhorcado extends JFrame {
	private JTable tablaNumeros;
	private int hoveredRow = -1;
	private int hoveredCol = -1;
	private DefaultTableModel modeloTabla;
	private boolean[][] seleccionados = new boolean[10][10]; // Esto crea la matriz 10x10;
	private JTextArea areaResultados;
	private JTextArea areaHistorial;
	private JButton botonReiniciar;
	private JButton botonVerMenosDiez;
	private JButton btnExportarPDF;
	private JButton botonCerrarDialogo;
	private MouseAdapter cursorMano;
	private Set<Integer> numerosAhorcados = new HashSet<>();
	private Set<Integer> numerosAhorcadosAdicionales = new HashSet<>();
	private TablaEventHandler handler;
	private Set<String> combinacionesProcesadas = new HashSet<>();

	// Lista para almacenar todas las combinaciones detectadas
	private List<String> todasLasCombinaciones = new ArrayList<>();

	// =====================
	// MAPA combinacion -> ahorcado
	// =====================
	private final Map<String, Integer> combinacionYAhorcado = new HashMap<>();

	private String normalizar(int a, int b) {
		int menor = Math.min(a, b);
		int mayor = Math.max(a, b);
		return menor + " - " + mayor;
	}

	private void exportarResultadosPDF() {

		String texto = areaHistorial.getText();

		// Validación: no hay datos reales para exportar
		if (texto == null || texto.trim().isEmpty()) {

			if (texto == null || texto.trim().isEmpty()) {
				AlertasUI.mostrarAlerta(this, "No hay resultados para exportar.");
				return;
			}
		}

		FileDialog fd = new FileDialog(this, "Guardar resultados en PDF", FileDialog.SAVE);
		fd.setFile("Resultados_Ahorcado.pdf");
		fd.setVisible(true);

		String directory = fd.getDirectory();
		String filename = fd.getFile();
		if (directory == null || filename == null)
			return;

		File archivo = new File(directory, filename);
		if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
			archivo = new File(archivo.getAbsolutePath() + ".pdf");
		}

		String[] lineas = obtenerResultadosParaExportar();
		if (lineas.length == 0 || (lineas.length == 1 && lineas[0].trim().isEmpty())) {
			JOptionPane.showMessageDialog(this, "No hay resultados para exportar.", "Aviso",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		try (PDDocument document = new PDDocument()) {

			PDPage page = new PDPage(PDRectangle.LETTER);
			document.addPage(page);

			PDPageContentStream content = new PDPageContentStream(document, page);
			content.setFont(PDType1Font.HELVETICA, 11);

			float margin = 50;
			float leading = 14.5f;
			int numColumnas = 2;

			float pageWidth = PDRectangle.LETTER.getWidth();
			float pageHeight = PDRectangle.LETTER.getHeight();

			float usableWidth = pageWidth - 2 * margin;
			float usableHeight = pageHeight - 2 * margin;
			float columnWidth = usableWidth / numColumnas;

			int currentColumn = 0;
			float yPosition = pageHeight - margin;

			for (String linea : lineas) {
				// Si llegamos al margen inferior, saltamos de columna
				if (yPosition <= margin) {
					currentColumn++;
					yPosition = pageHeight - margin;
					if (currentColumn >= numColumnas) {
						// todas las columnas llenas → nueva página
						content.close();
						page = new PDPage(PDRectangle.LETTER);
						document.addPage(page);
						content = new PDPageContentStream(document, page);
						content.setFont(PDType1Font.HELVETICA, 11);
						currentColumn = 0;
					}
				}

				float xPosition = margin + currentColumn * columnWidth;

				// 🔹 Escribimos usando coordenadas absolutas
				content.beginText();
				content.newLineAtOffset(xPosition, yPosition);
				content.showText(linea);
				content.endText();

				yPosition -= leading;
			}

			content.close();
			document.save(archivo);

			AlertasUI.mostrarAlerta(this, "PDF Exportado correctamente");

		} catch (IOException ex) {
			AlertasUI.mostrarAlerta(this, "Error al exportar el PDF:\n");
		}
	}

	private String[] obtenerResultadosParaExportar() {
		return areaHistorial.getText().split("\n");
	}

	public Integer getAhorcadoDeCombinacion(int a, int b) {
		return combinacionYAhorcado.get(normalizar(a, b));
	}

	// Para los estilos
	private final Color COLOR_PRIMARIO = new Color(37, 99, 235); // Azul
	private final Color COLOR_ACENTO = new Color(30, 58, 138); // Hover
	private final Color COLOR_FONDO = new Color(243, 244, 246); // Fondo gris claro
	private final Color COLOR_TEXTO = new Color(17, 24, 39); // Texto normal
	private final Color COLOR_TEXTAREAS = new Color(255, 255, 255); // Color blanco
	private final Color COLOR_BORDE = new Color(200, 200, 200); // Gris claro para bordes

	private static final int[][] TABLERO_EXTENDIDO = {
			{ 1, 11, 21, 31, 41, 51, 61, 71, 81, 91, 1, 11, 21, 31, 41, 51, 61, 71 },
			{ 2, 12, 22, 32, 42, 52, 62, 72, 82, 92, 2, 12, 22, 32, 42, 52, 62, 72 },
			{ 3, 13, 23, 33, 43, 53, 63, 73, 83, 93, 3, 13, 23, 33, 43, 53, 63, 73 },
			{ 4, 14, 24, 34, 44, 54, 64, 74, 84, 94, 4, 14, 24, 34, 44, 54, 64, 74 },
			{ 5, 15, 25, 35, 45, 55, 65, 75, 85, 95, 5, 15, 25, 35, 45, 55, 65, 75 },
			{ 6, 16, 26, 36, 46, 56, 66, 76, 86, 96, 6, 16, 26, 36, 46, 56, 66, 76 },
			{ 7, 17, 27, 37, 47, 57, 67, 77, 87, 97, 7, 17, 27, 37, 47, 57, 67, 77 },
			{ 8, 18, 28, 38, 48, 58, 68, 78, 88, 98, 8, 18, 28, 38, 48, 58, 68, 78 },
			{ 9, 19, 29, 39, 49, 59, 69, 79, 89, 99, 9, 19, 29, 39, 49, 59, 69, 79 },
			{ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 10, 20, 30, 40, 50, 60, 70, 80 },
			{ 11, 21, 31, 41, 51, 61, 71, 81, 91, 1, 11, 21, 31, 41, 51, 61, 71, 81 },
			{ 12, 22, 32, 42, 52, 62, 72, 82, 92, 2, 12, 22, 32, 42, 52, 62, 72, 82 },
			{ 13, 23, 33, 43, 53, 63, 73, 83, 93, 3, 13, 23, 33, 43, 53, 63, 73, 83 },
			{ 14, 24, 34, 44, 54, 64, 74, 84, 94, 4, 14, 24, 34, 44, 54, 64, 74, 84 },
			{ 15, 25, 35, 45, 55, 65, 75, 85, 95, 5, 15, 25, 35, 45, 55, 65, 75, 85 },
			{ 16, 26, 36, 46, 56, 66, 76, 86, 96, 6, 16, 26, 36, 46, 56, 66, 76, 86 },
			{ 17, 27, 37, 47, 57, 67, 77, 87, 97, 7, 17, 27, 37, 47, 57, 67, 77, 87 },
			{ 18, 28, 38, 48, 58, 68, 78, 88, 98, 8, 18, 28, 38, 48, 58, 68, 78, 88 } };

	public List<String> getTodasLasCombinaciones() {
		return new ArrayList<>(todasLasCombinaciones);
	}

//	

	public void limpiarCombinaciones() {
		todasLasCombinaciones.clear();
		combinacionYAhorcado.clear(); // 👈 ESTO ES CLAVE
	}

	public InterfazAhorcado() {
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
//		System.out.println(iconUrl != null ? "Cargado: " + iconUrl : "❌ No encontrado");

		if (iconUrl != null) {
			ImageIcon icono = new ImageIcon(iconUrl);
			setIconImage(icono.getImage());
		}
//		setIconImage(icono.getImage());\
		// Inicializar logicaJuego primero

		JLabel lblSubtitleTabla = new JLabel("SELECCIÓN NUMÉRICA");
		lblSubtitleTabla.setHorizontalAlignment(SwingConstants.LEFT);
		lblSubtitleTabla.setFont(new Font("Arial", Font.BOLD, 18));
		lblSubtitleTabla.setForeground(COLOR_TEXTO);
		lblSubtitleTabla.setBounds(10, 50, 360, 40);
		add(lblSubtitleTabla);

		JLabel lblSubtituloHistorial = new JLabel("RESULTADOS");
		lblSubtituloHistorial.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSubtituloHistorial.setFont(new Font("Arial", Font.BOLD, 18));
//		lblSubtituloHistorial.setForeground(COLOR_TEXTO);
		lblSubtituloHistorial.setBounds(0, 50, 600, 40);
		add(lblSubtituloHistorial);

//		JLabel lblSubtituloResultados = new JLabel("Resultados_Última Selección");
//		lblSubtituloResultados.setHorizontalAlignment(SwingConstants.LEFT);
//		lblSubtituloResultados.setVerticalAlignment(SwingConstants.EAST);
//		lblSubtituloResultados.setFont(new Font("Arial", Font.BOLD, 18));
//		lblSubtituloResultados.setForeground(COLOR_PRIMARIO);
//		lblSubtituloResultados.setBounds(10, 525, 350, 34);
//		add(lblSubtituloResultados);

		// Inicializar modelo y tabla
		modeloTabla = new DefaultTableModel(10, 10) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tablaNumeros = new JTable(modeloTabla);

		tablaNumeros.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int row = tablaNumeros.rowAtPoint(e.getPoint());
				int col = tablaNumeros.columnAtPoint(e.getPoint());

				if (row != hoveredRow || col != hoveredCol) {
					hoveredRow = row;
					hoveredCol = col;
					tablaNumeros.repaint();
				}
			}
		});

		tablaNumeros.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				"seleccionarCelda");

		tablaNumeros.getActionMap().put("seleccionarCelda", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int fila = tablaNumeros.getSelectedRow();
				int columna = tablaNumeros.getSelectedColumn();

				if (fila >= 0 && columna >= 0) {
					// Marcar la celda como seleccionada
					seleccionados[fila][columna] = true;

					// Forzar la actualización visual
					tablaNumeros.repaint();

					// Procesar combinaciones para esta selección
					List<Integer> numerosFila = getNumerosSeleccionadosEnFila(fila);
					List<Integer> numerosColumna = getNumerosSeleccionadosEnColumna(columna);
					List<Integer> numerosDiagonal = getNumerosSeleccionadosEnDiagonal();
					List<List<Integer>> numerosDiagonalExt = getNumerosSeleccionadosEnDiagonalExtendida();
					List<List<Integer>> columnasExt = getNumerosSeleccionadosEnColumnaExtendida();

					if (!numerosFila.isEmpty()) {
						procesarCombinacionesEnLista(numerosFila, "Fila " + (fila + 1));
					}
					if (!numerosColumna.isEmpty()) {
						procesarCombinacionesEnLista(numerosColumna, "Columna " + (columna + 1));
					}
					if (!numerosDiagonal.isEmpty()) {
						procesarCombinacionesEnLista(numerosDiagonal, "Diagonal ");
					}
					if (!numerosDiagonalExt.isEmpty()) {
						List<List<Integer>> diagonalesExtendidas = getNumerosSeleccionadosEnDiagonalExtendida();
						for (List<Integer> diagonal : diagonalesExtendidas) {
							procesarCombinacionesEnLista(diagonal, "Diagonal Extendida");
						}
					}
					if (!columnasExt.isEmpty()) {
						for (List<Integer> columnaExt : columnasExt) {
							procesarCombinacionesEnLista(columnaExt, "Columna Extendida");
						}
					}
				}
			}
		});

		// Inicializar array de selección
		seleccionados = new boolean[10][10];

		// Configurar tabla
		tablaNumeros.setRowHeight(45);
		tablaNumeros.setTableHeader(null);
		tablaNumeros.setBackground(COLOR_FONDO);
		tablaNumeros.setForeground(COLOR_TEXTO);
		tablaNumeros.setGridColor(COLOR_BORDE);

		// Configurar renderizador de celdas con efecto hover
		tablaNumeros.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			private Point lastMousePosition = null;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				// Estilo base
				setFont(new Font("Arial", Font.BOLD, 16));
				setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				setHorizontalAlignment(JLabel.CENTER);
				setVerticalAlignment(JLabel.CENTER);

				Color background = COLOR_FONDO;
				Color foreground = COLOR_TEXTO;

				// Hover
				Point mousePos = table.getMousePosition();
				if (mousePos != null) {
					int hoveredRow = table.rowAtPoint(mousePos);
					int hoveredCol = table.columnAtPoint(mousePos);
					if (hoveredRow == row && hoveredCol == column) {
						background = new Color(230, 230, 250);
					}
				}

				// Selección prioritaria
				if (seleccionados[row][column]) {
					background = COLOR_PRIMARIO;
					foreground = COLOR_FONDO;
				}
				// Números ahorcados
				else if (value instanceof Integer) {
					Integer numero = (Integer) value;
					if ((numerosAhorcados.contains(numero) || numerosAhorcadosAdicionales.contains(numero))) {
						foreground = Color.RED;
						// background = COLOR_FONDO; // opcional, o dejar transparente
					}
				}

				setBackground(background);
				setForeground(foreground);

				// Borde cuando tiene foco
				if (hasFocus) {
					setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
							BorderFactory.createEmptyBorder(5, 5, 5, 5)));
				}

				return this;
			}
		});

		tablaNumeros.setFocusTraversalKeysEnabled(true);
		tablaNumeros.setFocusable(true);

		// Inicializar handler y agregar listener
		handler = new TablaEventHandler(this);
		tablaNumeros.addMouseListener(handler);

		int[][] matriz = { { 1, 11, 21, 31, 41, 51, 61, 71, 81, 91 }, { 2, 12, 22, 32, 42, 52, 62, 72, 82, 92 },
				{ 3, 13, 23, 33, 43, 53, 63, 73, 83, 93 }, { 4, 14, 24, 34, 44, 54, 64, 74, 84, 94 },
				{ 5, 15, 25, 35, 45, 55, 65, 75, 85, 95 }, { 6, 16, 26, 36, 46, 56, 66, 76, 86, 96 },
				{ 7, 17, 27, 37, 47, 57, 67, 77, 87, 97 }, { 8, 18, 28, 38, 48, 58, 68, 78, 88, 98 },
				{ 9, 19, 29, 39, 49, 59, 69, 79, 89, 99 }, { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 } };
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				modeloTabla.setValueAt(matriz[i][j], i, j);
			}
		}
		
		int alturaTabla = 445; // por ejemplo, la altura que quieras para la tabla
		int numFilas = tablaNumeros.getRowCount();
		tablaNumeros.setRowHeight(alturaTabla / numFilas);
		
		int anchoTabla = 445;
		int numCols = tablaNumeros.getColumnCount();
		for (int i = 0; i < numCols; i++) {
		    tablaNumeros.getColumnModel().getColumn(i).setPreferredWidth(anchoTabla / numCols);
		}

		// Crear área de texto para resultados
		areaResultados = new JTextArea(5, 40);
		areaResultados.setEditable(false);
		areaResultados.setFont(new Font("Arial", Font.PLAIN, 12));
		areaResultados.setForeground(COLOR_TEXTO);
		areaResultados.setBackground(COLOR_FONDO);
		areaResultados
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_FONDO),
						BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		// Crear área de texto para historial
		areaHistorial = new JTextArea(5, 45);
		areaHistorial.setEditable(false);
		areaHistorial.setFont(new Font("Arial", Font.PLAIN, 14));
		areaHistorial.setForeground(COLOR_TEXTO);
		areaHistorial.setBackground(COLOR_TEXTAREAS);
		areaHistorial.setLineWrap(true);
		areaHistorial.setWrapStyleWord(true);
		areaHistorial.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
		
		// Crear panel central que contendrá la tabla y el área de resultados
		JPanel panelCentral = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Tabla en la parte superior
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1; // ocupa la mayor parte del espacio
		gbc.fill = GridBagConstraints.BOTH;
		panelCentral.add(tablaNumeros, gbc);

		// Área de resultados en la parte inferior
		gbc.gridy = 1;
		gbc.weighty = 0.3; // ocupa un 30% del espacio vertical
		panelCentral.add(areaResultados, gbc);

		// Agregar panel central al JFrame
		add(panelCentral, BorderLayout.CENTER);
//
//		// Configurar scroll pane para la tabla
//		JScrollPane scrollPaneTabla = new JScrollPane(tablaNumeros);
//		scrollPaneTabla.setBorder(BorderFactory.createEmptyBorder());

		// Configurar scroll pane para resultados
		JScrollPane scrollPaneResultados = new JScrollPane(areaResultados);
		scrollPaneResultados
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
						BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		// Configurar scroll pane para historial
		JScrollPane scrollPaneHistorial = new JScrollPane(areaHistorial);
//		scrollPaneHistorial.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		scrollPaneHistorial.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneHistorial.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// Ajustar ancho del scroll
		scrollPaneHistorial.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
		scrollPaneHistorial.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, 8));

		// Aplicar estilo moderno
		scrollPaneHistorial.getVerticalScrollBar().setUI(new ModernScrollBarUI());
		scrollPaneHistorial.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

		// Crear panel para botones
		botonReiniciar = new JButton("Reiniciar");
		botonReiniciar.setFont(new Font("Arial", Font.BOLD, 16));
		botonReiniciar.setBackground(COLOR_PRIMARIO);
		botonReiniciar.setForeground(COLOR_FONDO);
		botonReiniciar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		botonReiniciar.addActionListener(e -> reiniciarJuego());
		botonReiniciar.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				botonReiniciar.setBackground(COLOR_ACENTO);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				botonReiniciar.setBackground(COLOR_PRIMARIO);
			}

		});

		botonReiniciar.addMouseListener(cursorMano);

		botonVerMenosDiez = new JButton("Ver 10 Menos");
		botonVerMenosDiez.setFont(new Font("Arial", Font.BOLD, 16));
		botonVerMenosDiez.setBackground(COLOR_PRIMARIO);
		botonVerMenosDiez.setForeground(COLOR_FONDO);
		botonVerMenosDiez.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		// Hover
		botonVerMenosDiez.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				botonVerMenosDiez.setBackground(COLOR_ACENTO); // cambia al color hover
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				botonVerMenosDiez.setBackground(COLOR_PRIMARIO); // vuelve al color original
			}

		});
		botonVerMenosDiez.addMouseListener(cursorMano);
		
		final ImageIcon originalIcon = new ImageIcon(getClass().getResource("iconoExportarPdf.png"));

		// 2️⃣ Escalar el icono (tamaño normal)
		final Image imagenEscalada = originalIcon.getImage().getScaledInstance(54, 54, Image.SCALE_SMOOTH);
		ImageIcon iconoPDF = new ImageIcon(imagenEscalada);

		// 4️⃣ Botón
		btnExportarPDF = new JButton(iconoPDF);

		// 4️⃣ Estilo plano
		btnExportarPDF.setBorderPainted(false);
		btnExportarPDF.setContentAreaFilled(false);
		btnExportarPDF.setFocusPainted(false);
		btnExportarPDF.setOpaque(false);
		btnExportarPDF.setCursor(new Cursor(Cursor.HAND_CURSOR)); // cursor mano


		// 6️⃣ Dar tamaño REAL al botón
		btnExportarPDF.setPreferredSize(new Dimension(58, 48));

		// 5️⃣ Tamaño real y márgenes
		btnExportarPDF.setPreferredSize(new Dimension(58, 48));
		btnExportarPDF.setMargin(new Insets(10, 20, 10, 20));


		// 8️⃣ Tooltip
		btnExportarPDF.setToolTipText("Exportar resultados a PDF");

		btnExportarPDF.addMouseListener(cursorMano);

		btnExportarPDF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportarResultadosPDF();
			}
		});
		
		// 8️⃣ Hover con zoom sutil
		btnExportarPDF.addMouseListener(new java.awt.event.MouseAdapter() {
		    @Override
		    public void mouseEntered(MouseEvent e) {
		        Image imagenHover = originalIcon.getImage().getScaledInstance(58, 58, Image.SCALE_SMOOTH);
		        btnExportarPDF.setIcon(new ImageIcon(imagenHover));
		    }

		    @Override
		    public void mouseExited(MouseEvent e) {
		        btnExportarPDF.setIcon(iconoPDF); // vuelve al tamaño normal
		    }
		});

		botonVerMenosDiez.addActionListener(e -> {
			Set<Integer> seleccionados = obtenerNumerosSeleccionados();
			if (seleccionados.isEmpty()) {
				// Crear un botón personalizado para el JOptionPane
				AlertasUI.mostrarAlerta(this,"No hay números seleccionados.");
				return;

			}

			int numeroAhorcado = 0;
			MostrarMenosDiez ventana = new MostrarMenosDiez(obtenerNumerosSeleccionados(), this, numeroAhorcado);
			ventana.setVisible(true);
		});

		botonReiniciar.registerKeyboardAction(e -> botonReiniciar.doClick(),
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);

		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panelBotones.setBackground(COLOR_FONDO);
		panelBotones.add(btnExportarPDF);
		panelBotones.add(botonVerMenosDiez);
		panelBotones.add(botonReiniciar);

		// Organizar componentes en el frame
		setLayout(new BorderLayout(0, 32));
		add(tablaNumeros, BorderLayout.CENTER);
		add(new JScrollPane(areaResultados), BorderLayout.SOUTH);
		add(scrollPaneHistorial, BorderLayout.EAST);
		add(panelBotones, BorderLayout.NORTH);

		setTitle("Hanged Number");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		getContentPane().setBackground(COLOR_FONDO);
		setSize(1000, 700);
		setLocationRelativeTo(null);
		getRootPane().setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, COLOR_BORDE));

	}

	private List<Integer> getNumerosSeleccionadosEnFila(int fila) {
		List<Integer> numeros = new ArrayList<>();
		for (int j = 0; j < 10; j++) {
			if (seleccionados[fila][j]) {
				Object valor = tablaNumeros.getValueAt(fila, j);
				if (valor != null) {
					numeros.add((Integer) valor);
				}
			}
		}
		return numeros;
	}

	private List<Integer> getNumerosSeleccionadosEnColumna(int columna) {
		List<Integer> numeros = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			if (seleccionados[i][columna]) {
				Object valor = tablaNumeros.getValueAt(i, columna);
				if (valor != null) {
					numeros.add((Integer) valor);
				}
			}
		}
		return numeros;
	}

	private List<Integer> getNumerosSeleccionadosEnDiagonal() {
		List<Integer> numeros = new ArrayList<>();

		for (int desplazamiento = -9; desplazamiento <= 9; desplazamiento++) {
			List<Integer> diagonalActual = new ArrayList<>();

			for (int fila = 0; fila < 10; fila++) {
				int columna = fila + desplazamiento;
				if (columna >= 0 && columna < 10) {
					if (seleccionados[fila][columna]) {
						Object valor = tablaNumeros.getValueAt(fila, columna);
						if (valor != null) {
							diagonalActual.add((Integer) valor);
						}
					}
				}
			}

			if (diagonalActual.size() >= 2) {
				numeros.addAll(diagonalActual);
			}
		}

		return numeros;
	}

	public List<List<Integer>> getNumerosSeleccionadosEnDiagonalExtendida() {
		List<List<Integer>> diagonalesValidas = new ArrayList<>();
		int filas = TABLERO_EXTENDIDO.length;
		int columnas = TABLERO_EXTENDIDO[0].length;

		for (int inicioCol = 0; inicioCol < columnas; inicioCol++) {
			List<Integer> diagonal = new ArrayList<>();
			int fila = 0;
			int col = inicioCol;

			while (fila < filas && col < columnas) {
				int numero = TABLERO_EXTENDIDO[fila][col];
				if (esCoordenadaValidaEnTableroPrincipal(numero, fila, col)) {
					diagonal.add(numero);
				}
				fila++;
				col++;
			}

			if (diagonal.size() >= 2) {
				diagonalesValidas.add(diagonal);
			}
		}

		for (int inicioFila = 1; inicioFila < filas; inicioFila++) {
			List<Integer> diagonal = new ArrayList<>();
			int fila = inicioFila;
			int col = 0;

			while (fila < filas && col < columnas) {
				int numero = TABLERO_EXTENDIDO[fila][col];
				if (esCoordenadaValidaEnTableroPrincipal(numero, fila, col)) {
					diagonal.add(numero);
				}
				fila++;
				col++;
			}

			if (diagonal.size() >= 2) {
				diagonalesValidas.add(diagonal);
			}
		}

		List<List<Integer>> diagonalesFiltradas = new ArrayList<>();
		for (List<Integer> diagonal : diagonalesValidas) {
			if (diagonal.size() > 1) {
				diagonalesFiltradas.add(diagonal);
			}
		}
		return diagonalesFiltradas;
	}

	private List<List<Integer>> getNumerosSeleccionadosEnColumnaExtendida() {
		List<List<Integer>> columnasValidas = new ArrayList<>();
		int filas = TABLERO_EXTENDIDO.length;
		int columnas = TABLERO_EXTENDIDO[0].length;

		for (int col = 0; col < columnas; col++) {
			List<Integer> columnaExtendida = new ArrayList<>();

			for (int fila = 0; fila < filas; fila++) {
				int numero = TABLERO_EXTENDIDO[fila][col];
				if (esCoordenadaValidaEnTableroPrincipal(numero, fila, col)) {
					columnaExtendida.add(numero);
				}
			}

			if (columnaExtendida.size() >= 2) {
				columnasValidas.add(columnaExtendida);
			}
		}

		return columnasValidas;
	}

	private List<List<Integer>> getNumerosSeleccionadosEnFilaExtendida() {
		List<List<Integer>> filasValidas = new ArrayList<>();
		int filas = TABLERO_EXTENDIDO.length;
		int columnas = TABLERO_EXTENDIDO[0].length;

		for (int fila = 0; fila < filas; fila++) {
			List<Integer> filaExtendida = new ArrayList<>();

			for (int col = 0; col < columnas; col++) {
				int numero = TABLERO_EXTENDIDO[fila][col];
				if (esCoordenadaValidaEnTableroPrincipal(numero, fila, col)) {
					filaExtendida.add(numero);
				}
			}

			if (filaExtendida.size() >= 2) {
				filasValidas.add(filaExtendida);
			}
		}

		return filasValidas;
	}

	public boolean esCoordenadaValidaEnTableroPrincipal(int numero, int filaExtendida, int colExtendida) {
		if (numero < 1 || numero > 100)
			return false;

		int col = (numero - 1) / 10;
		int fila = (numero - 1) % 10;

		if (fila >= 0 && fila < 10 && col >= 0 && col < 10) {
			return seleccionados[fila][col];
		}
		return false;
	}

	public boolean validarDiagonalExtendidaMultiple(int num1, int num2) {
		List<int[]> posicionesNum1 = buscarTodasLasPosiciones(num1);
		List<int[]> posicionesNum2 = buscarTodasLasPosiciones(num2);

		for (int[] pos1 : posicionesNum1) {
			for (int[] pos2 : posicionesNum2) {
				int fila1 = pos1[0];
				int columna1 = pos1[1];
				int fila2 = pos2[0];
				int columna2 = pos2[1];

				if ((fila2 - fila1) == (columna2 - columna1) && (fila2 > fila1)) {

					if (columna1 >= 10 || columna2 >= 10) {
						continue;
					}

					int filaIntermedia = (fila1 + fila2) / 2;
					int columnaIntermedia = (columna1 + columna2) / 2;

					if (filaIntermedia >= 0 && filaIntermedia < TABLERO_EXTENDIDO.length && columnaIntermedia >= 0
							&& columnaIntermedia < 10) {

						int valorIntermedio = TABLERO_EXTENDIDO[filaIntermedia][columnaIntermedia];

						if (valorIntermedio != 0) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private List<int[]> buscarTodasLasPosiciones(int numero) {
		List<int[]> posiciones = new ArrayList<>();
		for (int fila = 0; fila < TABLERO_EXTENDIDO.length; fila++) {
			for (int columna = 0; columna < TABLERO_EXTENDIDO[0].length; columna++) {
				if (columna < 10 && TABLERO_EXTENDIDO[fila][columna] == numero) {
					posiciones.add(new int[] { fila, columna });
				}
			}
		}
		return posiciones;
	}

	public int contarNumerosEntreExtendida(int numeroInicio, int numeroFin) {
		List<int[]> posicionesInicio = new ArrayList<>();
		List<int[]> posicionesFin = new ArrayList<>();

		for (int i = 0; i < TABLERO_EXTENDIDO.length; i++) {
			for (int j = 0; j < TABLERO_EXTENDIDO[0].length; j++) {
				if (TABLERO_EXTENDIDO[i][j] == numeroInicio) {
					posicionesInicio.add(new int[] { i, j });
				}
				if (TABLERO_EXTENDIDO[i][j] == numeroFin) {
					posicionesFin.add(new int[] { i, j });
				}
			}
		}

		int menorCantidad = Integer.MAX_VALUE;

		for (int[] ini : posicionesInicio) {
			for (int[] fin : posicionesFin) {
				int filaInicio = ini[0], colInicio = ini[1];
				int filaFin = fin[0], colFin = fin[1];

				if (Math.abs(filaInicio - filaFin) == Math.abs(colInicio - colFin)) {
					int pasoFila = filaInicio < filaFin ? 1 : -1;
					int pasoCol = colInicio < colFin ? 1 : -1;

					int i = filaInicio + pasoFila;
					int j = colInicio + pasoCol;
					int cantidad = 0;

					while (i != filaFin || j != colFin) {
						cantidad++;
						i += pasoFila;
						j += pasoCol;
					}

					if (cantidad < menorCantidad) {
						menorCantidad = cantidad;
					}
				}
			}
		}

		return menorCantidad == Integer.MAX_VALUE ? 0 : menorCantidad;
	}

	private Integer encontrarNumeroAhorcadoDiagonalExtendida(int numeroInicio, int numeroFin) {
		List<int[]> posicionesInicio = new ArrayList<>();
		List<int[]> posicionesFin = new ArrayList<>();

		for (int i = 0; i < TABLERO_EXTENDIDO.length; i++) {
			for (int j = 0; j < TABLERO_EXTENDIDO[0].length; j++) {
				if (TABLERO_EXTENDIDO[i][j] == numeroInicio) {
					posicionesInicio.add(new int[] { i, j });
				}
				if (TABLERO_EXTENDIDO[i][j] == numeroFin) {
					posicionesFin.add(new int[] { i, j });
				}
			}
		}

		for (int[] inicio : posicionesInicio) {
			for (int[] fin : posicionesFin) {
				int filaInicio = inicio[0], colInicio = inicio[1];
				int filaFin = fin[0], colFin = fin[1];

				if (Math.abs(filaInicio - filaFin) == Math.abs(colInicio - colFin)) {
					int filaMedio = (filaInicio + filaFin) / 2;
					int colMedio = (colInicio + colFin) / 2;

					if (filaMedio >= 0 && filaMedio < TABLERO_EXTENDIDO.length && colMedio >= 0
							&& colMedio < TABLERO_EXTENDIDO[0].length) {
						return TABLERO_EXTENDIDO[filaMedio][colMedio];
					}
				}
			}
		}

		return null;
	}

	/**
	 * Devuelve todos los números que el usuario ha seleccionado en la tabla.
	 */
	public Set<Integer> obtenerNumerosSeleccionados() {
		Set<Integer> seleccionadosUsuario = new HashSet<>();
		for (int fila = 0; fila < 10; fila++) {
			for (int col = 0; col < 10; col++) {
				if (seleccionados[fila][col]) { // ← tu matriz de selección
					Object valor = tablaNumeros.getValueAt(fila, col);
					if (valor instanceof Integer) {
						seleccionadosUsuario.add((Integer) valor);
					}
				}
			}
		}
		return seleccionadosUsuario;
	}

	private boolean seleccionadoEnTablero(int numero) {
		int fila = (numero - 1) / 10;
		int columna = (numero - 1) % 10;
		return seleccionados[columna][fila];
	}

	public List<int[]> encontrarCoordenadasEnTableroExtendido(int numero) {
		List<int[]> coordenadas = new ArrayList<>();
		for (int fila = 0; fila < TABLERO_EXTENDIDO.length; fila++) {
			for (int col = 0; col < TABLERO_EXTENDIDO[0].length; col++) { // permitir zona extendida
				if (TABLERO_EXTENDIDO[fila][col] == numero) {
					coordenadas.add(new int[] { fila, col });
				}
			}
		}
		return coordenadas;
	}

	public boolean diagonalDescendenteExtendida(int num1, int num2) {
		List<int[]> posicionesNum1 = encontrarCoordenadasEnTableroExtendido(num1);
		List<int[]> posicionesNum2 = encontrarCoordenadasEnTableroExtendido(num2);

		for (int[] pos1 : posicionesNum1) {
			for (int[] pos2 : posicionesNum2) {
				int fila1 = pos1[0], col1 = pos1[1];
				int fila2 = pos2[0], col2 = pos2[1];
				int deltaFila = fila2 - fila1;
				int deltaCol = col2 - col1;

				if (deltaFila == deltaCol) { // misma diagonal descendente
					int cantidadEntre = Math.abs(deltaFila) - 1;
					if (cantidadEntre == 1 || cantidadEntre == 3 || cantidadEntre == 5 && cantidadEntre % 2 == 1) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean mismaFilaExtendida(int num1, int num2) {
		for (int i = 0; i < TABLERO_EXTENDIDO.length; i++) {
			int index1 = -1, index2 = -1;
			for (int j = 0; j < TABLERO_EXTENDIDO[i].length; j++) {
				if (TABLERO_EXTENDIDO[i][j] == num1) {
					index1 = j;
				}
				if (TABLERO_EXTENDIDO[i][j] == num2) {
					index2 = j;
				}
			}
			if (index1 != -1 && index2 != -1) {
				return true; // están en la misma fila extendida
			}
		}
		return false;
	}

	private String crearIdentificadorCombinacion(int a, int b) {
		int menor = Math.min(a, b);
		int mayor = Math.max(a, b);
		return menor + "-" + mayor;
	}

	private String claveUnica(String tipo, int a, int b) {
		int menor = Math.min(a, b);
		int mayor = Math.max(a, b);
		return tipo + "-" + menor + "-" + mayor;
	}

	public void procesarCombinacionesEnLista(List<Integer> numeros, String tipo) {
//		Set<Integer> numerosAhorcadosValidos = new HashSet<>();
//		Set<Integer> numerosAhorcadosInvalidos = new HashSet<>();
		if (numeros == null || tipo == null) {
			throw new IllegalArgumentException("Los parámetros no pueden ser nulos");
		}
		if (numeros.size() < 2)
			return;

		if (areaResultados != null)
			areaResultados.setText("");

		for (int i = 0; i < numeros.size() - 1; i++) {
			for (int j = i + 1; j < numeros.size(); j++) {
				Integer num1 = numeros.get(i);
				Integer num2 = numeros.get(j);

//				if (num1 == null || num2 == null || num1.equals(num2) || numerosAhorcados.contains(num1)
//						|| numerosAhorcados.contains(num2)) {
//					continue;
//				}

				if (num1 == null || num2 == null || num1.equals(num2)) {
					continue;
				}
				if ((numerosAhorcados.contains(num1) && !seleccionadoEnTablero(num1))
						|| (numerosAhorcados.contains(num2) && !seleccionadoEnTablero(num2))) {
					continue;
				}

				int menor = Math.min(num1, num2);
				int mayor = Math.max(num1, num2);

				int fila1 = (num1 - 1) / 10;
				int columna1 = (num1 - 1) % 10;
				int fila2 = (num2 - 1) / 10;
				int columna2 = (num2 - 1) % 10;

				boolean mismaFila = fila1 == fila2;
				boolean mismaColumna = columna1 == columna2;
				boolean diagonalDescendente = (fila2 - fila1) == (columna2 - columna1);

				boolean esDiagonalExtendida = tipo.equals("Diagonal Extendida");
				boolean esColumnaExtendida = tipo.equals("Columna Extendida");
				boolean esFilaExtendida = tipo.equals("Fila Extendida");

				Integer ahorcadoPrincipal = null;
				boolean combinacionValida = false;
				boolean combinacionValidaFilaExtendida = false;
				int cantidadNumerosFilaExt = numeros.size();

				if (!esDiagonalExtendida && !esColumnaExtendida && !esFilaExtendida) {
					int cantidad = contarNumerosEntre(menor, mayor);
					ahorcadoPrincipal = (menor + mayor) / 2;
					combinacionValida = (mismaFila || mismaColumna || diagonalDescendente) && cantidad > 0
							&& cantidad <= 5 && cantidad % 2 == 1;
					if (combinacionValida && mismaColumna && cantidad == 1) {
						combinacionValida = false;
						int candidato = (menor + mayor) / 2;
						ahorcadoPrincipal = null; // ❌ no marcarlo como ahorcado normal
					}
					// 🚫 No procesar combinaciones inválidas ni ignoradas
					if (!combinacionValida || ahorcadoPrincipal == null) {
						continue;
					}

				} else if (esDiagonalExtendida) {
					if (mismaFila || mismaColumna || diagonalDescendente) {
						continue;
					}
					String clave = claveUnica(tipo, num1, num2);
					if (combinacionesProcesadas.contains(clave)) {
						continue;
					}

					int cantidad = contarNumerosEntreExtendida(num1, num2);
					ahorcadoPrincipal = encontrarNumeroAhorcadoDiagonalExtendida(num1, num2);
					boolean diagonalValida = diagonalDescendenteExtendida(num1, num2) || mismaFilaExtendida(num1, num2);
					combinacionValida = diagonalValida && cantidad > 0 && cantidad <= 5 && cantidad % 2 == 1;

					combinacionesProcesadas.add(clave);

					if (!combinacionValida) {
//						mostrarCombinacionInvalida(num1, num2,
//								"- Número ahorcado es nulo, coincide con los seleccionados o se encuentra a tres pasos o más.\n");
						continue;
					}

					if (seleccionadoEnTablero(ahorcadoPrincipal)) {
						mostrarCombinacionInvalida(num1, num2,
								"- El número " + ahorcadoPrincipal + " ya está seleccionado\n");
						continue;
					}

					// ❗ Evitar combinaciones duplicadas o invertidas
					String key = normalizar(num1, num2);
					if (combinacionYAhorcado.containsKey(key)) {
						continue; // Ya registrada → saltar
					}

					StringBuilder resultado = new StringBuilder();
					resultado.append("Combinación: " + num1).append(" - ").append(num2).append("; Número ahorcado: ")
							.append(ahorcadoPrincipal);

					if (combinacionValida && ahorcadoPrincipal != null
							&& !numerosAhorcados.contains(ahorcadoPrincipal)) {
						numerosAhorcados.add(ahorcadoPrincipal);
					}

					List<Integer> nuevos = encontrarNumerosAhorcadosAdicionales(num1, num2, numeros,
							numerosAhorcadosAdicionales);
					for (Integer numero : nuevos) {
						if (numero != null && !numerosAhorcados.contains(numero)) {
							resultado.append(" ").append(Math.abs(numero));
							numerosAhorcados.add(numero);
						}
					}

					if (areaResultados != null) {
						areaResultados.append(resultado.toString());
						areaResultados.append("\n");
						registrarCombinacion(resultado.toString()); // ✅ ya no dará error
					}

					if (areaHistorial != null) {
						String historial = areaHistorial.getText();
						if (!historial.isEmpty())
							historial += "\n\n";
						historial += resultado.toString();
						areaHistorial.setText(historial);
						SwingUtilities.invokeLater(() -> {
							areaHistorial.setCaretPosition(areaHistorial.getDocument().getLength());
							areaHistorial.revalidate();
							areaHistorial.repaint();
							registrarCombinacion(resultado.toString()); // ✅ ya no dará error
						});
					}

					continue;
				} else if (esColumnaExtendida) {
					int cantidad = contarNumerosEntreEnColumnaExtendida(num1, num2);
					if (cantidad > 0 && cantidad <= 5 && cantidad % 2 == 1) {
						ahorcadoPrincipal = encontrarNumeroAhorcadoColumnaExtendida(num1, num2);
						combinacionValida = true;
					}
				} else if (esFilaExtendida) {
					ahorcadoPrincipal = encontrarAhorcadosFilaExtendidaParteCircular(num1, num2);
					if (ahorcadoPrincipal != null && !num1.equals(ahorcadoPrincipal)
							&& !num2.equals(ahorcadoPrincipal)) {
						combinacionValidaFilaExtendida = true;
					}
				}
				boolean ahorcadoCircularFilaExtValido = esFilaExtendida && cantidadNumerosFilaExt > 0
						&& cantidadNumerosFilaExt <= 5 && (cantidadNumerosFilaExt % 2 == 1)
						&& encontrarAhorcadosFilaExtendidaParteCircular(num1, num2) != null;

				if (esFilaExtendida) {
					String clave = claveUnica(tipo, num1, num2);
					if (combinacionesProcesadas.contains(clave)) {
						continue;
					}

					List<Integer> nuevos = encontrarNumerosAhorcadosAdicionales(num1, num2, numeros,
							numerosAhorcadosAdicionales);
					Integer ahorcadoFilaExtendida = encontrarAhorcadosFilaExtendidaParteCircular(num1, num2);

					if ((ahorcadoFilaExtendida == null || num1.equals(ahorcadoFilaExtendida)
							|| num2.equals(ahorcadoFilaExtendida)) && nuevos.isEmpty()) {
						continue;
					}

					combinacionesProcesadas.add(clave);

					if (ahorcadoFilaExtendida != null && !num1.equals(ahorcadoFilaExtendida)
							&& !num2.equals(ahorcadoFilaExtendida)) {
						numerosAhorcados.add(ahorcadoFilaExtendida);
					}
					for (Integer numero : nuevos) {
						if (numero != null && !numerosAhorcados.contains(numero)) {
							numerosAhorcados.add(numero);
						}
					}

					StringBuilder resultado = new StringBuilder();
					resultado.append("Ahorcado adicional: ");

					boolean tieneDatos = false;
					if (ahorcadoFilaExtendida != null && !num1.equals(ahorcadoFilaExtendida)
							&& !num2.equals(ahorcadoFilaExtendida)) {
						resultado.append(" ").append(ahorcadoFilaExtendida);
						tieneDatos = true;
					}
					for (Integer numero : nuevos) {
						resultado.append(" ").append(numero);
						tieneDatos = true;
					}

					if (tieneDatos) {
						if (areaResultados != null) {
							areaResultados.append(resultado.toString());
							areaResultados.append("\n");
						}

						if (areaHistorial != null) {
							String historial = areaHistorial.getText();
							if (!historial.isEmpty())
								historial += "\n";
							historial += resultado.toString();
							areaHistorial.setText(historial);
							SwingUtilities.invokeLater(() -> {
								areaHistorial.setCaretPosition(areaHistorial.getDocument().getLength());
								areaHistorial.revalidate();
								areaHistorial.repaint();
							});
						}
					}

					continue;
				}

				boolean ahorcadoCircularValido = esFilaExtendida && numeros.size() > 0 && numeros.size() == 5
						&& numeros.size() % 2 == 1 && encontrarAhorcadosFilaExtendidaParteCircular(num1, num2) != null;

				if ((ahorcadoPrincipal == null || num1.equals(ahorcadoPrincipal) || num2.equals(ahorcadoPrincipal))
						&& !ahorcadoCircularValido) {
//					mostrarCombinacionInvalida(num1, num2,
//							"- Número ahorcado es nulo, coincide con los seleccionados o se encuentra a tres pasos o más.\n");
					continue;
				}

				if (!combinacionValida) {
//					mostrarCombinacionInvalida(num1, num2,
//							"- Número ahorcado es nulo, coincide con los seleccionados o se encuentra a tres pasos o más.\n");

					List<Integer> nuevos = encontrarNumerosAhorcadosAdicionales(num1, num2, numeros,
							numerosAhorcadosAdicionales);

					if (!nuevos.isEmpty()) {
						StringBuilder resultadoAdicionales = new StringBuilder();
						resultadoAdicionales.append("[").append(tipo).append("] Combinación inválida entre: ")
								.append(num1).append(" y ").append(num2).append("\n")
								.append("Ahorcados adicionales detectados: ");
						for (Integer numero : nuevos) {
							if (numero != null && !numerosAhorcados.contains(numero)) {
								resultadoAdicionales.append(" ").append(Math.abs(numero));
								numerosAhorcados.add(numero);
							}
						}
						if (areaResultados != null) {
							areaResultados.append(resultadoAdicionales.toString());
							areaResultados.append("\n");
						}
					}
					continue;
				}

				String combinacionId = claveUnica(tipo, num1, num2);
				if (combinacionesProcesadas.contains(combinacionId)) {
					continue;
				}

				if (seleccionadoEnTablero(ahorcadoPrincipal)) {
					mostrarCombinacionInvalida(num1, num2,
							"- El número " + ahorcadoPrincipal + " ya está seleccionado\n");
					continue;
				}

				// ❗ Evitar combinaciones duplicadas o invertidas
				String key = normalizar(num1, num2);
				if (combinacionYAhorcado.containsKey(key)) {
					continue; // Ya registrada → saltar
				}

				StringBuilder resultado = new StringBuilder();
				resultado.append("Combinación: ").append(num1).append(" - ").append(num2)
						.append(";   Número ahorcado: ").append(ahorcadoPrincipal);

				if (combinacionValida && ahorcadoPrincipal != null && !numerosAhorcados.contains(ahorcadoPrincipal)) {
					numerosAhorcados.add(ahorcadoPrincipal);
				}

				List<Integer> nuevos = encontrarNumerosAhorcadosAdicionales(num1, num2, numeros,
						numerosAhorcadosAdicionales);
				for (Integer numero : nuevos) {
					if (numero != null && !numerosAhorcados.contains(numero)) {
						resultado.append(" ").append(Math.abs(numero));
					}
				}

				if (areaResultados != null) {
					areaResultados.append(resultado.toString());
					areaResultados.append("\n");
//					 registrarCombinacion(resultado.toString()); // 👈 Guardar también aquí
				}

				combinacionesProcesadas.add(combinacionId);

				if (areaHistorial != null) {
					String historial = areaHistorial.getText();
					if (!historial.isEmpty())
						historial += "\n\n";
					historial += resultado.toString();
					areaHistorial.setText(historial);
					SwingUtilities.invokeLater(() -> {
						areaHistorial.setCaretPosition(areaHistorial.getDocument().getLength());
						areaHistorial.revalidate();
						areaHistorial.repaint();
					});
					registrarCombinacion(resultado.toString()); // 👈 Guardar también aquí
				}
			}
		}
	}

	/**
	 * Intercepta una combinación válida detectada y la guarda en memoria para usos
	 * posteriores.
	 */
	public List<String> registrarCombinacion(String combinacion) {
		if (combinacion == null || combinacion.trim().isEmpty()) {
			return todasLasCombinaciones;
		}

		String linea = combinacion.trim();
		todasLasCombinaciones.add(linea);

		// Intentamos parsear formato: "Combinación: N1 - N2; Número ahorcado: X"
		try {
			// separar la parte de combinación y la parte de ahorcado si existe
			String[] partes = linea.split(";");
			String parteCombo = partes[0].trim();

			// normalizar prefijos
			String lower = parteCombo.toLowerCase();
			if (lower.startsWith("combinación:")) {
				parteCombo = parteCombo.substring("combinación:".length()).trim();
			} else if (lower.startsWith("combinacion:")) {
				parteCombo = parteCombo.substring("combinacion:".length()).trim();
			}

			// ahora parteCombo debería ser "N1 - N2"
			String[] nums = parteCombo.split(" - ");
			if (nums.length == 2) {
				int n1 = Integer.parseInt(nums[0].trim());
				int n2 = Integer.parseInt(nums[1].trim());
				String key = normalizar(n1, n2);

				// buscar ahorcado si viene en la misma línea
				Integer ahorcado = null;
				if (partes.length > 1) {
					// buscar en el resto de la línea el número tras "Número ahorcado:" si existe
					for (int i = 1; i < partes.length; i++) {
						String p = partes[i].trim().toLowerCase();
						if (p.contains("número ahorcado") || p.contains("numero ahorcado")) {
							String[] sec = partes[i].split(":");
							if (sec.length >= 2) {
								try {
									ahorcado = Integer.parseInt(sec[1].trim().split("\\s+")[0]);
								} catch (NumberFormatException e) {
									ahorcado = null;
								}
							}
							break;
						}
					}
				}

				// Si no vino el ahorcado en la cadena, intenta buscarlo en la propia
				// representación
				// (por si en algún punto guardas "Combinación: 11 - 51; Número ahorcado: 31" o
				// ya la construyes)
				if (ahorcado == null) {
					// intentar buscar pattern "Número ahorcado: X" en la línea completa
					int idx = linea.toLowerCase().indexOf("número ahorcado");
					if (idx == -1)
						idx = linea.toLowerCase().indexOf("numero ahorcado");
					if (idx != -1) {
						String substr = linea.substring(idx);
						String[] sec = substr.split(":");
						if (sec.length >= 2) {
							try {
								ahorcado = Integer.parseInt(sec[1].trim().split("\\s+")[0]);
							} catch (NumberFormatException ex) {
								ahorcado = null;
							}
						}
					}
				}

				// Si calculaste / obtuviste el ahorcado, guarda la relación
				if (ahorcado != null) {
					combinacionYAhorcado.put(key, ahorcado);
				}
			}
		} catch (Exception ex) {
			// No queremos romper la lógica por un parseo; dejamos la lista como estaba.
		}

		return new ArrayList<>(todasLasCombinaciones);
	}

	public int contarNumerosEntreFilaExtendida(int num1, int num2) {
		if (num1 == num2) {
			return 0;
		}

		// Obtener columna
		int col1 = (num1 - 1) % 10;
		int col2 = (num2 - 1) % 10;

		if (col1 != col2) {
			return -1; // No están en la misma columna
		}

		// Obtener fila
		int fila1 = (num1 - 1) / 10;
		int fila2 = (num2 - 1) / 10;

		// Calcular pasos verticales circulares
		int totalFilas = 10; // asumiendo 10 filas (0 a 9)
		int distancia = Math.abs(fila2 - fila1);
		int pasosCirculares = Math.min(distancia, totalFilas - distancia);

		// Restar 1 porque queremos cuántos hay ENTRE ellos
		return pasosCirculares - 1;
	}

	public int contarNumerosEntreEnColumnaExtendida(int num1, int num2) {
		for (int col = 0; col < TABLERO_EXTENDIDO[0].length; col++) {
			List<Integer> columna = new ArrayList<>();
			for (int fila = 0; fila < TABLERO_EXTENDIDO.length; fila++) {
				columna.add(TABLERO_EXTENDIDO[fila][col]);
			}
			int index1 = columna.indexOf(num1);
			int index2 = columna.indexOf(num2);
			if (index1 != -1 && index2 != -1 && Math.abs(index2 - index1) > 1) {
				return Math.abs(index2 - index1) - 1;
			}
		}
		return -1;
	}

	public Integer encontrarNumeroAhorcadoColumnaExtendida(int num1, int num2) {
		for (int col = 0; col < TABLERO_EXTENDIDO[0].length; col++) {
			List<Integer> columna = new ArrayList<>();
			for (int fila = 0; fila < TABLERO_EXTENDIDO.length; fila++) {
				columna.add(TABLERO_EXTENDIDO[fila][col]);
			}
			int index1 = columna.indexOf(num1);
			int index2 = columna.indexOf(num2);
			if (index1 != -1 && index2 != -1 && Math.abs(index1 - index2) % 2 == 0) {
				int medio = (index1 + index2) / 2;
				return columna.get(medio);
			}
		}
		return null;
	}

	public Integer encontrarAhorcadosFilaExtendidaParteCircular(int num1, int num2) {
		for (int fila = 0; fila < TABLERO_EXTENDIDO.length; fila++) {
			List<Integer> filaActual = Arrays.stream(TABLERO_EXTENDIDO[fila]).boxed().collect(Collectors.toList());
			int total = filaActual.size();

			int mayor = Math.max(num1, num2);
			int menor = Math.min(num1, num2);

			int inicio = filaActual.indexOf(mayor);
			if (inicio == -1)
				continue;

			// Buscar la siguiente aparición del menor desde el índice mayor
			int fin = -1;
			for (int i = 1; i < total; i++) {
				int idx = (inicio + i) % total;
				if (filaActual.get(idx) == menor) {
					fin = idx;
					break;
				}
			}
			if (fin == -1)
				continue;

			// Construir recorrido circular excluyendo los extremos
			List<Integer> recorrido = new ArrayList<>();
			int idx = (inicio + 1) % total;
			while (idx != fin) {
				recorrido.add(filaActual.get(idx));
				idx = (idx + 1) % total;
			}
			// Aquí se manejan las validaciones de las filas extendida (circular)
			if (recorrido.size() >= 3 && recorrido.size() <= 5 && recorrido.size() % 2 == 1) {
				int idxAhorcado = recorrido.size() / 2;
				return recorrido.get(idxAhorcado);
			}
		}
		return null;
	}

	private void mostrarCombinacionInvalida(Integer num1, Integer num2, String motivo) {
		StringBuilder resultado = new StringBuilder();
		resultado.append("Combinación Inválida: ").append(num1).append(", ").append(num2).append("\n");
		resultado.append("Motivo: ").append(motivo).append("\n");
		areaResultados.append(resultado.toString());
		areaResultados.append("\n");
	}

	public List<Integer> encontrarNumerosAhorcadosAdicionales(int num1, int num2, List<Integer> numerosSeleccionados,
			Set<Integer> numerosAhorcadosAdicionales) {
		List<Integer> ahorcados = new ArrayList<>();

		int filaNum1 = -1, colNum1 = -1;
		int filaNum2 = -1, colNum2 = -1;

		// Buscar posiciones en el TABLERO_EXTENDIDO
		for (int i = 0; i < TABLERO_EXTENDIDO.length; i++) {
			for (int j = 0; j < TABLERO_EXTENDIDO[i].length; j++) {
				int valor = TABLERO_EXTENDIDO[i][j];
				if (valor == num1) {
					filaNum1 = i;
					colNum1 = j;
				}
				if (valor == num2) {
					filaNum2 = i;
					colNum2 = j;
				}
			}
		}

		if (filaNum1 == -1 || filaNum2 == -1 || colNum1 == -1 || colNum2 == -1) {
			System.out.println("No se encontraron num1 o num2 en TABLERO_EXTENDIDO");
			return ahorcados; // no se encontraron
		}

		if (filaNum1 == filaNum2) {
			int fila = filaNum1;
			int totalColumnas = TABLERO_EXTENDIDO[fila].length;

			// Calcular distancia circular entre columnas
			int distanciaDirecta = Math.abs(colNum2 - colNum1) - 1;
			int distanciaCircular = totalColumnas - Math.abs(colNum2 - colNum1) - 1;

			// Elegir la distancia menor (circular)
			int cantidadEntre = Math.min(distanciaDirecta, distanciaCircular);

			// Ahora elegir el sentido para hallar índice medio circular
			int menorCol, mayorCol;
			boolean usoDistanciaDirecta = distanciaDirecta <= distanciaCircular;

			if (usoDistanciaDirecta) {
				menorCol = Math.min(colNum1, colNum2);
				mayorCol = Math.max(colNum1, colNum2);
			} else {
				// En sentido circular invertido, la fila circular "pasa por los extremos"
				// Para hallar el índice medio, calculamos el punto medio circular
				// Ejemplo: entre 81 y 1, columnas 8 y 0 en fila con 10 columnas, el medio será
				// columna 9 (91)
				menorCol = mayorCol = -1; // Lo calculamos abajo
			}

			if (cantidadEntre <= 5 && (cantidadEntre == 3 || cantidadEntre == 5)) {
				int indiceMedio;

				if (usoDistanciaDirecta) {
					indiceMedio = (menorCol + mayorCol) / 2;
				} else {
					// Distancia circular invertida
					// Tomamos el punto medio circular entre colNum1 y colNum2
					// Por ejemplo, si colNum1=8 y colNum2=0 y totalColumnas=10, la distancia
					// circular es 1
					// El índice medio es (colNum1 + (cantidadEntre + 1)/2) mod totalColumnas

					// Primero definimos cuál es el inicio y fin en sentido circular
					int start = colNum1;
					int steps = (cantidadEntre + 1) / 2;
					indiceMedio = (start + steps) % totalColumnas;
				}

				int posibleAhorcado = TABLERO_EXTENDIDO[fila][indiceMedio];

				if (!numerosSeleccionados.contains(posibleAhorcado)
						&& !numerosAhorcadosAdicionales.contains(posibleAhorcado)) {
					ahorcados.add(posibleAhorcado);
					numerosAhorcadosAdicionales.add(posibleAhorcado);
				}
			}

			return ahorcados;
		}

		return ahorcados; // No se encontró una alineación válida
	}

	private int contarNumerosEntre(int num1, int num2) {
		int filaNum1 = -1, colNum1 = -1;
		int filaNum2 = -1, colNum2 = -1;

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				int valor = (Integer) tablaNumeros.getValueAt(i, j);
				if (valor == num1) {
					filaNum1 = i;
					colNum1 = j;
				}
				if (valor == num2) {
					filaNum2 = i;
					colNum2 = j;
				}
			}
		}

		int contador = 0;

		if (filaNum1 == filaNum2) {
			int inicio = Math.min(colNum1, colNum2) + 1;
			int fin = Math.max(colNum1, colNum2);
			for (int i = inicio; i < fin; i++) {
				contador++;
			}
		} else if (colNum1 == colNum2) {
			int inicio = Math.min(filaNum1, filaNum2) + 1;
			int fin = Math.max(filaNum1, filaNum2);
			for (int i = inicio; i < fin; i++) {
				contador++;
			}
		} else if (Math.abs(filaNum1 - filaNum2) == Math.abs(colNum1 - colNum2)) {
			int pasos = Math.abs(filaNum1 - filaNum2) - 1;
			contador = pasos;
		}
		return contador;
	}

	public void reiniciarJuego() {
		// Limpiar matriz de selección (visual y lógica)
		for (int i = 0; i < seleccionados.length; i++) {
			for (int j = 0; j < seleccionados[i].length; j++) {
				seleccionados[i][j] = false;
			}
		}

		// Limpiar sets y listas relacionadas con el estado del juego
		numerosAhorcadosAdicionales.clear();
		combinacionesProcesadas.clear();
		numerosAhorcados.clear();

		// Limpiar la lista que guarda todas las combinaciones detectadas
		limpiarCombinaciones(); // <-- llamada clave que faltaba

		// Limpiar UI
		if (areaResultados != null)
			areaResultados.setText("");
		if (areaHistorial != null)
			areaHistorial.setText("");
		tablaNumeros.clearSelection();
		tablaNumeros.repaint();
	}

	class TablaEventHandler extends MouseAdapter {
		private final InterfazAhorcado interfaz;

		public TablaEventHandler(InterfazAhorcado interfaz) {
			this.interfaz = interfaz;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				int fila = interfaz.tablaNumeros.rowAtPoint(e.getPoint());
				int columna = interfaz.tablaNumeros.columnAtPoint(e.getPoint());
				if (fila >= 0 && fila < 10 && columna >= 0 && columna < 10) {
					interfaz.seleccionados[fila][columna] = !interfaz.seleccionados[fila][columna];
					interfaz.tablaNumeros.repaint();

					List<Integer> numerosFila = interfaz.getNumerosSeleccionadosEnFila(fila);
					List<Integer> numerosColumna = interfaz.getNumerosSeleccionadosEnColumna(columna);
					List<Integer> numerosDiagonal = interfaz.getNumerosSeleccionadosEnDiagonal();
					List<List<Integer>> numerosDiagonalExt = getNumerosSeleccionadosEnDiagonalExtendida();
					List<List<Integer>> columnasExt = getNumerosSeleccionadosEnColumnaExtendida();
					List<List<Integer>> filasExt = getNumerosSeleccionadosEnFilaExtendida();

					if (!numerosFila.isEmpty()) {
						interfaz.procesarCombinacionesEnLista(numerosFila, "Fila " + (fila + 1));
					}
//					if (!numerosColumna.isEmpty()) {
//						interfaz.procesarCombinacionesEnLista(numerosColumna, "Columna " + (columna + 1));
//					}
					if (!numerosDiagonal.isEmpty()) {
						interfaz.procesarCombinacionesEnLista(numerosDiagonal, "Diagonal ");
					}
					if (!numerosDiagonalExt.isEmpty()) {
						for (List<Integer> diagonal : getNumerosSeleccionadosEnDiagonalExtendida()) {
							procesarCombinacionesEnLista(diagonal, "Diagonal Extendida");
						}
					}
					if (!columnasExt.isEmpty()) {
						for (List<Integer> columnaExt : columnasExt) {
							procesarCombinacionesEnLista(columnaExt, "Columna Extendida");
						}
					}
					if (!filasExt.isEmpty()) {
						for (List<Integer> filaExt : filasExt) {
							procesarCombinacionesEnLista(filaExt, "Fila Extendida");
						}
					}
				}

			}
		}
	}
}