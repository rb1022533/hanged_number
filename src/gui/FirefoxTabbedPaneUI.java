package gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class FirefoxTabbedPaneUI extends BasicTabbedPaneUI {

	private int hoverTab = -1;

	private final Color COLOR_ACTIVA = new Color(255, 255, 255);
	private final Color COLOR_INACTIVA = new Color(240, 240, 240);
	private final Color COLOR_HOVER = new Color(200, 200, 200);
	private final Color COLOR_TEXTO = new Color(30, 30, 30);
	private final Color COLOR_AZUL = new Color(37, 99, 235); // reemplaza con tu azul exacto

	private final int SEPARACION = 8;

	@Override
	protected void installDefaults() {
		super.installDefaults();
		tabAreaInsets = new Insets(5, 5, 5, 5);
		// Este es el espacio total alrededor de las pestañas
		tabInsets = new Insets(5, 15, 5, 15);
		// Este es el padding dentro de cada pestaña
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		// 🔹 Hover dinámico para pestañas inactivas
		tabPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {

				if (tabPane == null || tabPane.getTabCount() == 0) return;

			    int index = tabForCoordinate(tabPane, e.getX(), e.getY());
			    int selected = tabPane.getSelectedIndex();

			    if (index == selected || index == -1) {
			        hoverTab = -1;
			    } else {
			        hoverTab = index;
			    }

			    if (tabPane != null && tabPane.getTabCount() > 0) {
			        tabPane.repaint();
			    }
			}

		});

		// 🔹 Cuando el mouse sale del JTabbedPane, quitar hover
		tabPane.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				hoverTab = -1; // quitar hover
				if (tabPane != null) {
				    tabPane.repaint();
				}
			}
		});

		// 🔹 Activar navegación por teclado
		installKeyboardNavigation();
	}

	// 🔴 NO PINTAMOS LA BARRA
	@Override
	protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
		super.paintTabArea(g, tabPlacement, selectedIndex);
	}

	@Override
	protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
			boolean isSelected) {

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int spacing = SEPARACION;
		int drawX = x + spacing / 2;
		int drawW = w - spacing;
		int drawY = y + 2;
		int drawH = h - 2;

		// 🔹 Dibujar sombra solo si es la pestaña activa
//		if (isSelected) {
//			Color shadowColor = new Color(0, 0, 0, 30);
//			g2.setColor(shadowColor);
//			int shadowOffsetX = 2;
//			int shadowOffsetY = 2;
//			g2.fillRect(drawX + shadowOffsetX, drawY + shadowOffsetY, drawW, drawH);
//		}

		// Fondo de la pestaña
		Color color;
		if (tabIndex == hoverTab) {
			color = COLOR_HOVER;

		} else {
			color = COLOR_INACTIVA;
		}
		g2.setColor(color);
		g2.fillRect(drawX, drawY, drawW, drawH);

		// 🔹 Franja azul al final (si la pestaña está activa)
		if (isSelected) {
			String title = tabPane.getTitleAt(tabIndex);
			FontMetrics metrics = g.getFontMetrics(tabPane.getFont());
			int textWidth = metrics.stringWidth(title);

			// Centrar la franja debajo del texto
			int stripeHeight = 3;
			int stripeX = drawX + (drawW - textWidth) / 2;
			int stripeY = drawY + drawH - stripeHeight;

			g2.setColor(COLOR_AZUL);
			g2.fillRect(stripeX, stripeY, textWidth, stripeHeight);
		}
	}

	// 🔴 Eliminamos TODOS los bordes
	@Override
	protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
		// No pintar nada
	}

	@Override
	protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
			Rectangle iconRect, Rectangle textRect, boolean isSelected) {
		// Nada
	}

	@Override
	protected Insets getContentBorderInsets(int tabPlacement) {
		return new Insets(0, 0, 0, 0);
	}

	@Override
	protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title,
			Rectangle textRect, boolean isSelected) {

		g.setFont(font);
		g.setColor(COLOR_TEXTO);

// 🔹 Calcula ancho y alto del texto
		int textWidth = metrics.stringWidth(title);
		int textHeight = metrics.getHeight();

// 🔹 Calcula el rectángulo de la pestaña pintada
		Rectangle tabRect = getTabBounds(tabPane, tabIndex); // rect original
		int spacing = SEPARACION;

		int x = tabRect.x + spacing / 2 + (tabRect.width - spacing - textWidth) / 2;
		int y = tabRect.y + (tabRect.height - textHeight) / 2 + metrics.getAscent();

		g.drawString(title, x, y);
	}

	@Override
	protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
		int base = super.calculateTabWidth(tabPlacement, tabIndex, metrics);
		return base + SEPARACION; // misma separación para todas
	}

	@Override
	protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
		int base = super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);
		return base + 4; // altura uniforme
	}

	@Override
	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
			boolean isSelected) {
		// NO dibujar borde nativo
	}

	@Override
	protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w,
			int h) {
	}

	@Override
	protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w,
			int h) {
	}

	@Override
	protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w,
			int h) {
	}

	@Override
	protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w,
			int h) {
	}

	private void installKeyboardNavigation() {
		InputMap im = tabPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW); // <-- cambio aquí
		ActionMap am = tabPane.getActionMap();

		// Ctrl+Tab -> siguiente pestaña
		im.put(KeyStroke.getKeyStroke("ctrl TAB"), "nextTab");
		am.put("nextTab", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int next = (tabPane.getSelectedIndex() + 1) % tabPane.getTabCount();
				tabPane.setSelectedIndex(next);
			}
		});

		// Ctrl+Shift+Tab -> pestaña anterior
		im.put(KeyStroke.getKeyStroke("ctrl shift TAB"), "previousTab");
		am.put("previousTab", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int prev = (tabPane.getSelectedIndex() - 1 + tabPane.getTabCount()) % tabPane.getTabCount();
				tabPane.setSelectedIndex(prev);
			}
		});
	}
}
