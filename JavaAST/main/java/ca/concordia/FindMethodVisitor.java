package ca.concordia;

import org.eclipse.jdt.core.dom.*;


public class FindMethodVisitor extends ASTVisitor  {
    private final int lineNumber;
    private String methodName;
    private MethodData methodData;
    protected final CompilationUnit compilationUnit;
    private Boolean isTest;

    public FindMethodVisitor(CompilationUnit compilationUnit, int lineNumber, boolean isTest) {
        this.compilationUnit = compilationUnit;
        this.lineNumber = lineNumber;
        this.isTest = isTest;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        int startPosition = node.getStartPosition();
        int endPosition = startPosition + node.getLength();
        int startLine = compilationUnit.getLineNumber(startPosition);
        int endLine = compilationUnit.getLineNumber(endPosition);

        if (lineNumber >= startLine && lineNumber <= endLine) {
            methodName = node.getName().toString();
            methodData = new MethodData(methodName, startLine, endLine);
        }

        return super.visit(node);
    }

    public String getMethodName() {
        return methodName;
    }
    public MethodData getmethodData() {
        return methodData;
    }


}