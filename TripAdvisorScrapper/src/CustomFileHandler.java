import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CustomFileHandler {
    
    private void createDirectory(String path){
        File directory = new File(path);
        if (! directory.exists()){
            directory.mkdir();
        }
    }

    public void createDirectories(String... paths){
        for (String path : paths){
            createDirectory(path);
        }
    }

    public void writeFile(String path,String content){
        File file = new File(path);
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFile(String path){
        File file = new File(path);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendFile(String path,String content){
        File file = new File(path);
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<String> getFilePaths(String path){
        String currentPath = System.getProperty("user.dir");

        File folder = new File(currentPath + "/" + path);
        File[] listOfFiles = folder.listFiles();
        List<String> filePaths = new java.util.ArrayList<String>();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                filePaths.add(listOfFiles[i].getAbsolutePath());
            }
        }
        return filePaths;
    }

    public List<String> readFileLines(String path){
        List<String> lines = new java.util.ArrayList<String>();
        try {
            java.util.Scanner s = new java.util.Scanner(new File(path));
            while (s.hasNextLine()) {
                lines.add(s.nextLine());
            }
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(lines.size());
        return lines;
    }
    
    public CustomFileHandler() {
    }

}
