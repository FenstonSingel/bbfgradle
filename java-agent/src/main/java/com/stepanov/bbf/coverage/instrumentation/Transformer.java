package com.stepanov.bbf.coverage.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import com.stepanov.bbf.coverage.CompilerInstrumentation;

public class Transformer implements ClassFileTransformer {

    // TODO Look into blocking (even) more supposedly auxiliary packages.
    List<String> blocklist = Arrays.asList("cli", "diagnostics", "utils", "container");
//    List<String> allowlist = Arrays.asList("backend", "frontend", "fir");

    private boolean isTransformationUnnecessary(String className) {
        if (!className.startsWith("org/jetbrains/kotlin/")) return true;
        List<String> tokens = Arrays.asList(className.split("[/$]"));
        for (String blockedEntry : blocklist) {
            if (tokens.contains(blockedEntry)) return true;
        }
//        for (String allowedEntry : allowlist) {
//            if (tokens.contains(allowedEntry)) return false;
//        }
//        return true;
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
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);

        ClassVisitor instrumenter;
        switch (CompilerInstrumentation.getCoverageType()) {
            case METHOD:
                instrumenter = new MethodInstrumenter(classWriter);
                break;
            case BRANCH:
                instrumenter = new BranchInstrumenter(classWriter);
                break;
            default:
                CompilerInstrumentation.increaseTimeSpentOnInstrumentation(System.currentTimeMillis() - startTime);
                throw new IllegalStateException("Unexpected value: " + CompilerInstrumentation.getCoverageType());
        }

        classReader.accept(instrumenter, ClassReader.EXPAND_FRAMES);
        byte[] newClassFile = classWriter.toByteArray();

        CompilerInstrumentation.increaseTimeSpentOnInstrumentation(System.currentTimeMillis() - startTime);
        return newClassFile;
    }

}
