
public class App {
    public static void main(String[] args) throws Exception {
        CustomFileHandler fh = new CustomFileHandler();
        fh.createDirectories("./TripAdvisorData");
        new TripAdvisor("São Paulo");
    }
}






