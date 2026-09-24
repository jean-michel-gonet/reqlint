package com.reqlint.plugin;

import com.reqlint.core.output.latex.TraceabilityMatrixOutput;
import com.reqlint.core.model.specification.SpecificationTree;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@Mojo(name = "traceability-matrix", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class TraceabilityMatrixMojo extends ReqlintMojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceabilityMatrixMojo.class);

    /**
     * Name of the latex file where to output the upstream traceability matrix - SSS to SR.
     */
    @Parameter(name = "upstreamSssSrsTrxOutput")
    protected File upstreamSssSrsTrxOutput;

    /**
     * Name of the latex file where to output the downstream traceability matrix - SSS to SR.
     */
    @Parameter(name = "downstreamSssSrsTrxOutput")
    protected File downstreamSssSrsTrxOutput;

    /**
     * Name of the latex file where to output the upstream traceability matrix - TC to SR.
     */
    @Parameter(name = "upstreamSrsTcTrxOutput")
    protected File upstreamSrsTcTrxOutput;

    /**
     * Name of the latex file where to output the upstream traceability matrix - SR to TC.
     */
    @Parameter(name = "downstreamSrsTcTrxOutput")
    protected File downstreamSrsTcTrxOutput;

    /**
     * Name of the latex file where to output the warnings detected while building the traceability matrix.
     */
    @Parameter(name = "traceabilityWarningsOutput")
    protected File traceabilityWarningsOutput;

    @Override
    public void execute() throws MojoExecutionException {
        LOGGER.info("traceability-matrix goal started");
        try {
            SpecificationTree specificationTree = readSpecificationTree();
            specificationTree.verify();
            writeSssSrsTrx(specificationTree);
            writeTraceabilityWarnings(specificationTree);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run traceability-matrix goal", e);
        }
    }

    private void writeSssSrsTrx(SpecificationTree specificationTree) throws IOException {
        TraceabilityMatrixOutput traceabilityMatrixOutput = new TraceabilityMatrixOutput(specificationTree);
        if (upstreamSssSrsTrxOutput != null) {
            try (Writer writer = this.open("SSS / SRS upstream traceability matrix", upstreamSssSrsTrxOutput)) {
                traceabilityMatrixOutput.writeUpstreamSssSrsTrx(writer);
            }
        }
        if (downstreamSssSrsTrxOutput != null) {
            try (Writer writer = open("SSS / SRS downstream traceability matrix", downstreamSssSrsTrxOutput)) {
                traceabilityMatrixOutput.writeDownstreamSssSrsTrx(writer);
            }
        }

        if (upstreamSrsTcTrxOutput != null) {
            try (Writer writer = this.open("SRS / TC upstream traceability matrix", upstreamSrsTcTrxOutput)) {
                traceabilityMatrixOutput.writeUpstreamSrsTcTrx(writer);
            }
        }
        if (downstreamSrsTcTrxOutput != null) {
            try (Writer writer = open("SRS / TC downstream traceability matrix", downstreamSrsTcTrxOutput)) {
                traceabilityMatrixOutput.writeDownstreamSrsTcTrx(writer);
            }
        }
    }

    private void writeTraceabilityWarnings(SpecificationTree specificationTree) throws  IOException {
        if (traceabilityWarningsOutput.exists()) {
            if (!traceabilityWarningsOutput.delete()) {
                throw new IllegalArgumentException("Cannot overwrite " + traceabilityWarningsOutput.getAbsolutePath());
            }
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityWarningsOutput))) {
            TraceabilityMatrixOutput traceabilityMatrixOutput = new TraceabilityMatrixOutput(specificationTree);
            traceabilityMatrixOutput.writeWarnings(writer);
        }
    }
}
