package fr.jachou.hyronia;

import com.azuriom.azauth.AuthenticationException;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;
import javafx.scene.layout.Background;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;



public class LauncherPanel extends JPanel implements SwingerEventListener {

    private Image background = Swinger.getResource("background.png");

    private JTextField usernameField = new JTextField();
    private JTextField passwordField = new JPasswordField();

    private STexturedButton playButton = new STexturedButton(Swinger.getResource("play.png"));
    private STexturedButton quitButton = new STexturedButton(Swinger.getResource("croix.png"));
    private STexturedButton hideButton = new STexturedButton(Swinger.getResource("fermer.png"));

    private SColoredBar progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
    private JLabel infoLabel = new JLabel("Cliquez sur Jouer !");

    public LauncherPanel() {
        this.setLayout(null);

        usernameField.setForeground(Color.WHITE);
        usernameField.setFont(usernameField.getFont().deriveFont(20F));
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setOpaque(false);
        usernameField.setBorder(null);
        usernameField.setBounds(71, 369, 390, 40);
        this.add(usernameField);

        passwordField.setForeground(Color.WHITE);
        passwordField.setFont(passwordField.getFont().deriveFont(20F));
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setOpaque(false);
        passwordField.setBorder(null);
        passwordField.setBounds(74, 495, 390, 40);
        this.add(passwordField);

        playButton.setBounds(163, 610, 262, 58);
        playButton.addEventListener(this);
        this.add(playButton);

        quitButton.setBounds(500, 5, 10, 10);
        quitButton.addEventListener(this);
        this.add(quitButton);

        hideButton.setBounds(420, 0, 79, 62);
        hideButton.addEventListener(this);
        this.add(hideButton);

        progressBar.setBounds(18, 350, 510, 20);
        this.add(progressBar);

        infoLabel.setBounds(170, 320, 400, 25);
        infoLabel.setFont(usernameField.getFont());
        infoLabel.setForeground(Color.WHITE);
        this.add(infoLabel);
    }

    @Override
    public void paintComponent (Graphics g) {
        super.paintComponent(g);

        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    @Override
    public void onEvent(SwingerEvent e) {
        if (e.getSource() == playButton) {
            setFieldsEnable(false);

            if (usernameField.getText().replaceAll(" ", "").length() == 0 || passwordField.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Erreur veuillez entrez un pseudo et un mot de passe valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                setFieldsEnable(true);
                return;
            }


            Thread t = new Thread() {
              public void run() {
                  try {
                      Launcher.auth(usernameField.getText(), passwordField.getText());
                  } catch (AuthenticationException | IOException e) {
                      JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, le mail ou le mot de passe n'est pas valide. " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                      setFieldsEnable(true);
                      return;
                  }

                  try {
                      Launcher.update();
                  } catch (Exception e) {
                      Launcher.interruptThread();
                      Launcher.getErrorUtil().catchError(e, "Impossible de mettre à jour Hyronia.");
                      setFieldsEnable(true);
                      return;
                  }

                  try {
                      Launcher.launch();
                  } catch (IOException e) {
                      Launcher.getErrorUtil().catchError(e, "Impossible de lancer Hyronia.");
                      setFieldsEnable(true);
                  }

                  System.out.println("Connexion réussi");
              }
            };
            t.start();
        } else if (e.getSource() == quitButton) {
            System.exit(0);
        } else if (e.getSource() == hideButton) {
            LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
        }
    }

    private void setFieldsEnable(boolean enable) {
        usernameField.setEnabled(enable);
        passwordField.setEnabled(enable);
        playButton.setEnabled(enable);
    }

    public SColoredBar getProgressBar() {
        return progressBar;
    }

    public void setInfoText(String text) {
        infoLabel.setText(text);
    }

}
