/**
 * ClassList.java
 *
 */
package jde.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;


/**
 * A ClassPathEntry that represents the classlist file in JDK 9 and later
 *
 */
class ClassList extends ClassPathEntry {
    private final File classList;

    ClassList() {
        classList = new File(new File(System.getProperty("java.home"), "lib"),
                             "classlist");
    }

    @Override
    protected void load() throws IOException {
        if (classList.canRead()) {
            BufferedReader in = new BufferedReader(new FileReader(classList));
            String line;
            while ((line = in.readLine()) != null) {
                String className = line.replace("/", ".");
                addClass(className);
            }
        }
        setLoaded(true);
    }
}

