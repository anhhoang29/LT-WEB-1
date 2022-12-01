package com.shoplane.controllers.client.order;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shoplane.dao.BillDAO;
import com.shoplane.models.Bill;
import com.shoplane.models.Order;
import com.shoplane.models.User;
import com.shoplane.utils.Helper;

@WebServlet(urlPatterns = { "/checkout", "/checkout/" })
public class CheckoutServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  BillDAO billDAO = null;

  public CheckoutServlet() {
    super();
    this.billDAO = new BillDAO();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      // forward url
      String url = "/default/checkouts/index.jsp";

      // Forward
      request.getRequestDispatcher(url).forward(request, response);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    @SuppressWarnings("unchecked")
    List<Order> orders = (List<Order>) request.getSession().getAttribute("orders");
    User user = (User) request.getSession().getAttribute("user");
    try {

      // Get data from form checkout
      String fullname = request.getParameter("fullname");
      String phonenumber = request.getParameter("phonenumber");
      String address = request.getParameter("address");
      String totalPriceStr = request.getParameter("totalPrice");

      // create Bill
      String billId = Helper.getRandom();
      int totalPrice = Integer.parseInt(totalPriceStr);
      Bill bill = new Bill(billId, new Date(), totalPrice, user);
      bill.setOrders(orders);
      // Set bill in each order
      for (Order order : orders) {
        order.setBill(bill);
      }

      // Insert Bill, list order to db
      Bill billCreated = this.billDAO.create(bill);
      if (billCreated != null) {
        request.getSession().removeAttribute("orders");
        request.getSession().removeAttribute("orderSize");
        String msg = "success";
        response.getWriter().append(msg);
      }
    } catch (Exception e) {
      String errorPage = "/500.jsp";
      request.getRequestDispatcher(errorPage).forward(request, response);
    }
  }

}
