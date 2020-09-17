import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {
    protected String currentPath;
    protected List<File> files;

    public FileManager() {
        StringBuilder sb = new StringBuilder(new File("").getAbsolutePath());
        this.currentPath = sb.toString();
//        System.out.println("current path: " + currentPath);
        this.files = new ArrayList<>();
    }

    protected void updateFiles() {
        File dir = new File(currentPath);
        files.clear();
        files.addAll(Arrays.asList(dir.listFiles()));
    }

    public List<FileMetadata> getFileList() {
        List<FileMetadata> fileList = new ArrayList<>();
        updateFiles();
        for (File file: files) {
            fileList.add(new FileMetadata(file.getName(),file.isDirectory(),file.length()));
        }
        return fileList;
    }

    public void stepInto(String directory) {
        currentPath = currentPath + File.separator + directory;
        System.out.println(currentPath);
    }
}
