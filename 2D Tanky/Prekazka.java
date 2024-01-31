import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Trieda Prekazka reprezentuje prekážku v hernom svete.
 * Slúži na definovanie vlastností a správaní prekážky v hre.
 * 
 * @author Daniel J.
 * 
 * @version 2.0
 */

public class Prekazka {
    private double x;
    private double y;
    private double sirka;
    private double vyska;

    /**
     * Konštruktor pre triedu Prekazka.
     * 
     * @param x     x-ová pozícia prekážky
     * @param y     y-ová pozícia prekážky
     * @param sirka šírka prekážky
     * @param vyska výška prekážky
     */
    public Prekazka(double x, double y, double sirka, double vyska) {
        this.x = x;
        this.y = y;
        this.sirka = sirka;
        this.vyska = vyska;
    }

    /**
     * Vráti tvar prekážky ako obdĺžnik.
     * 
     * @return Tvar prekážky ako Rectangle2D.Double
     */
    public Shape getShape() {
        return new Rectangle2D.Double(this.x, this.y, this.sirka, this.vyska);
    }

    /**
     * Vráti x-ovú pozíciu prekážky.
     * 
     * @return x-ová pozícia prekážky
     */
    public double getX() {
        return this.x;
    }

    /**
     * Vráti y-ovú pozíciu prekážky.
     * 
     * @return y-ová pozícia prekážky
     */
    public double getY() {
        return this.y;
    }

    /**
     * Vráti šírku prekážky.
     * 
     * @return Šírka prekážky
     */
    public double getSirka() {
        return this.sirka;
    }

    /**
     * Vráti výšku prekážky.
     * 
     * @return Výška prekážky
     */
    public double getVyska() {
        return this.vyska;
    }
}
