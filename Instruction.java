public class Instruction {
    int id;
    String name;
    double param_x;
    long param_y;
    String param_tmp;
    Instruction(String name,int id){
        this.name=name;
        this.id=id;
        this.param_y=-500;
        this.param_x=-500.0;
    }
    Instruction(String name,int id,double param_x){
        this.name=name;
        this.id=id;
        this.param_x=param_x;
        this.param_y=-500;
    }
//    Instruction(int id,int param_x){
//        this.id=id;
//        this.param_x=param_x;
//    }
    Instruction(String name,int id,long param_y){
        this.name=name;
        this.id=id;
        this.param_y=param_y;
    }
    Instruction(int id,String tmp){
        this.id=id;
        this.param_tmp=tmp;
    }
}
