package org.myorg.demo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestMain {

    public static void main(String[] args) throws ClassNotFoundException {
        User user = new User();
        user.setId(1);
        user.setUserName("xiao");
        String sql1 = getQuerySql(user);
        System.out.println(sql1);
    }

    private static String getQuerySql(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ");
        Class c = user.getClass();
        if (c.isAnnotationPresent(Table.class)) {
            // 获取表名
            Table table = (Table) c.getAnnotation(Table.class);
            String tableName = table.value();
            sb.append(tableName).append(" where 1=1 ");
            // 获取字段名
            Field[] fields = c.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(Column.class)) {
                    Column column = (Column) f.getAnnotation(Column.class);
                    // 得到注解：user_name 数据库中字段名
                    String columnName = column.name();
                    String columnValue = column.value();
                    // 得到成员变量名：userName
                    String filedName = f.getName();
                    // 构造get方法名形如：getUserName
                    String getMethodName = "get" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1);
                    Object filedValue = null;
                    try {
                        // 反射获取值
                        Method getMethod = c.getMethod(getMethodName);
                        filedValue = getMethod.invoke(user);
                        // 合成SQL，这里就不对每种数据类型做处理了
                        String conStr = String.format(" and %s = '%s'", columnName, filedValue);
                        if (f.getGenericType().toString().equals("class java.lang.String")) {
                            if (columnValue.toUpperCase().equals("LIKE")) {
                                conStr = String.format(" and %s LIKE '%s%s%s'", columnName, "%", filedValue, "%");
                            } else {
                                conStr = String.format(" and %s = '%s'", columnName, filedValue);
                            }
                        } else if (f.getGenericType().toString().equals("int")) {
                            conStr = String.format(" and %s = %s", columnName, filedValue);
                        }
                        //
                        sb.append(conStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // 返回构造的SQL字符串
        return sb.toString();
    }
}