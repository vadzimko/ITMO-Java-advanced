package ru.ifmo.rain.badyaev.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Stream;

public class RecursiveWalk {
    private static int FNV_CONST = 16777619;
    private static int FNV0 = (int) 2166136261L;
    private static String BAD_FILE_HASH = "00000000";

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Usage: RecursiveWalk <input> <output>");
        } else {
            int lastSlash = args[1].lastIndexOf(System.getProperty("file.separator"));
            if (lastSlash != -1) {
                new File(args[1].substring(0, lastSlash)).mkdirs();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8))) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {

                    String directoryName = reader.readLine();
                    while (directoryName != null) {
                        File directory = new File(directoryName);
                        if (directory.isFile()) {
                            printHash(writer, findHash(directory), directoryName);
                        } else {
                            try {
                                Stream<Path> paths = Files.walk(Paths.get(directoryName));

                                paths.forEach((path) -> {
                                    String fileName = path.toString();
                                    File file = new File(fileName);
                                    if (file.isFile()) {
                                        printHash(writer, findHash(file), fileName);
                                    }
                                });
                            } catch (NoSuchFileException | InvalidPathException e) {
                                printHash(writer, BAD_FILE_HASH, directoryName);
                            } catch (IOException e) {
                                System.err.println("Error while scanning: " + directory + ", " + e.getMessage());
                            }
                        }

                        directoryName = reader.readLine();
                    }
                } catch (UnsupportedEncodingException e) {
                    System.err.println("Unsupported encoding utf-8");
                } catch (FileNotFoundException e) {
                    System.err.println("Can't create or open file: " + args[1] + ", " + e.getMessage());
                } catch (SecurityException e) {
                    System.err.println("Don't have access to write to file: " + args[1] + ", " + e.getMessage());
                }

            } catch (UnsupportedEncodingException e) {
                System.err.println("Unsupported encoding utf-8");
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + args[0] + ", " + e.getMessage());
            } catch (SecurityException e) {
                System.err.println("Don't have access to read from file: " + args[0] + ", " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error while reading from: " + args[0] + ", " + e.getMessage());
            }
        }
    }

    private static String findHash(File file) {
        try (InputStream is = new FileInputStream(file)) {
            byte[] buf = new byte[4096];
            int size;
            int hash = FNV0;
            while ((size = is.read(buf)) != -1) {
                for (int i = 0; i < size; i++) {
                    hash *= FNV_CONST;
                    hash ^= buf[i] & 255;
                }
            }
            return String.format("%08x", hash);
        } catch (IOException e) {
            return BAD_FILE_HASH;
        }
    }

    private static void printHash(BufferedWriter writer, String hash, String file) {
        try {
            writer.write(hash + " " + file + System.getProperty("line.separator"));
        } catch (IOException e) {
            System.err.println("Error while writing to output file");
        }
    }
}