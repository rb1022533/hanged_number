package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AlertasUI {
	private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
	private static final Color COLOR_FONDO = new Color(243, 244, 246);
	private static final Color COLOR_ACENTO = new Color(30, 58, 138); // Hover

	public static void mostrarAlerta(Component parent, String mensaje) {

		// Cursor de mano
		MouseAdapter cursorMano = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				((JComponent) e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				((JComponent) e.getSource()).setCursor(Cursor.getDefaultCursor());
			}
		};

		JButton botonCerrar = new JButton("OK");
		botonCerrar.setFont(new Font("Arial", Font.BOLD, 14));
		botonCerrar.setBackground(COLOR_PRIMARIO);
		botonCerrar.setForeground(COLOR_FONDO);
		botonCerrar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		botonCerrar.setFocusPainted(false);
		botonCerrar.addMouseListener(cursorMano);

		// Hover
		botonCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				botonCerrar.setBackground(COLOR_ACENTO); // cambia al color hover
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				botonCerrar.setBackground(COLOR_PRIMARIO); // vuelve al color original
			}

		});

		Object[] options = { botonCerrar };

		JOptionPane pane = new JOptionPane(mensaje, JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
				options, options[0]);

		JDialog dialog = pane.createDialog(parent, "Atención");
		botonCerrar.addActionListener(e -> dialog.dispose());
		dialog.setVisible(true);
	}

}
