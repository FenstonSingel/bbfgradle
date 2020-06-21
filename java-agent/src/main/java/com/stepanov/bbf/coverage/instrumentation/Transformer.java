package com.stepanov.bbf.coverage.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.stepanov.bbf.coverage.CompilerInstrumentation;

public class Transformer implements ClassFileTransformer {

    private boolean isClassRelevant(String className) {
        return className.contains("org/jetbrains/kotlin/");
    }

    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classFile)
    {
        if (!CompilerInstrumentation.getShouldClassesBeInstrumented()) {
            return null;
        }

        if (!isClassRelevant(className)) {
            return null;
        }

        CompilerInstrumentation.startInstrumentationTimer();

        byte[] classFileCopy = Arrays.copyOf(classFile, classFile.length);
        ClassReader classReader = new ClassReader(classFileCopy);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        MethodInstrumenter instrumenter = new MethodInstrumenter(classWriter); // choose necessary implementation
        classReader.accept(instrumenter, 0);
        byte[] newClassFile = classWriter.toByteArray();

        CompilerInstrumentation.pauseInstrumentationTimer();

        return newClassFile;
    }

}
