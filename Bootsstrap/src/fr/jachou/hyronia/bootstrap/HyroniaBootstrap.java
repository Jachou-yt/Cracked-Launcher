package fr.jachou.hyronia.bootstrap;

import fr.theshark34.openlauncherlib.bootstrap.Bootstrap;
import fr.theshark34.openlauncherlib.bootstrap.LauncherClasspath;
import fr.theshark34.openlauncherlib.bootstrap.LauncherInfos;
import fr.theshark34.openlauncherlib.util.ErrorUtil;
import fr.theshark34.openlauncherlib.util.GameDir;
import fr.theshark34.openlauncherlib.util.SplashScreen;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;

import java.io.File;
import java.io.IOException;

public class HyroniaBootstrap {

    private static SplashScreen splash;
    private static SColoredBar bar;
    private static Thread barThread;

    private static final LauncherInfos HL_B_INFOS = new LauncherInfos("hyronia", "fr.jachou.hyronia.LauncherFrame");
    private static final File HL_B_DIR = GameDir.createGameDir("hyronia");
    private static final LauncherClasspath Hl_B_P = new LauncherClasspath(new File(HL_B_DIR, "Launcher/launcher.jar"), new File(HL_B_DIR, "Launcher/Libs/"));

    private static ErrorUtil errorUtil = new ErrorUtil(new File(HL_B_DIR, "Launcher/crashes/"));

    public static void main(String[] args) {
        Swinger.setResourcePath("/fr/jachou/hyronia/bootstrap/ressources");
        displaySplash();
        try {
            doUpdate();
        } catch (Exception e) {
            errorUtil.catchError(e, "Impossible de mettre à jour le launcher !");
            barThread.interrupt();
        }
        try {
            launchLauncher();
        } catch (IOException e) {
            errorUtil.catchError(e, "Impossible de lancer le launcher !");
        }
    }

    private static void displaySplash() {
        splash = new SplashScreen("Hyronia V1.0" , Swinger.getResource("splash.png"));
        splash.setBackground(Swinger.TRANSPARENT);
        splash.getContentPane().setBackground(Swinger.TRANSPARENT);
        splash.setLayout(null);
        splash.setVisible(true);

        bar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
        bar.setBounds(18, 350, 50, 20);
        bar.setVisible(true);
    }

    private static void doUpdate() throws Exception {
        SUpdate su = new SUpdate("http://martayan.alwaysdata.net/", new File(HL_B_DIR, "Launcher"));

        barThread = new Thread() {
          @Override
          public void run() {
              while (!this.isInterrupted()) {
                  bar.setValue((int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000));
                  bar.setMaximum((int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000));
              }
          }
        };
        barThread.start();

        su.start();
        barThread.interrupt();
    }

    private static void launchLauncher() throws IOException {
        Bootstrap bootstrap = new Bootstrap(Hl_B_P, HL_B_INFOS);
        Process p = bootstrap.launch();
        splash.setVisible(false);
        try {
            p.waitFor();
        } catch (InterruptedException e) {
        }
        System.exit(0);
    }

}
