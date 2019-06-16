package sh.lrk.soundboard;


import java.io.File;

public class SoundboardSample {
    private File file;
    private String name;

    public SoundboardSample(File file, String name) {
        this.file = file;
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }
}
