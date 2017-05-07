package cz.osu.core;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.stmt.BlockStmt;
import cz.osu.core.enums.Annotations;
import cz.osu.core.model.Method;
import cz.osu.core.model.TestSuit;
import cz.osu.core.model.Variable;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: thesis
 * Created by Jakub on 10. 4. 2017.
 */
@Component
public class TestSuitParser {

    @Inject
    private TestCaseParser testCaseParser;

    @Inject
    private BindingResolver bindingResolver;

    /**
     * Represents java file with whole test suit. Provides fields, methods etc. as objects.
     */
    private CompilationUnit compilationUnit;

    public void setCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    /**
     * Method filters MethodDeclarations by given annotation.
     * @param annotation filtering criteria.
     * @return List of MethodDeclarations whose annotation matches @param annotation.
     */
    List<MethodDeclaration> filterMethodsByAnnotation(String annotation) {
        List<MethodDeclaration> methods = compilationUnit.getChildNodesByType(MethodDeclaration.class);

        return methods.stream()
                .filter(method -> method.getAnnotationByName(annotation).isPresent())
                .collect(Collectors.toList());
    }

    private boolean isTestIgnored(MethodDeclaration testCase) {
        return testCase.getAnnotationByName(Annotations.IGNORE.getValue()).isPresent();
    }

    List<MethodCallExpr> filterMethodCallExpr(BlockStmt methodBody) {
        return methodBody.getChildNodesByType(MethodCallExpr.class);
    }

    List<FieldDeclaration> getFieldDeclarations() {
        return compilationUnit.getChildNodesByType(FieldDeclaration.class);
    }

    /*public TestSuit parseTestSuit() {

    }*/



    /*protected final List<Variable> initializeFields() {
        FieldSetterVisitor fvisitor = new FieldSetterVisitor();
        List<Variable> fields = new ArrayList<>();
        MethodDeclaration method;

        fvisitor.visit(compilationUnit, fields);

        if(setUpMethod) {
            method = findSetUpMethod();
            resolveBindings(fields, method);
        }

        return fields;
    }


    // Vrací true pokud má Selenium test setUp metodu jinak false
    protected final boolean hasSetUpMethod() {
        return !findMethodsByAnnotation("@Before").isEmpty();
    }


    public List<MethodDeclaration> findMethodsByAnnotation(String annotation) {
        List<BodyDeclaration> members = filterMembers();

        List<MethodDeclaration> methods = members
                .stream()
                .map(e -> (MethodDeclaration)e)
                .filter(e -> (e.getAnnotations() == null)?
                        false :
                        e.getAnnotations()
                                .stream()
                                .anyMatch(a-> a.toString().contains(annotation)))
                .collect(Collectors.toList());

        return methods;
    }

    // Vrací metodu podle jejího názvu
    public MethodDeclaration findMethodByName(String name) {
        List<BodyDeclaration> members = filterMembers(o -> o instanceof MethodDeclaration);

        List<MethodDeclaration> method = members
                .stream()
                .map(e -> (MethodDeclaration)e)
                .filter(e -> e.getName().equals(name))
                .collect(Collectors.toList());

        return method.get(0);
    }

    // Vrací seznam deklarovaných proměnných v rámci jedné metody
    private List<Variable> findMethodVariables(MethodDeclaration method) {
        VariableSetterVisitor vVisitor = new VariableSetterVisitor();
        List<Variable> testVariables = new ArrayList<>();

        vVisitor.visit(method, testVariables);

        return testVariables;
    }

    // Návázaní hodnot proměnných metody
    public void resolveBindings(List<Variable> vars, MethodDeclaration method) {
        List<Value> varValues;

        for (Variable var: vars) {
            varValues = findVariableValues(method, var.getName());
            if(varValues != null) {
                for (Value varValue : varValues) {
                    var.setValue(varValue);
                }
            }
        }
    }

    // Metoda
    public List<Variable> initializeVariables(String testName) {
        MethodDeclaration testMethod = findMethodByName(testName);
        variables = findMethodVariables(testMethod);
        // K lokálním proměnným přidám ještě fieldy (instanční proměnné, které
        // se mohou v metodé vyskytovat)
        variables.addAll(fields);
        resolveBindings(variables, testMethod);

        return variables;
    }

    // Metoda která zjistí hodnoty proměnných v rámci Testu(testovací metody)
    // Zamyslet se nad vyjimkou, kterou by mohl vyhodit matcher
    private List<Value> findVariableValues(MethodDeclaration method, String varName) {
        Pattern p1 = Pattern.compile(varName + "\\s+\\=\\s.+\\;");
        Pattern p2 = Pattern.compile("[^\\s\\;]+\\s*[^\\s\\;]*");
        List<Statement> statements = method.getBody().get().getStatements();
        List<Value> varValues;

        List<Statement> matchedStatements = statements
                .stream()
                .filter(e -> p1.matcher(e.toString()).find())
                .collect(Collectors.toList());

        if(matchedStatements.isEmpty())
            return null;

        List<String> values = matchedStatements
                .stream()
                .flatMap(line -> Stream.of(line.toString().split("=")))
                .filter(e -> !e.contains(varName))
                .map(e -> { Matcher m = p2.matcher(e);
                    m.find();
                    return m.group();})
                .collect(Collectors.toList());

        List<Integer> valuesBeginLine = matchedStatements
                .stream()
                .map(e -> e.getBegin().get().line)
                .collect(Collectors.toList());

        varValues = mergeVariableValues(values, valuesBeginLine);

        return varValues;
    }

    private List<Value> mergeVariableValues(List<String> values, List<Integer> valuesBeginLine) {
        List<Value> varValues = new ArrayList<>();
        Iterator<String> it1 = values.iterator();
        Iterator<Integer> it2 = valuesBeginLine.iterator();
        int beginLine = it2.next();
        int endLine;
        String value;

        while (it1.hasNext()) {
            value = it1.next();
            if(it2.hasNext()){
                endLine = it2.next();
                varValues.add(new Value(value, beginLine, endLine));
                beginLine = endLine;
            }
            else
                varValues.add(new Value(value, beginLine));
        }
        return varValues;
    }

    // Vnitří třída (inner class), která má jedinou metodu visit
    // Visit je metoda která "navštíví" vsechny deklarovane promenné v rámci jedné
    // metody(Sel. testu) a dále využije metody setVariables k nastavení seznamu
    // testVariables, jejiž položky maji tvar Variable(typ, nazev, hodnota(y))
    private class VariableSetterVisitor extends VoidVisitorAdapter {

        @Override
        public void visit(VariableDeclarationExpr n, Object testVariables) {
            List<VariableDeclarator> myVars = n.getVariables();
            String type = n.getType().toString();
            setVariables(myVars, testVariables, type);
        }
    }

    // Vnitří třída (inner class), která má jedinou metodu visit
    // Visit je metoda která "navštíví" vsechny deklarovane fieldy v rámci
    // celeho souboru se Selenium testem(testy)
    private class FieldSetterVisitor extends VoidVisitorAdapter {

        @Override
        public void visit(FieldDeclaration n, Object testVariables) {
            List<VariableDeclarator> myFields = n.getVariables();
            String type = n.getType().toString();
            setVariables(myFields, testVariables, type);
        }
    }

    // Metoda visit navštíví a naplní seznam příkazů (List of commands)
    // Každý command může být složen z několika volání metod (Method call expr)
    // Př: driver.findElement(By.id(String)).click() --> skláda se z volání
    // tří metod
    private class Method extends VoidVisitorAdapter {
        private Deque<MethodCallExpr> command = new ArrayDeque<>();
        private int counter = 0;

        @Override
        public void visit(MethodCallExpr n, Object arg) {
            counter++;
            super.visit(n, arg);
            //System.out.println(n.getBeginLine() + " " + n.getName());
            command.push(n);
            counter--;
            if(counter == 0){
                List<Deque<MethodCallExpr>> tmpCommands = (List<Deque<MethodCallExpr>>) arg;
                tmpCommands.add(new ArrayDeque<>(command));
                command.clear();
            }
        }
    }

    private void setVariables(List<VariableDeclarator> vars, Object testVariables, String type) {
        List<Variable> tmp = (List<Variable>)testVariables;

        for (VariableDeclarator var: vars){
            if(var.getInitializer() != null)
                tmp.add(new Variable(type, var.getNameAsString()),
                        new Value(var.getInitializer().get().toString(),
                                var.getBegin().get().line)));
            else
                tmp.add(new Variable(type, var.getId().toString()));
        }
    }

    // Potom upravím aby parametrem byla nejaka podmínka a ne jen string
    private List<Variable> filterVariables(List<Variable> vars, String driverName){
        List<Variable> filtredVars = vars
                .stream()
                .filter(e -> e.getValues()
                        .stream()
                        .anyMatch(v -> v.getValue().contains(driverName)))
                .collect(Collectors.toList());

        return filtredVars;
    }

    // Vrací seznam commands týkající se pouze příkazů Selenia
    // tzn. příkazy volané nad WebDriverem, WebElement atd.
    private List<Deque<MethodCallExpr>> filterMethodCallExprs(List<Variable> vars,
                                                              MethodDeclaration method, String driverName) {
        List<Deque<MethodCallExpr>> methodCallExprs;
        List<Deque<MethodCallExpr>> seleniumMethodCallExprs;

        methodCallExprs = new ArrayList<>();
        new Method().visit(method, methodCallExprs);

        seleniumMethodCallExprs = methodCallExprs
                .stream()
                .filter(m -> vars
                        .stream()
                        .anyMatch(var -> m.getFirst().toString().contains(var.getName() + ".")
                                || m.getFirst().toString().contains(driverName)))
                .collect(Collectors.toList());

        return seleniumMethodCallExprs;
    }

    private List<Deque<MethodCallExpr>> filterSeleniumMethodCallExprs(List<Variable> vars,
                                                                      List<Deque<MethodCallExpr>> seleniumMethodCallExprs,
                                                                      MethodCallCondition c) {
        List<Deque<MethodCallExpr>> driverMethodCalls;

        driverMethodCalls = seleniumMethodCallExprs
                .stream()
                .filter(m -> vars
                        .stream()
                        .flatMap(var -> var.getValues().stream())
                        .anyMatch(val -> c.passedCondition(m.getFirst().toString(),
                                val.getValue())))
                .collect(Collectors.toList());

        return driverMethodCalls;
    }

    // POZN: PŘEDĚLAT ... natvrdo nastavena hodnota
    // pokud má var jen jednu hotnotu tak endline je nastaveny na 0
    // pri vytvaření poslední value (nebo jedine) zjistit endline cele metody
    private String findVarValue(Variable var, int lineNumber) {
        String value = var.getValues()
                .stream()
                .filter(val -> (val.getBeginLine() < lineNumber)
                        && (100 > lineNumber))  // !!!!!!!
                .map(val -> val.getValue())
                .reduce("", String::concat);

        return value;
    }

    // Možná použít reduce s copy konstruktorem arraydeque ???
    private Deque<MethodCallExpr> getMethodCallExprsByVarValue(String varValue,
                                                               List<Deque<MethodCallExpr>> tmpMethodCallExprs) {
        Deque<MethodCallExpr> methodCallExprs;

        methodCallExprs = tmpMethodCallExprs
                .stream()
                .filter(e -> e.getFirst().toString().equals(varValue))
                .flatMap(e -> e.stream())
                .collect(ArrayDeque::new, ArrayDeque::add, ArrayDeque::addAll);

        return methodCallExprs;
    }

    private void assignmentToFluent(
            List<Variable> seleniumVars,
            List<Deque<MethodCallExpr>> driverMethodCallExprs,
            List<Deque<MethodCallExpr>> tmpMethodCallExprs) {
        Deque<MethodCallExpr> tmpDequeMCExpr;
        MethodCallExpr tmpMCExpr;
        Variable tmpVar;
        String tmpStr;
        String varValue;

        for (Deque<MethodCallExpr> dmce : driverMethodCallExprs) {
            tmpMCExpr = dmce.getFirst();
            tmpStr = tmpMCExpr.toString();

            for (Variable var : seleniumVars) {
                if(tmpStr.contains(var.getName())) {
                    int lineNumber = tmpMCExpr.getBegin().get().line;
                    varValue = findVarValue(var, lineNumber);
                    tmpDequeMCExpr = getMethodCallExprsByVarValue(varValue,
                            tmpMethodCallExprs);
                    dmce.addAll(tmpDequeMCExpr);
                }
            }
        }
    }

    // Vrací seznam methodcalls (selenium prikazu), které slouží jako vstup pro
    // vytvoření mých Commands
    public List<Deque<MethodCallExpr>> prepareDriverMethodCalls(
            String testName,
            List<Variable> vars){
        List<Deque<MethodCallExpr>> driverMethodCallExprs;
        List<Deque<MethodCallExpr>> seleniumMethodCallExprs;
        List<Deque<MethodCallExpr>> tmpMethodCallExprs;
        List<Variable> seleniumVars;
        MethodDeclaration method;
        String driverName = "driver";
        // TODO: 11. 4. 2017 hardcoded driver name for testing purpose


        //method = findMethodByName(testName);
        seleniumVars = filterVariables(vars, driverName);
        if (!seleniumVars.isEmpty()) {
            seleniumMethodCallExprs = filterMethodCallExprs(seleniumVars, method, driverName);
            driverMethodCallExprs = filterSeleniumMethodCallExprs(seleniumVars,
                    seleniumMethodCallExprs, (m, val) -> !m.equals(val));
            tmpMethodCallExprs = filterSeleniumMethodCallExprs(seleniumVars,
                    seleniumMethodCallExprs, (m, val) -> m.equals(val));

            assignmentToFluent(seleniumVars, driverMethodCallExprs, tmpMethodCallExprs);
        }
        else
            driverMethodCallExprs = filterMethodCallExprs(vars, method, driverName);

        return driverMethodCallExprs;
    }

    public String resolveParametrBinding(int beginLine, String param,
                                         List<Variable> vars){
        Variable var = vars
                .stream()
                .filter(v -> v.getName().equals(param))
                .reduce(null, (a, v) -> new Variable(v));

        if (var != null)
            return findVarValue(var, beginLine);
        else
            return param;
    }

    public boolean removeParam(String methodNameExpr,
                               Deque<MethodCallExpr> driverMethodCallExprs) {
        Iterator<Expression> it;
        List<Expression> paramExprs;
        String param;

        for (MethodCallExpr driverMethodCallExpr : driverMethodCallExprs) {
            paramExprs = driverMethodCallExpr.getArguments();
            if (paramExprs != null) {
                it = paramExprs.iterator();
                while (it.hasNext()) {
                    param = it.next().toString();
                    if (param.equals(methodNameExpr)) {
                        it.remove();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String trimParamQuotes(String param){
        int firstChar = 1;
        int lastChar = param.length()-1;

        return param.substring(firstChar, lastChar);
    }

    // UPRAVIT ABYCH NEMEL V PARAMS VOLÁNÍ VNITRNICH METOD !!! ZÍTRA
    public List<Deque<String>> prepareCommand(List<Variable> vars,
                                              Deque<MethodCallExpr> driverMethodCallExprs){

        List<Deque<String>> preparedCommand = new ArrayList<>();
        List<Expression> paramExprs;
        Deque<String> methodNames = new ArrayDeque<>();
        Deque<String> params = new ArrayDeque<>();
        MethodCallExpr tmp;
        String currentMethodName;
        String tmpParam;
        int beginLine;

        while (!driverMethodCallExprs.isEmpty()){
            tmp = driverMethodCallExprs.pollLast();
            currentMethodName = tmp.getNameAsString();
            paramExprs = tmp.getArguments();
            beginLine = tmp.getBegin().get().line;
            methodNames.addLast(currentMethodName);

            if (paramExprs != null) {
                for (Expression paramExpr : paramExprs) {
                    tmpParam = trimParamQuotes(paramExpr.toString());
                    params.addLast(resolveParametrBinding(beginLine, tmpParam, vars));
                }
            }
            removeParam(tmp.toString(), driverMethodCallExprs);
        }

        preparedCommand.add(methodNames);
        preparedCommand.add(params);

        return preparedCommand;
    }

    public Deque<Command> parse(String testName){
        variables = initializeVariables(testName);
        List<Deque<MethodCallExpr>> driverMethodCallExprs = prepareDriverMethodCalls(testName, variables);
        Deque<Command> commands = new ArrayDeque<>();

        for (Deque<MethodCallExpr> driverMethodCallExpr : driverMethodCallExprs) {
            List<Deque<String>> preparedCommand = prepareCommand(variables, driverMethodCallExpr);
            commands.addLast(new Command(preparedCommand.get(0),
                    preparedCommand.get(1)));
        }

        return commands;
    }

    public static interface MethodCallCondition {
        boolean passedCondition(String mce, String val);
    }*/
}
