package com.stepanov.bbf.coverage.instrumentation;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.ASM7;

public class BranchInstrumenter extends ClassVisitor {

    public BranchInstrumenter(ClassVisitor classVisitor) {
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
        return new MethodVisitor(ASM7, defaultMethodVisitor) {
            private int insnCounter = 0;

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                switch (opcode) {
                    case Opcodes.IFNULL:
                    case Opcodes.IFNONNULL:
                        mv.visitInsn(Opcodes.DUP);
                        mv.visitLdcInsn(className + ":" + name + descriptor);
                        mv.visitLdcInsn(insnCounter);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "com/stepanov/bbf/coverage/CompilerInstrumentation",
                                "recordUnaryRefCmp",
                                "(Ljava/lang/Object;Ljava/lang/String;I)V",
                                false
                        );
                        break;
                    case Opcodes.IFEQ:
                    case Opcodes.IFNE:
                    case Opcodes.IFLT:
                    case Opcodes.IFLE:
                    case Opcodes.IFGT:
                    case Opcodes.IFGE:
                        mv.visitInsn(Opcodes.DUP);
                        mv.visitLdcInsn(className + ":" + name + descriptor);
                        mv.visitLdcInsn(insnCounter);
                        mv.visitLdcInsn(opcode);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "com/stepanov/bbf/coverage/CompilerInstrumentation",
                                "recordUnaryIntCmp",
                                "(ILjava/lang/String;II)V",
                                false
                        );
                        break;
                    case Opcodes.IF_ACMPEQ:
                    case Opcodes.IF_ACMPNE:
                        mv.visitInsn(Opcodes.DUP2);
                        mv.visitLdcInsn(className + ":" + name + descriptor);
                        mv.visitLdcInsn(insnCounter);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "com/stepanov/bbf/coverage/CompilerInstrumentation",
                                "recordBinaryRefCmp",
                                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;I)V",
                                false
                        );
                        break;
                    case Opcodes.IF_ICMPEQ:
                    case Opcodes.IF_ICMPNE:
                    case Opcodes.IF_ICMPLT:
                    case Opcodes.IF_ICMPLE:
                    case Opcodes.IF_ICMPGT:
                    case Opcodes.IF_ICMPGE:
                        mv.visitInsn(Opcodes.DUP2);
                        mv.visitLdcInsn(className + ":" + name + descriptor);
                        mv.visitLdcInsn(insnCounter);
                        mv.visitLdcInsn(opcode);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "com/stepanov/bbf/coverage/CompilerInstrumentation",
                                "recordBinaryIntCmp",
                                "(IILjava/lang/String;II)V",
                                false
                        );
                        break;
                }
                if (opcode != Opcodes.GOTO && opcode != Opcodes.JSR) {
                    insnCounter++;
                }
                super.visitJumpInsn(opcode, label);
            }
        };
    }

}
