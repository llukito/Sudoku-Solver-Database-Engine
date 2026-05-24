import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;


 public class SudokuFrame extends JFrame {

	 private JTextArea puzzleArea;
	 private JTextArea solutionArea;
	 private JButton checkButton;
	 private JCheckBox autoCheck;

	public SudokuFrame() {
		super("Sudoku Solver");

		setLayout(new BorderLayout(4, 4));

		setUpPuzzleArea();
		add(puzzleArea, BorderLayout.CENTER);

		setUpSolutionArea();
		add(solutionArea, BorderLayout.EAST);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		setUpCheckButton();
		setUpAutoCheck();
		
		bottomPanel.add(checkButton);
		bottomPanel.add(autoCheck);

		add(bottomPanel, BorderLayout.SOUTH);
		
		registerCheckButtonListeners();
		registerPuzzleAreaListeners();

		setUpJFrame();
	}

	 private void setUpPuzzleArea(){
		puzzleArea = new JTextArea(15, 20);
		puzzleArea.setBorder(new TitledBorder("Puzzle"));
	}

	 private void setUpSolutionArea(){
		 solutionArea = new JTextArea(15, 20);
		 solutionArea.setBorder(new TitledBorder("Solution"));
		 solutionArea.setEditable(false); // Make it read-only
	 }

	 private void setUpCheckButton(){
		 checkButton = new JButton("Check");
	 }

	 private void setUpAutoCheck(){
		 autoCheck = new JCheckBox("Auto Check");
		 autoCheck.setSelected(true);
	 }

	 private void registerCheckButtonListeners() {
		 checkButton.addActionListener(new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent e) {
				 solveAndDisplay();
			 }
		 });
	 }

	 private void registerPuzzleAreaListeners() {
		 puzzleArea.getDocument().addDocumentListener(new DocumentListener() {
			 @Override
			 public void insertUpdate(DocumentEvent e) { doAutoCheck(); }
			 @Override
			 public void removeUpdate(DocumentEvent e) { doAutoCheck(); }
			 @Override
			 public void changedUpdate(DocumentEvent e) { doAutoCheck(); }
		 });
	 }

	 private void doAutoCheck() {
		 if (autoCheck.isSelected()) {
			 solveAndDisplay();
		 }
	 }

	 private void solveAndDisplay() {
		 try {
			 Sudoku sudoku = new Sudoku(puzzleArea.getText());
			 int numSolutions = sudoku.solve();

			 String result = sudoku.getSolutionText();
			 result += "solutions:" + numSolutions + "\n";
			 result += "elapsed:" + sudoku.getElapsed() + "ms\n";

			 solutionArea.setText(result);
		 } catch (Exception e) {
			 solutionArea.setText("Parsing problem");
		 }
	 }

	 private void setUpJFrame(){
		 setLocationByPlatform(true);
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 pack();
		 setVisible(true);
	 }

	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
	}

}
