package telran.company.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import telran.company.dto.DepartmentAvgSalary;
import telran.company.dto.Employee;
import telran.company.dto.SalaryIntervalDistribution;
import telran.company.service.CompanyService;
import telran.company.service.CompanyServiceImpl;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //for definition of tests ordering
class CompanyTest {
private static final long ID1 = 123;
private static final long ID2 = 124;
private static final int SALARY1 = 5000;
private static final String DEPARTMENT1 = "dep1";
private static final LocalDate DATE1 = LocalDate.of(1970, 10, 23);
private static final int SALARY2 = 6000;
private static final LocalDate DATE2 = LocalDate.of(1975, 1, 1);
private static final long ID3 = 125;
private static final long ID4 = 126;
private static final long ID5 = 127;
private static final int SALARY3 = 7000;
private static final int SALARY4 = 8000;
private static final int SALARY5 = 9000;
private static final String DEPARTMENT2 = "dep2";
private static final String DEPARTMENT3 = "dep3";
private static final LocalDate DATE3 = LocalDate.of(1980, 5, 3);
private static final LocalDate DATE4 = LocalDate.of(1990, 5, 3);
private static final LocalDate DATE5 =LocalDate.of(2000, 5, 3);
private static final long ID6 = 1000;
private static final String DEPARTMENT6 = "Audit";
private static final String FILE_PATH = "test.data";
Employee empl1 = new Employee(ID1, "name1", SALARY1, DEPARTMENT1, DATE1);
Employee empl2 = new Employee(ID2, "name2", SALARY2, DEPARTMENT1, DATE2);
Employee empl3 = new Employee(ID3, "name3", SALARY3, DEPARTMENT2, DATE3);
Employee empl4 = new Employee(ID4, "name4", SALARY4, DEPARTMENT2, DATE4);
Employee empl5= new Employee(ID5, "name5", SALARY5, DEPARTMENT3, DATE5);
Employee[] employees = {empl1, empl2, empl3, empl4, empl5};
CompanyService company = null;
	@BeforeEach
	void setUp() throws Exception {
		company = new CompanyServiceImpl();
		for(Employee empl: employees) {
			company.hireEmployee(empl);
		}
	}

	@Test
	void testHireEmployeeNormal() {
		Employee newEmployee = new Employee(ID6, "name6", SALARY1, DEPARTMENT1, DATE1);
		assertEquals(newEmployee, company.hireEmployee(newEmployee));
	}
	@Test
	void testHireEmployeeException() {
		Employee newEmployee = empl1;
		//FIXME
		boolean flException = false;
		try {
			company.hireEmployee(newEmployee);
		} catch (IllegalStateException e) {
			flException = true;
		}
		assertTrue(flException);
	}

	@Test
	void testFireEmployeeNormal() {
		assertEquals(empl1, company.fireEmployee(ID1));
		assertEquals(empl1, company.hireEmployee(empl1));
	}
	@Test
	void testFireEmployeeException() {
		boolean flException = false;
		try {
			company.fireEmployee(ID6);
		} catch (IllegalStateException e) {
			flException = true;
		}
		assertTrue(flException);
	}

	@Test
	void testGetEmployee() {
		assertEquals(empl1, company.getEmployee(ID1));
		assertNull(company.getEmployee(ID6));
	}

	@Test
	void testGetEmployeesByDepartment() {
		Employee [] expectedDep1 = {empl1, empl2};
		Employee [] expectedDep2 = {empl3, empl4};
		List<Employee> list1 = company.getEmployeesByDepartment(DEPARTMENT1);
		List<Employee> list2 = company.getEmployeesByDepartment(DEPARTMENT2);
		
		assertTrue(company.getEmployeesByDepartment(DEPARTMENT6).isEmpty());
		runListTest(expectedDep1, list1);
		runListTest(expectedDep2, list2);
		
	}

	@Test
	void testGetAllEmployees() {
		runListTest(employees, company.getAllEmployees());
	}

	@Test
	void testGetEmployeesBySalary() {
		runListTest(employees, company.getEmployeesBySalary(0, Integer.MAX_VALUE)); //all employees
		runListTest(new Employee[] {}, company.getEmployeesBySalary(100000, Integer.MAX_VALUE)); //none employees
		runListTest(new Employee[] {empl1, empl2}, company.getEmployeesBySalary(SALARY1, SALARY3));
		
	}

	@Test
	void testGetEmployeeByAge() {
		runListTest(employees, company.getEmployeeByAge(0, 100));
		runListTest(new Employee[0], company.getEmployeeByAge(90, 100));
		runListTest(new Employee[] {empl2, empl3}, company.getEmployeeByAge(getAge(DATE3), getAge(DATE1)));
	}

	@Test
	void testSalaryDistributionByDepartments() {
		DepartmentAvgSalary[] expectedDistribution = {
			new DepartmentAvgSalary(DEPARTMENT1, (SALARY1 + SALARY2) / 2),	
			new DepartmentAvgSalary(DEPARTMENT2, (SALARY3 + SALARY4) / 2),
			new DepartmentAvgSalary(DEPARTMENT3, SALARY5)
		};
		List<DepartmentAvgSalary> listDistribution = company.salaryDistributionByDepartments();
		DepartmentAvgSalary[] actualDistribution =
				listDistribution.toArray(new DepartmentAvgSalary[] {});
		Arrays.sort(actualDistribution);
		assertArrayEquals(expectedDistribution, actualDistribution);
	}

	@Test
	void testGetSalaryDistribution() {
		int interval = 2000;
		List<SalaryIntervalDistribution> distribution = company.getSalaryDistribution(interval);
		SalaryIntervalDistribution[] expectedDistrubution = {
			new SalaryIntervalDistribution(SALARY1, SALARY1 + interval, 2)	,
			new SalaryIntervalDistribution(SALARY3, SALARY3 + interval, 2),
			new SalaryIntervalDistribution(SALARY5, SALARY5 + interval, 1)
		};
		assertArrayEquals(expectedDistrubution, distribution.toArray(new SalaryIntervalDistribution[0]));
		
	}

	@Test
	void testUpdateDepartment() {
		assertEquals(empl2, company.updateDepartment(ID2, DEPARTMENT2));
		runListTest(new Employee[] {empl1}, company.getEmployeesByDepartment(DEPARTMENT1));
		runListTest(new Employee[] {empl2, empl3, empl4}, company.getEmployeesByDepartment(DEPARTMENT2));
		
	}

	@Test
	void testUpdateSalary() {
		assertEquals(empl2, company.updateSalary(ID2, SALARY3));
		runListTest(new Employee[] {empl1}, company.getEmployeesBySalary(SALARY1, SALARY3));
		runListTest(new Employee[] {empl2, empl3, empl4}, company.getEmployeesBySalary(SALARY3, SALARY5));
	}

	@Test
	@Order(1)
	void testSave() {
		company.save(FILE_PATH); //saving company data in the file
	}

	@Test
	@Order(2)
	void testRestore() {
		CompanyService companySaved = new CompanyServiceImpl();
		companySaved.restore(FILE_PATH);
		runListTest(employees, companySaved.getAllEmployees());
	}
	private int getAge(LocalDate birthdate) {
		int result = (int) ChronoUnit.YEARS.between(birthdate, LocalDate.now());
		return result;
	}
	private void runListTest(Employee [] expected, List<Employee> list) {
		Employee[] actual = list.toArray(new Employee[] {});
		Arrays.sort(actual);
		assertArrayEquals(expected, actual);
	}

}
