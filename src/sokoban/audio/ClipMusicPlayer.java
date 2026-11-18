package sokoban.audio;

import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ClipMusicPlayer implements MusicPlayer {

    private final Clip clip;

    private ClipMusicPlayer(Clip clip) {
        this.clip = clip;
    }

    public static MusicPlayer fromFile(Path file) {
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(file.toFile())) {
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            return new ClipMusicPlayer(clip);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | IllegalArgumentException e) {
            return new SilentMusicPlayer();
        }
    }

    @Override
    public void play() {
        if (!clip.isRunning()) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    @Override
    public void stop() {
        clip.stop();
    }
}
