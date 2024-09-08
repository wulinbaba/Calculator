import java.math.BigDecimal;
import java.util.Stack;

public class Calculator extends BasicCalculator{
    private Stack<BigDecimal> undoStack = new Stack<>(); // 撤销栈
    private Stack<BigDecimal> redoStack = new Stack<>(); // 重做栈

    /**
     * 撤销操作，回撤到上一个计算结果
     */
    public void undo(){
        if(undoStack.isEmpty()){
            System.out.println("无法再undo!");
        } else {
            BigDecimal lastNum = undoStack.pop();
            redoStack.push(preNum);
            preNum = lastNum;
            System.out.println("undo撤销操作后的值：" + preNum);
        }
    }

    /**
     * 重做操作，根据撤销进行重做上一个操作
     */
    public void redo(){
        if(redoStack.isEmpty()){
            System.out.println("无法再redo!");
        } else {
            BigDecimal nextNum = redoStack.pop();
            undoStack.push(nextNum);
            preNum = nextNum;
            System.out.println("redo重做操作后的值：" + preNum);
        }
    }

    /**
     * 计算操作，等同于按下计算器的等于按钮
     */
    public void calc(){
        preNum = getPreNum() == null ? BigDecimal.ZERO : getPreNum();
        undoStack.push(preNum);
        if(operator == null){
            System.out.println("请输入运算符!");
        }
        if(newNum != null){ // 已输入新值
            // 累加计算，调用基类计算方法
            BigDecimal ret = calculate(preNum, operator, newNum);
            preNum = ret;
            operator = null;
            newNum = null;
            redoStack.clear(); // 清空重做栈
        }
        display(); // 显示操作结果
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
