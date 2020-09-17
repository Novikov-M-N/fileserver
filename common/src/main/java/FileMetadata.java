import java.io.Serializable;

/**
 * Передаваемая по сети информация о файлах и функционал, касающийся её представления
 */
public class FileMetadata implements Serializable {
    private final String name;
    private final boolean isDirectory;
    private final long length;


    public FileMetadata(String name, boolean isDirectory, long length) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.length = length;
    }

    public String getName() { return name; }
    public boolean getIsDirectory() { return isDirectory; };
    public long getLength() { return length; }
    public String getUserFriendlyLength() {
        long tempLength = length;
        String[] units = new String[]{" Б"," кБ"," МБ"," ГБ"," ТБ"};
        int i = 0;
        while (tempLength > 1023 && i < 4) {
            tempLength = tempLength/1024;
            i++;
        }
        String result = tempLength + units[i];
        return result;
    }

}
