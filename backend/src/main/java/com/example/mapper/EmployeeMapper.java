package com.example.mapper;

import com.example.entity.Employee;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface EmployeeMapper {

    int insert(Employee employee);

    void updateById(Employee employee);

    void deleteById(Integer id);

    Employee selectById(Integer id);

    Employee selectByUsername(String username);

    List<Employee> selectAll(Employee employee);

    List<Employee> selectByDepartmentIds(@Param("departmentIds") List<Integer> departmentIds);

    int checkEmployeeCompany(@Param("employeeId") Integer employeeId, @Param("companyId") Integer companyId);

    @Select("SELECT COUNT(*) FROM employee e JOIN department d ON e.department_id = d.id WHERE d.company_id = #{companyId}")
    int countByCompanyId(@Param("companyId") Integer companyId);

    @Select("SELECT COUNT(*) FROM employee e JOIN department d ON e.department_id = d.id WHERE d.company_id = #{companyId} AND e.role = 'DOCTOR'")
    int countDoctorsByCompanyId(@Param("companyId") Integer companyId);

    @Select("SELECT company_id FROM department WHERE id = #{departmentId}")
    Integer getCompanyIdByDepartmentId(Integer departmentId);

    @Select("SELECT d.id, d.name, d.company_id, e.id as emp_id, e.name as emp_name " +
            "FROM department d " +
            "JOIN employee e ON e.department_id = d.id " +
            "WHERE e.id = #{employeeId}")
    Map<String, Object> getEmployeeDepartmentInfo(Integer employeeId);

    /**
     * 获取员工所属公司ID
     * @param employeeId 员工ID
     * @return 公司ID
     */
    @Select("SELECT d.company_id FROM employee e " +
            "LEFT JOIN department d ON e.department_id = d.id " +
            "WHERE e.id = #{employeeId}")
    Integer getEmployeeCompanyId(Integer employeeId);

    /**
     * 根据姓名查询用户
     * @param name 用户姓名
     * @return 用户列表
     */
    @Select("SELECT * FROM employee WHERE name = #{name}")
    List<Employee> selectByName(String name);

}
