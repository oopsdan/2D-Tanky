/**
 * Hlavný vstupný bod pre spustenie hry.
 * Táto trieda inicializuje potrebné objekty a spúšťa hru na základe
 * užívateľských vstupov.
 * Zahrňuje vytvorenie okna hry, získanie mien hráčov z úvodného menu a
 * spustenie inštancie hry.
 * 
 * @author Daniel J.
 * 
 * @version 2.0
 */

public class Main {

    private Platno platno;

    /**
     * Vstupný bod pre spustenie aplikácie.
     * 
     * @param args Pole reťazcov obsahujúce argumenty príkazového riadka
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.spustiAplikaciu();
    }

    /**
     * Inicializuje potrebné objekty a spúšťa hru na základe užívateľských vstupov.
     */
    private void spustiAplikaciu() {
        // Inicializácia platna
        this.platno = Platno.dajPlatno();

        // Inicializácia StartMenu s použitím platna
        StartMenu startMenu = new StartMenu(this.platno);

        while (!startMenu.jeHraSpustena()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Získanie mien hráčov z StartMenu
        String menoHraca1 = startMenu.getMenoHraca1();
        String menoHraca2 = startMenu.getMenoHraca2();

        // Inicializácia hry s menami hráčov
        Hra hra = new Hra(menoHraca1, menoHraca2, startMenu);

        // Spustenie hry
        hra.spustiHru();
    }
}
