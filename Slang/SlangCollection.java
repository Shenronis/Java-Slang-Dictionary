package Slang;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * Slang
 * Created by Hoàng Thiện
 * Date 12/19/2021 - 10:08 AM
 * Description: This will be the structure to represent slang and its definition
 */

public class SlangCollection {
    private static final String filepath = "cache.bin";
    private static final String resetpath = "slang.bak";

    private HashMap<String, ArrayList<String>> collection;

    public SlangCollection() {
        collection = new HashMap<String, ArrayList<String>>();
    }

    public SlangCollection(SlangCollection slangCollection) {
        this.collection = null;
        if (slangCollection.collection != null) {
            this.collection = new HashMap<>(slangCollection.collection);
        }
    }

    public int length() {
        if (collection != null) {
            return collection.size();
        }
        return 0;
    }

    public boolean doesSlangExist(String slang) {
        return collection.containsKey(slang);
    }

    public void addSlang(String slang, ArrayList<String> definition) throws IllegalAccessException, IllegalArgumentException {
        if (!slang.isEmpty() && !definition.isEmpty()) {
            if (!collection.containsKey(slang)) {
                collection.put(slang, definition);
            } else {
                throw new IllegalAccessException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void addSlang(String slang, String definition) throws IllegalAccessException, IllegalArgumentException {
        ArrayList<String> definitions = new ArrayList<>();
        definitions.add(definition);
        this.addSlang(slang, definitions);
    }

    public void removeSlang(String slang) throws IllegalAccessException, IllegalArgumentException {
        if (!slang.isEmpty()) {
            if (collection.containsKey(slang)) {
                collection.remove(slang);
            } else {
                throw new IllegalAccessException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void editSlang(String slang, String newSlang) throws IllegalAccessException, IllegalArgumentException {
        if (!slang.isEmpty()) {
            if (collection.containsKey(slang)) {
                ArrayList<String> values = collection.remove(slang);
                collection.put(newSlang, values);
            } else {
                throw new IllegalAccessException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public ArrayList<String> getDefinition(String slang) {
        if (collection.containsKey(slang)) {
            return collection.get(slang);
        }
        return null;
    }

    public void addDefinition(String slang, String definition) throws IllegalAccessException, IllegalArgumentException {
        if (!slang.isEmpty() && !definition.isEmpty()) {
            if (collection.containsKey(slang)) {
                if (!collection.get(slang).contains(definition)) {
                    collection.get(slang).add(definition);
                } else {
                    throw new IllegalAccessException();
                }
            } else {
                ArrayList<String> definitions = new ArrayList<>(collection.get(slang));
                definitions.add(definition);
                addSlang(slang, definitions);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void addDefinition(String slang, ArrayList<String> defs) throws IllegalArgumentException {
        for (String def : defs) {
            try {
                addDefinition(slang, def);
            } catch (IllegalAccessException e) {
                System.out.println("Definition duplication ["+slang+"]: " + def + "");
            }
        }
    }

    public void removeDefinition(String slang, String def) throws IllegalAccessException, IllegalArgumentException {
        if (!slang.isEmpty()) {
            if (collection.containsKey(slang)) {
                ArrayList<String> updated_defs = collection.get(slang);
                updated_defs.remove(def);
                collection.put(slang, updated_defs);
            } else {
                throw new IllegalAccessException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void editDefinition(String slang, String oldDef, String newDef) throws IllegalAccessException, IllegalArgumentException {
        if (!slang.isEmpty()) {
            if (collection.containsKey(slang)) {
                ArrayList<String> values = collection.get(slang);
                values.remove(oldDef);
                values.add(newDef);
                collection.put(slang, values);
            } else {
                throw new IllegalAccessException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public HashMap<String, ArrayList<String>> getCollection() {
        return collection;
    }

    public String getRandomSlang() {
        Random random = new Random();
        Object[] keySet = collection.keySet().toArray();
        Object randomKey = keySet[random.nextInt(collection.size())];
        return randomKey.toString();
    }

    public void SaveCache() {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(collection);
            objectOut.close();
            System.out.println("Saving...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ReadCache() {
        if (new File(filepath).exists()) {
            collection.clear();
            try {
                FileInputStream fileInput = new FileInputStream(filepath);
                ObjectInputStream objectInput = new ObjectInputStream(fileInput);
                HashMap<String, ArrayList<String>> hashMap = (HashMap<String, ArrayList<String>>) objectInput.readObject();
                if (hashMap != null) {
                    collection = new HashMap<>(hashMap);
                }
                objectInput.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private String FileBrowser(boolean isExporting) throws NullPointerException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        FileNameExtensionFilter textFilter = new FileNameExtensionFilter("Text files (*.txt)", "txt");
        fileChooser.setFileFilter(textFilter);

        int currentDialog;

        if (!isExporting) {
            fileChooser.setDialogTitle("Select Slang List (*.csv)");
            currentDialog = fileChooser.showOpenDialog(null);
        } else {
            fileChooser.setDialogTitle("Export Slang List (*.csv)");
            currentDialog = fileChooser.showSaveDialog(null);
        }

        if (currentDialog == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                if (file.exists()) {
                    String path = file.getAbsolutePath();

                    int result = JOptionPane.CLOSED_OPTION;
                    if (fileChooser.getDialogType() == JFileChooser.OPEN_DIALOG) {
                        result = JOptionPane.showConfirmDialog(fileChooser, "This will overwrite the current list, please be sure to export the current list!", "Overwrite?", JOptionPane.OK_CANCEL_OPTION);
                        switch (result) {
                            case JOptionPane.OK_OPTION:
                                return path;
                            case JOptionPane.CLOSED_OPTION, JOptionPane.CANCEL_OPTION:
                                return "";
                        }
                    } else if (fileChooser.getDialogType() == JFileChooser.SAVE_DIALOG) {
                        result = JOptionPane.showConfirmDialog(fileChooser, "This will overwrite the current file. Continue?!", "Overwrite?", JOptionPane.OK_CANCEL_OPTION);
                    }

                    switch (result) {
                        case JOptionPane.OK_OPTION:
                            return path;
                        case JOptionPane.CLOSED_OPTION, JOptionPane.CANCEL_OPTION:
                            return "";
                    }
                }
                else if (isExporting) {
                    String path = file.getAbsolutePath();
                    if(!path.endsWith(".txt") ) {
                        path += ".txt";
                    }
                    return path;
                }
            } catch (NullPointerException | SecurityException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public void ImportSlangDictionary() throws IllegalArgumentException {
        ImportSlangDictionary(FileBrowser(false));
    }

    public void ImportSlangDictionary(String path) throws IllegalArgumentException{
        if (!path.isEmpty()) {
            int count = 0;
            String row;
            collection.clear();
            String lastSlang = "";
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                while((row = reader.readLine()) != null) {
                    String[] data = row.split("`");
                    if (data.length > 1) {
                        lastSlang = data[0];
                        String[] defs = data[1].split("\\|");
                        ArrayList<String> newListOfDefs = new ArrayList<>(Arrays.asList(defs));
                        for (int i = 0; i < newListOfDefs.size(); i++) {
                            newListOfDefs.set(i, newListOfDefs.get(i).replaceAll("\\s", ""));
                        }
                        try {
                            this.addSlang(lastSlang, newListOfDefs);
                        } catch (IllegalAccessException e) {
                            System.out.println("Slang Duplication ["+lastSlang+"] at line " + count);
                            System.out.println("Proceed to add any new definition...");
                            this.addDefinition(lastSlang, newListOfDefs);
                            continue;
                        }
                    } else {
                        if (!lastSlang.equals("")) {
                            this.addDefinition(lastSlang, data[0]);
                        } else {
                            throw new IllegalArgumentException();
                        }
                    }
                    count++;
                }
                reader.close();
                System.out.println("Successfully imported " + path);
                System.out.println(count + " Slang(s) was imported!");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                System.out.println("Error loading data at line " + count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resetSlangDictionary() throws FileNotFoundException {
        ImportSlangDictionary(resetpath);
    }
}
