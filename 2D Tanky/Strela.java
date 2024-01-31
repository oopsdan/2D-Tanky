import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Trieda Strela reprezentuje strely v hre.
 * Slúži na definovanie vlastností a správania strely počas hry.
 * 
 * @author Daniel J.
 * 
 * @version 3.0
 */

public class Strela {
    private double x;
    private double y;
    private double smer;
    private double rychlost;
    private double priemer;
    private boolean jeTankViditelny;
    private boolean jeStrelaViditelna;
    private boolean vybuchnuta;
    private Tank tank;

    /**
     * Konštruktor pre triedu Strela.
     * 
     * @param tank Tank, ktorý vystrelil strelu
     */
    public Strela(Tank tank) {
        this.x = 0;
        this.y = 0;
        this.smer = 0;
        this.rychlost = 8;
        this.priemer = 7;
        this.jeTankViditelny = false;
        this.jeStrelaViditelna = false;
        this.vybuchnuta = false;
        this.tank = tank;
    }

    /**
     * Metóda na vystrelenie strely z tanku.
     * 
     * @return True, ak bola strela úspešne vystrelená, inak False
     */
    public boolean vystrel() {
        // Kontrola, či tank nie je vybuchnutý a strela nie je viditeľná
        if (!this.tank.jeVybuchnuty() && !this.jeTankViditelny) {
            // Získanie súradníc tanku (stredových)
            double tankStredX = this.tank.getX() + this.tank.getSirka() / 2;
            double tankStredY = this.tank.getY() + this.tank.getVyska() / 2;

            // Nastavenie pozície a smeru strely v závislosti od smeru tanku
            this.jeTankViditelny = true;
            switch ((int)this.tank.getSmer()) {
                case 0:
                    this.x = tankStredX - 2.5;
                    this.y = this.tank.getY() - 5;
                    this.smer = 270;
                    break;
                case 90:
                    this.x = this.tank.getX() + this.tank.getSirka();
                    this.y = tankStredY - 2.5;
                    this.smer = 0;
                    break;
                case 180:
                    this.x = tankStredX - 2.5;
                    this.y = this.tank.getY() + this.tank.getVyska();
                    this.smer = 90;
                    break;
                case 270:
                    this.x = this.tank.getX() - 10;
                    this.y = tankStredY - 2.5;
                    this.smer = 180;
                    break;
                default:
                    this.smer = 0;
                    break;
            }

            return true; // Strela bola úspešne vystrelená
        }
        return false; // Strela nebola vystrelená
    }

    /**
     * Metóda na aktualizáciu pozície a kontroly kolízií strely.
     */
    public void update() {
        // Inicializácia zoznamu tankov, ktoré majú byť vymazané
        List<Tank> vymazTanky = new ArrayList<>();

        if (this.jeTankViditelny) {
            // Aktualizácia pozície strely na základe smeru a rýchlosti
            this.x += this.rychlost * Math.cos(Math.toRadians(this.smer));
            this.y += this.rychlost * Math.sin(Math.toRadians(this.smer));

            // Kontrola kolízií (so všetkými objektami v hre)
            List<Object> objectsKopia = new ArrayList<>(Platno.dajPlatno().getObjekty());
            for (Object object : objectsKopia) {
                // Kontrola kolízie so prekážkou
                if (object instanceof Prekazka) {
                    Prekazka zabrana = (Prekazka)object;
                    if (zabrana.getShape().intersects(this.getShape().getBounds2D())) {
                        this.jeTankViditelny = false;
                        return;
                    }
                }
            }

            // Kontrola, či strela neopustila hraciu plochu
            if (this.x < 0 || this.x > Platno.dajPlatno().getSirka() || this.y < 0
                    || this.y > Platno.dajPlatno().getVyska()) {
                this.jeTankViditelny = false;
            } else {
                // Kontrola kolízií so všetkými tankami
                for (Object object : Platno.dajPlatno().getObjekty()) {
                    if (object instanceof Tank) {
                        Tank susednyTank = (Tank)object;
                        if (!susednyTank.equals(this.tank) && !susednyTank.jeVybuchnuty() &&
                                this.getShape().intersects(susednyTank.getShape().getBounds2D())) {
                            vymazTanky.add(susednyTank);
                            this.jeTankViditelny = false;
                        }
                    }
                }
            }
        } else {
            // Nastavenie pozície strely na pozíciu tanku, ak nie je viditeľná
            this.x = this.tank.getX() + (this.tank.getSirka() - 10) / 2;
            this.y = this.tank.getY();
        }

        // Vymazanie tankov, ktoré boli zasiahnuté strelou
        for (Tank vymazTank : vymazTanky) {
            Platno.dajPlatno().vymazObjekt(vymazTank);
            vymazTank.nastavAkoVybuchnuty(true);
            this.tank.zvysSkore();
        }
    }

    /**
     * Metóda vráti tvar strely ako elipsu (kruh).
     * 
     * @return Tvar strely ako Ellipse2D.Double
     */
    public Shape getShape() {
        return new Ellipse2D.Double(this.x, this.y, this.priemer, this.priemer);
    }

    /**
     * Vráti odkaz na tank, ktorý vystrelil strelu.
     * 
     * @return Odkaz na tank
     */
    public Tank getTank() {
        return this.tank;
    }

    /**
     * Indikuje, či je tank viditeľný.
     * 
     * @return True, ak je tank viditeľný, inak False
     */
    public boolean jeTankViditelny() {
        return this.jeTankViditelny;
    }

    /**
     * Nastaví, či je tank viditeľný.
     * 
     * @param jeTankViditelny True, ak je tank viditeľný, inak False
     */
    public void nastavAkoTankViditelny(boolean jeTankViditelny) {
        this.jeTankViditelny = jeTankViditelny;
    }

    /**
     * Nastaví x-ovú pozíciu strely.
     * 
     * @param x x-ová pozícia strely
     */
    public void nastavX(double x) {
        this.x = x;
    }

    /**
     * Nastaví y-ovú pozíciu strely.
     * 
     * @param y y-ová pozícia strely
     */
    public void nastavY(double y) {
        this.y = y;
    }

    /**
     * Vráti x-ovú pozíciu strely.
     * 
     * @return x-ová pozícia strely
     */
    public double getX() {
        return this.x;
    }

    /**
     * Vráti y-ovú pozíciu strely.
     * 
     * @return y-ová pozícia strely
     */
    public double getY() {
        return this.y;
    }

    /**
     * Indikuje, či je strela viditeľná.
     * 
     * @return True, ak je strela viditeľná, inak False
     */
    public boolean jeStrelaViditelna() {
        return this.jeStrelaViditelna;
    }

    /**
     * Indikuje, či strela vybuchla.
     * 
     * @return True, ak strela vybuchla, inak False
     */
    public boolean jeVybuchnuta() {
        return this.vybuchnuta;
    }

    /**
     * Nastaví, či strela vybuchla.
     * 
     * @param vybuchnuta True, ak strela vybuchla, inak False
     */
    public void nastavAkoVybuchnuta(boolean vybuchnuta) {
        this.vybuchnuta = vybuchnuta;
    }
}
