package shift;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/*
Audio file links:
http://freemusicarchive.org/music/Poor_Alexei_1268/Music_For_Headphones/
*/
public class Audio implements LineListener
{
    private boolean mute = true;
    private Clip musicClip1, musicClip2;
    private AudioInputStream musicStream1, musicStream2;
    private int backgroundClipNumber = 1;
    private FloatControl musicVolume;
    private Clip currentClip;
    
    public Audio()
    {
        try{
            musicClip1 = AudioSystem.getClip();
            InputStream inClone1 = getClass().getResourceAsStream("/Music/Poor_Alexei_-_01_-_Aleppo.wav");
            InputStream in1 = new BufferedInputStream(inClone1);
            musicStream1 = AudioSystem.getAudioInputStream(in1);
            musicClip1.open(musicStream1);
            musicClip1.addLineListener(this);
            
            musicClip2 = AudioSystem.getClip();
            InputStream inClone2 = getClass().getResourceAsStream("/Music/Poor_Alexei_-_02_-_Interiors.wav");
            InputStream in2 = new BufferedInputStream(inClone2);
            musicStream2 = AudioSystem.getAudioInputStream(in2);
            musicClip2.open(musicStream2);
            
            currentClip = musicClip1;
            musicClip1.drain();
            //musicClip1.setMicrosecondPosition(musicClip1.getMicrosecondLength() - 30000000);
            musicVolume = (FloatControl)musicClip1.getControl(FloatControl.Type.MASTER_GAIN);
            if(!mute)
            {
                musicClip1.start();//starts the music. 
            }
        }catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    public float getVolume(){return musicVolume.getValue();}
    
    public void setVolume(float f)
    {
        musicVolume.setValue(f);
    }

    @Override
    public void update(LineEvent event) 
    {
        if(event.getType() == LineEvent.Type.STOP)
        {
            backgroundClipNumber++;
            if(backgroundClipNumber > 2)
            {
                backgroundClipNumber = 1;
            }
            float saveVolume;
            switch(backgroundClipNumber)
            {
                case 1:
                    musicClip1.drain();
                    //musicClip1.setMicrosecondPosition(musicClip1.getMicrosecondLength() - 30000000);
                    musicClip1.start();
                    musicClip1.addLineListener(this);
                    saveVolume = musicVolume.getValue();
                    musicVolume = (FloatControl)musicClip1.getControl(FloatControl.Type.MASTER_GAIN);
                    musicVolume.setValue(saveVolume);
                    currentClip = musicClip1;
                    break;
                case 2:
                    musicClip2.drain();
                    //musicClip2.setMicrosecondPosition(musicClip2.getMicrosecondLength() - 30000000);
                    musicClip2.start();
                    musicClip2.addLineListener(this);
                    saveVolume = musicVolume.getValue();
                    musicVolume = (FloatControl)musicClip2.getControl(FloatControl.Type.MASTER_GAIN);
                    musicVolume.setValue(saveVolume);
                    currentClip = musicClip2;
                    break;
            }
        }
    }
}