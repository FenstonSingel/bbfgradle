package com.stepanov.bbf.coverage.instrumentation;

import com.stepanov.bbf.coverage.CompilerInstrumentation;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;
import java.util.HashMap;

import static org.objectweb.asm.Opcodes.ASM7;

public class BranchInstrumenter extends ClassVisitor {

    public BranchInstrumenter(ClassVisitor classVisitor) {
        super(ASM7, classVisitor);
    }

    private String className = "";

    private static final Map<Integer, String> opcodeToStr = new HashMap<>();
    static {
        opcodeToStr.put(Opcodes.IFNULL, "IFNULL");
        opcodeToStr.put(Opcodes.IFNONNULL, "IFNONNULL");
        opcodeToStr.put(Opcodes.IFEQ, "IFEQ");
        opcodeToStr.put(Opcodes.IFNE, "IFNE");
        opcodeToStr.put(Opcodes.IFLT, "IFLT");
        opcodeToStr.put(Opcodes.IFLE, "IFLE");
        opcodeToStr.put(Opcodes.IFGT, "IFGT");
        opcodeToStr.put(Opcodes.IFGE, "IFGE");
        opcodeToStr.put(Opcodes.IF_ACMPEQ, "IF_ACMPEQ");
        opcodeToStr.put(Opcodes.IF_ACMPNE, "IF_ACMPNE");
        opcodeToStr.put(Opcodes.IF_ICMPEQ, "IF_ICMPEQ");
        opcodeToStr.put(Opcodes.IF_ICMPNE, "IF_ICMPNE");
        opcodeToStr.put(Opcodes.IF_ICMPLT, "IF_ICMPLT");
        opcodeToStr.put(Opcodes.IF_ICMPLE, "IF_ICMPLE");
        opcodeToStr.put(Opcodes.IF_ICMPGT, "IF_ICMPGT");
        opcodeToStr.put(Opcodes.IF_ICMPGE, "IF_ICMPGE");
        opcodeToStr.put(Opcodes.TABLESWITCH, "TABLESWITCH");
        opcodeToStr.put(Opcodes.LOOKUPSWITCH, "LOOKUPSWITCH");
    }

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
            private final Map<Integer, Integer> insnCounter = new HashMap<>();

            private String getInsnId(int opcode) {
                return className + ":" + name + descriptor + ":" + opcodeToStr.get(opcode) + insnCounter.get(opcode);
            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                if (opcode != Opcodes.GOTO && opcode != Opcodes.JSR) {
                    insnCounter.merge(opcode, 1, Integer::sum);
                }
                switch (opcode) {
                    case Opcodes.IFNULL:
                    case Opcodes.IFNONNULL:
                        mv.visitInsn(Opcodes.DUP);
                        mv.visitLdcInsn(getInsnId(opcode));
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "com/stepanov/bbf/coverage/CompilerInstrumentation",
                                "recordUnaryRefCmp",
                                "(Ljava/lang/Object;Ljava/lang/String;)V",
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
                        mv.visitLdcInsn(getInsnId(opcode));
                        mv.visitLdcInsn(opcode);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "com/stepanov/bbf/coverage/CompilerInstrumentation",
                                "recordUnaryIntCmp",
                                "(ILjava/lang/String;I)V",
                                false
                        );
                        break;
                    case Opcodes.IF_ACMPEQ:
                    case Opcodes.IF_ACMPNE:
                        mv.visitInsn(Opcodes.DUP2);
                        mv.visitLdcInsn(getInsnId(opcode));
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "com/stepanov/bbf/coverage/CompilerInstrumentation",
                                "recordBinaryRefCmp",
                                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V",
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
                        mv.visitLdcInsn(getInsnId(opcode));
                        mv.visitLdcInsn(opcode);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "com/stepanov/bbf/coverage/CompilerInstrumentation",
                                "recordBinaryIntCmp",
                                "(IILjava/lang/String;I)V",
                                false
                        );
                        break;
                }
                super.visitJumpInsn(opcode, label);
            }

            @Override
            public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
                insnCounter.merge(Opcodes.TABLESWITCH, 1, Integer::sum);
                String insn_id = getInsnId(Opcodes.TABLESWITCH);
                CompilerInstrumentation.rememberTableSwitch(insn_id, min, max);
                mv.visitInsn(Opcodes.DUP);
                mv.visitLdcInsn(insn_id);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "com/stepanov/bbf/coverage/CompilerInstrumentation",
                        "recordTableSwitch",
                        "(ILjava/lang/String;)V",
                        false
                );
                super.visitTableSwitchInsn(min, max, dflt, labels);
            }

            @Override
            public void visitLookupSwitchInsn(Label dflt, int[] keys, Label... labels) {
                insnCounter.merge(Opcodes.LOOKUPSWITCH, 1, Integer::sum);
                String insn_id = getInsnId(Opcodes.LOOKUPSWITCH);
                CompilerInstrumentation.rememberLookupSwitch(insn_id, keys);
                mv.visitInsn(Opcodes.DUP);
                mv.visitLdcInsn(insn_id);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "com/stepanov/bbf/coverage/CompilerInstrumentation",
                        "recordLookupSwitch",
                        "(ILjava/lang/String;)V",
                        false
                );
                mv.visitLookupSwitchInsn(dflt, keys, labels);
            }
        };
    }

}
