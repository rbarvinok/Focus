package ua.focus.javaclass.servisClass;

import java.io.File;

import static ua.focus.controller.Controller.executeError;

public class DeleteAllFilesFolder {
    public static void deleteAllFilesFolder(String path) {
        File[] listFiles = new File(path).listFiles();
        if (listFiles != null && executeError != true) {
            for (File myFile : listFiles)
                if (myFile.isFile()) myFile.delete();
        }
    }
}
