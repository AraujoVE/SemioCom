
public class App {
    public static void main(String[] args) throws Exception {
        CustomFileHandler fh = new CustomFileHandler();
        fh.createDirectories("/home/vinicius/gits/SemioCom/TripAdvisorScrapper/TripAdvisorData");
        new TripAdvisor("São Paulo",Integer.parseInt(args[0]),Integer.parseInt(args[1]));
    }
}






