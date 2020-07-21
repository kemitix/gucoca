package net.kemitix.gucoca.email;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionStackTrace {

    String generate(Throwable t) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream sout = new PrintStream(out);
        t.printStackTrace(sout);
        return out.toString();
    }

}
