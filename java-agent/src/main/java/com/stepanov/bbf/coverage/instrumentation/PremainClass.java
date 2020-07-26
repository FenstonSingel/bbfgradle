package com.stepanov.bbf.coverage.instrumentation;

import com.stepanov.bbf.coverage.CompilerInstrumentation;
import java.lang.instrument.Instrumentation;

public class PremainClass {

    public static void premain(String args, Instrumentation instr) {
        // Set the required coverage type here.
        CompilerInstrumentation.setCoverageType(CompilerInstrumentation.CoverageType.BRANCH);
        instr.addTransformer(new Transformer());
    }

}
