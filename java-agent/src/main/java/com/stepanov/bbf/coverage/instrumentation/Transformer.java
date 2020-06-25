package com.stepanov.bbf.coverage.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import com.stepanov.bbf.coverage.CompilerInstrumentation;

public class Transformer implements ClassFileTransformer {

    List<String> blocklist = Arrays.asList("cli");

    private boolean isClassRelevant(String className) {
        for (String blockedEntry : blocklist) {
            if (className.contains(blockedEntry)) return false;
        }
        return className.contains("org/jetbrains/kotlin/");
    }

    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classFile)
    {
        long startTime = System.currentTimeMillis();

        if (!CompilerInstrumentation.getShouldClassesBeInstrumented()) {
            CompilerInstrumentation.updateInstrumentationTimer(System.currentTimeMillis() - startTime);
            return null;
        }

        if (!isClassRelevant(className)) {
            CompilerInstrumentation.updateInstrumentationTimer(System.currentTimeMillis() - startTime);
            return null;
        }

        byte[] classFileCopy = Arrays.copyOf(classFile, classFile.length);
        ClassReader classReader = new ClassReader(classFileCopy);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        MethodInstrumenter instrumenter = new MethodInstrumenter(classWriter); // choose necessary implementation
        classReader.accept(instrumenter, 0);
        byte[] newClassFile = classWriter.toByteArray();

        CompilerInstrumentation.updateInstrumentationTimer(System.currentTimeMillis() - startTime);
        return newClassFile;
    }

}
