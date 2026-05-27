package gui.tabs;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.*;
import java.net.URL;
import java.util.*;

public class DetachableTabbedPane extends JTabbedPane {

	private final Map<String, Component> tabsRegistry = new HashMap<>();
	private final List<JFrame> detachedFrames = new ArrayList<>();
	private boolean draggingDetached = false;
	private int draggedTabIndex = -1;
	private JFrame draggedFrame = null;
	private Point dragOffset = null;

	public DetachableTabbedPane() {

		MouseAdapter adapter = new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				draggedTabIndex = indexAtLocation(e.getX(), e.getY());

				if (draggedTabIndex >= 0) {
					Rectangle r = getBoundsAt(draggedTabIndex);
					dragOffset = new Point(e.getX() - r.x, e.getY() - r.y);
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {

				if (draggedTabIndex < 0)
					return;

				Point screenPoint = e.getLocationOnScreen();

				if (!draggingDetached && !getVisibleRect().contains(e.getPoint())) {
					draggedFrame = detachTab(draggedTabIndex);
					draggingDetached = true;
				}

				if (draggingDetached && draggedFrame != null) {
					draggedFrame.setLocation(screenPoint.x - dragOffset.x, screenPoint.y - 10);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				draggedTabIndex = -1;
				draggingDetached = false;
				draggedFrame = null;
			}
		};

		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}

	// =========================
	// ADD TAB CONTROLADO
	// =========================
	@Override
	public void addTab(String title, Component component) {

		// si ya existe → reemplaza contenido, NO duplica
		if (tabsRegistry.containsKey(title)) {
			int index = indexOfTab(title);

			if (index >= 0) {
				setComponentAt(index, component);
			} else {
				super.addTab(title, component);
			}

			tabsRegistry.put(title, component);
			return;
		}

		super.addTab(title, component);
		tabsRegistry.put(title, component);
	}

	// =========================
	// DETACH TAB
	// =========================
	private JFrame detachTab(int index) {

		Component component = getComponentAt(index);
		String title = getTitleAt(index);
		Icon icon = getIconAt(index);

		removeTabAt(index);

		// eliminar del registro porque ya salió del JTabbedPane
		tabsRegistry.remove(title);

		JFrame frame = new JFrame(title);
		URL iconUrl = getClass().getResource("/gui/favicon.png");

		if (iconUrl != null) {

			ImageIcon icon1 = new ImageIcon(iconUrl);

			frame.setIconImage(icon1.getImage());
		}
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(700, 400);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		panel.add(component);

		frame.add(panel);

		frame.setLocation(MouseInfo.getPointerInfo().getLocation());

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

				detachedFrames.remove(frame);

				// devolver pestaña al tabbedpane
				superAddSafe(title, component);
			}
		});

		detachedFrames.add(frame);
		frame.setVisible(true);

		return frame;
	}

	// =========================
	// REATTACH SEGURO (SIN RECURSIÓN NI DUPLICADOS)
	// =========================
	private void superAddSafe(String title, Component component) {

		SwingUtilities.invokeLater(() -> {
			if (!tabsRegistry.containsKey(title)) {
				super.addTab(title, component);
				tabsRegistry.put(title, component);
			}
		});
	}

	// =========================
	// RESET COMPLETO
	// =========================
	public void resetTabs() {
		super.removeAll();
		tabsRegistry.clear();
	}

	// =========================
	// CIERRE TOTAL
	// =========================
	public void cerrarVentanasDetacadas() {

		for (JFrame f : new ArrayList<>(detachedFrames)) {
			f.dispose();
		}

		detachedFrames.clear();
	}
}