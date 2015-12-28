package de.planet_metax.blocklayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import de.planet_metax.blocklayout.BlockLayout.BlockLayoutConstraints;;

public class BlockLayoutTest extends JFrame {

	private static final long serialVersionUID = -2083939555504538039L;
	
	public BlockLayoutTest() {
		super("Blocklayout Test: Report");
		super.setSize(800, 600);
		super.setLocationRelativeTo(null);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Create fullsize panel for all elements and with scrollbars (if needed).
		JPanel content = new JPanel();
		JScrollPane contentScroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentScroll, BorderLayout.CENTER);
		
		// Create Blocklayout with default alignment: centered
		// and a vgap (vertical gap between elements) of 4 pixels
		// and a padding (distance between elements and container boders) of 10 pixels
		content.setLayout(new BlockLayout(BlockLayout.ALIGN_CENTER, 4, 10));
		
		// Create a headline with left alignment
		JLabel headline1 = new JLabel("This is a headline");
		headline1.setBackground(Color.black);
		headline1.setForeground(Color.yellow);
		headline1.setBorder(new EmptyBorder(4, 4, 4, 4));
		headline1.setOpaque(true);
		headline1.setFont(headline1.getFont().deriveFont(16f).deriveFont(Font.BOLD));
		content.add(headline1, new BlockLayoutConstraints(BlockLayout.ALIGN_JUSTIFY));
		
		// Creat button with right alignment
		JButton button1 = new JButton("Print Report");
		content.add(button1, new BlockLayoutConstraints(BlockLayout.ALIGN_RIGHT));
		
		// Create a textbox with with full width (justify)
		// and with an additional margin of 8 pixels in every direction
		JTextArea textbox1 = new JTextArea("Lorem Ipsum ...\n...\n...", 4, 3);
		content.add(textbox1, new BlockLayoutConstraints(BlockLayout.ALIGN_JUSTIFY, 8));
		
		JTextArea textbox2 = new JTextArea("Lorem Ipsum ...\n...\n...", 8, 3);
		content.add(textbox2, new BlockLayoutConstraints(BlockLayout.ALIGN_JUSTIFY, 8));
		
		// Create cantered button
		JButton button2 = new JButton("Export Report to pdf");
		content.add(button2);
		
		// Create another headline (with a top margin of 16 pixels)
		JLabel headline2 = new JLabel("This is another headline");
		headline2.setBackground(Color.black);
		headline2.setForeground(Color.yellow);
		headline2.setBorder(new EmptyBorder(4, 4, 4, 4));
		headline2.setOpaque(true);
		headline2.setFont(headline1.getFont().deriveFont(16f).deriveFont(Font.BOLD));
		content.add(headline2, new BlockLayoutConstraints(BlockLayout.ALIGN_JUSTIFY, 16, 0, 16, 0));
		
		// Create a quite large element, which will force the scrollbar to scroll,
		// if the window is resized.
		String[] table_header = {"Test 1", "Test 2", "Test 3", "Test 4", "Test 5", "Test 6"};
		String[][] table_data = {
				{"Cell 1A", "Cell 2A", "Cell 3A", "Cell 4A", "Cell 5A", "Cell 6A"},
				{"Cell 1B", "Cell 2D", "Cell 3B", "Cell 4B", "Cell 5B", "Cell 6B"},
				{"Cell 1C", "Cell 2C", "Cell 3C", "Cell 4C", "Cell 5C", "Cell 6C"}
		};
		
		JPanel tablePanel = new JPanel(new BorderLayout());
		JTable table = new JTable(table_data, table_header);
		tablePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tablePanel.add(table, BorderLayout.CENTER);
		content.add(tablePanel);
	}

	public static void main(String[] args) {
		// Start Blocklayout test app
		new BlockLayoutTest().setVisible(true);
	}
	
}
