import java.io.File;

/**
 * Расширение класса файлового менеджера для работы на сервере - ограничение доступа
 * корневой папкой пользователя
 */
public class ServerFileManager extends FileManager{
    private final String rootPath;

    public ServerFileManager(String login) {
        super();
        StringBuilder sb = new StringBuilder(this.currentPath)
                .append(File.separator)
                .append("storage")
                .append(File.separator)
                .append(login);
        this.rootPath = sb.toString();
        this.currentPath = rootPath;
//        System.out.println("root path: " + rootPath);
        new File(rootPath).mkdirs();
    }
}
