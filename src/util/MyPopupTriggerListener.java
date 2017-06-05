package util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyPopupTriggerListener extends MouseAdapter {
	public MyPopupMenu menu;
	
	public MyPopupTriggerListener(MyPopupMenu menu) {
		this.menu = menu;
	}
	
	public void mousePressed(MouseEvent ev) {
		if (ev.isPopupTrigger()) {
			menu.show(ev.getComponent(), ev.getX(), ev.getY());
		}
	}

	public void mouseReleased(MouseEvent ev) {
		if (ev.isPopupTrigger()) {
			menu.show(ev.getComponent(), ev.getX(), ev.getY());
		}
	}

	public void mouseClicked(MouseEvent ev) {
	}
}