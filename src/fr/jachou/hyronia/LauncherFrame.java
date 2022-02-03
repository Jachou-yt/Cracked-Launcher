package fr.jachou.hyronia;

import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.util.WindowMover;

import javax.swing.*;

public class LauncherFrame extends JFrame {

    private static LauncherFrame instance;
    private LauncherPanel launcherPanel;

    public LauncherFrame() {
        this.setTitle("Hyronia Launcher | V1.0");
        this.setSize(1080, 720);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.setIconImage(Swinger.getResource("icon.png"));
        this.setContentPane(launcherPanel = new LauncherPanel());

        WindowMover mover = new WindowMover(this);
        this.addMouseListener(mover);
        this.addMouseMotionListener(mover);

        this.setVisible(true);

    }

    public static void main(String[] args) {
        Swinger.setSystemLookNFeel();
        Swinger.setResourcePath("/fr/jachou/hyronia/ressources/");
        Launcher.HL_CRASHES_DIR.mkdirs();

        instance = new LauncherFrame();
    }

    public static LauncherFrame getInstance() {
        return instance;
    }

    public LauncherPanel getLauncherPanel() {
        return this.launcherPanel;
    }
}
