import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Canvas is a class to allow for simple graphical drawing on a canvas.
 * This is a modification of the general purpose Canvas, specially made for
 * the BlueJ "shapes" example.
 *
 * @author: Bruce Quig
 * @author: Michael Kolling (mik)
 * 
 * @version: 1.6.1 (shapes)
 */

public class Platno {

    private static Platno platnoSingleton;

    /**
     * Factory method to get the canvas singleton object.
     */
    public static Platno dajPlatno() {
        if (Platno.platnoSingleton == null) {
            Platno.platnoSingleton = new Platno("Tanky 2D", 1024, 768, Color.WHITE);
        }
        Platno.platnoSingleton.setVisible(true);
        return Platno.platnoSingleton;
    }

    // ----- instance part -----

    private JFrame frame;
    private CanvasPane canvas;
    private Graphics2D graphics;
    private Color pozadie;
    private Image canvasImage;
    private Timer timer;
    private List<Object> objekty;
    private HashMap<Object, IDraw> tvary;
    private boolean bezi = true;
    private HashSet<Integer> pressedKeys = new HashSet<>();
    private Color farbaPozadia = Color.WHITE;
    private List<Prekazka> prekazky;

    /**
     * Create a Canvas.
     * 
     * @param title   title to appear in Canvas Frame
     * @param width   the desired width for the canvas
     * @param height  the desired height for the canvas
     * @param bgClour the desired background colour of the canvas
     */
    private Platno(String titulok, int sirka, int vyska, Color pozadie) {
        this.frame = new JFrame();
        this.canvas = new CanvasPane();
        this.frame.setContentPane(this.canvas);
        this.frame.setTitle(titulok);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.canvas.setPreferredSize(new Dimension(sirka, vyska));
        this.timer = new Timer(25, null);
        this.timer.start();
        this.pozadie = pozadie;
        this.frame.pack();
        this.objekty = new ArrayList<>();
        this.prekazky = new ArrayList<>();
        this.tvary = new HashMap<>();

        /**
         * Frame setResizable tutorial =
         * https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/Frame.html
         */
        this.frame.setResizable(false);

        /**
         * Pridaj double buffering
         * Double buffering tutorial =
         * https://docs.oracle.com/javase/tutorial/extra/fullscreen/doublebuf.html
         */
        this.canvasImage = new BufferedImage(sirka, vyska, BufferedImage.TYPE_INT_ARGB);
        this.graphics = (Graphics2D)this.canvasImage.getGraphics();
        this.graphics.setColor(pozadie);
        this.graphics.fillRect(0, 0, sirka, vyska);
        this.graphics.setColor(Color.black);

        // Vycentruj okno na obrazovke
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.frame.getWidth()) / 2;
        int y = (screenSize.height - this.frame.getHeight()) / 2;
        this.frame.setLocation(x, y);

        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Platno.this.bezi = false;
            }
        });
    }

    /**
     * Set the canvas visibility and brings canvas to the front of screen
     * when made visible. This method can also be used to bring an already
     * visible canvas to the front of other windows.
     * 
     * @param visible boolean value representing the desired visibility of
     *                the canvas (true or false)
     */
    public void setVisible(boolean visible) {
        if (this.graphics == null) {
            // First time: instantiate the offscreen image and fill it with
            // the background colour
            Dimension size = this.canvas.getSize();
            this.canvasImage = this.canvas.createImage(size.width, size.height);
            this.graphics = (Graphics2D)this.canvasImage.getGraphics();
            this.graphics.setColor(this.pozadie);
            this.graphics.fillRect(0, 0, size.width, size.height);
            this.graphics.setColor(Color.black);
        }
        this.frame.setVisible(visible);
    }

    /**
     * Draw a given shape onto the canvas.
     * 
     * @param referenceObject an object to define identity for this shape
     * @param color           the color of the shape
     * @param shape           the shape object to be drawn on the canvas
     */
    // Note: this is a slightly backwards way of maintaining the shape
    // objects. It is carefully designed to keep the visible shape interfaces
    // in this project clean and simple for educational purposes.
    public void draw(Object object, String farba, Shape tvar) {
        this.objekty.remove(object); // just in case it was already there
        this.objekty.add(object); // add at the end
        this.tvary.put(object, new PopisTvaru(tvar, farba));
        this.redraw();
    }

    /**
     * Draw a given image onto the canvas.
     * 
     * @param referenceObject an object to define identity for this image
     * @param image           the image object to be drawn on the canvas
     * @param transform       the transformation applied to the image
     */
    // Note: this is a slightly backwards way of maintaining the shape
    // objects. It is carefully designed to keep the visible shape interfaces
    // in this project clean and simple for educational purposes.
    public void draw(Object object, BufferedImage image, AffineTransform transform) {
        this.objekty.remove(object); // just in case it was already there
        this.objekty.add(object); // add at the end
        this.tvary.put(object, new PopisObrazku(image, transform));
        this.redraw();
    }

    /**
     * Metóda na vykreslenie textu na plátno.
     * 
     * @param text     Text, ktorý sa má vykresliť.
     * @param x        X-ová pozícia textu.
     * @param y        Y-ová pozícia textu.
     * @param fontSize Veľkosť písma textu.
     */
    public void draw(String text, int x, double d, int fontSize) {
        // Získanie grafického kontextu z obrázku plátna
        Graphics2D graphic = (Graphics2D)this.canvasImage.getGraphics();

        // Nastavenie fontu písma a farby textu
        graphic.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        graphic.setColor(Color.black);

        // Vykreslenie textu na plátno
        graphic.drawString(text, x, (int)d);

        // Prekreslenie celého plátna
        this.canvas.repaint();
    }

    /**
     * Metóda na vykreslenie prekážky na hracej ploche.
     * 
     * @param x     X-ová pozícia prekážky.
     * @param y     Y-ová pozícia prekážky.
     * @param sirka Šírka prekážky.
     * @param vyska Výška prekážky.
     */
    public void nakresliPrekazku(double x, double y, double sirka, double vyska) {
        // Vytvorí novú prekážku s danými parametrami
        Prekazka zabrana = new Prekazka(x, y, sirka, vyska);

        // Pridá prekážku do zoznamu prekážok
        this.prekazky.add(zabrana);

        // Aktualizuje grafické reprezentácie prekážok
        this.prekresliPrekazky();

        // Vyvolá prekreslenie celého plátna
        this.redraw();
    }

    /**
     * Metóda na prekreslenie prekážok na hracej ploche.
     */
    private void prekresliPrekazky() {
        // Vytvorí kopiu zoznamu prekážok pre bezpečné iterovanie
        List<Prekazka> copyPrekazky = new ArrayList<>(this.prekazky);

        // Prejde všetky prekážky v kópii a pridá ich do mapy tvarov, ak ešte nie sú
        // obsiahnuté
        for (Prekazka prekazka : copyPrekazky) {
            if (!this.tvary.containsKey(prekazka)) {
                this.tvary.put(prekazka, new PopisTvaru(prekazka.getShape(), null));
            }
        }
    }

    /**
     * Metóda na kontrolu kolízie medzi tankom a prekážkami.
     * 
     * @param tank Tank, ktorého kolíziu kontrolujeme.
     * @return true, ak došlo k kolízii, inak false.
     */
    public boolean nastalaKoliziaTankuPrekazky(Tank tank) {
        // Vytvorí kopiu zoznamu objektov pre bezpečné iterovanie
        List<Object> kopiaOfobjecty = new ArrayList<>(this.objekty);

        // Prejde všetky objekty v kópii a zistí, či tank koliduje s nejakou prekážkou
        for (Object object : kopiaOfobjecty) {
            if (object instanceof Prekazka) {
                Prekazka zabrana = (Prekazka)object;

                // Kontroluje kolíziu pomocou tvarov objektov
                if (tank.getShape().intersects(zabrana.getShape().getBounds2D())) {
                    return true; // Kolízia nastala
                }
            }
        }
        return false; // Žiadna kolízia
    }

    /**
     * Erase a given shape's from the screen.
     * 
     * @param referenceObject the shape object to be erased
     */
    public void erase(Object object) {
        this.objekty.remove(object);
        this.tvary.remove(object);
        this.redraw();
    }

    /**
     * Nastaví prednastavenú farbu popredia na základe zadaného farbového názvu.
     * 
     * @param color Farba popredia na nastavenie.
     */
    public void setForegroundColor(String color) {
        if (color.equals("green")) {
            this.nastavPozadieZelenejFarby();
        } else if (color.equals("white")) {
            this.nastavPozadieBielejFarby();
        } else {
            switch (color) {
                case "Červená":
                    this.graphics.setColor(Color.red);
                    break;
                case "Azúrová":
                    this.graphics.setColor(Color.cyan);
                    break;
                case "Sivá":
                    this.graphics.setColor(Color.gray);
                    break;
                case "Oranžová":
                    this.graphics.setColor(Color.orange);
                    break;
                case "Ružová":
                    this.graphics.setColor(Color.pink);
                    break;
                case "Čierna":
                    this.graphics.setColor(Color.black);
                    break;
                case "Modrá":
                    this.graphics.setColor(Color.blue);
                    break;
                case "Žltá":
                    this.graphics.setColor(Color.yellow);
                    break;
                case "green":
                    this.graphics.setColor(Color.green);
                    break;
                case "Purpurová":
                    this.graphics.setColor(Color.magenta);
                    break;
                case "white":
                    this.graphics.setColor(Color.white);
                    break;
                default:
                    this.graphics.setColor(Color.gray);
                    break;
            }
        }
    }

    /**
     * Wait for a specified number of milliseconds before finishing.
     * This provides an easy way to specify a small delay which can be
     * used when producing animations.
     * 
     * @param milliseconds the number
     */
    public void wait(int milisekundy) {
        try {
            Thread.sleep(milisekundy);
        } catch (Exception e) {
            System.out.println("Cakanie sa nepodarilo");
        }
    }

    /**
     * * Redraw all shapes currently on the Canvas.
     */
    private void redraw() {
        this.erase();
        this.prekresliPrekazky();

        // Create a copy of the objects list to avoid ConcurrentModificationException
        List<Object> copyOfObjekty = new ArrayList<>(this.objekty);

        for (Object tvar : copyOfObjekty) {
            if (tvar != null && this.tvary.containsKey(tvar) && this.tvary.get(tvar) != null) {
                this.tvary.get(tvar).draw(this.graphics);
            } else {
                this.objekty.remove(tvar); // Remove null or invalid objects from the original list
            }
        }

        this.canvas.repaint();
    }

    /**
     * Erase the whole canvas. (Does not repaint.)
     */
    private void erase() {
        Color original = this.graphics.getColor();
        this.graphics.setColor(this.pozadie);
        Dimension size = this.canvas.getSize();
        this.graphics.fill(new Rectangle(0, 0, size.width, size.height));
        this.graphics.setColor(original);
    }

    /**
     * Pridáva poslúchača klávesnice pre okno.
     * 
     * @param listener Poslúchač klávesnice.
     */
    public void addKeyListener(KeyListener listener) {
        this.frame.addKeyListener(listener);
    }

    /**
     * Pridáva poslúchača myši pre plátno.
     * 
     * @param listener Poslúchač myši.
     */
    public void addMouseListener(MouseListener listener) {
        this.canvas.addMouseListener(listener);
    }

    /**
     * Pridáva poslúchača pre časovač.
     * 
     * @param listener Poslúchač pre časovač.
     */
    public void addTimerListener(ActionListener listener) {
        this.timer.addActionListener(listener);
    }

    /**
     * Kontroluje, či je daný kláves stlačený.
     * 
     * @param keyCode Kód stlačeného klávesu.
     * @return True, ak je kláves stlačený, inak false.
     */
    public boolean isKeyPressed(int keyCode) {
        return this.pressedKeys.contains(keyCode);
    }

    /**
     * Pridáva poslúchača okna.
     * 
     * @param listener Poslúchač okna.
     */
    public void addWindowListener(WindowAdapter listener) {
        this.frame.addWindowListener(listener);
    }

    /************************************************************************
     * Inner class CanvasPane - the actual canvas component contained in the
     * Canvas frame. This is essentially a JPanel with added capability to
     * refresh the image drawn on it.
     */
    private class CanvasPane extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(Platno.this.canvasImage, 0, 0, this);
            Toolkit.getDefaultToolkit().sync();
        }
    }

    /***********************************************************************
     * Inner interface IDraw - defines functions that need to be supported by
     * shapes descriptors
     */
    private interface IDraw {
        void draw(Graphics2D graphics);
    }

    /************************************************************************
     * Inner class CanvasPane - the actual canvas component contained in the
     * Canvas frame. This is essentially a JPanel with added capability to
     * refresh the image drawn on it.
     */

    /**
     * Reprezentuje popis tvaru pre kreslenie na plátne.
     */
    private class PopisTvaru implements IDraw {
        private Shape tvar;
        private String farba;

        /**
         * Inicializuje popis tvaru s daným tvarom a farbou.
         * 
         * @param tvar  Tvar, ktorý bude popísaný.
         * @param farba Farba, ktorou sa tvar vyplní (prednastavená na "Sivá", ak je
         *              farba null).
         */
        private PopisTvaru(Shape tvar, String farba) {
            this.tvar = tvar;
            this.farba = (farba != null) ? farba : "Sivá"; // Predvolená farba, ak je farba null
        }

        /**
         * Kreslí tvar na plátne s nastavenou farbou.
         * 
         * @param graphics Grafika na kreslenie.
         */
        public void draw(Graphics2D graphics) {
            Platno.this.setForegroundColor(this.farba);
            graphics.fill(this.tvar);
        }
    }

    /**
     * Reprezentuje popis obrazku pre kreslenie na plátne.
     */
    private class PopisObrazku implements IDraw {
        private BufferedImage obrazok;
        private AffineTransform transformacia;

        /**
         * Inicializuje popis obrazku s daným obrazkom a transformáciou.
         * 
         * @param obrazok       Obrazok na kreslenie.
         * @param transformacia Transformácia, ktorá sa použije pri kreslení obrazka.
         */
        private PopisObrazku(BufferedImage obrazok, AffineTransform transformacia) {
            this.obrazok = obrazok;
            this.transformacia = transformacia;
        }

        /**
         * Kreslí obrazok na plátne s použitím nastavenej transformácie.
         * 
         * @param graphics Grafika na kreslenie.
         */
        public void draw(Graphics2D graphics) {
            graphics.drawImage(this.obrazok, this.transformacia, null);
        }
    }

    /**
     * Získava zoznam objektov, ktoré sú momentálne na plátne.
     * 
     * @return Zoznam objektov na plátne.
     */
    public List<Object> getObjekty() {
        return this.objekty;
    }

    /**
     * Získava zaostrenie na okno pre klávesnicu.
     */
    public void zameranieNaOkno() {
        this.frame.requestFocusInWindow();
    }

    /**
     * Získava šírku plátna.
     * 
     * @return Šírka plátna.
     */
    public int getSirka() {
        return this.canvas.getWidth();
    }

    /**
     * Získava výšku plátna.
     * 
     * @return Výška plátna.
     */
    public int getVyska() {
        return this.canvas.getHeight();
    }

    /**
     * Nastavuje pozadie plátne na zelenú farbu a prekresľuje plátno.
     */
    public void nastavPozadieZelenejFarby() {
        this.pozadie = Color.green;
        this.redraw();
    }

    /**
     * Nastavuje pozadie plátne na bielu farbu a prekresľuje plátno.
     */
    public void nastavPozadieBielejFarby() {
        this.pozadie = Color.white;
        this.redraw();
    }

    /**
     * Získava farbu pozadia plátne.
     * 
     * @return Farba pozadia plátne.
     */
    public Color getFarbuPozadia() {
        return this.farbaPozadia;
    }

    /**
     * Kontroluje, či plátno beží.
     * 
     * @return True, ak plátno beží, inak false.
     */
    public boolean bezi() {
        return this.bezi;
    }

    /**
     * Získava zoznam prekážok na plátne.
     * 
     * @return Zoznam prekážok na plátne.
     */
    public List<Prekazka> getPrekazky() {
        return this.prekazky;
    }

    /**
     * Odstráni objekt z plátna.
     * 
     * @param object Objekt, ktorý sa má odstrániť z plátna.
     */
    public void vymazObjekt(Object object) {
        this.objekty.remove(object);
    }
}