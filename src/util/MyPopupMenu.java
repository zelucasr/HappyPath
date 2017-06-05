package util;

import javax.swing.JPopupMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import model.Grid;

public class MyPopupMenu extends JPopupMenu{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int i, j;
	public MyPopupMenu(int i, int j) {
		super();
		this.i = i;
		this.j = j;
		
		JMenuItem source = new JMenuItem("Set as Source");
		source.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Grid.setSource(i, j);
			}
			
		});
		this.add(source);
		
		JMenuItem destiny = new JMenuItem("Set as Destiny");
		destiny.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Grid.setDestiny(i, j);
			}
		});
		this.add(destiny);
		
		JMenuItem bomb = new JMenuItem("Drop bomb here");
		bomb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Grid.dropBomb(i, j, Grid.bombPower);
			}
		});
		this.add(bomb);
	}
}
