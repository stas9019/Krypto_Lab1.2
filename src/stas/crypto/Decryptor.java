package stas.crypto;

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
* Key is 9dc8c57d63d241b0
Decoded text: China Allegedly Arrested Hackers To Comply With The U.S. Government's Demands
*
* */


public class Decryptor {

    String fileName;

    String keyPart1;
    String keyPart2 = "63d241b0";

    byte[] cypher;

    Decryptor(String fileName) throws IOException
    {
        this.fileName = fileName;
    }


    private void readCypher() throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String line;

        List<Byte> cypherList = new ArrayList<>();

        line = reader.readLine();
        for(String block : line.split(" "))
        {
            cypherList.add((byte)Integer.parseInt(block, 2));
        }


        cypher = new byte[cypherList.size()];
        for(int i = 0; i < cypherList.size(); i++) {
            cypher[i] = cypherList.get(i).byteValue();
        }



    }


    private void brutforce() throws IOException
    {

        String key;

        for (long i = 0; i < Long.MAX_VALUE / 2; i++)
        {

            keyPart1 = Long.toHexString(i);

            while (keyPart1.length() < 8)
                keyPart1 = "0"+keyPart1;

            key = keyPart1 + keyPart2;

            if(tryKey(key))
                break;
        }


    }

    private boolean tryKey(String key)
    {
        StreamCipher decryptor= new RC4Engine();

        KeyParameter keyParam = new KeyParameter(key.getBytes(StandardCharsets.US_ASCII));

        decryptor.init(false, keyParam);


        byte[] result = new byte[cypher.length];

        decryptor.processBytes(cypher, 0, cypher.length, result, 0);

        for(Byte b : result)
        {
            if(!(b >=32 && b < 127) || ( b == 10))
                return false;

        }

        System.out.println("Key is " + key);
        System.out.println("Decoded text: "+ new String(result));

        return true;


    }

    public static void main(String[] args) {

        Decryptor decryptor = null;

        try {
            decryptor = new Decryptor(args[0]);
            decryptor.readCypher();
            decryptor.brutforce();
        }catch (IOException e) {
            e.printStackTrace();
        }





    }
}
