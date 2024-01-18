import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.IntelliJTheme;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.print.attribute.standard.JobKOctets;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Notepad2 extends JFrame implements ActionListener, DocumentListener {

    private boolean isSaved = true;
    private JTextArea txtContent = null;
    private JMenuItem mniNew = null;
    private JMenuItem mniOpen = null;
    private JMenuItem mniSave = null;
    private JMenuItem mniExit = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
                new Notepad2();
            }
        });
    }

    private Notepad2(){
        setTitle("Notepad");

        setSize(300,400);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //gestione salvataggio
        setLocationRelativeTo(null);

        initUI();

        setVisible(true);
    }

    private void initUI() {
        LafManager.install(new DarculaTheme());


        JMenuBar mnbMain = new JMenuBar();
        JMenu mnuFile = new JMenu("File");
        mniNew = new JMenuItem("New");
        mniOpen = new JMenuItem("Open...");
        mniSave = new JMenuItem("Save As...");
        mniExit = new JMenuItem("Exit");

        mniNew.addActionListener(this);
        mniOpen.addActionListener(this);
        mniSave.addActionListener(this);
        mniExit.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(!checkSaved()) exit();
            }
        });

        mnuFile.add(mniNew);
        mnuFile.add(mniOpen);
        mnuFile.add(mniSave);
        mnuFile.addSeparator();
        mnuFile.add(mniExit);

        mnbMain.add(mnuFile);
        setJMenuBar(mnbMain); // uguale a borderlayout nord

        txtContent = new JTextArea();
        txtContent.getDocument().addDocumentListener(this);

        JScrollPane pnlCenter = new JScrollPane(txtContent);
        add(pnlCenter, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == mniNew){
            if(!checkSaved())
                newFile();
        }
        if(e.getSource() == mniOpen){
            if(!checkSaved())
                openFile();
        }
        if(e.getSource() == mniSave){
            if(!checkSaved())
                saveFile();
        }
        if(e.getSource() == mniExit){
            if(!checkSaved())
                exit();
        }
    }

    private void exit() {
        System.exit(0);
    }

    private void saveFile() {
        JFileChooser jfc = new JFileChooser();
        int rc = jfc.showSaveDialog(this);
        if(rc != JFileChooser.APPROVE_OPTION) return;

        try {
            PrintWriter pw = new PrintWriter(jfc.getSelectedFile());
            String content = txtContent.getText();
            pw.println(content);
            isSaved = true;
            pw.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void openFile() {
        JFileChooser jfc = new JFileChooser();
        int rc = jfc.showOpenDialog(this);
        if(rc != JFileChooser.APPROVE_OPTION) return;
        try {
            BufferedReader br = new BufferedReader(new FileReader(jfc.getSelectedFile()));
            String line = br.readLine();
            txtContent.setText("");
            while(line!=null){
                txtContent.append(line + "\n");
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void newFile() {
        isSaved = true;
        txtContent.setText("");
    }

    private boolean checkSaved() {
        if(isSaved) return false;

        int rc = JOptionPane.showOptionDialog(this, "Non hai salvato il file, vuoi salvarlo?", "Alert", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

        if(rc == JOptionPane.CANCEL_OPTION) return true; // annullamento operazione
        if(rc == JOptionPane.YES_OPTION) saveFile();
        if(rc == JOptionPane.NO_OPTION) {} // continua con operazione
        return false;
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) { isSaved = false; }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) { isSaved = false; }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) { isSaved = false; }
}
