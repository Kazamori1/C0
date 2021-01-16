import java.util.*;

public class SymTable {
    Map<String,Variable> vars=new TreeMap<>();
    SymTable ancestor;
    static List<Variable> globalTable=new ArrayList<>();
    static List<Variable> funcTable=new ArrayList<>();
    SymTable(){
        //for global
    }
    SymTable(SymTable ancestor){
        this.ancestor=ancestor;
    }
    Variable getVarByName(String name){
        if(this.vars.get(name)!=null){
            return this.vars.get(name);
        }else if(this.ancestor!=null){
            return this.ancestor.getVarByName(name);
        }else{
            return null;
        }
     }
    boolean isDeclared(String name){
        return this.vars.get(name)!=null;
    }
    boolean addVar(Variable var){
        if(isDeclared(var.name)){
            System.out.println("false");
            return false;
        }else{
            vars.put(var.name,var);
            return true;
        }
    }
}
