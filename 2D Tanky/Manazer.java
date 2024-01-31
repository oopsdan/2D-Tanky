import java.util.ArrayList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Automaticky posiela spravy danym objektom:<br />
 * posunDole() - pri stlačení klávesy DOWN<br />
 * posunHore() - pri stlačení klávesy UP<br />
 * posunVlavo() - pri stlačení klávesy LEFT<br />
 * posunVpravo() - pri stlačení klávesy RIGHT<br />
 * aktivuj() - pri stlačení klávesy ENTER alebo SPACE<br />
 * zrus() - pri stlačení klávesy ESC<br />
 * tik() - každých 0,25 sekundy<br />
 * vyberSuradnice(x, y) - pri kliknutí myšou
 */

public class Manazer {
    private ArrayList<Object> spravovaneObjekty;
    private ArrayList<Integer> vymazaneObjekty;

    /**
     * Implementácia KeyAdapter pre spracovanie klávesových udalostí
     */
    private class ManazerKlaves extends KeyAdapter {
        public void keyPressed(KeyEvent event) {
            // Posielanie správ na základe stlačených kláves
            if (event.getKeyCode() == KeyEvent.VK_DOWN) {
                Manazer.this.posliSpravu("posunDole");
            } else if (event.getKeyCode() == KeyEvent.VK_UP) {
                Manazer.this.posliSpravu("posunHore");
            } else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
                Manazer.this.posliSpravu("posunVlavo");
            } else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
                Manazer.this.posliSpravu("posunVpravo");
            } else if (event.getKeyCode() == KeyEvent.VK_SPACE || event.getKeyCode() == KeyEvent.VK_ENTER) {
                Manazer.this.posliSpravu("aktivuj");
            } else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                Manazer.this.posliSpravu("zrus");
            }
        }
    }

    /**
     * Implementácia MouseAdapter pre spracovanie myšových udalostí
     */
    private class ManazerMysi extends MouseAdapter {
        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                Manazer.this.posliSpravu("vyberSuradnice", event.getX(), event.getY());
            }
        }
    }

    /**
     * Odoslanie správy zvoleným objektom zo zoznamu spravovaných objektov.
     * 
     * @param selektor Názov metódy (správy), ktorá sa má zavolať na každom objekte.
     */
    private void posliSpravu(String selektor) {
        // Vytvorenie kopie zoznamu spravovaných objektov
        List<Object> kopiaSpravovanychObjektov = new ArrayList<>(this.spravovaneObjekty);
        // Zoznam objektov, ktoré budú odstránené po iterácii
        List<Object> objektyNaOdstranenie = new ArrayList<>();

        // Prechádzanie cez kópiu zoznamu spravovaných objektov
        for (Object adresat : kopiaSpravovanychObjektov) {
            try {
                // Overenie, či objekt nie je null
                if (adresat != null) {
                    // Získanie metódy podľa názvu (selektora)
                    Method sprava = adresat.getClass().getMethod(selektor);
                    // Volanie metódy na objekte
                    sprava.invoke(adresat);
                }
            } catch (ReflectiveOperationException e) {
                // Uloženie objektov na odstránenie, ktoré spôsobili výnimku pri volaní metódy
                objektyNaOdstranenie.add(adresat);
            }
        }

        // Odstránenie objektov zo zoznamu po iterácii
        this.spravovaneObjekty.removeAll(objektyNaOdstranenie);
        // Odstránenie vymazaných objektov zo zoznamu
        this.odstranVymazaneObjekty();
    }

    /**
     * Odoslanie správy zvoleným objektom so zadanými parametrami zo zoznamu
     * spravovaných objektov.
     * 
     * @param selektor       Názov metódy (správy), ktorá sa má zavolať na každom
     *                       objekte.
     * @param prvyParameter  Prvý parameter pre volanú metódu.
     * @param druhyParameter Druhý parameter pre volanú metódu.
     */
    private void posliSpravu(String selektor, int prvyParameter, int druhyParameter) {
        // Vytvorenie kopie zoznamu spravovaných objektov
        List<Object> kopiaSpravovanychObjektov = new ArrayList<>(this.spravovaneObjekty);

        // Prechádzanie cez kópiu zoznamu spravovaných objektov
        for (Object adresat : kopiaSpravovanychObjektov) {
            try {
                // Overenie, či objekt nie je null
                if (adresat != null) {
                    // Získanie metódy podľa názvu (selektora) a parametrov typu Integer.TYPE,
                    // Integer.TYPE
                    Method sprava = adresat.getClass().getMethod(selektor, Integer.TYPE, Integer.TYPE);
                    // Volanie metódy na objekte s danými parametrami
                    sprava.invoke(adresat, prvyParameter, druhyParameter);
                }
            } catch (ReflectiveOperationException e) {
                // Odstránenie objektu spôsobujúceho výnimku zo zoznamu spravovaných objektov
                this.spravovaneObjekty.remove(adresat);
                // Odstránenie vymazaných objektov zo zoznamu po zmene
                this.odstranVymazaneObjekty();
            }
        }
    }

    /**
     * Odstránenie vymazaných objektov zo zoznamu spravovaných objektov.
     */
    private void odstranVymazaneObjekty() {
        // Kontrola, či zoznam vymazaných objektov nie je prázdny
        if (!this.vymazaneObjekty.isEmpty()) {
            // Triedenie zoznamu vymazaných objektov v zostupnom poradí
            Collections.sort(this.vymazaneObjekty, Collections.reverseOrder());

            // Iterátor prechádzajúci cez zoznam vymazaných objektov
            Iterator<Integer> iterator = this.vymazaneObjekty.iterator();
            while (iterator.hasNext()) {
                // Získanie indexu objektu na odstránenie
                int index = iterator.next();
                // Odstránenie posledného prvku vráteného iterátorom
                iterator.remove();

                // Kontrola, či index je v platnom rozsahu zoznamu spravovaných objektov
                if (index >= 0 && index < this.spravovaneObjekty.size()) {
                    // Nastavenie hodnoty na null na danom indexe
                    this.spravovaneObjekty.set(index, null);
                }
            }

            // Odstránenie všetkých null hodnôt zo zoznamu spravovaných objektov
            this.spravovaneObjekty.removeIf(object -> object == null);
        }
    }

    /**
     * Vytvára nový manažér, ktorý zatiaľ nespravuje žiadne objekty.
     */
    public Manazer() {
        // Inicializácia zoznamov a pridanie poslucháčov na klávesnicu, myš a časovač
        this.spravovaneObjekty = new ArrayList<>();
        this.vymazaneObjekty = new ArrayList<>();
        Platno.dajPlatno().addKeyListener(new ManazerKlaves());
        Platno.dajPlatno().addMouseListener(new ManazerMysi());
    }

    /**
     * Manažér bude spravovať daný objekt.
     */
    public void spravujObjekt(Object objekt) {
        this.spravovaneObjekty.add(objekt);
    }

    /**
     * Manažér prestane spravovať daný objekt.
     */
    public void prestanSpravovatObjekt(Object objekt) {
        int index = this.spravovaneObjekty.indexOf(objekt);
        if (index >= 0) {
            this.spravovaneObjekty.set(index, null);
            this.vymazaneObjekty.add(index);
        }
    }
}
