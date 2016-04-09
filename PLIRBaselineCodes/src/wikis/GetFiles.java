/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikis;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class GetFiles {

    private List<String> setOfFiles;

    public GetFiles() {
        setOfFiles = new ArrayList<String>();
    }

    public void getCorpusFiles(String parentDir) {
        FilenameFilter filter1 = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return !name.startsWith(".");
            }
        };

        File dir = new File(parentDir);
        if (dir.isDirectory()) {
            File[] dirs = dir.listFiles(filter1);
            for (int i = 0; i < dirs.length; i++) {
                if (dirs[i].isFile()) {
                    // if (dir.getAbsolutePath().endsWith("pdf")) {

                    setOfFiles.add(dirs[i].getAbsolutePath());

                    //   }
                } else if (dirs[i].isDirectory()) {
                    getCorpusFiles(dirs[i].toString());
                }
            }
        } else if(dir.isFile()){
            setOfFiles.add(dir.getAbsolutePath());
            //System.out.println("Please provide path to a directory that contians files for indexing not a file name, nothing to index!");
            //System.err.println("here: " + dir.getAbsolutePath());
        }

    }

    public List<String> getFiles() {
        return setOfFiles;
    }

}
