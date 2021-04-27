import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
class Student implements Cloneable {
    // 对象引用
    private Student subj;
    private String name;

    public Student(String name){
        this.name = name;
    }

    /**
     * 浅拷贝
     * @return
     */
    public Object shallowCopy() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * 深拷贝
     * @return
     */
    public Student deepCopy() {
        return new Student(new Student(subj.getName()), name);
    }
}

public class TestCopy {
    public static void main(String[] args) {
        // 原始对象
        Student stud = new Student(new Student("Java"), "BigKang");
        // 浅拷贝对象
        Student shallowCopy = (Student) stud.shallowCopy();
        // 深拷贝对象
        Student deepCopy = stud.deepCopy();

        System.out.println("原始对象: " + stud.getName() + " - " + stud.getSubj());
        System.out.println("浅拷贝对象: " + shallowCopy.getName() + " - " + shallowCopy.getSubj());
        System.out.println("深拷贝对象: " + deepCopy.getName() + " - " + deepCopy.getSubj());

        // 修改值
        stud.getSubj().setName("Python");
        System.out.println("-----------修改后");
        System.out.println("原始对象: " + stud.getName() + " - " + stud.getSubj());
        System.out.println("浅拷贝对象: " + shallowCopy.getName() + " - " + shallowCopy.getSubj());
        System.out.println("深拷贝对象: " + deepCopy.getName() + " - " + deepCopy.getSubj());
    }
}