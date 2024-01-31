import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;

/**
 * Trieda Tank reprezentuje (hráčov) tank v hre.
 * Slúži na definovanie vlastností a správania tanku počas hry.
 * 
 * @author Daniel J.
 * 
 * @version 3.0
 */

public class Tank {
    private double x;
    private double y;
    private double rychlost;
    private double sirka;
    private double vyska;
    private double predosleX;
    private double predosleY;
    private boolean jeVybuchnuty;
    private double smer;
    private int skore;

    /**
     * Konštruktor pre vytvorenie objektu Tank.
     * 
     * @param x x-ová pozícia tanku
     * @param y y-ová pozícia tanku
     */
    public Tank(double x, double y) {
        this.x = x;
        this.y = y;
        this.rychlost = 5;
        this.sirka = 30;
        this.vyska = 30;
        this.predosleX = x;
        this.predosleY = y;
        this.jeVybuchnuty = false;
        this.smer = 0;
        this.skore = 0;
    }

    /**
     * Vráti tvar tanku ako Java AWT Shape.
     * 
     * @return Tvar tanku
     */
    public Shape getShape() {
        // Definícia veľkostí a tvaru častí tanku
        double sirkaKanonu = 8;
        double vyskaKanonu = 12;
        double sirkaTela = 30;
        double vyskaTela = 30;
        double vyskaKolesa = 4;

        // Vytvorenie transformačnej matice pre otáčanie tanku podľa aktuálneho smeru
        AffineTransform rotation = AffineTransform.getRotateInstance(Math.toRadians(this.smer), this.x + sirkaTela / 2,
                this.y + vyskaTela / 2);

        // Vytvorenie tela tanku a jeho otáčanie
        Rectangle2D.Double tankTelo = new Rectangle2D.Double(this.x, this.y, this.sirka, this.vyska);
        Shape otoceneTeloTanku = rotation.createTransformedShape(tankTelo);

        // Vytvorenie kanóna a jeho otáčanie
        Rectangle2D.Double kanon = new Rectangle2D.Double(this.x + (sirkaTela - sirkaKanonu) / 2, this.y - vyskaKanonu,
                sirkaKanonu, vyskaKanonu);
        Shape otocenyKanon = rotation.createTransformedShape(kanon);

        // Vytvorenie kolies a ich otáčanie
        Rectangle2D.Double laveKoleso = new Rectangle2D.Double(this.x, this.y + vyskaTela, sirkaTela / 2, vyskaKolesa);
        Rectangle2D.Double praveKoleso = new Rectangle2D.Double(this.x + sirkaTela / 2, this.y + vyskaTela,
                sirkaTela / 2, vyskaKolesa);
        Shape otoceneLaveKoleso = rotation.createTransformedShape(laveKoleso);
        Shape otocenePraveKoleso = rotation.createTransformedShape(praveKoleso);

        // Vytvorenie oblasti tvoriacej tvar celého tanku
        java.awt.geom.Area tvarTanku = new java.awt.geom.Area(otoceneTeloTanku);
        tvarTanku.add(new java.awt.geom.Area(otocenyKanon));
        tvarTanku.add(new java.awt.geom.Area(otoceneLaveKoleso));
        tvarTanku.add(new java.awt.geom.Area(otocenePraveKoleso));

        return tvarTanku;
    }

    /**
     * Kontroluje, či je možné pohnúť tankom na nové súradnice a zmeniť jeho smer.
     * 
     * @param noveX    Nová x-ová pozícia tanku
     * @param noveY    Nová y-ová pozícia tanku
     * @param novySmer Nový smer tanku
     */
    private void mozeSaHybat(double noveX, double noveY, double novySmer) {
        // Kontrola a nastavenie novej pozície tanku len ak nie je kolízia s inými
        // objektami
        if (this.mozeSaHybat(noveX, noveY)) {
            this.x = noveX;
            this.y = noveY;
            this.smer = novySmer;
        }
    }

    /**
     * Pohne tankom v zadanom smere podľa zvoleného typu pohybu.
     *
     * @param smer Typ pohybu (HORE, DOLE, DO_LAVA, DO_PRAVA)
     */
    public void pohniSa(Pohyb smer) {
        // Základné pohyby tanku bez kontroly prekážok
        switch (smer) {
            case HORE:
                this.mozeSaHybat(this.x, this.y - this.rychlost, 0);
                break;
            case DOLE:
                this.mozeSaHybat(this.x, this.y + this.rychlost, 180);
                break;
            case DO_LAVA:
                this.mozeSaHybat(this.x - this.rychlost, this.y, 270);
                break;
            case DO_PRAVA:
                this.mozeSaHybat(this.x + this.rychlost, this.y, 90);
                break;
        }
    }

    /**
     * Pohne tankom v zadanom smere podľa zvoleného typu pohybu a zohľadní prípadné
     * prekážky.
     *
     * @param smer Typ pohybu (HORE, DOLE, DO_LAVA, DO_PRAVA)
     */
    public void pohniSaSPrekazkou(Pohyb smer) {
        // Pohyb tanku s kontrolou prekážok
        switch (smer) {
            case HORE:
                this.mozeSaHybat(this.x, this.y - this.rychlost, 0);
                break;
            case DOLE:
                this.mozeSaHybat(this.x, this.y + this.rychlost, 180);
                break;
            case DO_LAVA:
                this.mozeSaHybat(this.x - this.rychlost, this.y, 270);
                break;
            case DO_PRAVA:
                this.mozeSaHybat(this.x + this.rychlost, this.y, 90);
                break;
        }
    }

    /**
     * Kontroluje, či je možné pohnúť tankom na zadané súradnice bez kolízie s inými
     * objektami.
     *
     * @param suradnica1 Nová x-ová pozícia tanku
     * @param suradnica2 Nová y-ová pozícia tanku
     * @return True, ak je možné pohnúť tankom, inak false
     */
    private boolean mozeSaHybat(double suradnica1, double suradnica2) {
        // Konštanty pre veľkosť hitboxu
        double velkostHitboxu1 = 30;

        // Kontrola, či tank nie je mimo hranice hracej plochy
        if (!(suradnica1 >= 0 && suradnica2 >= 0 && suradnica1 + velkostHitboxu1 <= Platno.dajPlatno().getSirka()
                && suradnica2 + velkostHitboxu1 <= Platno.dajPlatno().getVyska())) {
            return false;
        }

        // Kontrola kolízie s inými tankami
        ArrayList<Object> kopiaobjectov = new ArrayList<>(Platno.dajPlatno().getObjekty());
        for (Object object : kopiaobjectov) {
            if (object instanceof Tank && object != this) {
                Tank tank = (Tank)object;
                double velkostHitboxu2 = 30;

                if (suradnica1 < tank.getX() + velkostHitboxu2 &&
                        suradnica1 + velkostHitboxu1 > tank.getX() &&
                        suradnica2 + velkostHitboxu1 > tank.getY() &&
                        suradnica2 < tank.getY() + velkostHitboxu2) {
                    // Kolízia s iným tankom,= pohyb nie je možný
                    return false;
                }
            }
        }

        // Kontrola kolízie s prekážkami
        ArrayList<Object> objectsKopia = new ArrayList<>(Platno.dajPlatno().getObjekty());
        for (Object object : objectsKopia) {
            if (object instanceof Prekazka) {
                Prekazka zabrana = (Prekazka)object;
                if (suradnica1 < zabrana.getX() + zabrana.getSirka() &&
                        suradnica1 + velkostHitboxu1 > zabrana.getX() &&
                        suradnica2 + velkostHitboxu1 > zabrana.getY() &&
                        suradnica2 < zabrana.getY() + zabrana.getVyska()) {
                    // Kolízia s prekážkou,= pohyb nie je možný
                    return false;
                }
            }
        }

        // Pohyb je možný,= žiadne kolízie
        return true;
    }

    /**
     * Kontroluje, či tank presahuje zadanú prekážku.
     *
     * @param prekazka Prekážka na kontrolu presahovania
     * @return True, ak tank presahuje prekážku, inak false
     */
    public boolean presahuje(Prekazka prekazka) {
        return this.getShape().getBounds2D().intersects(prekazka.getShape().getBounds2D());
    }

    /**
     * Aktualizuje stav tanku na základe aktuálnych podmienok a udalostí v hre.
     */
    public void update() {
        // Kontrola, či tank nie je vybuchnutý
        if (!this.jeVybuchnuty) {
            // Uloženie predchádzajúcej pozície tanku
            this.predosleX = this.x;
            this.predosleY = this.y;

            // Kopírovanie objektov
            ArrayList<Object> objectsKopia = new ArrayList<>(Platno.dajPlatno().getObjekty());
            Strela strelaToRemove = null;

            // Prechádzanie objektov v hre
            for (Object object : objectsKopia) {
                try {
                    // Kontrola kolízie so strelou
                    if (object instanceof Strela) {
                        Strela strela = (Strela)object;
                        if (strela.jeTankViditelny() && strela.getShape().intersects(this.getShape().getBounds2D())
                                && strela.getTank() != this) {
                            // Tank vybuchne pri zásahu strelou
                            this.vybuchnuty();
                            strela.nastavAkoTankViditelny(false);
                            strelaToRemove = strela;
                            return;
                        }
                    } else if (object instanceof Tank && object != this) {
                        // Kontrola kolízie s iným tankom
                        if (!this.mozeSaHybat(this.x, this.y)) {
                            // Vráti tank na predchádzajúcu pozíciu
                            this.x = this.predosleX;
                            this.y = this.predosleY;
                        }
                    } else if (object instanceof Prekazka) {
                        // Kontrola kolízie s prekážkou
                        Prekazka zabrana = (Prekazka)object;
                        if (this.getShape().intersects(zabrana.getShape().getBounds2D())) {
                            // Vráti tank na predchádzajúcu pozíciu pri kolízii s prekážkou
                            this.x = this.predosleX;
                            this.y = this.predosleY;
                            return;
                        }
                    }
                } catch (ConcurrentModificationException e) {
                    e.printStackTrace();
                }
            }

            // Odstránenie strely po kolízii
            if (strelaToRemove != null) {
                Platno.dajPlatno().vymazObjekt(strelaToRemove);
            }
        }
    }

    /**
     * Vráti šírku tanku.
     * 
     * @return Šírka tanku
     */
    public double getSirka() {
        return this.sirka;
    }

    /**
     * Vráti výšku tanku.
     * 
     * @return Výška tanku
     */
    public double getVyska() {
        return this.vyska;
    }

    /**
     * Vráti x-ovú pozíciu tanku.
     * 
     * @return x-ová pozícia tanku
     */
    public double getX() {
        return this.x;
    }

    /**
     * Vráti y-ovú pozíciu tanku.
     * 
     * @return y-ová pozícia tanku
     */
    public double getY() {
        return this.y;
    }

    /**
     * Vráti aktuálny smer, do ktorého je natočený tank.
     *
     * @return Aktuálny smer tanku
     */
    public double getSmer() {
        return this.smer;
    }

    /**
     * Vráti aktuálny stav, či je tank vybuchnutý.
     *
     * @return True, ak je tank vybuchnutý, inak false
     */
    public boolean jeVybuchnuty() {
        return this.jeVybuchnuty;
    }

    /**
     * Nastaví stav vybuchnutia tanku na true.
     */
    public void vybuchnuty() {
        this.jeVybuchnuty = true;
    }

    /**
     * Nastaví stav vybuchnutia tanku podľa zadaného boolean hodnoty.
     *
     * @param vybuchnuty Nový stav vybuchnutia tanku
     */
    public void nastavAkoVybuchnuty(boolean vybuchnuty) {
        this.jeVybuchnuty = vybuchnuty;
    }

    /**
     * Vráti aktuálne skóre tanku.
     *
     * @return Skóre tanku
     */
    public int getSkore() {
        return this.skore;
    }

    /**
     * Zvýši aktuálne skóre tanku o jedna.
     */
    public void zvysSkore() {
        this.skore++;
    }

    /**
     * Nastaví aktuálne skóre tanku na zadanú hodnotu.
     *
     * @param skore Nová hodnota skóre
     */
    public void nastavSkore(int skore) {
        this.skore = skore;
    }

    /**
     * Vráti rýchlosť tanku.
     *
     * @return Rýchlosť tanku
     */
    public double getRychlost() {
        return this.rychlost;
    }

    /**
     * Nastaví pozíciu tanku s predom definovaným odstupom od okraja hracej plochy a
     * kontroluje kolízie s prekážkami.
     *
     * @param odstupOdOkraja Odstup od okraja hracej plochy
     * @param random         Generátor náhodných čísel
     */
    public void prednadstavPoziciu(int odstupOdOkraja, Random random) {
        // Nastavenie pozície tanku s kontrolou kolízií
        boolean kolizia;

        do {
            this.x = odstupOdOkraja + random.nextInt(Platno.dajPlatno().getSirka() - 2 * odstupOdOkraja);
            this.y = odstupOdOkraja + random.nextInt(Platno.dajPlatno().getVyska() - 2 * odstupOdOkraja);

            // Skontroluj koliziu s prekážkami
            kolizia = Platno.dajPlatno().nastalaKoliziaTankuPrekazky(this);

        } while (kolizia);
    }

}