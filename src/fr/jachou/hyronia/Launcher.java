package fr.jachou.hyronia;

import fr.theshark34.openlauncherlib.launcher.GameTweak;
import fr.theshark34.openlauncherlib.launcher.GameType;
import club.minnced.discord.rpc.DiscordRichPresence;
import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import fr.theshark34.openlauncherlib.launcher.GameLauncher;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import java.io.IOException;
import com.azuriom.azauth.AuthenticationException;
import com.azuriom.azauth.AzAuthenticator;
import fr.theshark34.openlauncherlib.util.ErrorUtil;
import fr.theshark34.openlauncherlib.launcher.AuthInfos;
import java.io.File;
import fr.theshark34.openlauncherlib.launcher.GameFolder;
import fr.theshark34.openlauncherlib.launcher.GameInfos;
import fr.theshark34.openlauncherlib.launcher.GameVersion;

public class Launcher
{
    public static final GameVersion HL_VERSION;
    public static final GameInfos HL_INFOS;
    public static final GameFolder HL_FOLDER;
    public static final File HL_DIR;
    public static final File HL_CRASHES_DIR;
    private static Thread updateThread;
    private static AuthInfos authInfos;
    private static ErrorUtil errorUtil;

    public static void auth(final String username, final String password) throws AuthenticationException, IOException {
        final AzAuthenticator authenticator = new AzAuthenticator("https://hyronia.evohebergweb.eu");
        Launcher.authInfos = authenticator.authenticate(username, password, AuthInfos.class);
    }

    public static void update() throws Exception {
        final SUpdate su = new SUpdate("https://hyronia.evohebergweb.eu/S-updater/", Launcher.HL_DIR);
        (Launcher.updateThread = new Thread() {
            private int val;
            private int max;

            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    if (BarAPI.getNumberOfFileToDownload() == 0) {
                        LauncherFrame.getInstance().getLauncherPanel().setInfoText("V\u00e9rification des fichiers");
                    }
                    this.val = (int)(BarAPI.getNumberOfTotalDownloadedBytes() / 1000L);
                    this.max = (int)(BarAPI.getNumberOfTotalBytesToDownload() / 1000L);
                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(this.max);
                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(this.val);
                    LauncherFrame.getInstance().getLauncherPanel().setInfoText("T\u00e9l\u00e9chargement des fichiers " + BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " + Swinger.percentage(this.val, this.max) + "%");
                }
            }
        }).start();
        su.start();
        Launcher.updateThread.interrupt();
    }

    public static void update_natives() throws Exception {
        final SUpdate su = new SUpdate("http://martayan.alwaysdata.net/natives/", Launcher.HL_DIR);
        (Launcher.updateThread = new Thread() {
            private int val;
            private int max;

            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    if (BarAPI.getNumberOfFileToDownload() == 0) {
                        LauncherFrame.getInstance().getLauncherPanel().setInfoText("V\u00e9rification des natives");
                    }
                    this.val = (int)(BarAPI.getNumberOfTotalDownloadedBytes() / 1000L);
                    this.max = (int)(BarAPI.getNumberOfTotalBytesToDownload() / 1000L);
                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(this.max);
                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(this.val);
                    LauncherFrame.getInstance().getLauncherPanel().setInfoText("T\u00e9l\u00e9chargement des fichiers " + BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " + Swinger.percentage(this.val, this.max) + "%");
                }
            }
        }).start();
        su.start();
        Launcher.updateThread.interrupt();
    }

    public static void launch() throws IOException {
        final GameLauncher gameLauncher = new GameLauncher(Launcher.HL_INFOS, Launcher.HL_FOLDER, Launcher.authInfos);
        final Process p = gameLauncher.launch();
        try {
            Thread.sleep(5000L);
        }
        catch (InterruptedException ex) {}
        LauncherFrame.getInstance().setVisible(false);
        try {
            p.waitFor();
        }
        catch (InterruptedException ex2) {}
        System.exit(0);
    }

    public static void interruptThread() {
        Launcher.updateThread.interrupt();
    }

    public static ErrorUtil getErrorUtil() {
        return Launcher.errorUtil;
    }

    public static void discordRPC() {
        final DiscordRPC discord = DiscordRPC.INSTANCE;
        final String appID = "936024107802316831";
        final String SteamID = "";
        final DiscordEventHandlers handlers = new DiscordEventHandlers();
        discord.Discord_Initialize(appID, handlers, true, SteamID);
        final DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.largeImageKey = "hyronia_no_background";
        presence.largeImageText = "Hyronia";
        presence.details = "Lanceur Hyronia";
        presence.state = "Version : 1.17.1";
        discord.Discord_UpdatePresence(presence);
    }

    static {
        HL_VERSION = new GameVersion("1.16.5", GameType.V1_8_HIGHER);
        HL_INFOS = new GameInfos("hyronia", Launcher.HL_VERSION, false, new GameTweak[0]);
        HL_FOLDER = new GameFolder("assets", "libs", "natives", "minecraft.jar");
        HL_DIR = Launcher.HL_INFOS.getGameDir();
        HL_CRASHES_DIR = new File(Launcher.HL_DIR, "crashes");
        Launcher.errorUtil = new ErrorUtil(Launcher.HL_CRASHES_DIR);
    }
}