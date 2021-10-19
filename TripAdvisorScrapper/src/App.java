import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        CustomFileHandler fh = new CustomFileHandler();
        List<String> params = fh.readFileLines("./dataPath.txt");
        fh.createDirectories("./TripAdvisorData");
        new TripAdvisor(params.get(0).trim(),Integer.parseInt(params.get(1).trim()),Integer.parseInt(params.get(2).trim()));
    }
}






