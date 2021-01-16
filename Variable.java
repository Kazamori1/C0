import java.util.ArrayList;
import java.util.List;

public class Variable {
    static int funcNum=1;
    Type type;
    String name;
    int val;
    int no;
    int isCon;
    int isFunc;
    int isGlobal;
    int ret_slots;
    int param_slots;
    int loc_slots;
    int body_count;
    List<Instruction> instructions;
    List<Param> params;
    Variable(String name,Type type,int no,int isCon,int isFunc,int isGlobal){
        this.name=name;
        this.type=type;
        this.no=no;
        this.isCon=isCon;
        this.isFunc=isFunc;
        this.isGlobal=isGlobal;
        this.val=0;
        this.ret_slots=0;
        this.loc_slots=0;
        this.param_slots=0;
        this.body_count=0;
        if(isFunc==1){
            instructions=new ArrayList<>();
            params=new ArrayList<>();
        }else{
            instructions=null;
            params=null;
        }
    }

}

abstract class Expression{
    /*
    Expr -> E1 | IDENT=E1 ????
    E1 -> E2 ( ( "!=" | "==" | "<=" | ">=" | "<" | ">" ) E2 )*
    E2 -> E3 ( ( "+" | "-" ) E3 )*
    E3 -> E4 ( ( "*" | "/" ) E4 )*
    E4 -> E5 | E5 as type
    E5 -> E6 | - E5
    E6 -> E7 | IDENT (E1.list)
    E7 -> INT | DOUBLE | IDENT | ( E1 )
     */
    Type type;

    public void setType(Type type) {
        this.type = type;
    }

    static  class literal extends Expression{
        Token val;
        literal(Token val,SymTable table,Variable func){
            this.val=val;
            if(val.tokenType==TokenType.STRING_LITERAL){
                this.type=Type.STRING;
                SymTable.globalTable.add(new Variable((String) val.tokenValue,Type.STRING,SymTable.globalTable.size(),1,0,1));
            }else if(val.tokenType==TokenType.DOUBLE_LITERAL){
                this.type=Type.DOUBLE;
                func.instructions.add(new Instruction("push",0x01,Double.doubleToRawLongBits((Double) val.tokenValue)));
            }else {
                this.type=Type.INT;
                func.instructions.add(new Instruction("push",0x01,Long.parseLong(val.tokenValue.toString())));
            }
        }

        @Override
        public String toString() {
            return "("+this.val.tokenType+this.val.tokenValue+")";
        }
    }

    static class ident extends Expression{
        Variable x=null;
        ident(Token a,SymTable table,Variable func){
            this.x=table.getVarByName((String) a.tokenValue);
            if(x==null){
                System.exit(177);
            }else if(x.isFunc==1){
                System.exit(888);
            }
            else{
                if(x.isGlobal==1){
                    func.instructions.add(new Instruction("globa",0x0c,x.no));
                }else{
                    func.instructions.add(new Instruction("loca",0x0a,x.no));
                }
                func.instructions.add(new Instruction("load64",0x13));
                this.type=x.type;
            }
        }


    }

    static class group extends Expression{
        Expression self;
        group(Expression self){
            this.self=self;
            this.type=self.type;
        }
        public String toString() {
            return "("+this.type+"|G|"+self+")";
        }
    }

    static class unary extends Expression{
        Token operator;
        Expression r;
        unary(Token operator,Expression r){
            this.operator=operator;
            this.r=r;
            this.type=r.type;
        }
        public String toString() {
            return "("+this.type+"|U| "+r+")";
        }
    }

    static class binary extends Expression{
        Expression l;
        Token operator;
        Expression r;
        binary(Expression l,Token operator,Expression r){
            if(l.type!=r.type){
                System.exit(109);
            }
            this.type=r.type;
            this.l=l;
            this.operator=operator;
            this.r=r;
        }
        public String toString() {
            return "("+this.type+"|B| "+l+operator+r+")";
        }
    }

    static class call_expr extends Expression{
        call_expr(Type type){
            this.type=type;
        }
    }


    static class assign_expr extends  Expression{
        assign_expr(){
            this.type=Type.VOID;
        }
    }

}
class Param{
    Type type;
    int isCon;
    Param(Type type,int isCon){
        this.type=type;
        this.isCon=isCon;
    }
}
enum Type{
    VOID,INT,DOUBLE,STRING,BOOLEAN
}