# Introduction

Reqlint is a Doc-as-Code Maven plugin for SWAL3 requirements validation. 
ReqLint parses LaTeX specs and Cucumber test outputs to perform graph static analysis, 
ensuring zero orphan requirements or unverified test cases. 
Keep your regulatory traceability matrices structurally sound and directly tied to your Java build pipeline. 

# Debugging

In the terminal, use `mvnDebug` instead of `mvn`. 
For example:

```bash
mvnDbg reqlint:test-results@test-results-config
```

In your IDE, create a remote debug configuration with:
- port number: 8000.
- Host: localhost

Place a breakpoint somewhere appropriate and click on Debug/Attach.