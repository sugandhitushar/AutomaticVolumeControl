import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class AutomaticVolumeControl extends JPanel {
    static Capture capture;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Automatic Volume Control");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        stopButton.setEnabled(false);

        startButton.addActionListener(new ActionListener(){
        
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("System audio level: " + Audio.getMasterOutputVolume() * 100);
                capture = new Capture(Audio.getMasterOutputVolume());
                capture.start();
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                
            }
        });

        
        stopButton.addActionListener(new ActionListener(){
        
            @Override
            public void actionPerformed(ActionEvent e) {
                capture.stop();
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        frame.add(buttonPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,40);
        frame.setVisible(true);
    }
}