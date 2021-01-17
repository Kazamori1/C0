import java.util.ArrayList;
import java.util.List;

public class Output {
    List<Byte> output=new ArrayList<>();

    List<Byte> run(){
        output.addAll(to_bytes(0x72303b3e,4));
        output.addAll(to_bytes(0x00000001,4));
        output.addAll(to_bytes(SymTable.globalTable.size(),4));
        for(Variable variable:SymTable.globalTable){
            if(variable.isCon==1){
                output.addAll(to_bytes(1,1));
            }else{
                output.addAll(to_bytes(0,1));
            }
            if(variable.isFunc==1||variable.type==Type.STRING) {
                output.addAll(to_bytes(variable.name.length(), 4));
                output.addAll(string_to_bytes(variable.name));
            }else{
                output.addAll(to_bytes(8,4));
                output.addAll(to_bytes(0,8));
            }
        }
        output.addAll(to_bytes(SymTable.funcTable.size(),4));
        for(Variable variable:SymTable.funcTable){
            int num=0;
            for(Variable var:SymTable.globalTable){
                if(var.name.equals(variable.name)){
                    num=var.no;
                    break;
                }
            }
            //ystem.out.println(num+" "+variable.instructions.size());
            //variable.loc_slots+=variable.param_slots;
            output.addAll(to_bytes(num,4));
            output.addAll(to_bytes(variable.ret_slots,4));
            output.addAll(to_bytes(variable.param_slots,4));
            output.addAll(to_bytes(variable.loc_slots,4));
            output.addAll(to_bytes(variable.instructions.size(),4));
            System.out.println(variable.name+" "+variable.ret_slots+" "+variable.param_slots+" "+variable.loc_slots);
            for(Instruction instruction:variable.instructions){
                if(instruction.id==0x01){
                    output.addAll(to_bytes(instruction.id,1));
                    if(instruction.param_y!=-500)
                        output.addAll(to_bytes(instruction.param_y,8));
                    else
                        output.addAll(double_to_bytes(instruction.param_x));
                }else if(instruction.param_y!=-500){
                    output.addAll(to_bytes(instruction.id,1));
                    output.addAll(to_bytes(instruction.param_y,4));
                }else{
                    output.addAll(to_bytes(instruction.id,1));
                }
            }
        }
        return this.output;
    }

    static List<Byte> to_bytes(long src,int len){
        List<Byte> tmp=new ArrayList<>();
        int s=8*(len-1);
        for(int i=0;i<len;i++){
            tmp.add((byte) (( src >> ( s - i * 8 )) & 0xFF ));
        }
        return tmp;
    }
    static List<Byte> double_to_bytes(double src) {
        long value = Double.doubleToRawLongBits(src);
        List<Byte> tmp = new ArrayList<>();
        int start = 8 * (8-1);
        for (int i = 0; i < 8; i++) {
            tmp.add((byte) (( value >> ( start - i * 8 )) & 0xFF ));
        }
        return tmp;
    }
    static List<Byte> string_to_bytes(String src) {
        List<Byte> tmp = new ArrayList<>();
        for (int i=0;i<src.length();i++){
            char c=src.charAt(i);
            tmp.add((byte)(c&0xff));
        }
        return tmp ;
    }
}

