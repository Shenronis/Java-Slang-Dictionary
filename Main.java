import Slang.SlangCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Dictionary dictionary = new Dictionary();
        dictionary.setVisible(true);
    }
}

class Dictionary extends JFrame implements ActionListener {
    SlangCollection slangCollection;
    final String historyPath = "history.bin";
    HashMap<String, String> historyList;

    JMenuItem opt_save, opt_load, opt_reset, opt_bySlang, opt_byDef, opt_slangRand, opt_history;
    JButton search_btn, clear_btn, control_add, control_del, control_edit;
    JComboBox<ComboItem> search_type;
    JTextField search_input;
    JList<String> dictionary_slang, dictionary_def;
    DefaultListModel<String> dictionary_slang_model, dictionary_def_model;
    Object currentSelect;

    public Dictionary() {
        super("Slang Dictionary - 19127555");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(600, 600));
        this.setLayout(new BorderLayout());

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createMiscMenu());

        // Main panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Search panel
        JPanel panel_search = createSearchPanel();

        // Data panel
        JPanel panel_dictionary = createDictionaryPanel();

        // Control panel
        JPanel panel_control = createControlPanel();

        // Data
        slangCollection = new SlangCollection();
        slangCollection.ReadCache();
        refresh();

        // History
        historyList = new HashMap<>();
        loadSearchHistory();

        // Layout
        panel.add(panel_search, BorderLayout.NORTH);
        panel.add(panel_dictionary, BorderLayout.CENTER);
        panel.add(panel_control, BorderLayout.SOUTH);
        this.add(panel, BorderLayout.CENTER);
        this.add(menuBar, BorderLayout.NORTH);
        this.pack();
    }

    private JMenu createFileMenu() {
        JMenu menu_file = new JMenu("File");

        // Save & Load
        opt_save = new JMenuItem("Save"); opt_save.addActionListener(this);
        opt_load = new JMenuItem("Import"); opt_load.addActionListener(this);
        opt_reset = new JMenuItem("Restore"); opt_reset.addActionListener(this);

        menu_file.add(opt_save);
        menu_file.add(opt_load);
        menu_file.add(opt_reset);
        return  menu_file;
    }

    private JMenu createMiscMenu() {
        JMenu menu_misc = new JMenu("Misc");

        // Multiple choice menu
        JMenu sub_mc = new JMenu("Multiple Choices");
        opt_bySlang = new JMenuItem("by Slang"); opt_bySlang.addActionListener(this);
        opt_byDef = new JMenuItem("by Definition"); opt_byDef.addActionListener(this);
        sub_mc.add(opt_bySlang);
        sub_mc.add(opt_byDef);

        // Random slang of the day
        opt_slangRand = new JMenuItem("Random Slang"); opt_slangRand.addActionListener(this);

        // Search history
        opt_history = new JMenuItem("Search history"); opt_history.addActionListener(this);

        menu_misc.add(sub_mc);
        menu_misc.add(opt_slangRand);
        menu_misc.add(opt_history);
        return menu_misc;
    }

    private JPanel createSearchPanel() {
        JPanel panel_search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        search_input = new JTextField();
        search_type = createTypeBox();
        search_btn = new JButton("Search"); search_btn.addActionListener(this);
        clear_btn = new JButton("Clear"); clear_btn.addActionListener(this);

        search_type.setPreferredSize(new Dimension(150, 25));
        search_input.setPreferredSize(new Dimension(400, 25));
        search_btn.setPreferredSize(new Dimension(100,25));
        clear_btn.setPreferredSize(new Dimension(100,25));

        panel_search.add(search_type);
        panel_search.add(search_input);
        panel_search.add(search_btn);
        panel_search.add(clear_btn);

        return panel_search;
    }

    private JPanel createDictionaryPanel() {
        JPanel panel_dictionary = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel_dictionary.setBorder(new EmptyBorder(10, 0, 10 ,0));

        JPanel panel_list_slang = new JPanel(new BorderLayout());
        panel_list_slang.setPreferredSize(new Dimension(255, 500));
        panel_list_slang.setBorder(new EmptyBorder(0, 0, 0, 5));

        JPanel panel_list_def = new JPanel(new BorderLayout());
        panel_list_def.setPreferredSize(new Dimension(500, 500));
        panel_list_def.setBorder(new EmptyBorder(0, 5, 0, 0));

        dictionary_slang_model = new DefaultListModel<>();
        dictionary_def_model = new DefaultListModel<>();

        dictionary_slang = new JList<>(dictionary_slang_model);
        dictionary_slang.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = dictionary_slang.getSelectedValue();
                if (selected != null) {
                    System.out.println(selected);
                    refreshDefinitions(selected);
                    currentSelect = dictionary_slang;
                }
            }
        });

        dictionary_def = new JList<>(dictionary_def_model);
        dictionary_def.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = dictionary_def.getSelectedValue();
                if (selected != null) {
                    System.out.println(dictionary_def.getSelectedValue());
                    currentSelect = dictionary_def;
                }
            }
        });

        panel_list_slang.add(new JScrollPane(dictionary_slang));
        panel_list_def.add(new JScrollPane(dictionary_def));

        panel_dictionary.add(panel_list_slang);
        panel_dictionary.add(panel_list_def);

        return panel_dictionary;
    }

    private JPanel createControlPanel() {
        JPanel panel_control = new JPanel(new FlowLayout(FlowLayout.LEFT));

        control_add = new JButton("Add");
        control_add.addActionListener(this);
        control_add.setPreferredSize(new Dimension(100, 25));

        control_edit = new JButton("Edit");
        control_edit.addActionListener(this);
        control_edit.setPreferredSize(new Dimension(100, 25));

        control_del = new JButton("Remove");
        control_del.addActionListener(this);
        control_del.setPreferredSize(new Dimension(100, 25));

        panel_control.add(control_add);
        panel_control.add(control_edit);
        panel_control.add(control_del);

        return panel_control;
    }

    private JComboBox<ComboItem> createTypeBox() {
        JComboBox<ComboItem> comboBox = new JComboBox<>();
        comboBox.addItem(new ComboItem("Find by Slang", "slang"));
        comboBox.addItem(new ComboItem("Find by Definition", "def"));

        return comboBox;
    }

    private void refresh() {
        HashMap<String, ArrayList<String>> list = slangCollection.getCollection();
        Object[] listKey = list.keySet().toArray();
        dictionary_slang_model.removeAllElements();
        dictionary_def_model.removeAllElements();
        for (Object slang : listKey) {
            dictionary_slang_model.addElement(slang.toString());
        }
        dictionary_slang.setModel(dictionary_slang_model);
    }

    private void refreshDefinitions(String slang) {
        dictionary_def_model.removeAllElements();
        ArrayList<String> defs = slangCollection.getDefinition(slang);
        for (String def : defs) {
            dictionary_def_model.addElement(def);
        }
        dictionary_def.setModel(dictionary_def_model);
    }

    private void search(String slang) {
        if (!slang.isEmpty()) {
            dictionary_slang_model.removeAllElements();
            dictionary_def_model.removeAllElements();

            HashMap<String, ArrayList<String>> list = slangCollection.getCollection();
            Object[] listKey = list.keySet().toArray();

            Pattern slangRegex = Pattern.compile(Pattern.quote(slang), Pattern.CASE_INSENSITIVE);
            ComboItem currentSearchType = (ComboItem) search_type.getSelectedItem();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            if (currentSearchType.getValue().equals("slang")) {
                historyList.put(dateFormat.format(date), "Find by slang: " + slang);
                for (Object value : listKey) {
                    String valueStr = value.toString();
                    Matcher mymatcher= slangRegex.matcher(valueStr);
                    if (mymatcher.find()) {
                        dictionary_slang_model.addElement(valueStr);
                    }
                }
            } else if (currentSearchType.getValue().equals("def")) {
                historyList.put(dateFormat.format(date), "Find by definition: " + slang);
                for (Object value : listKey) {
                    String valueStr = value.toString();
                    ArrayList<String> defs = slangCollection.getDefinition(valueStr);
                    for (String def : defs) {
                        Matcher mymatcher= slangRegex.matcher(def);
                        if (mymatcher.find()) {
                            dictionary_slang_model.addElement(valueStr);
                        }
                    }
                }
            }

            dictionary_slang.setModel(dictionary_slang_model);
            saveSearchHistory();
        }
    }

    public void loadSearchHistory() {
        if (new File(historyPath).exists()) {
            historyList.clear();
            try {
                FileInputStream fileInput = new FileInputStream(historyPath);
                ObjectInputStream objectInput = new ObjectInputStream(fileInput);
                HashMap<String, String> hashMap = (HashMap<String,String>) objectInput.readObject();
                if (hashMap != null) {
                    historyList = new HashMap<>(hashMap);
                }
                objectInput.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void saveSearchHistory() {
        try {
            FileOutputStream fileOut = new FileOutputStream(historyPath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(historyList);
            objectOut.close();
            System.out.println("Saving history...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void quiz(boolean bySlang) {
        JDialog quizPopup = new JDialog(this, "Slang Quiz");
        quizPopup.setLayout(new BorderLayout());
        quizPopup.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Random random = new Random();
        ArrayList<String> answerLabels = new ArrayList<>();

        String chosenSlang = slangCollection.getRandomSlang();
        ArrayList<String> defs = slangCollection.getDefinition(chosenSlang);
        String chosenDef = defs.get(random.nextInt(defs.size()));

        JLabel questionLabel = new JLabel();
        JPanel ans_panel = new JPanel();

        if (bySlang) {
            String randomDef;
            answerLabels.add(chosenDef);
            questionLabel.setText("What does " + chosenSlang + " mean?");
            for (int i = 0; i < 3; i++) {
                String randomSlang = slangCollection.getRandomSlang();
                ArrayList<String> randomDefs = slangCollection.getDefinition(randomSlang);
                do {
                    randomDef = randomDefs.get(random.nextInt(randomDefs.size()));
                } while (randomDef.equals(chosenDef));
                answerLabels.add(randomDef);
            }

            while(!answerLabels.isEmpty()) {
                int randomIndex = random.nextInt(answerLabels.size());
                JButton newButton = new JButton(answerLabels.get(randomIndex));
                newButton.addActionListener(e -> {
                    if (newButton.getText().equals(chosenDef)) {
                        JOptionPane.showMessageDialog(quizPopup, "Excellent answer! " + chosenSlang + " means " + chosenDef, "CORRECT", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(quizPopup, "Pity, " + chosenSlang + " means " + chosenDef, "WRONG", JOptionPane.PLAIN_MESSAGE);
                    }
                    quizPopup.dispose();
                });

                FontMetrics metrics = newButton.getFontMetrics(getFont());
                int width = metrics.stringWidth(newButton.getText());
                int height = metrics.getHeight();
                Dimension btnSize =  new Dimension(width+100,height+10);
                newButton.setPreferredSize(btnSize);
                newButton.setBounds(new Rectangle(getLocation(), getPreferredSize()));

                ans_panel.add(newButton);
                answerLabels.remove(randomIndex);
            }
        }

        quizPopup.add(questionLabel, BorderLayout.NORTH);
        quizPopup.add(ans_panel, BorderLayout.CENTER);
        quizPopup.pack();
        quizPopup.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == opt_save) {
            slangCollection.SaveCache();
        }

        if (e.getSource() == opt_load) {
            slangCollection.ImportSlangDictionary();
        }

        if (e.getSource() == opt_reset) {
            try {
                slangCollection.resetSlangDictionary();
                refresh();
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Missing slang.bak", "Backup file not found", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.getSource() == opt_slangRand) {
            String slang = slangCollection.getRandomSlang();
            ArrayList<String> defs = slangCollection.getDefinition(slang);
            String definition = "\n";

            for (String def : defs) {
                definition += def + "\n";
            }

            JOptionPane.showMessageDialog(this, slang + " means: " + definition, "Slang of the day", JOptionPane.PLAIN_MESSAGE);
        }

        if (e.getSource() == opt_history) {
            JTextArea ta = new JTextArea(20, 20);
            Object[] listKey = historyList.keySet().toArray();
            for (Object timestamp : listKey) {
                ta.append(timestamp.toString() + " " + historyList.get(timestamp.toString()) + "\n");
            }
            JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Search history", JOptionPane.PLAIN_MESSAGE);
        }

        if (e.getSource() == opt_bySlang) {
            quiz(true);
        }

        if (e.getSource() == control_add) {
            String slang, def;
            try {
                if (currentSelect == dictionary_def) {
                    def = JOptionPane.showInputDialog(this, "Definition:");
                    if (def != null) {
                        System.out.println("SELECTED VALUE:" + dictionary_slang.getSelectedValue() + def);
                        slangCollection.addDefinition(dictionary_slang.getSelectedValue(), def);
                        refreshDefinitions(dictionary_slang.getSelectedValue());
                    }
                } else {
                    slang = JOptionPane.showInputDialog(this, "Slang:");
                    def = JOptionPane.showInputDialog(this, "Definition:");
                    if (slang != null && def != null) {
                        if (slangCollection.doesSlangExist(slang)) {
                            int opt = JOptionPane.showConfirmDialog(this, "This Slang already exists!\nDo you want to add the definition to the existing one", "Add Definition?", JOptionPane.YES_NO_OPTION);
                            if (opt == JOptionPane.YES_OPTION) {
                                slangCollection.addDefinition(slang, def);
                            }
                        } else {
                            slangCollection.addSlang(slang, def);
                            refresh();
                        }
                    }
                }
            } catch (IllegalArgumentException input) {
                JOptionPane.showMessageDialog(this, "Invalid input", "Warning", JOptionPane.WARNING_MESSAGE);
            } catch (IllegalAccessException exist) {
                JOptionPane.showMessageDialog(this, "Slang or Definition already exists", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.getSource() == control_edit) {
            try {
                if (currentSelect == dictionary_slang) {
                    String oldSlang = dictionary_slang.getSelectedValue();
                    String newSlang = JOptionPane.showInputDialog(this, "Edit Definition:");
                    if (newSlang != null) {
                        slangCollection.editSlang(oldSlang, newSlang);
                        refresh();
                    }
                } else if (currentSelect == dictionary_def) {
                    String slang = dictionary_slang.getSelectedValue();
                    String oldDef = dictionary_def.getSelectedValue();
                    String newDef = JOptionPane.showInputDialog(this, "Edit Definition:");
                    if (newDef != null) {
                        slangCollection.editDefinition(slang, oldDef, newDef);
                        refresh();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.getSource() == control_del) {
            try {
                if (currentSelect == dictionary_slang) {
                    String value = dictionary_slang.getSelectedValue();
                    if (value != null) {
                        slangCollection.removeSlang(value);
                        refresh();
                        JOptionPane.showMessageDialog(this, value + " was removed from the dictionary", "Success", JOptionPane.WARNING_MESSAGE);
                    } else JOptionPane.showMessageDialog(this, "Please select a value", "Info", JOptionPane.WARNING_MESSAGE);
                } else if (currentSelect == dictionary_def) {
                    String value = dictionary_def.getSelectedValue();
                    if (value != null) {
                        String curSlang = dictionary_slang.getSelectedValue();
                        slangCollection.removeDefinition(curSlang, value);
                        refreshDefinitions(curSlang);
                        JOptionPane.showMessageDialog(this, value + " was removed from " + curSlang + " definitions", "Success", JOptionPane.WARNING_MESSAGE);
                    } else JOptionPane.showMessageDialog(this, "Please select a value", "Info", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (e.getSource() == search_btn) {
            search(search_input.getText());
        }

        if (e.getSource() == clear_btn) {
            search_input.setText("");
            refresh();
        }
    }
}

class ComboItem {
    private final String key, value;

    public ComboItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
