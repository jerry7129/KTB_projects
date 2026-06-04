public class TestRegex {
    public static void main(String[] args) {
        String extractedPath = "/public/temp/temp-20260605032113-e6cfb09c.png";
        String fileKey = extractedPath.replaceFirst("^/?public/", "");
        System.out.println("extractedPath: " + extractedPath);
        System.out.println("fileKey: " + fileKey);
    }
}
