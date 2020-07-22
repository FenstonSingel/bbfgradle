package com.stepanov.bbf.coverage.instrumentation;

import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM7;

public class MethodInstrumenter extends ClassVisitor {

    public MethodInstrumenter(ClassVisitor classVisitor) {
        super(ASM7, classVisitor);
    }

    private String className = "";

    @Override
    public void visit(int version,
                      int access,
                      String name,
                      String signature,
                      String superName,
                      String[] interfaces)
    {
        className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String descriptor,
                                     String signature,
                                     String[] exceptions)
    {
        MethodVisitor defaultMethodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
        return new AdviceAdapter(ASM7, defaultMethodVisitor, access, name, descriptor) {
            @Override
            protected void onMethodEnter() {
                mv.visitLdcInsn(className + ":" + name + descriptor);
                mv.visitMethodInsn(INVOKESTATIC,
                        "com/stepanov/bbf/coverage/CompilerInstrumentation",
                        "recordMethodExecution",
                        "(Ljava/lang/String;)V",
                        false
                );
            }
        };
    }

}
