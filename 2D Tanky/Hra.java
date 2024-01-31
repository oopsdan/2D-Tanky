import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * Trieda Hra
 * 
 * Táto trieda reprezentuje implementáciu hry s dvoma tankmi a prekážkami.
 * Hra sa skladá z viacerých častí, vrátane inicializácie herných objektov,
 * správy klávesnicových vstupov, logiky pohybu, streľby a vyhodnocovania konca
 * hry.
 * 
 * @author Daniel J.
 * 
 * @version 5.0
 */

public class Hra {
    private int pocetPrekazok = 35;
    private int dlzkaCasu = 90; // v sekundách
    private int casovyInterval = 1000;
    private int pocetMaxKol = 5;
    private int vitazneSkore;
    private boolean hraSkoncila;
    private int aktualneKolo;
    private int zostavajuciCas;
    private Tank hrac1;
    private Tank hrac2;
    private Strela strela1;
    private Strela strela2;
    private List<Prekazka> prekazky = new ArrayList<>();
    private Prekazka prekazka;
    private String menoHraca1;
    private String menoHraca2;
    private Set<Integer> klavesyHraca1 = new HashSet<>();
    private Set<Integer> klavesyHraca2 = new HashSet<>();
    private ExecutorService executorService;
    private Manazer manazer;
    private Platno platno;
    private Timer casovac;
    private final Object zamokPrekazky = new Object();
    private StartMenu startMenu;

    /**
     * Konštruktor pre triedu Hra.
     * 
     * @param menoHraca1 Meno prvého hráča.
     * @param menoHraca2 Meno druhého hráča.
     * @param startMenu  Reference na StartMenu pre získanie farieb hráčov.
     */
    public Hra(String menoHraca1, String menoHraca2, StartMenu startMenu) {
        // Inicializácia premenných a objektov
        this.manazer = new Manazer();
        this.menoHraca1 = menoHraca1;
        this.menoHraca2 = menoHraca2;
        this.hraSkoncila = false;
        this.aktualneKolo = 1;
        this.vitazneSkore = 3;
        this.startMenu = startMenu;

        // Spustenie herných objektov
        this.spusti();
    }

    // 1.
    /**
     * Spustí herné objekty a inicializuje herné prostredie.
     */
    public void spusti() {
        this.spustiPlatno();
        this.spustiPrekazky();
        this.spustiTankyStrely();
        this.spustiStrelbu();
        this.spustiPosluchace();
        this.spustiCas();
    }

    /**
     * Spustí platno pre vizualizáciu.
     */
    private void spustiPlatno() {
        this.platno = Platno.dajPlatno();
        this.platno.zameranieNaOkno();
    }

    /**
     * Spawni prekážky na náhodných pozíciách.
     */
    private void spustiPrekazky() {
        for (int i = 0; i < pocetPrekazok; i++) {
            this.vygenerujNahodnePrekazky();
        }
        this.prekazka = this.vygenerujNahodnePrekazky();
    }

    /**
     * Spustí tanky a strely pre hráčov.
     */
    private void spustiTankyStrely() {
        Random random = new Random();
        int odstupOdOkraja = 50;

        // Inicializácia tankov a striel
        this.hrac1 = this.vygenerujTankyBezKolizie(odstupOdOkraja, random);
        this.hrac2 = this.vygenerujTankyBezKolizie(odstupOdOkraja, random);
        this.strela1 = new Strela(this.hrac1);
        this.strela2 = new Strela(this.hrac2);

        // Pridanie objektov do manažéra
        this.manazer.spravujObjekt(this.hrac1);
        this.manazer.spravujObjekt(this.hrac2);
        this.manazer.spravujObjekt(this.strela1);
        this.manazer.spravujObjekt(this.strela2);
        this.manazer.spravujObjekt(this.prekazka);
    }

    /**
     * Spustí vlákna pre spracovanie streľby hráčov.
     */
    private void spustiStrelbu() {
        this.executorService = Executors.newFixedThreadPool(2);
    }

    /**
     * Spustí poslúchače klávesnice pre ovládanie hráčov.
     */
    private void spustiPosluchace() {
        this.platno.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Sprav, ak potrebné.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                Hra.this.spracujStalecenuKlavesu(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Hra.this.spracujUvolnenuKlavesu(e);
            }
        });
    }

    /**
     * Spustí časovač pre sledovanie zostávajúceho času hry.
     */
    private void spustiCas() {
        this.zostavajuciCas = dlzkaCasu;
        this.casovac = new Timer(this.casovyInterval, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hra.this.spravujCas();
            }
        });
        this.casovac.start();
    }

    /**
     * Generuje náhodné prekážky a pridáva ich do zoznamu.
     * 
     * @return Nová prekážka vygenerovaná náhodne.
     */
    private Prekazka vygenerujNahodnePrekazky() {
        // Inicializujeme generátor náhodných čísel
        Random random = new Random();

        // Náhodne určíme pozíciu prekážky v rámci platna
        double x = random.nextInt(Platno.dajPlatno().getSirka() - 50);
        double y = random.nextInt(Platno.dajPlatno().getVyska() - 50);

        // Náhodne určíme rozmery prekážky
        double width = 30 + random.nextInt(50);
        double height = 30 + random.nextInt(50);

        // Vytvoríme prekážku na základe náhodných parametrov
        Prekazka zabrana = new Prekazka(x, y, width, height);

        // Synchronizujeme pridávanie prekážky do zoznamu
        synchronized (this.zamokPrekazky) {
            this.prekazky.add(zabrana);
            this.manazer.spravujObjekt(zabrana);
        }

        // Vrátime novú prekážku
        return zabrana;
    }

    /**
     * Generuje tanky bez kolízie s prekážkami.
     * 
     * @param odstupOdOkraja Vzdialenosť od okraja platna.
     * @param random         Inštancia Random pre generovanie náhodných hodnôt.
     * @return Tank bez kolízie s prekážkami.
     */
    private Tank vygenerujTankyBezKolizie(int odstupOdOkraja, Random random) {
        Tank tank;
        boolean kolizia;

        // Opakovane generujeme tanky, kým nenájdeme taký, ktorý nemá kolíziu s
        // prekážkami
        do {
            tank = new Tank(
                    odstupOdOkraja + random.nextInt(Platno.dajPlatno().getSirka() - 2 * odstupOdOkraja),
                    odstupOdOkraja + random.nextInt(Platno.dajPlatno().getVyska() - 2 * odstupOdOkraja));

            // Kontrolujeme kolíziu tanku s existujúcimi prekážkami
            kolizia = this.kontrolujKoliziuTankuPrekazky(tank);
        } while (kolizia);

        // Vrátime nový tank bez kolízie
        return tank;
    }

    /**
     * Kontroluje, či nastala kolízia medzi tankom a prekážkou.
     * 
     * @param tank Tank, ktorého kolíziu kontrolujeme.
     * @return True, ak nastala kolízia, inak false.
     */
    private boolean kontrolujKoliziuTankuPrekazky(Tank tank) {
        // Prechádzame všetky prekážky a kontrolujeme kolíziu s tankom
        for (Prekazka zabrana : this.prekazky) {
            if (tank.getShape().intersects(zabrana.getShape().getBounds2D())) {
                return true; // Kolízia bola zistená
            }
        }
        return false; // Žiadna kolízia nebola zistená
    }

    // 2.
    /**
     * Spracuje stlačenú klávesu.
     * 
     * @param e KeyEvent objekt reprezentujúci stlačenú klávesu.
     */
    private void spracujStalecenuKlavesu(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_W:
                this.klavesyHraca1.clear();
                this.klavesyHraca1.add(KeyEvent.VK_W);
                break;
            case KeyEvent.VK_S:
                this.klavesyHraca1.clear();
                this.klavesyHraca1.add(KeyEvent.VK_S);
                break;
            case KeyEvent.VK_A:
                this.klavesyHraca1.clear();
                this.klavesyHraca1.add(KeyEvent.VK_A);
                break;
            case KeyEvent.VK_D:
                this.klavesyHraca1.clear();
                this.klavesyHraca1.add(KeyEvent.VK_D);
                break;
            case KeyEvent.VK_SPACE:
                this.executorService.submit(() -> this.strela1.vystrel());
                break;
            case KeyEvent.VK_UP:
                this.klavesyHraca2.clear();
                this.klavesyHraca2.add(KeyEvent.VK_UP);
                break;
            case KeyEvent.VK_DOWN:
                this.klavesyHraca2.clear();
                this.klavesyHraca2.add(KeyEvent.VK_DOWN);
                break;
            case KeyEvent.VK_LEFT:
                this.klavesyHraca2.clear();
                this.klavesyHraca2.add(KeyEvent.VK_LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                this.klavesyHraca2.clear();
                this.klavesyHraca2.add(KeyEvent.VK_RIGHT);
                break;
            case KeyEvent.VK_ENTER:
                this.executorService.submit(() -> this.strela2.vystrel());
                break;
            case KeyEvent.VK_ESCAPE:
                this.vypniHru();
                break;
        }

        this.executorService.submit(this::spracujKlavesoveUdalosti);
    }

    /**
     * Spracuje uvoľnenú klávesu.
     * 
     * @param e KeyEvent objekt reprezentujúci uvoľnenú klávesu.
     */
    private void spracujUvolnenuKlavesu(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                this.klavesyHraca1.remove(key);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                this.klavesyHraca2.remove(key);
                break;
        }

        this.executorService.submit(this::spracujKlavesoveUdalosti);
    }

    /**
     * Spracuje klávesnicové udalosti a vykoná príslušné akcie.
     */
    private void spracujKlavesoveUdalosti() {
        if (this.klavesyHraca1.contains(KeyEvent.VK_W)) {
            this.executorService.submit(() -> this.hrac1.pohniSa(Pohyb.HORE));
        }
        if (this.klavesyHraca1.contains(KeyEvent.VK_S)) {
            this.executorService.submit(() -> this.hrac1.pohniSa(Pohyb.DOLE));
        }
        if (this.klavesyHraca1.contains(KeyEvent.VK_A)) {
            this.executorService.submit(() -> this.hrac1.pohniSa(Pohyb.DO_LAVA));
        }
        if (this.klavesyHraca1.contains(KeyEvent.VK_D)) {
            this.executorService.submit(() -> this.hrac1.pohniSa(Pohyb.DO_PRAVA));
        }

        if (this.klavesyHraca2.contains(KeyEvent.VK_UP)) {
            this.executorService.submit(() -> this.hrac2.pohniSa(Pohyb.HORE));
        }
        if (this.klavesyHraca2.contains(KeyEvent.VK_DOWN)) {
            this.executorService.submit(() -> this.hrac2.pohniSa(Pohyb.DOLE));
        }
        if (this.klavesyHraca2.contains(KeyEvent.VK_LEFT)) {
            this.executorService.submit(() -> this.hrac2.pohniSa(Pohyb.DO_LAVA));
        }
        if (this.klavesyHraca2.contains(KeyEvent.VK_RIGHT)) {
            this.executorService.submit(() -> this.hrac2.pohniSa(Pohyb.DO_PRAVA));
        }

        this.kontrolujVybuchTanku();
    }

    // 3.
    /**
     * Spravuje plynutie času v hre, odpočítava zostávajúci čas.
     */
    private void spravujCas() {
        this.zostavajuciCas--;
        if (this.zostavajuciCas <= 0) {
            this.resetniHru();
            this.casovac.start();
        }
    }

    /**
     * Kontroluje, či hráč dosiahol víťazné skóre alebo či hráči vybuchli.
     */
    private void kontrolujVybuchTanku() {
        if (this.hrac1.getSkore() >= vitazneSkore || this.hrac2.getSkore() >= vitazneSkore) {
            this.spravujVyhru();
        } else if (this.hrac1.jeVybuchnuty() || this.hrac2.jeVybuchnuty()) {
            this.aktualneKolo++;
            this.resetniHru();
        }
    }

    /**
     * Spravuje vyhru v hre.
     */
    private void spravujVyhru() {
        this.casovac.stop();
        String winningPlayerName = this.vyberVitaza();
        this.zobrazDialogMoznosti(winningPlayerName);
        this.spustiHru();
    }

    /**
     * Vyberá víťaza hry na základe skóre hráčov.
     * 
     * @return Meno hráča, ktorý vyhral.
     */
    private String vyberVitaza() {
        if (this.hrac1.getSkore() > this.hrac2.getSkore()) {
            return this.menoHraca1;
        } else {
            return this.menoHraca2;
        }
    }

    /**
     * Vypíše dialog s možnosťami po skončení hry.
     * 
     * @param winner Meno víťaza hry.
     */
    private void zobrazDialogMoznosti(String winner) {
        // Vytvoríme správu pre hráča, ktorý vyhral
        String message = winner + " vyhráva! | Skóre: " + this.hrac1.getSkore() + " : " + this.hrac2.getSkore();

        // Vytvoríme panel s výsledkom hry
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(new JLabel(message));

        // Vytvoríme dialog s možnosťami pre hru
        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
                new Object[] { "Hrať znova", "Ukončiť" }, null);

        JDialog dialog = optionPane.createDialog("Game Over");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Nastavenie správania pri zatváraní okna (prázdna implementácia)
            }
        });

        // Nastavenie klávesovej skratky pre tlačidlo ESC
        optionPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                "escape");
        optionPane.getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Nastavenie správania pri stlačení klávesy ESC (prázdna implementácia)
            }
        });

        // Zobrazíme dialog a získame vybranú možnosť
        dialog.setVisible(true);
        Object selectedValue = optionPane.getValue();

        // Podľa vybranej možnosti vykonáme príslušnú akciu
        if ("Hrať znova".equals(selectedValue)) {
            this.restartujHru();
        } else if ("Ukončiť".equals(selectedValue)) {
            this.vypniHru();
        }
    }

    /**
     * Spravuje koniec hry, zväčšuje aktuálne kolo a resetuje hru.
     */
    private void spravujKoniecHry() {
        this.aktualneKolo++;
        this.resetniHru();
        this.zostavajuciCas = dlzkaCasu;
        this.casovac.restart();
    }

    // 4.
    /**
     * Zobrazuje tanky, strely a prekážky na platne.
     * Iterator tutorial = https://www.w3schools.com/java/java_iterator.asp
     */
    private void zobrazTankyStrelyPrekazky() {
        // Určíme farby pre tanky a strely v závislosti od ich stavu
        String tankColor1 = this.hrac1.jeVybuchnuty() ? "Čierna" : this.startMenu.getHrac1VybranaFarba();
        String tankColor2 = this.hrac2.jeVybuchnuty() ? "Čierna" : this.startMenu.getHrac2VybranaFarba();

        String bulletColor1 = this.hrac1.jeVybuchnuty() ? "Čierna" : this.startMenu.getHrac1VybranaFarba();
        String bulletColor2 = this.hrac2.jeVybuchnuty() ? "Čierna" : this.startMenu.getHrac2VybranaFarba();

        // Vytvoríme kópiu zoznamu prekážok (synchronizovanú)
        List<Prekazka> prekazkyKopia = new ArrayList<>();
        synchronized (this.zamokPrekazky) {
            if (this.prekazky != null) {
                prekazkyKopia.addAll(this.prekazky);
            }
        }

        // Iterator cez prekážky a vykreslíme ich na platno
        Iterator<Prekazka> iterator = prekazkyKopia.iterator();
        while (iterator.hasNext()) {
            Prekazka zabrana = iterator.next();
            this.platno.draw(zabrana, "Oranžová", zabrana.getShape());
        }

        // Vykreslíme tanky, strely a ich farby na platno
        this.platno.draw(this.hrac1, tankColor1, this.hrac1.getShape());
        this.platno.draw(this.hrac2, tankColor2, this.hrac2.getShape());
        this.platno.draw(this.strela1, bulletColor1, this.strela1.getShape());
        this.platno.draw(this.strela2, bulletColor2, this.strela2.getShape());
    }

    /**
     * Zobrazí mená hráčov pod ich tankami.
     */
    private void zobrazMena() {
        int velkostPisma = 11;
        int posunutieMena = 5;

        int menoX1 = (int)(this.hrac1.getX() + this.hrac1.getSirka() / 2);
        int menoY1 = (int)(this.hrac1.getY() + this.hrac1.getVyska() + velkostPisma + posunutieMena);
        this.zobrazText(this.menoHraca1, "black", menoX1, menoY1, velkostPisma);

        int menoX2 = (int)(this.hrac2.getX() + this.hrac2.getSirka() / 2);
        int menoY2 = (int)(this.hrac2.getY() + this.hrac2.getVyska() + velkostPisma + posunutieMena);
        this.zobrazText(this.menoHraca2, "black", menoX2, menoY2, velkostPisma);
    }

    /**
     * Zobrazuje text na daných súradniciach s určenou veľkosťou písma.
     * 
     * @param text         Text, ktorý sa má zobraziť.
     * @param color        Farba textu.
     * @param x            X-súradnica textu.
     * @param y            Y-súradnica textu.
     * @param velkostPisma Veľkosť písma textu.
     */
    private void zobrazText(String text, String color, int x, int y, int velkostPisma) {
        int zaciatokX = x - (text.length() * velkostPisma) / 2;

        for (int i = 0; i < text.length(); i++) {
            String character = String.valueOf(text.charAt(i));
            this.platno.draw(character, zaciatokX + i * velkostPisma, y, velkostPisma);
        }
    }

    /**
     * Zobrazí mená hráčov na strede plátna.
     */
    private void zobrazMenaNaStred() {
        // Veľkosť písma pre informácie o hráčoch
        int velkostPisma = 15;

        // Výpočet šírky textu "vs" pre jeho centrovanie
        int vsTextSirka = velkostPisma * 0;

        // Celková šírka textu pre mená a skóre hráčov
        int sirkaTextuSpolu = 1 * velkostPisma + vsTextSirka;

        // Medzera medzi textami
        int medzera = 10;

        // Počiatočná X-ová pozícia pre vycentrovanie textu na stred obrazovky
        int zaciatokX = Platno.dajPlatno().getSirka() / 3 - sirkaTextuSpolu / 3 + medzera;

        // Pozícia pre meno a skóre prvého hráča
        int menoX1 = zaciatokX;
        int menoY1 = velkostPisma;
        this.zobrazText(this.menoHraca1, "black", menoX1, menoY1, velkostPisma);

        int skoreX1 = menoX1;
        int skoreY1 = menoY1 + velkostPisma;
        this.zobrazText("Skóre: " + this.hrac1.getSkore(), "black", skoreX1, skoreY1, velkostPisma);

        // Pozícia pre "vs"
        int vsX = Platno.dajPlatno().getSirka() / 2 - vsTextSirka / 2;
        int vsY = velkostPisma;
        this.zobrazText("vs", "black", vsX, vsY, velkostPisma * 1);

        // Pozícia pre meno a skóre druhého hráča
        int menoX2 = Platno.dajPlatno().getSirka() - zaciatokX - velkostPisma;
        int menoY2 = velkostPisma;
        this.zobrazText(this.menoHraca2, "black", menoX2, menoY2, velkostPisma);

        int skoreX2 = menoX2;
        int skoreY2 = menoY2 + velkostPisma;
        this.zobrazText("Skóre: " + this.hrac2.getSkore(), "black", skoreX2, skoreY2, velkostPisma);
    }

    /**
     * Zobrazí aktuálne kolo a zostávajúci čas na plátne.
     */
    private void zobrazKoloCas() {
        int velkostPisma = 15;
        int casX = Platno.dajPlatno().getSirka() / 2;
        int casY = velkostPisma * 2;

        String koloText = "Kolo: " + (aktualneKolo) + "/" + pocetMaxKol;
        int minuty = this.zostavajuciCas / 60;
        int sekundy = this.zostavajuciCas % 60;
        String casText = String.format("%02d:%02d", minuty, sekundy);

        this.zobrazText(koloText, "black", casX, casY, velkostPisma);
        this.zobrazText(casText, "black", casX, casY + velkostPisma, velkostPisma);
    }

    // 5.
    /**
     * Spustí herný loop, ktorý aktualizuje herný stav.
     */
    public void spustiHru() {
        // Hlavný herný cyklus, ktorý sa opakuje do dosiahnutia maximálneho počtu kôl
        while (this.aktualneKolo <= pocetMaxKol) {
            // Kontrola, či jeden z hráčov je vybuchnutý
            boolean tank1jeVybuchnuty = this.hrac1.jeVybuchnuty();
            boolean tank2jeVybuchnuty = this.hrac2.jeVybuchnuty();

            // Ak je niektorý hráč vybuchnutý, ukončíme hru a prejdeme na ďalšie kolo
            if (tank1jeVybuchnuty || tank2jeVybuchnuty) {
                if (!this.hraSkoncila) {
                    this.hraSkoncila = true;
                    this.spravujKoniecHry();
                }
                continue;
            } else {
                this.hraSkoncila = false;
            }

            // Aktualizácia pozícií a stavu objektov v hre
            this.hrac1.update();
            this.hrac2.update();
            this.strela1.update();
            this.strela2.update();

            // Vykreslenie aktualizovaného herného stavu na platno
            SwingUtilities.invokeLater(() -> {
                this.zobrazTankyStrelyPrekazky();
                this.zobrazMenaNaStred();
                this.zobrazMena();
                this.zobrazKoloCas();
            });

            // Čakanie na malú dobu (10 milisekúnd)
            this.platno.wait(10);

            // Kontrola stlačenia klávesy ESC pre ukončenie hry
            if (this.platno.isKeyPressed(KeyEvent.VK_ESCAPE)) {
                this.vypniHru();
            }

            // Kontrola víťazného stavu po každom aktualizovaní
            if (this.hrac1.getSkore() >= vitazneSkore || this.hrac2.getSkore() >= vitazneSkore) {
                this.spravujVyhru();
                return;
            }
        }
    }

    /**
     * Resetuje hru a pripraví ju na ďalšie kolo.
     */
    private void resetniHru() {
        // Zastavenie časovača
        this.casovac.stop();

        // Uloženie skóre hráčov
        int skorePlayer1 = this.hrac1.getSkore();
        int skorePlayer2 = this.hrac2.getSkore();

        // Spravovanie vybuchnutých tankov
        this.spravujVybuchnutyTank(this.hrac1);
        this.spravujVybuchnutyTank(this.hrac2);

        // Resetovanie strely
        this.resetniStrelu(this.strela1);
        this.resetniStrelu(this.strela2);

        // Vymazanie prekážok
        this.vymazPrekazky();

        // Generovanie nových náhodných prekážok
        for (int i = 0; i < pocetPrekazok; i++) {
            this.vygenerujNahodnePrekazky();
        }

        // Prednastavenie pozícií hráčov od okraja
        int odstupOdOkraja = 50;
        this.hrac1.prednadstavPoziciu(odstupOdOkraja, new Random());
        this.hrac2.prednadstavPoziciu(odstupOdOkraja, new Random());

        // Kontrola kolízií tankov s prekážkami
        boolean tank1Kolizia = this.kontrolujKoliziuTankuPrekazky(this.hrac1);
        boolean tank2Kolizia = this.kontrolujKoliziuTankuPrekazky(this.hrac2);

        // Ak nastala kolízia, opakovane resetujeme hru
        if (tank1Kolizia || tank2Kolizia) {
            this.resetniHru();
            return;
        }

        // Nastavenie skóre hráčov na pôvodné hodnoty
        this.hrac1.nastavSkore(skorePlayer1);
        this.hrac2.nastavSkore(skorePlayer2);

        // Vykreslenie aktualizovaného stavu na platno
        SwingUtilities.invokeLater(() -> {
            this.zobrazTankyStrelyPrekazky();
        });

        // Kontrola vybuchnutia hráčov
        if (this.hrac1.jeVybuchnuty() || this.hrac2.jeVybuchnuty()) {
            this.spravujKoniecHry();
        } else {
            // Nastavenie zbývajúceho času a spustenie časovača
            this.zostavajuciCas = dlzkaCasu;
            this.casovac.start();
        }

        // Kontrola víťazného stavu
        if (this.hrac1.getSkore() >= vitazneSkore || this.hrac2.getSkore() >= vitazneSkore) {
            this.spravujVyhru();
            return;
        }
    }

    /**
     * Reštartuje hru do pôvodného stavu.
     */
    private void restartujHru() {
        // Nastavenie počiatočného kola a skóre hráčov
        this.aktualneKolo = 1;
        this.hrac1.nastavSkore(0);
        this.hrac2.nastavSkore(0);
        this.hraSkoncila = false;

        // Spravovanie vybuchnutých tankov
        this.spravujVybuchnutyTank(this.hrac1);
        this.spravujVybuchnutyTank(this.hrac2);

        // Resetovanie strely
        this.resetniStrelu(this.strela1);
        this.resetniStrelu(this.strela2);

        // Vymazanie prekážok
        this.vymazPrekazky();

        // Generovanie nových náhodných prekážok
        for (int i = 0; i < pocetPrekazok; i++) {
            this.vygenerujNahodnePrekazky();
        }

        // Prednastavenie pozícií hráčov od okraja
        int odstupOdOkraja = 50;
        this.hrac1.prednadstavPoziciu(odstupOdOkraja, new Random());
        this.hrac2.prednadstavPoziciu(odstupOdOkraja, new Random());

        // Kontrola kolízií tankov s prekážkami
        boolean tank1Kolizia = this.kontrolujKoliziuTankuPrekazky(this.hrac1);
        boolean tank2Kolizia = this.kontrolujKoliziuTankuPrekazky(this.hrac2);

        // Ak nastala kolízia, opakovane reštartujeme hru
        if (tank1Kolizia || tank2Kolizia) {
            this.restartujHru();
            return;
        }

        // Vykreslenie aktualizovaného stavu na platno
        SwingUtilities.invokeLater(() -> {
            this.zobrazTankyStrelyPrekazky();
        });

        // Nastavenie zbývajúceho času a spustenie časovača
        this.zostavajuciCas = dlzkaCasu;
        this.casovac.start();
    }

    /**
     * Ukončí hru a zavrie aplikáciu.
     */
    private void vypniHru() {
        System.exit(0);
    }

    /**
     * Spravuje vlastnosti vybuchnutého tanku.
     * 
     * @param tank Tank, ktorý má byť spravovaný po vybuchnutí.
     */
    private void spravujVybuchnutyTank(Tank tank) {
        // Ak je tank vybuchnutý, odstránime ho zo hry
        if (tank.jeVybuchnuty()) {
            this.platno.vymazObjekt(tank);
            this.manazer.prestanSpravovatObjekt(tank);
            tank.nastavAkoVybuchnuty(false);
        }
    }

    /**
     * Resetuje vlastnosti strely na pôvodné hodnoty.
     * 
     * @param strela Strela, ktorú treba zresetovať.
     */
    private void resetniStrelu(Strela strela) {
        strela.nastavAkoTankViditelny(false);
        strela.nastavAkoVybuchnuta(false);
    }

    /**
     * Vymaže všetky prekážky z hry.
     */
    private void vymazPrekazky() {
        // Synchronizovane odstránenie všetkých prekážok zo zoznamu
        synchronized (this.zamokPrekazky) {
            Iterator<Prekazka> iterator = this.prekazky.iterator();
            while (iterator.hasNext()) {
                Prekazka zabrana = iterator.next();
                this.platno.vymazObjekt(zabrana);
                this.manazer.prestanSpravovatObjekt(zabrana);
                iterator.remove();
            }
        }
    }
}