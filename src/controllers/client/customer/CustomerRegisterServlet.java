package controllers.client.customer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.client.CustomerService;

@WebServlet("/register")
public class CustomerRegisterServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  public CustomerRegisterServlet() {
    super();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    CustomerService customerService = new CustomerService(request, response);
    customerService.handleGetRegister();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    CustomerService customerService = new CustomerService(request, response);
    customerService.handlePostRegister();
  }

}
