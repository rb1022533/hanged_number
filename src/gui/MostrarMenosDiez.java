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
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

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
		area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		area.setLineWrap(true);
		area.setFont(new Font("Arial", Font.PLAIN, 16));
		area.setEditable(false);
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
		pestañas.setFocusable(true);

		UIManager.put("TabbedPane.contentOpaque", false);

		// Pestaña 1 - Menos Diez
		pestañas.addTab("10 Menos", scrollPaneArea);

		// Pestaña 2 - Otros resultados (por ahora vacía)
		JTextArea areaOtros = new JTextArea();
		areaOtros.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		areaOtros.setEditable(false);
		areaOtros.setFont(new Font("Arial", Font.PLAIN, 16));
		areaOtros.setLineWrap(true);
		areaOtros.setWrapStyleWord(true);
		areaOtros.setBackground(COLOR_TEXTAREAS);
		areaOtros.setForeground(COLOR_TEXTO);

		StringBuilder sbOtros = new StringBuilder();

		// Ordenamos correctamente la lista
		List<NumerosDiezMenos> listaOrdenada = new ArrayList<>(menosDiez);
		listaOrdenada.sort(Comparator.comparingInt(NumerosDiezMenos::getNumeroDiezMenos));

		// Detectar grupos de cuatro en la misma línea
		Set<Integer> usadosEnGrupoCuatro = new java.util.HashSet<>();

		for (int i = 0; i < listaOrdenada.size(); i++) {

			NumerosDiezMenos par1 = listaOrdenada.get(i);

			for (int j = i + 1; j < listaOrdenada.size(); j++) {

				NumerosDiezMenos par2 = listaOrdenada.get(j);

				int diff1 = par2.getNumeroDiezMenos() - par1.getNumeroDiezMenos();
				int diff2 = par2.getNumeroBase() - par1.getNumeroBase();

				// misma línea vertical
				if (Math.abs(diff1) == Math.abs(diff2) && diff1 % 10 == 0) {

					// Caso 1
					int ref1 = par1.getNumeroBase();
					int diezMenos2 = par2.getNumeroDiezMenos();

					int diffA = Math.abs(diezMenos2 - ref1);
					int casillasA = (diffA / 10) - 1;

					// Caso 2
					int ref2 = par2.getNumeroBase();
					int diezMenos1 = par1.getNumeroDiezMenos();

					int diffB = Math.abs(diezMenos1 - ref2);
					int casillasB = (diffB / 10) - 1;

					if (par1.getNumeroBase() % 10 != par2.getNumeroBase() % 10) {
						continue;
					}

					if ((casillasA == 3 || casillasA == 5) || (casillasB == 3 || casillasB == 5)
							|| (casillasA == 1 && casillasB == 5) || (casillasB == 1 && casillasA == 5)) {

						usadosEnGrupoCuatro.add(par1.getNumeroDiezMenos());
						usadosEnGrupoCuatro.add(par1.getNumeroBase());

						usadosEnGrupoCuatro.add(par2.getNumeroDiezMenos());
						usadosEnGrupoCuatro.add(par2.getNumeroBase());

						sbOtros.append("Menos diez:   ").append(par1.getNumeroDiezMenos()).append("  ")
								.append(par1.getNumeroBase()).append("\n");

						/* calcular ahorcados */
						int a = par1.getNumeroBase();
						int b = par2.getNumeroDiezMenos();

						// punto medio vertical
						int ahorcado1 = (a + b) / 2;

						// segundo ahorcado (50 más abajo)
						int ahorcado2 = ahorcado1 + 50;
						if (ahorcado2 > 100) {
							ahorcado2 -= 100;
						}

						sbOtros.append("Ahorcados:     ");

						boolean imprimirAlgo = false;

						if (!numerosSeleccionados.contains(ahorcado1)) {
							sbOtros.append(ahorcado1);
							imprimirAlgo = true;
						}

						if (!numerosSeleccionados.contains(ahorcado2)) {
							if (imprimirAlgo) {
								sbOtros.append(" ");
							}
							sbOtros.append(ahorcado2);
							imprimirAlgo = true;
						}

						// -------- NUEVO AHORCADO POR RELACIÓN ±20 --------

						int baseActual = par1.getNumeroBase();
						int numeroMas40 = baseActual + 40;
						int ahorcadoExtra = baseActual + 20;

						if (numerosSeleccionados.contains(numeroMas40)) {

							if (!numerosSeleccionados.contains(ahorcadoExtra) && ahorcadoExtra != ahorcado1
									&& ahorcadoExtra != ahorcado2) {

								if (imprimirAlgo) {
									sbOtros.append(" ");
								}

								sbOtros.append(ahorcadoExtra);
								imprimirAlgo = true;
							}
						}

						sbOtros.append("\n");

						int extraIzquierda = par2.getNumeroDiezMenos() - 20;
						int extraDerecha = par2.getNumeroBase() + 20;

						boolean tieneIzquierda = numerosSeleccionados.contains(extraIzquierda);
						boolean tieneDerecha = numerosSeleccionados.contains(extraDerecha);

						sbOtros.append("Menos diez:   ");

						if (tieneIzquierda) {
							sbOtros.append(extraIzquierda).append("  ( )  ");
						}

						sbOtros.append(par2.getNumeroDiezMenos()).append("  ").append(par2.getNumeroBase());

						if (tieneDerecha) {
							sbOtros.append("  ( )  ").append(extraDerecha);
						}

						sbOtros.append("\n\n");
					}
				}
			}
		}

		Set<Integer> paresEnGrupo = new java.util.HashSet<>();

		for (int i = 0; i < listaOrdenada.size() - 1; i++) {
			NumerosDiezMenos actual = listaOrdenada.get(i);

			for (int j = i + 1; j < listaOrdenada.size(); j++) {
				NumerosDiezMenos siguiente = listaOrdenada.get(j);

				int diferencia = siguiente.getNumeroDiezMenos() - actual.getNumeroDiezMenos();

				if (diferencia == 2 || diferencia == 4 || diferencia == 6) {

					paresEnGrupo.add(actual.getNumeroDiezMenos());
					paresEnGrupo.add(siguiente.getNumeroDiezMenos());

				}
			}
		}

		for (int i = 0; i < listaOrdenada.size() - 1; i++) {
			NumerosDiezMenos actual = listaOrdenada.get(i);

			for (int j = i + 1; j < listaOrdenada.size(); j++) {
				NumerosDiezMenos siguiente = listaOrdenada.get(j);

				int diferencia = siguiente.getNumeroDiezMenos() - actual.getNumeroDiezMenos();

				if (diferencia > 6)
					break;

				// válido cuando hay 1, 3 o 5 números entre ellos
				if (diferencia == 2 || diferencia == 4 || diferencia == 6) {

					paresEnGrupo.add(actual.getNumeroDiezMenos());
					paresEnGrupo.add(siguiente.getNumeroDiezMenos());

					int numeroMedio = actual.getNumeroDiezMenos() + (diferencia / 2);
					int baseMedio = actual.getNumeroBase() + (diferencia / 2);

					int numeroDiezMenos = siguiente.getNumeroDiezMenos();
					int numeroBase = siguiente.getNumeroBase();

					int extraIzquierda = numeroDiezMenos - 20; // a la izquierda
					int extraDerecha = numeroBase + 20; // a la derecha

					boolean medioSeleccionado = numerosSeleccionados.contains(numeroMedio);
					boolean baseMedioSeleccionado = numerosSeleccionados.contains(baseMedio);

					boolean extraDerechaSeleccionado = numerosSeleccionados.contains(extraDerecha);

					boolean extraSeleccionado = false;

					if (extraDerechaSeleccionado) {
						int ahorcadoExtra1 = extraDerecha - 11;
						extraSeleccionado = numerosSeleccionados.contains(ahorcadoExtra1);
					}

					// ❌ Si todos están seleccionados, no hay ahorcados
					if (medioSeleccionado && baseMedioSeleccionado && extraSeleccionado) {
						continue;
					}

					boolean tieneExtraIzquierda = numerosSeleccionados.contains(extraIzquierda);

					// ----------------------------
					// 1️⃣ Bloque “actual” (solo números alineados)
					sbOtros.append("Menos diez:   ");
					if (tieneExtraIzquierda) {
						sbOtros.append("           "); // espacios para empujar números a la derecha
					}
					sbOtros.append(actual.getNumeroDiezMenos()).append("  ").append(actual.getNumeroBase())
							.append("\n");

					sbOtros.append("Ahorcados:    ");
					if (tieneExtraIzquierda) {
						sbOtros.append("           ");
					}

					if (!medioSeleccionado && !baseMedioSeleccionado) {
						sbOtros.append(numeroMedio).append("  ").append(baseMedio);
					} else if (!medioSeleccionado) {
						sbOtros.append(numeroMedio);
					} else if (!baseMedioSeleccionado) {
						sbOtros.append("      ").append(baseMedio);
					}

					// 🔵 Ahorcado -11 solo si diferencia == 2
					if (diferencia == 2 && numerosSeleccionados.contains(extraDerecha)) {
						int ahorcadoExtra = extraDerecha - 11;
						if (!numerosSeleccionados.contains(ahorcadoExtra)) {

							// Calcular espacios exactos hasta extraDerecha
							// La línea "Menos diez" siguiente tendrá:
							// "Menos diez: " + posible extraIzquierda + numeroDiezMenos + numeroBase + " (
							// ) " + extraDerecha
							// Queremos que -11 quede alineado sobre "( )"
							int baseLength = "Menos diez:   ".length();
							if (tieneExtraIzquierda)
								baseLength += 12; // espacio extra izquierda
							baseLength += String.valueOf(numeroDiezMenos).length() + 3; // +2 por espacio
							baseLength += String.valueOf(numeroBase).length() + 3; // +2 por espacio antes de ( )

							// Ahora agregamos espacios hasta llegar a esa posición
							int currentLength = sbOtros.length() - sbOtros.lastIndexOf("\n") - 1;
							int espacios = baseLength - currentLength;
							for (int s = 0; s < espacios; s++)
								sbOtros.append(" ");

							sbOtros.append(ahorcadoExtra);
						}
					}

					sbOtros.append("\n");

					// ----------------------------
					// 2️⃣ Línea siguiente con extras
					sbOtros.append("Menos diez:   ");

					boolean tieneIzquierda = numerosSeleccionados.contains(extraIzquierda);
					boolean tieneDerecha = numerosSeleccionados.contains(extraDerecha);

					if (tieneIzquierda) {
						sbOtros.append(extraIzquierda).append("  ( )  ");
					}

					sbOtros.append(numeroDiezMenos).append("  ").append(numeroBase);

					if (tieneDerecha) {
						sbOtros.append("  ( )  ").append(extraDerecha);
					}

					sbOtros.append("\n\n");
				}
			}
		}

		for (NumerosDiezMenos num : listaOrdenada) {

			int diezMenos = num.getNumeroDiezMenos();
			int base = num.getNumeroBase();

			if (paresEnGrupo.contains(diezMenos) || usadosEnGrupoCuatro.contains(diezMenos)
					|| usadosEnGrupoCuatro.contains(base)) {
				continue;
			}

			int extraIzquierda = diezMenos - 20;
			int extraDerecha = base + 20;

			boolean tieneIzquierda = numerosSeleccionados.contains(extraIzquierda);
			boolean tieneDerecha = numerosSeleccionados.contains(extraDerecha);

			sbOtros.append("Menos diez:   ");

			if (tieneIzquierda) {
				sbOtros.append(extraIzquierda).append("  ").append("( ) ");
			}

			sbOtros.append(diezMenos).append("  ").append(base);

			if (tieneDerecha) {
				sbOtros.append("  ( ) ").append(extraDerecha);
			}

			sbOtros.append("\n\n");
		}

		if (sbOtros.length() == 0) {
			areaOtros.setText("No hay grupos de cuatro con ahorcados.");
		} else {
			areaOtros.setText(sbOtros.toString());
		}

		JScrollPane scrollOtros = new JScrollPane(areaOtros);
		scrollOtros.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		scrollOtros.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollOtros.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollOtros.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE)); // ancho vertical
		scrollOtros.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, 8)); // alto
																									// horizontal

		// Aplicar el scroll moderno
		scrollOtros.getVerticalScrollBar().setUI(new ModernScrollBarUI());
		scrollOtros.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

		pestañas.addTab("Grupos de a Cuatro", scrollOtros);

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
			exportarPDF(areaOtros.getText(), "Grupos de a Cuatro");
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

	}

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

}