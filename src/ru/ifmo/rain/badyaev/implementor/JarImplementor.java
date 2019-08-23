package ru.ifmo.rain.badyaev.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Produces jar implementation of classes and interfaces
 * <br>
 * Implements {@link JarImpler}
 */
public class JarImplementor extends Implementor implements JarImpler {

    /**
     * Produces <code>.jar</code> file implementing class or interface specified by provided <code>token</code>.
     * <p>
     * Generated class classes name should be same as classes name of the type token with <code>Impl</code> suffix
     * added.
     *
     * @param token type token to create implementation for.
     * @param jarFile target <code>.jar</code> file.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        try {
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

            implement(token, tempDir);
            compile(getFilePath(token, tempDir, JAVA_EXTENSION));
            makeJar(getFilePath(token, Paths.get("."), CLASS_EXTENSION), tempDir, jarFile);
        } catch (IOException e) {
            throw new ImplerException("Error while implementing jar " + e.getMessage());
        }
    }

    /**
     * Compiles class
     *
     * @param classPath path to class
     * @throws ImplerException if fail
     * @see ImplerException
     */
    private void compile(Path classPath) throws ImplerException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler == null) {
            throw new ImplerException("Java compiler was not found");
        }

        int returnCode = javaCompiler.run(null, null, null, classPath.toString());
        if (returnCode != 0) {
            throw new ImplerException("Error with code: " + returnCode + " during compilation");
        }
    }

    /**
     * Makes jar
     *
     * @param className class name
     * @param root      path root
     * @param jarFile   jar path
     * @throws ImplerException if error occurred while creating jar
     */
    private void makeJar(Path className, Path root, Path jarFile) throws ImplerException {
        className = className.normalize();
        Path classFile = root.resolve(className);

        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jarFile))) {
            out.putNextEntry(new ZipEntry(className.toString()));
            Files.copy(classFile, out);
            out.closeEntry();
        } catch (IOException e) {
            throw new ImplerException("Error while creating jar file: " + e.getMessage());
        }
    }

    /**
     * Entry point of the program. Command line arguments are processed here
     * <p>
     * Usage:
     * <ul>
     * <li>{@code java -jar JarImplementor.jar -jar class-to-implement path-to-jar}</li>
     * <li>{@code java -jar JarImplementor.jar class-to-implement path-to-class}</li>
     * </ul>
     *
     * @param args command line arguments.
     * @see Implementor
     */
    public static void main(String[] args) {
        if (args.length < 2 || args.length > 3 || args.length == 3 && !args[0].equals("-jar")) {
            System.err.println("Usage: java Implementor <class> <path> | -jar <class> <path>");
            return;
        }
        JarImplementor implementor = new JarImplementor();

        try {
            if (args.length == 2) {
                implementor.implement(Class.forName(args[0]), Paths.get(args[1]));
            } else {
                implementor.implementJar(Class.forName(args[1]), Paths.get(args[2]));
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Incorrect class " + e.getMessage());
        } catch (InvalidPathException e) {
            System.err.println("Incorrect path" + e.getMessage());
        } catch (ImplerException e) {
            System.err.println("Error while implementing class " + e.getMessage());
        }
    }
}
