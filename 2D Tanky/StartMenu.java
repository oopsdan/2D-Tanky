import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

/**
 * Trieda StartMenu reprezentuje počiatočné menu hry.
 * Slúži na získanie informácií od hráčov pred spustením hry.
 * 
 * @author Daniel J.
 * 
 * @version 5.0
 */

public class StartMenu {
    private JFrame okno;
    private boolean jeHraSpustena = false;
    private Platno platno;
    private JTextField poleNaMenoHrac1;
    private JTextField poleNaMenoHrac2;
    private int sirkaOkna;
    private int vyskaOkna;
    private String nazovOkna;
    private String nazovHry;
    private String hrac1VybranaFarba;
    private String hrac2VybranaFarba;

    /**
     * Konštruktor pre triedu StartMenu.
     * 
     * @param platno Inštancia Platno, ktorú bude menu používať
     */
    public StartMenu(Platno platno) {
        this.platno = platno;
        this.sirkaOkna = 1024;
        this.vyskaOkna = 768;
        this.nazovOkna = "Štart menu";
        this.nazovHry = "TANKY 2D";

        this.spusti();
    }

    /**
     * Metóda pre inicializáciu počiatočného menu
     */
    private void spusti() {
        this.vytvorokno();
        this.vytvorPoleNaMeno();
        this.nastavokno();
    }

    /**
     * Metóda pre vytvorenie hlavného okna
     */
    private void vytvorokno() {
        this.okno = new JFrame(this.nazovOkna);
        this.okno.setSize(this.sirkaOkna, this.vyskaOkna);
        this.okno.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.okno.setResizable(false);
        this.dajoknoNaStred();
    }

    /**
     * Metóda pre vytvorenie textových polí na mená hráčov
     */
    private void vytvorPoleNaMeno() {
        this.poleNaMenoHrac1 = new JTextField();
        this.poleNaMenoHrac2 = new JTextField();
    }

    /**
     * Metóda na nastavenie vzhľadu hlavného okna a jeho komponentov
     * 
     */
    private void nastavokno() {
        JPanel hlavnyPanel = new JPanel();
        hlavnyPanel.setLayout(new BoxLayout(hlavnyPanel, BoxLayout.Y_AXIS));
        hlavnyPanel.setBackground(new Color(52, 53, 65));

        this.vytvorNazovHry(hlavnyPanel);
        this.vytvorMiestoPreTlacidlo(hlavnyPanel);

        this.okno.add(hlavnyPanel);
        this.okno.setVisible(true);
    }

    /**
     * Metóda pre pridanie názvu hry do panelu
     * @param susednyPanel Susedný panel, do ktorého sa panel s názvom pridáva.
     * 
     */
    private void vytvorNazovHry(JPanel susednyPanel) {
        // Vytvorenie názvu hry s formátovaním
        JLabel nazov = new JLabel(this.nazovHry);
        nazov.setFont(new Font("Tahoma", Font.BOLD, 60));
        nazov.setForeground(Color.white);

        // Vycentrovanie názvu na paneli
        nazov.setAlignmentX(Component.CENTER_ALIGNMENT);
        nazov.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Vytvorenie panelu pre názov hry
        JPanel panelNazvu = new JPanel();
        panelNazvu.setLayout(new BoxLayout(panelNazvu, BoxLayout.Y_AXIS));
        panelNazvu.setBackground(new Color(52, 53, 65));
        panelNazvu.add(Box.createVerticalGlue());
        panelNazvu.add(nazov);
        panelNazvu.add(Box.createVerticalGlue());

        // Pridanie panelu s názvom hry do susedného panelu
        susednyPanel.add(panelNazvu);
    }

    /**
     * Metóda pre vytvorenie miesta pre tlačidlá v paneli
     * 
     *  @param susednyPanel Susedný panel, do ktorého sa panel pre tlačidlá pridáva.
     */
    private void vytvorMiestoPreTlacidlo(JPanel susednyPanel) {
        // Vytvorenie panelu pre tlačidlá
        JPanel panelTlacidiel = new JPanel();
        panelTlacidiel.setLayout(new BoxLayout(panelTlacidiel, BoxLayout.Y_AXIS));
        panelTlacidiel.setBackground(new Color(52, 53, 65));

        // Pridanie tlačidiel do panelu
        this.vytvorTlacidlo(panelTlacidiel);

        // Pridanie panelu s tlačidlami do susedného panelu
        susednyPanel.add(panelTlacidiel);
    }

    /**
     * Metóda pre vytvorenie tlačidiel.
     * 
     * @param panelTlacidiel Panel, do ktorého sa tlačidlá pridajú.
     */
    private void vytvorTlacidlo(JPanel panelTlacidiel) {
        this.pridajTlacidlo(panelTlacidiel, "Spustiť", e -> this.spustiHru());
        this.pridajTlacidlo(panelTlacidiel, "Nastavenia", e -> this.zobrazNastavenia());
        this.pridajTlacidlo(panelTlacidiel, "Koniec", e -> System.exit(0));
    }

    /**
     * Metóda pre pridanie tlačidla do panelu
     * 
     * @param panel    Panel, do ktorého sa tlačidlo pridáva.
     * @param text     Text tlačidla.
     * @param listener Akcia, ktorá sa vykoná pri stlačení tlačidla.
     * @return         Vytvorené tlačidlo.
     * 
     */
    private JButton pridajTlacidlo(JPanel panel, String text, ActionListener listener) {
        // Vytvorenie tlačidla a definovanie vlastností
        JButton tlacidlo = new JButton(text);
        tlacidlo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tlacidlo.addActionListener(listener);
        tlacidlo.setPreferredSize(new Dimension(120, 30));

        // Vytvorenie panelu pre tlačidlo a nastavenie vlastností
        JPanel panelTlacidla = new JPanel();
        panelTlacidla.setBackground(new Color(52, 53, 65));
        panelTlacidla.add(Box.createVerticalGlue());
        panelTlacidla.add(tlacidlo);
        panelTlacidla.add(Box.createVerticalGlue());
        panelTlacidla.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Pridanie panelu s tlačidlom do hlavného panelu
        panel.add(panelTlacidla);

        // Vrátenie vytvoreného tlačidla pre ďalšie použitie
        return tlacidlo;
    }

    /**
     * Metóda pre umiestnenie okna na stred obrazovky
     */
    private void dajoknoNaStred() {
        Dimension velkostOkna = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (velkostOkna.width - this.okno.getWidth()) / 2;
        int y = (velkostOkna.height - this.okno.getHeight()) / 2;
        this.okno.setLocation(x, y);
    }

    /**
     * Metóda pre spustenie hry
     */
    private void spustiHru() {
        this.jeHraSpustena = true;
        this.okno.dispose();
    }

    /**
     * Metóda pre zobrazenie mapy
     * 
     */
    private void zobrazMapy() {
        // Možnosti výberu mapy
        // Leto - zelená farba, Zima - biela farba
        // Default - biela farba
        String[] moznosti = { "Leto", "Zima" };

        // Zobrazenie dialogu na výber mapy
        String vybranaMoznost = (String)JOptionPane.showInputDialog(
                this.okno,
                "Zvoľ mapu:",
                "Nastavenie mapy",
                JOptionPane.PLAIN_MESSAGE,
                null,
                moznosti,
                moznosti[0]);

        // Nastavenie pozadia podľa výberu mapy
        if ("Leto".equals(vybranaMoznost)) {
            this.platno.nastavPozadieZelenejFarby();
        } else if ("Zima".equals(vybranaMoznost)) {
            this.platno.nastavPozadieBielejFarby();
        }
    }

    /**
     * Metóda pre zobrazenie nastavení
     */
    private void zobrazNastavenia() {
        // Vytvorenie panelu s tlačidlami pre rôzne nastavenia
        JPanel panelTlacidiel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tlačidlo pre zobrazenie ovládania
        JButton tlacidloOvladanie = new JButton("Ovládanie");
        tlacidloOvladanie.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tlacidloOvladanie.addActionListener(e -> this.zobrazOvladanie());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelTlacidiel.add(tlacidloOvladanie, gbc);

        // Tlačidlo pre nastavenie mien hráčov
        JButton nastavTlacidloMena = new JButton("Mená");
        nastavTlacidloMena.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nastavTlacidloMena.addActionListener(e -> this.nastavMenaHracov());
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelTlacidiel.add(nastavTlacidloMena, gbc);

        // Tlačidlo pre nastavenie farieb hráčov
        JButton nastavTlacidloFarby = new JButton("Farby");
        nastavTlacidloFarby.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nastavTlacidloFarby.addActionListener(e -> this.nastavFarbyHracov());
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelTlacidiel.add(nastavTlacidloFarby, gbc);

        // Tlačidlo pre výber mapy
        JButton nastavTlacidloMapy = new JButton("Mapy");
        nastavTlacidloMapy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nastavTlacidloMapy.addActionListener(e -> this.zobrazMapy());
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelTlacidiel.add(nastavTlacidloMapy, gbc);

        // Zobrazenie dialógu s nastaveniami
        int result = JOptionPane.showOptionDialog(
                this.okno,
                panelTlacidiel,
                "Nastavenia",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);

        if (result == JOptionPane.OK_OPTION) {
            return;
        } else if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }
    }

    /**
     * Metóda pre nastavenie mien hráčov
     */
    private void nastavMenaHracov() {
        // Zadanie mena pre hráča 1
        String menoHraca1 = JOptionPane.showInputDialog(this.okno, "Zadaj meno hráča 1 (max 10 znakov):",
                "Nastavenie mena hráča 1", JOptionPane.PLAIN_MESSAGE);

        // Kontrola, či bolo meno zadané
        if (menoHraca1 == null) {
            return;
        }

        // Kontrola, či meno nie je príliš dlhé
        if (!this.jeMenoDostupne(menoHraca1)) {
            JOptionPane.showMessageDialog(this.okno, "Meno nesmie byť dlhšie ako 10 znakov!", "Chyba",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Zadanie mena pre hráča 2
        String menoHraca2 = JOptionPane.showInputDialog(this.okno, "Zadaj meno hráča 2 (max 10 znakov):",
                "Nastavenie mena hráča 2", JOptionPane.PLAIN_MESSAGE);

        // Kontrola, či bolo meno zadané
        if (menoHraca2 == null) {
            return;
        }

        // Kontrola, či meno nie je príliš dlhé
        if (!this.jeMenoDostupne(menoHraca2)) {
            JOptionPane.showMessageDialog(this.okno, "Meno nesmie byť dlhšie ako 10 znakov!", "Chyba",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Zobrazenie zadaných mien hráčov
        String sprava = "Meno hráča 1: " + menoHraca1 + "\nMeno hráča 2: " + menoHraca2;
        JOptionPane.showMessageDialog(this.okno, sprava, "Zobrazenie mien hráčov", JOptionPane.PLAIN_MESSAGE);

        // Aktualizácia textových polí pre zobrazenie mien hráčov
        if (this.poleNaMenoHrac1 != null) {
            this.poleNaMenoHrac1.setText(menoHraca1);
        }
        if (this.poleNaMenoHrac2 != null) {
            this.poleNaMenoHrac2.setText(menoHraca2);
        }
    }

    /**
     * Kontroluje, či je zadané meno dostupné.
     * 
     * @param meno Meno na skontrolovanie.
     * @return True, ak je meno dostupné a má maximálne 10 znakov, inak false.
     */
    private boolean jeMenoDostupne(String meno) {
        return meno != null && meno.length() <= 10;
    }

    /**
     * Nastavuje farby hráčov pomocou dialógových okien.
     */
    private void nastavFarbyHracov() {
        // Pole farieb pre hráčov
        String[] farby = { "Červená", "Žltá", "Azúrová", "Modrá", "Purpurová", "Ružová", "Sivá" };

        // Zobrazenie dialógového okna pre výber farby pre hráča 1
        this.hrac1VybranaFarba = (String)JOptionPane.showInputDialog(
                this.okno, "Vyber farbu pre hráča 1:", "Nastavenie farby hráča 1",
                JOptionPane.PLAIN_MESSAGE, null, farby, farby[0]);

        // Zobrazenie dialógového okna pre výber farby pre hráča 2
        this.hrac2VybranaFarba = (String)JOptionPane.showInputDialog(
                this.okno, "Vyber farbu pre hráča 2:", "Nastavenie farby hráča 2",
                JOptionPane.PLAIN_MESSAGE, null, farby, farby[0]);
    }

    /**
     * Zobrazuje informácie o ovládaní hry pomocou dialógového okna.
     */
    private void zobrazOvladanie() {
        JOptionPane.showMessageDialog(this.okno,
                "Hráč 1:\n"
                        + "   Pohyb: W, A, S, D\n"
                        + "   Streľba: Space\n\n"
                        + "Hráč 2:\n"
                        + "   Pohyb: Šípky\n"
                        + "   Streľba: Enter",
                "Ovládanie", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Vráti informáciu o tom, či je hra spustená.
     * 
     * @return True, ak je hra spustená, inak false
     */
    public boolean jeHraSpustena() {
        return this.jeHraSpustena;
    }

    /**
     * Vráti meno hráča 1 zadané v texteovom poli.
     * 
     * @return Meno hráča 1 alebo predvolené "Hráč 1", ak nie je zadané
     */
    public String getMenoHraca1() {
        return this.poleNaMenoHrac1 != null && !this.poleNaMenoHrac1.getText().isEmpty()
                ? this.poleNaMenoHrac1.getText()
                : "Hráč 1";
    }

    /**
     * Vráti meno hráča 2 zadané v texteovom poli.
     * 
     * @return Meno hráča 2 alebo predvolené "Hráč 2", ak nie je zadané
     */
    public String getMenoHraca2() {
        return this.poleNaMenoHrac2 != null && !this.poleNaMenoHrac2.getText().isEmpty()
                ? this.poleNaMenoHrac2.getText()
                : "Hráč 2";
    }

    /**
     * Získava vybranú farbu pre hráča 1.
     *
     * @return Vybraná farba pre hráča 1.
     */
    public String getHrac1VybranaFarba() {
        return this.hrac1VybranaFarba;
    }

    /**
     * Získava vybranú farbu pre hráča 2.
     *
     * @return Vybraná farba pre hráča 2.
     */
    public String getHrac2VybranaFarba() {
        return this.hrac2VybranaFarba;
    }
}