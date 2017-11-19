package soep;

import java.io.File;
import java.io.IOException;

public class HarvestUtil {
    public static File createWorkingDir() throws IOException {
        String path = System.getProperty("user.home") + File.separator + "GitHub" + File.separator;
        // System.out.println("Path string: " + path);
        File dir = new File(path);
        if (dir.exists()) {
            System.out.println(dir + " already exists");
            // System.out.println("Canonical path: " + dir.getCanonicalPath());
            // System.out.println("dir toString(): " + dir.toString());
            return dir;
        } else if (dir.mkdirs()) {
            System.out.println(dir + " was created");
            return dir;
        } else {
            System.out.println(dir + " was not created");
            return null;
        }
    }

    // Does a repo already exist?
    public static boolean repoExists(String repoName) throws IOException {
        boolean status = false;
        String[] fileList = HarvestUtil.createWorkingDir().list();

        for(String str : fileList){
            if(repoName.equals(str)){
                System.out.println(repoName + " already exists!");
                status = true; //return Git reference to it!
            }
        }

        return status;
    }

    public static void main(String[] args) throws IOException {
        String repoName = "ElasticSearch";
        File testFile = HarvestUtil.createWorkingDir();
        System.out.println("File (canonical): " + testFile); // .getCanonicalFile()
        File localFileRepo = new File(testFile + File.separator + repoName + File.separator + "local");
        System.out.println("localFileRepo: " + localFileRepo);

        System.out.println("Project exists? " + HarvestUtil.repoExists("ElasticSearch"));
    }
}