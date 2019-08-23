package ru.ifmo.rain.badyaev.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Produces implementation of classes and interfaces
 * <br>
 * Implements {@link Impler}
 */
public class Implementor implements Impler {

    /**
     * System dependent new line symbol for writer
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * End of line
     */
    private static final String END_LINE = ";" + NEW_LINE;

    /**
     * Space symbol
     */
    private static final String SPACE = " ";

    /**
     * Comma symbol
     */
    private static final String COMMA = ",";

    /**
     * Decimeter for enumeration
     */
    private static final String DELIMITER = COMMA + SPACE;


    /**
     * Tab symbol
     */
    private static final String TAB = "    ";

    /**
     * Left curve bracket (opening)
     */
    private static final String LEFT_CURVE_BRACKET = "{";

    /**
     * Right curve bracket (closing)
     */
    private static final String RIGHT_CURVE_BRACKET = "}";

    /**
     * Opens new scope
     */
    private static final String OPEN_BLOCK = LEFT_CURVE_BRACKET + NEW_LINE;

    /**
     * Closes scope
     */
    private static final String CLOSE_BLOCK = RIGHT_CURVE_BRACKET + NEW_LINE;

    /**
     * Left bracket (opening)
     */
    private static final String LEFT_BRACKET = "(";

    /**
     * Right bracket (closing)
     */
    private static final String RIGHT_BRACKET = ")";

    /**
     * String with no info (for methods which should return nothing in some cases)
     */
    private static final String EMPTY_INFO = "";

    /**
     * Single indent
     */
    private static final String INDENT = TAB;

    /**
     * Double indent
     */
    private static final String DOUBLE_INDENT = TAB + TAB;

    /**
     * Open scope and make indent
     */
    private static final String OPEN_BODY = SPACE + OPEN_BLOCK + DOUBLE_INDENT;

    /**
     * Close scope and print new line
     */
    private static final String CLOSE_BODY = END_LINE + INDENT + CLOSE_BLOCK + NEW_LINE;

    /**
     * Suffix for file
     */
    private static final String FILE_SUFFIX = "Impl";

    /**
     * Java file extension
     */
    static final String JAVA_EXTENSION = ".java";

    /**
     * Class file extension
     */
    static final String CLASS_EXTENSION = ".class";

    /**
     * Appends bracket around value
     *
     * @param sb    StringBuilder buffer
     * @param value String to surround with brackets
     * @return {@link java.lang.StringBuilder} - from arguments
     */
    private StringBuilder appendWithBrackets(StringBuilder sb, String value) {
        return sb.append(LEFT_BRACKET)
                .append(value)
                .append(RIGHT_BRACKET);
    }

    /**
     * Returns Implementation Class Name
     *
     * @param token type token
     * @return {@link String} - new class name
     */
    private String getClassName(Class<?> token) {
        return token.getSimpleName() + FILE_SUFFIX;
    }

    /**
     * Returns full path where implementation class of <code>classDefinition</code> with extension <code>extension</code> should be generated
     *
     * @param token     class type
     * @param root      root path
     * @param extension file extension
     * @return {@link Path} - file path
     * @throws IOException if error while creating directories
     */
    Path getFilePath(Class<?> token, Path root, final String extension) throws IOException {
        if (token.getPackage() != null) {
            root = root.resolve(token.getPackage().getName().replace('.', File.separatorChar) + File.separatorChar);
        }
        Files.createDirectories(root);

        return root.resolve(getClassName(token) + extension);
    }

    /**
     * Returns full path where implementation class of <code>classDefinition</code> with extension <code>extension</code> should be generated
     *
     * @param modifier     modifier of method
     * @param badModifiers modifiers which must be removed
     * @return {@link String} - modifiers string representation
     */
    private String getModifierName(int modifier, int... badModifiers) {
        for (int badModifier : badModifiers) {
            modifier &= ~badModifier;
        }

        String modifierValue = Modifier.toString(modifier);
        if (modifierValue.length() > 0) {
            return modifierValue + SPACE;
        }

        return EMPTY_INFO;
    }

    /**
     * Produces code implementing class or interface specified by provided <code>token</code>.
     * <p>
     * Generated class classes name should be same as classes name of the type token with <code>Impl</code> suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * <code>root</code> directory and have correct file name. For example, the implementation of the
     * interface {@link java.util.List} should go to <code>$root/java/util/ListImpl.java</code>
     *
     * @param token type token to create implementation for.
     * @param root  root directory.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be
     *                                                                 generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (token == null || root == null) {
            throw new ImplerException("Invalid arguments: null found");
        }

        if (token.isPrimitive() || token == Enum.class || token.isArray() || Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Invalid target to implement");
        }

        try {
            root = getFilePath(token, root, JAVA_EXTENSION);
        } catch (IOException e) {
            throw new ImplerException("Error while creating directories for implementation file: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(root.toString()), StandardCharsets.UTF_8))) {
            createImplementation(token, writer);

        } catch (UnsupportedEncodingException e) {
            throw new ImplerException("Unsupported encoding utf-8");
        } catch (FileNotFoundException e) {
            throw new ImplerException("Can't create or open file: " + root.toString() + ", " + e.getMessage());
        } catch (SecurityException e) {
            throw new ImplerException("Don't have access to write to file: " + root.toString() + ", " + e.getMessage());
        } catch (IOException e) {
            throw new ImplerException("Error while writing to: " + root.toString() + ", " + e.getMessage());
        }
    }

    /**
     * Generates full implementation of class or interface in file with suffix <code>Impl.java</code>
     * <br>
     * Prints the following: <br>
     * <ul>
     * <li>package</li>
     * <li>class declaration</li>
     * <li>constructors</li>
     * <li>methods</li>
     * </ul>
     *
     * @param token  type token
     * @param writer writer to print result
     * @throws ImplerException if error occurred during implementation
     * @throws IOException     if error occurred during implementation
     */
    private void createImplementation(Class<?> token, BufferedWriter writer) throws IOException, ImplerException {
        writePackage(token, writer);
        writeDeclaration(token, writer);
        writeConstructors(token, writer);
        writeMethods(token, writer);
    }

    /**
     * Prints package of class using
     *
     * @param token  type token
     * @param writer writer to print result
     * @throws IOException if error with IO
     */
    private void writePackage(Class<?> token, BufferedWriter writer) throws IOException {
        writer.write("package " + token.getPackageName() + END_LINE + NEW_LINE);
    }

    /**
     * Prints class declaration
     *
     * @param token  type token
     * @param writer writer to print result
     * @throws IOException if error with IO
     */
    private void writeDeclaration(Class<?> token, BufferedWriter writer) throws IOException {
        String string = getModifierName(token.getModifiers(), Modifier.ABSTRACT, Modifier.INTERFACE)
                + "class"
                + SPACE
                + getClassName(token)
                + SPACE
                + (token.isInterface() ? "implements" : "extends")
                + SPACE
                + token.getSimpleName()
                + SPACE
                + OPEN_BLOCK
                + NEW_LINE;

        writer.write(string);
    }

    /**
     * Prints class constructors
     *
     * @param token  type token
     * @param writer writer to print result
     * @throws IOException     if error with IO
     * @throws ImplerException if error during implementation
     */
    private void writeConstructors(Class<?> token, BufferedWriter writer) throws IOException, ImplerException {
        Constructor<?>[] constructors = token.getDeclaredConstructors();
        StringBuilder sb = new StringBuilder();

        boolean hasGoodConstructor = false;
        for (Constructor constructor : constructors) {
            if (!Modifier.isPrivate(constructor.getModifiers())) {
                sb.append(getExecutableModifiers(constructor))
                        .append(getClassName(token));

                addParametersAndExceptions(sb, constructor)
                        .append(OPEN_BODY)
                        .append("super");

                appendWithBrackets(sb, getParameters(constructor, false))
                        .append(CLOSE_BODY);

                hasGoodConstructor = true;
            }
        }

        if (!hasGoodConstructor && !token.isInterface()) {
            throw new ImplerException("Target contains only private constructors");
        }

        writer.write(sb.toString());
    }

    /**
     * Converts method to {@link ComparableMethod} and removes not abstract methods
     *
     * @param methods   array of class methods to convert
     * @param collector collector to collect comparable methods in collection
     * @param <T>       collection for methods
     * @return collection of comparable methods
     */
    private <T> T methodsToComparableMethods(Method[] methods, Collector<ComparableMethod, ?, T> collector) {
        return Arrays.stream(methods)
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .map(ComparableMethod::new)
                .collect(collector);
    }

    /**
     * Prints class methods
     *
     * @param token  type token
     * @param writer writer to print result
     * @throws IOException if error with IO
     */
    private void writeMethods(Class<?> token, BufferedWriter writer) throws IOException {
        Set<ComparableMethod> set = methodsToComparableMethods(token.getMethods(), Collectors.toCollection(HashSet::new));

        Class<?> superToken = token;
        while (superToken != null) {
            set.addAll(methodsToComparableMethods(token.getDeclaredMethods(), Collectors.toList()));
            superToken = superToken.getSuperclass();
        }

        for (ComparableMethod method : set) {
            writer.write(getMethod(method.getRealMethod()));
        }

        writer.write(CLOSE_BLOCK);
    }

    /**
     * Returns method or constructor modifiers
     *
     * @param executable method or constructor
     * @return {@link String}
     */
    private String getExecutableModifiers(Executable executable) {
        return INDENT + getModifierName(executable.getModifiers(), Modifier.ABSTRACT, Modifier.TRANSIENT, Modifier.NATIVE);
    }

    /**
     * Adds parameters and exceptions for methods and constructors
     *
     * @param sb         StringBuilder buffer
     * @param executable method or constructor
     * @return {@link StringBuilder} from arguments
     */
    private StringBuilder addParametersAndExceptions(StringBuilder sb, Executable executable) {
        appendWithBrackets(sb, getParameters(executable, true));

        Class<?>[] exceptions = executable.getExceptionTypes();
        if (exceptions.length > 0) {
            sb.append(SPACE)
                    .append("throws")
                    .append(SPACE);

            int exceptionsNumber = exceptions.length;
            for (int i = 0; i < exceptionsNumber; i++) {
                if (i > 0) {
                    sb.append(DELIMITER);
                }
                sb.append(exceptions[i].getCanonicalName());
            }
        }

        return sb;
    }

    /**
     * Returns method or constructor parameters
     *
     * @param executable method or constructor
     * @param withTypes  writer to print result
     * @return {@link String} parameters
     */
    private String getParameters(Executable executable, boolean withTypes) {
        Parameter[] parameters = executable.getParameters();

        if (parameters.length > 0) {
            StringBuilder sb = new StringBuilder();

            int paramsLength = parameters.length;
            for (int i = 0; i < paramsLength; i++) {
                if (i > 0) {
                    sb.append(DELIMITER);
                }

                if (withTypes) {
                    sb.append(parameters[i].getType().getCanonicalName()).append(SPACE);
                }
                sb.append(parameters[i].getName());
            }

            return sb.toString();
        }

        return EMPTY_INFO;
    }

    /**
     * Returns method representation as string
     *
     * @param method method
     * @return {@link String} - string representation
     */
    private String getMethod(Method method) {
        StringBuilder sb = new StringBuilder();
        for (Annotation annotation : method.getAnnotations()) {
            sb.append(annotation).append(NEW_LINE);
        }

        sb.append(getExecutableModifiers(method))
                .append(method.getReturnType().getCanonicalName())
                .append(SPACE)
                .append(method.getName());

        addParametersAndExceptions(sb, method)
                .append(OPEN_BODY)
                .append("return");

        if (!method.getReturnType().equals(void.class)) {
            sb.append(SPACE).append(getDefaultValue(method.getReturnType()));
        }

        sb.append(CLOSE_BODY);

        return sb.toString();
    }

    /**
     * Number default value
     */
    private static final String RETURN_PRIMITIVE_DEFAULT_VALUE = "0";

    /**
     * Boolean default value
     */
    private static final String RETURN_BOOLEAN_DEFAULT_VALUE = "false";

    /**
     * Void default value
     */
    private static final String RETURN_VOID_DEFAULT_VALUE = EMPTY_INFO;

    /**
     * Object default value
     */
    private static final String RETURN_OBJECT_DEFAULT_VALUE = "null";

    /**
     * Returns class default value represented as string
     *
     * @param token class type
     * @return {@link String} default value
     */
    private String getDefaultValue(Class<?> token) {
        if (token.equals(void.class)) {
            return RETURN_VOID_DEFAULT_VALUE;
        } else if (token.equals(boolean.class)) {
            return RETURN_BOOLEAN_DEFAULT_VALUE;
        } else if (token.isPrimitive()) {
            return RETURN_PRIMITIVE_DEFAULT_VALUE;
        }

        return RETURN_OBJECT_DEFAULT_VALUE;
    }

    /**
     * Helper class which provides true comparison used to store methods in {@link java.util.Set}
     */
    private class ComparableMethod {

        /**
         * Method which is wrapped
         */
        private Method method;

        /**
         * Method string view
         */
        private String stringValue;

        /**
         * Constructor from method
         *
         * @param method method to wrap
         */
        ComparableMethod(Method method) {
            this.method = method;
            this.stringValue = getMethod(method);
        }

        /**
         * Returns source method
         *
         * @return {@link Method} - source method
         */
        Method getRealMethod() {
            return method;
        }

        /**
         * Returns method hash
         *
         * @return a hash code value for this object.
         */
        @Override
        public int hashCode() {
            return stringValue.hashCode();
        }

        /**
         * Check if objects are equal
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            return ((ComparableMethod) obj).stringValue.equals(stringValue);
        }
    }

}
