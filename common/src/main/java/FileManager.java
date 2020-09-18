import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {
    //Имя директории, по которому нужно перейти в каталог выше
    protected static final String STEP_OUT = "..";
    //Размер буфера для считывания и записи байтов в файл
    protected static final int BUFFER_SIZE = 8;
    //Файл, с которым менеджер работает в данный момент
    protected File currentFile;
    protected FileInputStream is;

    protected String currentPath;
    protected List<File> files;

    public FileManager() {
        StringBuilder sb = new StringBuilder(new File("").getAbsolutePath());
        this.currentPath = sb.toString();
//        System.out.println("current path: " + currentPath);
        this.files = new ArrayList<>();
    }

    public int getBufferSize() { return BUFFER_SIZE; }

    public void setCurrentFile(String name) {
        currentFile = new File(currentPath + File.separator + name);
        try {
            is = new FileInputStream(currentFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void updateFiles() {
        File dir = new File(currentPath);
        files.clear();
        files.addAll(Arrays.asList(dir.listFiles()));
    }

    public List<FileMetadata> getFileList() {
        List<FileMetadata> fileList = new ArrayList<>();
        updateFiles();
        fileList.add(new FileMetadata("..",true,0));
        for (File file: files) {
            fileList.add(new FileMetadata(file.getName(),file.isDirectory(),file.length()));
        }
        return fileList;
    }

    public void changeCurrentDirectory(String directory) {
        if (directory.equals(STEP_OUT)) { currentPath = stepOutDirectory(); }
        else { currentPath = currentPath + File.separator + directory; }
//        System.out.println(currentPath);
    }

    protected String stepOutDirectory() {
        return new File(currentPath).getParent();
    }

    public int read(byte[] targetBuffer) {
        int readBytesCount = 0;
        try {
            readBytesCount = is.read(targetBuffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readBytesCount;
    }

    public void createFile(String name) {
        File file = new File(currentPath + File.separator + name);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String name, byte[] data, boolean append) {
        File file = new File(currentPath + File.separator + name);
        try(FileOutputStream os = new FileOutputStream(file, append)) {
            os.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
