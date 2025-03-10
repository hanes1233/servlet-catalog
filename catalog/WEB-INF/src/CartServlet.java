
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Logger;
import java.lang.StringBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

public class CartServlet extends HttpServlet {

    private final static Logger log = Logger.getLogger(CartServlet.class.getName());
    private CartManager cartManager;
    private DatabaseManager db;
    private static final long serialVersionUid = 1L;

    @Override
    public void init() {
        this.db = new DatabaseManager();
        Map<String, Map<CartItem, Integer>> users = this.db.getUserCarts();
        this.cartManager = new CartManager(users);
    }

    @Override
    public void destroy() {
        this.db.writeUserCarts(this.cartManager.getUserCarts());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        validateSessionAndEmail(request, response);

        // Retrieve current session user's email
        String sessionEmail = (String) request.getSession().getAttribute("email");

        // Get the PrintWriter to send manually generated HTML content in the response
        PrintWriter out = response.getWriter();

        // Retrieve cart associated with session's email
        Map<CartItem, Integer> userCart = this.cartManager.getUserCart(sessionEmail);
        if (userCart == null || userCart.isEmpty()) {
            out.println(generateEmptyCart());
        } else {
            out.println(CartSummaryHtmlGenerator.getCartSummaryPage(userCart));
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        validateSessionAndEmail(request, response);

        // Create cart item based on parameters request holds
        CartItem cartItem = createItem(request);

        // Retrieve current session user's email
        String email = (String) request.getSession().getAttribute("email");

        // Add item to cart and associate with email
        this.cartManager.addToCart(email, cartItem);
        log.info("Item was added to cart. Redirecting back to catalog...");
        response.sendRedirect("/catalog/catalog.html");
    }

    private String generateEmptyCart() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head><title>My Icecream Shop!</title></head>");
        sb.append("<body>");
        sb.append("<h3>Your cart is empty.</h3>");
        sb.append(
                "<h3>Go back to <a href=\"http://localhost:8080/catalog/catalog.html\">catalog</a> to add some items</h3>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private void validateSessionAndEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        if (session == null) {
            response.sendRedirect("/catalog/login.html");
        } else {
            String email = (String) session.getAttribute("email");
            if (email == null) {
                session.invalidate();
                response.sendRedirect("/catalog/login.html");
            }
        }
    }

    private CartItem createItem(HttpServletRequest request) {
        // Capture parameters from request to create CartItem (simply just an item)
        String imgAddress = (String) request.getParameter("imgAddress");
        String itemName = (String) request.getParameter("itemName");
        String itemPriceRaw = (String) request.getParameter("itemPrice");
        int parsedItemPrice = Integer.parseInt(itemPriceRaw);

        CartItem cartItem = new CartItem(imgAddress, itemName, parsedItemPrice);
        return cartItem;
    }

}
