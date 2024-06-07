package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.Dao;
import model.Account;
import model.Cart;
import model.Email;
import model.EmailUtils;
import model.Product;
import model.SoLuongDaBan;
import model.TongChiTieuBanHang;

/**
 * Servlet implementation class OrderControl
 */
@WebServlet(name = "OrderControl", urlPatterns = {"/order"})
public class OrderControl extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrderControl() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		Account acc = (Account) session.getAttribute("acc");
		if(acc == null) {
			response.sendRedirect("login");
			return;
		}
		
		int accountID = acc.getId();
		Dao dao = new Dao();
		List<Cart> listCart = dao.getCartByAccountID(accountID);
		List<Product> listPro = dao.getAllProduct();
		// lay ra tong tien va san pham cua acc
		double totalMoney = 0;
		for(Cart c : listCart) {
			for(Product p : listPro) {
				if(c.getProductID()==p.getId()) {
					totalMoney=totalMoney+(p.getPrice()*c.getAmount());
				}
			}
		}
		// lay ra tong tien ban hang them 
//		double tongTienBanHangThem = 0;
//		int sell_ID;
//		for (Cart c : listCart) {
//			for (Product p : listPro) {
//				if(c.getProductID() == p.getId()) {
//					tongTienBanHangThem = 0;
//					sell_ID = dao.getSellIDByProductID(p.getId());
//					tongTienBanHangThem = tongTienBanHangThem + (p.getPrice() * c.getAmount());
//					TongChiTieuBanHang t2 = dao.checkTongChiTieuBanHangExist(sell_ID);
//					if(t2 == null) {
//						dao.insertTongChiTieuBanHang(sell_ID, 0, tongTienBanHangThem);
//					}else {
//						dao.editTongBanHang(sell_ID, tongTienBanHangThem);
//					}
//				}
//			}
//		}
//		
//		for (Cart c : listCart) {
//			for (Product p : listPro) {
//				if(c.getProductID() == p.getId()) {
//					SoLuongDaBan s = dao.checkSoLuongDaBanExist(p.getId());
//					if(s == null) {
//						dao.insertSoLuongDaBan(p.getId(), c.getAmount());
//						
//					}else {
//						dao.editSoLuongDaBan(p.getId(), c.getAmount());
//					}
//				}
//			}
//		}
//		
		dao.insertInvoice(accountID, totalMoney);
//		TongChiTieuBanHang t = dao.checkTongChiTieuBanHangExist(accountID);
//		if(t == null) {
//			dao.insertTongChiTieuBanHang(accountID, totalMoney, 0);
//		}else {
//			dao.editTongChiTieu(accountID, totalMoney);
//		}
		request.getRequestDispatcher("datHang.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
		String name = request.getParameter("name");
		String sdt = request.getParameter("sdt");
		String email = request.getParameter("email");
		String address = request.getParameter("address");
		String content = request.getParameter("content");
		
		HttpSession session = request.getSession();
		Account acc = (Account) session.getAttribute("acc");
		if(acc == null) {
			response.sendRedirect("login");
			return;
		}
		int accountID = acc.getId();
		Dao dao = new Dao();
		List<Cart> list = dao.getCartByAccountID(accountID);
        List<Product> list2 = dao.getAllProduct();
			
        double totalMoney=0;
        for(Cart c : list) {
			for(Product p : list2) {
				if(c.getProductID()==p.getId()) {
					totalMoney=totalMoney+(p.getPrice()*c.getAmount());
				}
			}
		}
//        double totalMoneyVAT=totalMoney+totalMoney*0.1;
//        
        Email e = new Email();
        e.setFrom("trong221119@gmail.com");
        e.setFromPassword("ucmn odtw ewpb exki");
        e.setTo(email);
        e.setSubject("Dat hang thanh cong");
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(name).append("<br>");
		sb.append("Ban vua dat dang <br> ");
		sb.append("Dia chi nhan hang cua ban la: <b>").append(address).append(" </b> <br>");
		sb.append("So dien thoai khi nhan hang cua ban la: <b>").append(sdt).append(" </b> <br>");
		sb.append("Yeu cau cua ban: ").append(content).append("</b> <br>");
		sb.append("Cac san pham ban dat la: <br>");
		for(Cart c : list) {
			for(Product p : list2) {
				if(c.getProductID()==p.getId()) {
					sb.append(p.getName()).append(" | ").append("Price:").append(p.getPrice()).append("$").append(" | ").append("Amount:").append(c.getAmount()).append(" | ").append("Size:").append(c.getSize()).append("<br>");
				}
			}
		}
		sb.append("Tong Tien: ").append(String.format("%.02f",totalMoney)).append("$").append("<br>");
		sb.append("Cam on ban da dat hang <br>");
		sb.append("Chu cua hang");

		e.setContent(sb.toString());
		EmailUtils.send(e);
		
		request.setAttribute("mess", "Dat hang thanh cong");
		dao.deleteCartByAccountID(accountID);
	
        
        request.setAttribute("email", email);
        request.getRequestDispatcher("thankyou.jsp").forward(request, response);
	} catch (Exception e) {
		request.setAttribute("error", "Dat hang that bai");
		e.printStackTrace();
	}
		request.getRequestDispatcher("datHang.jsp").forward(request, response);
	}

}
