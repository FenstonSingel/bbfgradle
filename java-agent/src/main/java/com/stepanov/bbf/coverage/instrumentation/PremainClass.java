package com.stepanov.bbf.coverage.instrumentation;

import com.stepanov.bbf.coverage.CompilerInstrumentation;
import java.lang.instrument.Instrumentation;

public class PremainClass {

    public static void premain(String args, Instrumentation instr) {
        // TODO Look into making a proper settings manager instead of having to constantly rebuild things.
        // Set the required coverage type here.
        CompilerInstrumentation.setCoverageType(CompilerInstrumentation.CoverageType.METHOD);
        instr.addTransformer(new Transformer());
    }

}
