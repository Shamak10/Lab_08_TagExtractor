import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

class TagExtractorGUI extends JFrame {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JButton selectTextFileButton;
    private JButton selectStopWordsButton;
    private JButton extractTagsButton;
    private JButton saveTagsButton;

    private File selectedTextFile;
    private Set<String> stopWords;
    private Map<String, Integer> tagFrequencyMap;

    public TagExtractorGUI() {
        setTitle("Tag Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea = new JTextArea(20, 40);
        scrollPane = new JScrollPane(textArea);
        selectTextFileButton = new JButton("Select Text File");
        selectStopWordsButton = new JButton("Select Stop Words File");
        extractTagsButton = new JButton("Extract Tags");
        saveTagsButton = new JButton("Save Tags");

        // Add action listeners for buttons
        selectTextFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectTextFile();
            }
        });

        selectStopWordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectStopWordsFile();
            }
        });

        extractTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extractTags();
            }
        });

        saveTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTags();
            }
        });

        // Create the layout and add components
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectTextFileButton);
        buttonPanel.add(selectStopWordsButton);
        buttonPanel.add(extractTagsButton);
        buttonPanel.add(saveTagsButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(buttonPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private void selectTextFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedTextFile = fileChooser.getSelectedFile();
            textArea.setText("Selected Text File: " + selectedTextFile.getName() + "\n");
        }
    }

    private void selectStopWordsFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File stopWordsFile = fileChooser.getSelectedFile();
            stopWords = loadStopWords(stopWordsFile);
            textArea.append("Selected Stop Words File: " + stopWordsFile.getName() + "\n");
        }
    }

    private Set<String> loadStopWords(File stopWordsFile) {
        Set<String> stopWordsSet = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWordsSet.add(line.toLowerCase());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading stop words file.");
            e.printStackTrace();
        }
        return stopWordsSet;
    }

    private void extractTags() {
        if (selectedTextFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a text file first.");
            return;
        }

        if (stopWords == null) {
            JOptionPane.showMessageDialog(this, "Please select a stop words file first.");
            return;
        }

        tagFrequencyMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(selectedTextFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                    if (!stopWords.contains(word)) {
                        tagFrequencyMap.put(word, tagFrequencyMap.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error processing the text file.");
            e.printStackTrace();
        }

        displayTags();
    }

    private void displayTags() {
        textArea.append("\nTags and Frequencies:\n");

        for (Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
            textArea.append(entry.getKey() + " : " + entry.getValue() + "\n");
        }
    }

    private void saveTags() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                for (Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
                    writer.println(entry.getKey() + " : " + entry.getValue());
                }
                textArea.append("\nTags saved to: " + outputFile.getName() + "\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving tags to a file.");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TagExtractorGUI().setVisible(true);
        });
    }
}
