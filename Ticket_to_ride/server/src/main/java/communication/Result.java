package communication;

/**
 * Created by jalton on 10/1/18.
 */

public class Result {
    private boolean success;
    private Object data;
    private String errorInfo;

    public Result(boolean success, Object data, String errorInfo) {
        this.success = success;
        this.data = data;
        this.errorInfo = errorInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorInfo() {
        return "" + errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String toString() {
        return "{ " + success + ", " + data + ", " + errorInfo + "}";
    }
}
