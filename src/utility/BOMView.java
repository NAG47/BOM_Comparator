package utility;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;

import au.com.bytecode.opencsv.CSVReader;
import core.Manipulator;
import core.BOM.*;
import core.Part.Part;
import dataStructures.Bag;
import dataStructures.Node;
import dataStructures.Queue;

import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Choice;

import javax.swing.JProgressBar;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JCheckBox;
/**
 * @author Nuan
 * @version 1.0
 */
public class BOMView {

	private JFrame frm;
	private JTree WindChilltree, AS400tree;
	private JLabel prtNumlbl, prtNmlbl, prtPoslbl, prtIMlbl, prtQtylbl, prtTplbl, prtMatNumlbl, prtMatQtylbl;
	private File IMdirec, AS400direc, WCdirec, SFdirec;
	private JTextField IM_txtField;
	private JTextField AS400txtfield;
	private JTextField windChilltxtfield;
	private JTextField Scrap_txtField;
	private JTextField Ratio_txtField;
	private JTextField SpecialCase_txtField;
	private boolean[] options;
	private BOM b, d;
	private Manipulator manip;
	private Choice choice;
	private Process process;
	private JProgressBar progressBar;
	private JCheckBox chckbxmntmxWcMode,chckbxNewCheckBox;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BOMView view = new BOMView();
					view.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public String[] readAppData() throws FileNotFoundException{
		String user = System.getProperty("user.home");
		File f = new File(user + "/Desktop/BOM_Comparator/data.txt");
		Scanner scanner = new Scanner(f);
		String[] dat = scanner.nextLine().split("-,-");
		scanner.close();
		f = new File(user + "/Desktop/BOM_Comparator/Stuff/ver.txt");
		scanner = new Scanner(f);
		frm.setTitle("BOM Comparator - " + scanner.nextLine());
		scanner.close();
		
		IMdirec = new File(dat[0]).getParentFile();
		AS400direc = new File(dat[5]).getParentFile();
		WCdirec = new File(dat[6]).getParentFile();
		SFdirec = new File(dat[1]).getParentFile();
		return dat;
	}
	
	@SuppressWarnings("serial")
	public void updateTree(int num){
		if (num == 0) {
			//Sorter s = new Sorter(b.getRoot().adj);
			//b.getRoot().adj = s.mergeSort();
			AS400tree.setRootVisible(true);
			AS400tree.setModel(new DefaultTreeModel(
					new DefaultMutableTreeNode(b.getRoot().item) {
						{
							Queue<Node<Part>> q = new Queue<Node<Part>>();
							for (Node<Part> p : b.getRoot().adj) {
								q.enqueue(p);
							}
							
							while(!q.isEmpty()){
								Node<Part> current = q.dequeue();
								current.treeNode = new DefaultMutableTreeNode(current.item);
								if (current.item.getPos() != 1) {
									current.edgedTo.treeNode.add(current.treeNode);
								} else add(current.treeNode);
								
								for (Node<Part> p : current.adj){
									q.enqueue(p);
								}
							}
						}
					}
				));
			
		} else if (num == 1){
			WindChilltree.setRootVisible(true);
			Sorter s = new Sorter(d.getRoot().adj);
			d.getRoot().adj = s.mergeSort();
			WindChilltree.setModel(new DefaultTreeModel(
					new DefaultMutableTreeNode(d.getRoot().item) {
						{
							Queue<Node<Part>> q = new Queue<Node<Part>>();
							for (Node<Part> p : d.getRoot().adj) {
								q.enqueue(p);
							}
							
							while(!q.isEmpty()){
								Node<Part> current = q.dequeue();
								current.treeNode = new DefaultMutableTreeNode(current.item);
								if (current.item.getPos() != 1) {
									current.edgedTo.treeNode.add(current.treeNode);
								} else add(current.treeNode);
								
								for (Node<Part> p : current.adj){
									q.enqueue(p);
								}
							}
						}
					}
				));
		}
		
	}
	
	public void writeAppData() throws IOException{
		String user = System.getProperty("user.home");
		File f = new File(user + "/Desktop/BOM_Comparator/data.txt");
		FileWriter fw = new FileWriter(f);
		PrintWriter pw = new PrintWriter(fw);
		pw.print(IM_txtField.getText() + "-,-" + Scrap_txtField.getText() + "-,-" + Ratio_txtField.getText() + "-,-" + SpecialCase_txtField.getText() + "-,-" + choice.getSelectedItem() + "-,-" + AS400txtfield.getText() + "-,-" + windChilltxtfield.getText() + "-,-end");
		pw.flush();
		pw.close();
	}
	/**
	 * Create the application.
	 * @throws FileNotFoundException 
	 */
	public BOMView() throws FileNotFoundException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("serial")
	private void initialize() throws FileNotFoundException {
		options = new boolean[12];
		for (int i = 0; i < options.length; i++) {
			options[i] = true;
		}
		
		setFrame(new JFrame());
		String[] dat = readAppData();
		getFrame().setBounds(100, 100, 1149, 578);
		
		JScrollPane scrollPane = new JScrollPane();
		
		AS400tree = new JTree();
		AS400tree.setRootVisible(false);
		AS400tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("AS400 BOM") {
				{
				}
			}
		));
		AS400tree.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		
		AS400tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) AS400tree.getLastSelectedPathComponent();
				
				if (node == null) return;
				
				Part nodeItem = (Part) node.getUserObject();
				
				prtNumlbl.setText(nodeItem.getID());
				prtNmlbl.setText(nodeItem.getName());
				prtPoslbl.setText(nodeItem.getPos() + "");
				prtQtylbl.setText(nodeItem.getQty() + "");
				prtIMlbl.setText(nodeItem.getMKNum());
				prtTplbl.setText(nodeItem.getClass().getSimpleName());
				prtMatNumlbl.setText(nodeItem.getMatNum());
				prtMatQtylbl.setText(nodeItem.getMatQty() + "");
			}
		});
		scrollPane.setViewportView(AS400tree);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);
		menuBar.setBounds(0, 0, 100, 21);
		frm.getContentPane().add(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmInput = new JMenuItem("Input");
		mntmInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String user = System.getProperty("user.home");
				try {
					Desktop.getDesktop().open(new File(user + "/Desktop/BOM_Comparator/Input"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		mnNewMenu.add(mntmInput);
		
		JMenuItem mntmOutput = new JMenuItem("Output");
		mntmOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String user = System.getProperty("user.home");
				try {
					Desktop.getDesktop().open(new File(user + "/Desktop/BOM_Comparator/Output/Upload"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		mnNewMenu.add(mntmOutput);
		
		JMenu mnConfigure = new JMenu("Configure");
		menuBar.add(mnConfigure);
		
		JMenuItem mntmManipulations = new JMenuItem("Manipulations");
		mntmManipulations.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							SettingPage window = new SettingPage(options);
							window.getFrmManip().setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		chckbxmntmxWcMode = new JCheckBox("2x WC Mode");
		mnConfigure.add(chckbxmntmxWcMode);
		
		chckbxNewCheckBox = new JCheckBox("Populate BOM");
		mnConfigure.add(chckbxNewCheckBox);
		mnConfigure.add(mntmManipulations);
		
		JLabel lblAsBom = new JLabel("AS400 BOM");
		lblAsBom.setHorizontalAlignment(SwingConstants.CENTER);
		lblAsBom.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panel.setLayout(null);
		
		JLabel lblPartNumber = new JLabel("Part Number:");
		lblPartNumber.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPartNumber.setBounds(10, 11, 94, 14);
		panel.add(lblPartNumber);
		
		JLabel lblPartName = new JLabel("Part Name:");
		lblPartName.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPartName.setBounds(10, 36, 94, 14);
		panel.add(lblPartName);
		
		JLabel lblPartPosition = new JLabel("Part Position:");
		lblPartPosition.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPartPosition.setBounds(10, 61, 94, 14);
		panel.add(lblPartPosition);
		
		prtNumlbl = new JLabel("");
		prtNumlbl.setBounds(114, 12, 216, 14);
		panel.add(prtNumlbl);
		
		prtNmlbl = new JLabel("");
		prtNmlbl.setFont(new Font("Tahoma", Font.PLAIN, 9));
		prtNmlbl.setBounds(114, 37, 216, 14);
		panel.add(prtNmlbl);
		
		prtPoslbl = new JLabel("");
		prtPoslbl.setBounds(114, 62, 216, 14);
		panel.add(prtPoslbl);
		
		JLabel lblQuantity = new JLabel("Quantity:");
		lblQuantity.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblQuantity.setBounds(10, 113, 94, 14);
		panel.add(lblQuantity);
		
		JLabel lblItemMasterNum = new JLabel("IM Num:");
		lblItemMasterNum.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblItemMasterNum.setBounds(10, 138, 94, 14);
		panel.add(lblItemMasterNum);
		
		prtQtylbl = new JLabel("");
		prtQtylbl.setBounds(114, 113, 216, 14);
		panel.add(prtQtylbl);
		
		prtIMlbl = new JLabel("");
		prtIMlbl.setBounds(114, 138, 216, 14);
		panel.add(prtIMlbl);
		
		JLabel lblPartType = new JLabel("Type:");
		lblPartType.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPartType.setBounds(10, 86, 94, 14);
		panel.add(lblPartType);
		
		prtTplbl = new JLabel("");
		prtTplbl.setBounds(114, 88, 216, 14);
		panel.add(prtTplbl);
		
		JLabel lblPart = new JLabel("Part View");
		lblPart.setHorizontalAlignment(SwingConstants.CENTER);
		lblPart.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		
		JLabel label = new JLabel("IM:");
		
		IM_txtField = new JTextField();
		IM_txtField.setText(dat[0]);
		IM_txtField.setColumns(10);
		
		JLabel label_1 = new JLabel("AS400:");
		
		AS400txtfield = new JTextField();
		AS400txtfield.setColumns(10);
		
		windChilltxtfield = new JTextField();
		windChilltxtfield.setColumns(10);
		
		JButton button = new JButton("browse");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				if (WCdirec == null) {
					String user = System.getProperty("user.home");
					fileChooser.setCurrentDirectory(new File(user + "/Desktop/BOM_Comparator/Input/BOMs/WindChill BOM"));
				} else {
					fileChooser.setCurrentDirectory(WCdirec);
				}
				fileChooser.setDialogTitle("Select BOM file");
				fileChooser.showOpenDialog(frm);
				File prototypeFile = fileChooser.getSelectedFile();
				windChilltxtfield.setText("" + prototypeFile);
				try {
					d = new WindChill_BOM(windChilltxtfield.getText(), null);
					updateTree(1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		JLabel label_2 = new JLabel("WindChill:");
		label_2.setFont(new Font("Tahoma", Font.BOLD, 8));
		
		JLabel label_3 = new JLabel("Scrap:");
		
		Scrap_txtField = new JTextField();
		Scrap_txtField.setText(dat[1]);
		Scrap_txtField.setColumns(10);
		
		JButton button_1 = new JButton("browse");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				if (SFdirec == null) {
					String user = System.getProperty("user.home");
					fileChooser.setCurrentDirectory(new File(user + "/Desktop/BOM_Comparator/Input/Scrap factor lists"));
				} else {
					fileChooser.setCurrentDirectory(SFdirec);
				}
				fileChooser.setDialogTitle("Select BOM file");
				fileChooser.showOpenDialog(frm);
				File prototypeFile = fileChooser.getSelectedFile();
				Scrap_txtField.setText("" + prototypeFile);
				updateChoice();
			}
		});
		
		Ratio_txtField = new JTextField();
		Ratio_txtField.setToolTipText("Weld Wire Ratio");
		Ratio_txtField.setText(dat[2]);
		Ratio_txtField.setColumns(10);
		
		SpecialCase_txtField = new JTextField();
		SpecialCase_txtField.setToolTipText("Special Cases for Removal");
		SpecialCase_txtField.setText(dat[3]);
		SpecialCase_txtField.setColumns(10);
		
		JButton button_2 = new JButton("browse");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!chckbxNewCheckBox.isSelected()) {
					JFileChooser fileChooser = new JFileChooser();
					if (AS400direc == null) {
						String user = System.getProperty("user.home");
						fileChooser.setCurrentDirectory(new File(user + "/Desktop/BOM_Comparator/Input/BOMs/AS400 BOM"));
					} else {
						fileChooser.setCurrentDirectory(AS400direc);
					}
					fileChooser.setDialogTitle("Select BOM file");
					fileChooser.showOpenDialog(frm);
					File prototypeFile = fileChooser.getSelectedFile();
					AS400txtfield.setText("" + prototypeFile);
					try {
						if (!chckbxmntmxWcMode.isSelected()) {
							b = new AS400_BOM(AS400txtfield.getText(), null);
						} else b = new WindChill_BOM(AS400txtfield.getText(), null);
						updateTree(0);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} System.out.println("This mode does not support AS400 BOM's");
				
				
			}
		});
		
		JButton button_3 = new JButton("browse");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				if (IMdirec == null) {
					String user = System.getProperty("user.home");
					fileChooser.setCurrentDirectory(new File(user + "/Desktop/BOM_Comparator/Input/IM lists"));
				} else {
					fileChooser.setCurrentDirectory(IMdirec);
				}
				fileChooser.setDialogTitle("Select BOM file");
				fileChooser.showOpenDialog(frm);
				File prototypeFile = fileChooser.getSelectedFile();
				IM_txtField.setText("" + prototypeFile);
				
				
			}
		});
		
		choice = new Choice();
		updateChoice();
		choice.select(dat[4]);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		
		JButton button_4 = new JButton("Compare");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				process = new Process();
				process.addPropertyChangeListener(
					     new PropertyChangeListener() {
					         public  void propertyChange(PropertyChangeEvent evt) {
					        	 if (evt.getNewValue().toString().equals("STARTED")) {
					        		progressBar.setIndeterminate(true);
								} else if (evt.getNewValue().toString().equals("DONE")) {
									progressBar.setIndeterminate(false);
								}
					         }
					     });
				process.execute();
				
			}
		});
		button_4.setFont(new Font("Tahoma", Font.BOLD, 13));
		
		progressBar = new JProgressBar();
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		WindChilltree = new JTree();
		WindChilltree.setRootVisible(false);
		WindChilltree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("WindChill BOM") {
				{
				}
			}
		));
		WindChilltree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) WindChilltree.getLastSelectedPathComponent();
				
				if (node == null) return;
				
				Part nodeItem = (Part) node.getUserObject();
				
				prtNumlbl.setText(nodeItem.getID());
				prtNmlbl.setText(nodeItem.getName());
				prtPoslbl.setText(nodeItem.getPos() + "");
				prtQtylbl.setText(nodeItem.getQty() + "");
				prtIMlbl.setText(nodeItem.getMKNum());
				prtTplbl.setText(nodeItem.getClass().getSimpleName());
				prtMatNumlbl.setText(nodeItem.getMatNum());
				prtMatQtylbl.setText(nodeItem.getMatQty() + "");
			}
		});
		WindChilltree.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		/**/
		scrollPane_1.setViewportView(WindChilltree);
		
		JLabel lblWindchillBom = new JLabel("WindChill BOM");
		lblWindchillBom.setHorizontalAlignment(SwingConstants.CENTER);
		lblWindchillBom.setFont(new Font("Tahoma", Font.BOLD, 15));
		GroupLayout groupLayout = new GroupLayout(frm.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(10)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblAsBom, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
							.addGap(10)
							.addComponent(lblWindchillBom, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
							.addGap(10)
							.addComponent(lblPart, GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
							.addGap(9))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
							.addGap(10)
							.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
							.addGap(10)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(panel, GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
									.addGap(2))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
									.addGap(2)))))
					.addGap(12))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblAsBom, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 2, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED, 2, Short.MAX_VALUE)
							.addComponent(lblWindchillBom, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblPart, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
					.addGap(9)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
							.addGap(11)
							.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
							.addGap(11)
							.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)))
					.addGap(10))
		);
		
		JLabel lblMaterialNum = new JLabel("Material Num:");
		lblMaterialNum.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblMaterialNum.setBounds(10, 164, 94, 14);
		panel.add(lblMaterialNum);
		
		JLabel lblMaterialQty = new JLabel("Material Qty:");
		lblMaterialQty.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblMaterialQty.setBounds(10, 189, 94, 14);
		panel.add(lblMaterialQty);
		
		prtMatNumlbl = new JLabel("");
		prtMatNumlbl.setBounds(114, 165, 216, 14);
		panel.add(prtMatNumlbl);
		
		prtMatQtylbl = new JLabel("");
		prtMatQtylbl.setBounds(114, 190, 216, 14);
		panel.add(prtMatQtylbl);
		
		JButton btnNewButton = new JButton("Stop");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (process.getState().toString().equals("STARTED")) {
					process.cancel(true);
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 13));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(18)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(button_4, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
							.addGap(8))))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(9)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
						.addComponent(button_4, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
					.addGap(8))
		);
		panel_2.setLayout(gl_panel_2);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(19)
					.addComponent(label, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
					.addGap(16)
					.addComponent(IM_txtField, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
					.addGap(10)
					.addComponent(button_3, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
					.addGap(8))
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(19)
					.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addComponent(AS400txtfield, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
					.addGap(10)
					.addComponent(button_2, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
					.addGap(8))
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(19)
					.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addComponent(windChilltxtfield, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
					.addGap(10)
					.addComponent(button, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
					.addGap(8))
				.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(70)
							.addComponent(Ratio_txtField, GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
							.addGap(10)
							.addComponent(SpecialCase_txtField, GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
							.addGap(10)
							.addComponent(choice, GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(19)
							.addComponent(label_3, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addComponent(Scrap_txtField, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)))
					.addGap(10)
					.addComponent(button_1, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
					.addGap(8))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(15)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(3)
							.addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(3))
						.addComponent(IM_txtField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button_3, GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE))
					.addGap(11)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(3)
							.addComponent(label_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(3))
						.addComponent(AS400txtfield, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button_2, GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE))
					.addGap(11)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(4)
							.addComponent(label_2, GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
							.addGap(2))
						.addComponent(windChilltxtfield, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button, GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE))
					.addGap(11)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(3)
							.addComponent(label_3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(3))
						.addComponent(Scrap_txtField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button_1, GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE))
					.addGap(11)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(Ratio_txtField, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
								.addComponent(SpecialCase_txtField, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
							.addGap(15))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(0)
							.addComponent(choice, GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
							.addGap(18))))
		);
		panel_1.setLayout(gl_panel_1);
		frm.getContentPane().setLayout(groupLayout);
	}

	public JFrame getFrame() {
		return frm;
	}

	public void setFrame(JFrame frame) {
		this.frm = frame;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public Bag<String> readSFType(String fileName) throws IOException{
		File file = new File(fileName);
		CSVReader reader = new CSVReader(new FileReader(file));
		String[] nextLine;
		reader.readNext();
		reader.readNext();
		Bag<String> bag = new Bag<String>();
		while((nextLine = reader.readNext()) != null){
			if (bag.get(nextLine[2]) == null) {
				bag.add(nextLine[2]);
			}
		}
		reader.close();
		return bag;
	}
	
	public void updateChoice(){
		try {
			Bag<String> bag = readSFType(Scrap_txtField.getText());
			for (String s : bag) {
				choice.add(s);
			}
		} catch (IOException e) {
			System.out.println("No valid scrap file");
		}
	}
	
	class Process extends SwingWorker<String, Object>{

		@Override
		protected String doInBackground() throws IOException {
			System.out.println("Starting");
			
			try {
				String user = System.getProperty("user.home");
				Writer writer = new Writer(user + "/Desktop/BOM_Comparator/log.txt");
				
				try {
					
					System.out.println("Creating BOMs");
					if (!chckbxNewCheckBox.isSelected()) {
						if (!chckbxmntmxWcMode.isSelected()) {
							b = new AS400_BOM(AS400txtfield.getText(), null);
						} else b = new WindChill_BOM(AS400txtfield.getText(), null);
						updateTree(0);
					} else b = null;
					
					d = new WindChill_BOM(windChilltxtfield.getText(), writer);
					updateTree(1);
					
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(frm, "I can't find one of the BOM files you gave me!"); return null;
				}
			
				try {
					manip = new Manipulator(b, d, writer, frm);
				} catch (Exception e) {
					e.printStackTrace();
				}
				writer.getBOMs(b, d);
				System.out.println("starting manipulations");
				boolean done = manip.doAllManipulations(d, options, new String[]{IM_txtField.getText(), Scrap_txtField.getText(), choice.getSelectedItem(), Ratio_txtField.getText(), SpecialCase_txtField.getText()});
				if (b instanceof WindChill_BOM) {
					done = manip.doAllManipulations(b, options, new String[]{IM_txtField.getText(), Scrap_txtField.getText(), choice.getSelectedItem(), Ratio_txtField.getText(), SpecialCase_txtField.getText()});
				}
				if (done) {
					if (!chckbxNewCheckBox.isSelected()) {
						updateTree(0);
					}
					updateTree(1);
					writer.writeBOM(d); //TODO
					if (!chckbxNewCheckBox.isSelected()) {
						System.out.println("comparing");
						manip.compare();
						manip.removeExtrasFromBag(manip.getDeleted());
					} else if (chckbxNewCheckBox.isSelected()) {
						System.out.println("populating");
						manip.populateBOM(d);
					}
					System.out.println("writing delta list");
					File upload = writer.writeMass(manip.getDeleted(), manip.getAdded(), manip.getQtyChanged());
					writer.writeChangeList(manip.getMatched(), manip.getDeleted(), manip.getAdded());
					Desktop.getDesktop().open(upload);
					
				} else {
					System.out.println("an error occured with the manipulations");
				}
				writer.close();
				System.out.println("done");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			writeAppData();
			return null;
		}
		
	}
}
