package com.ofss.main.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 * Servlet implementation class EmployeeServlet
 */
public class EmployeeServlet extends HttpServlet {
	private List<Employee> employees = new ArrayList<>();
    private int nextId = 1;

    @Override
    public void init() throws ServletException {
        // Initialize some sample data
        employees.add(new Employee(nextId++, "John Doe", "Engineering", 50000.0));
        employees.add(new Employee(nextId++, "Jane Smith", "HR", 60000.0));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            // Retrieve single employee by ID
            int id = Integer.parseInt(idParam);
            Employee employee = findEmployeeById(id);
            
            if (employee != null) {
                // Convert Employee to XML
                try {
                    JAXBContext jaxbContext = JAXBContext.newInstance(Employee.class);
                    Marshaller marshaller = jaxbContext.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                    // Write XML to response
                    marshaller.marshal(employee, response.getWriter());
                } catch (JAXBException e) {
                    throw new ServletException("Error converting Employee to XML", e);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            // Retrieve all employees
            response.setContentType("text/xml");
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Employee.class, Employees.class);
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                // Write XML list to response
                marshaller.marshal(employees, response.getWriter());
            } catch (JAXBException e) {
                throw new ServletException("Error converting Employees list to XML", e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Read XML data from request body
        Employee newEmployee;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Employee.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            newEmployee = (Employee) unmarshaller.unmarshal(request.getInputStream());
        } catch (JAXBException e) {
            throw new ServletException("Error reading XML data", e);
        }

        // Add new employee to list
        newEmployee.setId(nextId++);
        employees.add(newEmployee);

        // Send response
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Read XML data from request body
        Employee updatedEmployee;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Employee.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            updatedEmployee = (Employee) unmarshaller.unmarshal(request.getInputStream());
        } catch (JAXBException e) {
            throw new ServletException("Error reading XML data", e);
        }

        // Update existing employee in list
        Employee existingEmployee = findEmployeeById(updatedEmployee.getId());
        if (existingEmployee != null) {
            existingEmployee.setName(updatedEmployee.getName());
            existingEmployee.setDepartment(updatedEmployee.getDepartment());
            existingEmployee.setSalary(updatedEmployee.getSalary());
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            int id = Integer.parseInt(idParam);
            Employee employeeToRemove = findEmployeeById(id);
            if (employeeToRemove != null) {
                employees.remove(employeeToRemove);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private Employee findEmployeeById(int id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }


}
