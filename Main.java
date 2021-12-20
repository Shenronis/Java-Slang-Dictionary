import Slang.SlangCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Dictionary dictionary = new Dictionary();
        dictionary.setVisible(true);
    }
}

class Dictionary extends JFrame implements ActionListener {
    SlangCollection slangCollection;

    JButton search_btn, control_add, control_del;
    JComboBox<ComboItem> search_type;
    JTextField search_input;
    JList<String> dictionary_slang, dictionary_def;
    DefaultListModel<String> dictionary_slang_model, dictionary_def_model;

    public Dictionary() {
        super("Slang Dictionary - 19127555");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(600, 600));
        this.setLayout(new BorderLayout());

        // Init
        slangCollection = new SlangCollection();

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
        JMenuItem opt_save = new JMenuItem("Save");
        JMenuItem opt_load = new JMenuItem("Load");

        menu_file.add(opt_save);
        menu_file.add(opt_load);
        return  menu_file;
    }

    private JMenu createMiscMenu() {
        JMenu menu_misc = new JMenu("Misc");

        // Multiple choice menu
        JMenu sub_mc = new JMenu("Multiple Choices");
        JMenuItem opt_bySlang = new JMenuItem("by Slang");
        JMenuItem opt_byDef = new JMenuItem("by Definition");
        sub_mc.add(opt_bySlang);
        sub_mc.add(opt_byDef);

        // Random slang of the day
        JMenuItem opt_slangRand = new JMenuItem("Random Slang");

        menu_misc.add(sub_mc);
        menu_misc.add(opt_slangRand);
        return menu_misc;
    }

    private JPanel createSearchPanel() {
        JPanel panel_search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        search_input = new JTextField();
        search_type = createTypeBox();
        search_btn = new JButton("Search"); search_btn.addActionListener(this);

        search_type.setPreferredSize(new Dimension(150, 25));
        search_input.setPreferredSize(new Dimension(500, 25));
        search_btn.setPreferredSize(new Dimension(100,25));

        panel_search.add(createTypeBox());
        panel_search.add(search_input);
        panel_search.add(search_btn);

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
        dictionary_def = new JList<>(dictionary_def_model);
        panel_list_slang.add(dictionary_slang);
        panel_list_def.add(dictionary_def);

        panel_dictionary.add(panel_list_slang);
        panel_dictionary.add(panel_list_def);

        return panel_dictionary;
    }

    private JPanel createControlPanel() {
        JPanel panel_control = new JPanel(new FlowLayout(FlowLayout.LEFT));

        control_add = new JButton("Add");
        control_add.addActionListener(this);
        control_add.setPreferredSize(new Dimension(100, 25));

        control_del = new JButton("Remove");
        control_del.addActionListener(this);
        control_del.setPreferredSize(new Dimension(100, 25));

        panel_control.add(control_add);
        panel_control.add(control_del);

        return panel_control;
    }

    private JComboBox<ComboItem> createTypeBox() {
        JComboBox<ComboItem> comboBox = new JComboBox<ComboItem>();
        comboBox.addItem(new ComboItem("Find by Slang", "slang"));
        comboBox.addItem(new ComboItem("Find by Definition", "def"));

        return comboBox;
    }

    private void refresh() {
        HashMap<String, ArrayList<String>> list = slangCollection.getCollection();
        Object[] listKey = list.keySet().toArray();
        dictionary_slang_model.removeAllElements();
        for (Object slang : listKey) {
            dictionary_slang_model.addElement(slang.toString());
            ArrayList<String> defs = slangCollection.getDefinition(slang.toString());
            for (String def : defs) {
                dictionary_def_model.addElement(def);
            }
        }
        dictionary_slang.setModel(dictionary_slang_model);
        dictionary_def.setModel(dictionary_def_model);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == control_add) {
            String slang = JOptionPane.showInputDialog(this, "Slang:");
            String def = JOptionPane.showInputDialog(this, "Definition:");
            try {
                slangCollection.addSlang(slang, def);
            } catch (IllegalArgumentException input) {
                JOptionPane.showMessageDialog(this, "Invalid input", "Warning", JOptionPane.WARNING_MESSAGE);
            } catch (IllegalAccessException exist) {
                JOptionPane.showConfirmDialog(this, "This Slang already exists!\nDo you want to add the definition to the existing one", "Add Definition?", JOptionPane.YES_NO_OPTION);
            }
            refresh();
        }

        if (e.getSource() == control_del) {
            System.out.println("Del");
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