package com.shoplane.services.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shoplane.dao.CategoryDAO;
import com.shoplane.dao.ProductDAO;
import com.shoplane.dao.ProductTypeDAO;
import com.shoplane.models.Category;
import com.shoplane.models.Product;
import com.shoplane.models.ProductType;
import com.shoplane.services.SuperService;
import com.shoplane.utils.Constants;
import com.shoplane.utils.Helper;

public class ProductService extends SuperService {
  ProductDAO productDAO = null;
  ProductTypeDAO productTypeDAO = null;
  CategoryDAO categoryDAO = null;

  public ProductService(HttpServletRequest request, HttpServletResponse response) {
    super(request, response);
    this.productDAO = new ProductDAO();
    this.productTypeDAO = new ProductTypeDAO();
    this.categoryDAO = new CategoryDAO();
  }

  // [GET] ListProductServlet => Client side
  public void getCollectionProduct() throws IOException {
    try {
      // Set encoding
      super.setEncoding(Constants.UTF8);

      // Link
      String url = "/pages/default/collections/index.jsp";
      // Get params
      String productTypeId = super.getParameter("product_type");
      String categoryId = super.getParameter("category_id");
      String currentPageStr = super.getParameter("current_page");
      String pageSizeStr = super.getParameter("page_size");
      String sortBy = super.getParameter("sort_by_price");

      int currentPage = 1;
      int pageSize = 10;
      byte isDelete = 0;

      if (currentPageStr != null && pageSizeStr != null) {
        if (Helper.isNumeric(currentPageStr)) {
          currentPage = Integer.parseInt(currentPageStr);
        }

        if (Helper.isNumeric(pageSizeStr)) {
          pageSize = Integer.parseInt(pageSizeStr);
        }
      }
      if (sortBy == null)
        sortBy = Constants.DESC;

      List<ProductType> productTypes = this.productTypeDAO.findAll();
      ProductType productType = this.productTypeDAO.find(productTypeId);

      List<Category> categories = this.categoryDAO.findByProductType(productType);
      Category category = this.categoryDAO.find(categoryId);
      List<Product> products = new ArrayList<>();
      int totalItem = 0;

      Map<String, Object> params = new HashMap<>();
      params.put("isDelete", isDelete);
      params.put("productType", productType);

      if (categoryId.equals(Constants.SHIRT_ALL) && productTypeId.equals(Constants.SHIRT)) {
        products = this.productDAO.paginationByProductTypeAndIsDeleted(params, currentPage, pageSize, sortBy);
        totalItem = this.productDAO.countByProductTypeAndIsDeleted(params);
      }
      if (categoryId.equals(Constants.SHORT_ALL) && productTypeId.equals(Constants.SHORT)) {
        products = this.productDAO.paginationByProductTypeAndIsDeleted(params, currentPage, pageSize, sortBy);
        totalItem = this.productDAO.countByProductTypeAndIsDeleted(params);
      }
      if (!categoryId.equals(Constants.SHIRT_ALL) && !categoryId.equals(Constants.SHORT_ALL)) {
        params.put("category", category);
        products = this.productDAO.paginationByCategoryAndProductTypeAndIsDeleted(params, currentPage, pageSize,
            sortBy);
        totalItem = this.productDAO.countByProductTypeAndCategoryAndIsDeleted(params);
      }

      // Chưa ổn lắm => Cần phải fix chỉnh lại chỗ này
      int totalPage = (int) Math.ceil((double) totalItem / pageSize);

      super.setAttribute("products", products);
      super.setAttribute("productTypes", productTypes);
      super.setAttribute("categories", categories);
      super.setAttribute("category", category);
      // Set value of param url
      super.setAttribute("totalPage", totalPage);
      super.setAttribute("currentPage", currentPage);
      super.setAttribute("pageSize", pageSize);
      super.setAttribute("productType", productTypeId);
      super.setAttribute("categoryId", categoryId);

      // Forward
      super.forwardToPage(url);

    } catch (Exception e) {
      super.log(e.getMessage());
      String error = super.getContextPath() + "/500";
      super.redirectToPage(error);
    }
  }
}
