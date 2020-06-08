
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class NotePadPlus extends javax.swing.JFrame implements ActionListener {

    public final static String AUTHOR_NAME = "Author Name";
    public final static String AUTHOR_EMAIL = "author@gmail.com";

    public final Color HIGHTLIGHT_COLOR = Color.YELLOW;

    private DefaultHighlightPainter highlightPainter;
    private String foundText; // the current found text

    private JFileChooser fileChooser;

    /**
     * Creates new form TextEditor
     */
    public NotePadPlus() {
        initComponents();
        setLocationRelativeTo(null);

        highlightPainter = new DefaultHighlightPainter(HIGHTLIGHT_COLOR);

        fileChooser = new JFileChooser();

        // action listners for buttons
        jButtonOpen.addActionListener(this);
        jButtonSearch.addActionListener(this);
        jButtonReplace.addActionListener(this);
        jButtonSave.addActionListener(this);

        // action listeners for menu items
        jMenuItemOpen.addActionListener(this);
        jMenuItemSearchAll.addActionListener(this);
        jMenuItemReplaceAll.addActionListener(this);
        jMenuItemForeground.addActionListener(this);
        jMenuItemBackground.addActionListener(this);
        jMenuItemExit.addActionListener(this);
        jMenuItemAbout.addActionListener(this);
        jMenuItemSave.addActionListener(this);

        // add short cut keys
        jMenuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        jMenuItemSearchAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        jMenuItemReplaceAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        jMenuItemForeground.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.ALT_MASK));
        jMenuItemBackground.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.ALT_MASK));
        jMenuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        jMenuItemAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        jMenuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

        jMenuItemOpen.setMnemonic(KeyEvent.VK_O);
        jMenuItemSearchAll.setMnemonic(KeyEvent.VK_F);
        jMenuItemReplaceAll.setMnemonic(KeyEvent.VK_R);
        jMenuItemForeground.setMnemonic(KeyEvent.VK_F);
        jMenuItemBackground.setMnemonic(KeyEvent.VK_B);
        jMenuItemExit.setMnemonic(KeyEvent.VK_X);
        jMenuItemAbout.setMnemonic(KeyEvent.VK_A);
        jMenuItemSave.setMnemonic(KeyEvent.VK_S);

        jMenuFile.setMnemonic(KeyEvent.VK_F);
        jMenuSearch.setMnemonic(KeyEvent.VK_S);
        jMenuColours.setMnemonic(KeyEvent.VK_C);
        jMenuHelp.setMnemonic(KeyEvent.VK_H);

    }

    public void searchAll(String findText) {

        if (findText.isEmpty()) {
            return;
        }

        Highlighter hilite = jTextPane.getHighlighter();

        // remove current highlights
        hilite.removeAllHighlights();

        int results = 0;
        try {

            Document doc = jTextPane.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;
            while ((pos = text.toLowerCase().indexOf(findText.toLowerCase(), pos)) >= 0) {
                hilite.addHighlight(pos, pos + findText.length(), highlightPainter);
                pos += findText.length();
                results++;
            }
        } catch (BadLocationException e) {
            System.out.println(e);
        }

        if (results == 0) {
            showMessage("No matches found");
        } else {
            this.foundText = findText;
            showMessage(results + " matches found");
        }

    }

    public void replaceAll(String replaceText) {
        if (foundText != null) {
            try {
                jTextPane.getHighlighter().removeAllHighlights();
                Document doc = jTextPane.getDocument();
                String text = doc.getText(0, doc.getLength());
                int pos = 0;
                while ((pos = text.toLowerCase().indexOf(foundText.toLowerCase(), pos)) >= 0) {
                    jTextPane.setSelectionStart(pos);
                    jTextPane.setSelectionEnd(pos + foundText.length());
                    jTextPane.replaceSelection(replaceText);
                    text = doc.getText(0, doc.getLength());
                    pos += replaceText.length();
                }
                showMessage("Successfully replaced all");
                foundText = null;
                //
                removeSelection();
            } catch (BadLocationException ex) {
                System.err.println(ex.getMessage());
            }
        } else {
            showMessage("No matches found to replace");
        }

    }

    private void saveFile() {

        showMessage("Saving the file...");
        // Open the file, only this time we call
        int option = fileChooser.showSaveDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File openFile = fileChooser.getSelectedFile();

                BufferedWriter out = new BufferedWriter(new FileWriter(openFile.getPath()));
                out.write(jTextPane.getText());
                out.close();
                showMessage("Successfully saved the file");

            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        } else {
            showMessage("");
        }

    }

    public void about() {
        String content = "Author: " + NotePadPlus.AUTHOR_NAME + " | " + "Contact: " + NotePadPlus.AUTHOR_EMAIL;
        showMessage(content);
    }

    public void showMessage(String msg) {
        jLabelInfo.setText(msg);
    }

    private Color chooseColor() {
        showMessage("Choosing a color....");
        Color color = JColorChooser.showDialog(this, "Select a color", Color.GREEN);
        return color;
    }

    private void doStyle(int start, int end, Object style, Color color) {

        StyledDocument styledDocument = jTextPane.getStyledDocument();

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet oldSet = styledDocument.getCharacterElement(end - 1).getAttributes();
        AttributeSet aset = sc.addAttribute(oldSet, style, color);

        int selectedLength = end - start;
        styledDocument.setCharacterAttributes(start, selectedLength, aset, true);
    }

    private void removeStyle() {
        StyledDocument styledDocument = jTextPane.getStyledDocument();
        styledDocument.setCharacterAttributes(0, styledDocument.getLength(), SimpleAttributeSet.EMPTY, true);
    }

    private void removeSelection() {
        jTextPane.setSelectionStart(0);
        jTextPane.setSelectionEnd(0);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // If the source is the "open" option
        if (e.getSource() == jButtonOpen || e.getSource() == jMenuItemOpen) {

            showMessage("Openning a file...");
            int option = fileChooser.showOpenDialog(this); // get the option that the user selected (approve or cancel)

            if (option == JFileChooser.APPROVE_OPTION) {
                jTextPane.setText("");
                try {
                    File openFile = fileChooser.getSelectedFile();
                    Scanner sc = new Scanner(new FileReader(openFile.getPath()));
                    while (sc.hasNext()) {
                        jTextPane.setText(jTextPane.getText() + sc.nextLine() + "\n");
                    }
                    // clean if there is any style
                    removeStyle();
                    jTextPane.setCaretPosition(0);
                    
                    showMessage("Successfully opened file : " + openFile.getName() + " | Size: " + openFile.length() + " bytes");
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }

            } else {
                showMessage("");
            }
        } // If the source was the "search all" option
        else if (e.getSource() == jButtonSearch || e.getSource() == jMenuItemSearchAll) {
            String findText = jTextFieldFind.getText().trim();
            searchAll(findText); // search and highlight
        } // If the source was the "replace all" option
        else if (e.getSource() == jButtonReplace || e.getSource() == jMenuItemReplaceAll) {
            String replaceText = jTextFieldReplace.getText().trim();
            replaceAll(replaceText); // search and highlight
        } // If the source was the "set foreground" option
        else if (e.getSource() == jMenuItemForeground) {

            int selectionStart = jTextPane.getSelectionStart();
            int selectionEnd = jTextPane.getSelectionEnd();
            if ((selectionEnd == 0 && selectionStart == 0) || (selectionEnd == selectionStart)) {
                showMessage("No any selection");
            } else {
                Color chooseColor = chooseColor();
                if (chooseColor != null) {
                    doStyle(selectionStart, selectionEnd, StyleConstants.Foreground, chooseColor);
                    showMessage("Successfully changed the foreground color");
                } else {
                    showMessage("");
                }
            }
        } // If the source was the "set background" option
        else if (e.getSource() == jMenuItemBackground) {

            int selectionStart = jTextPane.getSelectionStart();
            int selectionEnd = jTextPane.getSelectionEnd();

            if ((selectionEnd == 0 && selectionStart == 0) || (selectionEnd == selectionStart)) {
                showMessage("No any selection");
            } else {
                Color chooseColor = chooseColor();
                if (chooseColor != null) {
                    doStyle(selectionStart, selectionEnd, StyleConstants.Background, chooseColor);
                    showMessage("Successfully changed the background color");
                } else {
                    showMessage("");
                }
            }
        } // If the source was the "save" option
        else if (e.getSource() == jButtonSave || e.getSource() == jMenuItemSave) {
            saveFile();
        } // If the source was the "quit" option
        else if (e.getSource() == jMenuItemExit) {
            System.exit(0);
        } // If the source was the "about" option
        else if (e.getSource() == jMenuItemAbout) {
            about();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel = new javax.swing.JPanel();
        jToolBar = new javax.swing.JToolBar();
        jButtonOpen = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonSearch = new javax.swing.JButton();
        jButtonReplace = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane = new javax.swing.JTextPane();
        jTextFieldFind = new javax.swing.JTextField();
        jTextFieldReplace = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabelInfo = new javax.swing.JLabel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuSearch = new javax.swing.JMenu();
        jMenuItemSearchAll = new javax.swing.JMenuItem();
        jMenuItemReplaceAll = new javax.swing.JMenuItem();
        jMenuColours = new javax.swing.JMenu();
        jMenuItemForeground = new javax.swing.JMenuItem();
        jMenuItemBackground = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Text Editor");
        setResizable(false);

        jPanel.setLayout(new java.awt.BorderLayout());

        jToolBar.setRollover(true);

        jButtonOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/open.png"))); // NOI18N
        jButtonOpen.setToolTipText("Open");
        jButtonOpen.setFocusable(false);
        jButtonOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonOpen.setPreferredSize(new java.awt.Dimension(33, 49));
        jButtonOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar.add(jButtonOpen);

        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        jButtonSave.setToolTipText("Save");
        jButtonSave.setFocusable(false);
        jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar.add(jButtonSave);

        jButtonSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/searchall.png"))); // NOI18N
        jButtonSearch.setToolTipText("Search");
        jButtonSearch.setFocusable(false);
        jButtonSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar.add(jButtonSearch);

        jButtonReplace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/replace.png"))); // NOI18N
        jButtonReplace.setToolTipText("Replace All");
        jButtonReplace.setFocusable(false);
        jButtonReplace.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonReplace.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar.add(jButtonReplace);

        jPanel.add(jToolBar, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setViewportView(jTextPane);

        jLabel1.setText("Find");

        jLabel2.setText("Replace");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldFind, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldReplace, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(302, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldReplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jPanel.add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabelInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jMenuBar.setBackground(new java.awt.Color(255, 255, 255));
        jMenuBar.setBorder(null);

        jMenuFile.setText("File");

        jMenuItemOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/open.png"))); // NOI18N
        jMenuItemOpen.setText("Open");
        jMenuFile.add(jMenuItemOpen);

        jMenuItemSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        jMenuItemSave.setText("Save");
        jMenuFile.add(jMenuItemSave);

        jMenuItemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/close.png"))); // NOI18N
        jMenuItemExit.setText("Exit");
        jMenuFile.add(jMenuItemExit);

        jMenuBar.add(jMenuFile);

        jMenuSearch.setText("Search");

        jMenuItemSearchAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/searchall.png"))); // NOI18N
        jMenuItemSearchAll.setText("Search All");
        jMenuSearch.add(jMenuItemSearchAll);

        jMenuItemReplaceAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/replace.png"))); // NOI18N
        jMenuItemReplaceAll.setText("Replace All");
        jMenuSearch.add(jMenuItemReplaceAll);

        jMenuBar.add(jMenuSearch);

        jMenuColours.setText("Colours");

        jMenuItemForeground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/foreground.png"))); // NOI18N
        jMenuItemForeground.setText("Foreground");
        jMenuColours.add(jMenuItemForeground);

        jMenuItemBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/background.png"))); // NOI18N
        jMenuItemBackground.setText("Background");
        jMenuColours.add(jMenuItemBackground);

        jMenuBar.add(jMenuColours);

        jMenuHelp.setText("Help");

        jMenuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/about.png"))); // NOI18N
        jMenuItemAbout.setText("About");
        jMenuHelp.add(jMenuItemAbout);

        jMenuBar.add(jMenuHelp);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 806, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("GTK+".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NotePadPlus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NotePadPlus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NotePadPlus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NotePadPlus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NotePadPlus().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonReplace;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuColours;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemBackground;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemForeground;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemReplaceAll;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSearchAll;
    private javax.swing.JMenu jMenuSearch;
    private javax.swing.JPanel jPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldFind;
    private javax.swing.JTextField jTextFieldReplace;
    private javax.swing.JTextPane jTextPane;
    private javax.swing.JToolBar jToolBar;
    // End of variables declaration//GEN-END:variables

}
