import com.databaseprovider.controller.DbController;
import com.databaseprovider.controller.DeptController;

public class Test {
    public static void main(String[] args) {
        DeptController deptController=new DeptController();
        System.out.println(deptController.selectList());
    }
}
