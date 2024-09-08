import java.math.BigDecimal;
import java.math.RoundingMode;

public class BasicCalculator {
    /**
     * 累计计算值
     */
    protected BigDecimal preNum;

    /**
     * 新输入值
     */
    protected BigDecimal newNum;

    /**
     * 当前运算符
     */
    protected String operator;

    /**
     * 默认精度5位小数
     */
    protected int scale = 5;

    public BigDecimal getPreNum() {
        return preNum;
    }

    public void setPreNum(BigDecimal preNum) {
        this.preNum = preNum;
    }

    public BigDecimal getNewNum() {
        return newNum;
    }

    public void setNewNum(BigDecimal newNum) {
        if(preNum == null){ // 未计算过,累计总值为第一个输入值
            preNum = newNum;
        }else{
            this.newNum = newNum;
        }
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * 进行累计计算
     * @param preNum 累计计算值
     * @param operator 当前运算符
     * @param newNum 新输入值
     * @return 计算结果
     */
    public BigDecimal calculate(BigDecimal preNum, String operator, BigDecimal newNum) {
        BigDecimal ret = BigDecimal.ZERO;
        operator = operator == null ? "+" : operator;
        switch (operator){
            case "+":
                ret = preNum.add(newNum);
                break;
            case "-":
                if (BigDecimal.ZERO.equals(newNum)) {
                    throw new ArithmeticException("除数不能为零");
                }
                ret = preNum.subtract(newNum).setScale(scale, RoundingMode.HALF_UP);
                break;
            case "*":
                ret = preNum.multiply(newNum).setScale(scale, RoundingMode.HALF_UP);
                break;
            case "/":
                ret = preNum.divide(newNum, scale, RoundingMode.HALF_UP);
                break;
            default:
                throw new IllegalStateException("Unexpected operator: " + operator);
        }
        return ret;
    }

}
