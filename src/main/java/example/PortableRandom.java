package example;

import java.io.*;
import java.util.ArrayList;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * For testing purposes, this random number generator reads a predetermined set of random numbers
 * (0-1) and then serves out the same doubles, or converts into Gaussian shape. If the numbers
 * get exhausted, the generator just cycles back through.
 */
public class PortableRandom extends java.util.Random {

    public double[] uniform;
    private int index = 0;
    private static NormalDistribution normal = new NormalDistribution();

    public PortableRandom(File sample) throws FileNotFoundException, IOException {
        this(sample, false);
    }

    public PortableRandom(File sample, boolean byteArray) throws FileNotFoundException, IOException {
        ArrayList<Double> incoming = new ArrayList<Double>();
        if (byteArray) {
            DataInputStream reader = new DataInputStream(new FileInputStream(sample));
            while (reader.available()>=4) {
                incoming.add(reader.readDouble());
            }
            reader.close();
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(sample));
            while (reader.ready()) {
                String line = reader.readLine();
                incoming.add(Double.parseDouble(line));
            }
            System.out.println("incoming data read with " + incoming.size() + "values.");
            reader.close();
        }
        //this.uniform = new double[incoming.size()];
        //System.out.println("Confirming uniform " + (this.uniform == null ? "is" : "isn't") + " null.");
        //for (int i=0; i<uniform.length; i++) uniform[i] = incoming.get(i);
        uniform = incoming.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private double doubleFromLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return Double.parseDouble(line);
    }

    private double doubleFromPackedBytes(DataInputStream stream) throws IOException {
        return stream.readDouble();
    }

    @Override
    public double nextDouble() {
        if (uniform.length==0) return super.nextDouble();
        double retVal = uniform[index++];
        if (index >= uniform.length) index = 0; // reset to beginning
        return retVal;
    }

    @Override
    public double nextGaussian() {
        return normal.inverseCumulativeProbability(nextDouble());
    }

    public void dumpData(String filename) throws IOException {
        File file = new File(filename);
        file.createNewFile();
        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        for (double d : uniform) {
            out.writeDouble(d);
        }
        out.close();
    }
}
