
import java.util.Map;
import java.util.logging.Logger;
import java.util.HashMap;

public class CartManager {

    private Map<String, Map<CartItem, Integer>> userCarts;
    private final static Logger log = Logger.getLogger(CartManager.class.getName());

    public CartManager() {
        this.userCarts = new HashMap<>();
    }

    public CartManager(Map<String, Map<CartItem, Integer>> userCarts) {
        this.userCarts = userCarts;
    }

    public Map<String, Map<CartItem, Integer>> getUserCarts() {
        return userCarts;
    }

    public Map<CartItem, Integer> getUserCart(String email) {
        if (!this.userCarts.containsKey(email)) {
            return null;
        }

        return this.userCarts.get(email);
    }

    public void addToCart(String email, CartItem item) {
        // If email is not within the map, do register new CartItem associated with this
        // email
        if (!this.userCarts.containsKey(email)) {
            log.info("Beginning of " + email + " email registration");
            // Define new cart
            Map<CartItem, Integer> cartItemToAdd = new HashMap<>();
            cartItemToAdd.put(item, 1);

            // Put new cart to map of all carts and associate with email as key
            this.userCarts.put(email, cartItemToAdd);
            log.info("Item added to cart.");
        } else {
            Map<CartItem, Integer> usersCart = this.userCarts.get(email);
            if (usersCart.containsKey(item)) {
                Integer updatedQuantity = usersCart.get(item) + 1;
                log.info("Increasing " + item.getName() + " to quantity of " + updatedQuantity);
                usersCart.put(item, updatedQuantity);
            } else {
                log.info("Creating new record in map for " + item.getName() + " with 1 quantity");
                usersCart.put(item, 1);
            }
            this.userCarts.put(email, usersCart);
            log.info("Item was successfuly added to general cart");
        }
    }

}
