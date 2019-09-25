package doa.tsv;
import org.junit.Test;

import java.util.List;

import tsvdao.TSVReaderWriter;

public class TSVReaderWriterTest {

    @Test
    public void CompareTest() {
        String[] header = new String[2];
        header[0] = "string,asdf";
        header[1] = "valume";
        TSVReaderWriter rw = new TSVReaderWriter(header);
        rw.writeHeader();
        rw.writeLine(header);

        List<String[]> lines = rw.readAll();
        assert(lines.size() == 1);
        String[] tokens = lines.get(0);
        assert(tokens.length == 2);
        assert(tokens[0].equals("string,asdf"));
        assert(tokens[1].equals("valume"));

        System.out.println("Done");
    }

}
