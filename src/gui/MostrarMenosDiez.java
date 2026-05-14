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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MostrarMenosDiez extends JFrame {

	private final Color COLOR_PRIMARIO = new Color(37, 99, 235);
	private final Color COLOR_ACENTO = new Color(30, 58, 138);
	private final Color COLOR_FONDO = new Color(240, 240, 240); // 243, 244, 246
	private final Color COLOR_TEXTO = new Color(17, 24, 39);
	private final Color COLOR_TEXTAREAS = new Color(255, 255, 255);

	private MouseAdapter cursorMano;

	public MostrarMenosDiez(Set<Integer> numerosSeleccionados) {
		this(numerosSeleccionados, null, 0); // delega al constructor principal
	}

	// Constructor
	public MostrarMenosDiez(Set<Integer> numerosSeleccionados, InterfazAhorcado interfaz, int numeroAhorcado) {

		URL iconUrl = getClass().getResource("/gui/favicon.png");
		if (iconUrl != null) {
			setIconImage(new ImageIcon(iconUrl).getImage());
		}

		// ?? Inicializar cursor de manito
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

		setTitle("Hanged Number");
		setSize(700, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(true);
		getContentPane().setBackground(COLOR_FONDO);
		setLayout(new BorderLayout());

		// Calcula los números -10 con combinación y número ahorcado
		Set<NumerosDiezMenos> menosDiez = NumerosDiezMenos.obtenerMenosDiez(numerosSeleccionados, interfaz,
				numeroAhorcado);

		// Área de texto
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setFont(new Font("Arial", Font.PLAIN, 16));
		area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setBackground(COLOR_TEXTAREAS);
		area.setForeground(COLOR_TEXTO);

		// PROBAR SCROLL
		JScrollPane scrollPaneArea = new JScrollPane(area);
		scrollPaneArea.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		scrollPaneArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollPaneArea.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE)); // ancho vertical
		scrollPaneArea.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, 8)); // alto
																										// horizontal

		// Aplicar el scroll moderno
		scrollPaneArea.getVerticalScrollBar().setUI(new ModernScrollBarUI());
		scrollPaneArea.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

		if (menosDiez.isEmpty()) {
			area.setText("No hay números que sean diez menos entre los seleccionados.");
		} else {
			StringBuilder sb = new StringBuilder();
			menosDiez.stream().sorted((a, b) -> Integer.compare(a.getNumeroDiezMenos(), b.getNumeroDiezMenos()))
					.forEach(num -> sb.append(num.toString()).append("\n"));
			area.setText(sb.toString());
		}

		// Crear pestañas
		JTabbedPane pestañas = new JTabbedPane();

		pestañas.setUI(new FirefoxTabbedPaneUI());

		pestañas.setOpaque(false);
		pestañas.setBorder(null);
		pestañas.setBackground(null);
		pestañas.setFocusable(false);
//		pestañas.setContentAreaFilled(false);

		UIManager.put("TabbedPane.contentOpaque", false);

		// Pestaña 1 - Menos Diez
		pestañas.addTab("10 Menos", scrollPaneArea);

		// Pestaña 2 - Otros resultados (por ahora vacía)
		JTextArea areaOtros = new JTextArea();
		areaOtros.setEditable(false);
		areaOtros.setFont(new Font("Arial", Font.PLAIN, 16));
		areaOtros.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		areaOtros.setLineWrap(true);
		areaOtros.setWrapStyleWord(true);
		areaOtros.setBackground(COLOR_TEXTAREAS);
		areaOtros.setForeground(COLOR_TEXTO);

		// PROBAR SCROLL
		JScrollPane scrollPaneAreaOtros = new JScrollPane(areaOtros);
		scrollPaneAreaOtros.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		scrollPaneAreaOtros.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneAreaOtros.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollPaneAreaOtros.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE)); // ancho
																											// vertical
		scrollPaneAreaOtros.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, 8)); // alto
		// horizontal

		// Aplicar el scroll moderno
		scrollPaneAreaOtros.getVerticalScrollBar().setUI(new ModernScrollBarUI());
		scrollPaneAreaOtros.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

		if (menosDiez.isEmpty()) {
			areaOtros.setText("No hay resultados para mostrar.");
		} else if (interfaz == null) {
			areaOtros.setText("No hay datos de ahorcados disponibles.");
		} else {
			String resultado = construirGruposConAhorcados(menosDiez, interfaz, numerosSeleccionados);
			areaOtros.setText(resultado);
		}

		pestañas.addTab("Grupos de Cuatro", scrollPaneAreaOtros);

		// Agregar pestañas al centro
		add(pestañas, BorderLayout.CENTER);

		// Botón Cerrar
		JButton cerrar = new JButton("Cancelar");
		cerrar.setFont(new Font("Arial", Font.BOLD, 16));
		cerrar.setBackground(COLOR_PRIMARIO);
		cerrar.setForeground(COLOR_TEXTAREAS);
		cerrar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		cerrar.addActionListener(e -> dispose());
		cerrar.addMouseListener(cursorMano);
		cerrar.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				cerrar.setBackground(COLOR_ACENTO);
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				cerrar.setBackground(COLOR_PRIMARIO);
			}
		});

		URL iconUrl1 = getClass().getResource("/gui/iconoExportarPdf.png");

		final ImageIcon originalIcon;
		final ImageIcon iconoNormal;
		final ImageIcon iconoHover;

		if (iconUrl1 == null) {
			System.err.println("? iconoExportarPdf.png NO encontrado");
			originalIcon = null;
			iconoNormal = null;
			iconoHover = null;
		} else {
			originalIcon = new ImageIcon(iconUrl1);

			iconoNormal = new ImageIcon(originalIcon.getImage().getScaledInstance(54, 54, Image.SCALE_SMOOTH));

			iconoHover = new ImageIcon(originalIcon.getImage().getScaledInstance(58, 58, Image.SCALE_SMOOTH));
		}
		// 4?? Crear botón con icono normal
		JButton btnExportarPDF = new JButton(iconoNormal);
		btnExportarPDF.setBorderPainted(false);
		btnExportarPDF.setContentAreaFilled(false);
		btnExportarPDF.setFocusPainted(false);
		btnExportarPDF.setOpaque(false);
		btnExportarPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnExportarPDF.setPreferredSize(new Dimension(58, 48));
		btnExportarPDF.setToolTipText("Exportar resultados a PDF");
		btnExportarPDF.addMouseListener(cursorMano);

		// 5?? Hover: cambiar icono a más grande y volver al original
		btnExportarPDF.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				// ?? AQUÍ VA ESTO
				if (iconoHover != null) {
					btnExportarPDF.setIcon(iconoHover);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (iconoNormal != null) {
					btnExportarPDF.setIcon(iconoNormal);
				}
			}
		});

		// 6?? Acción al hacer clic
		btnExportarPDF.addActionListener(e -> {
			exportarPDF(area.getText(), "Resultados 10 Menos");
			exportarPDF(areaOtros.getText(), "Grupos de Cuatro");
		});

		// PANEL SUPERIOR (TÍTULO + BOTONES)
		JPanel panelSuperior = new JPanel(new BorderLayout());
		panelSuperior.setBackground(COLOR_FONDO);

		// --------- TÍTULO (IZQUIERDA) ---------
		JLabel lblSubtitleTabla = new JLabel("NÚMEROS 10 MENOS");
		lblSubtitleTabla.setFont(new Font("Arial", Font.BOLD, 18));
		lblSubtitleTabla.setForeground(COLOR_TEXTO);
		lblSubtitleTabla.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // margen izquierdo

		panelSuperior.add(lblSubtitleTabla, BorderLayout.WEST);

		// --------- PANEL BOTONES (DERECHA) ---------
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		panelBotones.setBackground(COLOR_FONDO);

		// Botones (usar los ya creados con iconos y listeners)
		panelBotones.add(btnExportarPDF);
		panelBotones.add(cerrar);

		panelSuperior.add(panelBotones, BorderLayout.EAST);

		// Finalmente agregar al NORTH de la ventana
		add(panelSuperior, BorderLayout.NORTH);

		// 👇 AGREGA ESTO AL FINAL DEL CONSTRUCTOR
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				// opcional
			}

			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				dispose();
			}
		});

	}

	// Método Dibujar Título
	// Método Dibujar Título
	private static void dibujarTitulo(PDPageContentStream contentStream, String titulo) throws IOException {
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
		contentStream.newLineAtOffset(50, 740);
		contentStream.showText(titulo);
		contentStream.endText();
	}

	// Método para Numerar Páginas
	private static void numerarPagina(PDPageContentStream contentStream, int pagina, int totalPaginas)
			throws IOException {
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 10);
		contentStream.newLineAtOffset(250, 20);

		if (totalPaginas > 0) {
			contentStream.showText("Página " + pagina + " de " + totalPaginas);
		} else {
			contentStream.showText("Página " + pagina);
		}

		contentStream.endText();
	}

	// Limpia texto para que PDFBox no falle en exe
	// ---------------------------------------------
	private String limpiarPDF(String s) {
		return s.replace("\\", "\\\\").replace("(", "  (").replace(")", "  )").replace("\r", "").replace("\n", "")
				.replace("\t", " ");
	}

	private void exportarPDF(String texto, String tituloPDF) {
		if (texto == null || texto.trim().isEmpty()) {
			AlertasUI.mostrarAlerta(this, "No hay resultados para exportar: " + tituloPDF);
			return;
		}

		FileDialog fd = new FileDialog(this, "Guardar " + tituloPDF + " en PDF", FileDialog.SAVE);
		fd.setFile(tituloPDF.replace(" ", "_") + ".pdf");
		fd.setVisible(true);

		String directory = fd.getDirectory();
		String filename = fd.getFile();
		if (directory == null || filename == null)
			return;

		File archivo = new File(directory, filename);
		if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
			archivo = new File(archivo.getAbsolutePath() + ".pdf");
		}

		try {
			archivo = archivo.getCanonicalFile();
		} catch (IOException e) {
			AlertasUI.mostrarAlerta(this, "Ruta inválida para guardar el PDF.");
			return;
		}

		if (!archivo.getParentFile().canWrite()) {
			AlertasUI.mostrarAlerta(this,
					"No tienes permiso para guardar en esa carpeta.\n\nIntenta usar Documentos o Escritorio.");
			return;
		}

		String[] lineas = texto.split("\n");
		int paginaActual = 1;

		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage(PDRectangle.LETTER);
			document.addPage(page);

			PDPageContentStream content = new PDPageContentStream(document, page);
			content.setFont(PDType1Font.HELVETICA, 11);

			float margin = 50;
			float leading = 14.5f;
			float yPosition = PDRectangle.LETTER.getHeight() - margin;

			// Título y numeración
			dibujarTitulo(content, tituloPDF);
			numerarPagina(content, paginaActual, -1);

			yPosition -= 40;

			for (String linea : lineas) {
				if (yPosition <= margin) {
					content.close();
					paginaActual++;

					page = new PDPage(PDRectangle.LETTER);
					document.addPage(page);
					content = new PDPageContentStream(document, page);
					content.setFont(PDType1Font.HELVETICA, 11);

					dibujarTitulo(content, tituloPDF);
					numerarPagina(content, paginaActual, -1);
					yPosition = PDRectangle.LETTER.getHeight() - margin - 40;
				}

				content.beginText();
				content.newLineAtOffset(margin, yPosition);
				content.showText(limpiarPDF(linea));
				content.endText();
				yPosition -= leading;
			}

			content.close();
			document.save(archivo);

			AlertasUI.mostrarAlerta(this, "PDF exportado correctamente:\n" + archivo.getName());

		} catch (IOException ex) {
			AlertasUI.mostrarAlerta(this, "Error al exportar el PDF:\n" + ex.getMessage());
		}
	}

	private String construirGruposConAhorcados(Set<NumerosDiezMenos> menosDiez, InterfazAhorcado interfaz,
			Set<Integer> seleccionados) {

		List<NumerosDiezMenos> lista = new ArrayList<>(menosDiez);
		lista.sort((a, b) -> Integer.compare(a.getNumeroBase(), b.getNumeroBase()));

		StringBuilder resultado = new StringBuilder();

		for (int i = 0; i < lista.size(); i++) {
			for (int j = i + 1; j < lista.size(); j++) {

				NumerosDiezMenos n1 = lista.get(i);
				NumerosDiezMenos n2 = lista.get(j);

				int a1 = n1.getNumeroDiezMenos();
				int b1 = n1.getNumeroBase();

				int a2 = n2.getNumeroDiezMenos();
				int b2 = n2.getNumeroBase();

				List<Integer> ahorcados = new ArrayList<>();

				List<Integer> numerosGrupo = new ArrayList<>();

				numerosGrupo.add(a1);
				numerosGrupo.add(b1);
				numerosGrupo.add(a2);
				numerosGrupo.add(b2);

				// Extras reales del primer grupo
				agregarSiExiste(numerosGrupo, detectarIzquierda(a1, seleccionados));
				agregarSiExiste(numerosGrupo, detectarDerecha(b1, seleccionados));

				// Extras reales del segundo grupo
				agregarSiExiste(numerosGrupo, detectarIzquierda(a2, seleccionados));
				agregarSiExiste(numerosGrupo, detectarDerecha(b2, seleccionados));

// 🔹 Detectar ahorcados
				for (int x = 0; x < numerosGrupo.size(); x++) {
					for (int y = x + 1; y < numerosGrupo.size(); y++) {

						Integer ah = interfaz.getAhorcadoDeCombinacion(numerosGrupo.get(x), numerosGrupo.get(y));

						if (ah != null && !ahorcados.contains(ah)) {
							ahorcados.add(ah);
						}
					}
				}

// 🔹 Refuerzo vertical
				for (int x = 0; x < numerosGrupo.size(); x++) {
					for (int y = 0; y < numerosGrupo.size(); y++) {

						if (x == y)
							continue;

						if (Math.abs(numerosGrupo.get(x) - numerosGrupo.get(y)) % 10 == 0) {

							Integer ah = interfaz.getAhorcadoDeCombinacion(numerosGrupo.get(x), numerosGrupo.get(y));

							if (ah != null && !ahorcados.contains(ah)) {
								ahorcados.add(ah);
							}
						}
					}
				}

// 🔹 Mostrar solo si hay resultados
				if (!ahorcados.isEmpty()) {

// ===== PRIMER GRUPO =====
					resultado.append("Menos diez: ").append(formatearGrupo(a1, b1, seleccionados)).append("\n");

					resultado.append("Ahorcados:  ");
					for (Integer ah : ahorcados) {
						resultado.append(ah).append(" ");
					}
					resultado.append("\n");

// ===== DETECCIÓN CORRECTA =====
					Integer extraIzq = detectarIzquierda(a2, seleccionados);
					if (extraIzq == null) {
						extraIzq = detectarIzquierda(b2, seleccionados);
					}

					Integer extraDer = detectarDerecha(b2, seleccionados);
					if (extraDer == null) {
						extraDer = detectarDerecha(a2, seleccionados);
					}

// ===== SEGUNDO GRUPO =====
					resultado.append("Menos diez: ").append(formatearGrupo(a2, b2, seleccionados)).append("\n\n");

				}
			}
		}

		return resultado.toString();
	}

	private Integer detectarDerecha(int numero, Set<Integer> seleccionados) {
		int candidato = numero + 20;

		if (candidato > 100) {
			candidato -= 100;
		}

		return seleccionados.contains(candidato) ? candidato : null;
	}

	private Integer detectarIzquierda(int numero, Set<Integer> seleccionados) {
		int candidato = numero - 20;

		if (candidato <= 0) {
			candidato += 100;
		}

		return seleccionados.contains(candidato) ? candidato : null;
	}

	private String formatearGrupo(int a, int b, Set<Integer> seleccionados) {

		Integer extraIzq = detectarIzquierda(a, seleccionados);
		Integer extraDer = detectarDerecha(b, seleccionados);

		StringBuilder grupo = new StringBuilder();

		if (extraIzq != null) {
			grupo.append(extraIzq).append(" ( ) ");
		}

		grupo.append(a).append(" ").append(b);

		if (extraDer != null) {
			grupo.append(" ( ) ").append(extraDer);
		}

		return grupo.toString();
	}

	private void agregarSiExiste(List<Integer> lista, Integer numero) {
		if (numero != null && !lista.contains(numero)) {
			lista.add(numero);
		}
	}

}