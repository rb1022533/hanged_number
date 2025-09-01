package gui;

import javax.swing.*;

import java.io.InputStream;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.print.DocFlavor.URL;

public class InterfazBienvenida extends JFrame {
	private final Color COLOR_PRIMARIO = new Color(45, 52, 54); // Gris oscuro profesional
	private final Color COLOR_SECUNDARIO = new Color(149, 165, 166); // Gris medio suave
	private final Color COLOR_ACENTO = new Color(65, 105, 225); // Azul profesional
	private final Color COLOR_FONDO = new Color(242, 242, 242); // Blanco suave
	private BufferedImage imagenFondo;

	public InterfazBienvenida() {
		java.net.URL iconUrl = getClass().getResource("favicon.png");
		System.out.println(iconUrl != null ? "Cargado: " + iconUrl : "No encontrado");

		if (iconUrl != null) {
			ImageIcon icono = new ImageIcon(iconUrl);
			setIconImage(icono.getImage());
		}
//        setIconImage(iconUrl.getFile());

		setTitle("Bienvenido a Ahorcado Numérico");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		try {
			InputStream fondoStream = getClass().getResourceAsStream("fondo.png");
			if (fondoStream == null) {
				System.err.println("No se encontró la imagen de fondo");
			} else {
				imagenFondo = ImageIO.read(fondoStream);
				System.out.println("Imagen de fondo cargada correctamente");
			}
		} catch (IOException e) {
			System.err.println("Error al cargar la imagen de fondo: " + e.getMessage());
			imagenFondo = null;
		}

		// Panel principal con centrado
		JPanel panelPrincipal = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (imagenFondo != null) {
					g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), null);
				}
			}
		};

		panelPrincipal.setLayout(new GridBagLayout());
		panelPrincipal.setBorder(BorderFactory.createEmptyBorder(140, 20, 140, 20));

		// Título del juego
		JLabel titulo = new JLabel("Hanged Number");
		titulo.setFont(new Font("Arial", Font.BOLD, 38));
		titulo.setForeground(COLOR_PRIMARIO);
		titulo.setHorizontalAlignment(JLabel.LEFT);

		// Botón para comenzar el juego
		JButton botonComenzar = new JButton("Comenzar");
		botonComenzar.setFont(new Font("Arial", Font.BOLD, 16));
		botonComenzar.setBackground(COLOR_ACENTO);
		botonComenzar.setForeground(COLOR_FONDO);
		botonComenzar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		botonComenzar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		botonComenzar.setHorizontalAlignment(JLabel.CENTER);


		// Efectos hover para el botón
		botonComenzar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				botonComenzar.setBackground(COLOR_PRIMARIO); // Azul más claro
			}

			@Override
			public void mouseExited(MouseEvent e) {
				botonComenzar.setBackground(COLOR_ACENTO);
			}
		});

		// Acción del botón
		botonComenzar.addActionListener(e -> {
			dispose(); // Cerrar la ventana de bienvenida
			new InterfazAhorcado().setVisible(true); // Abrir la ventana principal
		});

		// Agregar escucha de teclado para Enter
		botonComenzar.registerKeyboardAction(e -> botonComenzar.doClick(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				JComponent.WHEN_FOCUSED);

		// Configuración del título (no cambia)
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 50, -20, 50);
		gbc.gridx = 5;
		gbc.weightx = 5;
		gbc.anchor = GridBagConstraints.LINE_START;

		gbc.gridy = 0;
		gbc.weighty = 1;
		panelPrincipal.add(titulo, gbc);

		// Configuración del botón (ajustada)
		gbc.gridy = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.BASELINE_LEADING;
		gbc.ipadx = 30;
		gbc.ipady = 5;
		panelPrincipal.add(botonComenzar, gbc);

		// Agregar el panel al frame
		add(panelPrincipal);

		// Tamaño y posición de la ventana
		setSize(700, 500);
		setLocationRelativeTo(null);

		// Solicitar el foco inicial al botón
		SwingUtilities.invokeLater(() -> botonComenzar.requestFocusInWindow());
	}

	private void setIconImage(String file) {
		// TODO Auto-generated method stub

	}
}
