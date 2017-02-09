package utility;

import java.awt.Color;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;

import dataStructures.Bag;

public class SettingPage {

	private JFrame frmManip;

	/**
	 * Create the application.
	 */
	public SettingPage(boolean[] opt) {
		initialize(opt);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(final boolean[] options) {
		setFrmManip(new JFrame());
		getFrmManip().setType(Type.POPUP);
		getFrmManip().setResizable(false);
		getFrmManip().getContentPane().setBackground(new Color(245, 245, 245));
		getFrmManip().setBounds(100, 100, 225, 424);
		getFrmManip().getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panel.setBounds(10, 11, 199, 318);
		getFrmManip().getContentPane().add(panel);
		panel.setLayout(null);
		
		final Bag<JRadioButton> buttonBag = new Bag<JRadioButton>();
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Pick List Manipulation");
		rdbtnNewRadioButton.setSelected(false);
		rdbtnNewRadioButton.setBounds(6, 7, 187, 23);
		panel.add(rdbtnNewRadioButton);
		buttonBag.add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Standardize Names");
		rdbtnNewRadioButton_1.setSelected(false);
		rdbtnNewRadioButton_1.setBounds(6, 33, 187, 23);
		panel.add(rdbtnNewRadioButton_1);
		buttonBag.add(rdbtnNewRadioButton_1);
		
		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("IM Match");
		rdbtnNewRadioButton_2.setSelected(false);
		rdbtnNewRadioButton_2.setBounds(6, 59, 187, 23);
		panel.add(rdbtnNewRadioButton_2);
		buttonBag.add(rdbtnNewRadioButton_2);
		
		JRadioButton rdbtnNewRadioButton_3 = new JRadioButton("Remove Concept Parts");
		rdbtnNewRadioButton_3.setSelected(false);
		rdbtnNewRadioButton_3.setBounds(6, 85, 187, 23);
		panel.add(rdbtnNewRadioButton_3);
		buttonBag.add(rdbtnNewRadioButton_3);
		
		JRadioButton rdbtnNewRadioButton_4 = new JRadioButton("Remove Item Type 4 Parts");
		rdbtnNewRadioButton_4.setSelected(false);
		rdbtnNewRadioButton_4.setBounds(6, 111, 187, 23);
		panel.add(rdbtnNewRadioButton_4);
		buttonBag.add(rdbtnNewRadioButton_4);
		
		JRadioButton rdbtnNewRadioButton_5 = new JRadioButton("Add Duplicates");
		rdbtnNewRadioButton_5.setSelected(false);
		rdbtnNewRadioButton_5.setBounds(6, 137, 187, 23);
		panel.add(rdbtnNewRadioButton_5);
		buttonBag.add(rdbtnNewRadioButton_5);
		
		JRadioButton rdbtnNewRadioButton_6 = new JRadioButton("Add Material Children");
		rdbtnNewRadioButton_6.setSelected(false);
		rdbtnNewRadioButton_6.setBounds(6, 163, 187, 23);
		panel.add(rdbtnNewRadioButton_6);
		buttonBag.add(rdbtnNewRadioButton_6);
		
		JRadioButton rdbtnNewRadioButton_7 = new JRadioButton("Add Payment Children");
		rdbtnNewRadioButton_7.setSelected(false);
		rdbtnNewRadioButton_7.setBounds(6, 189, 187, 23);
		panel.add(rdbtnNewRadioButton_7);
		buttonBag.add(rdbtnNewRadioButton_7);
		
		JRadioButton rdbtnNewRadioButton_8 = new JRadioButton("Change Temp ID's");
		rdbtnNewRadioButton_8.setSelected(false);
		rdbtnNewRadioButton_8.setBounds(6, 213, 187, 23);
		panel.add(rdbtnNewRadioButton_8);
		buttonBag.add(rdbtnNewRadioButton_8);
		
		JRadioButton rdbtnNewRadioButton_9 = new JRadioButton("Add Weld Children");
		rdbtnNewRadioButton_9.setSelected(false);
		rdbtnNewRadioButton_9.setBounds(6, 239, 187, 23);
		panel.add(rdbtnNewRadioButton_9);
		buttonBag.add(rdbtnNewRadioButton_9);
		
		JRadioButton rdbtnNewRadioButton_10 = new JRadioButton("Scrap Factor Manipulation");
		rdbtnNewRadioButton_10.setSelected(false);
		rdbtnNewRadioButton_10.setBounds(6, 265, 187, 23);
		panel.add(rdbtnNewRadioButton_10);
		buttonBag.add(rdbtnNewRadioButton_10);
		
		JRadioButton rdbtnNewRadioButton_11 = new JRadioButton("Special Case Removal");
		rdbtnNewRadioButton_11.setSelected(false);
		rdbtnNewRadioButton_11.setBounds(6, 291, 187, 23);
		panel.add(rdbtnNewRadioButton_11);
		buttonBag.add(rdbtnNewRadioButton_11);
		
		int counter = 0;
		for (JRadioButton b : buttonBag) {
			if (options[counter]) {
				b.setSelected(true);
			}
			counter++;
		}
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panel_1.setBounds(10, 340, 199, 45);
		getFrmManip().getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JButton btnNewButton = new JButton("Apply");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int counter = 0;
				for (JRadioButton b : buttonBag) {
					if (b.isSelected()) {
						options[counter] = true;
					} else {
						options[counter] = false;
					}
					counter++;
				}
				getFrmManip().setVisible(false);
			}
		});
		btnNewButton.setBounds(10, 11, 179, 23);
		panel_1.add(btnNewButton);
	}

	public JFrame getFrmManip() {
		return frmManip;
	}

	public void setFrmManip(JFrame frmManip) {
		this.frmManip = frmManip;
	}

}
