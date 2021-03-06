import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
    private final List<Token> tokens;
    private int cur=0;
    static Stack<Integer> while_p=new Stack<Integer>();
    Parser(List<Token> tokens){
        this.tokens=tokens;
        //while_p.push(-1);
    }

    private Token current(){
        return tokens.get(cur);
    }

    private void suppose(TokenType type,int exit_code){
        if(current().tokenType==type){
        }else{
            System.exit(exit_code);
        }
    }

    private Token previous(){
        return tokens.get(cur-1);
    }

    private Token next(){
        return tokens.get(cur+1);
    }

    private void advance(){
        if(!isEnd()) cur++;
    }

    private boolean isEnd(){
        return current().tokenType==TokenType.EOF;
    }

    private boolean isMatch(TokenType... types){
        for(TokenType type : types){
            if(current().tokenType==type){
                return true;
            }
        }
        return false;
    }
    public void startP(String dest) throws IOException {
        Variable func=new Variable("_start",Type.VOID,0,1,1,1,0);
        SymTable table=new SymTable();
        SymTable init_table=table;
        table.addVar(func);
        SymTable.globalTable.add(func);
        SymTable.funcTable.add(func);
        while(!isMatch(TokenType.EOF)){
            decl_stmt(func,table);
        }
        func.instructions.add(new Instruction("callname",0x4a,table.getVarByName("main").no));
        System.out.println(SymTable.globalTable.size()+" "+SymTable.funcTable.size());
        for(Variable var:SymTable.globalTable){
            System.out.println();
            System.out.println(var.name);
        }

        for(Variable var:SymTable.funcTable){
            System.out.println();
            System.out.println(var.name);
            for(Instruction ins:var.instructions){
                if(ins.param_y!=-500){
                    System.out.println(ins.name+" "+ins.param_y);
                }else if(ins.param_x!=-500.0){
                    System.out.println(ins.name+" "+ins.param_x);
                }
                else{
                    System.out.println(ins.name);
                }
            }
        }

        Output o=new Output();
        o.run();
        byte[] res = new byte[o.output.size()];
        for (int i = 0; i < o.output.size(); i++) {
            res[i] = o.output.get(i);
        }
        DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(dest)));
        out.write(res);
    }

    private void decl_stmt(Variable func,SymTable table){
        if(current().tokenType==TokenType.LET_KW){
            let_stmt(func,table);
        }else if(current().tokenType==TokenType.CONST_KW){
            const_stmt(func,table);
        }else if(current().tokenType==TokenType.FN_KW){
           fn_stmt(func,table);
        }else {
            stmt(func,table);
        }
    }

    private void let_stmt(Variable func,SymTable table){
        System.out.println("let");
        advance();
        suppose(TokenType.IDENT,1);
        String name=(String) current().tokenValue;
        advance();
        suppose(TokenType.COLON,1);
        advance();
        suppose(TokenType.IDENT,1);
        Type type=null;
        if(current().tokenValue.equals("int")){
            type=Type.INT;
        }else if(current().tokenValue.equals("double")){
            type=Type.DOUBLE;
        }else{
            System.exit(1);
        }
        advance();
        if(table.ancestor==null){
            Variable tmp=new Variable(name,type,table.vars.size(),0,0,1,0);
            if(!table.addVar(tmp)){
                System.exit(301);
            }
            SymTable.globalTable.add(tmp);
            func.instructions.add(new Instruction("globa",0x0c,table.vars.size()-1));
        }else{
            if(!table.addVar(new Variable(name,type,func.loc_slots,0,0,0,0))){
                System.exit(301);
            }
            func.instructions.add(new Instruction("loca",0x0a,func.loc_slots++));
        }
        if(isMatch(TokenType.ASSIGN)){
            advance();
            if(type!=E_0(func,table).type){
                System.exit(333);
            }
            func.instructions.add(new Instruction("store64",0x17));
        }
        suppose(TokenType.SEMICOLON,1);
        advance();
    }

    private void const_stmt(Variable func,SymTable table){
        System.out.println("const");
        advance();
        suppose(TokenType.IDENT,202);
        String name=(String) current().tokenValue;
        advance();
        suppose(TokenType.COLON,202);
        advance();
        suppose(TokenType.IDENT,202);
        Type type=null;
        if(current().tokenValue.equals("int")){
            type=Type.INT;
        }else if(current().tokenValue.equals("double")){
            type=Type.DOUBLE;
        }else{
            System.exit(202);
        }
        advance();
        if(table.ancestor==null){
            Variable tmp=new Variable(name,type,table.vars.size(),1,0,1,0);
            if(!table.addVar(tmp)){
                System.exit(302);
            }
            SymTable.globalTable.add(tmp);
            func.instructions.add(new Instruction("globa",0x0c,table.vars.size()-1));
        }else{
            if(!table.addVar(new Variable(name,type,table.vars.size(),1,0,0,0))){
                System.exit(302);
            }
            func.instructions.add(new Instruction("loca",0x0a,func.loc_slots++));
        }
        suppose(TokenType.ASSIGN,202);
        advance();
        E_0(func,table);
        func.instructions.add(new Instruction("store64",0x17));
        suppose(TokenType.SEMICOLON,202);
        advance();
    }

    private void fn_stmt(Variable func,SymTable table){
        //Variable new_func=new Variable();
        System.out.println("fn");
        advance();
        suppose(TokenType.IDENT,2);
        String name=(String) current().tokenValue;
        advance();
        suppose(TokenType.L_PAREN,2);
        advance();
        List<Param> x=new ArrayList<>();
        SymTable new_table=new SymTable(table);
        while(!isMatch(TokenType.R_PAREN)){
            int isCon=0;
            if(isMatch(TokenType.CONST_KW)){
                isCon=1;
                advance();
            }
            suppose(TokenType.IDENT,2);
            String name_p=(String) current().tokenValue;
            advance();
            suppose(TokenType.COLON,2);
            advance();
            suppose(TokenType.IDENT,2);
            if(current().tokenValue.equals("int")){
                x.add(new Param(Type.INT,isCon));
                new_table.addVar(new Variable(name_p,Type.INT,new_table.vars.size(),isCon,0,0,1));
            }else if(current().tokenValue.equals("double")){
                x.add(new Param(Type.DOUBLE,isCon));
                new_table.addVar(new Variable(name_p,Type.DOUBLE,new_table.vars.size(),isCon,0,0,1));
            }else{
                System.exit(2);
            }
            advance();
            if(!isMatch(TokenType.R_PAREN)){
                suppose(TokenType.COMMA,2);
                advance();
            }
        }
        suppose(TokenType.R_PAREN,2);
        advance();
        suppose(TokenType.ARROW,2);
        advance();
        suppose(TokenType.IDENT,2);
        Variable new_func=null;
        if(current().tokenValue.equals("int")){
            new_func=new Variable(name,Type.INT,table.vars.size(),1,1,1,0);
            new_func.params=x;
            new_func.ret_slots=1;
        }else if(current().tokenValue.equals("double")){
            new_func=new Variable(name,Type.DOUBLE,table.vars.size(),1,1,1,0);
            new_func.params=x;
            new_func.ret_slots=1;
        }else if(current().tokenValue.equals("void")){
            new_func=new Variable(name,Type.VOID,table.vars.size(),1,1,1,0);
            new_func.params=x;
            new_func.ret_slots=0;
        } else{
            System.exit(2);
        }
        if(!table.addVar(new_func)){
            System.exit(303);
        }
        advance();
        new_func.param_slots=new_table.vars.size();

        new_func.loc_slots=0;

        suppose(TokenType.L_BRACE,2);
        block_stmt(new_func,new_table);
        SymTable.globalTable.add(new_func);
        SymTable.funcTable.add(new_func);
//        if(new_func.ret_slots==1){
//            new_func.instructions.add(new Instruction("store64",0x17));
//        }
        if(new_func.ret_slots==0)
            new_func.instructions.add(new Instruction("ret",0x49));
        else
            new_func.instructions.add(new Instruction("nop",0x00));
    }

    private void stmt(Variable func,SymTable table){
        if(current().tokenType==TokenType.IF_KW){
            if_stmt(func,table);
        }else if(current().tokenType==TokenType.WHILE_KW){
            while_stmt(func,table);
        }else if(current().tokenType==TokenType.RETURN_KW){
            return_stmt(func,table);
        }else if(current().tokenType==TokenType.L_BRACE){
            block_stmt(func,new SymTable(table));
        }else if(current().tokenType==TokenType.CONTINUE_KW){
            continue_stmt(func,table);
        }else if(current().tokenType==TokenType.BREAK_KW){
            break_stmt(func,table);
        }else if(current().tokenType==TokenType.SEMICOLON){
            empty_stmt(func,table);
        }else {
            //System.out.println("2333");
            expr_stmt(func,table);
        }
    }

    private void if_stmt(Variable func,SymTable table){
        System.out.println("if");
        advance();
        Expression tmp=E_0(func,table);
        func.instructions.add(new Instruction("brF",0x42,0));
        int id=func.instructions.size();
        suppose(TokenType.L_BRACE,204);
        block_stmt(func,new SymTable(table));
        if(isMatch(TokenType.ELSE_KW)){
            advance();
            func.instructions.add(new Instruction("br",0x41,0));
            int id_2=func.instructions.size();
            func.instructions.get(id-1).param_y=id_2-id;
            if(isMatch(TokenType.L_BRACE)){
                block_stmt(func,new SymTable(table));
            }else if(isMatch(TokenType.IF_KW)){
                if_stmt(func, table);
            }else{
                System.exit(6);
            }
            func.instructions.add(new Instruction("nop",0x00));
            int id_3=func.instructions.size();
            func.instructions.get(id_2-1).param_y=id_3-id_2;
        }else{
            func.instructions.add(new Instruction("nop",0x00));
            int id_4=func.instructions.size();
            func.instructions.get(id-1).param_y=id_4-id;
        }
    }

    private void while_stmt(Variable func,SymTable table){
        System.out.println("while");
        advance();
        func.instructions.add(new Instruction("nop",0x00));
        int start=func.instructions.size();
        while_p.push(start);
        Expression tmp=E_0(func,table);
        func.instructions.add(new Instruction("brT",0x43,1));
        func.instructions.add(new Instruction("br",0x41,0));
        int id=func.instructions.size();
        suppose(TokenType.L_BRACE,205);
        block_stmt(func,new SymTable(table));
        int end=func.instructions.size();
        func.instructions.add(new Instruction("br",0x41,start-end-2));
        func.instructions.add(new Instruction("nop",0x00));
        end++;
        func.instructions.get(id-1).param_y=end-id+1;
        for(int i=0;i<func.instructions.size();i++){
            if(func.instructions.get(i).id==0x41&&func.instructions.get(i).param_y==10000){
                func.instructions.get(i).param_y=end-i;
            }
        }
        while_p.pop();
    }

    private Type expr_stmt(Variable func,SymTable table){
        System.out.println("expr");
        if(isMatch(TokenType.SEMICOLON)){
            empty_stmt(func, table);
            return Type.VOID;
        }
        Type type=E_0(func,table).type;
        suppose(TokenType.SEMICOLON,5);
        advance();
        return type;
    }

    private void return_stmt(Variable func,SymTable table){
        System.out.println("return");
        advance();
        func.instructions.add(new Instruction("arga",0x0b,0));
        if(func.type!=expr_stmt(func,table)){
            System.exit(5);
        }
        if(func.ret_slots==1){
            func.instructions.add(new Instruction("store64",0x17));
        }
        func.instructions.add(new Instruction("ret",0x49));
    }

    private void block_stmt(Variable func,SymTable table){
        System.out.println("block");
        advance();
        while(!isMatch(TokenType.R_BRACE)){
            decl_stmt(func,table);
        }
        advance();
        //System.out.println("4444");
    }

    private void empty_stmt(Variable func,SymTable table){
        System.out.println("empty");
        suppose(TokenType.SEMICOLON,207);
        advance();
    }

    private void break_stmt(Variable func,SymTable table){
        System.out.println("break");
        advance();
        suppose(TokenType.SEMICOLON,208);
        advance();
        func.instructions.add(new Instruction("br",0x41,10000));
    }

    private void continue_stmt(Variable func,SymTable table){
        System.out.println("continue");
        advance();
        suppose(TokenType.SEMICOLON,209);
        advance();
        int x=func.instructions.size();
        if(while_p.empty())
            System.exit(-1);
        else
            func.instructions.add(new Instruction("br",0x41,while_p.peek()-x-1));
    }






    private Expression E_0(Variable func,SymTable table){
        if(current().tokenType==TokenType.IDENT&&next().tokenType==TokenType.ASSIGN){
            Variable tmp=table.getVarByName((String)current().tokenValue);
            if(tmp.isGlobal==0)
                if(tmp.isParam==0)
                    func.instructions.add(new Instruction("loca",0x0a,tmp.no));
                else
                    func.instructions.add(new Instruction("arga",0x0b,tmp.no+ func.ret_slots));
            else
                func.instructions.add(new Instruction("globa",0x0c,tmp.no));
            advance();
            advance();
            Expression expr=E_0(func,table);
            if(tmp.type==expr.type&&tmp.isFunc==0&&tmp.isCon==0){
                func.instructions.add(new Instruction("store64",0x17));
            }else{
                System.exit(2);
            }
            return new Expression.assign_expr();
        }
        else return E_1(func,table);
    }

    private Expression E_1(Variable func,SymTable table){
        Expression expr=E_2(func,table);
        while(isMatch(TokenType.LT,TokenType.GT,TokenType.LE,TokenType.GE,TokenType.EQ,TokenType.NEQ)){
            Token tmp=current();
            advance();
            Expression right=E_2(func,table);
            expr=new Expression.binary(expr,tmp,right);
            if(expr.type==Type.INT)
                func.instructions.add(new Instruction("cmpi",0x30));
            else if(expr.type==Type.DOUBLE)
                func.instructions.add(new Instruction("cmpf",0x32));
            else
                System.exit(2);
            if(tmp.tokenType==TokenType.GT){
                func.instructions.add(new Instruction("setgt",0x3a));
            }
            if(tmp.tokenType==TokenType.LT){
                func.instructions.add(new Instruction("setlt",0x39));
            }
            if(tmp.tokenType==TokenType.GE){
                func.instructions.add(new Instruction("setlt",0x39));
                func.instructions.add(new Instruction("not",0x2e));;
            }
            if(tmp.tokenType==TokenType.LE){
                func.instructions.add(new Instruction("setgt",0x3a));
                func.instructions.add(new Instruction("not",0x2e));
            }
            if(tmp.tokenType==TokenType.EQ){
                func.instructions.add(new Instruction("not",0x2e));
            }
            if(tmp.tokenType==TokenType.NEQ){

            }
        }
        return expr;
    }

    private Expression E_2(Variable func,SymTable table){
        Expression expr=E_3(func,table);
        while(isMatch(TokenType.MINUS,TokenType.PLUS)){
            Token tmp=current();
            advance();
            Expression right=E_3(func,table);
            //tem.out.println(right);
            expr=new Expression.binary(expr,tmp,right);
            //System.out.println(expr);
            //return expr;
            if(tmp.tokenType==TokenType.PLUS){
                System.out.println(expr.type.toString()+" "+right.type.toString());
                if(expr.type==Type.INT&&expr.type==right.type)
                    func.instructions.add(new Instruction("addi",0x20));
                else if(expr.type==Type.DOUBLE&&expr.type==right.type)
                    func.instructions.add(new Instruction("addf",0x24));
                else
                    System.exit(501);
            }
            if(tmp.tokenType==TokenType.MINUS){
                if(expr.type==Type.INT&&expr.type==right.type)
                    func.instructions.add(new Instruction("subi",0x21));
                else if(expr.type==Type.DOUBLE&&expr.type==right.type)
                    func.instructions.add(new Instruction("subf",0x25));
                else
                    System.exit(501);
            }
        }
        return expr;
    }

    private Expression E_3(Variable func,SymTable table){
        Expression expr=E_4(func,table);
        while(isMatch(TokenType.MUL,TokenType.DIV)){
            Token tmp=current();
            advance();
            Expression right=E_4(func,table);
            expr=new Expression.binary(expr,tmp,right);
            if(tmp.tokenType==TokenType.MUL){
                if(expr.type==Type.INT&&expr.type==right.type)
                    func.instructions.add(new Instruction("muli",0x22));
                else if(expr.type==Type.DOUBLE&&expr.type==right.type)
                    func.instructions.add(new Instruction("mulf",0x26));
                else
                    System.exit(502);
            }
            if(tmp.tokenType==TokenType.DIV){
                if(expr.type==Type.INT&&expr.type==right.type)
                    func.instructions.add(new Instruction("divi",0x23));
                else if(expr.type==Type.DOUBLE&&expr.type==right.type)
                    func.instructions.add(new Instruction("divf",0x27));
                else
                    System.exit(502);
            }
        }
        return expr;
    }

    private Expression E_4(Variable func,SymTable table){
        Expression expr=E_5(func,table);
        while(isMatch(TokenType.AS_KW)){
            advance();
            if(isMatch(TokenType.IDENT)&&(current().tokenValue.equals("int"))){
                advance();
                if(expr.type!=Type.STRING&&expr.type!=Type.VOID){
                    expr.setType(Type.INT);
                    func.instructions.add(new Instruction("ftoi",0x37));
                }
                else System.exit(108);
            }
            else if(isMatch(TokenType.IDENT)&&(current().tokenValue.equals("double"))){
                advance();
                if(expr.type!=Type.STRING&&expr.type!=Type.VOID){
                    expr.setType(Type.DOUBLE);
                    func.instructions.add(new Instruction("itof",0x36));
                }
                else System.exit(108);
            }
            else{
                System.exit(107);
            }
        }
        return expr;
    }

    private Expression E_5(Variable func,SymTable table){
        if(isMatch(TokenType.MINUS)){
            Token tmp=current();
            advance();
            Expression right=E_5(func,table);
            if(right.type==Type.INT){
                func.instructions.add(new Instruction("negi",0x34));
            }
            else if(right.type==Type.DOUBLE){
                func.instructions.add(new Instruction("negf",0x35));
            }else{
                System.exit(503);
            }
            return new Expression.unary(tmp,right);
        }
        return E_6(func,table);
    }

    private Expression E_6(Variable func,SymTable table){

        if(current().tokenType==TokenType.IDENT&&next().tokenType==TokenType.L_PAREN){
            Token tmp=current();
            advance();
            advance();
            if(tmp.tokenValue.equals("getint")){
                func.instructions.add(new Instruction("scani",0x50));
                suppose(TokenType.R_PAREN,778);
                advance();
                //suppose(TokenType.SEMICOLON,778);
                //advance();
                return new Expression.call_expr(Type.INT);
            }else if(tmp.tokenValue.equals("getchar")){
                func.instructions.add(new Instruction("scanc",0x51));
                suppose(TokenType.R_PAREN,778);
                advance();
                //suppose(TokenType.SEMICOLON,778);
                //advance();
                return new Expression.call_expr(Type.INT);
            }else if(tmp.tokenValue.equals("getdouble")){
                func.instructions.add(new Instruction("scanf",0x52));
                suppose(TokenType.R_PAREN,778);
                advance();
                //suppose(TokenType.SEMICOLON,778);
                //advance();
                return new Expression.call_expr(Type.DOUBLE);
            }else if(tmp.tokenValue.equals("putint")){
                if(E_1(func,table).type!=Type.INT){
                    System.exit(776);
                }
                func.instructions.add(new Instruction("printi",0x54));
                suppose(TokenType.R_PAREN,778);
                advance();
                //suppose(TokenType.SEMICOLON,778);
                //advance();
                return new Expression.call_expr(Type.VOID);
            }
            else if(tmp.tokenValue.equals("putchar")){
                if(E_1(func,table).type!=Type.INT){
                    System.exit(776);
                }
                func.instructions.add(new Instruction("printc",0x55));
                suppose(TokenType.R_PAREN,778);
                advance();
                //suppose(TokenType.SEMICOLON,778);
                //advance();
                return new Expression.call_expr(Type.VOID);
            }
            else if(tmp.tokenValue.equals("putdouble")){
                if(E_1(func,table).type!=Type.DOUBLE){
                    System.exit(776);
                }
                func.instructions.add(new Instruction("printf",0x56));
                suppose(TokenType.R_PAREN,778);
                advance();
                //suppose(TokenType.SEMICOLON,778);
                //advance();
                return new Expression.call_expr(Type.VOID);
            }
            else if(tmp.tokenValue.equals("putstr")){

                if(E_1(func,table).type!=Type.STRING){
                    System.exit(776);
                }
                func.instructions.add(new Instruction("push",0x01,SymTable.globalTable.size()-1));
                func.instructions.add(new Instruction("prints",0x57));
                suppose(TokenType.R_PAREN,778);
                advance();
                //suppose(TokenType.SEMICOLON,778);
                //advance();
                return new Expression.call_expr(Type.VOID);
            }
            else if(tmp.tokenValue.equals("putln")){
                func.instructions.add(new Instruction("println",0x58));
                suppose(TokenType.R_PAREN,778);
                advance();
                //suppose(TokenType.SEMICOLON,778);
                //advance();
                return new Expression.call_expr(Type.VOID);
            }
            else{
                Variable cur_func=table.getVarByName((String) tmp.tokenValue);
                if((cur_func==null||cur_func.isFunc==0)){
                    System.exit(777);
                }else{
                    func.instructions.add(new Instruction("stackalloc",0x1a,cur_func.ret_slots));
                    int i=0;
                    while(current().tokenType!=TokenType.R_PAREN){
                        Expression e=E_1(func,table);
                        if(i>=cur_func.params.size()|| e.type!=cur_func.params.get(i).type){
                            System.exit(666);
                        }else{
                            i++;
                        }
                        if(current().tokenType!=TokenType.R_PAREN){
                            suppose(TokenType.COMMA,667);
                            advance();
                        }
                    }
                    advance();
                    if(i==cur_func.params.size()){
                        func.instructions.add(new Instruction("callname",0x4a,cur_func.no));
                        if(cur_func.ret_slots==1){
                            //func.instructions.add(new Instruction("pop",0x02));
                        }
                        return new Expression.call_expr(cur_func.type);
                    }else{
                        System.exit(668);
                    }
                }
            }


        }
        return E_7(func,table);
    }

    private Expression E_7(Variable func,SymTable table){
        //System.out.println("111");
        if(isMatch(TokenType.IDENT,TokenType.STRING_LITERAL,TokenType.UINT_LITERAL,TokenType.CHAR_LITERAL,TokenType.DOUBLE_LITERAL)){
            if(current().tokenType==TokenType.IDENT){
                advance();
                return new Expression.ident(previous(),table,func);
            }
            advance();
            //System.out.println("111");
            return new Expression.literal(previous(),table,func);
        }
        if(isMatch(TokenType.L_PAREN)){
            advance();
            Expression expr=E_1(func,table);
            if(isMatch(TokenType.R_PAREN)){
                advance();
                return new Expression.group(expr);
            }else{
                System.exit(102);
            }
        }
        System.exit(103);
        return null;
    }
}
