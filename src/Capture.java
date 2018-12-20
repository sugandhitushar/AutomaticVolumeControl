import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.Scanner;

public class Capture implements Runnable {
    final int bufferSize = 16384;
    AudioInputStream audioInputStream;
    TargetDataLine line;
    Thread thread;
    float defaultLevel;

    public Capture(float defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public void start() {
        thread = new Thread(this);
        thread.setName("Capture");
        thread.start();
    }

    public void stop() {
        Audio.setMasterOutputVolume(defaultLevel);
        thread = null;
    }

    public float calculateRMSLevel(byte[] audioData) {
        long lSum = 0;
        for(int i = 0; i < audioData.length; i++) {
            lSum = lSum + audioData[i];
        }

        double dAvg = lSum / audioData.length;
        double sumMeanSquare = 0.0;

        for(int j = 0; j < audioData.length; j++) {
            sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);
        }

        double averageMeanSquare = sumMeanSquare / audioData.length;

        return (float) (Math.pow(averageMeanSquare, 0.5d) + 0.5) * 0.01F;
    } 

    public void run() {
        audioInputStream = null;

        // define the required attributes for our line,
        // and make sure a compatible line is supported.
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000, 16, 2, 4, 8000, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if(!AudioSystem.isLineSupported(info)) {
            stop();
            System.out.println("Line matching " + info + " not supported.");
            return;
        }

        // get and open the target data line for capture
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        } catch(Exception e) {
            stop();
            System.out.println("Unable to open the line: " + e);
            return;
        }

        // play back the captured audio data
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead;

        line.start();

        /*
        for(int i = 0; i < 3; i++) {
            Audio.setMasterOutputVolume(1.0F);
        }
        */

        float rmsLevel = 0;
        while(thread != null) {
            if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                break;
            }

            if((rmsLevel = calculateRMSLevel(data))  < defaultLevel) {
                rmsLevel = defaultLevel;
            }
            System.out.println("Volume level: " + rmsLevel*100);
            Audio.setMasterOutputVolume(rmsLevel);
        }

        // we reached the end of the stream. stop and close the line
        line.stop();
        line.close();
        line = null;
        stop();
    }
}