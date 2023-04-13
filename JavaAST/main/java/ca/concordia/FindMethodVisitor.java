package ca.concordia;

import org.eclipse.jdt.core.dom.*;


public class FindMethodVisitor extends ASTVisitor  {
    private final int lineNumber;
    private String methodName;
    protected final CompilationUnit compilationUnit;
    public FindMethodVisitor(CompilationUnit compilationUnit, int lineNumber) {
        this.compilationUnit = compilationUnit;
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        int startPosition = node.getStartPosition();
        int endPosition = startPosition + node.getLength();
        int startLine = compilationUnit.getLineNumber(startPosition);
        int endLine = compilationUnit.getLineNumber(endPosition);

        if (lineNumber >= startLine && lineNumber <= endLine) {
            methodName = node.getName().toString();
        }

        return super.visit(node);
    }

    public String getMethodName() {
        return methodName;
    }

}
