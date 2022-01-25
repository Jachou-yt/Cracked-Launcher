package fr.jachou.hyronia;



import com.azuriom.azauth.AuthenticationException;
import com.azuriom.azauth.AzAuthenticator;
import fr.theshark34.openlauncherlib.launcher.*;
import fr.theshark34.openlauncherlib.util.ErrorUtil;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.Swinger;

import java.io.File;
import java.io.IOException;


public class Launcher {

    public static final GameVersion HL_VERSION = new GameVersion("1.17.1", GameType.V1_8_HIGHER);
    public static final GameInfos HL_INFOS = new GameInfos("hyronia", HL_VERSION, true, new GameTweak[] {GameTweak.OPTIFINE, GameTweak.SHADER});
    public static final File HL_DIR = HL_INFOS.getGameDir();
    public static final File HL_CRASHES_DIR = new File(HL_DIR, "crashes");

    private static Thread updateThread;
    private static AuthInfos authInfos;

    private static ErrorUtil errorUtil = new ErrorUtil(HL_CRASHES_DIR);

    public static void auth(String username, String password) throws AuthenticationException, IOException {
        AzAuthenticator authenticator = new AzAuthenticator("https://jachou.alwaysdata.net");
        authInfos = authenticator.authenticate(username, password, AuthInfos.class);
    }

    public static void update() throws Exception {
        SUpdate su = new SUpdate("http://martayan.alwaysdata.net/", HL_DIR);
        su.addApplication(new FileDeleter());
        updateThread = new Thread() {
            private int val;
            private int max;

            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    if (BarAPI.getNumberOfFileToDownload() == 0) {
                        LauncherFrame.getInstance().getLauncherPanel().setInfoText("Vérification des fichiers");
                    }

                    val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
                    max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);

                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(max);
                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(val);

                    LauncherFrame.getInstance().getLauncherPanel().setInfoText("Téléchargement des fichiers " +
                            BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " +
                            Swinger.percentage(val, max) + "%");
                }
            }
        };
        updateThread.start();
        su.start();
        updateThread.interrupt();
    }

    public static void launch() throws IOException {
        GameLauncher gameLauncher = new GameLauncher(HL_INFOS, GameFolder.BASIC, authInfos);
        Process p = gameLauncher.launch();
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
        }
        LauncherFrame.getInstance().setVisible(false);
        try {
            p.waitFor();
        } catch (InterruptedException e) {
        }
        System.exit(0);
    }

    public static void interruptThread() {
        updateThread.interrupt();
    }

    public static ErrorUtil getErrorUtil() {
        return errorUtil;
    }
}