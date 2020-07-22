package com.stepanov.bbf.coverage.instrumentation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import com.stepanov.bbf.coverage.CompilerInstrumentation;

public class Transformer implements ClassFileTransformer {

    List<String> blocklist = Arrays.asList("cli");

    private boolean isTransformationUnnecessary(String className) {
        if (!CompilerInstrumentation.getShouldClassesBeInstrumented()) return true;
        if (!className.startsWith("org/jetbrains/kotlin/")) return true;
        for (String blockedEntry : blocklist) {
            // TODO It **might** be better to break className into a set of '/'-separated substrings.
            if (className.contains(blockedEntry)) return true;
        }
        return false;
    }

    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classFile)
    {
        return transform(className, classFile);
    }

    public byte[] transform(String className, byte[] classFile) {
        long startTime = System.currentTimeMillis();

        if (isTransformationUnnecessary(className)) {
            CompilerInstrumentation.increaseTimeSpentOnInstrumentation(System.currentTimeMillis() - startTime);
            return null;
        }

        byte[] classFileCopy = Arrays.copyOf(classFile, classFile.length);
        ClassReader classReader = new ClassReader(classFileCopy);

        byte[] newClassFile = performTransformation(classReader);
        CompilerInstrumentation.increaseTimeSpentOnInstrumentation(System.currentTimeMillis() - startTime);
        return newClassFile;
    }

    public byte[] transform(String className) throws IOException {
        long startTime = System.currentTimeMillis();

        if (isTransformationUnnecessary(className)) {
            CompilerInstrumentation.increaseTimeSpentOnInstrumentation(System.currentTimeMillis() - startTime);
            return null;
        }

        InputStream resource = ClassLoader.getSystemResourceAsStream(className + ".class");
        if (resource == null) throw new IOException(String.format("Class %s was not loaded.", className));
        ClassReader classReader = new ClassReader(resource);

        byte[] newClassFile = performTransformation(classReader);
        CompilerInstrumentation.increaseTimeSpentOnInstrumentation(System.currentTimeMillis() - startTime);
        return newClassFile;
    }

    private byte[] performTransformation(ClassReader classReader) {
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);

        // A necessary implementation has to be manually configured here.

        MethodInstrumenter instrumenter = new MethodInstrumenter(classWriter);
//        BranchInstrumenter instrumenter = new BranchInstrumenter(classWriter);

        classReader.accept(instrumenter, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

}
