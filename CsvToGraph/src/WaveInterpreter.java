import java.io.*;
import java.lang.String;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.Math;
import javax.sound.midi.*;

public class WaveInterpreter {

    public static void main(String[] args) throws Exception{

        // Read the data from the file and finds the points where
        // someone "played" and then timestamps each point.

        String file = "../datagetter/test10.csv";
        File newFile = new File(file);
        int resolution = 12;
        double percentDeviation = 0.2;
        Scanner fileScanner = new Scanner(newFile);
        int tempo = Integer.parseInt(fileScanner.nextLine());
        int baseA = Integer.parseInt(fileScanner.nextLine());
        int toAdd;
        for (int i = 1; i < 12; i++) {
            toAdd = Integer.parseInt(fileScanner.next());
            System.out.println(toAdd);
            if (Math.abs((baseA / i) - toAdd)>= ((baseA / i) * 0.1)){
                i -= 1;
                continue;
            }
            baseA += toAdd;
        }
        baseA /= 12;
        System.out.println(tempo);
        int index = 0;
        int lastBig = 0;
        List<int[]> soundPoints = new ArrayList<>();
        while (fileScanner.hasNextLine()) {
            try {
                index += 1;
                String magString = fileScanner.nextLine();
                if (magString.equals("")){
                    continue;
                }
                System.out.print(index + " ");
                int magnitude = Integer.parseInt(magString);
                if (Math.abs(magnitude - baseA) >= baseA * percentDeviation && magnitude > baseA && Math.abs(lastBig - index) > 3) {
                    lastBig = index;
                    soundPoints.add(new int[]{magnitude, index});
                }
            } catch (Exception e) {};
        }
        int subtractor = (soundPoints.get(0))[1];
        for (int i = 0; i < soundPoints.size(); i++){
            (soundPoints.get(i))[1] -= subtractor;
            System.out.println("[" + (soundPoints.get(i))[0] + ", " + (soundPoints.get(i))[1] + "]");
        }
        System.out.println(subtractor);
        System.out.println(soundPoints.size());
        System.out.println("Tempo: " + tempo + " BPM");

        // Takes the timestamped beats produced in the last section
        // and puts them into a new MIDI file to be read by Musescore.
        //
        // http://www.automatic-pilot.com/midifile.html

        Sequence s = new Sequence(javax.sound.midi.Sequence.PPQ,resolution);


        Track t = s.createTrack();


        byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
        SysexMessage sm = new SysexMessage();
        sm.setMessage(b, 6);
        MidiEvent me = new MidiEvent(sm,(long)0);
        t.add(me);


        tempo -= (tempo / 60.0);
        int mpq = (int) (15000000000.0 / tempo);
        MetaMessage mt = new MetaMessage();
        byte[] bt = ByteBuffer.allocate(4).putInt(mpq).array();

        //byte[] bt = {(byte) (mpq >> 24 & 0xff), (byte) (mpq >> 16 & 0xff), (byte) (mpq >> 8 & 0xff), (byte) (mpq & 0xff)};
        mt.setMessage(0x51, bt, 4);
        me = new MidiEvent(mt, (long) 0);
        t.add(me);

        mt = new MetaMessage();
        String TrackName = new String("Sick Drum Beat Dude");
        mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
        me = new MidiEvent(mt,(long)0);
        t.add(me);

        ShortMessage mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7D,0x00);
        me = new MidiEvent(mm,(long)0);
        t.add(me);

        mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7F,0x00);
        me = new MidiEvent(mm,(long)0);
        t.add(me);

        int endingIndex = (soundPoints.size() - 1);

        for(int j = 0; j < endingIndex; j++) {
            //Note on
            mm = new ShortMessage();
            mm.setMessage(0xC0, 0x7F, 0x00);
            me = new MidiEvent(mm, (long) round((soundPoints.get(j))[1]));
            t.add(me);
            // Note off
            mm = new ShortMessage();
            mm.setMessage(0x90, 0x3C, 0x60);
            me = new MidiEvent(mm, (long) round((soundPoints.get(j))[1] + 12));
            t.add(me);

        }

        // For ending the track
        mm = new ShortMessage();
        mm.setMessage(0x80,0x3C,0x40);
        me = new MidiEvent(mm,(long) round((soundPoints.get(endingIndex))[1] + 13));
        t.add(me);

        mt = new MetaMessage();
        byte[] bet = {}; // empty array
        mt.setMessage(0x2F,bet,0);
        me = new MidiEvent(mt, (long)140);
        t.add(me);

        File f = new File("../datagetter/NewMIDIFile.mid");
        MidiSystem.write(s,1,f);

    }

    public static int round(int i) {
        return (int) (Math.round(i/24.0) * 3 - 3);
    }

}