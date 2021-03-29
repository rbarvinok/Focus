package ua.focus.javaclass.servisClass;

import java.io.File;

public class DeleteAllFilesFolder {
    public static void deleteAllFilesFolder(String path) {
        for (File myFile : new File(path).listFiles())
            if (myFile.isFile()) myFile.delete();
    }


}
