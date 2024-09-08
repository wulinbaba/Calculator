import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Calculator extends BasicCalculator{

    private List<BigDecimal> lastNumList = new ArrayList<>(); // 保存最近系列操作值
    private List<String> lastOptList = new ArrayList<>(); // 保存最近系列运算符
    private List<BigDecimal> lastTotalList = new ArrayList<>(); // 保存最近系列结果值
    private int lastOptIndex = -1; // undo/redo最近操作索引
    private int validIndexMax = -1; // undo/redo有效索引最大值

    /**
     * 计算操作，等同于按下计算器的等于按钮
     */
    public void calc(){
        preNum = getPreNum() == null ? BigDecimal.ZERO : getPreNum();
        if(operator == null){
            System.out.println("请输入运算符!");
        }
        if(newNum != null){ // 已输入新值
            // 累加计算，调用基类计算方法
            BigDecimal ret = calculate(preNum, operator, newNum);
            if(this.lastOptIndex == -1){ // 未执行redo/undo处于中间过程
                lastTotalList.add(preNum);
                lastNumList.add(newNum);
                lastOptList.add(operator);
            }else{ // 执行过redo/undo操作,覆盖undo/redo操作记录,并记录有效索引最大值
                this.lastOptIndex++;
                this.validIndexMax = this.lastOptIndex;
                this.lastTotalList.set(this.lastOptIndex, ret);
                this.lastNumList.set(this.lastOptIndex-1, newNum);
                this.lastOptList.set(this.lastOptIndex-1, operator);
            }
            preNum = ret;
            operator = null;
            newNum = null;
        }
        display(); // 显示操作结果
    }

    /**
     * 撤销操作，回撤到上一个计算结果
     */
    public void undo(){
        if(preNum != null && lastOptIndex == -1){ // 未进行undo/redo操作,存储最后计算结果
            lastTotalList.add(preNum);
            operator = null;
            newNum = null;
        }

        if(lastTotalList.size() == 0){
            System.out.println("无操作，无法继续撤销!");
        }else if(lastTotalList.size() == 1){
            System.out.println("undo后值:0,"+"undo前值:"+preNum);
            preNum = BigDecimal.ZERO;
        } else {
            if(lastOptIndex == -1){
                lastOptIndex = lastOptList.size()-1;
            }else{
                if(lastOptIndex-1 < 0){
                    System.out.println("无法再undo!");
                    return;
                }
                lastOptIndex--;
            }
            undoOperate(lastTotalList.get(lastOptIndex),lastOptList.get(lastOptIndex), lastNumList.get(lastOptIndex));
        }
        display(); // 显示操作结果
    }

    /**
     * 重做操作，根据撤销进行重做上一个操作
     */
    public void redo(){
        try{
            if(lastOptIndex > -1){
                if(lastOptIndex + 1 == lastTotalList.size() || lastOptIndex+1 == this.validIndexMax+1){
                    System.out.println("无法再redo!");
                    return;
                }
                lastOptIndex++;

                redoOperate(lastTotalList.get(lastOptIndex),lastOptList.get(lastOptIndex-1), lastNumList.get(lastOptIndex-1));
            }
            display(); // 显示操作结果
        }catch (Exception e){
            System.out.println("redo异常,lastOptIndex:"+lastOptIndex);
        }
    }

    private void undoOperate(BigDecimal lastTotal, String lastOpt, BigDecimal lastNum) {
        System.out.println("undo后值:"+lastTotal+",undo前值:"+preNum+",undo的操作:"+lastOpt+",undo操作的值:"+lastNum);
        preNum = lastTotal;
        operator = null;
        newNum = null;
    }

    private void redoOperate(BigDecimal redoTotal, String redoOpt, BigDecimal redoNum) {
        System.out.println("redo后值:"+redoTotal+",redo前值:"+preNum+",redo的操作:"+redoOpt+",redo操作的值:"+redoNum);
        preNum = redoTotal;
        operator = null;
        newNum = null;
    }

    /**
     * 显示操作结果
     */
    public String display(){
        StringBuilder sb = new StringBuilder();
        if(preNum != null){
            sb.append(preNum.setScale(scale, BigDecimal.ROUND_HALF_DOWN).toString());
        }
        if(operator != null){
            sb.append(operator);
        }
        if(newNum != null){
            sb.append(newNum);
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        calculator.setNewNum(new BigDecimal(3));
        calculator.setOperator("+");
        calculator.setNewNum(new BigDecimal(5));
        calculator.display();
        calculator.calc();  // 执行3+5计算
        calculator.setOperator("/");
        calculator.setNewNum(new BigDecimal(3));
        calculator.display();
        calculator.calc();  // 执行/3计算

        calculator.undo();  // 执行undo操作，回到执行/3之前的节点
        System.out.println("开始执行undo操作并附加额外计算:+2");
        calculator.setOperator("+");
        calculator.setNewNum(new BigDecimal(2));
        calculator.display();
        calculator.calc();  // 执行+2计算
        System.out.println("打断计算结束,重新进行undo/redo操作!");

        calculator.undo();  // 执行undo操作
        calculator.undo();  // 执行undo操作
        calculator.undo();  // 执行undo操作，触发无法undo结果
        calculator.redo();  // 执行redo操作
        calculator.redo();  // 执行redo操作
        calculator.redo();  // 执行redo操作，触发无法redo结果
    }

}
