package view;

import model.MetropolisTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Objects;

public class MetropolisFrame extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;

    private JTextField metropolisField;
    private JTextField continentField;
    private JTextField populationField;

    private JComboBox<String> populationDropdown;
    private JComboBox<String> matchDropdown;

    private JButton addButton;
    private JButton searchButton;

    private final MetropolisTableModel model;


    public MetropolisFrame() {
        super("Metropolis Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        model = new MetropolisTableModel();

        setupUI();
    }


    private void setupUI() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        createTopPanel();
        createCenterPanel();
        createRightPanel();
        setupActionListeners();
    }


    private void createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        metropolisField = new JTextField(12);
        continentField = new JTextField(12);
        populationField = new JTextField(12);

        topPanel.add(new JLabel("Metropolis:"));
        topPanel.add(metropolisField);
        topPanel.add(new JLabel("Continent:"));
        topPanel.add(continentField);
        topPanel.add(new JLabel("Population:"));
        topPanel.add(populationField);

        add(topPanel, BorderLayout.NORTH);
    }

    private void createCenterPanel() {
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        addButton = new JButton("Add");
        searchButton = new JButton("Search");

        Dimension dim = new Dimension(150, 30);
        addButton.setMaximumSize(dim);
        searchButton.setMaximumSize(dim);

        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel optionsBox = createOptionsBox();

        rightPanel.add(addButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(searchButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(optionsBox);

        add(rightPanel, BorderLayout.EAST);
    }

    private JPanel createOptionsBox() {
        JPanel optionsBox = new JPanel();
        optionsBox.setLayout(new BoxLayout(optionsBox, BoxLayout.Y_AXIS));
        optionsBox.setBorder(new TitledBorder("Search Options"));

        populationDropdown = new JComboBox<>(new String[]{"Population Larger Than", "Population Smaller Than"});
        matchDropdown = new JComboBox<>(new String[]{"Exact Match", "Partial Match"});

        optionsBox.add(populationDropdown);
        optionsBox.add(Box.createRigidArea(new Dimension(0, 10)));
        optionsBox.add(matchDropdown);

        return optionsBox;
    }


    private void setupActionListeners() {
        addButton.addActionListener(e -> executeAdd());
        searchButton.addActionListener(e -> executeSearch());
    }

    private void executeAdd() {
        String metropolis = metropolisField.getText().trim();
        String continent = continentField.getText().trim();
        String population = populationField.getText().trim();

        if (!metropolis.isEmpty()) {
            model.add(metropolis, continent, population);
        } else {
            JOptionPane.showMessageDialog(this, "Please enter at least a Metropolis name to add.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void executeSearch() {
        String metropolis = metropolisField.getText().trim();
        String continent = continentField.getText().trim();
        String population = populationField.getText().trim();

        boolean largerThan = Objects.equals(populationDropdown.getSelectedItem(), "Population Larger Than");
        boolean exactMatch = Objects.equals(matchDropdown.getSelectedItem(), "Exact Match");

        model.search(metropolis, continent, population, exactMatch, largerThan);
    }
}