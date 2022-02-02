package fr.jachou.hyronia;

import java.io.IOException;
import com.azuriom.azauth.AuthenticationException;
import javax.swing.JOptionPane;
import fr.theshark34.swinger.event.SwingerEvent;
import java.awt.image.ImageObserver;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.LayoutManager;
import javax.swing.JPasswordField;
import fr.theshark34.swinger.Swinger;
import javax.swing.JLabel;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.textured.STexturedButton;
import javax.swing.JTextField;
import java.awt.Image;
import fr.theshark34.swinger.event.SwingerEventListener;
import javax.swing.JPanel;

public class LauncherPanel extends JPanel implements SwingerEventListener
{
    private Image background;
    private JTextField usernameField;
    private JTextField passwordField;
    private STexturedButton playButton;
    private STexturedButton quitButton;
    private STexturedButton hideButton;
    private SColoredBar progressBar;
    private JLabel infoLabel;

    public LauncherPanel() {
        this.background = Swinger.getResource("background.png");
        this.usernameField = new JTextField();
        this.passwordField = new JPasswordField();
        this.playButton = new STexturedButton(Swinger.getResource("play.png"));
        this.quitButton = new STexturedButton(Swinger.getResource("quit1.png"));
        this.hideButton = new STexturedButton(Swinger.getResource("quit.png"));
        this.progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
        this.infoLabel = new JLabel("Cliquez sur Jouer !");
        this.setLayout(null);
        this.usernameField.setForeground(Color.WHITE);
        this.usernameField.setFont(this.usernameField.getFont().deriveFont(20.0f));
        this.usernameField.setCaretColor(Color.WHITE);
        this.usernameField.setOpaque(false);
        this.usernameField.setBorder(null);
        this.usernameField.setBounds(80, 430, 390, 40);
        this.add(this.usernameField);
        this.passwordField.setForeground(Color.WHITE);
        this.passwordField.setFont(this.passwordField.getFont().deriveFont(20.0f));
        this.passwordField.setCaretColor(Color.WHITE);
        this.passwordField.setOpaque(false);
        this.passwordField.setBorder(null);
        this.passwordField.setBounds(80, 566, 390, 40);
        this.add(this.passwordField);
        this.playButton.setBounds(134, 640, 262, 58);
        this.playButton.addEventListener(this);
        this.add(this.playButton);
        this.quitButton.setBounds(470, 0, 79, 62);
        this.quitButton.addEventListener(this);
        this.add(this.quitButton);
        this.hideButton.setBounds(420, 0, 79, 62);
        this.hideButton.addEventListener(this);
        this.add(this.hideButton);
        this.progressBar.setBounds(18, 350, 510, 20);
        this.add(this.progressBar);
        this.infoLabel.setBounds(190, 320, 200, 25);
        this.infoLabel.setFont(this.usernameField.getFont());
        this.infoLabel.setForeground(Color.WHITE);
        this.add(this.infoLabel);
    }

    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.background, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    @Override
    public void onEvent(final SwingerEvent e) {
        if (e.getSource() == this.playButton) {
            this.setFieldsEnable(false);
            if (this.usernameField.getText().replaceAll(" ", "").length() == 0 || this.passwordField.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Erreur veuillez entrez un pseudo et un mot de passe valides.", "Erreur", 0);
                this.setFieldsEnable(true);
                return;
            }
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Launcher.auth(LauncherPanel.this.usernameField.getText(), LauncherPanel.this.passwordField.getText());
                    }
                    catch (AuthenticationException | IOException authenticationException) {
                        JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, le mail ou le mot de passe n'est pas valide. " + authenticationException.getMessage(), "Erreur", 0);
                        LauncherPanel.this.setFieldsEnable(true);
                        return;
                    }
                    try {
                        Launcher.update();
                    }
                    catch (Exception e) {
                        Launcher.interruptThread();
                        Launcher.getErrorUtil().catchError(e, "Impossible de mettre \u00e0 jour Hyronia.");
                        LauncherPanel.this.setFieldsEnable(true);
                        return;
                    }
                    try {
                        Launcher.launch();
                    }
                    catch (IOException e2) {
                        Launcher.getErrorUtil().catchError(e2, "Impossible de lancer Hyronia.");
                        LauncherPanel.this.setFieldsEnable(true);
                    }
                    System.out.println("Connexion r\u00e9ussi");
                }
            };
            t.start();
        }
        else if (e.getSource() == this.quitButton) {
            System.exit(0);
        }
        else if (e.getSource() == this.hideButton) {
            LauncherFrame.getInstance().setState(1);
        }
    }

    private void setFieldsEnable(final boolean enable) {
        this.usernameField.setEnabled(enable);
        this.passwordField.setEnabled(enable);
        this.playButton.setEnabled(enable);
    }

    public SColoredBar getProgressBar() {
        return this.progressBar;
    }

    public void setInfoText(final String text) {
        this.infoLabel.setText(text);
    }
}