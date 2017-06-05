package util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class MyActionListener implements ActionListener {
	public int i,j;
	public JTextField delay;
	public JTextField bombPower;
	
	public MyActionListener(int i, int j) {
		this.i = i;
		this.j = j;
	}
	
	public MyActionListener(JTextField info, int type) {
		if (type == 0) {
			this.delay = info;
		} else {
			this.bombPower = info;
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
