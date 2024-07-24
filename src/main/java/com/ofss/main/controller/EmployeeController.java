package com.ofss.main.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlRootElement;

// Employee class (same as above)

@XmlRootElement
public class EmployeeController extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/xml");

		// Create an Employee object
		Employee employee = new Employee(1, "John Doe", "Engineering", 50000.0);
		Employee employee2 = new Employee(2, "John Doe", "Engineering", 50000.0);
		
		List<Employee> allEmployees = new ArrayList();
		allEmployees.add(employee2);
		allEmployees.add(employee);
		

		try {
			// Convert Employee object to XML
			JAXBContext jaxbContext = JAXBContext.newInstance(Employee.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write XML to response
			marshaller.marshal(allEmployees, response.getWriter());

		} catch (Exception e) {
			throw new ServletException("Error converting Employee to XML", e);
		}
	}
}
