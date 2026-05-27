package gui;

import logic.NumerosDiezMenos;

import logic.RelacionHN;

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

import gui.tabs.DetachableTabbedPane;

public class MostrarMenosDiez extends JFrame {

	private final Color COLOR_PRIMARIO = new Color(37, 99, 235);
	private final Color COLOR_ACENTO = new Color(30, 58, 138);
	private final Color COLOR_FONDO = new Color(240, 240, 240); // 243, 244, 246
	private final Color COLOR_TEXTO = new Color(17, 24, 39);
	private final Color COLOR_TEXTAREAS = new Color(255, 255, 255);

	private MouseAdapter cursorMano;
	private DetachableTabbedPane pestañas;
	private String textoRelaciones = "";

	public MostrarMenosDiez(Set<Integer> numerosSeleccionados, DetachableTabbedPane pestañas) {

		this(numerosSeleccionados, null, 0, pestañas);
	}

	// Constructor
	public MostrarMenosDiez(Set<Integer> numerosSeleccionados, InterfazAhorcado interfaz, int numeroAhorcado,
			DetachableTabbedPane pestañas) {

		this.pestañas = pestañas;
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
		scrollPaneArea.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 0));
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
		JTextArea areaGruposDeCuatro = new JTextArea();
		areaGruposDeCuatro.setEditable(false);
		areaGruposDeCuatro.setFont(new Font("Arial", Font.PLAIN, 16));
		areaGruposDeCuatro.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		areaGruposDeCuatro.setLineWrap(true);
		areaGruposDeCuatro.setWrapStyleWord(true);
		areaGruposDeCuatro.setBackground(COLOR_TEXTAREAS);
		areaGruposDeCuatro.setForeground(COLOR_TEXTO);

		// PROBAR SCROLL
		JScrollPane scrollPaneAreaOtros = new JScrollPane(areaGruposDeCuatro);
		scrollPaneAreaOtros.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 0));
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

			areaGruposDeCuatro.setText("No hay grupos de cuatro para mostrar.");

		} else if (interfaz == null) {

			areaGruposDeCuatro.setText("No hay datos de ahorcados disponibles.");

		} else {

			String resultado = construirGruposConAhorcados(menosDiez, interfaz, numerosSeleccionados);

			if (resultado == null || resultado.trim().isEmpty()) {

				areaGruposDeCuatro.setText("No hay grupos de cuatro para mostrar.");

			} else {

				areaGruposDeCuatro.setText(resultado);

			}
		}

		pestañas.addTab("Grupos de Cuatro", scrollPaneAreaOtros);

		// ===============================
		// NUEVA PESTAÑA RELACIONES
		// ===============================

		JTextArea areaRelaciones = new JTextArea();

		areaRelaciones.setEditable(false);
		areaRelaciones.setFont(new Font("Arial", Font.PLAIN, 16));
		areaRelaciones.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		areaRelaciones.setLineWrap(true);
		areaRelaciones.setWrapStyleWord(true);
		areaRelaciones.setBackground(COLOR_TEXTAREAS);
		areaRelaciones.setForeground(COLOR_TEXTO);

		// MOSTRAR RESULTADOS EN PESTAÑA "RELACIONES"
		textoRelaciones = RelacionHN.generarResultados(numerosSeleccionados);

		if (textoRelaciones.isBlank()) {
			areaRelaciones.setText("No hay relaciones válidas.");
		} else {
			areaRelaciones.setText(textoRelaciones);
		}

		// PROBAR SCROLL
		JScrollPane scrollPaneareaRelaciones = new JScrollPane(areaRelaciones);
		scrollPaneareaRelaciones.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 0));
		scrollPaneareaRelaciones.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneareaRelaciones.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollPaneareaRelaciones.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE)); // ancho
																												// vertical
		scrollPaneareaRelaciones.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, 8)); // alto
		// horizontal

		// Aplicar el scroll moderno
		scrollPaneareaRelaciones.getVerticalScrollBar().setUI(new ModernScrollBarUI());
		scrollPaneareaRelaciones.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

		pestañas.addTab("Relaciones", scrollPaneareaRelaciones);

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

		btnExportarPDF.addActionListener(e -> {

			mostrarDialogoExportacion(area.getText(), areaGruposDeCuatro.getText(), textoRelaciones);

		});

		// PANEL SUPERIOR (TÍTULO + BOTONES)
		JPanel panelSuperior = new JPanel(new BorderLayout());
		panelSuperior.setBackground(COLOR_FONDO);
		panelSuperior.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

		// --------- TÍTULO (IZQUIERDA) ---------
		JLabel lblSubtitleTabla = new JLabel("OPERACIONES");
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

	private boolean intentarExportar(String texto, String titulo) {
		if (texto != null && !texto.trim().isEmpty() && !texto.contains("No hay")) {
			exportarPDF(texto, titulo);
			return true;
		}
		return false;
	}

	private void mostrarDialogoExportacion(String txtMenos10, String txtGrupos, String txtRelaciones) {

		boolean hayMenos10 = txtMenos10 != null && !txtMenos10.trim().isEmpty() && !txtMenos10.contains("No hay");

		boolean hayGrupos = txtGrupos != null && !txtGrupos.trim().isEmpty() && !txtGrupos.contains("No hay");

		boolean hayRelaciones = txtRelaciones != null && !txtRelaciones.trim().isEmpty()
				&& !txtRelaciones.contains("No hay");

		if (!hayMenos10 && !hayGrupos && !hayRelaciones) {

			AlertasUI.mostrarAlerta(this, "No hay resultados disponibles para exportar.");

			return;
		}

		// ===== CREAR DIÁLOGO =====

		JDialog dialogo = new JDialog(this, "Exportar PDF", true);

		URL iconUrl = getClass().getResource("/gui/favicon.png");
		if (iconUrl != null) {
			dialogo.setIconImage(new ImageIcon(iconUrl).getImage());
		}

		dialogo.setSize(420, 320);
		dialogo.setLocationRelativeTo(this);
		dialogo.setResizable(false);

		Container cp = dialogo.getContentPane();
		cp.setBackground(COLOR_FONDO);
		cp.setLayout(new BorderLayout());

		// ===== TÍTULO =====

		JLabel titulo = new JLabel("Seleccionar resultados");

		titulo.setFont(new Font("Arial", Font.BOLD, 18));

		titulo.setForeground(COLOR_TEXTO);

		titulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

		// ===== PANEL CENTRAL =====

		JPanel panelCentro = new JPanel();

		panelCentro.setBackground(COLOR_FONDO);

		panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));

		panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		// ===== CHECKBOX =====

		JCheckBox chkTodo = crearCheckBox("Seleccionar todo");

		JCheckBox chk10 = crearCheckBox("10 Menos");

		JCheckBox chkGrupos = crearCheckBox("Grupos de Cuatro");

		JCheckBox chkRel = crearCheckBox("Relaciones");

		chk10.setEnabled(hayMenos10);
		chkGrupos.setEnabled(hayGrupos);
		chkRel.setEnabled(hayRelaciones);

		chkTodo.addActionListener(ev -> {

			boolean estado = chkTodo.isSelected();

			if (chk10.isEnabled())
				chk10.setSelected(estado);

			if (chkGrupos.isEnabled())
				chkGrupos.setSelected(estado);

			if (chkRel.isEnabled())
				chkRel.setSelected(estado);

		});
		// Distancia entre checkbox
		panelCentro.add(chkTodo);
		panelCentro.add(Box.createVerticalStrut(24));
		panelCentro.add(chk10);
		panelCentro.add(Box.createVerticalStrut(8));
		panelCentro.add(chkGrupos);
		panelCentro.add(Box.createVerticalStrut(8));
		panelCentro.add(chkRel);

		// ===== BOTONES =====

		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

		panelBotones.setBackground(COLOR_FONDO);

		JButton btnExportar = crearBotonDialogo("Exportar");

		JButton btnCancelar = crearBotonDialogo("Cancelar");

		panelBotones.add(btnExportar);
		panelBotones.add(btnCancelar);

		btnCancelar.addActionListener(ev -> dialogo.dispose());

		btnExportar.addActionListener(ev -> {

			boolean algoExportado = false;

			if (chk10.isSelected()) {

				exportarPDF(txtMenos10, "Resultados 10 Menos");

				algoExportado = true;
			}

			if (chkGrupos.isSelected()) {

				exportarPDF(txtGrupos, "Grupos de Cuatro");

				algoExportado = true;
			}

			if (chkRel.isSelected()) {

				exportarPDF(txtRelaciones, "Relaciones");

				algoExportado = true;
			}

			if (!algoExportado) {

				AlertasUI.mostrarAlerta(this, "Seleccione al menos una opción.");

				return;
			}

			dialogo.dispose();

		});

		dialogo.add(titulo, BorderLayout.NORTH);

		dialogo.add(panelCentro, BorderLayout.CENTER);

		dialogo.add(panelBotones, BorderLayout.SOUTH);

		dialogo.setVisible(true);
	}

	private JButton crearBotonDialogo(String texto) {

		JButton boton = new JButton(texto);

		boton.setFont(new Font("Arial", Font.BOLD, 15));

		boton.setBackground(COLOR_PRIMARIO);

		boton.setForeground(COLOR_TEXTAREAS);

		boton.setFocusPainted(false);

		boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		boton.addMouseListener(new MouseAdapter() {

			public void mouseEntered(MouseEvent e) {

				boton.setBackground(COLOR_ACENTO);
			}

			public void mouseExited(MouseEvent e) {

				boton.setBackground(COLOR_PRIMARIO);
			}
		});

		return boton;
	}

	private JCheckBox crearCheckBox(String texto) {

		return new CheckBoxEstilo(texto);
	}

	private class CheckBoxEstilo extends JCheckBox {

		public CheckBoxEstilo(String texto) {
			super(texto);

			setOpaque(false);
			setFocusPainted(false);
			setFont(new Font("Arial", Font.BOLD, 15));
			setForeground(COLOR_TEXTO);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			setPreferredSize(new Dimension(250, 28));
			setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
		}

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int size = 18;
			int y = (getHeight() - size) / 2;

			// Caja exterior
			if (isEnabled()) {
				g2.setColor(COLOR_PRIMARIO);
			} else {
				g2.setColor(Color.GRAY);
			}

			g2.fillRoundRect(0, y, size, size, 8, 8);

			// Estado seleccionado
			if (isSelected()) {

				g2.setColor(Color.WHITE);

				g2.setStroke(new BasicStroke(2.5f));

				g2.drawLine(4, y + 9, 8, y + 13);
				g2.drawLine(8, y + 13, 14, y + 5);
			}

			// Texto
			g2.setColor(isEnabled() ? COLOR_TEXTO : Color.GRAY);

			g2.setFont(getFont());

			FontMetrics fm = g2.getFontMetrics();

			int textX = size + 12;
			int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

			g2.drawString(getText(), textX, textY);

			g2.dispose();
		}
	}
}
