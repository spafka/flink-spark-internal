package test;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class generate {


    @Test
    public void randomFile() throws IOException {


        File file = new File("random.txt");

        if (file.exists()){
            file.delete();
        }



        BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(new FileOutputStream(file));

        for (int i=0;i<Integer.MAX_VALUE>>3;i++){
            bufferedOutputStream.write("deep dark\n ".getBytes());
//            bufferedOutputStream.flush();
        }
//        bufferedOutputStream.flush();
        bufferedOutputStream.close();

    }
}
